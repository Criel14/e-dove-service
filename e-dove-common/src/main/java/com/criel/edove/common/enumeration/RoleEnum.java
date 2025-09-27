package com.criel.edove.common.enumeration;

import lombok.Getter;

/**
 * 角色枚举
 */
@Getter
public enum RoleEnum {

    ADMIN("admin", "系统管理员：拥有最高权限，负责系统配置和管理"),
    USER("user", "普通用户：可使用快递驿站的基本功能"),
    STATION_ADMIN("station_admin", "驿站管理员：负责驿站的日常管理和运营"),
    STATION_STAFF("station_staff", "驿站普通工作人员：协助处理快递出入库等日常事务");

    private final String roleName;
    private final String roleDesc;

    RoleEnum(String roleName, String roleDesc) {
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }
}
