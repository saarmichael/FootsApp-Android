package com.example.footsapp_android;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    public static String BASE_URL;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        BASE_URL = context.getString(R.string.BaseUrl);
    }

    public static void changeUrl(String url) {
        BASE_URL = url;
    }



}
