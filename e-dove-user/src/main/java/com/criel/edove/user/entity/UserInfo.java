package com.criel.edove.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 存储系统所有用户的基本信息，包括用户、驿站工作人员、系统管理员等
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Getter
@Setter
@ToString
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识ID
     */
    @TableId("user_id")
    private Long userId;

    /**
     * 所属门店ID（工作人员才有值）
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 手机号码
     */
    @TableField("phone")
    private String phone;

    /**
     * 电子邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 头像图片URL地址
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
