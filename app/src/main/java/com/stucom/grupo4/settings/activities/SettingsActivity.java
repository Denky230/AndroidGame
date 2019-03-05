package com.stucom.grupo4.settings.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stucom.grupo4.settings.MyVolley;
import com.stucom.grupo4.settings.R;
import com.stucom.grupo4.settings.constants.APIData;
import com.stucom.grupo4.settings.persistence.SharedPrefsData;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST = 10;

    private EditText edName;
    private ImageView imgUser;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edName = findViewById(R.id.edName);
        imgUser = findViewById(R.id.imgUser);

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
    }
    @Override protected void onResume() {
        super.onResume();

        // Update input fields from SharedPreferences data
        updateInputFieldsFromUserData();
    }
    @Override protected void onPause() {
        // Save input fields data to SharedPreferences + API
        updateUserDataFromInputFields();
        userEditRequest();

        super.onPause();
    }
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                    // Get selected image Uri
                    Uri imgUserUri = data.getData();
                    // Save Uri into SharedPreferences
                    ed.putString(SharedPrefsData.Keys.USER_IMAGE.name(), String.valueOf(imgUserUri));
                    break;
            }
        }

        ed.apply();
    }

    private void updateInputFieldsFromUserData() {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        edName.setText(prefs.getString(SharedPrefsData.Keys.USER_NAME.name(), ""));
    }
    private void updateUserDataFromInputFields() {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(SharedPrefsData.Keys.USER_NAME.name(), edName.getText().toString());
        ed.apply();
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

                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("name", name);
                return params;
            }
        };
        MyVolley.getInstance(this).add(request);
    }
}