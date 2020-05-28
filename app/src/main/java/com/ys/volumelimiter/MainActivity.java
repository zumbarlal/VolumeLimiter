package com.ys.volumelimiter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.ys.volumelimiter.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding activityMainBinding;


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = activityMainBinding.getRoot();
        setContentView(view);

        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert am != null;
//        int musicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

        int audio_percentage = sharedPreferences.getInt(Constants.VOLUME_LIMIT, -1);
        if (audio_percentage ==-1) {
            audio_percentage = 100;
            sharedPreferences.edit().putInt(Constants.VOLUME_LIMIT, audio_percentage).apply();
        }

        activityMainBinding.seekBar.setProgress(audio_percentage);
        activityMainBinding.txtPercentage.setText(audio_percentage+"%");


        Intent intent = new Intent(getApplicationContext(), VolumeControlService.class);
        startService(intent);

        activityMainBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                Log.d(TAG, "onProgressChanged: "+progress);
                sharedPreferences.edit().putInt(Constants.VOLUME_LIMIT, progress).apply();
                activityMainBinding.txtPercentage.setText(progress+"%");

                int musicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                Log.d(TAG, "has changed : "+musicVolume);

                int limit = (int) (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * progress/100);
                if (musicVolume>limit) {
                    am.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            limit,
                            0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    //TESTING AUTHOR
}
