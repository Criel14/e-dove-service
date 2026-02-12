package com.criel.edove.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 获取用户角色信息响应数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesVO implements Serializable {

    /**
     * 用户的角色名称
     */
    private List<String> roleNames;

}
