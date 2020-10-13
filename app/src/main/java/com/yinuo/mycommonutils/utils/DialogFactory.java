package com.yinuo.mycommonutils.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;

import com.yinuo.mycommonutils.R;

/**
 * 对话框构造类
 * @auther zh
 * @date 2020/9/15
 * @time 17:34
 */
public class DialogFactory {
    /**
     * 构造弹窗
     *
     * @param context
     * @return
     */
    public static AlertDialog.Builder buildAlertDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.Dialog_Alert);
        return dialog;
    }

    public static AlertDialog.Builder buildSmartAlertDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.Dialog_Alert_NoTitle);
        return dialog;
    }

    public static AlertDialog.Builder buildMultichoiceAlertDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.Dialog_Alert_Multichoice);
        return dialog;
    }

    public static AlertDialog.Builder buildSinglechoiceAlertDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.Dialog_Alert_Singlechoice);
        return dialog;
    }

    private static Typeface mFontTtf;

    public static Typeface getFontTtf(Context ctx) {
        if (null == mFontTtf) {
            mFontTtf = Typeface.createFromAsset(ctx.getAssets(), "font/iconfont.ttf");
        }
        return mFontTtf;
    }
}
