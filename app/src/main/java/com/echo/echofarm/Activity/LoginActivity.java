package com.echo.echofarm.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.echo.echofarm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    EditText editID;
    EditText editPW;
    //추가
    TextView loginErrorText;
    Button loginBtn;
    Button joinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_logintest);

        editID = (EditText) findViewById(R.id.idEditText);
        editPW = (EditText) findViewById(R.id.passwordEditText);

        //추가
        loginErrorText = findViewById(R.id.loginErrorText);
        loginBtn = (Button) findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(this);
        joinBtn = (Button) findViewById(R.id.signupButton);
        joinBtn.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == loginBtn) {
            signIn(editID.getText().toString(), editPW.getText().toString());
        }
        else if(view == joinBtn) {
            Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);
            startActivity(joinIntent);
        }
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Authentication success",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            //테스트 용으로 포스트만드는 인텐트로 이동.
                            Intent makePostIntent = new Intent(LoginActivity.this, MakePostActivity.class);
                            startActivity(makePostIntent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // 추가
                            loginErrorText.setVisibility(View.VISIBLE);
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {//로비로 이동하게끔

    }

    //https://github.com/firebase/snippets-android/blob/4dac0d7756bd9f2af56b8fedc228e7f365f42d88/auth/app/src/main/java/com/google/firebase/quickstart/auth/FirebaseUIActivity.java#L49-L62
}