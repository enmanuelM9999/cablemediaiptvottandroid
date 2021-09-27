package co.cablebox.tv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Set;

public class PreUtils {
    private final static String LIVE_INFO = "live";
    private final static String IP_INFO = "ip";

    public static void setInt(Context context , String key , int value){
        SharedPreferences sp = context.getSharedPreferences(LIVE_INFO , Context.MODE_PRIVATE);
        sp.edit().putInt(key , value).commit();
    }

    public static int getInt(Context context , String key , int defValue){
        SharedPreferences sp = context.getSharedPreferences(LIVE_INFO, Context.MODE_PRIVATE);
        return sp.getInt(key , defValue);
    }

    public static void setString(Context context , String key , String value){
        SharedPreferences sp = context.getSharedPreferences(IP_INFO , Context.MODE_PRIVATE);
        sp.edit().putString(key , value).commit();
    }

    public static String getString(Context context , String key , String defValue){
        SharedPreferences sp = context.getSharedPreferences(IP_INFO, Context.MODE_PRIVATE);
        return sp.getString(key , defValue);
    }
}
