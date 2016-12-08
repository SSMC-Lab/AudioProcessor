package fruitbasket.com.audioprocessor;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fruitbasket.com.audioprocessor.sensor.AccSensor;
import fruitbasket.com.audioprocessor.sensor.GyrSensor;
import fruitbasket.com.audioprocessor.sensor.MagsSensor;
import fruitbasket.com.audioprocessor.task.AccCollectionTask;
import fruitbasket.com.audioprocessor.task.GyrCollectionTask;
import fruitbasket.com.audioprocessor.task.MagsCollectionTask;

import fruitbasket.com.audioprocessor.utilities.Utilities;

public class DataCollectionService extends Service {

    private static final String TAG = "DataCollectionService";

    private SensorManager mSensorManager;
    private SensorEventListener mySensorListener;
    private ExecutorService accExecutor;
    private ExecutorService gyrExecutor;
    private ExecutorService magsExecutor;

    private AccSensor[] accSensorDatas;
    private GyrSensor[] gyrSensorDatas;
    private MagsSensor[] magsSensorDatas;

    private int accLength = 0;
    private int gyrLength = 0;
    private int magsLength = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mSensorManager = (SensorManager) this.getSystemService(Service.SENSOR_SERVICE);
        mySensorListener = new mySensorListener();

        int i;

        accSensorDatas = new AccSensor[AppCondition.FAST_FLUSH_INTERVAL];
        for (i = 0; i < accSensorDatas.length; ++i) {
            accSensorDatas[i] = new AccSensor();
        }
        accExecutor = Executors.newSingleThreadExecutor();


        gyrSensorDatas = new GyrSensor[AppCondition.FAST_FLUSH_INTERVAL];
        for (i = 0; i < gyrSensorDatas.length; ++i) {
            gyrSensorDatas[i] = new GyrSensor();
        }
        gyrExecutor = Executors.newSingleThreadExecutor();

        magsSensorDatas = new MagsSensor[AppCondition.MID_FLUSH_INTERVAL];
        for (i = 0; i < magsSensorDatas.length; ++i) {
            magsSensorDatas[i] = new MagsSensor();
        }
        magsExecutor = Executors.newSingleThreadExecutor();

        registerListeners();
    }

    @Override
    public void onDestroy() {
        unregisterListeners();

        accExecutor.execute(new AccCollectionTask(accSensorDatas, accLength));
        accLength = 0;
        accExecutor.shutdown();


        gyrExecutor.execute(new GyrCollectionTask(gyrSensorDatas, gyrLength));
        gyrLength = 0;
        gyrExecutor.shutdown();

        magsExecutor.execute(new MagsCollectionTask(magsSensorDatas, magsLength));
        magsLength = 0;
        magsExecutor.shutdown();

        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "onBind()");
        return new MyBinder();
    }

    private void registerListeners() {


        mSensorManager.registerListener(mySensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(mySensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(mySensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterListeners() {
        mSensorManager.unregisterListener(mySensorListener);
    }


    class MyBinder extends Binder {
        DataCollectionService getService() {
            return DataCollectionService.this;
        }
    }

    class mySensorListener implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG,event.timestamp+"");
            switch (event.sensor.getType()) {

                case Sensor.TYPE_LINEAR_ACCELERATION:///
                    Log.d(TAG, "linear acceleration changed");
                    accSensorDatas[accLength].time = Utilities.getTime();
                    accSensorDatas[accLength].accels[0] = event.values[0];
                    accSensorDatas[accLength].accels[1] = event.values[1];
                    accSensorDatas[accLength].accels[2] = event.values[2];
                    ++accLength;
                    if (accLength >= accSensorDatas.length) {
                        accExecutor.execute(new AccCollectionTask(accSensorDatas, accSensorDatas.length));
                        accLength = 0;
                    }
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    Log.d(TAG, "GYROSCOPE changed");
                    gyrSensorDatas[gyrLength].time =Utilities.getTime();
                    gyrSensorDatas[gyrLength].gyr[0] = event.values[0];
                    gyrSensorDatas[gyrLength].gyr[1] = event.values[1];
                    gyrSensorDatas[gyrLength].gyr[2] = event.values[2];
                    ++gyrLength;
                    if (gyrLength >= gyrSensorDatas.length) {
                        gyrExecutor.execute(new GyrCollectionTask(gyrSensorDatas, gyrSensorDatas.length));
                        gyrLength = 0;
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magsSensorDatas[magsLength].time = Utilities.getTime();
                    magsSensorDatas[magsLength].mags[0] = event.values[0];
                    magsSensorDatas[magsLength].mags[1] = event.values[1];
                    magsSensorDatas[magsLength].mags[2] = event.values[2];
                    ++magsLength;
                    if (magsLength >= magsSensorDatas.length) {
                        magsExecutor.execute(new MagsCollectionTask(magsSensorDatas, magsSensorDatas.length));
                        magsLength = 0;
                    }
                    break;
            }
        }

    }
}
