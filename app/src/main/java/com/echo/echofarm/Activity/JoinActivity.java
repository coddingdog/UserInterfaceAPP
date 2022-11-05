package com.echo.echofarm.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.echo.echofarm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;

    EditText editID;
    EditText editPW;
    Button joinBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_join);

        editID = (EditText) findViewById(R.id.signupID);
        editPW = (EditText) findViewById(R.id.signupPassword);

        joinBtn = (Button) findViewById(R.id.signup_okButton);
        joinBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == joinBtn) {
            createAccount(editID.getText().toString(), editPW.getText().toString());
        }
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

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(JoinActivity.this, "createUserWithEmail:success",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);//화면 전환 시키기
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(JoinActivity.this, "createUserWithEmail failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {//원래 UI로 이동시키기
        Toast.makeText(JoinActivity.this, "Create success.",
                Toast.LENGTH_SHORT).show();

        finish();
    }
}