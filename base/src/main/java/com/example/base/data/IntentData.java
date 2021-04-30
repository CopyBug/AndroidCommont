package com.example.base.data;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class IntentData extends Intent {


    public IntentData(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public IntentData putExtra(String key, String value) {
        putExtra(key, value);
        return this;
    }

    public IntentData putExtra(String key, int value) {
        putExtra(key, value);
        return this;
    }

    public IntentData putExtra(String key, float value) {
        putExtra(key, value);
        return this;
    }

    public IntentData putExtra(String key, double value) {
        putExtra(key, value);
        return this;
    }

    public IntentData putExtra(String key, boolean value) {
        putExtra(key, value);
        return this;
    }

    public IntentData putExtra(String key, Object v) {
        putExtra(key, (Serializable) v);
        return this;
    }

    public IntentData putExtra(String key, int[] value) {
        putExtra(key, value);
        return this;
    }

    public IntentData putExtra(String key, float[] value) {
        putExtra(key, value);
        return this;
    }

    public IntentData putExtra(String key, boolean[] value) {
        putExtra(key, value);
        return this;
    }

    public IntentData putExtra(String key, ArrayList<? extends Parcelable> value){
        putParcelableArrayListExtra(key,value);
        return this;
    }
}
