package com.muniu.cloud.lucifer.share.service.model.rule;

import com.muniu.cloud.lucifer.share.service.entity.ShareRuleItemEntity;
import com.muniu.cloud.lucifer.share.service.exception.FunctionException;
import com.muniu.cloud.lucifer.share.service.impl.function.RuleFunction;

import java.util.Map;

public interface RulesInterface{

    String toSql(ShareRuleItemEntity ruleItem, Rule rule, Map<String, RuleFunction> functionMap) throws FunctionException;

}
