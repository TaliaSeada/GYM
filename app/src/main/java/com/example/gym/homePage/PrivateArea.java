package com.example.gym.homePage;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.MyDatePickerDialog;
import com.example.gym.R;

import com.example.gym.auth.UserController;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;


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
    static String email;
    String date;
    private final String TAG = "PrivateArea";
    private final UserController userController = new UserController();
    privateAreaController managePrivateArea = new privateAreaController();

    //update data in  Firebase
    public void addDetails(String email, double height, double weight) {
        managePrivateArea.addDetails(email,height,weight);
    }
    public void addDate(String date) {
        managePrivateArea.addDate(email,date);
    }
    public void addGender(String gender) {
        managePrivateArea.addGender(email, gender);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_area);
        email = userController.getConnectedUserMail();
        Button add = (Button) findViewById(R.id.addP);
        ImageButton addBirth = (ImageButton) findViewById(R.id.imageButtonAddBirth);
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
        addBirth.setOnClickListener(view -> {

            dialog.setTitle("Set Date");
            dialog.showDatePicker((view1, year, month, dayOfMonth) -> {
                date = year + "/" + month + "/" + dayOfMonth;
                input_ageTrainee.setText(date);
                addDate(date);

            }, Calendar.getInstance());
        });
        add.setOnClickListener(view -> {

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
        });
        managePrivateArea.getPersonalDetails(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> data = (HashMap<String, Object>) task.getResult().getData();
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
                assert genderString != null;
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

        });
        //Extracts the name from Firebase to activity_private_area.xml
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
}
