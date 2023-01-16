package com.example.gym.homePage.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.example.gym.R;
import com.example.gym.homePage.controller.privateAreaController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
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
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .8));
        Intent MessageIntent = getIntent();
        String email = MessageIntent.getStringExtra("email");
        input_ageTrainee=(TextView)findViewById(R.id.editTextDate1);
        input_heightTrainee=(TextView)findViewById(R.id.heightTrainee1);
        input_weightTrainee=(TextView)findViewById(R.id.weightTrainee2);
        input_genderTrainee= (TextView) findViewById(R.id.genderTrainee1);
        full_name_text = (TextView) findViewById(R.id.textViewName1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        Log.d(TAG, email);

        // load content from firebase
        loadContent(email);

    }

    public void loadContent(String email) {
        Task<HttpsCallableResult> exe = managePrivateArea.getPersonalDetails(email);
        exe.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> data = (HashMap<String, Object>) task.getResult().getData();
                double height;
                if(data.get("height") instanceof Integer){
                    height = (double)((Integer)(data.get("height")));
                }
                else{
                    height = (double) data.get("height");
                }
                String heightS = String.valueOf(height);
                input_heightTrainee.setText(heightS);
                double weight;
                if(data.get("weight") instanceof Integer){
                    weight = (double)((Integer)(data.get("weight")));
                }
                else{
                    weight = (double) data.get("weight");
                }
                String weightS = String.valueOf(weight);
                input_weightTrainee.setText(weightS);
                String DateBirth;
                DateBirth = (String) data.get("dateBirth");
                input_ageTrainee.setText(DateBirth);
                String genderString;
                genderString = (String) data.get("gender");
                input_genderTrainee.setText(genderString);
            }
         });
        managePrivateArea.getName(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap data = (HashMap) task.getResult().getData();
                    String fullName = (String) data.get("full_name");
                    full_name_text.setText(fullName);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }

        });
    }
}
