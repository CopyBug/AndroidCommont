package com.example.base.viewModelAction;

import androidx.lifecycle.MutableLiveData;

import com.example.base.event.SingleLiveEvent;

public class UIChangeObservable {
    private SingleLiveEvent<Boolean> refreshEvent;
    private SingleLiveEvent<Boolean> loadEvent;
    private SingleLiveEvent<String> dialogTipEvent;
    private SingleLiveEvent<Boolean> dialogShowEvent;
    private SingleLiveEvent<String> shortToastEvent;
    private SingleLiveEvent<String> longToastEvent;

    private <T> SingleLiveEvent<T> createLiveData(SingleLiveEvent<T> liveData) {
        if (liveData == null) {
            liveData = new SingleLiveEvent<>();
        }
        return liveData;
    }

    public SingleLiveEvent<Boolean> getRefreshEvent() {
        return refreshEvent = createLiveData(refreshEvent);
    }

    public SingleLiveEvent<Boolean> getLoadEvent() {
        return loadEvent = createLiveData(loadEvent);
    }

    public SingleLiveEvent<Boolean> getDialogShowEvent() {
        return dialogShowEvent = createLiveData(dialogShowEvent);
    }

    public SingleLiveEvent<String> getDialogTipEvent() {
        return dialogTipEvent = createLiveData(dialogTipEvent);
    }

    public SingleLiveEvent<String> getShortToastEvent() {
        return shortToastEvent = createLiveData(shortToastEvent);
    }

    public SingleLiveEvent<String> getLongToastEvent() {
        return longToastEvent = createLiveData(longToastEvent);
    }

    public void showDialog() {
        showDialog("请稍等");
    }

    public void showDialog(String tip) {
        setDialogTip(tip);
        getDialogShowEvent().postValue(true);
    }

    private void setDialogTip(String tip) {
        if (tip != null) {
            getDialogTipEvent().setValue(tip);
        }
    }

    public void hideDialog() {
        getDialogShowEvent().postValue(false);
    }

    public void showLongToast(String info) {
        if (info != null) {
            getLongToastEvent().postValue(info);
        }
    }

    public void showShortToast(String info) {
        if (info != null) {
            getShortToastEvent().postValue(info);
        }
    }

}