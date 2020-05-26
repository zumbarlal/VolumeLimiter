package in.zums.volumelimiter;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

/**
 * Created by Senses on 10-05-2020
 * Developer : Zumbarlal Saindane.
 */
public class VolumeControlService extends Service {
    private static final String TAG = "VolumeControlService";

    VolumeChangeReceiver volumeChangeReceiver;
    AudioManager am;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        volumeChangeReceiver = new VolumeChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(volumeChangeReceiver,intentFilter);

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(volumeChangeReceiver);
        Log.d(TAG, "onDestroy: ");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceTask = new Intent(this,this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 10000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    private void startMyOwnForeground(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String NOTIFICATION_CHANNEL_ID = "in.zums.volumecontroller";
            String channelName = "Volume background service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_notification)
                    .setContentTitle("Service is running...")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }

    private class VolumeChangeReceiver extends BroadcastReceiver {
        private static final String TAG = "VolumeChangeReceiver";

        SharedPreferences sharedPreferences;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Objects.requireNonNull(intent.getAction()).equals("android.media.VOLUME_CHANGED_ACTION")) {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                assert am != null;
                int musicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                Log.d(TAG, "has changed : "+musicVolume);

                sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
                int audio_percentage = sharedPreferences.getInt(Constants.VOLUME_LIMIT, -1);
                int limit = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * audio_percentage/100;

                if (musicVolume>limit) {
                    am.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            limit,
                            0);
                }
            }
        }
    }
}
