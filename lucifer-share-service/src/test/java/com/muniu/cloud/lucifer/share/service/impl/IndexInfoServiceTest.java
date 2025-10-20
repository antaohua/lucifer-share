// package com.muniu.cloud.lucifer.share.service.impl;

// import com.muniu.cloud.lucifer.share.service.entity.IndexInfoEntity;
// import com.muniu.cloud.lucifer.share.service.cache.IndexInfoCacheValue;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;

// import static org.junit.jupiter.api.Assertions.*;

// /**
//  * IndexInfoService 集成测试
//  * 直接连接数据库测试
//  */
// @SpringBootTest
// @ActiveProfiles("test")
// public class IndexInfoServiceTest {

//     @Autowired
//     private IndexInfoService indexInfoService;

//     @BeforeEach
//     public void setUp() {
//         // 刷新缓存
//         indexInfoService.refreshCache();
//     }

//     /**
//      * 测试获取所有指数缓存
//      */
//     @Test
//     @Transactional
//     public void testGetAllIndexCache() {
//         List<IndexInfoCacheValue> allCache = indexInfoService.getAllIndexCache();
//         assertNotNull(allCache);
//         System.out.println("所有缓存数据: " + allCache.size());
//     }
    
//     /**
//      * 测试根据来源获取指数缓存
//      */
//     @Test
//     @Transactional
//     public void testGetIndexCacheBySource() {
//         List<IndexInfoCacheValue> shCache = indexInfoService.getIndexCacheBySource("SH");
//         assertNotNull(shCache);
//         System.out.println("上海市场指数数量: " + shCache.size());

//         List<IndexInfoCacheValue> szCache = indexInfoService.getIndexCacheBySource("SZ");
//         assertNotNull(szCache);
//         System.out.println("深圳市场指数数量: " + szCache.size());
//     }
    
//     /**
//      * 测试根据指数代码获取缓存
//      */
//     @Test
//     @Transactional
//     public void testGetIndexCache() {
//         IndexInfoCacheValue cache = indexInfoService.getIndexCache("000001");
//         assertNotNull(cache);
//         assertEquals("000001", cache.getIndexCode());
//         System.out.println("指数信息: " + cache);
//     }
    
//     /**
//      * 测试根据指数名称获取缓存
//      */
//     @Test
//     @Transactional
//     public void testGetIndexCacheByName() {
//         IndexInfoCacheValue cache = indexInfoService.getIndexCacheByName("上证指数");
//         assertNotNull(cache);
//         assertEquals("上证指数", cache.getDisplayName());
//         System.out.println("指数信息: " + cache);
//     }
    
//     /**
//      * 测试指数缓存是否包含特定代码
//      */
//     @Test
//     @Transactional
//     public void testContainsIndex() {
//         assertTrue(indexInfoService.containsIndex("000001"));
//         assertFalse(indexInfoService.containsIndex("999999"));
//     }
    
//     /**
//      * 测试从API获取指数列表
//      */
//     @Test
//     @Transactional
//     public void testGetIndexInfoList() throws Exception {
//         List<IndexInfoEntity> indexInfoList = indexInfoService.getIndexInfoList();
//         assertNotNull(indexInfoList);
//         assertFalse(indexInfoList.isEmpty());
//         System.out.println("获取到的指数列表数量: " + indexInfoList.size());
//     }
    
//     /**
//      * 测试保存指数列表信息
//      * 使用事务回滚，避免测试数据影响生产数据
//      */
//     @Test
//     @Transactional
//     public void testSaveIndexInfoList() throws Exception {
//         List<IndexInfoEntity> indexInfoList = indexInfoService.getIndexInfoList();
//         indexInfoService.saveIndexInfoList(indexInfoList);
//         System.out.println("保存指数列表结果: " + indexInfoList.size());
//     }
    
//     /**
//      * 测试根据指数代码和更新日期查询
//      */
//     @Test
//     @Transactional
//     public void testGetIndexInfoByCodeAndDate() {
//         String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//         IndexInfoEntity indexInfo = indexInfoService.getIndexInfoByCodeAndDate("000001", Integer.parseInt(today));
//         if (indexInfo != null) {
//             System.out.println("查询到的指数信息: " + indexInfo);
//         }
//     }
    
//     /**
//      * 测试判断指数来源功能
//      */
//     @Test
//     @Transactional
//     public void testDetermineIndexSource() {
//         String shSource = indexInfoService.getIndexCache("000001").getSource();
//         String szSource = indexInfoService.getIndexCache("399001").getSource();
//         assertEquals("SH", shSource);
//         assertEquals("SZ", szSource);
//     }
// } 