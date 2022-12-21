package com.example.gym;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class PrivateAreaTrainer extends AppCompatActivity {
    //personal Details
    EditText input_firstTrainee;
    EditText input_lastTrainee;
    EditText input_weightTrainee;
    EditText input_ageTrainee;
    EditText input_heightTrainee;
    Button Add;
    static String email;

    private static final String TAG = "PrivateAreaT";
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    //create in  Firebase
    public void addDetails(String email,String firstName, String lastName, double height,double age, double weight) {
        Map<String, Object> personalDetails = new HashMap<>();
        personalDetails.put("first name", firstName);
        personalDetails.put("last name", lastName);
        personalDetails.put("height", height);
        personalDetails.put("weight", weight);
        personalDetails.put("age", age);
        db.collection("user-info").document(email).collection("PrivateArea")
                .document("personalDetails").set(personalDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_area_trainee);
        findViewById(R.id.imageBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomePageTrainer.class));
            }
        });
        //
        Add=findViewById(R.id.addP);
        input_ageTrainee=findViewById(R.id.ageTrainee);
        input_heightTrainee=findViewById(R.id.heightTrainee);
        input_weightTrainee=findViewById(R.id.weightTrainee);
        input_firstTrainee=findViewById(R.id.firstTrainee);
        input_lastTrainee=findViewById(R.id.lastTrainee);
        email = user.getEmail();


        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String output_firstName = input_firstTrainee.getText().toString();
                String output_lastName = input_lastTrainee.getText().toString();
                double output_weight = Double.parseDouble(input_weightTrainee.getText().toString());
                double output_height = Double.parseDouble(input_heightTrainee.getText().toString());
                double output_age = Double.parseDouble(input_ageTrainee.getText().toString());
                try{

                    addDetails(email, output_firstName, output_lastName, output_weight, output_height,output_age);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                finish();

            }
        });
        //Connecting the data from Firebase to activity_private_area_trainee.xml
        DocumentReference Reference= db.collection("user-info").document(email).collection("PrivateArea").document("personalDetails");
        Reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    input_firstTrainee.setText(value.getString("first name"));
                    input_lastTrainee.setText(value.getString("last name"));
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


    }
}
