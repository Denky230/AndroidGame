package com.stucom.grupo4.settings.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.stucom.grupo4.settings.model.User;
import com.stucom.grupo4.settings.persistence.SharedPrefsData;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int[] BUTTONS_ID = new int[] {
            R.id.btnPlay, R.id.btnRanking, R.id.btnSettings,
            R.id.btnAbout, R.id.btnUnregister
    };
    private final List<Integer> LOGIN_RESTRICTED_BUTTONS_ID = Arrays.asList(
            R.id.btnPlay, R.id.btnRanking, R.id.btnSettings, R.id.btnUnregister
    );

    private Button btnRegister;
    private TextView txtGreeting;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtGreeting = findViewById(R.id.txtUserGreeting);

        for (int id : BUTTONS_ID) {
            Button button = findViewById(id);
            button.setOnClickListener(this);
            if (LOGIN_RESTRICTED_BUTTONS_ID.contains(id)) {
                button.setEnabled(false);
            }
        }
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
    }
    @Override protected void onResume() {
        super.onResume();

        // Check if user is registered
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String token = prefs.getString(SharedPrefsData.Keys.TOKEN.name(), "");
        if (token.equals("")) {
            /* USER NOT REGISTERED */
            // Set greeting message
            String message = getString(R.string.txt_greeting, getString(R.string.txt_greeting_default));
            txtGreeting.setText(message);
        } else {
            /* USER REGISTERED */
            // Request User data from API
            String requestURL = APIData.API_URL + "user?token=" + token;
            userGetRequest(requestURL);

            // Unlock login restricted buttons
            for (int id : LOGIN_RESTRICTED_BUTTONS_ID) {
                findViewById(id).setEnabled(true);
            }
            // Disable login button
            btnRegister.setEnabled(false);
        }
    }

    @Override public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.btnPlay:
                intent = new Intent(MainActivity.this, WormyActivity.class);
                break;
            case R.id.btnRanking:
                intent = new Intent(MainActivity.this, RankingActivity.class);
                break;
            case R.id.btnSettings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                break;
            case R.id.btnAbout:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                break;
            case R.id.btnRegister:
                intent = new Intent(MainActivity.this, RegisterActivity.class);
                break;
            case R.id.btnUnregister:
                logOutAskingForAccountDeletion();
                return;
            default:
                return;
        }
        startActivity(intent);
    }

    private void logOutAskingForAccountDeletion() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean accountDeletion = false;
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        accountDeletion = true;
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }

                logOut(accountDeletion);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.txt_deleteAccountData)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }
    private void logOut(boolean accountDeletion) {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        String token = prefs.getString(SharedPrefsData.Keys.TOKEN.name(), "");

        // Request API logout
        String requestURL = APIData.API_URL + "unregister"
                + "token=" + token
                + "must_delete=" + String.valueOf(accountDeletion);
        logoutRequest(requestURL);

        // Erase token from phone
        ed.remove(SharedPrefsData.Keys.TOKEN.name());
        ed.apply();

        // Send to Register screen
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void userGetRequest(String requestDataURL) {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        final SharedPreferences.Editor ed = prefs.edit();

        Log.d("dky", requestDataURL);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                requestDataURL,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        Log.d("dky", response);

                        // Get User data from API
                        User user = parseAPIResponse(response);
                        String name = user.getName();

                        // Save User data to SharedPreferences
                        ed.putString(SharedPrefsData.Keys.USER_NAME.name(), name);
                        ed.apply();

                        // Set greeting message
                        String message = getString(R.string.txt_greeting, name);
                        txtGreeting.setText(message);
                    }
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {}
                }
        );
        MyVolley.getInstance(this).add(request);
    }
    private void logoutRequest(final String requestDataURL) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                requestDataURL,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {}
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {}
                }
        );
        MyVolley.getInstance(this).add(request);
    }

    private User parseAPIResponse(String response) {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<APIResponse<User>>() {}.getType();
        APIResponse<User> apiResponse = gson.fromJson(response, typeToken);

        return apiResponse.getData();
    }
}