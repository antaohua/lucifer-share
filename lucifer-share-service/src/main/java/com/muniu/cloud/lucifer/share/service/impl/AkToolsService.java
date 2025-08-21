package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.muniu.cloud.lucifer.commons.utils.exception.HttpClientException;
import com.muniu.cloud.lucifer.commons.utils.http.OkHttpClient3Util;
import com.muniu.cloud.lucifer.share.service.constant.AdjustConstant;
import com.muniu.cloud.lucifer.share.service.constant.PeriodConstant;
import com.muniu.cloud.lucifer.share.service.constant.ShareBoard;
import com.muniu.cloud.lucifer.share.service.constant.ShareExchange;
import com.muniu.cloud.lucifer.share.service.entity.ConceptMarket;
import com.muniu.cloud.lucifer.share.service.entity.MarketFundFlow;
import com.muniu.cloud.lucifer.share.service.model.SimpleShareInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AkToolsService {

    @Value("${ak-tools.url}")
    private String baseUrl;


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
            return OkHttpClient3Util.get(urlBuilder.toString());
        } catch (HttpClientException e) {
            log.error("请求失败: {}", e.getMessage(), e);
            return null;
        }
    }


    /**
     * 股票市场总貌-上海证券交易所
     * stock_sse_summary
     */
    public String stockSseSummary() throws IOException {
        return get("stock_sse_summary", null);
    }

    /**
     * 实时行情数据-东财-沪深京 A 股
     * stock_zh_a_spot_em
     */

    public String stockZhASpotEm() throws Exception {
        return get("stock_zh_a_spot_em", null);
    }


    private static final ShareBoard[] SHANGHAI_BOARDS = new ShareBoard[]{ShareBoard.MAIN, ShareBoard.STAR};

    /**
     * 股票列表-上证
     */

    public List<SimpleShareInfo> stockInfoShNameCode() throws IOException {
        List<SimpleShareInfo> shareInfos = Lists.newArrayList();
        for (ShareBoard board : SHANGHAI_BOARDS) {
            String symbol = board == ShareBoard.MAIN ? "主板A股" : board == ShareBoard.STAR ? "科创板" : "";

            if (StringUtils.isBlank(symbol)) {
                log.error("stockInfoShNameCode is null");
                continue;
            }
            Map<String, String> parameter = new HashMap<>();
            parameter.put("symbol", symbol);
            String jsonString = get("stock_info_sh_name_code", parameter);
            if (StringUtils.isBlank(jsonString)) {
                log.error("stockInfoANameCode is null");
                continue;
            }
            JSONArray array = JSON.parseArray(jsonString);
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String shareName = obj.getString("证券简称").replace(" ", "").trim();
                String shareCode = obj.getString("证券代码");
                //2019-07-22T00:00:00.000
                int listDate = obj.getString("上市日期").substring(0, 10).replace("-", "").equals("0000-00-00") ? 0 : Integer.parseInt(obj.getString("上市日期").substring(0, 10).replace("-", ""));
                shareInfos.add(new SimpleShareInfo(shareCode, shareName, listDate, ShareExchange.SH, board));
            }
            log.info("shareInfos-size:{}", shareInfos.size());

        }
        return shareInfos;
    }


    /**
     * 股票列表-深证
     */
    public List<SimpleShareInfo> stockInfoSzNameCode() throws IOException {

        Map<String, String> parameter = new HashMap<>();
        String symbol = "A股列表";
        parameter.put("symbol", symbol);
        String jsonString = get("stock_info_sz_name_code", parameter);
        List<SimpleShareInfo> shareInfos = Lists.newArrayList();
        if (StringUtils.isBlank(jsonString)) {
            log.error("stockInfoANameCode is null");
            return shareInfos;
        }
        JSONArray array = JSON.parseArray(jsonString);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String shareName = obj.getString("A股简称").replace(" ", "").trim();
            String shareCode = obj.getString("A股代码");
            //2019-07-22
            String board = obj.getString("板块");
            ShareBoard shareBoard = StringUtils.equals(board, "创业板") ? ShareBoard.STAR : StringUtils.equals(board, "主板") ? ShareBoard.MAIN : ShareBoard.UNKNOWN;
            int listDate = obj.getString("A股上市日期").substring(0, 10).replace("-", "").equals("0000-00-00") ? 0 : Integer.parseInt(obj.getString("A股上市日期").substring(0, 10).replace("-", ""));
            shareInfos.add(new SimpleShareInfo(shareCode, shareName, listDate, ShareExchange.SZ, shareBoard));
        }
        log.info("shareInfos-size:{}", shareInfos.size());
        return shareInfos;
    }

    /**
     * 股票列表-北证
     */
    public List<SimpleShareInfo> stockInfoBjNameCode() throws IOException {

        String jsonString = get("stock_info_bj_name_code", null);
        List<SimpleShareInfo> shareInfos = Lists.newArrayList();
        if (StringUtils.isBlank(jsonString)) {
            log.error("stockInfoANameCode is null");
            return shareInfos;
        }
        JSONArray array = JSON.parseArray(jsonString);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String shareName = obj.getString("证券简称").replace(" ", "").trim();
            String shareCode = obj.getString("证券代码");
            int listDate = obj.getString("上市日期").substring(0, 10).replace("-", "").equals("0000-00-00") ? 0 : Integer.parseInt(obj.getString("上市日期").substring(0, 10).replace("-", ""));
            shareInfos.add(new SimpleShareInfo(shareCode, shareName, listDate, ShareExchange.BJ, ShareBoard.BSE));
        }
        log.info("shareInfos-size:{}", shareInfos.size());
        return shareInfos;
    }


    /**
     * 股票列表-A股
     */
    public String stockInfoANameCode() throws IOException {
        return get("stock_info_a_code_name", null);
    }


    /**
     * 个股信息查询
     */
    public String stockIndividualInfoEm(String shareCode) throws IOException {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("symbol", shareCode);
        return get("stock_individual_info_em", parameter);
    }


    /**
     * 交易日历
     */
    public List<Integer> toolTradeDateHistSina() throws IOException {
        String jsonData = get("tool_trade_date_hist_sina", null);
        JSONArray array = JSON.parseArray(jsonData);
        return Optional.ofNullable(array).orElse(new JSONArray()).stream().map(b -> Integer.parseInt(((JSONObject) b).getString("trade_date").split("T")[0].replace("-", ""))).toList();
    }

    /**
     * 个股历史行情
     */
    public String stockZhAHist(String shareCode, PeriodConstant period, AdjustConstant adjust, String startDate, String endDate) throws IOException {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("symbol", shareCode);
        parameter.put("period", period.getCode());
        parameter.put("start_date", startDate);
        parameter.put("end_date", endDate);
        if (adjust != null && adjust != AdjustConstant.NONE) {
            parameter.put("adjust", adjust.getCode());
        }
        return get("stock_zh_a_hist", parameter);
    }





    /**
     * 获取市场资金流向数据
     */
    public List<MarketFundFlow> marketFundFlow() throws IOException {
        String jsonString =  get("stock_market_fund_flow", null);
        
        List<Map<String,String>> list = Objects.requireNonNull(JSON.parseArray(jsonString, Map.class)).stream().map(e-> {
            Map<String,String> map = Maps.newHashMap();
            for(Map.Entry<?, ?> entry : ((Map<?, ?>) e).entrySet()){
                map.put(entry.getKey().toString(),entry.getValue().toString());
            }
            return map;
        }).collect(Collectors.toList());
        
        List<MarketFundFlow> result = new ArrayList<>();
        Optional.of(list)
            .orElse(Collections.emptyList())
            .forEach(map -> {
                // 转换日期格式
                String dateStr = map.get("日期");
                LocalDate date = LocalDate.parse(dateStr);
                int tradeDate = Integer.parseInt(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                
                // 创建上证数据
                MarketFundFlow shFlow = new MarketFundFlow();
                shFlow.setTradeDate(tradeDate);
                shFlow.setMarketType("SH");
                shFlow.setClosingPrice(new BigDecimal(map.get("上证-收盘价")));
                shFlow.setChangeRate(new BigDecimal(map.get("上证-涨跌幅")));
                setCommonFieldsStatic(shFlow, map);
                result.add(shFlow);
                
                // 创建深证数据
                MarketFundFlow szFlow = new MarketFundFlow();
                szFlow.setTradeDate(tradeDate);
                szFlow.setMarketType("SZ");
                szFlow.setClosingPrice(new BigDecimal(map.get("深证-收盘价")));
                szFlow.setChangeRate(new BigDecimal(map.get("深证-涨跌幅")));
                setCommonFieldsStatic(szFlow, map);
                result.add(szFlow);
            });
            
        return result;
    }
    
    private static void setCommonFieldsStatic(MarketFundFlow entity, Map<String,String> map) {
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

    /**
     * 获取指数列表数据
     * @return 返回指数列表数据
     * @throws IOException 请求异常
     */
    public String indexStockInfo() throws IOException {
        return get("index_stock_info", null);
    }

    /**
     * 东方财富-概念板块
     * @return 概念板块数据JSON字符串
     * @throws IOException 请求异常
     */
    public List<ConceptMarket> stockBoardConceptNameEm() throws IOException {
        String jsonData = get("stock_board_concept_name_em", null);
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        JSONArray array = JSON.parseArray(jsonData);
        if (array == null || array.isEmpty()) {
            return null;
        }
        List<ConceptMarket> result = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new ConceptMarket(item,currentTime));
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
