package com.muniu.cloud.lucifer.share.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface  BaseShardingMapper<E>  extends BaseMapper<E> {



    /**
     * 查询所有表
     * @return 表名集合（不含前缀）
     */
    List<String> selectTable();


    /**
     * 创建表
     * @param sharding 分表标识，格式为yyyyMMdd
     */
    void createTable(@Param("sharding") String sharding);
}
