package com.example.androidcommont.frameWork.common;

import android.widget.Toast;

import androidx.databinding.ViewDataBinding;

import com.example.base.BaseDataBindingActivity;
import com.example.widget.action.HandlerAction;
import com.example.widget.action.TitleBarAction;
import com.example.widget.tools.Tips;
import com.example.widget.view.dialog.BaseDialog;
import com.example.widget.view.dialog.WaitDialog;

import java.util.Map;

public abstract class MyActivity<B extends ViewDataBinding> extends BaseDataBindingActivity<B> implements HandlerAction , TitleBarAction {
    private BaseDialog waitDialog;

    @Override
    protected void initViewModel() {

    }

    @Override
    public void showDialog(String info) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (waitDialog != null && waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }
                waitDialog = new WaitDialog.Builder(getBaseContext()).setMessage(info).show();
            }
        });
    }

    @Override
    public void hideDialog() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (waitDialog != null) {
                    waitDialog.hide();
                }
            }
        });
    }

    @Override
    public void shortToast(String info) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                Tips.show(getBaseContext(), info);
            }
        });

    }

    @Override
    public void longToast(String info) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                Tips.show(getBaseContext(), info, Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void startContainerActivity(Map<String, Object> activityEvent) {

    }



}
