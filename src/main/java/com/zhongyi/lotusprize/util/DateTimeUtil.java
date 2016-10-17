package com.zhongyi.lotusprize.util;

import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

public class DateTimeUtil {
	
	public static final long one_day_mills = 24 * 60 * 60 * 1000L;
	
	public static final int one_day_seconds = 24 * 60 * 60;
	
	private static final DateTimeFormatter _YMd = DateTimeFormat
			.forPattern("yyyy-MM-dd");

	private static final DateTimeFormatter _YMdHms = DateTimeFormat
			.forPattern("yyyy-MM-dd HH:mm:ss");

	public static String formatAsYYYYMMdd(Date date) {
		if (date != null)
			return _YMd.print(date.getTime());
		return null;
	}

	public static String formatAsYYYYMMddHHmmss(Date date) {
		if (date != null)
			return _YMdHms.print(date.getTime());
		return null;
	}

	public static Date parseAsYYYYMMdd(String _dateString) {
		if (!Strings.isNullOrEmpty(_dateString)) {
			return _YMd.parseDateTime(_dateString).toDate();
		}
		return null;
	}

	public static Date parseAsYYYYMMddHHmmss(String _dateString) {
		if (!Strings.isNullOrEmpty(_dateString)) {
			return _YMdHms.parseDateTime(_dateString).toDate();
		}
		return null;
	}

	
	public static Date currentDate(){
	    return new LocalDate().toDate();
	}
	

}
