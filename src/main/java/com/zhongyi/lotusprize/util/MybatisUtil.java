package com.zhongyi.lotusprize.util;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;


public class MybatisUtil {

    public static final String op_gt = ">";

    public static final String op_gte = ">=";

    public static final String op_lt = "<";

    public static final String op_lte = "<=";

    public static final String orderby_asc = "ASC";

    public static final String orderby_desc = "DESC";

    public static String tailWildcard(String s){
    	return Strings.isNullOrEmpty(s) ? s :s+"%";
    }

    public static boolean isNotNullOrEmpty(Object o) {
        return !isNullOrEmpty(o);
    }


    @SuppressWarnings("rawtypes")
    public static boolean isNullOrEmpty(Object o) {
        if (o == null)
            return true;

        if (o instanceof String) {
            if (((String) o).length() == 0) {
                return true;
            }
        } else if (o instanceof Collection) {
            if (((Collection) o).isEmpty()) {
                return true;
            }
        } else if (o instanceof Map) {
            if (((Map) o).isEmpty()) {
                return true;
            }
        } else if (o.getClass().isArray()) {
            if (((Object[]) o).length == 0) {
                return true;
            }
        } else if(o instanceof Iterable){
            return Iterables.isEmpty((Iterable)o);
        }
        return false;
    }

    
    public static boolean isZero(Number number) {
    	
        return number != null ? 0 == number.intValue():true;
    }

    public static boolean isNotZero(Number number) {
        return !isZero(number);
    }

}

