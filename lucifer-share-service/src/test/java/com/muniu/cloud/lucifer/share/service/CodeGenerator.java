//package com.muniu.cloud.lucifer.share.service;
//
//
//import com.baomidou.mybatisplus.generator.FastAutoGenerator;
//import com.baomidou.mybatisplus.generator.config.OutputFile;
//import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
//import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
//
//import java.sql.Types;
//import java.util.Collections;
//
//public class CodeGenerator {
//
//    public static void main(String[] args) {
//        FastAutoGenerator.create("jdbc:mysql://192.168.0.253:3306/lucifer_dev?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=utf8&useUnicode=true", "root", "1q2w3e4rQ")
//                .globalConfig(builder -> {
//                    builder.author("antaohua") // 设置作者
//                            .outputDir("lucifer-stock/lucifer-stock-service/src/main/java"); // 指定输出目录
//                })
//                .dataSourceConfig(builder ->
//                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
//                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
//                            if (typeCode == Types.SMALLINT) {
//                                // 自定义类型转换
//                                return DbColumnType.INTEGER;
//                            }
//                            return typeRegistry.getColumnType(metaInfo);
//                        })
//                )
//                .packageConfig(builder ->
//                        builder.parent("com.muniu.cloud.lucifer") // 设置父包名
//                                .moduleName("share") // 设置父包模块名
//                                .pathInfo(Collections.singletonMap(OutputFile.xml, "lucifer-stock/lucifer-stock-service/src/main/resources/mapper")) // 设置mapperXml生成路径
//                )
//                .strategyConfig(builder ->
//                        builder.addInclude("share_info").addInclude("share_marke").addInclude("share_market_hist").addInclude("share_rule").addInclude("share_rule_group")
//                )
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .execute();
//    }
//}
//
