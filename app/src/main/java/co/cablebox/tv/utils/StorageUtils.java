package co.cablebox.tv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Set;

import co.cablebox.tv.AppState;
import co.cablebox.tv.user.User;

public class StorageUtils {
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

    //-------------new, with app state

    public static void setString(String key , String value){
        Context context= AppState.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(IP_INFO , Context.MODE_PRIVATE);
        sp.edit().putString(key , value).commit();
    }

    public static String getString(String key , String defValue){
        Context context= AppState.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(IP_INFO, Context.MODE_PRIVATE);
        return sp.getString(key , defValue);
    }

    public static void setInt( String key , int value){
        Context context= AppState.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(LIVE_INFO , Context.MODE_PRIVATE);
        sp.edit().putInt(key , value).commit();
    }

    public static int getInt( String key , int defValue){
        Context context= AppState.getAppContext();
        SharedPreferences sp = context.getSharedPreferences(LIVE_INFO, Context.MODE_PRIVATE);
        return sp.getInt(key , defValue);
    }


}
