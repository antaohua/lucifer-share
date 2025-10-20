package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.muniu.cloud.lucifer.commons.model.vo.RestResponse;
import com.muniu.cloud.lucifer.share.service.constant.RuleDataSource;
import com.muniu.cloud.lucifer.share.service.dao.ShareRuleGroupDao;
import com.muniu.cloud.lucifer.share.service.dao.ShareRuleItemDao;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleGroupEntity;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleItemEntity;
import com.muniu.cloud.lucifer.share.service.exception.FunctionException;
import com.muniu.cloud.lucifer.share.service.impl.function.RuleFunction;
import com.muniu.cloud.lucifer.share.service.model.rule.Rule;
import com.muniu.cloud.lucifer.share.service.model.rule.RuleItem;
import com.muniu.cloud.lucifer.share.service.vo.QueryRuleItemResult;
import com.muniu.cloud.lucifer.share.service.vo.SaveRuleGroupParams;
import com.muniu.cloud.lucifer.share.service.vo.SaveRuleItemsParams;
import com.muniu.cloud.lucifer.share.service.vo.UpdateRuleGroupParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ShareRulesService {

    private final ShareRuleGroupDao shareRuleGroupDao;

    private final ShareRuleItemDao shareRuleItemDao;

    private final Map<String, RuleFunction> ruleFunctions;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ShareRulesService(ShareRuleGroupDao shareRuleGroupDao, ShareRuleItemDao shareRuleItemDao, List<RuleFunction> ruleFunctions,JdbcTemplate jdbcTemplate) {
        this.shareRuleGroupDao = shareRuleGroupDao;
        this.shareRuleItemDao = shareRuleItemDao;
        this.ruleFunctions = ruleFunctions.stream().collect(Collectors.toMap(RuleFunction::getCode, e->e));
        this.jdbcTemplate = jdbcTemplate;

    }



    public List<QueryRuleItemResult> getRuleItemByGroup(Long groupId) {
        List<ShareRuleItemEntity> shareRuleItemEntities = shareRuleItemDao.getByProperty("ruleGroup",groupId,"sort",false,true);
        if(CollectionUtils.isEmpty(shareRuleItemEntities)){
            return Lists.newArrayList();
        }
        return shareRuleItemEntities.stream().map(e -> {
            log.info(e.getRuleValue());
            Rule group = JSON.parseObject(e.getRuleValue(), Rule.class, JSONReader.autoTypeFilter(true, Rule.class, RuleItem.class));
            return new QueryRuleItemResult(e.getCron(), e.getRuleGroup(), e.getDataSource(), group, e.getDateType(), e.getDateValue());

        }).toList();
    }



    public RestResponse<List<ShareRuleGroupEntity>>  getGroupByUser(String userId) {
        List<ShareRuleGroupEntity> resultData = shareRuleGroupDao.getByProperty("userId",userId,ShareRuleGroupEntity.CREATE_TIME_NAME,false,true);
        return RestResponse.success(resultData);
    }


    public RestResponse<Void> saveGroup(String userId, SaveRuleGroupParams saveRuleGroupParams) {
        long currentTime = System.currentTimeMillis();
        ShareRuleGroupEntity ruleGroup = new ShareRuleGroupEntity(currentTime, userId, saveRuleGroupParams);
        shareRuleGroupDao.save(ruleGroup);
        return RestResponse.success();
    }


    public RestResponse<Void> updateGroup(String userId, UpdateRuleGroupParams updateRuleGroupParams) {
        ShareRuleGroupEntity group = shareRuleGroupDao.getById(updateRuleGroupParams.id());
        if(group == null){
            return RestResponse.fail("规则组不存在");
        }
        if(!StringUtils.equals(group.getUserId(),userId)){
            return RestResponse.fail("无权限操作");
        }
        group.setUpdateTime(System.currentTimeMillis());
        group.setDescription(updateRuleGroupParams.description());
        group.setName(updateRuleGroupParams.name());
        shareRuleGroupDao.update(group);
        return RestResponse.success();
    }


    public RestResponse<Void> deleteGroup(String userId, Long groupId) {
        if(groupId == null){
            return RestResponse.fail("规则组不存在");
        }
        ShareRuleGroupEntity group = shareRuleGroupDao.getById(groupId);
        if(group == null){
            return RestResponse.fail("规则组不存在");
        }
        if(!StringUtils.equals(group.getUserId(),userId)){
            return RestResponse.fail("无权限操作");
        }
        group.setDeleted(true);
        int result = shareRuleGroupDao.deleteById(groupId);
        if (result > 0) {
            return RestResponse.success();
        } else {
            return RestResponse.fail("删除失败");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public RestResponse<Void> saveGroupItem(SaveRuleItemsParams updateRuleItem) {
        ShareRuleGroupEntity group = shareRuleGroupDao.getById(updateRuleItem.groupId());
        if (group == null) {
            return RestResponse.fail("规则不存在");
        }
        shareRuleItemDao.deleteByProperty("ruleGroup",updateRuleItem.groupId());
        if (CollectionUtils.isEmpty(updateRuleItem.rule())) {
            return RestResponse.success();
        }
        List<ShareRuleItemEntity> shareRuleItemEntities = updateRuleItem.rule().stream().filter(rule -> {
                    Rule rules = JSON.parseObject(rule.getRuleValue(), Rule.class, JSONReader.autoTypeFilter(true, Rule.class, RuleItem.class));
                    return CollectionUtils.isNotEmpty(rules.getItems()) || CollectionUtils.isNotEmpty(rules.getGroups());
                }
        ).map(rule -> new ShareRuleItemEntity(System.currentTimeMillis(), updateRuleItem.groupId(), rule)).toList();
        if (CollectionUtils.isNotEmpty(shareRuleItemEntities)) {
            shareRuleItemDao.batchSaveOrUpdate(shareRuleItemEntities);
        }
        return RestResponse.success();
    }


    public Set<String> processHistoryRules(Long groupId) throws FunctionException{
        List<ShareRuleItemEntity> shareRuleItemEntities = shareRuleItemDao.getByProperty("ruleGroup",groupId,"sort",false,true);
        if(CollectionUtils.isEmpty(shareRuleItemEntities)){
            return Sets.newHashSet();
        }
        Set<String> fastRuleResult = Sets.newHashSet();
        for (ShareRuleItemEntity shareRuleItemEntity : shareRuleItemEntities) {
            Rule rule = JSON.parseObject(shareRuleItemEntity.getRuleValue(), Rule.class, JSONReader.autoTypeFilter(true, Rule.class, RuleItem.class));
            fastRuleResult = processHistoryRuleItem(shareRuleItemEntity,rule,fastRuleResult);
        }
        return fastRuleResult;
    }

    public Set<String> processHistoryRuleItem(ShareRuleItemEntity ruleItem, Rule rule, Set<String> fastRuleResult) throws FunctionException {
        String where = rule.toSql(ruleItem,rule,ruleFunctions);
        if (ruleItem.getDataSource() == RuleDataSource.HISTORY_SINGLE) {
            int date = Integer.parseInt(ruleItem.getDateValue().replace("-", ""));
            String sql = "SELECT share_code FROM td_share_hist_" + (date / 10000) + " WHERE ";
            sql += "date = " + date + " ";
            if (CollectionUtils.isNotEmpty(fastRuleResult)) {
                sql += "AND share_code IN (" + String.join(",", fastRuleResult) + ") ";

            }
            sql += "AND " + where;
            log.info("sql:{}", sql);
            List<String> ruslt = jdbcTemplate.queryForList(sql, String.class);
            log.info("rust:{}", ruslt);
            return Sets.newHashSet(ruslt);
        }
        return Sets.newHashSet();

    }




//    public static void main(String[] args) {
//        // Create simple conditions
//        RuleItem condition1 = new RuleItem("turnover_rate", RulesOperator.GET, "0.30");
//        RuleItem condition2 = new RuleItem("volume", RulesOperator.GT, "5000");
//        RuleItem condition3 = new RuleItem("closePrice", RulesOperator.EQ, "#{upLimitPrice}");
//        RuleItem condition4 = new RuleItem("closePrice", RulesOperator.EQ, "#{upLimitPrice}");
//        // Create condition group 1
//        Rule group1 = new Rule(RulesConnect.AND);
//        group1.addItem(condition1);
//      group1.addItem(condition2);
//      Rule group3 = new Rule(RulesConnect.AND);
//      group3.addItem(condition3);
//      group3.addItem(condition4);
//        group3.addGroup(group1);
//        // Create condition group 2
////      RulesGroup group2 = new RulesGroup(RulesConnect.OR);
////      group2.addGroup(group1);
////;     group2.addGroup(group3);
////      group2.addItem(condition4);
////      ShareHisTimeData hisTimeData = new ShareHisTimeData();
//
//        String jsonString = JSON.toJSONString(group1);
//        System.out.println(jsonString);
//
//        System.out.println("-----------");
//        System.out.println(group3.toJson());
//        System.out.println(group3.toSql(Maps.newHashMap()));
//        System.out.println("-----------");
//
//        //(( turnover_rate >= (0.30 + volume) and  volume > 5000) and  closePrice = null and  closePrice = null)
//
////      // Deserialize
////      ObjectReaderProvider provider = JSONFactory.getDefaultObjectReaderProvider();
////      provider.setAutoTypeBeforeHandler((typeName, expectClass, features) -> {
////         if (RulesGroup.class.getName().equals(typeName)) {
////            return RulesGroup.class;
////         } else if (RulesItem.class.getName().equals(typeName)) {
////            return RulesItem.class;
////         } else {
////            throw new JSONException("Unsupported type: " + typeName);
////         }
////      });
//
//        Rule object = JSON.parseObject("{\"connect\":\"AND\",\"items\":[{\"field\":\"share_code\",\"operator\":\"eq\",\"value\":\"1\"},{\"field\":\"high_price\",\"operator\":\"eq\",\"value\":\"1\"}],\"groups\":[{\"connect\":\"AND\",\"items\":[{\"field\":\"high_price\",\"operator\":\"eq\",\"value\":\"1\"},{\"field\":\"high_price\",\"operator\":\"eq\",\"value\":\"1\"}],\"groups\":[]}]}", Rule.class, JSONReader.autoTypeFilter(true, Rule.class, RuleItem.class));
//
//        System.out.println("Deserialization result: " + JSON.toJSONString(object));
//
//        System.out.println("Deserialization result: " + object.toSql(Maps.newHashMap()));
//
//    }

}
