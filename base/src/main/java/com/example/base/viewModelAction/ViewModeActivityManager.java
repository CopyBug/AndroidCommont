package com.example.base.viewModelAction;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

public interface ViewModeActivityManager {
    void finish() ;

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    default void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    void startActivity(Class<?> clz, Bundle bundle);

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    void startContainerActivity(String canonicalName);

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    void startContainerActivity(String canonicalName, Bundle bundle);
}
