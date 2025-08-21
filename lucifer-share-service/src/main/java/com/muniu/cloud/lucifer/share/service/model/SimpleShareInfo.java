package com.muniu.cloud.lucifer.share.service.model;

import com.muniu.cloud.lucifer.share.service.constant.ShareBoard;
import com.muniu.cloud.lucifer.share.service.constant.ShareExchange;
import lombok.Data;

@Data
public class SimpleShareInfo {

    private String shareCode;

    private String shareName;

    private int listDate;

    private ShareBoard shareBoard;

    private ShareExchange shareExchange;


    public SimpleShareInfo(String shareCode, String shareName, int listDate, ShareExchange shareExchange, ShareBoard shareBoard) {
        this.shareCode = shareCode;
        this.shareName = shareName;
        this.listDate = listDate;
        this.shareBoard = shareBoard;
        this.shareExchange = shareExchange;
    }
}
