package com.example.gym.homePage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.MyDatePickerDialog;
import com.example.gym.R;
import com.example.gym.auth.User;
import com.example.gym.messages.MessagesTrainee;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This department manages the personal details,
 * Each trainee will be able to update his age, weight, and height at any stage
 * **/

public class PrivateArea extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //personal Details
    private EditText input_weightTrainee;
    private EditText input_ageTrainee;
    private EditText input_heightTrainee;
    private TextView full_name_text;
    private Button Add;
    private ImageButton AddBirth;
    static String email;
    String date;
    private static final String TAG = "PrivateArea";
    protected  FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ManagePrivateArea managePrivateArea = new ManagePrivateArea();

    //update data in  Firebase
    public void addDetails(String email, double height, double weight) {
        managePrivateArea.addDetails(email,height,weight);
//        DocumentReference docRef = db.collection("user-info").document(email);
//
//        docRef.update("height", height);
//        docRef.update("weight", weight);
    }
    public void addDate(String date) {
        managePrivateArea.addDate(email,date);
//        DocumentReference docRef = db.collection("user-info").document(email);
//        // (async) Update one field
//        docRef.update("dateBirth", date);
    }
    public void addGender(String gender) {
        managePrivateArea.addGender(email, gender);
//        DocumentReference docRef = db.collection("user-info").document(email);
//        // (async) Update one field
//        docRef.update("gender", gender);
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_area);
        email = user.getEmail();
        Add=(Button) findViewById(R.id.addP);
        AddBirth=(ImageButton) findViewById(R.id.imageButtonAddBirth);
        input_ageTrainee=(EditText)findViewById(R.id.editTextDate);
        input_heightTrainee=(EditText)findViewById(R.id.heightTrainee);
        input_weightTrainee=(EditText)findViewById(R.id.weightTrainee);
        full_name_text= (TextView) findViewById(R.id.textViewName);
        MyDatePickerDialog dialog = new MyDatePickerDialog(this);
        Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
//        input_ageTrainee.setText(date);
        AddBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.setTitle("Set Date");
                dialog.showDatePicker(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(dayOfMonth);
                        input_ageTrainee.setText(date);
                        addDate(date);

                    }

                }, Calendar.getInstance());
            }
        });
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


                try{
                    addDetails(email, output_height ,output_weight);
                } catch (NullPointerException e){
                    e.printStackTrace();
                }

                finish();
            }
        });
        managePrivateArea.getPersonalDetails(email).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>(){
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    HashMap data = (HashMap) task.getResult().getData();
//                    data.forEach(m -> {
                    double height;
                try {
                    height = (Double) data.get("height");
                } catch (Exception e) {
                    height=0;
                }
                String heightS=String.valueOf(height);
                input_heightTrainee.setText(heightS);
                double weight;
                try {
                    weight = (Double) data.get("weight");
                } catch (Exception e) {
                    Log.d("TAG", e.toString());
                    weight=0;
                }
                String weightS=String.valueOf(weight);
                input_weightTrainee.setText(weightS);
                String DateBirth;
                try {
                    DateBirth = (String) data.get("dateBirth");
                } catch (Exception e) {
                    DateBirth="";
                }
                input_ageTrainee.setText(DateBirth);
                String genderString;
                try {
                    genderString = (String) data.get("gender");
                } catch (Exception e) {
                    genderString="Gender";
                    Log.d("myTag", genderString);
                }
                String[] baths = getResources().getStringArray(R.array.planets_array);
                try {
                    if (genderString.equals("female"))
                        spinner.setSelection(Arrays.asList(baths).indexOf("female"));

                    else {
                        spinner.setSelection(Arrays.asList(baths).indexOf("male"));

                    }
                } catch (Exception e) {
                    spinner.setSelection(Arrays.asList(baths).indexOf("Gender"));
                }
//            });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });
        //Extracts the name from Firebase to activity_private_area.xml
        managePrivateArea.getName(email).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>(){
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    HashMap data = (HashMap) task.getResult().getData();
                    Log.d(TAG,data.toString());
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

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        if (!text.equals("Gender"))
           addGender(text);
//        Toast.makeText(parent.getContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void birthDate(){
//        final String[] data = new String[1];
        MyDatePickerDialog dialog = new MyDatePickerDialog(this);
        dialog.setTitle("Set Date");
        dialog.showDatePicker(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(dayOfMonth);
                Log.d("myTag", String.valueOf(year));
                Log.d("myTag", String.valueOf(month));
                Log.d("myTag", String.valueOf(dayOfMonth));
            }

        }, Calendar.getInstance());
    }
}
