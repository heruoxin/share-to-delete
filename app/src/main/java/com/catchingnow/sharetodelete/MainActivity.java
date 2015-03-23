package com.catchingnow.sharetodelete;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private List<String> filePaths;
    private int allFilesCount;
    private int deleteAbleFilesCount;
    private boolean deleteAble = false;

    private LayoutInflater layoutInflater;
    private LinearLayout linearLayout;
    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        linearLayout = (LinearLayout) findViewById(R.id.details);
        titleView = (TextView) findViewById(R.id.title);
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addLoadingCard(null);

        final Handler mHandler = new Handler() {
            public void handleMessage (Message msg) {
                updateList(filePaths);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                filePaths = findFiles(getIntent());
                mHandler.obtainMessage().sendToTarget();
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onCancelClick(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void addLoadingCard(String title) {
        if (title != null) {
            titleView.setText(title);
        }

        findViewById(R.id.delete).setVisibility(View.INVISIBLE);
        deleteAble = false;

        linearLayout.removeAllViews();
        View inflate = layoutInflater.inflate(R.layout.activitya_main_loading_card, null);
        linearLayout.addView(inflate);
    }

    private ArrayList<String> findFiles(Intent intent) {
        String action = intent.getAction();

        ArrayList<String> filePaths = new ArrayList<>();

        if (Intent.ACTION_SEND.equals(action)) {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            allFilesCount = 1;
            String path = MyUtil.getRealPathFromURI(this, uri);
            if (!(path == null || path.isEmpty())) {
                filePaths.add(path);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            allFilesCount = uris.size();
            for (Uri uri: uris) {
                String path = MyUtil.getRealPathFromURI(this, uri);
                if (path == null || path.isEmpty()) continue;
                filePaths.add(path);
            }
        }
        deleteAbleFilesCount = filePaths.size();

        return filePaths;
    }

    public void updateList(List<String> filePaths) {
        if (filePaths == null || filePaths.size() == 0) {
            filePaths = new ArrayList<>();
            filePaths.add(getString(R.string.list_not_found));
        } else {
            findViewById(R.id.delete).setVisibility(View.VISIBLE);
            deleteAble = true;
        }

        //set title
        titleView.setText(getString(R.string.title_files_found, allFilesCount, deleteAbleFilesCount));

        linearLayout.removeAllViews();
        for (String filePath: filePaths) {
            if (filePath == null || filePath.isEmpty()) continue;
            View inflate = layoutInflater.inflate(R.layout.activity_main_card, null);
            ((TextView) inflate.findViewById(R.id.text)).setText(filePath);
            linearLayout.addView(inflate);
        }
    }

    public void onCancelClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

    public void onDeleteClick(View view) {
        if (!deleteAble) return;

        addLoadingCard(getString(R.string.title_deleting));

        int allFiles = filePaths.size();
        int deletedFiles = 0;
        for (String filePath: filePaths) {
            File file = new File(filePath);
            if (file.delete()) deletedFiles += 1;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
        Toast.makeText(
                this,
                getString(R.string.toast_deleted, deletedFiles, allFiles),
                Toast.LENGTH_LONG
        ).show();
        onCancelClick(view);
    }
}
