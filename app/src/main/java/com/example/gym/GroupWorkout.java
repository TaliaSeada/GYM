package com.example.gym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class GroupWorkout extends AppCompatActivity {
    EditText input;
    ImageView add;
    static ListView listView;
    static ArrayList<String> items;
    static ListViewGroupW adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_workout);

        listView = findViewById(R.id.list_item);
        input = findViewById(R.id.Input);
        add = findViewById(R.id.imageMenu); //add
        items = new ArrayList<>();


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
        adapter = new ListViewGroupW(getApplicationContext(), items);
        listView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = input.getText().toString();
                if (text == null || text.length() == 0) {
                    makeToast("Enter item");
                } else {
                    addItem(text);
                    input.setText("");
                    makeToast("added " + text);
                }
            }
        });
        loadContent();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i=new Intent(GroupWorkout.this, AddNewWorkoutTrainee.class);
                startActivity(i);
            }
        });
    }


    public void loadContent() {
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, "list.txt");
        byte[] content = new byte[(int) readFrom.length()];

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(readFrom);
            stream.read(content);

            String s = new String(content);
            // [Apple, Banana, Kiwi, Strawberry]
            s = s.substring(1, s.length() - 1);
            String split[] = s.split(", ");

            // There may be no items in the grocery list.
            if (split.length == 1 && split[0].isEmpty())
                items = new ArrayList<>();
            else items = new ArrayList<>(Arrays.asList(split));

            adapter = new ListViewGroupW(this, items);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected  void onDestroy() {
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, "list.txt"));
            writer.write(items.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public static void  removeItem(int remove){
        items.remove(remove);
        listView.setAdapter(adapter);
    }
    Toast t;
    private  void makeToast(String s){
        if(t!= null) t.cancel();;
        t=Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT);
        t.show();
    }
    public static void addItem(String item){
        items.add(item);
        listView.setAdapter(adapter);
    }
}