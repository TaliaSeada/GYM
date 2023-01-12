package com.example.gym.manager;

import static com.example.gym.auth.UserController.ROLE_MANAGER;
import static com.example.gym.auth.UserController.ROLE_TRAINEE;
import static com.example.gym.auth.UserController.ROLE_TRAINER;

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

import com.example.gym.R;
import com.example.gym.auth.User;
import com.example.gym.auth.UserController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// The manager page
public class ManageUsers extends AppCompatActivity {
    private static final String TAG = "ManageUsers";
    private UserController userController = new UserController();
    private String role;
    private final ArrayList<Map<String, String>> users = new ArrayList<Map<String, String>>();
    private SimpleAdapter adapter; // Connect between the view to the list of users

    private void updateUsersList() {
        userController.getUsersByRole(role)
            .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                @Override
                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                    if (task.isSuccessful()) {
                        users.clear();
                        ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
                        ArrayList<User> usersList = new ArrayList<>();
                        data.forEach(d -> usersList.add(new User(d)));

                        for (User userObj : usersList) {
                            final Map<String, String> user = new HashMap<>();
                            user.put("email", userObj.email);
                            user.put("full_name", userObj.full_name);
                            users.add(user);
                        }
                        adapter.notifyDataSetChanged(); // adapter do refresh to the data
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
        // match the users name and email to the screen
        final String[] fromMapKey = new String[] {"full_name", "email"}; // the list of the clients
        final int[] toLayoutId = new int[] {android.R.id.text1, android.R.id.text2};
        adapter = new SimpleAdapter(this, users, android.R.layout.simple_list_item_2, fromMapKey, toLayoutId);

        final ListView listview = (ListView) findViewById(R.id.list_users); //the list from xml
        listview.setAdapter(adapter);
        updateUsersList();

        // when click on user, ask if delete this user
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String email = (String) ((HashMap) parent.getItemAtPosition(position)).get("email");
                onDeleteClick(email); //open window
            }
        });
        //plus button
        FloatingActionButton addUserButton = findViewById(R.id.fab);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ManageUsers.this);
                builder.setTitle("Add User");
                View viewInflated = LayoutInflater.from(ManageUsers.this).inflate(R.layout.add_new_user_window, (ViewGroup) listview, false);
                // Set up the input box
                final EditText email = (EditText) viewInflated.findViewById(R.id.email);
                final EditText full_name = (EditText) viewInflated.findViewById(R.id.name);
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String email_val = email.getText().toString();
                        String full_name_val = full_name.getText().toString();
                        // Add the new user to the db
                        userController.createUser(email_val, role, full_name_val).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() { //what happened if the user added successfully
                            @Override
                            public void onSuccess(HttpsCallableResult result) {
                                updateUsersList();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ManageUsers.this);
                                builder.setMessage("Can't add this user.\nTry again later");
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
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show(); //show the add user window
            }
        });
    }

    // On click on specific user -> ask to delete
    public void onDeleteClick(String email) {

        AlertDialog.Builder alert = new AlertDialog.Builder(ManageUsers.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete this user? " + email);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userController.deleteUser(email).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() { //what happened if the user delete successfully
                    @Override
                    public void onSuccess(HttpsCallableResult result) {
                        updateUsersList();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageUsers.this);
                        builder.setMessage("Can't delete this user.\nTry again later");
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
