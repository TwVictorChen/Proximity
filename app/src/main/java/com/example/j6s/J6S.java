package com.example.j6s;

import android.annotation.SuppressLint;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.j6s.App.CHANNEL_ID;

public class J6S extends Service implements SensorEventListener {
    private String action;
    ComponentName admin;
    private int delay = 0;
    private boolean land = false;
    private DevicePolicyManager pm;
    boolean pocket;
    boolean ff;
    private PowerManager powerManager;
    private Sensor sensor;
    private SensorManager sensorManager;
    private PowerManager.WakeLock wakeLock;


   @Override
    public void onCreate() {
     super.onCreate();
    }

    @SuppressLint("InvalidWakeLockTag")

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.admin = new ComponentName(this, Admin.class);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.pm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        pm.isAdminActive(J6S.this.admin);
        this.powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        this.action = intent.getStringExtra("action");
        this.sensorManager.unregisterListener(this);
        this.pocket = false;
        this.delay = intent.getIntExtra("delay", 0);
        this.land = intent.getBooleanExtra("landscape", false);
        this.wakeLock = this.powerManager.newWakeLock(268435482, "WAKELOCK");
        this.action.equals("pocketproximity");
        this.sensorManager.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_NORMAL);
        this.pocket = true;
        this.ff = true;
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,
                0 /* Request code */,
                notificationIntent,
                0);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("J6S")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pi)
                .build();
        startForeground(1,notification);

        return START_REDELIVER_INTENT;
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        new Timer().schedule(new TimerTask() {
            public void run() {
                if (event.values[0] < J6S.this.sensor.getMaximumRange()) {
                    if (J6S.this.action.equals("pocketproximity")) {
                        J6S.this.wakeLock.acquire();
                        J6S.this.wakeLock.release();
                    }
                } else if (event.values[0] >= J6S.this.sensor.getMaximumRange() && J6S.this.action.equals("pocketproximity")) {
                   J6S.this.pm.lockNow();
                }
            }

        }, (long) J6S.this.delay);
    }


    public void onDestroy() {
        android.os.Debug.waitForDebugger();
        super.onDestroy();
        stopForeground(true);
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        this.sensorManager.unregisterListener(this);
    }


    public void onConfigurationChanged(Configuration myConfig) {
        super.onConfigurationChanged(myConfig);
        if (getResources().getConfiguration().orientation == 2) {
            if (this.land) {
                this.sensorManager.unregisterListener(this);
            }
        } else if (this.land) {
            this.sensorManager.registerListener(this, this.sensor, 3);
        }
    }

}





