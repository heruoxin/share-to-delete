package com.catchingnow.sharetodelete;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Date;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AutoCleanService extends JobService {

    public static final int NOTIFICATION_JOB_ID = 233;
    private SharedPreferences preferences;
    private NotificationManagerCompat notificationManagerCompat;

    public AutoCleanService() {
        super();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.v(MyUtil.PACKAGE_NAME, "onStartJob");
        notificationManagerCompat = NotificationManagerCompat.from(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        sendNotification();
        cleanUp();
        notificationManagerCompat.cancel(NOTIFICATION_JOB_ID);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void cleanUp() {
        int fileType = preferences.getInt(AutoCleanActivity.PREF_AUTO_CLEAN_FILE_TYPE, -1);
        long time = new Date().getTime() - (long)
                preferences.getInt(AutoCleanActivity.PREF_AUTO_CLEAN_DELAY_DATE, 999) *30*24*60*60*1000;
        //TODO: clean up.
        //if file.time.before(time) delete
    }

    private void sendNotification() {
        NotificationCompat.Builder perBuildNotification = new NotificationCompat.Builder(this)
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
