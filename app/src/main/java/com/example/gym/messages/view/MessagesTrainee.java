package com.example.gym.messages.view;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.example.gym.auth.UserController;
import com.example.gym.messages.controller.messagesController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class







MessagesTrainee extends AppCompatActivity {
    /**
     * This class is an application system where the trainee,
     * can send an application to the trainers
     * **/
    private final String TAG = "DBMess";
    private final messagesController ManageMessages = new messagesController();
    private final UserController userController = new UserController();
    //Defining datasets for extracting the information
    private ListView listView;
    private String email;
    private final ArrayList<String> title_array = new ArrayList<>();
    private final ArrayList<String> message_array = new ArrayList<>();
    private final ArrayList<String> date_array = new ArrayList<>();
    private final ArrayList<Integer> image_array = new ArrayList<>();
    private final ArrayList<String> answer_array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_messages_trainee);
        listView = findViewById(R.id.list_message);
        email = userController.getConnectedUserMail();
        updateMessagesList();
        //plus button
        FloatingActionButton addUserButton = findViewById(R.id.fabAdd);
        addUserButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MessagesTrainee.this);
            builder.setTitle("Add Messages");
            View viewInflated = LayoutInflater.from(MessagesTrainee.this).inflate(R.layout.add_new_message_window, listView, false);
            // Set up the input box
            final EditText title = viewInflated.findViewById(R.id.Title);
            final EditText message = viewInflated.findViewById(R.id.message);
            builder.setView(viewInflated);

            // Set up the buttons
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
                String title_val = title.getText().toString();
                String message_val = message.getText().toString();
                // Add the new user to the db
                ManageMessages.addMessageTrainee(email, message_val, title_val).addOnSuccessListener(httpsCallableResult -> updateMessagesList()).addOnFailureListener(e -> {
                    Log.d("myTag", e.toString());
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MessagesTrainee.this);
                    builder1.setMessage("Can't add this Message.\nTry again later");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Ok", (dialog1, id) -> dialog1.cancel());
                    AlertDialog alertDialog = builder1.create();
                    alertDialog.show();
                });
            });
                    builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

                    builder.show(); //show the add user window
                });


        //data refresh
        ImageView refresh = findViewById(R.id.imageRefresh);
        refresh.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(getApplicationContext(), MessagesTrainee.class));
        });

        //Data transfer between actions
        listView.setOnItemClickListener((adapterView, view, pos, l) -> {
            Intent senderIntent = new Intent(MessagesTrainee.this, ShowMessage.class);
            senderIntent.putExtra("key_sender", new String[]{message_array.get(pos), answer_array.get(pos)});
            startActivity(senderIntent);
        });
    }

    // Take the users from the db and put them in the view
    public void updateMessagesList(){
        ManageMessages.getMessageTrainee(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                        title_array.clear();
                        message_array.clear();
                        date_array.clear();
                        answer_array.clear();
                        image_array.clear();
                        ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
                assert data != null;
                data.forEach(m -> {
                 title_array.add((String) m.get("title"));
                 message_array.add((String) m.get("message"));
                 String answer = (String) m.get("answer");
                 answer_array.add(answer);
//                            Log.d("myTag", answer);
                 //Indicates whether a new message has been received
                    assert answer != null;
                    if (answer.isEmpty()){
                     image_array.add(R.drawable.close_mail);
                 }
                 else{
                     image_array.add(R.drawable.open_mail);
                 }
                    String strDate = (String) m.get("date");
                    date_array.add(strDate);
             });
                MessageAdapter messageAdapter = new MessageAdapter();
                        listView.setAdapter(messageAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

        });
    }

    // This class displays the messages as a list
    class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return title_array.toArray().length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        //Representation of the data in the app
        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.activity_my_item,null);
            TextView titleMess = view.findViewById(R.id.titleMess);
            TextView dateMess = view.findViewById(R.id.dateMess);
            ImageView imageViewMail = view.findViewById(R.id.newMail);
            titleMess.setText(title_array.get(i));
            imageViewMail.setImageResource(image_array.get(i));
            dateMess.setText(date_array.get(i));
            return view;
        }
    }
}