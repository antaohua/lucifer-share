package com.muniu.cloud.lucifer.share.service.model.rule;

import com.google.common.collect.Maps;
import com.muniu.cloud.lucifer.commons.model.dto.BaseModel;
import com.muniu.cloud.lucifer.share.service.constant.RulesFields;
import com.muniu.cloud.lucifer.share.service.constant.RulesOperator;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleItem;
import com.muniu.cloud.lucifer.share.service.exception.FunctionException;
import com.muniu.cloud.lucifer.share.service.impl.function.RuleFunction;
import com.muniu.cloud.lucifer.share.service.utils.FunctionParser;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Getter
@Setter
public class RuleItem extends BaseModel implements RulesInterface {

    private static final Logger logger = LoggerFactory.getLogger(RuleItem.class);

    private String field;

    private RulesOperator operator;

    private String value;

    private String valueType;

    public RuleItem(String field, RulesOperator operator, String value,String valueType) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.valueType = valueType;
    }

    @Override
    public String toSql(ShareRuleItem ruleItem, Rule rule, Map<String, RuleFunction> functionMap) throws FunctionException {
        RulesFields rulesField = RulesFields.getByName(field);
        if (ruleItem.getDataSource() == null || rulesField == null || rulesField.getRuleSource() != ruleItem.getDataSource()) {
            logger.warn("field = {} error", field);
            return "";
        }
        Map<String, String[]> functionAges = FunctionParser.parseFunctions(value);
        Map<String, String> functionResult = Maps.newHashMap();
        String result = null;
        if (StringUtils.equals("fn", getValueType())) {
            for (Map.Entry<String, String[]> entry : functionAges.entrySet()) {
                for (int i = 0; ArrayUtils.isNotEmpty(entry.getValue()) && i < entry.getValue().length; i++) {
                    if (StringUtils.startsWith(entry.getValue()[i], "RESULT_")) {
                        entry.getValue()[i] = functionResult.get(StringUtils.replace(entry.getValue()[i], "RESULT_", ""));
                    }
                }
                result = functionMap.get(entry.getKey()).process(ruleItem, rule, entry.getValue());
                functionResult.put(entry.getKey(), result);
            }
        }
        result = StringUtils.isBlank(result) ? value : result;
        return " " + rulesField.getDataColumn() + " " + operator.getSymbol() + " " + ((operator == RulesOperator.IN || operator == RulesOperator.NOT_IN) ? (" (" + result + ")") : result);
    }


}
