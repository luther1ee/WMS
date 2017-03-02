package com.ken.wms.security.Service;

import com.ken.wms.dao.RolePermissionMapper;
import com.ken.wms.domain.RolePermissionDO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 获取 URL 权限信息工厂类
 * @author ken
 * @since 2017/2/26.
 */
public class FilterChainDefinitionMapBuilder {
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    private StringBuilder builder = new StringBuilder();

    /**
     * 获取授权信息
     * @return 返回授权信息列表
     */
    public LinkedHashMap<String, String> builderFilterChainDefinitionMap(){
        LinkedHashMap<String, String> permissionMap = new LinkedHashMap<>();

        // 固定的权限配置
        permissionMap.put("/css/**", "anon");
        permissionMap.put("/js/**", "anon");
        permissionMap.put("/fonts/**", "anon");
        permissionMap.put("/media/**", "anon");
        permissionMap.put("/pagecomponent/**", "anon");
        permissionMap.put("/login", "anon");
        permissionMap.put("/account/login", "anon");
        permissionMap.put("/account/checkCode/**", "anon");
//        permissionMap.put("/account/logout", "logout");

        // 可变化的权限配置
        LinkedHashMap<String, String> permissions = getPermissionDataFromDB();
        if (permissions != null){
            permissionMap.putAll(permissions);
        }

        // 其他的权限配置
        permissionMap.put("/**", "authc");

        return permissionMap;
    }

    /**
     * 获取配置在数据库中的 URL 权限信息
     * @return 返回所有保存在数据库中的 URL 保存信息
     */
    private LinkedHashMap<String, String> getPermissionDataFromDB(){
        LinkedHashMap<String, String> permissionData = null;

        List<RolePermissionDO> rolePermissionDOS = rolePermissionMapper.selectAll();
        if (rolePermissionDOS != null){
            permissionData = new LinkedHashMap<>(rolePermissionDOS.size());
            String url;
            String permission;
            for (RolePermissionDO rolePermissionDO : rolePermissionDOS){
                url = rolePermissionDO.getUrl();
                permission = permissionStringBuilder(rolePermissionDO.getRole());
//                System.out.println(url + ":" + permission);
                permissionData.put(url, permission);
            }
        }

        return permissionData;
    }

    /**
     * 构造角色权限
     * @param role 角色
     * @return 返回 roles[role name] 格式的字符串
     */
    private String permissionStringBuilder(String role){
        builder.delete(0, builder.length());
        return builder.append("authc,roles[").append(role).append("]").toString();
    }
}
