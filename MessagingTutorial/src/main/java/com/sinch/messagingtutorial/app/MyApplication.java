package com.sinch.messagingtutorial.app;

import android.app.Application;
import com.parse.Parse;

public class MyApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();       //initalizes the parse key
        Parse.initialize(this, "vnCbnf4r1MiM0EPAgCDuddmeDMEKz46hVpBVSwSv", "lKFrjBuEyn59KVBJuvUJNSdVDweAkfXznH5lEJxx");
    }
}
