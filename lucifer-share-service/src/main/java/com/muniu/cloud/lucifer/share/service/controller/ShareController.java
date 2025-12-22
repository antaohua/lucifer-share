package com.muniu.cloud.lucifer.share.service.controller;

import com.google.common.collect.Lists;
import com.muniu.cloud.lucifer.commons.model.vo.RestResponse;
import com.muniu.cloud.lucifer.share.service.impl.ShareInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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

}
