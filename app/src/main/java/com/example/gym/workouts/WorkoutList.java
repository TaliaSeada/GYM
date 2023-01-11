package com.example.gym.workouts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.R;
import com.example.gym.workouts.interfaces.I_recyclerView;
import com.example.gym.workouts.interfaces.I_workoutList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorkoutList extends AppCompatActivity implements I_workoutList {
    // set global variable
    // set toast
    private Toast t;
    // set fields for data display
    private EditText input;
    private ImageView add;
    private RecyclerView rview;
    private myAdapter radapter;
    private final ArrayList<Item> ritems = new ArrayList<Item>();
    // get firebase instances
    private final String TAG = "DBWorkOut";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private int dragged;
    private final workoutControllet workoutControllet = new workoutControllet();

    @Override
    protected void onResume() {
        super.onResume();
        String email;
        try {
            Intent MessageIntent = getIntent();
            String[] MessageValue = MessageIntent.getStringArrayExtra("key_ex");
            final String role = MessageValue[0];
            if (role.equals("trainee")) {
                email = email_trainee;
            } else {
                email = MessageValue[1];
            }
        } catch (Exception e) {
            email = email_trainee;
        }

        // load content from firebase
        loadContent(email);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);
//        String role = getIntent().getStringExtra("role");
        final String email;
        String email1;
        Intent MessageIntent = getIntent();
        String[] MessageValue = MessageIntent.getStringArrayExtra("key_ex");
        final String role;
        String role1;
        try {
            role1 = MessageValue[0];
            if (role1.equals("trainee")) {
                email1 = email_trainee;
            } else {
                email1 = MessageValue[1];
            }
        } catch (Exception e) {
            email1 = email_trainee;
            role1 = "trainee";
        }
        role = role1;
        email = email1;
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.Callback() {
                    @Override
                    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        return makeMovementFlags(
                                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                ItemTouchHelper.END
                        );
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        dragged = viewHolder.getAbsoluteAdapterPosition();
                        int targetI = target.getAbsoluteAdapterPosition();
                        Collections.swap(ritems, dragged, targetI);
                        radapter.notifyItemMoved(dragged, targetI);
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        if (direction == ItemTouchHelper.END) {
                            String wo_name = ritems.get(viewHolder.getAbsoluteAdapterPosition()).getName();
                            Task<HttpsCallableResult> del_wo = workoutControllet.deleteWorkout(email, wo_name);
                            del_wo.addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                                @Override
                                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                    Map<String, Object> result = (Map<String, Object>) httpsCallableResult.getData();
                                    if (result.containsKey("message")) {
                                        loadContent(email);
                                        makeToast(wo_name + " Deleted Successfully");
                                        Log.d(TAG, (String) result.get("message"));
                                    }

                                    else
                                        Log.d(TAG, "Error deleting document " + result.get("error"));
                                }
                            });
                        }
                    }
                }
        );
        // set list adapter

        radapter = new myAdapter(getApplicationContext(), ritems, new I_recyclerView() {
            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(WorkoutList.this, ExerciseList.class);
                try {
                    i.putExtra("key_ex", new String[]{role, MessageValue[1], ritems.get(position).getName()});
                } catch (Exception e) {
                    i.putExtra("key_ex", new String[]{role, email, ritems.get(position).getName()});
                }

//                i.putExtra("role", role);
                startActivity(i);
//                nameTR = ritems.get(position).getName();
            }
        });
        rview = findViewById(R.id.recyclerView);
        rview.setLayoutManager(new LinearLayoutManager(this));
        helper.attachToRecyclerView(rview);

        // get text
        input = findViewById(R.id.Input);

        // load content from firebase
        loadContent(email);

        // set add button action
        add = findViewById(R.id.imageMenu);
        add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                try {
                    String wo_name = input.getText().toString();
                    addWO(email, wo_name);
                    input.setText("");
                    makeToast(wo_name + " Added Successfully");
                    radapter.notifyDataSetChanged();
                } catch (Exception e) {
                    makeToast("Type Workout Name");
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     * this function adds new workout to the trainee and updates the firebase
     * @param email trainee email
     * @param wo_name the name we insert in the app
     */
    @Override
    public void addWO(String email, String wo_name) {
        Task<HttpsCallableResult> wo = workoutControllet.addWorkout(email, wo_name);
        wo.addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                loadContent(email);
                Log.d(TAG, "Workout successfully added!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }

    /***
     * this function load the relevant content from the firebase
     * to the list we created in order to show it in the app screen.
     ***/
    @Override
    public void loadContent(String email) {
        Task<HttpsCallableResult> workout = workoutControllet.workouts_content(email);
        workout.addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                    int[] im = {R.drawable.im, R.drawable.im2, R.drawable.im3, R.drawable.im4, R.drawable.im5, R.drawable.im6
                            , R.drawable.im7, R.drawable.im8, R.drawable.im9, R.drawable.im10, R.drawable.im11, R.drawable.im12
                            , R.drawable.im13, R.drawable.im14, R.drawable.im15, R.drawable.im16, R.drawable.im17, R.drawable.im18, R.drawable.im19};
                    ritems.clear();
                    ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
                    final int[] i = {0};
                    data.forEach(w -> {
                        ritems.add(new Item((String) w.get("name"), im[i[0]]));
                        if (i[0] < 19) {
                            i[0]++;
                        } else {
                            i[0] = 0;
                        }

                    });
                    radapter.notifyDataSetChanged();
                    rview.setAdapter(radapter);
                } else {
                    Log.w("Get Workouts", task.getException());
                }
            }
        });

    }

    /***
     * this function raises a massage to the screen
     * @param s the massage we want to write on the screen
     */
    @Override
    public void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }
}