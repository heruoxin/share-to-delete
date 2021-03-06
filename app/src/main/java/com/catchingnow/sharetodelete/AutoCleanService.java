package com.catchingnow.sharetodelete;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AutoCleanService extends JobService {

    public static final int NOTIFICATION_JOB_ID = 233;
    public int deletedFileCount = 0;
    private Context context;
    private SharedPreferences preferences;
    private NotificationManagerCompat notificationManagerCompat;

    public AutoCleanService() {
        super();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        context = this;
        notificationManagerCompat = NotificationManagerCompat.from(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(AutoCleanSettingsActivity.PREF_AUTO_CLEAN, false)) return false;
        sendNotification();
        cleanUp();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v(MyUtil.PACKAGE_NAME, getString(R.string.toast_auto_clean, deletedFileCount));
                Toast.makeText(
                        context,
                        getString(R.string.toast_auto_clean, deletedFileCount),
                        Toast.LENGTH_SHORT
                ).show();
                notificationManagerCompat.cancel(NOTIFICATION_JOB_ID);
            }
        }, 3000);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void cleanUp() {
        int fileType =  Integer.parseInt(preferences.getString(AutoCleanSettingsActivity.PREF_AUTO_CLEAN_FILE_TYPE, "-1"));
        long time = new Date().getTime() - (long)
                Float.parseFloat(preferences.getString(AutoCleanSettingsActivity.PREF_AUTO_CLEAN_DELAY_DATE, "9999")) * 30 * 24 * 60 * 60 * 1000;
        boolean allSdcard = preferences.getBoolean(AutoCleanSettingsActivity.PREF_AUTO_CLEAN_PATH, true);

        Log.v(MyUtil.PACKAGE_NAME, "onStartJob fileType: "+ fileType);
        Log.v(MyUtil.PACKAGE_NAME, "onStartJob time: "+ time);
        switch (fileType) {
            case 0:
                cleanUp(MediaStore.Images.Media.INTERNAL_CONTENT_URI, time, allSdcard);
                cleanUp(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, time, allSdcard);
                break;
            case 1:
                cleanUp(MediaStore.Video.Media.INTERNAL_CONTENT_URI, time, allSdcard);
                cleanUp(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, time, allSdcard);
                break;
            case 2:
                cleanUp(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, time, allSdcard);
                cleanUp(MediaStore.Images.Media.INTERNAL_CONTENT_URI, time, allSdcard);
                cleanUp(MediaStore.Video.Media.INTERNAL_CONTENT_URI, time, allSdcard);
                cleanUp(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, time, allSdcard);
                break;
        }
    }

    private void cleanUp(Uri queryUri, long time, boolean allSdcard) {
        String selection = MediaStore.Images.Media.DATE_MODIFIED + "<" + time/1000;

        Cursor cursor = null;
        try {
            String[] project = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(
                    queryUri,
                    project,
                    selection,
                    null, null);
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            if (column_index == -1) {
                return;
            }
            cursor.moveToFirst();
            while (cursor.moveToNext()){
                String path = cursor.getString(column_index);
                if (path == null) continue;
                if (allSdcard || path.contains("Screenshots")) {
                    if (new File(path).delete()) deletedFileCount += 1;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder perBuildNotification = new NotificationCompat.Builder(context)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentTitle(getString(R.string.title_activity_auto_clean))
                .setContentText("")
                .setProgress(0, 0, true)
                .setColor(getResources().getColor(R.color.accent))
                .setSmallIcon(R.drawable.ic_notification_delete);
        notificationManagerCompat.notify(NOTIFICATION_JOB_ID, perBuildNotification.build());
    }

}
