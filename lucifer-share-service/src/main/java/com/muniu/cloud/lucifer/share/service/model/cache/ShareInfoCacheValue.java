package com.muniu.cloud.lucifer.share.service.model.cache;


import com.muniu.cloud.lucifer.share.service.constant.ShareBoard;
import com.muniu.cloud.lucifer.share.service.constant.ShareExchange;
import com.muniu.cloud.lucifer.share.service.constant.ShareStatus;
import com.muniu.cloud.lucifer.share.service.entity.ShareInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * @author antaohua
 */
@Setter
@Getter
public class ShareInfoCacheValue {
    /**
     * 板块
     * */
    private ShareBoard section;

    /**
     * 股简称
     * */
    private String shareName;

    /**
     * 股上市日期
     * */
    private int listDate;

    /**
     * 交易所
     * */
    private ShareExchange exchange;

    /**
     * 股状态
     * */
    private ShareStatus status;

    /**
     * 历史数据更新时间
     * */
    private int historyUpdateDate;

    private int infoUpdateDate;



    public ShareInfoCacheValue(ShareInfo entity) {
        this.section = ShareBoard.fromKey(entity.getSection());
        this.shareName = entity.getShareName();
        this.listDate = entity.getListDate() == null ? 0 : entity.getListDate();
        this.exchange = ShareExchange.getExchangeConstant(entity.getExchange());
        this.status = ShareStatus.fromCode(entity.getShareStatus());
        this.historyUpdateDate = Optional.ofNullable(entity.getHistoryUpdateDate()).orElse(0);
        this.infoUpdateDate = Optional.ofNullable(entity.getInfoUpdateDate()).orElse(0);
    }
}
