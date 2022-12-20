package com.example.gym;

import static android.service.notification.Condition.newId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;

public class NewMessage extends AppCompatActivity {
    private static final String TAG = "DBMess";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button send;
    EditText mes;
    EditText title;
    String email;


        public void addMess(String email, String message , String title) {
        Map<String, Object> exe = new HashMap<>();
        exe.put("trainee", email);
        exe.put("trainer", "all");
        exe.put("message", message);
        exe.put("answer", "");
        exe.put("title", title);
        exe.put("date", Calendar.getInstance().getTime());
        db.collection("message")
                .document()
                .set(exe).
                addOnSuccessListener(new OnSuccessListener<Void>() {
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
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        send = findViewById(R.id.send);
        mes = findViewById(R.id.message);
        title = findViewById(R.id.title);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = mes.getText().toString();
                String title1 = title.getText().toString();
                addMess(email, mess,title1);
                startActivity(new Intent(getApplicationContext(), Messages.class));

            }
        });
    }
}