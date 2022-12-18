package com.example.gym;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddWorkoutTrainer extends AppCompatActivity {
    private static final List<String> items = new ArrayList<>();
    static ListView listView;
    static String nameTR;


    private static final String TAG = "WorkOuts";
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
//    protected static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout_trainer);
        listView = findViewById(R.id.list);
        loadContent();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(AddWorkoutTrainer.this, GroupWorkoutTrainer.class);
                startActivity(i);
                nameTR = items.get(pos);
            }
        });
    }

    public void loadContent() {
        db.collection("users").
                whereEqualTo("role", "trainee").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        items.clear();
                        assert documentSnapshots != null;
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            items.add(snapshot.getId());
                        }
                        ArrayAdapter<String> adap = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_selectable_list_item, items);
                        adap.notifyDataSetChanged();
                        listView.setAdapter(adap);
                    }
                });
    }
}
