package com.example.ezmechanic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
//declare some variables
     TextView firstName, secondName, email;
     String fName, sName, userEmail;
     FirebaseAuth authProfile;
     ImageButton profileBtn, cameraBtn, settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //initialise them
        firstName = findViewById(R.id.fNameView);
        secondName = findViewById(R.id.sNameView);
        email = findViewById(R.id.emailView);
        profileBtn = findViewById(R.id.profileBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        //get the firebase user who is logged in
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //if the firebase user is empty then print an error
        if (firebaseUser == null) {
            Toast.makeText(ProfileActivity.this, "Error!", Toast.LENGTH_LONG).show();
        } else {
            //otherwise show the users profiles
            showUserProfile(firebaseUser);
        }

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraActivity();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivity();
            }
        });

    }
    //go to settings page
    private void settingsActivity() {
        Intent intent = new Intent(ProfileActivity.this,SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    //go to camera page
    private void cameraActivity() {
        Intent intent = new Intent(ProfileActivity.this,CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //method to display the currently logged in user
    private void showUserProfile(FirebaseUser firebaseUser) {
        //create a string with the userid
        String userID = firebaseUser.getUid();
        //Extract user reference from database for "Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User thisUser = snapshot.getValue(User.class);
                if (thisUser != null) {
                    //if user is not null , then we grab his details.
                    userEmail = firebaseUser.getEmail();
                    fName = thisUser.firstName;
                    sName = thisUser.secondName;
                    // Set the text views based on the data
                    firstName.setText(fName);
                    secondName.setText(sName);
                    email.setText(userEmail);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //print an error
                Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }


}