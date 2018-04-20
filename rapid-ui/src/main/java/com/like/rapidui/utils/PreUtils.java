package com.like.rapidui.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created By Like on 2016/8/12.
 * SharedPreference工具类
 */
@SuppressWarnings("unused")
public class PreUtils {

    public static void save(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String get(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public static void saveInt(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }

    public static void saveBool(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBool(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, true);
    }

    public static boolean getBool(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static void clear(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
    }

    public static void remove(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply();
    }
}
