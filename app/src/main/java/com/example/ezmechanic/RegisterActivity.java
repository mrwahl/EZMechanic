package com.example.ezmechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.Task;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {
    // declare some variables
    EditText inputName,inputSName,inputEmail, inputPassword,inputConfirmPassword;
    Button registerBtn2, backToLoginBTN;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    //firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //initialise them
        inputName = findViewById(R.id.inputName);
        inputSName = findViewById(R.id.inputSecondName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        registerBtn2 = findViewById(R.id.registerBtn2);
        backToLoginBTN = findViewById(R.id.backToLoginBTN);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //register button action listener
        registerBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAuth();
            }
            //authentication method here.

            private void PerformAuth() {
                String firstName = inputName.getText().toString();
                String secondName = inputSName.getText().toString();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmPassword = inputConfirmPassword.getText().toString().trim();
                // some form validation

                if(firstName.isEmpty()&&secondName.isEmpty()&&email.isEmpty()&&password.isEmpty()&&confirmPassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "All fields cannot be empty!", Toast.LENGTH_SHORT).show();
                }

                if(firstName.isEmpty()){
                    inputName.setError("Enter a first name !");
                    inputName.requestFocus();
                }
                if(secondName.isEmpty()){
                    inputSName.setError("Enter a surname !");
                    inputSName.requestFocus();

                }
                if(!email.matches(emailPattern)){
                    inputEmail.setError("Enter a proper email!");
                    inputEmail.requestFocus();
                }
                else if(password.isEmpty()){
                    inputPassword.setError("Password cannot be empty!");
                    inputPassword.requestFocus();
                }
                else if(password.length()<6){
                    inputPassword.setError("Password cannot be less than 6 characters ");
                    inputPassword.requestFocus();
                }
                else if(!password.equals(confirmPassword)){
                    inputConfirmPassword.setError("Passwords must match!");
                    inputConfirmPassword.requestFocus();
                }
                else{
                    progressDialog.setMessage("Please wait while it's registering....");
                    progressDialog.setTitle("Registering");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(firstName, secondName, email);
                                // add the User object just created into our Realtime database under " Users "
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //if task is okay then go to next activity which is login page
                                            progressDialog.dismiss();
                                            nextActivity();
                                            Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else if (!task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            } // END OF REGISTER METHOD
        });
        //on click to start back to login method
        backToLoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToLogin();
            }
        });

    }
    //back to login page
    private void backToLogin() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // go to profile page once registered successfully
    private void nextActivity() {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}