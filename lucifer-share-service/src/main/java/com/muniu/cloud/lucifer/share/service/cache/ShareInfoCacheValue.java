package com.muniu.cloud.lucifer.share.service.cache;


import com.muniu.cloud.lucifer.share.service.constant.ShareBoard;
import com.muniu.cloud.lucifer.share.service.constant.ShareExchange;
import com.muniu.cloud.lucifer.share.service.constant.ShareStatus;
import com.muniu.cloud.lucifer.share.service.entity.ShareInfo;

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


    public ShareBoard getSection() {
        return section;
    }

    public void setSection(ShareBoard section) {
        this.section = section;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public int getListDate() {
        return listDate;
    }

    public void setListDate(int listDate) {
        this.listDate = listDate;
    }

    public ShareExchange getExchange() {
        return exchange;
    }

    public void setExchange(ShareExchange exchange) {
        this.exchange = exchange;
    }

    public ShareStatus getStatus() {
        return status;
    }

    public void setStatus(ShareStatus status) {
        this.status = status;
    }

    public int getHistoryUpdateDate() {
        return historyUpdateDate;
    }

    public void setHistoryUpdateDate(int historyUpdateDate) {
        this.historyUpdateDate = historyUpdateDate;
    }

    public ShareInfoCacheValue(ShareBoard section, String shareName, int listDate, ShareExchange exchange, ShareStatus status, int historyUpdateDate) {
        this.section = section;
        this.shareName = shareName;
        this.listDate = listDate;
        this.exchange = exchange;
        this.status = status;
        this.historyUpdateDate = historyUpdateDate;
    }


    public ShareInfoCacheValue(ShareInfo entity) {
        this.section = ShareBoard.fromKey(entity.getSection());
        this.shareName = entity.getShareName();
        this.listDate = entity.getListDate() == null ? 0 : entity.getListDate();
        this.exchange = ShareExchange.getExchangeConstant(entity.getExchange());
        this.status = ShareStatus.fromCode(entity.getShareStatus());
        this.historyUpdateDate = entity.getHistoryUpdateDate();
    }
}
