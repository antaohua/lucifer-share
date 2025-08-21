package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("share_index_cons")
public class ShareIndexCons {

    private String id;

    private String indexCode;

    private String shareCode;

    private Integer date;

    private byte status;
}
