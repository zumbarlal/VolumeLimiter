package in.zums.volumelimiter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

import in.zums.volumelimiter.VolumeControlService;

/**
 * Created by Senses on 26-05-2020
 * Developer : Zumbarlal Saindane.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if(Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED) ||
                Objects.equals(intent.getAction(), Intent.ACTION_LOCKED_BOOT_COMPLETED)){
            intent = new Intent(context, VolumeControlService.class);
            context.startService(intent);
        }
    }
}
