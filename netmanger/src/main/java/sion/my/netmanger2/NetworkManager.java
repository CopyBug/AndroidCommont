package sion.my.netmanger2;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * 作者：梁浩文 , 日期： 2019/11/13.
 * 让代码更加美观
 */
public class NetworkManager {
    private String TAG="NetworkManager";
    private static NetworkManager instance;
    private static Application application;
    private NetStateReceiver stateReceiver;


    public NetworkManager() {
        this.stateReceiver = new NetStateReceiver();
    }

    public static NetworkManager getDefault() {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new NetworkManager();
                }
            }
        }
        return instance;
    }

    public static Application getApplication() {
        if (application == null) {
            throw new RuntimeException("NetManger.init() error");
        }
        return application;
    }

    public void init(Application myapplication) {
        application = myapplication;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(stateReceiver, filter);
    }

    public void registerObserver(Object register) {
        stateReceiver.registerObserver(register);
        Log.i(TAG, "registerObserver: ");
    }

    public void unRegisterObserver(Object register) {
        stateReceiver.unRegisterObserber(register);
    }

}
