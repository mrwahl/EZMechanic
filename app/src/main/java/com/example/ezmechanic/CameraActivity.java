package com.example.ezmechanic;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ezmechanic.ml.Android;


import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import org.tensorflow.lite.support.image.TensorImage;

public class CameraActivity extends AppCompatActivity {

    //declare some variables
    ImageView imageBox;
    TextView textBox,objecttv,scoretv,objecttv2;
    String newText;
    ImageButton profileBtn, cameraBtn, settingsBtn;
    Button predictBtn,bGallery;
    Bitmap img;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //initialise them

        imageBox = findViewById(R.id.imageView);
        newText = getString(R.string.imageisnowdisplayed);
        textBox = findViewById(R.id.textView);
        bGallery = findViewById(R.id.buttonGallery);
        profileBtn = findViewById(R.id.profileBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        predictBtn = findViewById(R.id.buttonPredict);
        objecttv = findViewById(R.id.objectField);
        objecttv2 = findViewById(R.id.objectField2);
        scoretv = findViewById(R.id.scoreField);

        //Upload the image when you click this button
        bGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear previous prediction
                objecttv.setText(null);
                scoretv.setText(null);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);

            }
        });

        //Predict the object when you click this button
        predictBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //if the image is empty then let the user know to upload one
                if(img == null){
                    Toast.makeText(CameraActivity.this, "Upload an image first!", Toast.LENGTH_SHORT).show();
                } // otherwise run the custom tensorflow lite model based on what the image is
                else if(img != null){
                    //if image is not null scale the bitmap
                    img = Bitmap.createScaledBitmap(img, 281, 324, true);

                    try {
                        //Create a new canvas to draw on
                        Canvas canvas = new Canvas(img);
                        //create a model to use
                        Android model = Android.newInstance(getApplicationContext());
                        // Creates inputs for reference.
                        TensorImage image = TensorImage.fromBitmap(img);
                        // Runs model inference and gets result.
                        Android.Outputs outputs = model.process(image);
                        Android.DetectionResult detectionResult = outputs.getDetectionResultList().get(0);

                        // Gets result from DetectionResult.
                        float score = detectionResult.getScoreAsFloat();
                        RectF location = detectionResult.getLocationAsRectF();
                        String category = detectionResult.getCategoryAsString();
                        //draw on the canvas with the given location from the model
                        drawBoundingBox(canvas, location);
                        // Releases model resources if no longer used.
                        model.close();
                        // here we will print out the results of the object to text views based on the image that is inputted by the user
                        // we print out object type, accuracy score and location of the object on the image
                        objecttv.setText(category);

                        scoretv.setText(Float.toString(score));
                        textBox.setText(newText);
                        imageBox.setImageBitmap(img);
                        //let user know we detected an object
                        Toast.makeText(CameraActivity.this, "Object detected! ", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        //print out an error to the user and restart the activity.
                        Toast.makeText(CameraActivity.this, "An error happened " + e, Toast.LENGTH_SHORT).show();
                        sameActivity();
                    }
                } // end of if else
            }
        }
        );

        //profile button
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileActivity();
            }
        });
        //settings button
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivity();
            }
        });


    }

    //the method where the bounding boxes are drawn
    void drawBoundingBox(Canvas canvas, RectF location) {
        // Draw the rectangle on the canvas of our image
        //set the paint attributes
        Paint boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setAlpha(200);
        boxPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(location, boxPaint);
    }



    // method to set the image the user uploaded
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100)
        {
            //set the image view with image data
            imageBox.setImageURI(data.getData());

            Uri uri = data.getData();
            try {
                // create a bitmap to be used later
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //method to go to profile page
    private void profileActivity() {
        Intent intent = new Intent(CameraActivity.this,ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    //method to go to settings page
    private void settingsActivity() {
        Intent intent = new Intent(CameraActivity.this,SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    //reload the camera activity
    private void sameActivity() {
        Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}