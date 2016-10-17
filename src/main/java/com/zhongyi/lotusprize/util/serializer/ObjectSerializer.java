package com.zhongyi.lotusprize.util.serializer;


public abstract class ObjectSerializer<T> {

    protected final Class<T> entityClass;

    public ObjectSerializer(Class<T> clazz) {
        entityClass = clazz;
//		System.out.println(((ParameterizedType) getClass()
//				.getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public boolean canSupport(Object object) {
        return entityClass.isInstance(object);
    }

    public abstract byte[] marshall(T t) throws Exception;

    public abstract T unmarshall(byte[] data) throws Exception;

}
