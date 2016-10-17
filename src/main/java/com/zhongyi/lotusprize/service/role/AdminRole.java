package com.zhongyi.lotusprize.service.role;

/**
 * Created by zzy on 14-2-22.
 */
public class AdminRole extends Role {

    //管理员功能权限列表
    public enum Permission {

    }


    AdminRole() {
        super(1,"admin");
    }

    @Override
    public String[] permissions() {
        Permission[] permissionArray = Permission.values();
        String[] stringArray = new String[permissionArray.length];
        for (int i = 0; i < permissionArray.length; i++) {
            stringArray[i] = permissionArray[i].name();
        }
        return stringArray;
    }


}
