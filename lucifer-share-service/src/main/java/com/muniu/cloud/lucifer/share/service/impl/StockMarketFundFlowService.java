package com.muniu.cloud.lucifer.share.service.impl;

import com.muniu.cloud.lucifer.commons.model.constants.Condition;
import com.muniu.cloud.lucifer.commons.model.constants.Operator;
import com.muniu.cloud.lucifer.share.service.dao.MarketFundFlowDao;
import com.muniu.cloud.lucifer.share.service.entity.MarketFundFlowEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StockMarketFundFlowService {

    private final AkToolsService akToolsService;

    private final MarketFundFlowDao marketFundFlowDao;

    private final TradingDateTimeService tradingDayService;

    @Autowired
    public StockMarketFundFlowService(AkToolsService akToolsService, MarketFundFlowDao marketFundFlowDao, TradingDateTimeService tradingDayService) {
        this.akToolsService = akToolsService;
        this.marketFundFlowDao = marketFundFlowDao;
        this.tradingDayService = tradingDayService;
    }

    @Scheduled(cron = "0 0 17 * * ?")  // 每天下午5点执行
    public void syncFundFlowData() {
        // 检查是否是交易日
        if (!tradingDayService.isTradingDay(LocalDate.now())) {
            log.info("今天不是交易日，跳过数据同步");
            return;
        }
        try {
            List<MarketFundFlowEntity> fundFlows = akToolsService.marketFundFlow();
            for (MarketFundFlowEntity flow : fundFlows) {
                marketFundFlowDao.save(flow);
            }
            
            log.info("同步资金流向数据成功，数据条数: {}", fundFlows.size());
                
        } catch (Exception e) {
            log.error("同步资金流向数据失败", e);
        }
    }
    
    /**
     * 按照日期查询资金流向数据
     * @param tradeDate 交易日期，格式：20240925
     * @return 资金流向数据列表
     */
    public List<MarketFundFlowEntity> queryByDate(Integer tradeDate) {
        return marketFundFlowDao.getByProperty(Map.of("tradeDate",tradeDate),false);
    }
    
    /**
     * 按照市场类型和日期查询资金流向数据
     * @param marketType 市场类型：SH-上证，SZ-深证
     * @param tradeDate 交易日期，格式：20240925
     * @return 资金流向数据列表
     */
    public List<MarketFundFlowEntity> queryByMarketTypeAndDate(String marketType, Integer tradeDate) {
        return marketFundFlowDao.getByProperty(Map.of("marketType",marketType,"tradeDate",tradeDate),false);
    }
    
    /**
     * 按照市场类型和时间段查询资金流向数据
     * @param marketType 市场类型：SH-上证，SZ-深证
     * @param startDate 开始日期，格式：20240925
     * @param endDate 结束日期，格式：20240925
     * @return 资金流向数据列表
     */
    public List<MarketFundFlowEntity> queryByMarketTypeAndDateRange(String marketType, Integer startDate, Integer endDate) {
        List<Condition> whereConditions = List.of(
                Condition.build("marketType", Operator.EQUALS, marketType),
                Condition.build("tradeDate", Operator.GREATER_THAN_OR_EQUALS,startDate),
            Condition.build("tradeDate", Operator.LESS_THAN_OR_EQUALS, endDate));
        return marketFundFlowDao.getByProperty(whereConditions,false);
    }
}
