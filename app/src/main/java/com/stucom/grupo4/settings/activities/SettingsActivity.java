package com.stucom.grupo4.settings.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    final int GALLERY_REQUEST = 10;

    private boolean uploading = false;

    private EditText edName;
    private ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Assign EditTexts to their R.id
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                    // Get selected image Uri
                    Uri imgUserUri = data.getData();
                    // Save Uri into SharedPreferences
                    ed.putString("userImage", String.valueOf(imgUserUri));
                    break;
            }
        }

        ed.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uploading = false;

        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
//
//        // Get data from SharedPreferences
//        String name = prefs.getString("name", "");
//        String userImage = prefs.getString("userImage", "");
//
//        // Assign data from SharedPreferences to EditTexts if any
//        edName.setText(name);
//        imgUser.setImageURI(Uri.parse(userImage));

        // Get data from server
        String requestURL = APIData.API_URL + "user" + "?token=" + prefs.getString("token", "");
        sendRequest(requestURL, Request.Method.GET);
    }

    @Override
    protected void onPause() {
        uploading = true;

        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        // Store data on server
        String requestURL = APIData.API_URL + "user" + "?token=" + prefs.getString("token", "");;
        sendRequest(requestURL, Request.Method.PUT);

        super.onPause();
    }

    void sendRequest(String requestDataURL, int requestMethod) {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        final SharedPreferences.Editor ed = prefs.edit();

        StringRequest request = new StringRequest(requestMethod, requestDataURL,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        if (uploading) {
                            
                        } else {
                            User user = parseAPIResponse(response);

                            // Save user to SharedPreferences
                            ed.putString("name", user.getName());
                            ed.apply();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        Log.d("dky", "Error");
                    }
                }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", edName.toString());
                // TO DO: upload image
//                params.put("image", );

                return params;
            }
        };
        MyVolley.getInstance(this).add(request);
    }

    User parseAPIResponse(String response) {
        Gson gson = new Gson();
        Type typeToken = new TypeToken<APIResponse<User>>() {}.getType();
        APIResponse<User> apiResponse = gson.fromJson(response, typeToken);

        return apiResponse.getData();
    }
}
