package sion.my.netmanger2.untils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import sion.my.netmanger2.NetworkManager;
import sion.my.netmanger2.enums.NetMode;

/**
 * 作者：梁浩文 , 日期： 2019/11/13.
 * 让代码更加美观
 */
public class NetWorkUntils {

    /**
     * @return 返回网络状态参考NetMode
     */
    @SuppressWarnings("all")
//添加权限 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    public static NetMode getNetWorkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) NetworkManager.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetMode.WIFT;
                case ConnectivityManager.TYPE_MOBILE:
                    //流量
                    return NetMode.CMNET;

            }
            // CONNECTED数据网络
        } else {
            //没有网络
            return NetMode.NONE;
        }
        return NetMode.AUTO;
    }

    public static boolean isableNet() {
        NetMode netWorkState = getNetWorkState();
        if (netWorkState == NetMode.NONE) {
            return false;
        }
        return true;
    }
}
