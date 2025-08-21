package com.muniu.cloud.lucifer.share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import com.muniu.cloud.lucifer.share.service.mapper.BaseShardingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
public class BaseShardingService<mapper extends BaseShardingMapper<entity>,entity> extends ServiceImpl<mapper, entity> {


    private final Set<String> tables = Sets.newConcurrentHashSet();
    private final Object lock = new Object();
    /**
     * 创建表（如果不存在）
     * @param sharding 分表值
     */
    @Transactional(rollbackFor = Exception.class)
    public void createTable(String sharding) {
        if (tables.contains(sharding)) {
            return;
        }
        synchronized (lock) {
            if (tables.isEmpty()) {
                tables.clear();
                tables.addAll(getBaseMapper().selectTable());
            }
            if (tables.contains(sharding)) {
                return;
            }
            getBaseMapper().createTable(sharding);
            tables.add(sharding);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void createTable(int sharding){
        createTable(String.valueOf(sharding));
    }

}
