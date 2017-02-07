package org.xchg.online.baseframe.utils;

import android.support.design.BuildConfig;
import android.util.Log;

/**
 * Created by rsankarx on 15/10/16.
 */
public class Logger {
    /**
     * Flag to check whether app is running in release or debug mode.
     * change this flag to false in release mode.
     */
    private static boolean debuggable = true;

    public static void d(String tag, String msg) {
        if(debuggable) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if(debuggable) {
            Log.e(tag,msg);
        }
    }

    public static void i(String tag, String msg) {
        if(debuggable) {
            Log.i(tag,msg);
        }
    }

    public static void v(String tag, String msg) {
        if(debuggable) {
            Log.v(tag,msg);
        }
    }

    public static void w(String tag, String msg) {
        if(debuggable) {
            Log.w(tag,msg);
        }
    }
}
