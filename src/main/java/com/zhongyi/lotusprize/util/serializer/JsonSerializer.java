package com.zhongyi.lotusprize.util.serializer;

import com.zhongyi.lotusprize.util.JsonUtil;

public class JsonSerializer<T> extends ObjectSerializer<T> {

    public JsonSerializer(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public byte[] marshall(T t) throws Exception {
        return JsonUtil.toJsonBytes(t);
    }

    @Override
    public T unmarshall(byte[] data) {
        if (data == null || data.length == 0)
            return null;
        return JsonUtil.fromJson(entityClass, data);
    }

    @SuppressWarnings("unchecked")
    public static <T> JsonSerializer<T> of(Class<T> clazz) throws Exception {
        return JsonSerializer.class.getConstructor(new Class[]{clazz.getClass()}).newInstance(new Object[]{clazz});
    }

}
