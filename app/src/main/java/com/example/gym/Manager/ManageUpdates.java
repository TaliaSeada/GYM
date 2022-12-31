package com.example.gym.Manager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManageUpdates extends AppCompatActivity {

    final ArrayList<Map<String, String>> updates = new ArrayList<Map<String, String>>();
    SimpleAdapter adapter;
    private static final String TAG = "ManageUpdates";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Take the updates from the db and put them in the view
    private void updateUpdatesList(){
        db.collection("updates").orderBy("date", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            updates.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Update updateObj = document.toObject(Update.class);

                                final Map<String, String> update = new HashMap<>();
                                update.put("id", document.getId());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                update.put("date", simpleDateFormat.format(updateObj.date).toString());
                                update.put("content", updateObj.content);
                                updates.add(update);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_updates);
        setTitle("Manage Updates");

        final String[] fromMapKey = new String[] {"content", "date"};
        final int[] toLayoutId = new int[] {android.R.id.text1, android.R.id.text2};
        adapter = new SimpleAdapter(this, updates, android.R.layout.simple_list_item_2, fromMapKey, toLayoutId);


        final ListView listview = (ListView) findViewById(R.id.list_updates);
        listview.setAdapter(adapter);
        updateUpdatesList();

        // when click on update, ask if delete this update
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) ((HashMap) parent.getItemAtPosition(position)).get("id");
                onDeleteClick(item);
            }
        });

        FloatingActionButton addUpdateButton = findViewById(R.id.fab);
        addUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ManageUpdates.this);
                builder.setTitle("Add Update");
                View viewInflated = LayoutInflater.from(ManageUpdates.this).inflate(R.layout.add_new_update_window, (ViewGroup) listview, false);
                // Set up the input
                final DatePicker date = (DatePicker) viewInflated.findViewById(R.id.datePicker);
                final EditText content = (EditText) viewInflated.findViewById(R.id.content);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Calendar c = new GregorianCalendar();
                        c.set(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59,59 );
                        Date date_val = c.getTime();
                        String content_val = content.getText().toString();

                        Update newUpdate = new Update(date_val, content_val);

                        // Add the new update to the db
                        db.collection("updates").document().set(newUpdate).addOnSuccessListener(new OnSuccessListener<Void>() { //what happened if the user added successfully
                            @Override
                            public void onSuccess(Void documentReference) {
                                updateUpdatesList();
                            }
                        });

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }
    // On click on specific update -> ask to delete
    public void onDeleteClick(String updateId) {

        AlertDialog.Builder alert = new AlertDialog.Builder(ManageUpdates.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete this update? ");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("updates").document(updateId).delete().addOnSuccessListener(new OnSuccessListener<Void>() { //what happened if the update delete successfully
                    @Override
                    public void onSuccess(Void documentReference) {
                        updateUpdatesList();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageUpdates.this);
                        builder.setMessage("Can't delete this update.\nTry again later");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

}
