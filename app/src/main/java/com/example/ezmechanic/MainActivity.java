package com.example.ezmechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    // declare some variables
    EditText inputEmail, inputPassword;
    Button registerBtn, loginBtn;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    //firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise them
        inputEmail = findViewById(R.id.enterEmailTV);
        inputPassword = findViewById(R.id.enterPasswordTV);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAuth();
            }
            private void PerformAuth() {

                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                // some data validation
                if(!email.matches(emailPattern)){
                    inputEmail.setError("Enter a proper email!");
                    inputEmail.requestFocus();
                }
                else if(email.isEmpty()){
                    inputEmail.setError("Email cannot be empty!");
                    inputEmail.requestFocus();
                }
                else if(password.isEmpty()){
                    inputPassword.setError("Password cannot be empty!");
                    inputPassword.requestFocus();
                }
                else{
                    progressDialog.setMessage("Please wait while it's logging in....");
                    progressDialog.setTitle("Logging in");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                progressDialog.dismiss();
                                                nextActivity();
                                                Toast.makeText(MainActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            } else{
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        }); // end of login btn

        //register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerActivity();
            }
        });


    }

    // go to register page
    private void registerActivity() {
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // go to profile page
    private void nextActivity() {
        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}//end of class