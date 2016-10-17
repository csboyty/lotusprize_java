package com.zhongyi.lotusprize.util.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class JavaSerializer<T> extends ObjectSerializer<T> {

    public JavaSerializer(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public byte[] marshall(T t) throws Exception {
        assert (t != null);
        if (!(t instanceof Serializable)) {
            throw new IllegalArgumentException(
                    " requires a Serializable payload ,but received an object of type ["
                            + t.getClass().getName() + "]");
        }
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos);
            objectOutputStream.writeObject(t);
            objectOutputStream.flush();
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception ex) {
            throw ex;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public T unmarshall(byte[] data) throws Exception {
        T result = null;

        if (data == null || data.length == 0) {
            return null;
        }

        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    byteStream);
            result = (T) objectInputStream.readObject();
        } catch (Exception ex) {
            throw ex;
        }

        return result;
    }


    @SuppressWarnings("unchecked")
    public static <T> JavaSerializer<T> of(Class<T> clazz) throws Exception {
        return JavaSerializer.class.getConstructor(new Class[]{clazz.getClass()}).newInstance(new Object[]{clazz});
    }


}
