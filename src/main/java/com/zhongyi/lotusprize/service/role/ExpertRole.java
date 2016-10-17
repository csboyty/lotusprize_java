package com.zhongyi.lotusprize.service.role;


public class ExpertRole extends Role {

	// 专业评委的权限
	public enum Permission {

	}

	ExpertRole() {
		super(2,"expert");
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
