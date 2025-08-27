package com.muniu.cloud.lucifer.share.service.controller;

import com.google.common.collect.Lists;
import com.muniu.cloud.lucifer.commons.model.page.PageParams;
import com.muniu.cloud.lucifer.commons.model.page.PageResult;
import com.muniu.cloud.lucifer.commons.model.vo.RestResponse;
import com.muniu.cloud.lucifer.share.service.entity.ShareInfo;
import com.muniu.cloud.lucifer.share.service.impl.ShareInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 股票相关控制器
 */
@RequestMapping("share")
@RestController
@Tag(name = "股票管理", description = "提供股票基本信息的查询功能")
public class ShareController {

    private final ShareInfoService shareInfoService;

    @Autowired
    public ShareController(ShareInfoService shareInfoService) {
        this.shareInfoService = shareInfoService;
    }


    @Operation(summary = "查询股票列表", description = "分页查询股票列表", responses = {@ApiResponse(responseCode = "200", description = "成功获取股票信息列表", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class)))})
    @PostMapping("page.json")
    public RestResponse<PageResult<ShareInfo>> list(@RequestBody PageParams pageParams) {
        return RestResponse.success(shareInfoService.getPageList(pageParams));
    }

    /**
     * 根据股票代码批量获取股票信息
     * @param shareCodes 股票代码列表
     * @return 股票信息列表
     */
    @PostMapping("getInfos.json")
    @Operation(
        summary = "批量获取股票信息", 
        description = "根据股票代码列表批量获取股票信息",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "成功获取股票信息列表",
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = RestResponse.class)
                )
            )
        }
    )
    public RestResponse<List<ShareInfo>> getInfos(
            @Parameter(description = "股票代码列表", required = true) 
            List<String> shareCodes) {
        if(shareCodes == null || shareCodes.isEmpty()){
            return RestResponse.success(Lists.newArrayList());
        }
        return RestResponse.success(shareInfoService.getByShareCodes(shareCodes));
    }
}
