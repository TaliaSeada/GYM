package com.example.gym;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class MessageT extends AppCompatActivity  {
    //Defining datasets for extracting the information
    private static final String TAG = "DBMess";
    private ListView listView;
    String email;
    ArrayList<String> title_array = new ArrayList<String>();
    ArrayList<String> message_array = new ArrayList<String>();
    ArrayList<String> id_array = new ArrayList<String>();
    ArrayList<String> date_array = new ArrayList<String>();
    ArrayList<String> sub_array = new ArrayList<String>();
    ArrayList<Integer> image_array = new ArrayList<Integer>();
    ArrayList<String> answer_array = new ArrayList<String>();

    ImageView refresh;

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void addMess(String email) {
        //connection to Firebase
        db.collection("message")
                .whereIn("trainer", Arrays.asList("all" , email))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            id_array.add(id);
                            String s = (String) snapshot.getData().get("trainee");
                            sub_array.add(s);
                            String b = (String) snapshot.getData().get("message");
                            message_array.add(b);
                            String a = (String) snapshot.getData().get("answer");
                            answer_array.add(a);
                            System.out.println(a);
                            //Updated when the trainer responded to the message

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
                        MessageT.MessageAdapter messageAdapter = new MessageT.MessageAdapter();
                        listView.setAdapter(messageAdapter);

                    }
                });
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_message_show);
        listView = findViewById(R.id.list_message);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        email = user.getEmail();
        addMess(email);

        //data refresh
        refresh = findViewById(R.id.imageRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), Messages.class));
            }
        });

        //Data transfer between departments
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView < ? > adapterView, View view,int pos, long l){
                Intent i = new Intent(MessageT.this, ResponseMessageTrainer.class);
                    i.putExtra("key_sender", new String[]{message_array.get(pos), answer_array.get(pos)});
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

//        DocumentReference Reference=  db.collection("message")
//                .whereIn("trainer", Arrays.asList("all" , email)).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                try {
//                    answer_array.get().setText(value.getString("first name"));
//                    input_lastTrainee.setText(value.getString("last name"));
//                    double height = (Double) value.get("height");
//                    String heightS=String.valueOf(height);
//                    input_heightTrainee.setText(heightS);
//                    double weight = (Double) value.get("weight");
//                    String weightS=String.valueOf(weight);
//                    input_weightTrainee.setText(weightS);
//                    double age = (Double) value.get("age");
//                    String ageS=String.valueOf(age);
//                    input_ageTrainee.setText(ageS);
//                    String phoneDB = value.getString("phone number");
//                    input_PhoneNumberPrefix.setText(phoneDB.substring(0,3));
//                    input_PhoneNumber.setText(phoneDB.substring(4,10));
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });

    }
}