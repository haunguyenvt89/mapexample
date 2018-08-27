package com.example.haunguyen.mapexample.Utlis;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveData {
    public final static String SETTING_KEY = "setting_key";
    public final static String REQUEST_TIME = "request_time";
    public static void saveSetting(int value, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(SETTING_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(REQUEST_TIME, value);
        editor.commit();
    }
    public static int getInt(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(SETTING_KEY, Context.MODE_PRIVATE);
        return sharedPref.getInt(REQUEST_TIME, 10);
    }

}
