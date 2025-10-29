package com.muniu.cloud.lucifer.share.service.model.cache;


import com.muniu.cloud.lucifer.share.service.constant.LuciferShareConstant;
import com.muniu.cloud.lucifer.share.service.constant.ShareBoard;
import com.muniu.cloud.lucifer.share.service.constant.ShareExchange;
import com.muniu.cloud.lucifer.share.service.constant.ShareStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.HashOperations;

import java.util.Map;

/**
 * @author antaohua
 */
@Setter
@Getter
@NoArgsConstructor
public class ShareInfoCacheValue {



    public static final String HASH_CODE = "code";

    public static final String HASH_STATUS = "status";

    public static final String HASH_BOARD = "board";

    public static final String HASH_EXCHANGE = "exchange";

    public static final String HASH_HISTORY = "history";

    public static final String HASH_NAME = "name";


    private String code;
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


    private ShareBoard board;
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


    public ShareInfoCacheValue(HashOperations<String, String, String> hashOps , String shareCode) {
        if (shareCode == null || shareCode.isEmpty()) return;
        Map<String, String> data = hashOps.entries(LuciferShareConstant.getRedisShareStatusKey(shareCode));
        if (data.containsKey(HASH_CODE)) return;
        this.code = data.get(HASH_CODE);
        this.shareName = data.get(HASH_NAME);
        this.exchange = ShareExchange.getExchangeConstant(data.get(HASH_EXCHANGE));
        this.status = ShareStatus.valueOf(data.get(HASH_STATUS));
        this.listDate = Integer.parseInt(data.get(HASH_EXCHANGE));
        this.board = ShareBoard.valueOf(data.get(HASH_BOARD));
        this.historyUpdateDate = Integer.parseInt(data.get(HASH_HISTORY) == null ? "0" : data.get(HASH_HISTORY));
    }







}
