package com.stucom.grupo4.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class SettingsActivity extends AppCompatActivity {

    final int GALLERY_REQUEST = 10;

    private EditText edName;
    private EditText edEmail;
    private ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Assign EditTexts to their R.id
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        imgUser = findViewById(R.id.imgUser);

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
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

        ed.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        // Get data from SharedPreferences
        String name = prefs.getString("name", "");
        String email = prefs.getString("email", "");
        String userImage = prefs.getString("userImage", "");

        // Assign data from SharedPreferences to EditTexts if any
        edName.setText(name);
        edEmail.setText(email);

        imgUser.setImageURI(Uri.parse(userImage));
    }

    @Override
    protected void onPause() {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        // Store variables in SharedPreferences
        String name = edName.getText().toString();
        if (!name.equals(""))
            ed.putString("name", name);

        String email = edEmail.getText().toString();
        if (!email.equals(""))
            ed.putString("email", email);

        ed.commit();
        super.onPause();
    }
}
