package com.muniu.cloud.lucifer.share.service.impl;

import java.util.List;
import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.muniu.cloud.lucifer.share.service.entity.MarketFundFlow;
import com.muniu.cloud.lucifer.share.service.mapper.MarketFundFlowMapper;

@Service
@Slf4j
public class StockMarketFundFlowService {

    private final AkToolsService akToolsService;

    private final MarketFundFlowMapper marketFundFlowMapper;

    private final TradingDayService tradingDayService;

    @Autowired
    public StockMarketFundFlowService(AkToolsService akToolsService, MarketFundFlowMapper marketFundFlowMapper, TradingDayService tradingDayService) {
        this.akToolsService = akToolsService;
        this.marketFundFlowMapper = marketFundFlowMapper;
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
            List<MarketFundFlow> fundFlows = akToolsService.marketFundFlow();
            
            for (MarketFundFlow flow : fundFlows) {
                marketFundFlowMapper.insert(flow);
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
    public List<MarketFundFlow> queryByDate(Integer tradeDate) {
        LambdaQueryWrapper<MarketFundFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketFundFlow::getTradeDate, tradeDate);
        return marketFundFlowMapper.selectList(wrapper);
    }
    
    /**
     * 按照市场类型和日期查询资金流向数据
     * @param marketType 市场类型：SH-上证，SZ-深证
     * @param tradeDate 交易日期，格式：20240925
     * @return 资金流向数据列表
     */
    public List<MarketFundFlow> queryByMarketTypeAndDate(String marketType, Integer tradeDate) {
        LambdaQueryWrapper<MarketFundFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketFundFlow::getMarketType, marketType)
               .eq(MarketFundFlow::getTradeDate, tradeDate);
        return marketFundFlowMapper.selectList(wrapper);
    }
    
    /**
     * 按照市场类型和时间段查询资金流向数据
     * @param marketType 市场类型：SH-上证，SZ-深证
     * @param startDate 开始日期，格式：20240925
     * @param endDate 结束日期，格式：20240925
     * @return 资金流向数据列表
     */
    public List<MarketFundFlow> queryByMarketTypeAndDateRange(String marketType, Integer startDate, Integer endDate) {
        LambdaQueryWrapper<MarketFundFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketFundFlow::getMarketType, marketType)
               .ge(MarketFundFlow::getTradeDate, startDate)
               .le(MarketFundFlow::getTradeDate, endDate)
               .orderByAsc(MarketFundFlow::getTradeDate);
        return marketFundFlowMapper.selectList(wrapper);
    }
}
