package com.eduday.fileencrypter;

import android.app.Application;
import android.content.Context;

import com.facebook.soloader.SoLoader;

import fr.maxcom.libmedia.Licensing;

public class AppInstance extends Application {

    AppInstance instance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SoLoader.init(this, false);
        Licensing.allow(getApplicationContext());

    }

}