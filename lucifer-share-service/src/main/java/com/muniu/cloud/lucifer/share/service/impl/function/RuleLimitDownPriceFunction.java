package com.muniu.cloud.lucifer.share.service.impl.function;

import com.muniu.cloud.lucifer.share.service.constant.RuleDataSource;
import com.muniu.cloud.lucifer.share.service.constant.RuleItemDataType;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleItemEntity;
import com.muniu.cloud.lucifer.share.service.exception.FunctionException;
import com.muniu.cloud.lucifer.share.service.model.rule.Rule;
import org.springframework.stereotype.Service;

@Service
public class RuleLimitDownPriceFunction implements RuleFunction {



    @Override
    public String process(ShareRuleItemEntity ruleItem, Rule rule, String... args) throws FunctionException {
        if (ruleItem.getDataSource() == null || ruleItem.getDataSource() != RuleDataSource.HISTORY_SINGLE) {
            throw new FunctionException("dataSource 不匹配",getCode());
        }
        return "limit_down";
    }

    @Override
    public RuleItemDataType getRuleDataType() {
        return RuleItemDataType.AMOUNT;
    }

    @Override
    public String getName() {
        return "跌停价";
    }

    @Override
    public String getCode() {
        return "LimitDownPrice";
    }
}
