package com.xibei.physicaldistributionmanagement.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.xibei.physicaldistributionmanagement.MyApplication;

public class ToastUtil {
    private static Toast mToast = null;

    public static Context mContext = MyApplication.getInstance();

    private static Toast toast;

    public ToastUtil() {
    }

    public static void toUi(final Activity context, final String content) {
//        if (toast == null) {
//            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
//        } else {
//            toast.setText(content);
//            toast.setDuration(Toast.LENGTH_SHORT);
//        }
//        toast.show();

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void showText(CharSequence text) {
        if (mContext != null) {
            if (mToast == null) {
                mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            try {
                mToast.show();
            } catch (Throwable var2) {
                var2.printStackTrace();
            }

        }
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
    public static void showToast(Context context,String str){

        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

    }
}
