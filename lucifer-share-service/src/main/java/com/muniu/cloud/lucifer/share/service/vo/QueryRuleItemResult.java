package com.muniu.cloud.lucifer.share.service.vo;

import com.muniu.cloud.lucifer.share.service.constant.RuleDataSource;
import com.muniu.cloud.lucifer.share.service.constant.RuleDateType;
import com.muniu.cloud.lucifer.share.service.model.rule.Rule;

public record QueryRuleItemResult(String cron, Long ruleGroup, RuleDataSource dataSource, Rule ruleValue, RuleDateType dateType, String dateValue) {
}
