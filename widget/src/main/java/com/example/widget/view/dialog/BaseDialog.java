package com.example.widget.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.example.widget.R;
import com.example.widget.tools.DisplayUtil;
import com.example.widget.action.ActivityAction;
import com.example.widget.action.AnimAction;
import com.example.widget.action.ClickAction;
import com.example.widget.action.HandlerAction;
import com.example.widget.action.ResourcesAction;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
public class BaseDialog extends AppCompatDialog implements LifecycleOwner,
        ActivityAction, ResourcesAction, HandlerAction, ClickAction, AnimAction,
        DialogInterface.OnShowListener, DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    private final ListenersWrapper<BaseDialog> mListeners = new ListenersWrapper<>(this);
    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    private List<OnShowListener> mShowListeners;
    private List<OnCancelListener> mCancelListeners;
    private List<OnDismissListener> mDismissListeners;

    public BaseDialog(Context context) {
        this(context, R.style.BaseDialogStyle);
    }

    public BaseDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    /**
     * ?????? Dialog ????????????
     */
    public View getContentView() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    /**
     * ????????????????????????
     */
    public int getGravity() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            return params.gravity;
        }
        return Gravity.NO_GRAVITY;
    }

    /**
     * ????????????
     */
    public void setWidth(int width) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = width;
            window.setAttributes(params);
        }
    }

    /**
     * ????????????
     */
    public void setHeight(int height) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.height = height;
            window.setAttributes(params);
        }
    }

    /**
     * ?????? Dialog ??????
     */
    public void setGravity(int gravity) {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(gravity);
        }
    }

    /**
     * ?????? Dialog ?????????
     */
    public void setWindowAnimations(@StyleRes int id) {
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(id);
        }
    }

    /**
     * ?????? Dialog ?????????
     */
    public int getWindowAnimations() {
        Window window = getWindow();
        if (window != null) {
            return window.getAttributes().windowAnimations;
        }
        return BaseDialog.ANIM_DEFAULT;
    }

    /**
     * ???????????????????????????
     */
    public void setBackgroundDimEnabled(boolean enabled) {
        Window window = getWindow();
        if (window != null) {
            if (enabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????
     */
    public void setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) float dimAmount) {
        Window window = getWindow();
        if (window != null) {
            window.setDimAmount(dimAmount);
        }
    }

    @Override
    public void dismiss() {
        removeCallbacks();
        View focusView = getCurrentFocus();
        if (focusView != null) {
            getSystemService(InputMethodManager.class).hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
        super.dismiss();
    }


    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    /**
     * ???????????????????????????
     *
     * @param listener       ?????????????????????
     * @deprecated           ????????? {@link #addOnShowListener(OnShowListener)}}
     */
    @Deprecated
    @Override
    public void setOnShowListener(@Nullable DialogInterface.OnShowListener listener) {
        if (listener == null) {
            return;
        }
        addOnShowListener(new ShowListenerWrapper(listener));
    }

    /**
     * ???????????????????????????
     *
     * @param listener       ?????????????????????
     * @deprecated           ????????? {@link #addOnCancelListener(OnCancelListener)}
     */
    @Deprecated
    @Override
    public void setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        if (listener == null) {
            return;
        }
        addOnCancelListener(new CancelListenerWrapper(listener));
    }

    /**
     * ???????????????????????????
     *
     * @param listener       ?????????????????????
     * @deprecated           ????????? {@link #addOnDismissListener(OnDismissListener)}
     */
    @Deprecated
    @Override
    public void setOnDismissListener(@Nullable DialogInterface.OnDismissListener listener) {
        if (listener == null) {
            return;
        }
        addOnDismissListener(new DismissListenerWrapper(listener));
    }

    /**
     * ???????????????????????????
     *
     * @param listener       ?????????????????????
     * @deprecated           ????????? {@link #setOnKeyListener(OnKeyListener)}
     */
    @Deprecated
    @Override
    public void setOnKeyListener(@Nullable DialogInterface.OnKeyListener listener) {
        super.setOnKeyListener(listener);
    }

    public void setOnKeyListener(@Nullable OnKeyListener listener) {
        super.setOnKeyListener(new KeyListenerWrapper(listener));
    }

    /**
     * ???????????????????????????
     *
     * @param listener      ???????????????
     */
    public void addOnShowListener(@Nullable OnShowListener listener) {
        if (mShowListeners == null) {
            mShowListeners = new ArrayList<>();
            super.setOnShowListener(mListeners);
        }
        mShowListeners.add(listener);
    }

    /**
     * ???????????????????????????
     *
     * @param listener      ???????????????
     */
    public void addOnCancelListener(@Nullable OnCancelListener listener) {
        if (mCancelListeners == null) {
            mCancelListeners = new ArrayList<>();
            super.setOnCancelListener(mListeners);
        }
        mCancelListeners.add(listener);
    }

    /**
     * ???????????????????????????
     *
     * @param listener      ???????????????
     */
    public void addOnDismissListener(@Nullable OnDismissListener listener) {
        if (mDismissListeners == null) {
            mDismissListeners = new ArrayList<>();
            super.setOnDismissListener(mListeners);
        }
        mDismissListeners.add(listener);
    }

    /**
     * ???????????????????????????
     *
     * @param listener      ???????????????
     */
    public void removeOnShowListener(@Nullable OnShowListener listener) {
        if (mShowListeners != null) {
            mShowListeners.remove(listener);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param listener      ???????????????
     */
    public void removeOnCancelListener(@Nullable OnCancelListener listener) {
        if (mCancelListeners != null) {
            mCancelListeners.remove(listener);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param listener      ???????????????
     */
    public void removeOnDismissListener(@Nullable OnDismissListener listener) {
        if (mDismissListeners != null) {
            mDismissListeners.remove(listener);
        }
    }

    /**
     * ???????????????????????????
     */
    private void setOnShowListeners(@Nullable List<OnShowListener> listeners) {
        super.setOnShowListener(mListeners);
        mShowListeners = listeners;
    }

    /**
     * ???????????????????????????
     */
    private void setOnCancelListeners(@Nullable List<OnCancelListener> listeners) {
        super.setOnCancelListener(mListeners);
        mCancelListeners = listeners;
    }

    /**
     * ???????????????????????????
     */
    private void setOnDismissListeners(@Nullable List<OnDismissListener> listeners) {
        super.setOnDismissListener(mListeners);
        mDismissListeners = listeners;
    }

    /**
     * {@link DialogInterface.OnShowListener}
     */
    @Override
    public void onShow(DialogInterface dialog) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        if (mShowListeners != null) {
            for (int i = 0; i < mShowListeners.size(); i++) {
                mShowListeners.get(i).onShow(this);
            }
        }
    }

    /**
     * {@link DialogInterface.OnCancelListener}
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        if (mCancelListeners != null) {
            for (int i = 0; i < mCancelListeners.size(); i++) {
                mCancelListeners.get(i).onCancel(this);
            }
        }
    }

    /**
     * {@link DialogInterface.OnDismissListener}
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);

        if (mDismissListeners != null) {
            for (int i = 0; i < mDismissListeners.size(); i++) {
                mDismissListeners.get(i).onDismiss(this);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<B extends Builder> implements LifecycleOwner, ActivityAction, ResourcesAction, ClickAction {

        /** ??????????????? */
        private final Context mContext;
        /** Dialog ?????? */
        public BaseDialog mDialog;
        /*windowType*/
        public int  windowType;
        /** Dialog ?????? */
        private View mContentView;

        /** ???????????? */
        private int mThemeId = R.style.BaseDialogStyle;
        /** ???????????? */
        private int mAnimStyle = BaseDialog.ANIM_DEFAULT;
        /** ???????????? */
        private int mGravity = Gravity.NO_GRAVITY;

        /** ???????????? */
        private int mXOffset;
        /** ???????????? */
        private int mYOffset;

        /** ??????????????? */
        private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        /** ????????????????????? */
        private boolean mBackgroundDimEnabled = true;
        /** ???????????????????????? */
        private float mBackgroundDimAmount = 0.5f;

        /** ????????????????????? */
        private boolean mCancelable = true;
        /** ??????????????????????????????  ??????????????????????????????????????? */
        private boolean mCanceledOnTouchOutside = true;

        /** Dialog Show ?????? */
        private List<OnShowListener> mOnShowListeners;
        /** Dialog Cancel ?????? */
        private List<OnCancelListener> mOnCancelListeners;
        /** Dialog Dismiss ?????? */
        private List<OnDismissListener> mOnDismissListeners;
        /** Dialog Key ?????? */
        private OnKeyListener mOnKeyListener;

        /** ?????????????????? */
        private SparseArray<OnClickListener> mClickArray;

        public void setWindowType(int windowType) {
            this.windowType = windowType;
        }

        public Builder(Activity activity) {
            this((Context) activity);
        }

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * ???????????? id
         */
        public B setThemeStyle(@StyleRes int id) {
            if (isCreated()) {
                // Dialog ????????????????????????????????? id
                throw new IllegalStateException("are you ok?");
            }
            mThemeId = id;
            return (B) this;
        }

        /**
         * ????????????
         */
        public B setContentView(@LayoutRes int id) {
            // ???????????????????????????????????? new FrameLayout??????????????????????????????XML ???????????????????????? LayoutParams ????????????????????????????????????????????????????????????
            return setContentView(LayoutInflater.from(mContext).inflate(id, new FrameLayout(mContext), false));
        }
        public B setContentView(View view) {
            mContentView = view;

            if (isCreated()) {
                mDialog.setContentView(view);
            } else {
                if (mContentView != null) {
                    ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
                    if (layoutParams != null && mWidth == ViewGroup.LayoutParams.WRAP_CONTENT && mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                        // ???????????? Dialog ??????????????????????????????????????????????????????????????????
                        setWidth(layoutParams.width);
                        setHeight(layoutParams.height);
                    }

                    // ????????????????????????????????????????????????????????????
                    if (mGravity == Gravity.NO_GRAVITY) {
                        if (layoutParams instanceof FrameLayout.LayoutParams) {
                            setGravity(((FrameLayout.LayoutParams) layoutParams).gravity);
                        } else if (layoutParams instanceof LinearLayout.LayoutParams) {
                            setGravity(((LinearLayout.LayoutParams) layoutParams).gravity);
                        } else {
                            // ?????????????????????
                            setGravity(Gravity.CENTER);
                        }
                    }
                }
            }
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B setGravity(int gravity) {
            // ?????? Android 4.2 ????????????????????????????????????????????? - ??????????????????????????????????????????
            mGravity = gravity;
            if (isCreated()) {
                mDialog.setGravity(gravity);
            }
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B setXOffset(int offset) {
            mXOffset = offset;
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B setYOffset(int offset) {
            mYOffset = offset;
            return (B) this;
        }
        public B setMarginHorizontal(int marginDp) {
            int windowsWidth = DisplayUtil.getWindowsWidth(getContext());
            int margin = DisplayUtil.dp2px(getContext(), marginDp);
            int currentWidth = windowsWidth - margin;
            return setWidth(currentWidth);
        }
        /**
         * ????????????
         */
        public B setWidth(int width) {
            mWidth = width;
            if (isCreated()) {
                mDialog.setWidth(width);
            } else {
                ViewGroup.LayoutParams params = mContentView != null ? mContentView.getLayoutParams() : null;
                if (params != null) {
                    params.width = width;
                    mContentView.setLayoutParams(params);
                }
            }
            return (B) this;
        }

        /**
         * ????????????
         */
        public B setHeight(int height) {
            mHeight = height;
            if (isCreated()) {
                mDialog.setHeight(height);
            } else {
                // ?????????????????????????????????????????? LayoutParams
                // ????????????????????????????????????????????????????????????????????? Dialog ????????????
                // ?????? Layout ???????????????????????????????????????????????????????????????
                // ??????????????????????????? View ??? LayoutParams ???????????????
                ViewGroup.LayoutParams params = mContentView != null ? mContentView.getLayoutParams() : null;
                if (params != null) {
                    params.height = height;
                    mContentView.setLayoutParams(params);
                }
            }
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            if (isCreated()) {
                mDialog.setCancelable(cancelable);
            }
            return (B) this;
        }

        /**
         * ??????????????????????????????????????????
         */
        public B setCanceledOnTouchOutside(boolean cancel) {
            mCanceledOnTouchOutside = cancel;
            if (isCreated() && mCancelable) {
                mDialog.setCanceledOnTouchOutside(cancel);
            }
            return (B) this;
        }

        /**
         * ?????????????????????????????????????????????????????????{@link AnimAction}???
         */
        public B setAnimStyle(@StyleRes int id) {
            mAnimStyle = id;
            if (isCreated()) {
                mDialog.setWindowAnimations(id);
            }
            return (B) this;
        }

        /**
         * ???????????????????????????
         */
        public B setBackgroundDimEnabled(boolean enabled) {
            mBackgroundDimEnabled = enabled;
            if (isCreated()) {
                mDialog.setBackgroundDimEnabled(enabled);
            }
            return (B) this;
        }

        /**
         * ???????????????????????????????????????????????????????????????????????????????????????????????????
         */
        public B setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) float dimAmount) {
            mBackgroundDimAmount = dimAmount;
            if (isCreated()) {
                mDialog.setBackgroundDimAmount(dimAmount);
            }
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B addOnShowListener( OnShowListener listener) {
            if (isCreated()) {
                mDialog.addOnShowListener(listener);
            } else {
                if (mOnShowListeners == null) {
                    mOnShowListeners = new ArrayList<>();
                }
                mOnShowListeners.add(listener);
            }
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B addOnCancelListener( OnCancelListener listener) {
            if (isCreated()) {
                mDialog.addOnCancelListener(listener);
            } else {
                if (mOnCancelListeners == null) {
                    mOnCancelListeners = new ArrayList<>();
                }
                mOnCancelListeners.add(listener);
            }
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B addOnDismissListener( OnDismissListener listener) {
            if (isCreated()) {
                mDialog.addOnDismissListener(listener);
            } else {
                if (mOnDismissListeners == null) {
                    mOnDismissListeners = new ArrayList<>();
                }
                mOnDismissListeners.add(listener);
            }
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B setOnKeyListener( OnKeyListener listener) {
            if (isCreated()) {
                mDialog.setOnKeyListener(listener);
            } else {
                mOnKeyListener = listener;
            }
            return (B) this;
        }

        /**
         * ????????????
         */
        public B setText(@IdRes int viewId, @StringRes int stringId) {
            return setText(viewId, getString(stringId));
        }
        public B setText(@IdRes int id, CharSequence text) {
            ((TextView) findViewById(id)).setText(text);
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B setTextColor(@IdRes int id, @ColorInt int color) {
            ((TextView) findViewById(id)).setTextColor(color);
            return (B) this;
        }

        /**
         * ????????????
         */
        public B setHint(@IdRes int viewId, @StringRes int stringId) {
            return setHint(viewId, getString(stringId));
        }
        public B setHint(@IdRes int id, CharSequence text) {
            ((TextView) findViewById(id)).setHint(text);
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B setVisibility(@IdRes int id, int visibility) {
            findViewById(id).setVisibility(visibility);
            return (B) this;
        }

        /**
         * ????????????
         */
        public B setBackground(@IdRes int viewId, @DrawableRes int drawableId) {
            return setBackground(viewId, ContextCompat.getDrawable(mContext, drawableId));
        }
        public B setBackground(@IdRes int id, Drawable drawable) {
            findViewById(id).setBackground(drawable);
            return (B) this;
        }

        /**
         * ????????????
         */
        public B setImageDrawable(@IdRes int viewId, @DrawableRes int drawableId) {
            return setBackground(viewId, ContextCompat.getDrawable(mContext, drawableId));
        }
        public B setImageDrawable(@IdRes int id, Drawable drawable) {
            ((ImageView) findViewById(id)).setImageDrawable(drawable);
            return (B) this;
        }

        /**
         * ??????????????????
         */
        public B setOnClickListener(@IdRes int id, OnClickListener listener) {
            if (isCreated()) {
                View view = mDialog.findViewById(id);
                if (view != null) {
                    view.setOnClickListener(new ViewClickWrapper(mDialog, listener));
                }
            } else {
                if (mClickArray == null) {
                    mClickArray = new SparseArray<>();
                }
                mClickArray.put(id, listener);
            }
            return (B) this;
        }

        /**
         * ??????
         */
        @SuppressLint("RtlHardcoded")
        public BaseDialog create() {

            // ????????????????????????
            if (mContentView == null) {
                throw new IllegalArgumentException("are you ok?");
            }

            // ???????????????????????????????????????????????????????????????
            if (mGravity == Gravity.NO_GRAVITY) {
                mGravity = Gravity.CENTER;
            }

            // ???????????????????????????????????????????????????????????????????????????
            if (mAnimStyle == BaseDialog.ANIM_DEFAULT) {
                switch (mGravity) {
                    case Gravity.TOP:
                        mAnimStyle = BaseDialog.ANIM_TOP;
                        break;
                    case Gravity.BOTTOM:
                        mAnimStyle = BaseDialog.ANIM_BOTTOM;
                        break;
                    case Gravity.LEFT:
                        mAnimStyle = BaseDialog.ANIM_LEFT;
                        break;
                    case Gravity.RIGHT:
                        mAnimStyle = BaseDialog.ANIM_RIGHT;
                        break;
                    default:
                        mAnimStyle = BaseDialog.ANIM_DEFAULT;
                        break;
                }
            }

            mDialog = createDialog(mContext, mThemeId);

            mDialog.setContentView(mContentView);
            mDialog.setCancelable(mCancelable);
            if (mCancelable) {
                mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
            }

            // ????????????
            Window window = mDialog.getWindow();
            if (window != null) {
                if(windowType!=0){
                    window.setType(windowType);
                }
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = mWidth;
                params.height = mHeight;
                params.gravity = mGravity;
                params.x = mXOffset;
                params.y = mYOffset;
                params.windowAnimations = mAnimStyle;
                window.setAttributes(params);
                if (mBackgroundDimEnabled) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    window.setDimAmount(mBackgroundDimAmount);
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
            }

            if (mOnShowListeners != null) {
                mDialog.setOnShowListeners(mOnShowListeners);
            }

            if (mOnCancelListeners != null) {
                mDialog.setOnCancelListeners(mOnCancelListeners);
            }

            if (mOnDismissListeners != null) {
                mDialog.setOnDismissListeners(mOnDismissListeners);
            }

            if (mOnKeyListener != null) {
                mDialog.setOnKeyListener(mOnKeyListener);
            }

            for (int i = 0; mClickArray != null && i < mClickArray.size(); i++) {
                mContentView.findViewById(mClickArray.keyAt(i)).setOnClickListener(new ViewClickWrapper(mDialog, mClickArray.valueAt(i)));
            }

            Activity activity = getActivity();
            if (activity != null) {
                DialogLifecycle.with(activity, mDialog);
            }

            return mDialog;
        }

        /**
         * ??????
         */
        public BaseDialog show() {
            if (!isCreated()) {
                create();
            }
            mDialog.show();
            return mDialog;
        }

        /**
         * ???????????? Dialog
         */
        public void dismiss() {
            if (mDialog != null) {
                mDialog.dismiss();
            }
        }

        @Override
        public Context getContext() {
            return mContext;
        }

        /**
         * ?????? Dialog ???????????????
         */
        public boolean isCreated() {
            return mDialog != null;
        }

        /**
         * ?????? Dialog ???????????????
         */
        public boolean isShowing() {
            return mDialog != null && mDialog.isShowing();
        }

        /**
         * ?????? Dialog ????????????????????????????????????????????? Dialog ?????????
         */
        protected BaseDialog createDialog(Context context, @StyleRes int themeId) {
            return new BaseDialog(context, themeId);
        }

        /**
         * ????????????
         */
        public final void post(Runnable r) {
            if (isShowing()) {
                mDialog.post(r);
            } else {
                addOnShowListener(new ShowPostWrapper(r));
            }
        }

        /**
         * ????????????????????????
         */
        public final void postDelayed(Runnable r, long delayMillis) {
            if (isShowing()) {
                mDialog.postDelayed(r, delayMillis);
            } else {
                addOnShowListener(new ShowPostDelayedWrapper(r, delayMillis));
            }
        }

        /**
         * ????????????????????????
         */
        public final void postAtTime(Runnable r, long uptimeMillis) {
            if (isShowing()) {
                mDialog.postAtTime(r, uptimeMillis);
            } else {
                addOnShowListener(new ShowPostAtTimeWrapper(r, uptimeMillis));
            }
        }

        /**
         * ?????? Dialog ????????????
         */
        public View getContentView() {
            return mContentView;
        }

        /**
         * ?????? id ?????? View
         */
        @Override
        public  <V extends View> V findViewById(@IdRes int id) {
            if (mContentView == null) {
                // ?????? setContentView ?????? findViewById ?
                throw new IllegalStateException("are you ok?");
            }
            return mContentView.findViewById(id);
        }

        /**
         * ???????????? Dialog ??????
         */
        @Nullable
        public BaseDialog getDialog() {
            return mDialog;
        }

        @Nullable
        @Override
        public Lifecycle getLifecycle() {
            if (mDialog != null) {
                return mDialog.getLifecycle();
            }
            return null;
        }
    }

    /**
     * Dialog ??????????????????
     */
    private static final class DialogLifecycle implements
            Application.ActivityLifecycleCallbacks,
            OnShowListener,
            OnDismissListener {

        private static void with(Activity activity, BaseDialog dialog) {
            new DialogLifecycle(activity, dialog);
        }

        private BaseDialog mDialog;
        private Activity mActivity;

        /** Dialog ????????????????????? Dialog ?????????????????????????????????????????????????????? */
        private int mDialogAnim;

        private DialogLifecycle(Activity activity, BaseDialog dialog) {
            mActivity = activity;
            dialog.addOnShowListener(this);
            dialog.addOnDismissListener(this);
        }

        @Override
        public void onActivityCreated( Activity activity, @Nullable Bundle savedInstanceState) {}

        @Override
        public void onActivityStarted( Activity activity) {}

        @Override
        public void onActivityResumed( Activity activity) {
            if (mActivity != activity) {
                return;
            }

            if (mDialog != null && mDialog.isShowing()) {
                // ?????? Dialog ??????????????????????????????????????????????????????????????????????????????????????????
                mDialog.postDelayed(() -> {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.setWindowAnimations(mDialogAnim);
                    }
                }, 100);
            }
        }

        @Override
        public void onActivityPaused( Activity activity) {
            if (mActivity != activity) {
                return;
            }

            if (mDialog != null && mDialog.isShowing()) {
                // ?????? Dialog ????????????
                mDialogAnim = mDialog.getWindowAnimations();
                // ?????? Dialog ???????????????
                mDialog.setWindowAnimations(BaseDialog.ANIM_EMPTY);
            }
        }

        @Override
        public void onActivityStopped( Activity activity) {}

        @Override
        public void onActivitySaveInstanceState( Activity activity, Bundle outState) {}

        @Override
        public void onActivityDestroyed( Activity activity) {
            if (mActivity != activity) {
                return;
            }

            if (mDialog != null) {
                mDialog.removeOnShowListener(this);
                mDialog.removeOnDismissListener(this);
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mDialog = null;
            }
            unregisterActivityLifecycleCallbacks();
            // ?????? Activity ??????
            mActivity = null;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            mDialog = dialog;
            registerActivityLifecycleCallbacks();
        }

        @Override
        public void onDismiss(BaseDialog dialog) {
            mDialog = null;
            unregisterActivityLifecycleCallbacks();
        }

        /**
         * ?????? Activity ??????????????????
         */
        private void registerActivityLifecycleCallbacks() {
            if (mActivity == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mActivity.registerActivityLifecycleCallbacks(this);
            } else {
                mActivity.getApplication().registerActivityLifecycleCallbacks(this);
            }
        }

        /**
         * ????????? Activity ??????????????????
         */
        private void unregisterActivityLifecycleCallbacks() {
            if (mActivity == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mActivity.unregisterActivityLifecycleCallbacks(this);
            } else {
                mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
            }
        }
    }

    /**
     * Dialog ?????????????????????????????? Dialog ???????????????????????????????????????
     */
    private static final class ListenersWrapper<T extends DialogInterface.OnShowListener & DialogInterface.OnCancelListener & DialogInterface.OnDismissListener>
            extends SoftReference<T> implements DialogInterface.OnShowListener, DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

        private ListenersWrapper(T referent) {
            super(referent);
        }

        @Override
        public void onShow(DialogInterface dialog) {
            if (get() != null) {
                get().onShow(dialog);
            }
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (get() != null) {
                get().onCancel(dialog);
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (get() != null) {
                get().onDismiss(dialog);
            }
        }
    }

    /**
     * ?????????????????????
     */
    private static final class ViewClickWrapper
            implements View.OnClickListener {

        private final BaseDialog mDialog;
        private final OnClickListener mListener;

        private ViewClickWrapper(BaseDialog dialog, OnClickListener listener) {
            mDialog = dialog;
            mListener = listener;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final void onClick(View v) {
            mListener.onClick(mDialog, v);
        }
    }

    /**
     * ?????????????????????
     */
    private static final class ShowListenerWrapper
            extends SoftReference<DialogInterface.OnShowListener>
            implements OnShowListener {

        private ShowListenerWrapper(DialogInterface.OnShowListener referent) {
            super(referent);
        }

        @Override
        public void onShow(BaseDialog dialog) {
            // ??????????????????????????????????????????
            if (get() != null) {
                get().onShow(dialog);
            }
        }
    }

    /**
     * ?????????????????????
     */
    private static final class CancelListenerWrapper
            extends SoftReference<DialogInterface.OnCancelListener>
            implements OnCancelListener {

        private CancelListenerWrapper(DialogInterface.OnCancelListener referent) {
            super(referent);
        }

        @Override
        public void onCancel(BaseDialog dialog) {
            // ??????????????????????????????????????????
            if (get() != null) {
                get().onCancel(dialog);
            }
        }
    }

    /**
     * ?????????????????????
     */
    private static final class DismissListenerWrapper
            extends SoftReference<DialogInterface.OnDismissListener>
            implements OnDismissListener {

        private DismissListenerWrapper(DialogInterface.OnDismissListener referent) {
            super(referent);
        }

        @Override
        public void onDismiss(BaseDialog dialog) {
            // ??????????????????????????????????????????
            if (get() != null) {
                get().onDismiss(dialog);
            }
        }
    }

    /**
     * ?????????????????????
     */
    private static final class KeyListenerWrapper
            implements DialogInterface.OnKeyListener {

        private final OnKeyListener mListener;

        private KeyListenerWrapper(OnKeyListener listener) {
            mListener = listener;
        }

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            // ??????????????????????????????????????????
            if (mListener != null && dialog instanceof BaseDialog) {
                mListener.onKey((BaseDialog) dialog, event);
            }
            return false;
        }
    }

    /**
     * post ???????????????
     */
    private static final class ShowPostWrapper implements OnShowListener {

        private final Runnable mRunnable;

        private ShowPostWrapper(Runnable r) {
            mRunnable = r;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            if (mRunnable != null) {
                dialog.removeOnShowListener(this);
                dialog.post(mRunnable);
            }
        }
    }

    /**
     * postDelayed ???????????????
     */
    private static final class ShowPostDelayedWrapper implements OnShowListener {

        private final Runnable mRunnable;
        private final long mDelayMillis;

        private ShowPostDelayedWrapper(Runnable r, long delayMillis) {
            mRunnable = r;
            mDelayMillis = delayMillis;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            if (mRunnable != null) {
                dialog.removeOnShowListener(this);
                dialog.postDelayed(mRunnable, mDelayMillis);
            }
        }
    }

    /**
     * postAtTime ???????????????
     */
    private static final class ShowPostAtTimeWrapper implements OnShowListener {

        private final Runnable mRunnable;
        private final long mUptimeMillis;

        private ShowPostAtTimeWrapper(Runnable r, long uptimeMillis) {
            mRunnable = r;
            mUptimeMillis = uptimeMillis;
        }

        @Override
        public void onShow(BaseDialog dialog) {
            if (mRunnable != null) {
                dialog.removeOnShowListener(this);
                dialog.postAtTime(mRunnable, mUptimeMillis);
            }
        }
    }

    /**
     * ???????????????
     */
    public interface OnClickListener<V extends View> {
        void onClick(BaseDialog dialog, V view);
    }

    /**
     * ???????????????
     */
    public interface OnShowListener {

        /**
         * Dialog ?????????
         */
        void onShow(BaseDialog dialog);
    }

    /**
     * ???????????????
     */
    public interface OnCancelListener {

        /**
         * Dialog ?????????
         */
        void onCancel(BaseDialog dialog);
    }

    /**
     * ???????????????
     */
    public interface OnDismissListener {

        /**
         * Dialog ?????????
         */
        void onDismiss(BaseDialog dialog);
    }

    /**
     * ???????????????
     */
    public interface OnKeyListener {

        /**
         * ???????????????
         */
        boolean onKey(BaseDialog dialog, KeyEvent event);
    }
}
