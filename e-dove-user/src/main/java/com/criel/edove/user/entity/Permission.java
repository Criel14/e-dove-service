package com.criel.edove.user.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 定义系统中具体的操作权限点
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Getter
@Setter
@TableName("permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限唯一标识ID
     */
    @TableId(value = "permission_id", type = IdType.AUTO)
    private Long permissionId;

    /**
     * 权限代码：如user:create, parcel:query
     */
    @TableField("permission_code")
    private String permissionCode;

    /**
     * 权限名称
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 权限描述
     */
    @TableField("permission_desc")
    private String permissionDesc;

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
