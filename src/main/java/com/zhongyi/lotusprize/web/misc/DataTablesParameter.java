package com.zhongyi.lotusprize.web.misc;

public class DataTablesParameter {
	
	private Integer sEcho;
	private int iDisplayLength;
	private int iDisplayStart;
	private int iColumns;
	private String sColumns;
	
	public Integer getsEcho() {
		return sEcho;
	}
	public void setsEcho(Integer sEcho) {
		this.sEcho = sEcho;
	}
	public int getiDisplayLength() {
		return iDisplayLength;
	}
	public void setiDisplayLength(int iDisplayLength) {
		this.iDisplayLength = iDisplayLength;
	}
	public int getiDisplayStart() {
		return iDisplayStart;
	}
	public void setiDisplayStart(int iDisplayStart) {
		this.iDisplayStart = iDisplayStart;
	}
	public int getiColumns() {
		return iColumns;
	}
	public void setiColumns(int iColumns) {
		this.iColumns = iColumns;
	}
	public String getsColumns() {
		return sColumns;
	}
	public void setsColumns(String sColumns) {
		this.sColumns = sColumns;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataTablesParameter [sEcho=").append(sEcho)
				.append(", iDisplayLength=").append(iDisplayLength)
				.append(", iDisplayStart=").append(iDisplayStart)
				.append(", iColumns=").append(iColumns).append(", sColumns=")
				.append(sColumns).append("]");
		return builder.toString();
	}
}

