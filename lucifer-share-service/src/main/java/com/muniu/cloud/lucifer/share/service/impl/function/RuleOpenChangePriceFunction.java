package com.muniu.cloud.lucifer.share.service.impl.function;

import com.muniu.cloud.lucifer.share.service.constant.RuleDataSource;
import com.muniu.cloud.lucifer.share.service.constant.RuleDateType;
import com.muniu.cloud.lucifer.share.service.constant.RuleItemDataType;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleItem;
import com.muniu.cloud.lucifer.share.service.exception.FunctionException;
import com.muniu.cloud.lucifer.share.service.model.rule.Rule;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RuleOpenChangePriceFunction implements RuleFunction {



    @Override
    public String process(ShareRuleItem ruleItem, Rule rule, String... args) throws FunctionException {

        if (ruleItem.getDataSource() == null || ruleItem.getDataSource() != RuleDataSource.HISTORY_SINGLE) {
            throw new FunctionException("dataSource 不匹配",getCode());
        }
        BigDecimal percentage;
        try {
            percentage = new BigDecimal(args[0]);
        } catch (NumberFormatException e) {
            throw new FunctionException("参数错误: " + args[0] + " 不是有效的数值", getCode());
        }

        percentage = calculatePercentage(percentage);
        return "ROUND(previous_close * "+percentage+ ",2 )";
    }

    public static BigDecimal calculatePercentage(BigDecimal percent) {
        // 将百分比转换为小数（例如 5% -> 0.05，-7% -> -0.07）
        BigDecimal decimal = percent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        // 计算 1 + 百分比（例如 1 + 0.05 = 1.05，1 + (-0.07) = 0.93）
        return BigDecimal.ONE.add(decimal);
    }

    @Override
    public RuleItemDataType getRuleDataType() {
        return RuleItemDataType.PERCENTAGE;
    }

    @Override
    public String getName() {
        return "开盘涨跌幅";
    }

    @Override
    public String getCode() {
        return "OpenChangePrice";
    }
}
