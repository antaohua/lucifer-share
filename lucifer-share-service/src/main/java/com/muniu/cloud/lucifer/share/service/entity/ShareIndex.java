package com.muniu.cloud.lucifer.share.service.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("share_indices")
public class ShareIndex {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private String indicesName;

    private String indicesType;

    private Integer indicesDate;

    private Long createTime;
}
