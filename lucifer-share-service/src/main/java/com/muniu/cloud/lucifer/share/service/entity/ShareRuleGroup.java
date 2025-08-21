package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.muniu.cloud.lucifer.share.service.vo.SaveRuleGroupParams;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
@Data
@ToString
@TableName("share_rule_group")
public class ShareRuleGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 删除标记
     */
    private Boolean deleted;

    /**
     * 规则组描述
     */
    private String description;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 用户ID
     */
    private String userId;

    private String name;




    public ShareRuleGroup() {
    }

    public ShareRuleGroup(Long id, Long createTime, Boolean deleted, String description, Long updateTime, String userId, String name) {
        this.id = id;
        this.createTime = createTime;
        this.deleted = deleted;
        this.description = description;
        this.updateTime = updateTime;
        this.userId = userId;
        this.name = name;
    }

    //新增构造函数
    public ShareRuleGroup(Long createTime, String userId, SaveRuleGroupParams saveRuleGroupParams) {
        this.createTime = createTime;
        this.deleted = false;
        this.description = StringUtils.isBlank(saveRuleGroupParams.description()) ? "" : saveRuleGroupParams.description();
        this.updateTime = createTime;
        this.userId = userId;
        this.name = saveRuleGroupParams.name();
    }

}
