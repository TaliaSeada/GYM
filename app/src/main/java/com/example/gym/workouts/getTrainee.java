package com.example.gym.workouts;

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

import com.example.gym.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class getTrainee extends AppCompatActivity {
    //set global variable
    static String nameTR;
    // create list to save the content read from firebase
    private final List<String> items = new ArrayList<>();
    ListView listView;
    // create list to show only the names from content read from firebase
    private final List<String> names = new ArrayList<>();
    // get firebase instance
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_trainee);
        // load content from firebase
        listView = findViewById(R.id.list);
        loadContent();

        // set on item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(getTrainee.this, WorkoutListTrainer.class);
                startActivity(i);
                nameTR = items.get(pos);
            }
        });
    }

    /***
     * this function load the relevant content from the firebase
     * to the lists we created, sets adapter to the 'names' list
     * in order to show it in the app screen.
     ***/
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
                        // set adapter
                        CustomAdapter customAdapter = new CustomAdapter();
                        ArrayAdapter<String> adap = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_selectable_list_item, names);
                        adap.notifyDataSetChanged();
                        listView.setAdapter(customAdapter);

                    }
                });
    }

    /***
     * this inner class sets the adapter to fit to our data
     ***/
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
