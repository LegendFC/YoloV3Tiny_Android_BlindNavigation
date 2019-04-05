package com.example.demo7.utils;

import android.content.Context;
import android.widget.Toast;

/**
 */
public class ToastUtils {

    // 构造方法私有化 不允许new对象
    private ToastUtils() {
    }

    // Toast对象
    private static Toast toast = null;

    /**
     * 显示Toast
     */
    public static void showText(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }
}