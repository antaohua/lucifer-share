package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.muniu.cloud.lucifer.commons.core.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.utils.constants.DateConstant;
import com.muniu.cloud.lucifer.commons.utils.exception.HttpClientException;
import com.muniu.cloud.lucifer.share.service.constant.AdjustConstant;
import com.muniu.cloud.lucifer.share.service.constant.PeriodConstant;
import com.muniu.cloud.lucifer.share.service.entity.TradeBoardMarketEntity;
import com.muniu.cloud.lucifer.share.service.entity.TradeFundFlowEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AkToolsService {

    @Value("${ak-tools.url}")
    private String baseUrl;


    private final LuciferHttpClient okHttpClient;

    public AkToolsService(@Qualifier("httpClient") LuciferHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    private String get(String method, Map<String, String> parameter) throws IOException {
        StringBuilder urlBuilder = new StringBuilder(baseUrl).append(method);
        // 添加查询参数
        if (parameter != null && !parameter.isEmpty()) {
            urlBuilder.append("?");
            boolean first = true;
            for (Map.Entry<String, String> entry : parameter.entrySet()) {
                if (!first) {
                    urlBuilder.append("&");
                }
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
        }
        log.info("请求数据：{}", urlBuilder);
        try {
            return okHttpClient.get(urlBuilder.toString());
        } catch (HttpClientException e) {
            log.error("请求失败: {}", e.getMessage(), e);
            return null;
        }
    }


//    /**
//     * 日内分时数据-新浪
//     * 接口: stock_intraday_sina
//     * 目标地址: https://vip.stock.finance.sina.com.cn/quotes_service/view/cn_bill.php?symbol=sz000001
//     * 描述: 新浪财经-日内分时数据
//     * 限量: 单次返回指定交易日的分时数据；只能获取近期的数据
//     * 输入参数
//     * 名称	类型	描述
//     * symbol	str	symbol="sz000001"; 带市场标识的股票代码
//     * date	str	date="20240321"; 交易日
//     * */
//    public String stockIntradaySina(String symbol,String date) throws IOException {
//        Map<String, String> parameter = new HashMap<>();
//        parameter.put("symbol", symbol);
//        parameter.put("date", date);
//        return get("stock_intraday_sina",parameter);
//    }

//    /**
//     * 股票市场总貌-上海证券交易所
//     * stock_sse_summary
//     */
//    public String stockSseSummary() throws IOException {
//        return get("stock_sse_summary", null);
//    }
//
//    /**
//     * 实时行情数据-东财-沪深京 A 股
//     * stock_zh_a_spot_em
//     */
//
//    public String stockZhASpotEm() throws Exception {
//        return get("stock_zh_a_spot_em", null);
//    }


//    /**
//     * 股票列表-A股
//     */
//    public String stockInfoANameCode() throws IOException {
//        return get("stock_info_a_code_name", null);
//    }


//    /**
//     * 个股信息查询
//     */
//    public String stockIndividualInfoEm(String shareCode) throws IOException {
//        Map<String, String> parameter = new HashMap<>();
//        parameter.put("symbol", shareCode);
//        return get("stock_individual_info_em", parameter);
//    }


    /**
     * 交易日历
     */
    public List<Integer> toolTradeDateHistSina() throws IOException {
        String jsonData = get("tool_trade_date_hist_sina", null);
        JSONArray array = JSON.parseArray(jsonData);
        return Optional.ofNullable(array).orElse(new JSONArray()).stream().map(b -> Integer.parseInt(((JSONObject) b).getString("trade_date").split("T")[0].replace("-", ""))).toList();
    }

//    /**
//     * 个股历史行情
//     */
//    public String stockZhAHist(String shareCode, PeriodConstant period, AdjustConstant adjust, String startDate, String endDate) throws IOException {
//        Map<String, String> parameter = new HashMap<>();
//        parameter.put("symbol", shareCode);
//        parameter.put("period", period.getCode());
//        parameter.put("start_date", startDate);
//        parameter.put("end_date", endDate);
//        if (adjust == null && adjust != AdjustConstant.NONE) {
//            parameter.put("adjust", adjust.getCode());
//        }
//        return get("stock_zh_a_hist", parameter);
//    }





    /**
     * 获取市场资金流向数据
     */
    public List<TradeFundFlowEntity> marketFundFlow() throws IOException {
        String jsonString =  get("stock_market_fund_flow", null);
        
        List<Map<String,String>> list = Objects.requireNonNull(JSON.parseArray(jsonString, Map.class)).stream().map(e-> {
            Map<String,String> map = Maps.newHashMap();
            for(Map.Entry<?, ?> entry : ((Map<?, ?>) e).entrySet()){
                map.put(entry.getKey().toString(),entry.getValue().toString());
            }
            return map;
        }).collect(Collectors.toList());
        
        List<TradeFundFlowEntity> result = new ArrayList<>();
        Optional.of(list)
            .orElse(Collections.emptyList())
            .forEach(map -> {
                // 转换日期格式
                String dateStr = map.get("日期");
                LocalDate date = LocalDate.parse(dateStr);

                
                // 创建上证数据
                TradeFundFlowEntity shFlow = new TradeFundFlowEntity();
                int tradeDate = Integer.parseInt(date.format(DateConstant.DATE_FORMATTER_YYYYMMDD));

                shFlow.setTradeDate(tradeDate);
                shFlow.setMarketType("SH");
                shFlow.setClosingPrice(new BigDecimal(map.get("上证-收盘价")));
                shFlow.setChangeRate(new BigDecimal(map.get("上证-涨跌幅")));
                setCommonFieldsStatic(shFlow, map);
                result.add(shFlow);
                
                // 创建深证数据
                TradeFundFlowEntity szFlow = new TradeFundFlowEntity();
                szFlow.setTradeDate(tradeDate);
                szFlow.setMarketType("SZ");
                szFlow.setClosingPrice(new BigDecimal(map.get("深证-收盘价")));
                szFlow.setChangeRate(new BigDecimal(map.get("深证-涨跌幅")));
                setCommonFieldsStatic(szFlow, map);
                result.add(szFlow);
            });
            
        return result;
    }
    
    private static void setCommonFieldsStatic(TradeFundFlowEntity entity, Map<String,String> map) {
        entity.setMainNetInflow(new BigDecimal(map.get("主力净流入-净额")));
        entity.setMainNetInflowRate(new BigDecimal(map.get("主力净流入-净占比")));
        entity.setSuperNetInflow(new BigDecimal(map.get("超大单净流入-净额")));
        entity.setSuperNetInflowRate(new BigDecimal(map.get("超大单净流入-净占比")));
        entity.setLargeNetInflow(new BigDecimal(map.get("大单净流入-净额")));
        entity.setLargeNetInflowRate(new BigDecimal(map.get("大单净流入-净占比")));
        entity.setMediumNetInflow(new BigDecimal(map.get("中单净流入-净额")));
        entity.setMediumNetInflowRate(new BigDecimal(map.get("中单净流入-净占比")));
        entity.setSmallNetInflow(new BigDecimal(map.get("小单净流入-净额")));
        entity.setSmallNetInflowRate(new BigDecimal(map.get("小单净流入-净占比")));
    }

    /**
     * 指数行情数据-东方财富
     * @param symbol 指数分类，可选值：上证系列指数、深证系列指数、沪深重要指数、中证系列指数、指数成份
     * @return 返回指数行情数据
     * @throws IOException 请求异常
     */
    public String stockZhIndexSpotEm(String symbol) throws IOException {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("symbol", symbol);
        return get("stock_zh_index_spot_em", parameter);
    }

    /**
     * 指数历史行情数据-东方财富
     * @param symbol 指数代码，如"sz399552"，支持 sz: 深交所, sh: 上交所, csi: 中证指数 + id(000905)
     * @param startDate 开始日期，格式为"19900101"
     * @param endDate 结束日期，格式为"20500101"
     * @return 返回指数历史行情数据
     * @throws IOException 请求异常
     */
    public String stockZhIndexDailyEm(String symbol, String startDate, String endDate) throws IOException {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("symbol", symbol);
        parameter.put("start_date", startDate);
        parameter.put("end_date", endDate);
        return get("stock_zh_index_daily_em", parameter);
    }

//    /**
//     * 获取指数列表数据
//     * @return 返回指数列表数据
//     * @throws IOException 请求异常
//     */
//    public String indexStockInfo() throws IOException {
//        return get("index_stock_info", null);
//    }

    /**
     * 东方财富-概念板块
     * 东方财富-概念板块
     * 接口: stock_board_concept_name_em
     * 目标地址: <a href="https://quote.eastmoney.com/center/boardlist.html#concept_board">...</a>
     * 描述: 东方财富网-行情中心-沪深京板块-概念板块
     * 限量: 单次返回当前时刻所有概念板块的实时行情数据
     * @return 概念板块数据JSON字符串
     * @throws IOException 请求异常
     */
    public List<TradeBoardMarketEntity> stockBoardConceptNameEm() throws IOException {
        String jsonData = get("stock_board_concept_name_em", null);
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        JSONArray array = JSON.parseArray(jsonData);
        if (array == null || array.isEmpty()) {
            return null;
        }
        List<TradeBoardMarketEntity> result = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new TradeBoardMarketEntity(item,currentTime));
        }
        return result;
    }
    
    /**
     * 东方财富-概念板块-板块成份
     * @param symbol 板块代码，如 BK0655
     * @return 板块成份股数据JSON字符串
     * @throws IOException 请求异常
     */
    public String stockBoardConceptConsEm(String symbol) throws IOException {
        if (StringUtils.isBlank(symbol)) {
            log.error("概念板块代码不能为空");
            return null;
        }
        
        Map<String, String> parameter = new HashMap<>();
        parameter.put("symbol", symbol);
        return get("stock_board_concept_cons_em", parameter);
    }


}
