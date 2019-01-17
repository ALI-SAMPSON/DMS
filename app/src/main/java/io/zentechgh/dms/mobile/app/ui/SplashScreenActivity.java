package io.zentechgh.dms.mobile.app.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Admin;
import io.zentechgh.dms.mobile.app.prefs.SavedSharePreference;
import maes.tech.intentanim.CustomIntent;

public class SplashScreenActivity extends AppCompatActivity {

    // delay time for splash screen
    private final int SPLASH_SCREEN_DISPLAY_TIME = 4000;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // getting instance of fireBaseAuth class
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    protected void onStart() {
        super.onStart();

        // checks if user is currently logged in
        if(SavedSharePreference.getEmail(SplashScreenActivity.this).length() == 0 && mAuth.getCurrentUser() == null){
            // open splash screen first
            splashScreen();
        }
        else if(SavedSharePreference.getEmail(SplashScreenActivity.this).length() != 0){

            // start the activity
            startActivity(new Intent(SplashScreenActivity.this,AdminHomeActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(SplashScreenActivity.this,"right-to-left");

            // finish the activity
            finish();

        }
        else if(mAuth.getCurrentUser() != null){
            // start the activity
            startActivity(new Intent(SplashScreenActivity.this,HomeActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");

            // finish the activity
            finish();
        }
    }


    //class to the handle the splash screen activity
    public void splashScreen() {

        Thread timer = new Thread() {
            @Override
            public void run() {
                try {

                    sleep(SPLASH_SCREEN_DISPLAY_TIME);

                    //Creates and start the intent of the next activity
                    startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));

                    // Add a custom animation ot the activity
                    CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");

                    // finishes the activity
                    finish(); // this prevents the app from going back to the splash screen

                    super.run();
                }
                catch (InterruptedException e) {
                    // displays a toast
                    Toast.makeText(SplashScreenActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        };
        //starts the timer
        timer.start();
    }

}
