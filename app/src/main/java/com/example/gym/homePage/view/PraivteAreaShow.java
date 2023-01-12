package com.example.gym.homePage.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.gym.R;
import com.example.gym.auth.UserController;
import com.example.gym.homePage.controll.privateAreaController;

import java.util.HashMap;

public class PraivteAreaShow extends AppCompatActivity {

    //personal Details
    private TextView input_weightTrainee;
    private TextView input_ageTrainee;
    private TextView input_heightTrainee;
    private TextView full_name_text;
    private TextView input_genderTrainee;

    private final String TAG = "PrivateArea";
    privateAreaController managePrivateArea = new privateAreaController();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praivte_area_show);
        Intent MessageIntent = getIntent();
        String email = MessageIntent.getStringExtra("email");
        input_ageTrainee=(TextView)findViewById(R.id.editTextDate1);
        input_heightTrainee=(TextView)findViewById(R.id.heightTrainee1);
        input_weightTrainee=(TextView)findViewById(R.id.weightTrainee2);
        input_genderTrainee= (TextView) findViewById(R.id.genderTrainee1);
        full_name_text = (TextView) findViewById(R.id.textViewName1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        Log.d(TAG, email);

        managePrivateArea.getPersonalDetails(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> data = (HashMap<String, Object>) task.getResult().getData();
                double height;
                try {
                    height = (Double) data.get("height");
                } catch (Exception e) {
                    height = 0;
                }
                String heightS = String.valueOf(height);
                input_heightTrainee.setText(heightS);
                double weight;
                try {
                    weight = (Double) data.get("weight");
                } catch (Exception e) {
                    Log.d("TAG", e.toString());
                    weight = 0;
                }
                String weightS = String.valueOf(weight);
                input_weightTrainee.setText(weightS);
                String DateBirth;
                try {
                    DateBirth = (String) data.get("dateBirth");
                } catch (Exception e) {
                    DateBirth = "-";
                }
                input_ageTrainee.setText(DateBirth);
                String genderString;
                try {
                    genderString = (String) data.get("gender");
                } catch (Exception e) {
                    genderString = "_";

                }
                input_genderTrainee.setText(genderString);
            }
         });
        managePrivateArea.getName(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap data = (HashMap) task.getResult().getData();
                try {
                    String fullName = (String) data.get("full_name");
                    full_name_text.setText(fullName);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    e.printStackTrace();
                }
//                    });
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }

        });
    }


}
