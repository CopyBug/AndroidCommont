package com.example.widget.action;

import android.view.View;

import androidx.annotation.IdRes;


import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;


public interface ClickAction extends View.OnClickListener {

    <V extends View> V findViewById(@IdRes int id);

    @Override
    default void onClick(View v) {
        // 默认不实现，让子类实现
    }

    default void setOnClickListener(@IdRes int... ids) {
        for (int id : ids) {
            RxView.clicks(findViewById(id))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(o -> onClick(findViewById(id)));
        }
    }

    default void setOnClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }
}