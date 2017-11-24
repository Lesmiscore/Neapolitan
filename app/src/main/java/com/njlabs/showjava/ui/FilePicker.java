package com.njlabs.showjava.ui;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;

import com.nononsenseapps.filepicker.*;

import java.io.*;

import uk.co.chrisjenx.calligraphy.*;

public class FilePicker extends AbstractFilePickerActivity<File> {

    private FilePickerFragment currentFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }


    protected AbstractFilePickerFragment<File> getFragment(String startPath, int mode, boolean allowMultiple, boolean allowCreateDir) {
        currentFragment = new FilePickerFragment();
        currentFragment.setArgs(startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath(), mode, allowMultiple, allowCreateDir);
        return currentFragment;
    }

    /**
     * Override the back-button.
     */
    @Override
    public void onBackPressed() {
        // If at top most level, normal behaviour
        if (currentFragment == null || currentFragment.isBackTop()) {
            finish();
        } else {
            // Else go up
            currentFragment.goUp();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
