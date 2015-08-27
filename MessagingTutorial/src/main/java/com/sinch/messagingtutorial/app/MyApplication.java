package com.sinch.messagingtutorial.app;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class MyApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();       //initalization key for my own personal parse account
        Parse.initialize(this, "vnCbnf4r1MiM0EPAgCDuddmeDMEKz46hVpBVSwSv", "lKFrjBuEyn59KVBJuvUJNSdVDweAkfXznH5lEJxx");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
