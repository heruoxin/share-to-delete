package com.catchingnow.sharetodelete;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
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

    public final static String PREF_AUTO_CLEAN = "pref_auto_clean";
    public final static String PREF_AUTO_CLEAN_FILE_TYPE = "pref_auto_clean_file_type";
    public final static String PREF_AUTO_CLEAN_DELAY_DATE = "pref_auto_clean_delay_date";

    private Toolbar mActionBar;
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefChangeListener;
    private Context context;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getBaseContext();
        addPreferencesFromResource(R.xml.preference);
        mActionBar.setTitle(getTitle());
    }

    @Override
    protected void onResume() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActionBar.setElevation(MyUtil.dip2px(context, 4));
        }

        super.onResume();
        initSharedPrefListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO: bind jobSchedle here.
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

