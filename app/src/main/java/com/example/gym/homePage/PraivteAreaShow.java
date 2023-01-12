package com.example.gym.homePage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gym.MyDatePickerDialog;
import com.example.gym.R;
import com.example.gym.auth.UserController;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class PraivteAreaShow extends AppCompatActivity {

    //personal Details
    private TextView input_weightTrainee;
    private TextView input_ageTrainee;
    private TextView input_heightTrainee;
    private TextView full_name_text;
    private TextView input_genderTrainee;

    static String email;
    private final String TAG = "PrivateArea";
    private final UserController userController = new UserController();
    privateAreaController managePrivateArea = new privateAreaController();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praivte_area_show);
        email = userController.getConnectedUserMail();
        ImageButton addBirth = (ImageButton) findViewById(R.id.imageButtonAddBirth);
        input_ageTrainee=(TextView)findViewById(R.id.editTextDate1);
        input_heightTrainee=(TextView)findViewById(R.id.heightTrainee1);
        input_weightTrainee=(TextView)findViewById(R.id.weightTrainee2);
        input_genderTrainee= (TextView) findViewById(R.id.genderTrainee1);
        // Create an ArrayAdapter using the string array and a default spinner layout

        managePrivateArea.getName(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap data = (HashMap) task.getResult().getData();
                try {
                    assert data != null;
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
    }


}
