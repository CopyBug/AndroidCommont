package sion.my.netmanger2.bean;

import java.lang.reflect.Method;

import sion.my.netmanger2.enums.NetMode;

/**
 * 作者：梁浩文 , 日期： 2019/11/13.
 * 让代码更加美观
 * 保证符合要求的网络监听注解方法，封装类
 */
public class MethodManger {
    //    参考参数类型    NetMode
    private Class<?> type;
    //网络类型
    private NetMode netMode;
    //需要执行的方法,network()
    private Method method;

    public MethodManger(Class<?> type, NetMode netMode, Method method) {
        this.type = type;
        this.netMode = netMode;
        this.method = method;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public NetMode getNetMode() {
        return netMode;
    }

    public void setNetMode(NetMode netMode) {
        this.netMode = netMode;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
