package com.muniu.cloud.lucifer.share.service.impl.function;

import com.muniu.cloud.lucifer.share.service.constant.RuleItemDataType;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleItemEntity;
import com.muniu.cloud.lucifer.share.service.exception.FunctionException;
import com.muniu.cloud.lucifer.share.service.model.rule.Rule;

public interface RuleFunction {

    /**
     * 处理规则
     * @param rule 规则项
     * @param args 参数
     * @return 处理结果
     */
    String process(ShareRuleItemEntity ruleItem, Rule rule, String... args) throws FunctionException;

    RuleItemDataType getRuleDataType();

    String getName();

    String getCode();

    record RuleFunctionBean(String name,String code ,String itemDataType){

    }
}
