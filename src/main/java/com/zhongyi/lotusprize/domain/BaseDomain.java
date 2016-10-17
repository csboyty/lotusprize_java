package com.zhongyi.lotusprize.domain;

import java.io.Serializable;

/**
 * Created by zzy on 14-2-21.
 */
@SuppressWarnings("serial")
public abstract class BaseDomain implements Serializable {

	public abstract boolean equals(Object o);

	public abstract int hashCode();

}
