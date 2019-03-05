package com.stucom.grupo4.settings.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.stucom.grupo4.settings.R;

public class AboutActivity extends AppCompatActivity {

    private final String AUTHOR_NAME = "Denky";
    private TextView txtAppMadeBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Set author name
        txtAppMadeBy = findViewById(R.id.txtAppMadeBy);
        txtAppMadeBy.setText(getString(R.string.txt_appMadeBy, AUTHOR_NAME));
    }
}