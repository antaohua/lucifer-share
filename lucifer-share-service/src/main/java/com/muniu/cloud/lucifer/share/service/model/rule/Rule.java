package com.muniu.cloud.lucifer.share.service.model.rule;

import com.alibaba.fastjson2.annotation.JSONField;
import com.google.common.collect.Lists;
import com.muniu.cloud.lucifer.share.service.constant.RulesConnect;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleItem;
import com.muniu.cloud.lucifer.share.service.exception.FunctionException;
import com.muniu.cloud.lucifer.share.service.impl.function.RuleFunction;
import lombok.Setter;
import com.muniu.cloud.lucifer.commons.model.base.BaseModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
public class Rule extends BaseModel implements RulesInterface {


   private List<Rule> groups = Lists.newArrayList();
   private List<RuleItem> items = Lists.newArrayList();
   private RulesConnect connect;


   @JSONField(name = "groups", serialize = true)
   public List<Rule> getGroups() {
      return CollectionUtils.isEmpty(groups) ? Lists.newArrayList() : groups;
   }

    public void addGroup(Rule group) {
      groups.add(group);
   }

   @JSONField(name = "items", serialize = true)
   public List<RuleItem> getItems() {
      return items.isEmpty() ? null : items;
   }

    public void addItem(RuleItem item) {
        items.add(item);
    }


   public RulesConnect getConnect() {
      return connect;
   }

    public Rule() {
   }

   public Rule(RulesConnect connect) {
      this.connect = connect;
   }

   @Override
   public String toSql(ShareRuleItem ruleItem , Rule rule, Map<String, RuleFunction> functionMap)throws FunctionException {
      StringBuilder sb = new StringBuilder();
      String op = connect.getCode();
      sb.append("(");
      List<RulesInterface> rules = new ArrayList<>(groups);
      rules.addAll(items);
      for (int i = 0; i < rules.size(); i++) {
         sb.append(rules.get(i).toSql(ruleItem, rule, functionMap));
         if (i < rules.size() - 1) {
            sb.append(" ").append(op).append(" ");
         }
      }
      sb.append(")");
      return sb.toString();
   }

}
