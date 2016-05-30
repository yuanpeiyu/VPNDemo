package com.example.yuanpeiyu.vpntest;

/**
 *
 */
public class SerializableTask {
    private static final String TAG = "SerializableTask";
    public long id;
    public long ctime;
    public int status;
    public String clzName;
    public byte[] binary;

    public static <T extends SerializableBean> SerializableTask fromObject(T obj, int status) {
        SerializableTask task = new SerializableTask();
        task.status = status;
        task.clzName = obj.getClass().getName();
        task.binary = obj.toBinary();
        return task;
    }

    public <T extends SerializableBean> T toObject(Class<T> clz) {
        return SerializableBeanUtils.convertGsonStringBinaryToObject(binary, clz);
    }

    public static <T extends SerializableBean> SerializableTask fromObject(T obj) {
        return fromObject(obj, 0);
    }
}
