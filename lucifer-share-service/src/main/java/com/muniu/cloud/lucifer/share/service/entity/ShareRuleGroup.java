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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * <p>
 * 
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
@Entity
@Table(name = "share_rule_group")
@Data
@ToString
@TableName("share_rule_group")
public class ShareRuleGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private Long createTime;

    /**
     * 删除标记
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    /**
     * 规则组描述
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    private Long updateTime;

    /**
     * 用户ID
     */
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;




    public ShareRuleGroup() {
    }

}
