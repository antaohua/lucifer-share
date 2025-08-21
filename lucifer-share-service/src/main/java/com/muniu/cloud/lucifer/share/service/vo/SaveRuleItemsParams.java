package com.muniu.cloud.lucifer.share.service.vo;

import java.util.List;

public record SaveRuleItemsParams(Long groupId, List<SaveRuleParams> rule) {
}
