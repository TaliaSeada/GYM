package com.example.gym;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

/**
 * This department manages the personal details,
 * Each trainee will be able to update his age, weight, and height at any stage
 * **/

public class PrivateArea extends AppCompatActivity {
    //personal Details
    EditText input_weightTrainee;
    EditText input_ageTrainee;
    EditText input_heightTrainee;
    TextView full_name_text;
    Button Add;
    static String email;

    private static final String TAG = "PrivateArea";
    protected  FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//    public int getAge(int year, int month, int dayOfMonth) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return Period.between(
//                    LocalDate.of(year, month, dayOfMonth),
//                    LocalDate.now()
//            ).getYears();
//        }
//        return 0;
//    }


    //update data in  Firebase
    public void addDetails(String email, double height, double weight,double age) {
        Map<String, Object> personalDetails = new HashMap<>();
        personalDetails.put("height", height);
        personalDetails.put("weight", weight);
        personalDetails.put("age", age);
        db.collection("user-info").document(email)
                .set(personalDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_area);
        email = user.getEmail();
        Add=(Button) findViewById(R.id.addP);
        input_ageTrainee=(EditText)findViewById(R.id.ageTrainee);
        input_heightTrainee=(EditText)findViewById(R.id.heightTrainee);
        input_weightTrainee=(EditText)findViewById(R.id.weightTrainee);
        full_name_text= (TextView) findViewById(R.id.textViewName);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double output_weight;
                try{
                    output_weight = Double.parseDouble(input_weightTrainee.getText().toString());
                } catch (NumberFormatException e) {
                    output_weight = 0;
                }
                double output_height;
                try{
                    output_height = Double.parseDouble(input_heightTrainee.getText().toString());
                } catch (NumberFormatException e) {
                    output_height = 0;
                }
                double output_age;
                try{
                    output_age = Double.parseDouble(input_ageTrainee.getText().toString());
                } catch (NumberFormatException e) {
                    output_age = 0;
                }

                try{
                    addDetails(email, output_height ,output_weight, output_age);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                finish();

            }
        });
        //Extracts the data from Firebase to activity_private_area.xml
        DocumentReference Reference= db.collection("user-info").document(email);
        Reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    double height = (Double) value.get("height");
                    String heightS=String.valueOf(height);
                    input_heightTrainee.setText(heightS);
                    double weight = (Double) value.get("weight");
                    String weightS=String.valueOf(weight);
                    input_weightTrainee.setText(weightS);
                    double age = (Double) value.get("age");
                    String ageS=String.valueOf(age);
                    input_ageTrainee.setText(ageS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        //Extracts the name from Firebase to activity_private_area.xml
        DocumentReference name= db.collection("users").document(email);
        name.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    String fullName = (String) value.get("full_name");
                    full_name_text.setText(fullName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
