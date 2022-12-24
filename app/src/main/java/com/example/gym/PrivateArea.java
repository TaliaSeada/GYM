package com.example.gym;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class PrivateArea extends AppCompatActivity {
    //personal Details
    EditText input_firstTrainee;
    EditText input_lastTrainee;
    EditText input_weightTrainee;
    EditText input_ageTrainee;
    EditText input_heightTrainee;
    EditText input_PhoneNumberPrefix;
    EditText input_PhoneNumber;

    Button Add;
    static String email;

    private static final String TAG = "PrivateAreaT";
    protected  FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public int getAge(int year, int month, int dayOfMonth) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Period.between(
                    LocalDate.of(year, month, dayOfMonth),
                    LocalDate.now()
            ).getYears();
        }
        return 0;
    }
    //create in  Firebase
    public void addDetails(String email,String firstName, String lastName, double height, double weight,double age, String phone) {
        Map<String, Object> personalDetails = new HashMap<>();
        personalDetails.put("first name", firstName);
        personalDetails.put("last name", lastName);
        personalDetails.put("height", height);
        personalDetails.put("weight", weight);
        personalDetails.put("age", age);
        Log.e("MYAPP", "exception: " +email );
        personalDetails.put("phone number", phone);
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



    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_area);
        //
        email = user.getEmail();
        Add=findViewById(R.id.addP);
        input_ageTrainee=findViewById(R.id.ageTrainee);
        input_heightTrainee=findViewById(R.id.heightTrainee);
        input_weightTrainee=findViewById(R.id.weightTrainee);
        input_firstTrainee=findViewById(R.id.firstTrainee);
        input_lastTrainee=findViewById(R.id.lastTrainee);
        input_PhoneNumber=findViewById(R.id.editTextPhone);
        input_PhoneNumberPrefix = findViewById(R.id.editTextPhonePrefix);


        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String output_firstName;
                try {
                    output_firstName= input_firstTrainee.getText().toString();

                } catch (Exception e) {
                    output_firstName="";
                }
                String output_lastName;
                try {
                    output_lastName= input_lastTrainee.getText().toString();

                } catch (Exception e) {
                    output_lastName="";
                }

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
                String phone_prefix =  input_PhoneNumberPrefix.getText().toString();
                String phone =  input_PhoneNumber.getText().toString();

                String output_phone = phone_prefix +phone ;
                try{
                    addDetails(email, output_firstName, output_lastName, output_height ,output_weight, output_age, output_phone);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                finish();

            }
        });
        //Connecting the data from Firebase to activity_private_area_trainee.xml
        DocumentReference Reference= db.collection("user-info").document(email);
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
                    String phoneDB = value.getString("phone number");
                    input_PhoneNumberPrefix.setText(phoneDB.substring(0,3));
                    input_PhoneNumber.setText(phoneDB.substring(4,10));


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }
}
