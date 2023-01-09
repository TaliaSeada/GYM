package com.example.gym.messages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.example.gym.auth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class MessagesTrainer extends AppCompatActivity  {
    /**
     * This class is an application system where the trainer,
     * can response an application to the trainee
     * **/
    //Defining datasets for extracting the information
    private static final String TAG = "DBMess";
    private ManageMessages ManageMessages = new ManageMessages();
    private ListView listView;
    private String email;
    private ArrayList<String> title_array = new ArrayList<String>();
    private ArrayList<String> message_array = new ArrayList<String>();
    private ArrayList<String> id_array = new ArrayList<String>();
    private ArrayList<String> date_array = new ArrayList<String>();
    private ArrayList<String> sub_array = new ArrayList<String>();
    private ArrayList<Integer> image_array = new ArrayList<Integer>();
    private ArrayList<String> answer_array = new ArrayList<String>();

    private ImageView refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_message_trainer);
        listView = (ListView) findViewById(R.id.list_message);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        email = user.getEmail();
        updateMessagesList(email);
        //data refresh
        refresh = (ImageView) findViewById(R.id.imageRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), MessagesTrainer.class));
            }
        });

        //Data transfer between departments
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView < ? > adapterView, View view,int pos, long l){
                Intent i = new Intent(MessagesTrainer.this, ResponseMessageTrainer.class);
                i.putExtra("key_sender", new String[]{message_array.get(pos), answer_array.get(pos), title_array.get(pos)});
                //Create the bundle
                Bundle bundle = new Bundle();
                //Add your data to bundle
                bundle.putString("id", id_array.get(pos));

                //Add the bundle to the intent
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }
    //connection to Firebase

    public void updateMessagesList(String email) {
        ManageMessages.getMessageTrainer(email).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    title_array.clear();
                    message_array.clear();
                    date_array.clear();
                    sub_array.clear();
                    image_array.clear();
                    answer_array.clear();
                    ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
                    ArrayList<User> messageList = new ArrayList<>();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        data.forEach(m -> {
                            title_array.add((String) m.get("title"));
                            sub_array.add((String) m.get("trainee"));
                            message_array.add((String) m.get("message"));
                            String answer = (String) m.get("answer");
                            answer_array.add(answer);
                            Log.d("myTag", answer);
                            //Indicates whether a new message has been received
                            if (answer.isEmpty()) {
                                image_array.add(R.drawable.close_mail);
                            } else {
                                image_array.add(R.drawable.open_mail);
                            }
                            Date date = (Date) m.get("date");
//                                Date date = document.getTimestamp("date").toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String strDate = sdf.format(date.getTime());
                            date_array.add(strDate);
                            ////////////////check////////////////////
                            id_array.add((String) m.get("id"));
                            ////////////////////////////////////////
                        });
                    }
                    MessageAdapter messageAdapter = new MessageAdapter();
                    listView.setAdapter(messageAdapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
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

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.activity_my_item_t,null);
            TextView titleMess = (TextView) view.findViewById(R.id.titleMess);
            TextView subTitle = (TextView) view.findViewById(R.id.fromMess);
            TextView dateMess = (TextView) view.findViewById(R.id.dateMess);
            ImageView imageViewMail = (ImageView) view.findViewById(R.id.newMail);
            titleMess.setText(title_array.get(i));
            subTitle.setText(sub_array.get(i));
            imageViewMail.setImageResource(image_array.get(i));
            dateMess.setText(date_array.get(i));
            return view;
        }

    }
}