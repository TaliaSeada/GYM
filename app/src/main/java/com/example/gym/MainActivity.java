package com.example.gym;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configNextButt();
    }
    private void configNextButt(){
        Button trainee = (Button) findViewById(R.id.Trainee);
        trainee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, logIn.class));
            }
        });
    }

    public void disable(View v){
        v.setEnabled(true);
    }
}