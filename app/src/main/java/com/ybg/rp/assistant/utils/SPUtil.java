package com.ybg.rp.assistant.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 共享参数工具类
 *
 */
public class SPUtil {

    private final static String name = "config";
    private final static int mode = Context.MODE_PRIVATE;

    public final static String TOKEN = "token";
    public final static String OPERATOR_ID = "operatorId";
    //登录
    public final static String KEY_IS_LOGIN= "key_is_login";

    /**存储*/
    public static void putBoolean(Context context,String key,boolean value){
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static void putInt(Context context,String key,int value){
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public static void putString(Context context,String key,String value){
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static void putLong(Context context,String key,long value){
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }


    /**获取*/
    public static boolean getBoolean(Context context,String key,boolean defValue){
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        boolean value = sp.getBoolean(key, defValue);
        return value;
    }
    public static int getInt(Context context,String key,int defValue){
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        int value = sp.getInt(key, defValue);
        return value;
    }
    public static long getLong(Context context,String key,long defValue){
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        long value = sp.getLong(key, defValue);
        return value;
    }
    public static String getString(Context context,String key,String defValue){
        SharedPreferences sp = context.getSharedPreferences(name, mode);
        String value = sp.getString(key, defValue);
        return value;
    }


}
