package com.example.base.viewModelAction;

import sion.my.netmanger2.enums.NetMode;

public interface ViewModelLife {
    /**
     * 初始化
     */
    void initViewModel() ;

    /**
     * 重启
     */
    void onResume() ;

    /**
     * 销毁
     */
    void onDestroy() ;

    /**
     * 暂停
     */
    void onPause();

    /**
     * 网络状态改变
     */
    void netWorkChange(NetMode netWorkEvent);

}
