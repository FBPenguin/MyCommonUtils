package com.yinuo.mycommonutils.utils.file;

import com.yinuo.mycommonutils.utils.LogUtil;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {

    private CloseUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void closeIO(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        LogUtil.d("closeIO error"+e.toString());
                    }
                }
            }
        }
    }

    public static void closeIOQuietly(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}
