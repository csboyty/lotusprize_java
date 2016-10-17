package com.zhongyi.lotusprize.service.role;


public class UserRole extends Role {

	// 普通用户权限
	public enum Permission {

	}

	UserRole() {
		super(8,"user");
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
