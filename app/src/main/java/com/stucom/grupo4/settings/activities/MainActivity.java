package com.stucom.grupo4.settings.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stucom.grupo4.settings.MyVolley;
import com.stucom.grupo4.settings.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView txtGreeting;
    Button btnPlay;
    Button btnRanking;
    Button btnSettings;
    Button btnAbout;
    Button btnUnregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtGreeting = findViewById(R.id.txtUserGreeting);

        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });

        btnRanking = findViewById(R.id.btnRanking);
        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnAbout = findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        btnUnregister = findViewById(R.id.btnUnregister);
        btnUnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TO DO: Attempt to log out
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        // Check if user is registered
        String token = prefs.getString("token", "");
        if (token.equals("")) {
            // Send to register screen
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

        // Set greeting message
        String name = prefs.getString("name", getString(R.string.txt_greeting_default));
        String message = getString(R.string.txt_greeting, name);
        txtGreeting.setText(message);
    }

    void sendRequest(String requestDataURL, int requestMethod) {
        StringRequest request = new StringRequest(requestMethod, requestDataURL,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };
        MyVolley.getInstance(this).add(request);
    }
}
