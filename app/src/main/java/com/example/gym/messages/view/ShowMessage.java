package com.example.gym.messages.view;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;



public class ShowMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);
        TextView message = findViewById(R.id.reciveMessage);
        TextView answer = findViewById(R.id.answerMessage);
        Button allMessage = findViewById(R.id.AllMesssage);
        Intent MessageIntent=getIntent();
        String [] MessageValue=MessageIntent.getStringArrayExtra("key_sender");
        message.setText(MessageValue[0]);
        answer.setText(MessageValue[1]);
        allMessage.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), MessagesTrainee.class)));

    }
}