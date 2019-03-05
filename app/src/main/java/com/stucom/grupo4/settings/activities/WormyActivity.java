package com.stucom.grupo4.settings.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stucom.grupo4.settings.R;
import com.stucom.grupo4.settings.customViews.WormyView;

public class WormyActivity extends AppCompatActivity
        implements WormyView.WormyListener {

    private WormyView wormyView;
    private TextView tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wormy);
        wormyView = findViewById(R.id.wormyView);
        Button btnNewGame = findViewById(R.id.btnNewGame);
        tvScore = findViewById(R.id.tvScore);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvScore.setText("0");
                wormyView.newGame();
            }
        });
        wormyView.setWormyListener(this);
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

    @Override
    public void scoreUpdated(View view, int score) {
        tvScore.setText(String.valueOf(score));
    }

    @Override
    public void gameLost(View view) {
        Toast.makeText(this, getString(R.string.game_over), Toast.LENGTH_LONG).show();
    }
}