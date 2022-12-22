package com.example.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Messages extends AppCompatActivity {

    private static final String TAG = "DBMess";
    private ListView listView;
    final ArrayList<Map<String, object_message>> mess = new ArrayList<Map<String, object_message>>();
    SimpleAdapter adapter;
    String email;
    ImageView add;
    ImageView back;
    static ListView listview;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void addMess(String email) {

        db.collection("message")
                .whereEqualTo("trainee", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mess.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final Map<String, object_message> m = new HashMap<>();
                                String title = (String) document.getData().get("title");
                                String id = document.getId();
                                String s = (String) document.getData().get("trainee");
                                String r = (String) document.getData().get("trainer");
                                String b = (String) document.getData().get("message");
                                String a = (String) document.getData().get("answer");
                                Date date = document.getTimestamp("date").toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String strDate = sdf.format(date.getTime());
                                object_message om = new object_message(id,title, s,r,b,a, strDate);
                                m.put("mess",om);
                                mess.add(m);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_messages);
        listView = findViewById(R.id.list_message);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        final String[] fromMapKey = new String[] {"email", "mess"};
        final int[] toLayoutId = new int[] {android.R.id.text1, android.R.id.text2};
        adapter = new SimpleAdapter(this, mess, android.R.layout.simple_list_item_2, fromMapKey, toLayoutId);
        listview = (ListView) findViewById(R.id.list_message);
        listview.setAdapter(adapter);
        addMess(email);
//        back = findViewById(R.id.imageBack);
        add = findViewById(R.id.imageAdd);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mess.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView < ? > adapterView, View view,int pos, long l){
                        //delete

            }
        });


//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                startActivity(new Intent(getApplicationContext(), HomePageTrainee.class));
//            }
//        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewMessage.class));
            }
        });
    }

}