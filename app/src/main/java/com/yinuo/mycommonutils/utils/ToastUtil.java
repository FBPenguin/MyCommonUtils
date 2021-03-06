package com.yinuo.mycommonutils.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast提示工具类
 * @auther zh
 * @date 2020/9/16
 * @time 10:23
 */
public class ToastUtil {
    private static Toast longToast, shortToast;

    /**
     * 长时间提醒
     *
     * @param resId
     */
    public static synchronized void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

    public static synchronized void showToast(Context context, String tips) {
        if (longToast == null) {
            longToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        }

        longToast.setText(tips);
        longToast.show();
    }

    /**
     * 短时间提醒
     *
     * @param tips
     */
    public static synchronized void shortToast(Context context, String tips) {
        if (shortToast == null) {
            shortToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        shortToast.setText(tips);
        shortToast.show();
    }

    /**
     * 短时间提醒
     *
     * @param tipsResId
     */
    public static synchronized void shortToast(Context context, int tipsResId) {
        showToast(context, context.getString(tipsResId));
    }
}
