package com.catchingnow.sharetodelete;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        readFiles(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        onCancelClick(null);
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

    private void readFiles(Intent intent) {
        String action = intent.getAction();

        ArrayList<String> filesPath = new ArrayList<>();

        if (Intent.ACTION_SEND.equals(action)) {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            filesPath.add(MyUtil.getRealPathFromURI(this, uri));
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            for (Uri uri: uris) {
                filesPath.add(MyUtil.getRealPathFromURI(this, uri));
            }
        }

        for (String filePath: filesPath) {
            Log.v("233", filePath);
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
        //TODO: delete file(s)
        onCancelClick(view);
    }
}
