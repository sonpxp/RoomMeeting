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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private EditText edName;
    private EditText edEmail;
    private EditText edPass;
    private Button btnCreate;
    private TextView btnLogin;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString();
                String email = edEmail.getText().toString();
                String pass = edPass.getText().toString();

                final User user = new User();
                user.setEmail(email);
                user.setPass(pass);
                user.setName(name);

                dialog = new ProgressDialog(SignupActivity.this);
                dialog.setMessage("please wait...");
                dialog.show();

                if (!validateForm()) {
                    dialog.dismiss();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            //successful
                            database.collection("Users")
                                    .document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                            //Toast.makeText(SignupActivity.this, "isSuccessful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignupActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                //finish();
            }
        });
    }

    private void init() {
        edName = findViewById(R.id.ed_name);
        edEmail = findViewById(R.id.ed_email);
        edPass = findViewById(R.id.ed_pass);
        btnCreate = findViewById(R.id.btn_create);
        btnLogin = findViewById(R.id.btn_login);
        dialog = new ProgressDialog(SignupActivity.this);
    }

    private boolean validateForm() {
        boolean result = true;
        String name, email, pass;
        name = edName.getText().toString();
        email = edEmail.getText().toString();
        pass = edPass.getText().toString();
        if (TextUtils.isEmpty(name)) {
            edName.setError("Required");
            result = false;
        } else if (TextUtils.isEmpty(email)) {
            edEmail.setError("Required");
            result = false;
        } else if (TextUtils.isEmpty(pass)) {
            edPass.setError("Required");
            result = false;
        } else if (!isValidEmail(email)) {
            edEmail.setError("Email Validation!");
            result = false;
        } else if (pass.length() < 6) {
            edPass.setError("Password must be at least 6 characters");
            result = false;
        } else {
            edEmail.setError(null);
            edName.setError(null);
            edPass.setError(null);
        }
        return result;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}