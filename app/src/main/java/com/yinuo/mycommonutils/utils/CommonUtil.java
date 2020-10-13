package com.yinuo.mycommonutils.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.view.ViewCompat;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yinuo.mycommonutils.R;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class CommonUtil {
    public static final String URL_AY_STATIC = "http://static.airtakeapp.com/";
    public static final String URL_AZ_STATIC = "http://static.getairtake.com/";

    private static PowerManager.WakeLock wakeLock;

    private static int transferSeq = 0;

    public static String getStaticUrl(boolean isEn) {
        return isEn ? URL_AZ_STATIC : URL_AY_STATIC;
    }

    public static void hideIMM(Activity activity) {
        if (activity == null) {
            return;
        }
        View fcs = activity.getCurrentFocus();
        if (fcs == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(fcs.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     *获取应用版本号
    */
    public static String getAppVersionName(Context context) {
        String versionName = "0";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                versionName = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     *获取应用名称
    */
    public static String getApplicationName(Context context, String packageName) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }


    public static boolean isChineseLang() {
        String countryKey = Locale.getDefault().getCountry().toUpperCase();
        if (!TextUtils.isEmpty(countryKey)) {
            if (countryKey.equals("ZH")) return true;
        }
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        return language.equals("zh");
    }

    /**
     * 判断是否是中国
     * 1、手机卡国家区号
     * 2、时区判断
     *
     * @return
     */
    public static boolean isChina(Context ctx) {
        try {
            String countryCode = getCountryCode(ctx, null);
            if (TextUtils.isEmpty(countryCode)) {
                TimeZone tz = TimeZone.getDefault();
                String id = tz.getID();
                return TextUtils.equals(id, "Asia/Shanghai");
            }
            return TextUtils.equals(countryCode, "CN");
        } catch (Exception e) {
        }
        return false;
    }

    public static int isChinaByTimeZoneAndContryCode(Context ctx) {
        int ret = 0;
        try {
            String countryCode = getCountryCode(ctx, null);
            if (TextUtils.isEmpty(countryCode)) {
                TimeZone tz = TimeZone.getDefault();
                String id = tz.getID();
                return TextUtils.equals(id, "Asia/Shanghai") ? 1 : 0;
            }
            return TextUtils.equals(countryCode, "CN") ? 1 : 0;
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }

    /**
     * 获取国家代码
     *
     * @param context
     * @param def     默认值
     * @return
     */
    public static String getCountryCode(Context context, String def) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int phoneType = tm.getPhoneType();
            if (phoneType != TelephonyManager.PHONE_TYPE_CDMA) {
                return tm.getNetworkCountryIso().toUpperCase();
            }
        } catch (Exception e) {
        }
        return def;
    }

    /**
     *播放提示音和启用震动
    */
    public static void playMedia(Context ctx, boolean shake, boolean media) {
        try {
            if (media) {
                Uri actualDefaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(ctx, RingtoneManager.TYPE_NOTIFICATION);
                if (actualDefaultRingtoneUri != null) {
                    RingtoneManager.getRingtone(ctx, actualDefaultRingtoneUri).play();
                }
            }
            if (shake) {
                Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{10, 400}, -1);
            }
        } catch (Exception e) {

        }
    }

    /**
     *自定义加载资源文件，返回资源ID
    */
    public static int loadLocalDrawable(Context context, String name) {
        int resID = context.getResources().getIdentifier(name, "drawable", "com.yinuo.mycommonutils");
        return resID;
    }

    /**
     * 通过seesionid获取uid
     *
     * @param sessionId
     * @return
     */
    public static String getUidBySessionId(String sessionId) {
        String str = sessionId.substring(0, sessionId.length() - 32);
        String str1 = str.substring(0, str.length() - 1);
        Integer str2 = Integer.valueOf(str.substring(str.length() - 1, str.length()));
        Integer uidLength = str1.length() - str2;
        float ff = (float) uidLength / 8;
        int div = (int) Math.ceil(ff);
        Integer start = 0;
        String str3 = "";
        for (int i = 0; i < div; i++) {
            Integer end = (i + 1) * 8;
            if (end > uidLength) {
                end = uidLength;
            }
            str3 += str1.substring(start, end + i);
            start = end + i + 1;
        }
        return str3;
    }

    /**
     * 是否是指定的的url
     *
     * @return
     */
    public static boolean isTuyaUrl(String url) {
        Uri uri = Uri.parse(url);
        if (uri != null && uri.getHost() != null) {
            String host = uri.getHost();
            if (host != null && (host.contains("yinuo.com") || host.contains("yinuo1.com")
                    || host.contains("yinuo2.com") || host.contains("yinuo3.com") || host.contains("yinuo4.me"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * url检查
     *
     * @param url
     * @return
     */
    public static boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        if (url.indexOf("http://") == -1 || url.indexOf("https://") == -1 || url.indexOf("file:///") == -1) {
            return true;
        }

        return false;
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        return Build.VERSION.SDK_INT >= VersionCode;
    }

    /**
     * 颜色加深处理
     *
     * @param RGBValues RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
     *                  Android中我们一般使用它的16进制，
     *                  例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
     *                  red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
     *                  所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
     * @return
     */
    public static int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }

    //设置应用语言类型
    public static void switchLanguage(Context context, int index) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        switch (index) {
            case 0:
                config.locale = Locale.getDefault();
                break;
            case 1:
                config.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 2:
                config.locale = Locale.ENGLISH;
                break;
            case 3:
                config.locale = new Locale("es", "ES");
                break;
        }
        resources.updateConfiguration(config, null);
    }

    /**
     * 重启应用
     *
     * @param ctx
     */
    public static void restartApplication(Context ctx) {
        final Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }

    /**
     * 获取手机本地时区
     */
    public static String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String displayName = "+08:00";
        if (tz != null) {
            String str = tz.getDisplayName();
            if (!TextUtils.isEmpty(str)) {
                int indexOf = str.indexOf("+");
                if (indexOf == -1) indexOf = str.indexOf("-");
                if (indexOf != -1) {
                    displayName = str.substring(indexOf);
                }
                if (!displayName.contains(":")) {
                    displayName = displayName.substring(0, 3) + ":" + displayName.substring(3);
                }

            }
        }
        return displayName;
    }

    public static String getLanguage() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh";
            } else {
                language = "zh_tw";
            }
        }

        return language;
    }

    @SuppressWarnings("ResourceType")
    public static void initSystemBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            TypedArray a = activity.obtainStyledAttributes(new int[]{
                    R.attr.status_system_bg_color, R.attr.status_bg_color});
            int setColor = a.getColor(0, -1);
            int statusBgColor = a.getColor(1, -1);
            //通知栏所需颜色
            if (setColor != -1) {
                tintManager.setStatusBarTintColor(setColor);
            } else if (statusBgColor != -1) {
                tintManager.setStatusBarTintColor(statusBgColor);
            }

            a.recycle();
            ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0).setFitsSystemWindows(true);//不设置全屏
        }
    }

    public static void initSystemBarColor(Activity activity, String color, boolean isFits) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(Color.parseColor(color));//通知栏所需颜色
            if (isFits)
                ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0).setFitsSystemWindows(true);//不设置全屏
        }
    }

    @TargetApi(19)
    protected static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void initSystemBarColor(Activity activity, int color) {
        if (activity == null) return;
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(color);
            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                mChildView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
        } else if (Build.VERSION.SDK_INT >= 19) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintColor(color);
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    /**
     * 获取顶部状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            Log.v("@@@@@@", "the status bar height is : " + statusBarHeight);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public static boolean isEmail(String data) {
        if (!TextUtils.isEmpty(data)) {
            Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
            return p.matcher(data).matches();
        }

        return false;
    }

    /**
     * 去除 ’86-‘ 前缀
     *
     * @param mobile
     * @return
     */
    public static String getPhoneNumberFormMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) return "";
        int i = mobile.indexOf("-");
        if (i >= 0) {
            mobile = mobile.substring(i + 1);
        }
        return mobile;
    }

    /**
     * 获取正确的 phoneCode 格式
     *
     * @param phoneCode
     * @return
     */
    public static String getRightPhoneCode(String phoneCode) {
        return phoneCode.replace("-", "").replace("+", "");
    }

}
