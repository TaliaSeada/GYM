package com.example.gym;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MAddNewWorkout extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    Button regBtn = findViewById(R.id.addWorkout);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_add_workout);

        //save data in FireBase on button click
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("workout");



                reference.setValue("change the workout data");
            }
        });
    }
}