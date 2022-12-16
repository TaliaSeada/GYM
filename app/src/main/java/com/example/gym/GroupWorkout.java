package com.example.gym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupWorkout extends AppCompatActivity {
    EditText input;
    ImageView add;
    static ListView listView;
//    static ArrayList<String> items;
    static ListViewGroupW adapter;
    static String nameTR;

    private static List<String> items = new ArrayList<>();

    private static final String TAG = "WorkOuts";
    protected static FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_workout);

        listView = findViewById(R.id.list_item);
        input = findViewById(R.id.Input);
        add = findViewById(R.id.imageMenu); //add
//        items = new ArrayList<>();

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
//        adapter = new ListViewGroupW(getApplicationContext(), items);
//        listView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = input.getText().toString();
                AddNewWorkoutTrainee.addWO(text);
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
                Intent i = new Intent(GroupWorkout.this, newScrennW.class);
                startActivity(i);
                nameTR = items.get(pos);
            }
        });
    }


    public void loadContent() {
        db.collection("user-info").document(Objects.requireNonNull(user.getEmail()))
                .collection("workouts")
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
//        File path = getApplicationContext().getFilesDir();
//        File readFrom = new File(path, "list.txt");
//        byte[] content = new byte[(int) readFrom.length()];
//
//        FileInputStream stream = null;
//        try {
//            stream = new FileInputStream(readFrom);
//            stream.read(content);
//
//            String s = new String(content);
//            // [Apple, Banana, Kiwi, Strawberry]
//            s = s.substring(1, s.length() - 1);
//            String split[] = s.split(", ");
//
//            // There may be no items in the grocery list.
//            if (split.length == 1 && split[0].isEmpty())
//                items = new ArrayList<>();
//            else items = new ArrayList<>(Arrays.asList(split));
//
//            adapter = new ListViewGroupW(this, items);
//            listView.setAdapter(adapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

    public static void addItem(String item) {
        items.add(item);
        listView.setAdapter(adapter);
    }
}