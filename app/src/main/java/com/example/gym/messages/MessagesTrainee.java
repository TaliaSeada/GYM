package com.example.gym.messages;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.R;
import com.example.gym.auth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessagesTrainee extends AppCompatActivity {
    /**
     * This class is an application system where the trainee,
     * can send an application to the trainers
     * **/
    private final String TAG = "DBMess";
    private final ManageMessages ManageMessages = new ManageMessages();
    //Defining datasets for extracting the information
    private ListView listView;
    private String email;
    private ArrayList<String> title_array = new ArrayList<String>();
    private ArrayList<String> message_array = new ArrayList<String>();
    private ArrayList<String> date_array = new ArrayList<String>();
    private ArrayList<String> sub_array = new ArrayList<String>();
    private ArrayList<Integer> image_array = new ArrayList<Integer>();
    private ArrayList<String> answer_array = new ArrayList<String>();

    private ImageView refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_messages_trainee);
        listView =(ListView) findViewById(R.id.list_message);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        email = user.getEmail();
        updateMessagesList();
        //plus button
        FloatingActionButton addUserButton = findViewById(R.id.fabAdd);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessagesTrainee.this);
                builder.setTitle("Add Messages");
                View viewInflated = LayoutInflater.from(MessagesTrainee.this).inflate(R.layout.add_new_message_window, (ViewGroup) listView, false);
                // Set up the input box
                final EditText title = (EditText) viewInflated.findViewById(R.id.Title);
                final EditText message = (EditText) viewInflated.findViewById(R.id.message);
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String title_val = title.getText().toString();
                        String message_val = message.getText().toString();
                        // Add the new user to the db
                        ManageMessages.addMessageTrainee(email, message_val, title_val).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                            @Override
                            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                updateMessagesList();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("myTag", e.toString());
                                AlertDialog.Builder builder = new AlertDialog.Builder(MessagesTrainee.this);
                                builder.setMessage("Can't add this Message.\nTry again later");
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
        //data refresh
        refresh = (ImageView) findViewById(R.id.imageRefresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), MessagesTrainee.class));
            }
        });

        //Data transfer between actions
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent senderIntent = new Intent(MessagesTrainee.this, ShowMessage.class);
                senderIntent.putExtra("key_sender", new String[]{message_array.get(pos), answer_array.get(pos)});
                startActivity(senderIntent);
            }
        });
    }

    // Take the users from the db and put them in the view
    public void updateMessagesList(){
        ManageMessages.getMessageTrainee(email).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>(){
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (task.isSuccessful()) {
                            title_array.clear();
                            message_array.clear();
                            date_array.clear();
                            sub_array.clear();
                            answer_array.clear();
                            image_array.clear();
                            ArrayList<HashMap> data = (ArrayList<HashMap>) task.getResult().getData();
                            ArrayList<User> messageList = new ArrayList<>();
                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                           data.forEach(m -> {
                            title_array.add((String) m.get("title"));
                            sub_array.add((String) m.get("trainee"));
                            message_array.add((String) m.get("message"));
                            String answer = (String) m.get("answer");
                            answer_array.add(answer);
//                            Log.d("myTag", answer);
                            //Indicates whether a new message has been received
                            if (answer.isEmpty()){
                                image_array.add(R.drawable.close_mail);
                            }
                            else{
                                image_array.add(R.drawable.open_mail);
                            }
//                            HashMap<String, Integer> d = (HashMap<String, Integer>) m.get("date");
//                            long dateEpoch = d.get("_seconds") * 1000L;
//                            Date date = new Date(dateEpoch);
////                            Date date = (Date) m.get("date");
////                          Date date = document.getTimestamp("date").toDate();
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            String strDate = sdf.format(date.getTime());
                               String strDate = (String) m.get("date");
                               date_array.add(strDate);
                        });
                    }
                    MessageAdapter messageAdapter = new MessageAdapter();
                            listView.setAdapter(messageAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

            }
        });
    }

    // This class displays the messages as a list
    class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return title_array.toArray().length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        //Representation of the data in the app
        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.activity_my_item,null);
            TextView titleMess = (TextView) view.findViewById(R.id.titleMess);
            TextView dateMess = (TextView) view.findViewById(R.id.dateMess);
            ImageView imageViewMail = (ImageView) view.findViewById(R.id.newMail);
            titleMess.setText(title_array.get(i));
            imageViewMail.setImageResource(image_array.get(i));
            dateMess.setText(date_array.get(i));
            return view;
        }
    }
}