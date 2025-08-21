package com.muniu.cloud.lucifer.share.service.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateRuleGroupParams(
        @NotNull
        Long id,
        @NotBlank(message = "规则名称不能为空")
        String name,
        String description) {

}
