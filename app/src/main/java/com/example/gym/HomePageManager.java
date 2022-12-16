package com.example.gym;

import static com.example.gym.auth.UserManager.ROLE_MANAGER;
import static com.example.gym.auth.UserManager.ROLE_TRAINEE;
import static com.example.gym.auth.UserManager.ROLE_TRAINER;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomePageManager extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_manager);
        navigationView.setNavigationItemSelectedListener(this);

    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
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
//                Intent intent3=new Intent(HomePageManager.this, ManageUsers.class);
//                intent3.putExtra("role","updates");
//                startActivity(intent3);
                break;
            }
            case R.id.nav_manage_managers: {
                Intent intent4=new Intent(HomePageManager.this, ManageUsers.class);
                intent4.putExtra("role", ROLE_MANAGER);
                startActivity(intent4);
                break;
            }
        }
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_manager_page);
        mDrawerLayout.closeDrawers();
        return true;
    }
}