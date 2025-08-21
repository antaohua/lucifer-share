package com.muniu.cloud.lucifer.share.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.muniu.cloud.lucifer.share.service.entity.MarketFundFlow;
import com.muniu.cloud.lucifer.share.service.impl.StockMarketFundFlowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.muniu.cloud.lucifer.commons.model.vo.RestResponse;

/**
 * 股票市场资金流向控制器
 */
@RestController
@RequestMapping("/share/market/fund-flow")
@Tag(name = "市场资金流向", description = "提供股票市场资金流向数据的查询功能")
public class StockMarketFundFlowController {

    private final StockMarketFundFlowService stockMarketFundFlowService;

    @Autowired
    public StockMarketFundFlowController(StockMarketFundFlowService stockMarketFundFlowService) {
        this.stockMarketFundFlowService = stockMarketFundFlowService;
    }

    /**
     * 按照日期查询资金流向数据
     * @param tradeDate 交易日期，格式：20240925
     * @return 资金流向数据列表
     */
    @GetMapping("/date/{tradeDate}")
    @Operation(
        summary = "按日期查询资金流向", 
        description = "根据交易日期查询股票市场资金流向数据",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取资金流向数据",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<MarketFundFlow>> queryByDate(
            @Parameter(description = "交易日期，格式：yyyyMMdd", example = "20240325", required = true) 
            @PathVariable("tradeDate") Integer tradeDate) {
        return RestResponse.success(stockMarketFundFlowService.queryByDate(tradeDate));
    }
    
    /**
     * 按照市场类型和日期查询资金流向数据
     * @param marketType 市场类型：SH-上证，SZ-深证
     * @param tradeDate 交易日期，格式：20240925
     * @return 资金流向数据列表
     */
    @GetMapping("/market/{marketType}/date/{tradeDate}")
    @Operation(
        summary = "按市场和日期查询资金流向", 
        description = "根据市场类型和交易日期查询股票市场资金流向数据",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取资金流向数据",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<MarketFundFlow>> queryByMarketTypeAndDate(
            @Parameter(description = "市场类型：SH-上证，SZ-深证", example = "SH", required = true) 
            @PathVariable("marketType") String marketType,
            @Parameter(description = "交易日期，格式：yyyyMMdd", example = "20240325", required = true) 
            @PathVariable("tradeDate") Integer tradeDate) {
        return RestResponse.success(stockMarketFundFlowService.queryByMarketTypeAndDate(marketType, tradeDate));
    }
    
    /**
     * 按照市场类型和时间段查询资金流向数据
     * @param marketType 市场类型：SH-上证，SZ-深证
     * @param startDate 开始日期，格式：20240925
     * @param endDate 结束日期，格式：20240925
     * @return 资金流向数据列表
     */
    @GetMapping("/market/{marketType}/date-range/{startDate}/{endDate}")
    @Operation(
        summary = "按市场和日期范围查询资金流向", 
        description = "根据市场类型和日期范围查询股票市场资金流向数据",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取资金流向数据",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<MarketFundFlow>> queryByMarketTypeAndDateRange(
            @Parameter(description = "市场类型：SH-上证，SZ-深证", example = "SH", required = true) 
            @PathVariable("marketType") String marketType,
            @Parameter(description = "开始日期，格式：yyyyMMdd", example = "20240301", required = true) 
            @PathVariable("startDate") Integer startDate,
            @Parameter(description = "结束日期，格式：yyyyMMdd", example = "20240325", required = true) 
            @PathVariable("endDate") Integer endDate) {
        return RestResponse.success(stockMarketFundFlowService.queryByMarketTypeAndDateRange(marketType, startDate, endDate));
    }
} 