package com.example.gym.messages;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Messages extends AppCompatActivity {
    /**
     * This class is an application system where the trainee,
     * can send an application to the trainers
     * **/
    private static final String TAG = "DBMess";
    //Defining datasets for extracting the information
    static ListView listView;
    String email;
    ArrayList<String> title_array = new ArrayList<String>();
    ArrayList<String> message_array = new ArrayList<String>();
    ArrayList<String> date_array = new ArrayList<String>();
    ArrayList<String> sub_array = new ArrayList<String>();
    ArrayList<Integer> image_array = new ArrayList<Integer>();
    ArrayList<String> answer_array = new ArrayList<String>();

    ImageView add;
    ImageView refresh;

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_messages);
        listView =(ListView) findViewById(R.id.list_message);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        email = user.getEmail();
        addMess(email);

        //Added a new message
        add = (ImageView) findViewById(R.id.imageAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewMessage.class));
            }
        });

        //data refresh
        refresh = (ImageView) findViewById(R.id.imageRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), Messages.class));
            }
        });

        //Data transfer between actions
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent senderIntent = new Intent(Messages.this, ShowMessage.class);
                senderIntent.putExtra("key_sender", new String[]{message_array.get(pos), answer_array.get(pos)});
                startActivity(senderIntent);
            }
        });
    }

    //connection to Firebase
    public void addMess(String email) {
        db.collection("message")
                .whereEqualTo("trainee", email).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        title_array.clear();
                        message_array.clear();
                        date_array.clear();
                        sub_array.clear();
                        assert documentSnapshots != null;
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            //Adding the data to the array
                            String title = (String) snapshot.getData().get("title");
                            title_array.add(title);
                            String trainee = (String) snapshot.getData().get("trainee");
                            sub_array.add(trainee);
                            String message = (String) snapshot.getData().get("message");
                            message_array.add(message);
                            String answer = (String) snapshot.getData().get("answer");
                            answer_array.add(answer);

                            //Indicates whether a new message has been received
                            if (answer.isEmpty()){
                                image_array.add(R.drawable.ic_mail);
                            }
                            else{
                                image_array.add(R.drawable.ic_mail_read);
                            }
                            Date date = snapshot.getTimestamp("date").toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String strDate = sdf.format(date.getTime());
                            date_array.add(strDate);
                        }
                        MessageAdapter messageAdapter = new MessageAdapter();
                        listView.setAdapter(messageAdapter);

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
            TextView titleMess = (TextView) view.findViewById(R.id.titleMess);
            TextView dateMess = (TextView) view.findViewById(R.id.dateMess);
            ImageView imageViewMail = (ImageView) view.findViewById(R.id.newMail);
            titleMess.setText(title_array.get(i));
            imageViewMail.setImageResource(image_array.get(i));
            dateMess.setText(date_array.get(i));
            return view;
        }
    }
}