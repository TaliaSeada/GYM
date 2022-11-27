package com.example.gym;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button=(Button) findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, HomePageTrainee.class);
                startActivity(intent);
            }
        });
//        getSupportActionBar().hide();
//        int SPLASH_TIME_OUT = 3000;
//        new Handler().postDelayed(() -> {
//            // This method will be executed once the timer is over
//            // Start your app main activity
//            Intent i = new Intent(MainActivity.this, HomePage.class);
//            startActivity(i);
//            // close this activity
//            finish();
//        }, SPLASH_TIME_OUT);
    }


}