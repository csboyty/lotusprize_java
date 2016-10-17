package com.zhongyi.lotusprize.service.role;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Created by zzy on 14-2-21.
 */
public class Roles {


	public static final AdminRole adminRole = new AdminRole();
	public static final TopicManagerRole topicManagerRole = new TopicManagerRole();
	public static final ExpertRole expertRole = new ExpertRole();
	public static final UserRole userRole = new UserRole();


	public static Role highestRole(int value) {
		if ((value & adminRole.value()) != 0)
			return adminRole;
		if ((value & expertRole.value()) != 0) {
			return expertRole;
		}
		if ((value & topicManagerRole.value()) != 0) {
			return topicManagerRole;
		}
		if (((value & userRole.value())) != 0) {
			return userRole;
		}
		return null;
	}
	
	public static String roleNames(int value){
		 Collection<Role> _roles = roles(value);
		 StringBuilder roleNames = new StringBuilder();
		 Iterator<Role> it = _roles.iterator();
		 if(it.hasNext()){
			 Role role = it.next();
			 roleNames.append(role.name());
		 }
		 while(it.hasNext()){
			 Role role = it.next();
			 roleNames.append(",");
			 roleNames.append(role.name());
		 }
		 return roleNames.toString();
		 
	}

	public static Collection<Role> roles(int value) {
		Set<Role> roles = Sets.newHashSet();
		if ((value & adminRole.value()) != 0)
			roles.add(adminRole);
		if ((value & expertRole.value()) != 0) {
			roles.add(expertRole);
		}
		if ((value & topicManagerRole.value()) != 0) {
			roles.add(topicManagerRole);
		}
		if (((value & userRole.value())) != 0) {
			roles.add(userRole);
		}
		return roles;
	}

	public static Integer roleNameByValue(String roleValue){
		switch(roleValue){
			case "admin":
				return adminRole.value();
			case "topicManager":
				return topicManagerRole.value();
			case "expert":
				return expertRole.value();
			case "user":
				return userRole.value();
		}
		return null;
	}
	
	public static boolean isRole(Integer roleValue,Role role){
		if(roleValue!=null){
			return (role.value() & roleValue) !=0;
		}
		return false;
	}
}
