package com.example.gym;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowMessage extends AppCompatActivity {
    TextView Message;
    TextView Answer;
    Button allMessage;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);
        Message=findViewById(R.id.reciveMessage);
        Answer=findViewById(R.id.answerMessage);
        allMessage=findViewById(R.id.AllMesssage);
        Intent MessageIntent=getIntent();
        String [] MessageValue=MessageIntent.getStringArrayExtra("key_sender");
        Message.setText(MessageValue[0]);
//        Intent AnswerIntent=getIntent();
//        String AnswerValue=AnswerIntent.getStringExtra("key_answer");
        Answer.setText(MessageValue[1]);
        allMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Messages.class));
            }
        });

    }
}