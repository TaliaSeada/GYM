package com.example.gym.homePage.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gym.R;
import com.example.gym.auth.LoginActivity;
import com.example.gym.auth.UserController;
import com.example.gym.homePage.controller.privateAreaController;
import com.example.gym.messages.view.MessagesTrainer;
import com.example.gym.workouts.view.getTrainee;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;

public class HomePageTrainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * This class is responsible for the trainer's main page.
     * this class contains a menu in which there are transitions to add train for trainee,
     * a system of inquiries and disconnection
     */
    private Toast t;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;
    private TextView full_name_text;
    private boolean clicked = false;
    private String email;

    private ActionBarDrawerToggle toggle;
    private FloatingActionButton contactbtn;
    private FloatingActionButton mailbtn;
    private FloatingActionButton callbtn;
    static int PERMISSION_CODE = 100;
    private final privateAreaController privateAreaController = new privateAreaController();
    private final UserController userController = new UserController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_trainer);

        // initialize animations
        if (rotateOpen == null) {
            rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        }
        if (rotateClose == null) {
            rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        }
        if (fromBottom == null) {
            fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        }
        if (toBottom == null) {
            toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        }

        // contact button
        contactbtn = findViewById(R.id.contactbtn);
        contactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onContactButtonClick();
            }
        });

//        String mail = "talias1235@gmail.com";
//        String phone = "0542670780";
        final String[] mail = new String[1];
        final String[] phone = new String[1];
        Task<HttpsCallableResult> contact = privateAreaController.getContact();
        contact.addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                ArrayList<String> data = (ArrayList<String>) task.getResult().getData();
                assert data != null;
                mail[0] = (String) data.get(0);
                phone[0] = (String) data.get(1);
            }
        });


        // mail button
        mailbtn = findViewById(R.id.send_mail);
        mailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{mail[0]});
                i.setType("message/rfc822");
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                } else {
                    makeToast("There Is No Application That Support This Action");
                }
            }
        });

        // call button
        callbtn = findViewById(R.id.call);
        if (ContextCompat.checkSelfPermission(HomePageTrainer.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomePageTrainer.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + phone[0]));
                startActivity(i);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = userController.getConnectedUserMail();
        full_name_text= (TextView) findViewById(R.id.name_text);
        //Extracts the name from Firebase to activity_private_area.xml
        privateAreaController.getName(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap data = (HashMap) task.getResult().getData();
                try {
                    assert data != null;
                    String fullName="Hello ";
                    fullName +=(String) data.get("full_name");
                    full_name_text.setText(fullName);
                } catch (Exception e) {
                    Log.d("home page", e.toString());
                    e.printStackTrace();
                }
//                    });
            } else {
                Log.d("home page", "Error getting documents: ", task.getException());
            }

        });
    }


    private void onContactButtonClick() {
        setVisiblity(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        clicked = !clicked;
    }

    private void setVisiblity(boolean clicked) {
        if (!clicked) {
            callbtn.setVisibility(View.VISIBLE);
            mailbtn.setVisibility(View.VISIBLE);
        } else {
            callbtn.setVisibility(View.INVISIBLE);
            mailbtn.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean clicked) {
        if (!clicked) {
            callbtn.startAnimation(fromBottom);
            mailbtn.startAnimation(fromBottom);
            contactbtn.startAnimation(rotateOpen);
        } else {
            callbtn.startAnimation(toBottom);
            mailbtn.startAnimation(toBottom);
            contactbtn.startAnimation(rotateClose);
        }
    }

    private void setClickable(boolean clicked) {
        if (!clicked) {
            callbtn.setClickable(true);
            mailbtn.setClickable(true);
        } else {
            callbtn.setClickable(false);
            mailbtn.setClickable(false);
        }
    }

    public void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //a menu in which there are transitions to add train for trainee,
    //  personal information, a system of inquiries and disconnection

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        if (id == R.id.nav_personal_details){
//          startActivity(new Intent(getApplicationContext(), PrivateArea.class));
//        }
//        else
        if (id == R.id.nav_message) {
            startActivity(new Intent(getApplicationContext(), MessagesTrainer.class));
        } else if (id == R.id.nav_add_train) {
            startActivity(new Intent(getApplicationContext(), getTrainee.class));
        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}