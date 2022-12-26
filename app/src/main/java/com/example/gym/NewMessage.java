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
/**
 * This class is the trainee,
 * can send message to the trainer
 * **/
public class NewMessage extends AppCompatActivity {
    private static final String TAG = "DBMess";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button send;
    EditText mes;
    EditText title;
    String email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        send = (Button) findViewById(R.id.send);
        mes = (EditText) findViewById(R.id.message);
        title = (EditText) findViewById(R.id.title);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = mes.getText().toString();
                String title1 = title.getText().toString();
                addMess(email, mess,title1);
                finish();
            }
        });
    }
    public void addMess(String email, String message , String title) {
        Map<String, Object> message_map = new HashMap<>();
        message_map.put("trainee", email);
        message_map.put("trainer", "all");
        message_map.put("message", message);
        message_map.put("answer", "");
        message_map.put("title", title);
        message_map.put("date", Calendar.getInstance().getTime());
        db.collection("message")
                .document()
                .set(message_map).
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
}

