package com.njlabs.showjava.ui;

import android.Manifest;
import android.annotation.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;

import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.*;
import com.mikepenz.materialdrawer.model.interfaces.*;
import com.njlabs.showjava.BuildConfig;
import com.njlabs.showjava.*;
import com.njlabs.showjava.R;
import com.njlabs.showjava.utils.*;
import com.njlabs.showjava.utils.logging.*;
import com.nononsenseapps.filepicker.*;

import org.apache.commons.io.*;

import java.io.*;
import java.util.*;


@SuppressWarnings("unused")
public class Landing extends BaseActivity{

    private static final int FILE_PICKER = 0;

    private LinearLayout welcomeLayout;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupLayout(R.layout.activity_landing);

        listView = findViewById(R.id.history_list);
        View header = getLayoutInflater().inflate(R.layout.history_header_view, listView, false);
        listView.addHeaderView(header, null, false);

        welcomeLayout = findViewById(R.id.welcome_layout);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.navbar_header)
                .addProfiles(
                        new ProfileDrawerItem().withName(getResources().getString(R.string.app_name)+(isPro()?" Pro":"")).withEmail("Version " + BuildConfig.VERSION_NAME).withSelectable(false)
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        ArrayList<IDrawerItem> drawerItems = new ArrayList<>();

        drawerItems.add(new PrimaryDrawerItem().withName("Home").withIcon(R.drawable.ic_action_home).withSelectable(false));
        drawerItems.add(new DividerDrawerItem());
        drawerItems.add(new PrimaryDrawerItem().withName("Report a Bug").withIcon(R.drawable.ic_action_bug_report).withSelectable(false));
        drawerItems.add(new PrimaryDrawerItem().withName("About the app").withIcon(R.drawable.ic_action_info).withSelectable(false));
        drawerItems.add(new PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_action_settings).withSelectable(false));

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withDrawerItems(drawerItems)
                .withOnDrawerItemClickListener((view, position, item) -> {
                    switch (position) {
                        case 2:
                            Uri uri = Uri.parse("https://github.com/niranjan94/show-java/issues/new");
                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            break;
                        case 3:
                            startActivity(new Intent(baseContext, About.class));
                            break;
                        case 4:
                            startActivity(new Intent(baseContext, SettingsActivity.class));
                            break;
                    }
                    return false;
                })
                .withCloseOnClick(true)
                .build();

        if(isMarshmallow()){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,  new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, Constants.STORAGE_PERMISSION_REQUEST);
            } else {
                initHistoryLoader();
            }
        } else {
            initHistoryLoader();
        }
    }

    public void initHistoryLoader(){
        HistoryLoader historyLoader = new HistoryLoader();
        historyLoader.execute();
    }
    public void SetupList(List<SourceInfo> AllPackages) {

        if (AllPackages.size() < 1) {
            listView.setVisibility(View.GONE);
            welcomeLayout.setVisibility(View.VISIBLE);
        } else {
            welcomeLayout.setVisibility(View.INVISIBLE);

            ArrayAdapter<SourceInfo> decompileHistoryItemArrayAdapter = new ArrayAdapter<SourceInfo>(getBaseContext(), R.layout.history_list_item, AllPackages) {
                @SuppressLint("InflateParams")
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(R.layout.history_list_item, null);
                    }

                    SourceInfo pkg = getItem(position);

                    ViewHolder holder = new ViewHolder();

                    holder.packageLabel = convertView.findViewById(R.id.history_item_label);
                    holder.packageName = convertView.findViewById(R.id.history_item_package);
                    holder.packageIcon = convertView.findViewById(R.id.history_item_icon);

                    convertView.setTag(holder);

                    holder.packageLabel.setText(pkg.getPackageLabel());
                    holder.packageName.setText(pkg.getPackageName());

                    if (pkg.getPackageLabel().equalsIgnoreCase(pkg.getPackageName())) {
                        holder.packageName.setVisibility(View.INVISIBLE);
                    }

                    String iconPath = Environment.getExternalStorageDirectory() + "/ShowJava/sources/" + pkg.getPackageName() + "/icon.png";

                    if (new File(iconPath).exists()) {
                        Bitmap iconBitmap = BitmapFactory.decodeFile(iconPath);
                        holder.packageIcon.setImageDrawable(new BitmapDrawable(getResources(), iconBitmap));
                    } else {
                        holder.packageIcon.setImageResource(R.drawable.generic_icon);
                    }

                    return convertView;
                }
            };
            listView.setAdapter(decompileHistoryItemArrayAdapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                final ViewHolder holder = (ViewHolder) view.getTag();
                final File sourceDir = new File(Environment.getExternalStorageDirectory() + "/ShowJava/sources/" + holder.packageName.getText().toString() + "");
                Intent i = new Intent(getApplicationContext(), JavaExplorer.class);
                i.putExtra("java_source_dir", sourceDir + "/");
                i.putExtra("package_id", holder.packageName.getText().toString());
                startActivity(i);
            });

            listView.setVisibility(View.VISIBLE);

        }
    }

    public void OpenAppListing(View v) {
        Intent i = new Intent(getApplicationContext(), AppListing.class);
        startActivity(i);
    }

    public void OpenFilePicker(View v) {
        Intent i = new Intent(this, FilePicker.class);

        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_PICKER) {
            if (data != null) {

                Uri uri = data.getData();
                File apkFile = new File(uri.getPath());
                final String PackageDir = apkFile.getAbsolutePath();

                Ln.d(PackageDir);

                final String PackageName;
                final String PackageId;

                if (FilenameUtils.isExtension(PackageDir, "apk")) {
                    PackageManager pm = getPackageManager();
                    PackageInfo info = pm.getPackageArchiveInfo(PackageDir, PackageManager.GET_ACTIVITIES);
                    if (info != null) {
                        ApplicationInfo appInfo = info.applicationInfo;

                        appInfo.sourceDir = PackageDir;
                        appInfo.publicSourceDir = PackageDir;
                        PackageName = info.applicationInfo.loadLabel(getPackageManager()).toString();
                        PackageId = info.packageName;

                    } else {
                        PackageName = "";
                        PackageId = "";
                    }

                    if(!prefs.getBoolean("hide_decompiler_select",false)){
                        final CharSequence[] items = { "CFR 0.102", "JaDX 0.6.1" };
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Pick a decompiler");
                        builder.setItems(items, (dialog, item) -> {
                            Intent i = new Intent(getApplicationContext(), AppProcessActivity.class);
                            i.putExtra("package_id", PackageId);
                            i.putExtra("package_label", PackageName);
                            i.putExtra("package_file_path", PackageDir);
                            i.putExtra("decompiler", (item==1?"jadx":"cfr"));
                            startActivity(i);
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Intent i = new Intent(getApplicationContext(), AppProcessActivity.class);
                        i.putExtra("package_id", PackageId);
                        i.putExtra("package_label", PackageName);
                        i.putExtra("package_file_path", PackageDir);
                        i.putExtra("decompiler", prefs.getString("decompiler", "cfr"));
                        startActivity(i);
                    }
                }
            }
        }
    }

    private void cleanOldSources() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/ShowJava");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.getName().equalsIgnoreCase("sources")) {
                    try {
                        if(file.exists()){
                            if(file.isDirectory()){
                                FileUtils.deleteDirectory(file);
                            } else {
                                file.delete();
                            }
                        }
                    } catch (Exception e) {
                        Ln.d(e);
                    }
                }
            }
        } else {
            dir.mkdirs();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        rerunHistoryLoader();
    }

    private void rerunHistoryLoader() {
        HistoryLoader historyLoaderTwo = new HistoryLoader();
        historyLoaderTwo.execute();
    }

    private static class ViewHolder {
        TextView packageLabel;
        TextView packageName;
        ImageView packageIcon;
        int position;
    }

    private class HistoryLoader extends AsyncTask<String, String, List<SourceInfo>> {

        @Override
        protected List<SourceInfo> doInBackground(String... params) {

            List<SourceInfo> historyItems = new ArrayList<>();
            File showJavaDir = new File(Environment.getExternalStorageDirectory() + "/ShowJava/");
            showJavaDir.mkdirs();

            File nomedia = new File(showJavaDir, ".nomedia");

            if (!nomedia.exists() || !nomedia.isFile()) {
                try {
                    nomedia.createNewFile();
                } catch (IOException e) {
                    Ln.d(e);
                }
            }

            File dir = new File(Environment.getExternalStorageDirectory() + "/ShowJava/sources");

            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null && files.length > 0)
                    for (File file : files) {
                        if (Utils.sourceExists(file)) {
                            historyItems.add(Utils.getSourceInfoFromSourcePath(file));
                        } else {
                            if (!Utils.isProcessorServiceRunning(baseContext)) {
                                try {
                                    if (file.exists()) {
                                        if (file.isDirectory()) {
                                            FileUtils.deleteDirectory(file);
                                        } else {
                                            file.delete();
                                        }
                                    }

                                } catch (Exception e) {
                                    Ln.d(e);
                                }
                            }
                            if (file.exists() && !file.isDirectory()) {
                                file.delete();
                            }
                        }
                    }
            }

            return historyItems;
        }

        @Override
        protected void onPostExecute(List<SourceInfo> AllPackages) {
            SetupList(AllPackages);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.STORAGE_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initHistoryLoader();
                } else {
                    Toast.makeText(baseContext, "Storage permission is required to use this app", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showError() {
        Toast.makeText(this, "Your purchase could not be verified.", Toast.LENGTH_SHORT).show();
    }

    public void showPurchased() {
        put(true);
        Toast.makeText(this, "Thank you for purchasing Show Java Pro :)", Toast.LENGTH_SHORT).show();
    }


}
