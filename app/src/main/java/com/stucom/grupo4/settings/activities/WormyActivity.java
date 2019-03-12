package com.stucom.grupo4.settings.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stucom.grupo4.settings.R;
import com.stucom.grupo4.settings.customViews.WormyView;

public class WormyActivity extends AppCompatActivity
        implements WormyView.WormyListener, SensorEventListener {

    // Music
    private MediaPlayer mediaPlayer;
    // SFX
    private SoundPool soundPool;
    private boolean loaded;

    private WormyView wormyView;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wormy);

        wormyView = findViewById(R.id.wormyView);
        Button btnNewGame = findViewById(R.id.btnNewGame);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wormyView.newGame();
            }
        });
        wormyView.setWormyListener(this);

        // Init audio
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Music
//        mediaPlayer = MediaPlayer.create(this, R.raw.)
        // SFX
        soundPool = new SoundPool(15, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    @Override protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch(event.getKeyCode()) {
            case KeyEvent.KEYCODE_W: wormyView.update(0, -10); break;
            case KeyEvent.KEYCODE_S: wormyView.update(0, +10); break;
            case KeyEvent.KEYCODE_A: wormyView.update(-10, 0); break;
            case KeyEvent.KEYCODE_D: wormyView.update(+10, 0); break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override public void gameStart(View view) {
        // Start game music
//        mediaPlayer.start();
    }
    @Override public void gameLost(View view) {
        Toast.makeText(this, getString(R.string.game_over), Toast.LENGTH_LONG).show();
    }
    @Override public void scoreUpdated(View view, int score) {

    }

    @Override public void onSensorChanged(SensorEvent event) {
        float xAcc = event.values[0];
        float yAcc = event.values[1];
        wormyView.update(-xAcc, yAcc);
//        Log.d("dky", "X - " + event.values[0] + "\nY - " + event.values[1]);
    }
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}