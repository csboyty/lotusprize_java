package com.zhongyi.lotusprize.mapper;

import java.util.List;

public class SqlQueryResult<T> {

	public final long totalRecords;

	public final List<T> data;

	public SqlQueryResult(long totalRecords, List<T> data) {
		super();
		this.totalRecords = totalRecords;
		this.data = data;
	}

}
