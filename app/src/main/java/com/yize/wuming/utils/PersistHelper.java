package com.yize.wuming.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistHelper {
    public static final String SP_USER_AGREEMENT_STATUS="user_agreement_status";
    public static final String SP_SAVE_FOLDER="save_folder";
    public static final String SP_SEARCH_COUNT="max_search_count";
    public static final String SP_NOTICE_DAILY="notice_daily";
    public static final String SP_NEW_DOWNLOAD_LINK="new_download_link";

    /**
     *
     * @param context 上下文
     * @param key 键名
     * @param value 值
     */
    public static void saveToSharedPreference(Context context,String key,String value){
        SharedPreferences.Editor editor=context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }
    /**
     *
     * @param context 上下文
     * @param key 键名
     * @param defaultValue 默认值
     */
    public static String readFromSharedPreference(Context context,String key,String defaultValue){
        SharedPreferences pref=context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return pref.getString(key,defaultValue);
    }
}
