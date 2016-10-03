package com.alex.hextest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.at_create_objects)
    public Button btnCreateObjects;

    private TestApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        app = (TestApplication) getApplication();
        //btnCreateObjects = (Button) findViewById(R.id.at_create_objects);

        btnCreateObjects.setOnClickListener(v -> {
            app.createObjects();
        });
    }



}
