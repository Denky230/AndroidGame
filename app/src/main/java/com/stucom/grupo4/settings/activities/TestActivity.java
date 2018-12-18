package com.stucom.grupo4.settings.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stucom.grupo4.settings.MyVolley;
import com.stucom.grupo4.settings.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    final String API_URL = "https://api.flx.cat/dam2game/";
    final String MY_TOKEN = "dd4dfc8d5e0fe7993ffb1a110dccf020193b0ab09a563350d3ae2f5a8c6ef177936d29a2701e3c8ee89c77aa51f2c072a537eccc62dd79b0d8ad20bc419b51b5";

    Button btnChangeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btnChangeName = findViewById(R.id.btn_test_changeName);
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestName();

                // NAME CHANGE
                String request = null;
                try {
                    request = API_URL + "user?token=" + URLEncoder.encode(MY_TOKEN, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sendRequest(request, Request.Method.PUT);

                requestName();
            }
        });
    }

    void requestName() {
        String request = null;
        try {
            request = API_URL + "user?token=" + URLEncoder.encode(MY_TOKEN, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sendRequest(request, Request.Method.GET);
    }

    void sendRequest(String requestDataURL, int requestMethod) {
        StringRequest request = new StringRequest(requestMethod, requestDataURL,
            new Response.Listener<String>() {
                @Override public void onResponse(String response) {
                    Log.d("dky", response);
                }
            },
            new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                Log.d("dky", "Error");
            }
            }) {
                @Override protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("param1", "value1");
                    params.put("param2", "value2");
                    return params;
                }
            };
        MyVolley.getInstance(this).add(request);
    }
}
