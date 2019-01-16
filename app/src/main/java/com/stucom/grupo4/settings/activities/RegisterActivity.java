package com.stucom.grupo4.settings.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stucom.grupo4.settings.APIResponse;
import com.stucom.grupo4.settings.MyVolley;
import com.stucom.grupo4.settings.R;
import com.stucom.grupo4.settings.constants.APIData;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText edName;
    TextView lblName;
    EditText edEmail;
    TextView lblEmail;

    Button btnRegister;

    boolean toVerify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edName = findViewById(R.id.edName);
        lblName = findViewById(R.id.lblName);
        edEmail = findViewById(R.id.edEmail);
        lblEmail = findViewById(R.id.lblEmail);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String request = APIData.API_URL + "register";
                sendRequest(request, Request.Method.POST);
            }
        });
    }

    void swapLayoutElements() {
        if (!toVerify) {
            // Change first text input values
            edName.setText("");
            edName.setHint("Type the verification code here");
            lblName.setText(R.string.lbl_codeVerification + ":");

            // Hide second text input
            edEmail.setVisibility(View.INVISIBLE);
            lblEmail.setVisibility(View.INVISIBLE);

            // Change submit button text
            btnRegister.setText(R.string.btn_verify);

        } else {
            // Change first text input values
            edName.setText("");
            edName.setHint("Type your name here");
            lblName.setText(R.string.lbl_name + ":");

            // Show second text input
            edEmail.setVisibility(View.VISIBLE);
            lblEmail.setVisibility(View.VISIBLE);

            // Change submit button text
            btnRegister.setText(R.string.btn_verify);
        }
    }

    void sendRequest(String requestDataURL, int requestMethod) {
        StringRequest request = new StringRequest(requestMethod, requestDataURL,
            new Response.Listener<String>() {
                @Override public void onResponse(String response) {
                    if (toVerify) {
                        /* REGISTRATION */
                        // Get token
                        String token = parseAPIResponse(response);

                        // Save token
                        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString("token", token);
                        ed.apply();

                        Log.d("dky", "Register successful - token: " + prefs.getString("token", "no token"));

                        // Send to Home screen
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    swapLayoutElements();
                    toVerify = !toVerify;
                }
            },
            new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                Log.d("dky", "Error");

                swapLayoutElements();
                toVerify = !toVerify;
            }
            }) {
                @Override protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", edEmail.getText().toString());
                    if (toVerify) {
                        params.put("verify", edName.getText().toString());
                    }

                    return params;
                }
            };
        MyVolley.getInstance(this).add(request);
    }

    String parseAPIResponse(String response) {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<APIResponse<String>>() {}.getType();
        APIResponse<String> apiResponse = gson.fromJson(response, typeToken);

        return apiResponse.getData();
    }
}
