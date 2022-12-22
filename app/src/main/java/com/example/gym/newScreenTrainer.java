package com.example.gym;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class newScreenTrainer extends AppCompatActivity {
    static ListView listView;
    static ListViewGroupW adapter;
    static String nameExe;

    ImageView add;
    ImageView Back;
    Button DELETE;
    String email = Objects.requireNonNull(AddWorkoutTrainer.nameTR);

    private static List<String> items = new ArrayList<>();

    private static final String TAG = "WorkOuts";
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadContent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_screen_w);
        listView = findViewById(R.id.list_item_in);
        items = new ArrayList<>();

        add = findViewById(R.id.imageMenu);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddNewWorkoutTrainer.class));
            }
        });
        Back = findViewById(R.id.imageBack);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GroupWorkoutTrainer.class));
            }
        });
        loadContent();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = items.get(i);
                makeToast(name);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                makeToast("Remove:" + items.get(i));
                removeItem(i);
                return false;
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(newScreenTrainer.this, exeUpdateTrainer.class);
                startActivity(i);
                nameExe = items.get(pos);
            }
        });

        DELETE = findViewById(R.id.delete2);
        DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("user-info").document(Objects.requireNonNull(email))
                        .collection("workouts").document(GroupWorkout.nameTR).delete();
                loadContent();
                finish();
            }
        });
    }

    public void loadContent() {
        db.collection("user-info").document(Objects.requireNonNull(email))
                .collection("workouts").document(GroupWorkoutTrainer.nameTR)
                .collection("exercises")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        items.clear();
                        for(DocumentSnapshot snapshot : documentSnapshots){
                            items.add(snapshot.getString("name"));
                        }
                        ArrayAdapter<String> adap = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_selectable_list_item, items);
                        adap.notifyDataSetChanged();
                        listView.setAdapter(adap);
                    }
                });

    }

    public static void removeItem(int remove) {
        items.remove(remove);
        listView.setAdapter(adapter);
    }

    Toast t;

    private void makeToast(String s) {
        if (t != null) t.cancel();
        ;
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }


}