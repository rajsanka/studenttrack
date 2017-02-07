package org.xchg.online.baseframe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.anon.smart.client.SmartCommunicator;
import org.anon.smart.client.SmartResponseListener;
import org.anon.smart.client.SmartSecurity;

import java.util.List;


/**
 * Created by rsankarx on 17/10/16.
 */

public class SessionManager {

    public static class LogoutListener implements SmartResponseListener {
        private Activity activity;

        LogoutListener(Activity act) {
            activity = act;
        }

        @Override
        public void handleResponse(List list) {

        }

        @Override
        public void handleError(double v, String s) {

        }

        @Override
        public void handleNetworkError(String s) {

        }
    }

    public static void storeSession(Context ctx, String email, String sessId) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
        prefsLoggedInUser.edit().putString(Preferences.KEY_LOGGED_IN_EMAIL, email).apply();
        prefsLoggedInUser.edit().putString(Preferences.SESSION_ID, sessId).apply();
        prefsLoggedInUser.edit().putBoolean(Preferences.IS_LOGIN, true).apply();
    }

    public static void storeRoleName(Context ctx, String role) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
        prefsLoggedInUser.edit().putString(Preferences.ROLE_NAME, role).apply();
    }

    public static void storeProfile(Context ctx, String email, String name, String phone) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_PROFILE, Context.MODE_PRIVATE);
        prefsLoggedInUser.edit().putString(Preferences.KEY_LOGGED_IN_EMAIL, email).apply();
        prefsLoggedInUser.edit().putString(Preferences.KEY_LOGGED_IN_NAME, name).apply();
        prefsLoggedInUser.edit().putString(Preferences.KEY_LOGGED_IN_PHONE, phone).apply();
    }

    public static String getRoleName(Context ctx) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
        return prefsLoggedInUser.getString(Preferences.ROLE_NAME, null);
    }

    public static String getProfileName(Context ctx) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_PROFILE, Context.MODE_PRIVATE);
        return prefsLoggedInUser.getString(Preferences.KEY_LOGGED_IN_NAME, null);
    }

    public static String getProfilePhone(Context ctx) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_PROFILE, Context.MODE_PRIVATE);
        return prefsLoggedInUser.getString(Preferences.KEY_LOGGED_IN_PHONE, null);
    }

    public static String getProfileEmail(Context ctx) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_PROFILE, Context.MODE_PRIVATE);
        return prefsLoggedInUser.getString(Preferences.KEY_LOGGED_IN_EMAIL, null);
    }

    public static void setupSmartCommunicator(Context ctx) {
        if (isLoggedIn(ctx)) {
            SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
            String sessId = prefsLoggedInUser.getString(Preferences.SESSION_ID, null);
            if (sessId != null) SmartCommunicator.getInstance(ctx).setSessionId(sessId);
        }
    }

    public static void storeFeaturePermitted(Context ctx, String feature, String permit) {
        SharedPreferences prefs = ctx.getSharedPreferences(Preferences.FEATURES_PERMITTED, Context.MODE_PRIVATE);
        prefs.edit().putString(feature, permit).apply();
    }

    public static void markAllPermitted(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(Preferences.FEATURES_PERMITTED, Context.MODE_PRIVATE);
        prefs.edit().putString(Preferences.ALL_PERMITTED, "true").apply();
    }

    public static boolean isLoggedIn(Context ctx) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
        return prefsLoggedInUser.getBoolean(Preferences.IS_LOGIN, false);
    }

    public static String getLoggedInUser(Context ctx) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
        return prefsLoggedInUser.getString(Preferences.KEY_LOGGED_IN_EMAIL, null);
    }

    public static boolean isDeviceCreated(Context ctx) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.DEVICE_DETAILS, Context.MODE_PRIVATE);
        return prefsLoggedInUser.getBoolean(Preferences.DEVICE_CREATED, false);
    }

    public static void storeDevice(Context ctx, String devId) {
        SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.DEVICE_DETAILS, Context.MODE_PRIVATE);
        prefsLoggedInUser.edit().putBoolean(Preferences.DEVICE_CREATED, true).apply();
        prefsLoggedInUser.edit().putString(Preferences.DEVICE_ID, devId).apply();
    }

    public static String getDeviceId(Context ctx) {
        if (isDeviceCreated(ctx)) {
            SharedPreferences prefsLoggedInUser = ctx.getSharedPreferences(Preferences.DEVICE_DETAILS, Context.MODE_PRIVATE);
            String device = prefsLoggedInUser.getString(Preferences.DEVICE_ID, "");
            return device;
        }

        return "";
    }

    public static boolean checkValidSession(Activity activity, SmartSecurity.SessionCheckListener listener) {
        SharedPreferences prefsLoggedInUser = activity.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
        String sessId = prefsLoggedInUser.getString(Preferences.SESSION_ID, null);
        boolean wait = false;
        if (sessId != null) {
            SmartSecurity.validSession(activity, sessId, listener);
            wait = true;
        }

        return wait;
    }

    public static void logout(Activity activity) {
        SmartSecurity.logout(activity, new LogoutListener(activity));
        SharedPreferences prefsLoggedInUser = activity.getSharedPreferences(Preferences.PREFS_LOGIN, Context.MODE_PRIVATE);
        prefsLoggedInUser.edit().remove(Preferences.KEY_LOGGED_IN_EMAIL).apply();
        prefsLoggedInUser.edit().remove(Preferences.SESSION_ID).apply();
        prefsLoggedInUser.edit().remove(Preferences.IS_LOGIN).apply();
    }
}
