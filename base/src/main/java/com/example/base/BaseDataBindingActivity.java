package com.example.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.base.data.DataBindingConfig;
import com.example.base.data.IntentData;
import com.example.base.data.ParameterField;

import java.util.Map;
import java.util.Random;

import sion.my.netmanger2.NetworkManager;
import sion.my.netmanger2.annotations.NetType;
import sion.my.netmanger2.enums.NetMode;

public abstract class BaseDataBindingActivity<B extends ViewDataBinding> extends FragmentActivity {

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    protected abstract DataBindingConfig getDataBindingConfig();

    private ViewModelProvider mActivityProvider;
    private B binding;
    private ViewModelProvider mApplicationProvider;

    protected abstract void initViewModel();

    public B getBinding() {
        return binding;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParam(getIntent());
        initViewModel();
        DataBindingConfig dataBindingConfig = getDataBindingConfig();
        binding = DataBindingUtil.setContentView(this, dataBindingConfig.getLayout());
        binding.setLifecycleOwner(this);
        if (dataBindingConfig.getVmVariableId() != 0) {
            binding.setVariable(dataBindingConfig.getVmVariableId(), dataBindingConfig.getStateViewModel());
            initViewObservable();
        }
        SparseArray bindingParams = dataBindingConfig.getBindingParams();
        for (int i = 0, length = bindingParams.size(); i < length; i++) {
            binding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i));
        }

    }

    public void initViewObservable() {
        BaseViewModel viewModel = getDataBindingConfig().getStateViewModel();
        viewModel.getUi().getDialogShowEvent().observe(this, show -> {
            if (show) {
                String value = viewModel.getUi().getDialogTipEvent().getValue();
                showDialog(value);
            } else {
                hideDialog();
            }
        });
        viewModel.getUi().getLongToastEvent().observe(this, info -> {
            longToast(info);
        });

        viewModel.getUi().getShortToastEvent().observe(this, info -> {
            shortToast(info);
        });

        viewModel.getContext().getStartContainerActivityEvent().observe(this, map -> {
            startContainerActivity(map);
        });

        viewModel.getContext().getStartActivityEvent().observe(this, map -> {
            startActivityEvent(map);
        });

        viewModel.getContext().getFinishEvent().observe(this, aVoid -> {
            finish();
        });
    }

    public abstract void showDialog(String info);

    public abstract void hideDialog();

    public abstract void shortToast(String info);

    public abstract void longToast(String info);

    protected <T extends ViewModel> T getActivityScopeViewModel(@NonNull Class<T> modelClass) {
        if (mActivityProvider == null) {
            mActivityProvider = new ViewModelProvider(this);
        }
        return mActivityProvider.get(modelClass);
    }


    protected <T extends ViewModel> T getActivityScopeViewModel(@NonNull Class<T> modelClass, BaseDataBindingActivity activity) {
        return new ViewModelProvider(activity).get(modelClass);
    }

    protected <T extends ViewModel> T getApplicationScopeViewModel(@NonNull Class<T> modelClass) {
        if (mApplicationProvider == null) {
            mApplicationProvider = new ViewModelProvider((BaseApplication) this.getApplicationContext(),
                    getAppFactory(this));
        }
        return mApplicationProvider.get(modelClass);
    }

    private ViewModelProvider.Factory getAppFactory(Activity activity) {
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


    public void initParam(Intent intent) {

    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkManager.getDefault().registerObserver(this);
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
        NetworkManager.getDefault().unRegisterObserver(this);
        if (getDataBindingConfig() != null && getDataBindingConfig().getStateViewModel() != null) {
            getDataBindingConfig().getStateViewModel().onPause();
        }
    }

    /**
     * 网络监听
     *
     * @param netMode
     */
    @NetType(nettype = NetMode.AUTO)
    public void changeNetWork(NetMode netMode) {
        if (getDataBindingConfig() != null && getDataBindingConfig().getStateViewModel() != null) {
            BaseViewModel viewModel = getDataBindingConfig().getStateViewModel();
            viewModel.netWorkChange(netMode);
            switch (netMode) {
                case NONE:
                    //无网络
                    break;
                case AUTO:
                    //有网络
                    break;
                case CMNET:
                    //笔记本电脑 PDA网络
                    break;
                case WIFT:
                    //WIFI
                    break;
                default:
                    break;
            }
        }

    }

    protected void startActivityEvent(Map<String, Object> params) {
        if (params != null) {
            Class<?> clz = (Class<?>) params.get(ParameterField.CLASS);
            Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
            Boolean finish = (Boolean) params.get(ParameterField.FINISH);
            startActivity(clz, bundle, finish);
        }
    }

    protected void startContainerActivity(Map<String, Object> params) {
        if (params != null) {
            String canonicalName = (String) params.get(ParameterField.CANONICAL_NAME);
            Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
            startContainerActivity(canonicalName, bundle);
        }
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle, boolean finish) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (finish) {
            startActivityFinish(intent);
        } else {
            startActivity(intent);
        }
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * startActivity 方法优化
     */

    public void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

    public void startActivityFinish(Class<? extends Activity> cls) {
        startActivityFinish(new IntentData(this, cls));
    }

    public void startActivityFinish(Intent intent) {
        startActivity(intent);
        finish();
    }

    public void startActivityFinish(IntentData intent) {
        startActivity(intent);
        finish();
    }

    public ActivityCallback mActivityCallback;
    private int mActivityRequestCode;

    /**
     * Activity 回调接口
     */
    public interface ActivityCallback {

        /**
         * 结果回调
         *
         * @param resultCode 结果码
         * @param data       数据
         */
        void onActivityResult(int resultCode, @Nullable Intent data);
    }

    public void startActivityForResult(Class<? extends Activity> cls, ActivityCallback callback) {
        startActivityForResult(new Intent(this, cls), null, callback);
    }

    public void startActivityForResult(Intent intent, ActivityCallback callback) {
        startActivityForResult(intent, null, callback);
    }

    public void startActivityForResult(Intent intent, @Nullable Bundle options, ActivityCallback callback) {
        // 回调还没有结束，所以不能再次调用此方法，这个方法只适合一对一回调，其他需求请使用原生的方法实现
        if (mActivityCallback == null) {
            mActivityCallback = callback;

            // 随机生成请求码，这个请求码在 0 - 255 之间
            mActivityRequestCode = new Random().nextInt(255);
            startActivityForResult(intent, mActivityRequestCode, options);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mActivityCallback != null && mActivityRequestCode == requestCode) {
            mActivityCallback.onActivityResult(resultCode, data);
            mActivityCallback = null;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
