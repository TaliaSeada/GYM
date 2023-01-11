package com.example.gym.homePage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gym.R;
import com.example.gym.messages.MessagesTrainee;
import com.example.gym.workouts.view.WorkoutList;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class HomePageTrainee extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * This class is responsible for the trainee's main page.
     * this class contains a menu in which there are transitions to training,
     * personal information, a system of inquiries and disconnection
     */
    private Toast t;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;
    private boolean clicked = false;

    private ActionBarDrawerToggle toggle;
    private FloatingActionButton contactbtn;
    private FloatingActionButton mailbtn;
    private FloatingActionButton callbtn;
    static int PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_trainee);

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

        // mail button
        mailbtn = findViewById(R.id.send_mail);
        mailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = "talias1235@gmail.com";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
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
        if (ContextCompat.checkSelfPermission(HomePageTrainee.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomePageTrainee.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "0542670780";
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + phone));
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


    //a menu in which there are transitions to training,
    // personal information, a system of inquiries and disconnection
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_train) {
            Intent intent = new Intent(getApplicationContext(), WorkoutList.class);
            intent.putExtra("role", "trainee");
            startActivity(intent);
        } else if (id == R.id.nav_personal_details) {
            startActivity(new Intent(getApplicationContext(), PrivateArea.class));
        } else if (id == R.id.nav_message) {
            startActivity(new Intent(getApplicationContext(), MessagesTrainee.class));
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