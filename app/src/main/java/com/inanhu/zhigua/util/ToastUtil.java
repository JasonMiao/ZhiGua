package com.inanhu.zhigua.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Jason on 2016/6/29.
 */
public class ToastUtil {

    private static Context context;
    private static Toast toast = null;

    public static void init(Context ctx) {
        context = ctx;
    }

    public static void showToast(String id) {
        if (toast == null) {
            toast = Toast.makeText(context, id, Toast.LENGTH_SHORT);
        } else {
            toast.setText(id);
        }
        toast.show();
    }

    public static void showToast(int id) {
        if (toast == null) {
            toast = Toast.makeText(context, id, Toast.LENGTH_SHORT);
        } else {
            toast.setText(id);
        }
        toast.show();
    }
}
