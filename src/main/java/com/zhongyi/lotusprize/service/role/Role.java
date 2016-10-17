package com.zhongyi.lotusprize.service.role;

import java.util.Objects;

/**
 * Created by zzy on 14-2-22.
 */
public abstract class Role {

	private final String _name;
	
	private final Integer _value;

	public Role(Integer value,String name) {
		this._value = value;
		this._name = name;
	}

	public Integer value(){
		return this._value;
	}
	
	public String name() {
		return this._name;
	}

	public abstract String[] permissions();

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Role))
			return false;
		Role other = (Role) o;
		return Objects.equals(_value, other._value);
	}

	public int hashCode() {
		return Objects.hash(_value);
	}

}
