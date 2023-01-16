package com.example.gym.messages.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.example.gym.auth.UserController;
import com.example.gym.messages.controller.messagesController;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * This class is an application system where the trainer,
 * can response an application to the trainee
 * **/
public class MessagesTrainer extends AppCompatActivity  {
    private final messagesController ManageMessages = new messagesController();
    private ListView listView;
    private final ArrayList<String> title_array = new ArrayList<>();
    private final ArrayList<String> message_array = new ArrayList<>();
    private final ArrayList<String> id_array = new ArrayList<>();
    private final ArrayList<String> date_array = new ArrayList<>();
    private final ArrayList<String> sub_array = new ArrayList<>();
    private final ArrayList<Integer> image_array = new ArrayList<>();
    private final ArrayList<String> answer_array = new ArrayList<>();
    private final UserController userController = new UserController();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_message_trainer);
        listView = findViewById(R.id.list_message);
        String email = userController.getConnectedUserMail();
        updateMessagesList(email);
        //data refresh
        ImageView refresh = findViewById(R.id.imageRefresh);
        refresh.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(getApplicationContext(), MessagesTrainer.class));
        });

        //Data transfer between departments
        listView.setOnItemClickListener((adapterView, view, pos, l) -> {
            Intent i = new Intent(MessagesTrainer.this, ResponseMessageTrainer.class);
            i.putExtra("key_sender", new String[]{message_array.get(pos), answer_array.get(pos), title_array.get(pos),sub_array.get(pos)});
            //Create the bundle
            Bundle bundle = new Bundle();
            //Add your data to bundle
            bundle.putString("id", id_array.get(pos));

            //Add the bundle to the intent
            i.putExtras(bundle);
            startActivity(i);
        });
    }
    //connection to Firebase

    public void updateMessagesList(String email) {
        ManageMessages.getMessageTrainer(email).addOnCompleteListener(this::onComplete);
    }

    private void onComplete(Task<HttpsCallableResult> task) {
        if (task.isSuccessful()) {
            title_array.clear();
            message_array.clear();
            date_array.clear();
            sub_array.clear();
            image_array.clear();
            answer_array.clear();
            ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
            assert data != null;
            data.forEach(m -> {
                title_array.add((String) m.get("title"));
                sub_array.add((String) m.get("trainee"));
                message_array.add((String) m.get("message"));
                String answer = (String) m.get("answer");
                answer_array.add(answer);
                //Indicates whether a new message has been received
                assert answer != null;
                if (answer.isEmpty()) {
                    image_array.add(R.drawable.close_mail);
                } else {
                    image_array.add(R.drawable.open_mail);
                }
                String strDate = (String) m.get("date");
                date_array.add(strDate);
                ////////////////check////////////////////
                id_array.add((String) m.get("id"));
                ////////////////////////////////////////
            });
            MessageAdapter messageAdapter = new MessageAdapter();
            listView.setAdapter(messageAdapter);
        } else {
            //Defining datasets for extracting the information
            String TAG = "DBMess";
            Log.d(TAG, "Error getting documents: ", task.getException());
        }
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

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.activity_my_item_t,null);
            TextView titleMess = view.findViewById(R.id.titleMess);
            TextView subTitle = view.findViewById(R.id.fromMess);
            TextView dateMess = view.findViewById(R.id.dateMess);
            ImageView imageViewMail = view.findViewById(R.id.newMail);
            titleMess.setText(title_array.get(i));
            subTitle.setText(sub_array.get(i));
            imageViewMail.setImageResource(image_array.get(i));
            dateMess.setText(date_array.get(i));
            return view;
        }

    }
}