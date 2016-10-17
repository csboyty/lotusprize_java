package com.zhongyi.lotusprize.service.role;


/**
 * Created by zzy on 14-2-22.
 */
public class TopicManagerRole extends Role {
	

	// 题目管理者的权限
	public enum Permission {

	}

	TopicManagerRole() {
		super(4,"topicManager");
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
