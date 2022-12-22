package com.example.gym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageT extends AppCompatActivity  {
    private static final String TAG = "DBMess";
    private ListView listView;
    final ArrayList<Map<String, object_message>> mess = new ArrayList<Map<String, object_message>>();
    SimpleAdapter adapter;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void addMess() {
        // create exercise
        db.collection("message")
                .whereIn("trainer", Arrays.asList("all" , "amit"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mess.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final Map<String, object_message> m = new HashMap<>();
                                //m.put("email", document.getId());
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_message_show);
        listView = findViewById(R.id.list_message);
        final String[] fromMapKey = new String[] {"email", "mess"};
        final int[] toLayoutId = new int[] {android.R.id.text1, android.R.id.text2};
        adapter = new SimpleAdapter(this, mess, android.R.layout.simple_list_item_2, fromMapKey, toLayoutId);
        final ListView listview = (ListView) findViewById(R.id.list_message);
        listview.setAdapter(adapter);
        addMess();

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
                if (mess.get(pos).get("mess").GetStatus())
                {
                Intent i = new Intent(MessageT.this, ResponseMessageTrainer.class);
                    //Create the bundle
                    Bundle bundle = new Bundle();

                    //Add your data to bundle
                    bundle.putString("id", mess.get(pos).get("mess").id);

                    //Add the bundle to the intent
                    i.putExtras(bundle);
                    startActivity(i);
            }
            }
        });

    }

}