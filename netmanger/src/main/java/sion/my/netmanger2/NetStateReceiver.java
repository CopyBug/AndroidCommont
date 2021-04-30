package sion.my.netmanger2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sion.my.netmanger2.annotations.NetType;
import sion.my.netmanger2.bean.MethodManger;
import sion.my.netmanger2.enums.NetMode;
import sion.my.netmanger2.untils.NetWorkUntils;

import static android.content.ContentValues.TAG;

/**
 * 作者：梁浩文 , 日期： 2019/11/13.
 * 让代码更加美观
 */
public class NetStateReceiver extends BroadcastReceiver {
    private NetMode netMode;
    private Map<Object, List<MethodManger>> networkList;

    public NetStateReceiver() {
        this.netMode = NetMode.NONE;
        this.networkList = new HashMap<>();
    }

    public NetMode getNetMode() {
        return netMode;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.e(TAG, "异常出错");
            return;
        }

        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean isableNet = NetWorkUntils.isableNet();
            if (!isableNet) {
                post(NetMode.NONE);
            } else {
                post(NetMode.AUTO);
                netMode = NetWorkUntils.getNetWorkState();
                post(netMode);
            }

        }
    }

    public void post(NetMode mnetMode) {
        Set<Object> set = networkList.keySet();
        for (final Object getter : set) {
            //所有注解的方法
            List<MethodManger> methodMangers = networkList.get(getter);
            if (methodMangers != null) {
                //获取所有的注解的方法
                for (MethodManger methodManger : methodMangers) {
                    //可以分配到的参数
                    if (methodManger.getType().isAssignableFrom(mnetMode.getClass())) {
                        Log.i(TAG, methodManger.getType() + "");
                        invoke(methodManger, getter, mnetMode);
                    }
                }
            }
        }
    }

    private void invoke(MethodManger method, Object getter, NetMode mnetMode) {
        Method methodMethod = method.getMethod();
        try {
            methodMethod.invoke(getter, mnetMode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void registerObserver(Object register) {
        List<MethodManger> methodMangers = networkList.get(register);
        if (methodMangers == null) {
            methodMangers = findAnnotationMethod(register);
            networkList.put(register, methodMangers);
        }
    }

    /*思路  找到这个类里面的拥有注解的方法*/
    private List<MethodManger> findAnnotationMethod(Object register) {
        ArrayList<MethodManger> methodList = new ArrayList<>();
        Class<?> aClass = register.getClass();
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            NetType netType = method.getAnnotation(NetType.class);
            if (netType == null) {
                continue;
            }
            //方法返回值的校验方法必须为void
            Type returnType = method.getGenericReturnType();
            if (!"void".equals(returnType.toString())) {
                throw new RuntimeException(method.getName() + "方法必须返回void");
            }
            //参数校验 方法中只能有一个参数
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new RuntimeException(method.getName() + "方法中只能带有一个参数");
            }

            //过滤的上面,得到符合要求的方法,才开始添加到集合methodList
            MethodManger manger = new MethodManger(parameterTypes[0], netType.nettype(), method);
            methodList.add(manger);
        }
        return methodList;
    }

    public void unRegisterObserber(Object register) {
        if (!networkList.isEmpty()) {
            networkList.remove(register);
        }
        Log.i(TAG, register.getClass().getName() + "<<<<注销成功");
    }
}
