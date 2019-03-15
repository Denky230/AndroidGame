package com.stucom.grupo4.settings.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
import com.stucom.grupo4.settings.persistence.SharedPrefsData;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextView lblName;
    private EditText edName;
    private TextView lblEmail;
    private EditText edEmail;
    private Button btnRegister;

    private boolean verifying = false;

    @Override protected void onCreate(Bundle savedInstanceState) {
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
                registrationRequest();
            }
        });
    }

    private void setRegisterLayout() {
        // Change first text input values
        edName.setText("");
        edName.setHint(R.string.hint_name);
        edName.setInputType(InputType.TYPE_CLASS_TEXT);
        lblName.setText(R.string.lbl_name);

        // Show second text input
        edEmail.setVisibility(View.VISIBLE);
        lblEmail.setVisibility(View.VISIBLE);

        // Change submit button text
        btnRegister.setText(R.string.btn_register);

    }
    private void setVerifyingLayout() {
        // Change first text input values
        edName.setText("");
        edName.setHint(R.string.hint_codeVerification);
        edName.setInputType(InputType.TYPE_CLASS_NUMBER);
        lblName.setText(R.string.lbl_codeVerification);

        // Hide second text input
        edEmail.setVisibility(View.INVISIBLE);
        lblEmail.setVisibility(View.INVISIBLE);

        // Change submit button text
        btnRegister.setText(R.string.btn_verify);
    }

    private void registrationRequest() {
        final SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        final SharedPreferences.Editor ed = prefs.edit();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                APIData.API_URL + "register",
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        if (verifying) {
                            // Get token from API
                            String token = parseAPIResponse(response);

                            // Save token to SharedPreferences
                            ed.putString(SharedPrefsData.Keys.TOKEN.name(), token);
                            ed.apply();

                            // Update User data in API
                            userEditRequest();

                            // Send to Home screen
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);

                            // Swap layout
                            setRegisterLayout();

                        } else {
                            // Save User input name to SharedPreferences
                            String name = edName.getText().toString();
                            ed.putString(SharedPrefsData.Keys.USER_NAME.name(), name);
                            ed.apply();

                            // Swap layout
                            setVerifyingLayout();
                        }

                        verifying = !verifying;
                    }
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {}
                }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", edEmail.getText().toString());
                if (verifying) {
                    // Send verification code against API
                    params.put("verify", edName.getText().toString());
                }
                return params;
            }
        };
        MyVolley.getInstance(this).add(request);
    }
    private void userEditRequest() {
        final SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        StringRequest request = new StringRequest(
                Request.Method.PUT,
                APIData.API_URL + "user",
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {}
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {}
                }) {
            @Override protected Map<String, String> getParams() {
                // Get User data from SharedPreferences
                String token = prefs.getString(SharedPrefsData.Keys.TOKEN.name(), "");
                String name = prefs.getString(SharedPrefsData.Keys.USER_NAME.name(), "");

                // Send User data against API
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("name", name);
                return params;
            }
        };
        MyVolley.getInstance(this).add(request);
    }
    private String parseAPIResponse(String response) {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<APIResponse<String>>() {}.getType();
        APIResponse<String> apiResponse = gson.fromJson(response, typeToken);

        return apiResponse.getData();
    }
}