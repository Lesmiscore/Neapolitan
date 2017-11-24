package com.njlabs.showjava;

import android.app.*;
import android.content.*;
import android.support.multidex.*;

import uk.co.chrisjenx.calligraphy.*;

public class MainApplication extends Application {

    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/lato-light.ttf")
                .setFontAttrId(com.nao20010128nao.Neapolitan.R.attr.fontPath)
                .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
