package com.example.base.viewModelAction;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public interface ViewModelRx {
    /**
     * 添加事件
     */
    void addSubscribe(Disposable disposable);

    /**
     * 销毁事件
     */
    void removeEvent();

    /**
     * 请大家在这里注册事件
     */
   void registerRxBus();
}
