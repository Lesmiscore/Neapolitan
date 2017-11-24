package com.njlabs.showjava.ui;

import android.os.*;
import android.view.*;
import android.widget.*;

import com.nao20010128nao.Neapolitan.*;

public class About extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_about);
        ((TextView) findViewById(R.id.AppVersion)).setText("Version " + BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
