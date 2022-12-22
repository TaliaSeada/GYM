package com.example.gym;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    private static final List<String> names = new ArrayList<>();
    static String nameTR;

    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();


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
                        names.clear();
                        items.clear();
                        assert documentSnapshots != null;
                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            names.add(snapshot.getString("full_name"));
                            items.add(snapshot.getId());
                        }
                        CustomAdapter customAdapter = new CustomAdapter();
                        ArrayAdapter<String> adap = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_selectable_list_item, names);
                        adap.notifyDataSetChanged();
                        //listView.setAdapter(adap);
                        listView.setAdapter(customAdapter);

                    }
                });
    }
    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.toArray().length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ResourceType")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayout,null);
            ImageView imageView= view.findViewById(R.id.imageView2);
            TextView textView = view.findViewById(R.id.textView9);

//            imageView.setImageResource(R.id.imageView2);
            textView.setText(names.get(i));
            return view;
        }
    }
}
