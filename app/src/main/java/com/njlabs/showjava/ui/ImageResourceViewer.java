package com.njlabs.showjava.ui;

import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;

import com.nao20010128nao.Neapolitan.*;
import com.njlabs.showjava.utils.*;

import org.apache.commons.io.*;

public class ImageResourceViewer extends BaseActivity {

    private String sourceFilePath;
    private String sourceFilename;
    private boolean isBlack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout(R.layout.activity_image_resource_viewer);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        ActionBar actionBar = getSupportActionBar();

        Bundle extras = getIntent().getExtras();
        String packageID = "";
        if (extras != null) {
            sourceFilePath = extras.getString("file_path");
            sourceFilename = FilenameUtils.getName(sourceFilePath);
            packageID = extras.getString("package_id");
        }

        if (actionBar != null) {
            actionBar.setTitle(sourceFilename);
            String subtitle = FilenameUtils.getFullPath(sourceFilePath).replace(Environment.getExternalStorageDirectory() + "/ShowJava/sources/" + packageID + "/", "");
            actionBar.setSubtitle(subtitle);
            if (sourceFilename.trim().equalsIgnoreCase("icon.png")) {
                actionBar.setSubtitle(packageID);
            }
        }

        TouchImageView touchImageView = findViewById(R.id.image_view);
        touchImageView.setImageDrawable(Drawable.createFromPath(sourceFilePath));

        touchImageView.setZoom(0.3f);
        touchImageView.setMinZoom(0.3f);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.invert_colors);
        item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invert_colors:
                if (isBlack) {
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                } else {
                    getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                }
                isBlack = !isBlack;
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
