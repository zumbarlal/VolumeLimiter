package com.ys.volumelimiter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ys.volumelimiter.VolumeControlService;

import java.util.Objects;

/**
 * Created by Senses on 26-05-2020
 * Developer : Zumbarlal Saindane.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d(TAG, "onReceive: ");
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED) ||
                Objects.equals(intent.getAction(), Intent.ACTION_LOCKED_BOOT_COMPLETED)) {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            Log.e(TAG, "launching from special > API 28 (" + Build.VERSION.SDK_INT + ")"); // You have to schedule a Service
//            boolean result = scheduleMainService(20L); // Time you will wait to launch
//        } else {
            intent = new Intent(context, VolumeControlService.class);
            context.startService(intent);
//        }
        }

    }

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    boolean scheduleMainService(Long segundos) {
        ComponentName serviceComponent = new ComponentName(context, VolumeControlService.class);
        JobInfo.Builder builder = new JobInfo.Builder(1234,serviceComponent);//getCommonBuilder(serviceComponent, YOUR_SERVICE_JOB_ID);
        builder.setMinimumLatency(TimeUnit.SECONDS.toMillis(segundos / 2)); // wait at least
        builder.setOverrideDeadline(TimeUnit.SECONDS.toMillis(segundos)); // maximum delay
        PersistableBundle extras = new PersistableBundle();
        extras.putLong("time", segundos);
        builder.setExtras(extras);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);  //getJobScheduler(context);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
            return true;
        } else {
            return false;
        }
    }*/
}
