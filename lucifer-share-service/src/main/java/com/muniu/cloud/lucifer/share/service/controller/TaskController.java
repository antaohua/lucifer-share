package com.muniu.cloud.lucifer.share.service.controller;


import com.muniu.cloud.lucifer.share.service.impl.ShareInfoService;
import com.muniu.cloud.lucifer.share.service.impl.TdIndexHistService;
import com.muniu.cloud.lucifer.share.service.impl.TdShareHistService;
import com.muniu.cloud.lucifer.share.service.impl.TdShareMarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务控制器，提供各种数据同步任务的手动触发接口
 */
@RequestMapping("share/task")
@RestController
@Tag(name = "数据同步任务", description = "提供各种数据同步任务的手动触发接口")
public class TaskController {

    @Autowired
    private ShareInfoService shareInfoService;

    @Autowired
    private TdShareMarketService tdShareMarketService;

    @Autowired
    private TdShareHistService tdShareHistService;

    @Autowired
    private TdIndexHistService tdIndexHistService;

    /**
     * 获取远程股票历史数据
     * @return 任务执行结果
     * @throws Exception 任务执行异常
     */
    @PostMapping("fetchRemoteStockHistData")
    @Operation(
        summary = "获取远程股票历史数据", 
        description = "从远程API获取股票的历史交易数据并保存到本地数据库"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "任务启动成功"),
        @ApiResponse(responseCode = "500", description = "任务启动失败")
    })
    public String fetchRemoteStockHistData() throws Exception {

        return "success";
    }



    /**
     * 获取远程股票实时数据
     * @return 任务执行结果
     * @throws Exception 任务执行异常
     */
    @PostMapping("fetchRemoteStockRealTimeData")
    @Operation(
        summary = "获取远程股票实时数据", 
        description = "从远程API获取股票的实时交易数据"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "任务启动成功"),
        @ApiResponse(responseCode = "500", description = "任务启动失败")
    })
    public String fetchRemoteStockRealTimeData() throws Exception {

        return "success";
    }

    /**
     * 使用实时数据更新股票历史数据
     * @return 任务执行结果
     * @throws Exception 任务执行异常
     */
    @PostMapping("updateShareHistByRealTimeData")
    @Operation(
        summary = "使用实时数据更新股票历史数据", 
        description = "使用当天的实时交易数据更新股票历史数据"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "任务启动成功"),
        @ApiResponse(responseCode = "500", description = "任务启动失败")
    })
    public String updateShareHistByRealTimeData() throws Exception {

        return "success";
    }

}
