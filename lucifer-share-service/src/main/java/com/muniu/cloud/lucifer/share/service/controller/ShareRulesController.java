package com.muniu.cloud.lucifer.share.service.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.muniu.cloud.lucifer.share.service.constant.*;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleGroup;
import com.muniu.cloud.lucifer.share.service.exception.FunctionException;
import com.muniu.cloud.lucifer.share.service.impl.ShareRulesService;
import com.muniu.cloud.lucifer.share.service.impl.function.RuleFunction;
import com.muniu.cloud.lucifer.share.service.model.rule.Rule;
import com.muniu.cloud.lucifer.share.service.model.rule.RuleItem;
import com.muniu.cloud.lucifer.share.service.vo.*;
import com.muniu.cloud.lucifer.system.api.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.muniu.cloud.lucifer.commons.model.base.KeyValue;
import com.muniu.cloud.lucifer.commons.model.vo.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 股票规则管理控制器
 */
@RestController
@RequestMapping("/share/rules")
@Tag(name = "股票规则管理", description = "提供股票选股规则的管理功能")
public class ShareRulesController {

    private final ShareRulesService shareRulesService;

    private final List<RuleFunction> ruleFunctions;

    @Autowired
    public ShareRulesController(ShareRulesService shareRulesService, List<RuleFunction> ruleFunctions) {
        this.shareRulesService = shareRulesService;
        this.ruleFunctions = ruleFunctions;
    }

    /**
     * 获取所有通用配置
     * @return 通用配置信息
     */
    @GetMapping("common/all")
    @Operation(
        summary = "获取通用配置信息", 
        description = "获取规则相关的所有通用配置信息，包括数据源、连接方式、字段等",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取配置信息",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<Map<String, List<?>>> getAllCommonConfig() {
        return RestResponse.success(Map.of(
                "rulesDataSource", Arrays.stream(RuleDataSource.values()).map(e -> new KeyValue(e.name(), e.getDescription())).toList(),
                "rulesConnect", Arrays.stream(RulesConnect.values()).map(e -> new KeyValue(e.getCode(), e.getName())).toList(),
                "historyFields", RulesFields.HISTORY_FIELDS,
                "rulesDateType", RuleDateType.RULE_DATE_TYPE_LIST
        ));
    }


//    @PostMapping("/group/query")
//    public RestResponse<PageResult<ShareRuleGroup>> groupQuery(@CurrentUser String loginName, @RequestBody QueryRuleGroupPageParams pageParams) {
//        return shareRulesService.queryGroup(pageParams, loginName);
//    }

    /**
     * 查询用户规则组
     * @param loginName 登录用户名
     * @return 规则组列表
     */
    @GetMapping("/group/user")
    @Operation(
        summary = "获取用户规则组", 
        description = "获取当前登录用户的所有规则组",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取规则组",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<ShareRuleGroup>> groupQuery(
            @Parameter(description = "登录用户名", hidden = true) 
            @CurrentUser String loginName) {
        return shareRulesService.getGroupByUser(loginName);
    }


    /**
     * 保存规则组
     * @param loginName 登录用户名
     * @param shareRule 规则组信息
     * @return 处理结果
     */
    @PostMapping("/group/save")
    @Operation(
        summary = "保存规则组", 
        description = "创建新的规则组",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "规则组保存成功",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<Void> groupSave(
            @Parameter(description = "登录用户名", hidden = true) 
            @CurrentUser String loginName, 
            @Parameter(description = "规则组信息", required = true) 
            @RequestBody @Valid SaveRuleGroupParams shareRule) {
        return shareRulesService.saveGroup(loginName, shareRule);
    }

    /**
     * 更新规则组
     * @param loginName 登录用户名
     * @param updateRuleGroupParams 规则组更新信息
     * @return 处理结果
     */
    @PostMapping("/group/update")
    @Operation(
        summary = "更新规则组", 
        description = "更新现有的规则组信息",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "规则组更新成功",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<Void> groupUpdate(
            @Parameter(description = "登录用户名", hidden = true) 
            @CurrentUser String loginName, 
            @Parameter(description = "规则组更新信息", required = true) 
            @RequestBody @Valid UpdateRuleGroupParams updateRuleGroupParams) {
        return shareRulesService.updateGroup(loginName, updateRuleGroupParams);
    }

    /**
     * 删除规则组
     * @param loginName 登录用户名
     * @param groupId 规则组ID
     * @return 处理结果
     */
    @GetMapping("/group/del/{groupId}")
    @Operation(
        summary = "删除规则组", 
        description = "删除指定的规则组",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "规则组删除成功",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<Void> groupDel(
            @Parameter(description = "登录用户名", hidden = true) 
            @CurrentUser String loginName,
            @Parameter(description = "规则组ID", required = true) 
            @PathVariable("groupId") Long groupId) {
        return shareRulesService.deleteGroup(loginName, groupId);
    }

    /**
     * 获取规则组下的规则项
     * @param groupId 规则组ID
     * @return 规则项列表
     */
    @GetMapping("item/items/{groupId}")
    @Operation(
        summary = "获取规则项列表", 
        description = "获取指定规则组下的所有规则项",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取规则项列表",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<QueryRuleItemResult>> itemItems(
            @Parameter(description = "规则组ID", required = true) 
            @PathVariable("groupId") Long groupId) {
        return RestResponse.success(shareRulesService.getRuleItemByGroup(groupId));
    }

    /**
     * 保存规则项
     * @param jsonString 规则项JSON字符串
     * @return 处理结果
     */
    @PostMapping("item/save")
    @Operation(
        summary = "保存规则项", 
        description = "保存规则项信息",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "规则项保存成功",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<Void> itemSave(
            @Parameter(description = "规则项JSON字符串", required = true) 
            @RequestBody String jsonString) {
        SaveRuleItemsParams saveRuleItemsParams = JSON.parseObject(jsonString, SaveRuleItemsParams.class, JSONReader.autoTypeFilter(true, Rule.class, RuleItem.class));
        return shareRulesService.saveGroupItem(saveRuleItemsParams);
    }


    /**
     * 执行规则组处理
     * @param groupId 规则组ID
     * @return 符合条件的股票代码集合
     * @throws FunctionException 函数执行异常
     */
    @GetMapping("process/{groupId}")
    @Operation(
        summary = "执行规则筛选", 
        description = "执行指定规则组的筛选条件，返回符合条件的股票代码集合",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "规则执行成功",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<Set<String>> process(
            @Parameter(description = "规则组ID", required = true) 
            @PathVariable("groupId") Long groupId) throws FunctionException {
        return RestResponse.success(shareRulesService.processHistoryRules(groupId));
    }

    /**
     * 获取所有规则函数列表
     * @return 规则函数列表
     */
    @GetMapping("function/list")
    @Operation(
        summary = "获取规则函数列表", 
        description = "获取系统中所有可用的规则函数",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取函数列表",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<RuleFunction.RuleFunctionBean>> functionList() {
        List<RuleFunction.RuleFunctionBean> result = ruleFunctions.stream().map(e->new RuleFunction.RuleFunctionBean(e.getName(),e.getCode(),e.getRuleDataType().name())).toList();
        return RestResponse.success(result);
    }
}
