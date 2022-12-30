package com.example.gym.homePage;

import static com.example.gym.auth.UserManager.ROLE_MANAGER;
import static com.example.gym.auth.UserManager.ROLE_TRAINEE;
import static com.example.gym.auth.UserManager.ROLE_TRAINER;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym.Manager.HomePageManager;
import com.example.gym.R;
import com.example.gym.auth.UserManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private UserManager userManager = new UserManager();
    private ProgressDialog progressDialog;

    // Launcher of the sign in screen (with google or email)
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
        new FirebaseAuthUIActivityResultContract(),
        new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
            @Override
            public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                // What happened when done with the login screen (success or failed)
                onSignInResult(result);
            }
        }
    );

    // The login screen create (with the login button)
    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when the activity created

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // put the correct xml layout

        Button login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSignInIntent(); // create the sign in page

            }
        });

        // The connecting box for user waiting
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting ...");
        progressDialog.setCancelable(false); // cant cancel it

        // Check if the user already signed in and transfer to the correct page
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            progressDialog.show();
            transferUserToPage();
        }
    }

    // Make google connection page work with- login with google and with email
    public void createSignInIntent() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch page for sign-in intent
     Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                //############LOGO##############
                .setLogo(R.drawable.login)
                //############LOGO##############
                .setTheme(R.style.Theme_GYM)
                .setAvailableProviders(providers)
                .build()
               ;

        signInLauncher.launch(signInIntent);

    }

    // After user try to connect with email or google
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        progressDialog.show();

        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            transferUserToPage();

        } else { // Sign in failed. If response is null the user canceled the sign-in flow using the back button.
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this); // Create alert dialog
            builder.setMessage("Login failed.\nTry again later");
            builder.setCancelable(true);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder.create();
            alert11.show();
            progressDialog.dismiss();
        }
    }

    // If the connect with google or email success
    public void transferUserToPage(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        // check if the user exist
        userManager.getUserDoc(email).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if(doc.exists()){
                    Log.d(TAG,"User exists");
                    String role = (String) doc.getData().get("role");
                    switch (role){
                        case ROLE_MANAGER:
                            Intent intent1=new Intent(LoginActivity.this, HomePageManager.class);
                            startActivity(intent1);
                            break;
                        case ROLE_TRAINER:
                            Intent intent2=new Intent(LoginActivity.this, HomePageTrainer.class);
                            startActivity(intent2);
                            break;
                        case ROLE_TRAINEE:
                            Intent intent3=new Intent(LoginActivity.this, HomePageTrainee.class);
                            startActivity(intent3);
                            break;
                    }
                } else {
                    Log.d(TAG,"User Not exists");
                    delete(); // Delete the user from the firebase auth

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setMessage("The user not exist.\nPlease talk to the manager");
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
                progressDialog.dismiss();
            }
        });
    }

    // Delete user that try to connect and not in the DB from the firebase auth
    public void delete() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // ...
                }
            });
    }
}

