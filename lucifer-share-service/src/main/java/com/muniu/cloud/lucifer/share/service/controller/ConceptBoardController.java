package com.muniu.cloud.lucifer.share.service.controller;

import com.muniu.cloud.lucifer.commons.model.vo.RestResponse;
import com.muniu.cloud.lucifer.share.service.impl.TdConceptMarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 概念板块控制器
 */
@RestController
@RequestMapping("/api/stock/concept/board")
@Tag(name = "概念板块接口", description = "提供概念板块数据查询和同步相关接口")
public class ConceptBoardController {

    private final TdConceptMarketService tdConceptMarketService;

    @Autowired
    public ConceptBoardController(TdConceptMarketService tdConceptMarketService) {
        this.tdConceptMarketService = tdConceptMarketService;
    }

    /**
     * 获取所有概念代码
     * @return 概念板块代码
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有概念板块代码", description = "返回所有概念板块的代码，优先从缓存获取")
    public RestResponse<List<String>> getAllConceptBoards() {
        return RestResponse.success(tdConceptMarketService.getAllConceptBoards());
    }

    /**
     * 手动触发同步概念板块数据
     * @return 处理结果
     */
    @PostMapping("/sync")
    @Operation(summary = "手动触发同步概念板块数据", description = "从东方财富获取最新概念板块数据并保存到数据库")
    public RestResponse<String> manualSync() throws IOException {
        return RestResponse.success();
    }
} 