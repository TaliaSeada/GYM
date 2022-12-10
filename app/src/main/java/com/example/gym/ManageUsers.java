package com.example.gym;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.auth.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ManageUsers extends AppCompatActivity {

    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ManageUsers";

    UserManager userManager = new UserManager();
    String role;
    final ArrayList<Map<String, String>> users = new ArrayList<Map<String, String>>();
    SimpleAdapter adapter;


    private void updateUsersList(){
        db.collection("users")
                .whereEqualTo("role", role)
                .get()
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

        if (role.equals("trainer")){
            setTitle("Manage Trainers");
        } else if (role.equals("trainee")){
            setTitle("Manage Trainees");
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
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            users.remove(item);
                            adapter.notifyDataSetChanged();
                            view.setAlpha(1);
                        }
                    });
            }
        });

        FloatingActionButton addUserButton = findViewById(R.id.fab);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

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


}
