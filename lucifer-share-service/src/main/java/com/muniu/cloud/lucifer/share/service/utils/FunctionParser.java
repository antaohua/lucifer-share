package com.muniu.cloud.lucifer.share.service.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionParser {
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("(\\w+)\\(([^()]*)\\)");
    public static Map<String, String[]> parseFunctions(String expression) {
        Map<String, String[]> functionMap = new LinkedHashMap<>();
        Matcher matcher;
        while ((matcher = FUNCTION_PATTERN.matcher(expression)).find()) {
            String functionName = matcher.group(1);
            String args = matcher.group(2);
            String[] argArray = args.split(",");
            // 存储解析出的函数
            functionMap.put(functionName, argArray);
            // 替换当前解析的函数，简化表达式，避免重复匹配
            String resultPlaceholder = "RESULT_" + functionName;
            expression = matcher.replaceFirst(resultPlaceholder);
        }
        return functionMap;
    }

    public static String replaceFunctionResults(String expression, Map<String, String> functionResults) {
        boolean hasReplacements;
        do {
            hasReplacements = false;
            Matcher matcher = FUNCTION_PATTERN.matcher(expression);
            StringBuilder buffer = new StringBuilder();

            while (matcher.find()) {
                String functionName = matcher.group(1);
                if (functionResults.containsKey(functionName)) {
                    String result = functionResults.get(functionName);
                    matcher.appendReplacement(buffer, result);
                    hasReplacements = true;
                }
            }
            matcher.appendTail(buffer);
            expression = buffer.toString();
        } while (hasReplacements); // 确保所有 `RESULT_xxx` 都被替换

        return expression;
    }

    public static void main(String[] args) {
        String expression = "upLimitPrice() + 3";

//        String expression = "1 + 3";
        // 解析函数
        Map<String, String[]> functionMap = parseFunctions(expression);
        for (Map.Entry<String, String[]> entry : functionMap.entrySet()) {
            System.out.println("Function: " + entry.getKey() + ", Args: " + Arrays.toString(entry.getValue()));
        }

//        // 执行函数并存储结果
//        Map<String, String> functionResults = Map.of("maxPrice","9");
//
//        // 替换表达式中的函数调用为计算结果
//        String finalExpression = replaceFunctionResults(expression, functionResults);
//
//        System.out.println("Final Parsed Expression: " + finalExpression);
    }

}