package com.hackx.deepakyadav.avoidtroubles;

import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.samsung.android.remotesensor.RemoteSensorCallback;
import com.samsung.android.remotesensor.RemoteSensorEvent;
import com.samsung.android.remotesensor.RemoteSensorManager;
import com.samsung.android.remotesensor.RemoteSensorNode;
import com.samsung.android.sdk.SsdkUnsupportedException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements RemoteSensorCallback {

    AnimationDrawable ballAnimation;
    ImageView basket;
    ObjectAnimator translateX;
    boolean registered = false;
    RemoteSensorManager remoteSensorManager;
    String LOG_TAG = MainActivity.class.getSimpleName();
    RemoteSensorNode remoteSensorNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView animated_image = (ImageView) findViewById(R.id.animated_image);
        animated_image.setBackgroundResource(R.drawable.anime);
        ballAnimation = (AnimationDrawable) animated_image.getBackground();
        basket = (ImageView) findViewById(R.id.basket);

        remoteSensorManager = RemoteSensorManager.getInstance();
    }

    public void startSensor(View view) {
        try {
            remoteSensorManager.start(getApplicationContext(),
                    MainActivity.this,
                    Looper.getMainLooper());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorNodeFound(RemoteSensorNode remoteSensorNode) {
        Toast.makeText(this, "Node Found", Toast.LENGTH_SHORT).show();
        if (!registered) {
            try {
                remoteSensorManager.startSensor(remoteSensorNode,
                        Sensor.TYPE_ACCELEROMETER,
                        SensorManager.SENSOR_DELAY_UI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (remoteSensorManager.isRunning())
                Toast.makeText(this,
                        "Remote Sensor Manager Running", Toast.LENGTH_SHORT).show();
            registered = true;
        }
    }

    @Override
    public void onSensorNodeLost(RemoteSensorNode remoteSensorNode) {

    }

    @Override
    public void onRemoteSensorChanged(RemoteSensorEvent event) {
        if (event.getValues()[0] > 1 || event.getValues()[0] < -1) {
            translateX = ObjectAnimator.ofFloat(basket,
                    "translationX",
                    -(int) event.getValues()[0] * 100);
            translateX.start();
        }
    }

    @Override
    public void onSensorManagerStopped() {

    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ballAnimation.start();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
