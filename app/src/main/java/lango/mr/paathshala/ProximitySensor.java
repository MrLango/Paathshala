package lango.mr.paathshala;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import static android.content.ContentValues.TAG;

/**
 * Created by MAHE on 4/6/2018.
 */

public class ProximitySensor extends Service {
    SensorManager mySensorManager;
    Sensor myProximitySensor;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        SensorManager mySensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);
    SensorEventListener proximitySensorEventListener
            = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0) {
                   turnOnScreen();
                } else {
                    turnOnScreen();
                }
            }
        }
    };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void turnOnScreen(){
       // MWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,"tag");
        //MWakeLock.acquire();
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "incall");
        }
        if (!mWakeLock.isHeld()) {
            //
            mWakeLock.acquire();
        } else {
            //Log.d(TAG, "New call active while incall (CPU only) wake lock already active");
        }
    }

    public void turnOffScreen(){
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,"tag");
        mWakeLock.acquire();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
