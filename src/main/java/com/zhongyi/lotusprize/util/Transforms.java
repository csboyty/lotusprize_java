package com.zhongyi.lotusprize.util;

import java.util.Collections;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

public class Transforms {
	
	private static final Splitter commaSplitter =Splitter.on(',');

    public static final IntToStringFunction intToStringFunction = new IntToStringFunction();

    public static final StringToIntFunction stringToIntFunction = new StringToIntFunction();
    
    
    public static Iterable<String> spliteByComma(String s){
        if(Strings.isNullOrEmpty(s))
            return Collections.emptyList();
    	return commaSplitter.split(s);
    }


    public static String[] intIterToStringArray(Iterable<Integer> intIter) {
        String[] stringArray = new String[Iterables.size(intIter)];
        int i = 0;
        for (Integer _int : intIter) {
            stringArray[i] = _int == null ? null :String.valueOf(_int);
            i++;
        }
        return stringArray;
    }
    
    public static Integer[] splitStringToIntArray(String s,String delimiter){
    	if(Strings.isNullOrEmpty(s)){
    		return new Integer[0];
    	}
    	String[] strArray = s.split(delimiter);
    	Integer[] intArray = new Integer[strArray.length];
    	for(int i=0;i<strArray.length;i++){
    		intArray[i] = Ints.tryParse(strArray[i]); 
    	}
    	return intArray;
    }

    
    public static Iterable<Integer> toIntegerIter(Iterable<String> stringIter){
    	return Iterables.transform(stringIter, stringToIntFunction);
    }

    private static class IntToStringFunction implements Function<Integer, String> {

        @Override
        public String apply(Integer input) {
        	if(input == null)
        		return null;
            return String.valueOf(input);
        }

    }

    private static class StringToIntFunction implements Function<String, Integer> {

        @Override
        public Integer apply(String input) {
        	if(Strings.isNullOrEmpty(input))
        		return null;
            return Ints.tryParse(input);
        }

    }
    
    
    public static Integer numericToInt(Object number){
        return number instanceof Integer ? (Integer)number : ((Long)number).intValue();
    }
    


}

