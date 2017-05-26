package com.example.mrr.final_project_mobile_programming.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mrr.final_project_mobile_programming.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    EditText etEmail;
    EditText etPassword;
    Button loginButton;
    Button signButton;

    public static final int PERMISSION_ALL = 1;

    public static final String[] PERMISSION = {

            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };

    static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!hasPermission(this, PERMISSION)) {

            ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_ALL);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("542047351553-qh66jst1ok4and1afm5evnr2ae6698p6.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        signButton = (Button) findViewById(R.id.signButton);
        signButton.setOnClickListener(this);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        etEmail.setText("witek119@gmail.com");
        etPassword.setText("1234567890");
    }

    public static boolean hasPermission(Context context, String[] permissions) {

        for(String permission : permissions) {

            if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, int grantResult) {

        switch (requestCode) {

            case PERMISSION_ALL:

                if(grantResult == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("PERMISSION GRANTED!");
                }

                else
                    System.out.println("PERMISSION DENIED");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.signButton:
                fireBaseCreateAccount();
                break;

            case R.id.loginButton:
                firebaseLogin();
                break;

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();
            Log.v("HANDLER", "LOGGED IN!");
            fireBaseAuthWithGoogle(account);
        } else {

            Toast.makeText(this, "WRONG!", Toast.LENGTH_SHORT).show();
            updateUI(false);
        }
    }

    public void updateUI(boolean update) {

        if(update) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            user = mAuth.getCurrentUser();
                            updateUI(true);
                        } else {

                            updateUI(false);
                        }
                    }
                });
    }


    private void fireBaseCreateAccount() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                            updateUI(true);
                        } else {

                            updateUI(false);
                        }
                    }
                });
    }

    private void firebaseLogin() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            updateUI(true);
                        } else {

                            Toast.makeText(LoginActivity.this, "Wrong Data", Toast.LENGTH_SHORT).show();
                            updateUI(false);
                        }

                        // ...
                    }
                });
    }


}
