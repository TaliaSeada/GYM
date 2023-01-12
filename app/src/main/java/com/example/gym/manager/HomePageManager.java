package com.example.gym.manager;

import static com.example.gym.auth.UserController.ROLE_MANAGER;
import static com.example.gym.auth.UserController.ROLE_TRAINEE;
import static com.example.gym.auth.UserController.ROLE_TRAINER;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.gym.auth.LoginActivity;
import com.example.gym.R;
import com.example.gym.homePage.controller.privateAreaController;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;

public class HomePageManager extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout; //layout that contains the menu
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private privateAreaController pAreaController = new privateAreaController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_manager);

        setTitle("Manager Page");

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.home_manager_page);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //define that the class handle when choose something in menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_manager);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_manage_trainers: {
                Intent intent1=new Intent(HomePageManager.this, ManageUsers.class);
                intent1.putExtra("role", ROLE_TRAINER);
                startActivity(intent1);
                break;
            }
            case R.id.nav_manage_trainees: {
                Intent intent2=new Intent(HomePageManager.this, ManageUsers.class);
                intent2.putExtra("role", ROLE_TRAINEE);
                startActivity(intent2);
                break;
            }
            case R.id.nav_manage_updates: {
                Intent intent3=new Intent(HomePageManager.this, ManageUpdates.class);
                startActivity(intent3);
                break;
            }
            case R.id.nav_manage_managers: {
                Intent intent4=new Intent(HomePageManager.this, ManageUsers.class);
                intent4.putExtra("role", ROLE_MANAGER);
                startActivity(intent4);
                break;
            }
            case R.id.nav_update_contact: {
                openContactUpdate();
                break;
            }
            case R.id.nav_logout: {
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });
                break;
            }
        }
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_manager_page);
        mDrawerLayout.closeDrawers();
        return true;
    }

    void openContactUpdate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePageManager.this);
        builder.setTitle("Edit contact details");
        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
        View viewInflated = LayoutInflater.from(HomePageManager.this).inflate(R.layout.edit_contact_window, view, false);
        // Set up the input box
        final EditText email = (EditText) viewInflated.findViewById(R.id.email);
        final EditText phone_num = (EditText) viewInflated.findViewById(R.id.phone);
        builder.setView(viewInflated);
        pAreaController.getContact().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult result) {
                ArrayList<String> data = (ArrayList<String>) result.getData();
                email.setText(data.get(0));
                phone_num.setText(data.get(1));

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String email_val = email.getText().toString();
                        String phone_val = phone_num.getText().toString();
                        // Add the new user to the db
                        pAreaController.updateContact(email_val, phone_val).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                            @Override
                            public void onSuccess(HttpsCallableResult result) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomePageManager.this);
                                builder.setMessage("Can't update contact details.\nTry again later");
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

                builder.show(); //show the contact update window
            }
        });

    }


}