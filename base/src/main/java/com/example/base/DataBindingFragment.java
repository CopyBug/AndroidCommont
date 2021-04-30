package com.example.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.base.data.DataBindingConfig;

public abstract class DataBindingFragment extends Fragment {

    protected FragmentActivity mActivity;
    private ViewModelProvider mFragmentProvider;
    private ViewModelProvider mActivityProvider;
    private ViewModelProvider mApplicationProvider;
    private ViewDataBinding mBinding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    protected abstract void initViewModel();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewModel();
    }

    protected abstract DataBindingConfig getDataBindingConfig();

    /**
     * TODO tip: 警惕使用。非必要情况下，尽可能不在子类中拿到 binding 实例乃至获取 view 实例。使用即埋下隐患。
     * 目前的方案是在 debug 模式下，对获取实例的情况给予提示。
     * <p>
     * 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350 和 https://xiaozhuanlan.com/topic/2356748910
     *
     * @return binding
     */
    protected ViewDataBinding getBinding() {
        return mBinding;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initParam();
        DataBindingConfig dataBindingConfig = getDataBindingConfig();

        //TODO tip: DataBinding 严格模式：
        // 将 DataBinding 实例限制于 base 页面中，默认不向子类暴露，
        // 通过这样的方式，来彻底解决 视图调用的一致性问题，
        // 如此，视图刷新的安全性将和基于函数式编程的 Jetpack Compose 持平。

        // 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350 和 https://xiaozhuanlan.com/topic/2356748910

        ViewDataBinding binding = DataBindingUtil.inflate(inflater, dataBindingConfig.getLayout(), container, false);
        binding.setLifecycleOwner(this);
        binding.setVariable(dataBindingConfig.getVmVariableId(), dataBindingConfig.getStateViewModel());
        SparseArray bindingParams = dataBindingConfig.getBindingParams();
        for (int i = 0, length = bindingParams.size(); i < length; i++) {
            binding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i));
        }
        mBinding = binding;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPrepareView = true;
    }

    public boolean isDebug() {
        return mActivity.getApplicationContext().getApplicationInfo() != null &&
                (mActivity.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
    public boolean isBackPressed() {
        return false;
    }
    protected void showLongToast(String text) {
        Toast.makeText(mActivity.getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String text) {
        Toast.makeText(mActivity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(int stringRes) {
        showLongToast(mActivity.getApplicationContext().getString(stringRes));
    }

    protected void showShortToast(int stringRes) {
        showShortToast(mActivity.getApplicationContext().getString(stringRes));
    }

    protected <T extends ViewModel> T getFragmentScopeViewModel(@NonNull Class<T> modelClass) {
        if (mFragmentProvider == null) {
            mFragmentProvider = new ViewModelProvider(this);
        }
        return mFragmentProvider.get(modelClass);
    }

    protected <T extends ViewModel> T getActivityScopeViewModel(@NonNull Class<T> modelClass) {
        if (mActivityProvider == null) {
            mActivityProvider = new ViewModelProvider(mActivity);
        }
        return mActivityProvider.get(modelClass);
    }

    protected <T extends ViewModel> T getApplicationScopeViewModel(@NonNull Class<T> modelClass) {
        if (mApplicationProvider == null) {
            mApplicationProvider = new ViewModelProvider(
                    (BaseApplication) mActivity.getApplicationContext(), getApplicationFactory(mActivity));
        }
        return mApplicationProvider.get(modelClass);
    }

    private ViewModelProvider.Factory getApplicationFactory(Activity activity) {
        checkActivity(this);
        Application application = checkApplication(activity);
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application);
    }

    private Application checkApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to "
                    + "Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }

    private void checkActivity(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Can't create ViewModelProvider for detached fragment");
        }
    }


    protected abstract void initView();

    protected abstract void initData();

    public void initParam() {
    }

    ;


    //懒加载
    private Boolean isInitData = false;               //标志位，判断数据是否初始化完成
    private Boolean isVisibleToUser = false;          //标志位，判断fragment是否可见
    private Boolean isPrepareView = false;            //标志位，判断view是否已经加载完成，避免空指针的操作
    private Boolean isReplaceFragment = false;          //是否是 replace Fragment 的形式


    //懒加载的方法
    public void lazyInitData() {
        if (!setFragmentTarget()) {
            if (isReplaceFragment) {
                if (!isInitData && isVisibleToUser && isPrepareView) {//如果数据还没有被加载过，并且fragment已经可见，view已经加载完成
                    initData();//加载数据
                    initView();
                    isInitData = true;//是否已经加载数据标志重新赋值为true
                }
            } else {
                if (!isInitData && isPrepareView) {//如果数据还没有被加载过，并且fragment已经可见，view已经加载完成
                    initData();//加载数据
                    initView();
                    isInitData = true;//是否已经加载数据标志重新赋值为true
                }
            }
        } else {
            if (isReplaceFragment) {
                if (!isInitData && isVisibleToUser && isPrepareView) {//如果数据还没有被加载过，并且fragment已经可见，view已经加载完成
                    initData();//加载数据
                    initView();
                    isInitData = true;//是否已经加载数据标志重新赋值为true
                } else if (!isInitData && getParentFragment() == null && isPrepareView) {
                    initData();
                    initView();
                    isInitData = true;
                }
            } else {
                if (!isInitData && isPrepareView) {//如果数据还没有被加载过，并且fragment已经可见，view已经加载完成
                    initData();//加载数据
                    initView();
                    isInitData = true;//是否已经加载数据标志重新赋值为true
                } else if (!isInitData && getParentFragment() == null && isPrepareView) {
                    initData();
                    initView();
                    isInitData = true;
                }
            }

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("initView", "setUserVisibleHint: " + isVisibleToUser);
        isReplaceFragment = true;
        this.isVisibleToUser = isVisibleToUser;
        lazyInitData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            lazyInitData();
        }
    }

    /**
     * fragment生命周期onViewCreated之后的方法，在这里调用一次懒加载，避免第一次不加载数据
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lazyInitData();
    }

    /**
     * 设置Fragment target，由子类实现
     */
    public boolean setFragmentTarget() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDataBindingConfig() != null && getDataBindingConfig().getStateViewModel() != null) {
            getDataBindingConfig().getStateViewModel().onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getDataBindingConfig() != null && getDataBindingConfig().getStateViewModel() != null) {
            getDataBindingConfig().getStateViewModel().onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getDataBindingConfig() != null && getDataBindingConfig().getStateViewModel() != null) {
            getDataBindingConfig().getStateViewModel().onPause();
        }
    }
}