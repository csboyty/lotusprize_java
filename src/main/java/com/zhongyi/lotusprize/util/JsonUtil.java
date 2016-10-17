package com.zhongyi.lotusprize.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhongyi.lotusprize.exception.JsonMarshallError;
import com.zhongyi.lotusprize.exception.JsonUnmarshallError;
import com.zhongyi.lotusprize.service.ApplicationProperty;


public abstract class JsonUtil {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    
    @SuppressWarnings("unchecked")
	public static Map<String,Object> fromJson(String s){
    	return (Map<String, Object>)fromJson(Map.class,s);
    }

    public static <T> T fromJson(Class<T> valueType, Reader reader) {
        try {
            return jsonMapper.readValue(reader, valueType);
        } catch (Exception e) {
            throw new JsonUnmarshallError(e);
        }
    }

    public static <T> T fromJson(Class<T> valueType, InputStream inputStream) {
        return fromJson(valueType, new InputStreamReader(inputStream, ApplicationProperty.instance().charset()));
    }


    public static <T> T fromJson(Class<T> valueType, byte[] bytes) {
        return fromJson(valueType, new InputStreamReader(new ByteArrayInputStream(bytes), ApplicationProperty.instance().charset()));
    }

    public static <T> T fromJson(Class<T> valueType, String s) {
        return fromJson(valueType, new StringReader(s));
    }

    public static <T> T fromJson(TypeReference<T> typeReference, Reader reader) {
        try {
            return jsonMapper.readValue(reader, typeReference);
        } catch (Exception e) {
            throw new JsonUnmarshallError(e);
        }
    }


    public static <T> T fromJson(TypeReference<T> typeReference, InputStream inputStream) {
        return fromJson(typeReference, new InputStreamReader(inputStream, ApplicationProperty.instance().charset()));
    }

    public static <T> T fromJson(TypeReference<T> typeReference, String s) {
        return fromJson(typeReference, new StringReader(s));
    }

    public static <T> T fromJson(TypeReference<T> typeReference, byte[] bytes) {
        return fromJson(typeReference, new InputStreamReader(new ByteArrayInputStream(bytes), ApplicationProperty.instance().charset()));
    }


    public static void toJson(Object object, Writer out) {
        try {
            JsonGenerator jsonGenerator = jsonMapper.getFactory().createGenerator(out);
            jsonMapper.writeValue(jsonGenerator, object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void toJson(Object object, OutputStream out) throws Exception {
        toJson(object, new OutputStreamWriter(out, ApplicationProperty.instance().charset()));
    }


    public static String toJsonString(Object object) {
        StringWriter out = new StringWriter();
        toJson(object, out);
        return out.toString();
    }

    public static byte[] toJsonBytes(Object object) {
        try {
            return jsonMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new JsonMarshallError(e);
        }

    }


    public static void main(String[] args) {
        Foo foo1 = new Foo();
        foo1.setA(1);
        foo1.setB("a");

        Foo foo2 = new Foo();
        foo2.setA(2);
        foo2.setB("b");

        Map<String, Foo> fooMap = Maps.newHashMap();
        fooMap.put("foo1", foo1);
        fooMap.put("foo2", foo2);

        String json = toJsonString(fooMap);
        System.out.println(json);
        System.out.println(fromJson(new TypeReference<Map<String, Foo>>() {
        }, json));

        System.out.println(JsonUtil.toJsonString(Lists.newArrayList("a", "b")));
        System.out.println(JsonUtil.fromJson(Iterable.class, "[\"a\",\"b\"]"));
        System.out.println(JsonUtil.fromJson(Map.class, "{\"a\":1,\"b\":2}"));
    }

    public static class Foo {

        private int a;

        private String b;

        public Foo() {
            super();
        }

        public Foo(int a, String b) {
            super();
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Foo [a=");
            builder.append(a);
            builder.append(", b=");
            builder.append(b);
            builder.append("]");
            return builder.toString();
        }


    }


}

