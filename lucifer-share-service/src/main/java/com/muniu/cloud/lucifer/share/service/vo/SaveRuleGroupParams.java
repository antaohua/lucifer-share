package com.muniu.cloud.lucifer.share.service.vo;

import jakarta.validation.constraints.NotBlank;

public record SaveRuleGroupParams(
        @NotBlank(message = "规则名称不能为空")
                            String name,
                            String description) {

}
