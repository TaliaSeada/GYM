package com.example.gym;

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
    UserManager userManager = new UserManager();

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
        new FirebaseAuthUIActivityResultContract(),
        new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
            @Override
            public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                onSignInResult(result);
            }
        }
    );

    // The screen create
    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when the activity created

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Button login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSignInIntent();
            }
        });
//        UserManager userManager = new UserManager();
//        userManager.createUser("bfbfb", "adminnnn");

    }

    // Make google connection page work with- login with google and with email
    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
//                .setLogo(R.drawable.logo)
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = user.getEmail();

            userManager.getUserDoc(email).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot doc) {
                    if(doc.exists()){
                        Log.d(TAG,"User exists");
                        String role = (String) doc.getData().get("role");
                        switch (role){
                            case "manager":
                                Intent intent1=new Intent(LoginActivity.this, HomePageManager.class);
                                startActivity(intent1);
                                break;
                            case "trainer":
                                Intent intent2=new Intent(LoginActivity.this, HomePageTrainer.class);
                                startActivity(intent2);
                                break;
                            case "trainee":
                                Intent intent3=new Intent(LoginActivity.this, HomePageTrainee.class);
                                startActivity(intent3);
                                break;
                        }
                    } else {
                        Log.d(TAG,"User Not exists");
                        delete(); // Delete the user from the firebase auth

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(findViewById(android.R.id.content).getContext());
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
                }
            });
        } else {

            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.

            AlertDialog.Builder builder1 = new AlertDialog.Builder(findViewById(android.R.id.content).getContext());
            builder1.setMessage("Login failed.\nTry again later");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    // ...
                }
            });
    }

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

