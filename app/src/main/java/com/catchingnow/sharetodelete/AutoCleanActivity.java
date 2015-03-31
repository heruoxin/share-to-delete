package com.catchingnow.sharetodelete;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class AutoCleanActivity extends PreferenceActivity {

    //Fix LG support V7 bug:
    //https://code.google.com/p/android/issues/detail?id=78154
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public final static String PREF_FIRST_LAUNCH = "pref_first_launch";
    public final static String PREF_AUTO_CLEAN = "pref_auto_clean";
    public final static String PREF_AUTO_CLEAN_FILE_TYPE = "pref_auto_clean_file_type";
    public final static String PREF_AUTO_CLEAN_DELAY_DATE = "pref_auto_clean_delay_date";
    public final static String PREF_AUTO_CLEAN_PATH = "pref_auto_clean_path";

    private Context context;
    private Toolbar mActionBar;
    private JobScheduler jobScheduler;
    private SharedPreferences preference;
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefChangeListener;

    public AutoCleanActivity() {
        myPrefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                switch (key) {
                    default:
                        break;
                }
            }
        };
    }

    public void initSharedPrefListener() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(myPrefChangeListener);
//        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
//        preference.edit().putLong(PREF_LAST_ACTIVE_THIS, new Date().getTime()).commit();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void bindJobService() {
        if (!preference.getBoolean(PREF_AUTO_CLEAN, false)) {
            jobScheduler.cancel(0);
        }
        JobInfo job = new JobInfo.Builder(0, new ComponentName(context, AutoCleanService.class))
                .setRequiresCharging(true)
                .setRequiresDeviceIdle(true)
                .setPeriodic(24 * 60 * 60 * 1000)
                .setPersisted(true)
                .build();
        jobScheduler.cancel(0);
        jobScheduler.schedule(job);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getBaseContext();
        addPreferencesFromResource(R.xml.preference);
        mActionBar.setTitle(getTitle());
        preference = PreferenceManager.getDefaultSharedPreferences(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActionBar.setElevation(MyUtil.dip2px(context, 4));
            jobScheduler.cancel(0);
            initSharedPrefListener();
        } else {
            findPreference("pref_auto_clean").setEnabled(false);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bindJobService();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.activity_auto_clean, new LinearLayout(this), false);

        mActionBar = (Toolbar) contentView.findViewById(R.id.my_toolbar);
        mActionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

        getWindow().setContentView(contentView);
    }

}

