package com.muniu.cloud.lucifer.share.service.controller;

import com.muniu.cloud.lucifer.share.service.cache.IndexInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.dto.IndexInfoQueryDTO;
import com.muniu.cloud.lucifer.share.service.dto.IndexInfoUpdateDTO;
import com.muniu.cloud.lucifer.share.service.entity.IndexInfo;
import com.muniu.cloud.lucifer.share.service.impl.TdIndexHistService;
import com.muniu.cloud.lucifer.share.service.impl.IndexInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.muniu.cloud.lucifer.commons.model.vo.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 指数相关控制器
 */
@RestController
@RequestMapping("/share/index")
@Tag(name = "指数管理", description = "提供指数基本信息的查询和管理功能")
public class IndexController {

    private final IndexInfoService indexInfoService;
    private final TdIndexHistService tdIndexHistService;

    @Autowired
    public IndexController(IndexInfoService indexInfoService, TdIndexHistService tdIndexHistService) {
        this.indexInfoService = indexInfoService;
        this.tdIndexHistService = tdIndexHistService;
    }

    /**
     * 获取所有指数信息
     * @return 指数信息列表
     */
    @GetMapping("/all")
    @Operation(
        summary = "获取所有指数信息", 
        description = "返回系统中所有指数的基本信息",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取指数信息列表",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<IndexInfoCacheValue>> getAllIndexes() {
        return RestResponse.success(indexInfoService.getAllIndexCache());
    }

    /**
     * 根据指数代码获取指数信息
     * @param indexCode 指数代码
     * @return 指数信息
     */
    @GetMapping("/{indexCode}")
    @Operation(
        summary = "根据指数代码获取信息", 
        description = "根据指定的指数代码查询指数信息",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取指数信息",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<IndexInfoCacheValue> getIndexByCode(
            @Parameter(description = "指数代码", required = true) 
            @PathVariable String indexCode) {
        return RestResponse.success(indexInfoService.getIndexCache(indexCode));
    }


    /**
     * 根据指数来源获取指数信息
     * @param source 指数来源
     * @return 指数信息列表
     */
    @GetMapping("/source/{source}")
    @Operation(
        summary = "根据来源获取指数列表", 
        description = "根据指数来源查询符合条件的指数列表",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取指数信息列表",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<IndexInfoCacheValue>> getIndexesBySource(
            @Parameter(description = "指数来源", required = true) 
            @PathVariable String source) {
        return RestResponse.success(indexInfoService.getIndexCacheBySource(source));
    }

    /**
     * 手动触发所有指数历史数据同步
     * @return 处理结果
     */
    @PostMapping("/sync-hist")
    @Operation(
        summary = "同步所有指数历史数据", 
        description = "手动触发所有指数的历史数据同步任务",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "同步任务启动成功",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<String> syncIndexHistData() throws Exception {
        tdIndexHistService.syncIndexHistData(false);
        return RestResponse.success("指数历史数据同步任务已启动");
    }

    /**
     * 手动触发单个指数历史数据同步
     * @param indexCode 指数代码
     * @return 处理结果
     */
    @PostMapping("/sync-hist/{indexCode}")
    @Operation(
        summary = "同步单个指数历史数据", 
        description = "手动触发指定指数的历史数据同步任务",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "同步任务启动成功",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<String> syncSingleIndexHistData(
            @Parameter(description = "指数代码", required = true) 
            @PathVariable String indexCode) throws Exception {
        tdIndexHistService.manualSyncIndexHist(indexCode);
        return RestResponse.success();
    }

    /**
     * 分页查询指数信息
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    @Operation(
        summary = "分页查询指数信息", 
        description = "根据条件分页查询指数信息",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "查询成功",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<IndexInfo>> queryIndexPage(
            @Parameter(description = "查询条件", required = true) 
            @RequestBody IndexInfoQueryDTO queryDTO) {
        List<IndexInfo> page = indexInfoService.queryIndexInfoPage(queryDTO);
        return RestResponse.success(page);
    }
    
    /**
     * 更新指数设置
     * @param updateDTO 更新数据
     * @return 处理结果
     */
    @PostMapping("/update")
    @Operation(
        summary = "更新指数设置", 
        description = "更新指定指数的来源、是否更新历史数据和是否更新成分股设置",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "更新成功",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<Boolean> updateIndexSettings(
            @Parameter(description = "更新数据", required = true) 
            @RequestBody @Valid IndexInfoUpdateDTO updateDTO) {
        boolean result = indexInfoService.updateIndexSettings(updateDTO);
        return RestResponse.success(result);
    }


} 