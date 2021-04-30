package com.example.widget.tools;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.widget.R;

/**
 * @author: limuyang
 * @date: 2019-11-29
 * @Description:
 */
public class Tips {

    /**
     * 显示 Toast
     *
     * @param message 提示信息
     */
    public static void show(Context context, String message) {
        show(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 显示 Toast
     *
     * @param message  提示信息
     * @param duration 显示时间长短
     */
    public static void show(Context mContext, String message, int duration) {
        Toast toast = new Toast(mContext);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(createTextToastView(mContext, message));
        toast.show();
    }

    /**
     * 创建自定义 Toast View
     *
     * @param message 文本消息
     * @return View
     */
    private static View createTextToastView(Context context, String message) {
        // 画圆角矩形背景
        float rc = DisplayUtil.dip2px(context, 6);
        RoundRectShape shape = new RoundRectShape(new float[]{rc, rc, rc, rc, rc, rc, rc, rc}, null, null);
        ShapeDrawable drawable = new ShapeDrawable(shape);
        drawable.getPaint().setColor(Color.argb(225, 240, 240, 240));
        drawable.getPaint().setStyle(Paint.Style.FILL);
        drawable.getPaint().setAntiAlias(true);
        drawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);

        // 创建View
        FrameLayout layout = new FrameLayout(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        layout.setPadding(DisplayUtil.dip2px(context, 16), DisplayUtil.dip2px(context, 12), DisplayUtil.dip2px(context, 16), DisplayUtil.dip2px(context, 12));
        layout.setBackground(drawable);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(15);
        textView.setText(message);
        textView.setLineSpacing(DisplayUtil.dp2px(context, 4), 1f);
        textView.setTextColor(Color.BLACK);

        layout.addView(textView);

        return layout;
    }


    public static void showHint(Context mContext, String message, int icon) {
        showHint(mContext, message, icon, Toast.LENGTH_SHORT);
    }

    public static void showHint(Context mContext, String message, int icon, int duration) {
        Toast toast = new Toast(mContext);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View hintView = View.inflate(mContext, R.layout.hint_dialog, null);
        hintView.<TextView>findViewById(R.id.tv_hint_message).setText(message);
        hintView.<ImageView>findViewById(R.id.iv_hint_icon).setImageResource(icon);
        toast.setView(hintView);
        toast.show();
    }
}
