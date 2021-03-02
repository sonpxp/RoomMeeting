package com.sonmob.roommeeting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText edEmailLogin;
    private EditText edPassLogin;
    private Button btnLogin;
    private TextView btnSignup;

    public FirebaseAuth mAuth;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmailLogin.getText().toString();
                String pass = edPassLogin.getText().toString();

                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage("please wait...");
                dialog.show();

                if (!validateForm()) {
                    dialog.dismiss();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Check for Email and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                //finish();
            }
        });

    }

    private void init() {
        edEmailLogin = findViewById(R.id.ed_email_login);
        edPassLogin = findViewById(R.id.ed_pass_login);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        dialog = new ProgressDialog(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean validateForm() {
        boolean result = true;
        String email, pass;
        email = edEmailLogin.getText().toString();
        pass = edPassLogin.getText().toString();

        if (TextUtils.isEmpty(email)) {
            edEmailLogin.setError("Required");
            result = false;
        } else if (TextUtils.isEmpty(pass)) {
            edPassLogin.setError("Required");
            result = false;
        } else if (!isValidEmail(email)) {
            edEmailLogin.setError("Email Validation!");
            result = false;
        } else {
            edEmailLogin.setError(null);
            edPassLogin.setError(null);
        }
        return result;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}