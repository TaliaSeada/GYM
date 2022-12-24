package com.example.gym;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Map;

public class Messages extends AppCompatActivity {
    private static final String TAG = "DBMess";
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
    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_messages);
        listView = findViewById(R.id.list_message);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        email = user.getEmail();
        add = findViewById(R.id.imageAdd);
        addMess(email);
        refresh = findViewById(R.id.imageRefresh);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewMessage.class));
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), Messages.class));
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent senderIntent = new Intent(Messages.this, ShowMessage.class);
                senderIntent.putExtra("key_sender", new String[]{message_array.get(pos), answer_array.get(pos)});
                Intent answerIntent = new Intent(Messages.this, ShowMessage.class);
                answerIntent.putExtra("key_answer",answer_array.get(pos));

                startActivity(senderIntent);
//                startActivity(answerIntent);
            }
        });
    }
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
                            String title = (String) snapshot.getData().get("title");
                            title_array.add(title);
                            String id = snapshot.getId();
                            String s = (String) snapshot.getData().get("trainee");
                            String r = (String) snapshot.getData().get("trainer");
                            sub_array.add(s);
                            String b = (String) snapshot.getData().get("message");
                            message_array.add(b);
                            String a = (String) snapshot.getData().get("answer");
                            answer_array.add(a);
                            if (a.isEmpty()){
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

        @SuppressLint({"ResourceType", "ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.activity_list_my_item,null);
            TextView titleMess = view.findViewById(R.id.titleMess);
//            TextView subTitle = view.findViewById(R.id.subTitle);
            // TextView Messages = view.findViewById(R.id.Messages);
            TextView dateMess = view.findViewById(R.id.dateMess);
            ImageView imageViewMail = view.findViewById(R.id.newMail);
            titleMess.setText(title_array.get(i));
//            subTitle.setText(sub_array.get(i));
             imageViewMail.setImageResource(image_array.get(i));
            //Messages.setText(message_array.get(i));
            dateMess.setText(date_array.get(i));
            return view;
        }
    }
}