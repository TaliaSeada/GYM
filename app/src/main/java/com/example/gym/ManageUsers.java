package com.example.gym;

import static com.example.gym.auth.UserManager.ROLE_MANAGER;
import static com.example.gym.auth.UserManager.ROLE_TRAINEE;
import static com.example.gym.auth.UserManager.ROLE_TRAINER;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.auth.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ManageUsers extends AppCompatActivity {

    private static final String TAG = "ManageUsers";

    UserManager userManager = new UserManager();
    String role;
    final ArrayList<Map<String, String>> users = new ArrayList<Map<String, String>>();
    SimpleAdapter adapter;


    private void updateUsersList(){
        userManager.getUsersByRole(role)
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        users.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final Map<String, String> user = new HashMap<>();
                            user.put("email", document.getId());
                            user.put("fullname", (String) document.getData().get("full_name"));
                            users.add(user);
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
        setContentView(R.layout.activity_manage_users);

        role = (String) getIntent().getExtras().get("role");

        switch (role) {
            case ROLE_TRAINER:
                setTitle("Manage Trainers");
                break;
            case ROLE_TRAINEE:
                setTitle("Manage Trainees");
                break;
            case ROLE_MANAGER:
                setTitle("Manage Managers");
                break;
        }
        final String[] fromMapKey = new String[] {"fullname", "email"};
        final int[] toLayoutId = new int[] {android.R.id.text1, android.R.id.text2};
        adapter = new SimpleAdapter(this, users, android.R.layout.simple_list_item_2, fromMapKey, toLayoutId);


        final ListView listview = (ListView) findViewById(R.id.list_users);
        listview.setAdapter(adapter);
        updateUsersList();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) ((HashMap) parent.getItemAtPosition(position)).get("email");
                onDeleteClick(item);
            }
        });

        FloatingActionButton addUserButton = findViewById(R.id.fab);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ManageUsers.this);
                builder.setTitle("Add User");
                View viewInflated = LayoutInflater.from(ManageUsers.this).inflate(R.layout.add_new_user_window, (ViewGroup) listview, false);
                // Set up the input
                final EditText email = (EditText) viewInflated.findViewById(R.id.email);
                final EditText full_name = (EditText) viewInflated.findViewById(R.id.name);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String email_val = email.getText().toString();
                        String full_name_val = full_name.getText().toString();
                        // Add the new user to the db
                        userManager.createUser(email_val, role, full_name_val).addOnSuccessListener(new OnSuccessListener<Void>() { //what happened if the user added successfully
                            @Override
                            public void onSuccess(Void documentReference) {
                                updateUsersList();
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

    // On click on specific user -> ask ti delete
    public void onDeleteClick(String email) {

        AlertDialog.Builder alert = new AlertDialog.Builder(ManageUsers.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete this user? " + email);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               userManager.deleteUser(email).addOnSuccessListener(new OnSuccessListener<Void>() { //what happened if the user added successfully
                    @Override
                    public void onSuccess(Void documentReference) {
                        updateUsersList();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ManageUsers.this);
                        builder1.setMessage("Can't delete this user.\nTry again later");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = builder1.create();
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
