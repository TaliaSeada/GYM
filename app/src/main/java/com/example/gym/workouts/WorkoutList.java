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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WorkoutList extends AppCompatActivity implements I_workoutList {
    // set global variable
    static String nameTR;
    // set toast
    private Toast t;
    // set fields for data display
    private EditText input;
    private ImageView add;
    private RecyclerView rview;
    private myAdapter radapter;
    private final ArrayList<Item> ritems = new ArrayList<Item>();
    // get firebase instances
    private static final String TAG = "DBWorkOut";
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email_trainee = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String email_trainer = getTrainee.nameTR;
    private int dragged;
    protected FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    @Override
    protected void onResume() {
        super.onResume();
        String role = getIntent().getStringExtra("role");
        String email;
        if (role.equals("trainee")) {
            email = email_trainee;
        } else {
            email = email_trainer;
        }
        // load content from firebase
        loadContent(email);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);
        String role = getIntent().getStringExtra("role");
        String email;
        if (role.equals("trainee")) {
            email = email_trainee;
        } else {
            email = email_trainer;
        }

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
                            db.collection("user-info").document(email).collection("workouts")
                                    .document(ritems.get(viewHolder.getAbsoluteAdapterPosition()).getName()).delete();
                            recreate();
                            makeToast("Item Removed");
                        }
                    }
                }
        );
        // set list adapter
        radapter = new myAdapter(getApplicationContext(), ritems, new I_recyclerView() {
            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(WorkoutList.this, ExerciseList.class);
                i.putExtra("role", role);
                startActivity(i);
                nameTR = ritems.get(position).getName();
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
                    String text = input.getText().toString();
                    addWO(email, text);
                    input.setText("");
                    makeToast(text + " Added Successfully");
                    radapter.notifyDataSetChanged();
//                  reload content to show the new workout
                    recreate();
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
        // create workout
        Map<String, Object> name = new HashMap<>();
        name.put("name", wo_name);
        // set in firebase
        db.collection("user-info").document(Objects.requireNonNull(email))
                .collection("workouts").document(wo_name).set(name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
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
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);

        Task<HttpsCallableResult> workout = mFunctions.getHttpsCallable("getWorkoutList").call(data);
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
                        if(i[0] < 19){
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