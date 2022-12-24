package com.example.gym;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class GroupWorkoutTrainer extends AppCompatActivity {
    EditText input;
    ImageView add;
    static GridView listView;
    static ListViewGroupTrainer adapter;
    static String nameTR;
    String email = Objects.requireNonNull(AddWorkoutTrainer.nameTR);
    private static final ArrayList<String> items = new ArrayList<>();

    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_workout);

        adapter = new ListViewGroupTrainer(GroupWorkoutTrainer.this, items);
        listView = findViewById(R.id.grid_workout);
        input = findViewById(R.id.Input);
        add = findViewById(R.id.imageMenu); //add

        loadContent();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = items.get(i);
                makeToast(name);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = input.getText().toString();
                AddNewWorkoutTrainee.addWO(email, text);
                if (text == null || text.length() == 0) {
                    makeToast("Enter item");
                } else {
                    addItem(text);
                    input.setText("");
                    makeToast("added " + text);
                }
                loadContent();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(GroupWorkoutTrainer.this, newScreenTrainer.class);
                startActivity(i);
                nameTR = items.get(pos);
            }
        });
    }


    public void loadContent() {
        db.collection("user-info").document(email)
                .collection("workouts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        items.clear();
                        assert documentSnapshots != null;
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            items.add(snapshot.getString("name"));
                        }
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, "list.txt"));
            writer.write(items.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public static void removeItem(int remove) {
        String rem = Objects.requireNonNull(AddWorkoutTrainer.nameTR);
        db.collection("user-info").document(Objects.requireNonNull(rem))
                .collection("workouts").document(items.get(remove)).delete();
        listView.setAdapter(adapter);
    }

    Toast t;

    private void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }

    public static void addItem(String item) {
        items.add(item);
        listView.setAdapter(adapter);
    }
}