package com.example.gym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button=(Button) findViewById(R.id.send);
        button.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this, HomePageTrainee.class);
            startActivity(intent);
        });
    }


}