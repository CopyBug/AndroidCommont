package com.example.base.data;

import android.util.SparseArray;

import com.example.base.BaseViewModel;

public class DataBindingConfig {
    private final int layout;

    private final int vmVariableId;

    private final BaseViewModel stateViewModel;

    private SparseArray bindingParams = new SparseArray();

    public DataBindingConfig(int layout, int vmVariableId, BaseViewModel stateViewModel) {
        this.layout = layout;
        this.vmVariableId = vmVariableId;
        this.stateViewModel = stateViewModel;
    }
    public DataBindingConfig(int layout) {
        this.layout = layout;
        this.vmVariableId = 0;
        this.stateViewModel = null;
    }

    public int getLayout() {
        return layout;
    }

    public int getVmVariableId() {
        return vmVariableId;
    }

    public BaseViewModel getStateViewModel() {
        return stateViewModel;
    }

    public SparseArray getBindingParams() {
        return bindingParams;
    }

    public DataBindingConfig addBindingParam(int variableId, Object object) {
        if (bindingParams.get(variableId) == null) {
            bindingParams.put(variableId, object);
        }
        return this;
    }
}
