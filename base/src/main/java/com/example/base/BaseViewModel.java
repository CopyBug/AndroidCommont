package com.example.base;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.base.event.SingleLiveEvent;
import com.example.base.viewModelAction.FrameWorkModel;
import com.example.base.viewModelAction.UIChangeObservable;
import com.example.base.viewModelAction.ViewModeActivityManager;
import com.example.base.viewModelAction.ViewModelLife;
import com.example.base.viewModelAction.ViewModelRx;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * #FrameWorkModel#这里主要处理与activity之间的交互
 */
public abstract class BaseViewModel extends ViewModel implements ViewModelLife, ViewModelRx {

    //管理activity事件传递
    private FrameWorkModel frameWorkModel;
    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private CompositeDisposable mCompositeDisposable;
    //管理Ui事件传递
    private UIChangeObservable ui;

    public FrameWorkModel getContext() {
        if (frameWorkModel == null) {
            frameWorkModel = new FrameWorkModel();
        }
        return frameWorkModel;
    }

    public UIChangeObservable getUi(){
        if (ui == null) {
            ui = new UIChangeObservable();
        }
        return ui;
    }

    /**
     * 添加事件
     */
    @Override
    public void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    /**
     * 销毁事件
     */
    @Override
    public void removeEvent() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    /**
     * 请大家在这里注册事件
     */
    public abstract void registerRxBus();


}
