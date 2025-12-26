package com.muniu.cloud.lucifer.share.service.clients;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.muniu.cloud.lucifer.commons.core.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.utils.exception.HttpClientException;
import com.muniu.cloud.lucifer.share.service.constant.AdjustConstant;
import com.muniu.cloud.lucifer.share.service.constant.PeriodConstant;
import com.muniu.cloud.lucifer.share.service.entity.TradeBoardMarketEntity;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class EastmoneyBoardMarketApiClient {

    private final LuciferHttpClient autoProxyHttpClient;


    public EastmoneyBoardMarketApiClient(@Qualifier("autoProxyHttpClient") LuciferHttpClient autoProxyHttpClient) {
        this.autoProxyHttpClient = autoProxyHttpClient;
    }


    public List<ConceptBoardDTO> boardMarket() {
        int pageNum = 1;
        int pageSize;
        List<ConceptBoardDTO> records = new ArrayList<>();
        do{
            String url = buildUrl(pageNum);
            String body;
            try {
                body= autoProxyHttpClient.get(url);
            }catch (HttpClientException e){
                log.error("error :staticProxyHttpClient pageNum:{}, error:{}", pageNum, e.getMessage());
                return  null;
            }
            JSONObject root = JSON.parseObject(body);
            JSONObject data = root.getJSONObject("data");
            if (data == null || !data.containsKey("diff") || data.getJSONArray("diff") == null || data.getJSONArray("diff").isEmpty()) {
                return null;
            }
            pageSize = data.getIntValue("total");
            JSONArray jsonArray = data.getJSONArray("diff");

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                ConceptBoardDTO dto = item.toJavaObject(ConceptBoardDTO.class);
                records.add(dto);
            }
            JSON.toJSONString(records);
            pageNum++;
        }while (pageSize == 100);

        return records;

    }

    @Data
    public static class ConceptBoardDTO {

        /** 板块代码，如 BK1628 */
        @JSONField(name = "f12")
        private String boardCode;

        /** 板块名称 */
        @JSONField(name = "f14")
        private String boardName;

        /** 最新指数 */
        @JSONField(name = "f2")
        private Double latestPrice;

        /** 涨跌幅 (%) */
        @JSONField(name = "f3")
        private Double changeRate;

        /** 涨跌额 */
        @JSONField(name = "f4")
        private Double changeAmount;

        /** 换手率 */
        @JSONField(name = "f8")
        private Double turnoverRate;

        /** 最高 */
        @JSONField(name = "f15")
        private Double high;

        /** 最低 */
        @JSONField(name = "f16")
        private Double low;

        /** 开盘 */
        @JSONField(name = "f17")
        private Double open;

        /** 昨收 */
        @JSONField(name = "f18")
        private Double preClose;

        /** 总市值 */
        @JSONField(name = "f20")
        private Long totalMarketValue;

        /** 流通市值 */
        @JSONField(name = "f21")
        private Long circulationMarketValue;

        /** 振幅 */
        @JSONField(name = "f22")
        private Double amplitude;

        /** 5 日涨跌幅 */
        @JSONField(name = "f24")
        private Double rise5Day;

        /** 20 日涨跌幅 */
        @JSONField(name = "f25")
        private Double rise20Day;

        /** 量比 */
        @JSONField(name = "f33")
        private Double volumeRatio;

        /** 主力净流入 */
        @JSONField(name = "f62")
        private Long mainMoney;

        /** 上涨家数 */
        @JSONField(name = "f104")
        private Integer riseCount;

        /** 平盘家数 */
        @JSONField(name = "f105")
        private Integer flatCount;

        /** 下跌家数 */
        @JSONField(name = "f107")
        private Integer fallCount;

        /** 60 日涨跌幅 */
        @JSONField(name = "f11")
        private Double changeRate60Day;

        /** 更新时间（时间戳，秒） */
        @JSONField(name = "f124")
        private Long updateTime;

        /** 领涨股名称 */
        @JSONField(name = "f128")
        private String leadingStock;

        /** 领涨股涨幅 */
        @JSONField(name = "f136")
        private Double leadingStockRate;

        /** 领涨股代码 */
        @JSONField(name = "f140")
        private String leadingStockCode;

        /** 是否 ST（概念一般为 0） */
        @JSONField(name = "f141")
        private Integer isSt;

        public TradeBoardMarketEntity toTradeBoardMarketEntity() {
            TradeBoardMarketEntity entity = new TradeBoardMarketEntity();
            entity.setBoardCode(this.boardCode);
            entity.setBoardName(this.boardName);
            entity.setLatestPrice(BigDecimal.valueOf(latestPrice));
            entity.setChangeAmount(BigDecimal.valueOf(changeAmount));
            entity.setChangeRate(BigDecimal.valueOf(changeRate));
            entity.setTotalMarketValue(this.totalMarketValue);
            entity.setTurnoverRate(BigDecimal.valueOf(turnoverRate));
            entity.setUpCount(this.riseCount);
            entity.setDownCount(this.fallCount);
            entity.setLeadingStock(this.leadingStock);
            entity.setLeadingStockChangeRate(BigDecimal.valueOf(leadingStockRate));
            entity.setUpdateTime(this.updateTime * 1000); // 转换为毫秒
            entity.setCreateTime(System.currentTimeMillis());
            entity.setRank(0); // 排名字段未提供，设置为0或根据需要处理

            return entity;
        }
    }



    public static String buildUrl(int pageNum) {
        // 构建 URL 和请求参数
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://79.push2.eastmoney.com/api/qt/clist/get")).newBuilder();
        urlBuilder.addQueryParameter("fields", "f2,f3,f4,f8,f12,f14,f15,f16,f17,f18,f20,f21,f24,f25,f22,f33,f11,f62,f128,f124,f107,f104,f105,f136");
        urlBuilder.addQueryParameter("fs", "m:90+t:3+f:!50");
        urlBuilder.addQueryParameter("fid", "f12");
        urlBuilder.addQueryParameter("invt", "2");
        urlBuilder.addQueryParameter("fltt", "2");
        urlBuilder.addQueryParameter("ut", "bd1d9ddb04089700cf9c27f6f7426281");
        urlBuilder.addQueryParameter("np", "1");
        urlBuilder.addQueryParameter("po", "1");
        urlBuilder.addQueryParameter("pn", "" + pageNum);
        urlBuilder.addQueryParameter("pz", "100");
        // 返回构建的 URL 字符串
        return urlBuilder.build().toString();
    }



}
