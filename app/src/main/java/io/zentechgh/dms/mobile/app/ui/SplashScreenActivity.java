package io.zentechgh.dms.mobile.app.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.prefs.SavedSharePreference;
import io.zentechgh.dms.mobile.app.ui.admin.AdminHomeActivity;
import io.zentechgh.dms.mobile.app.ui.user.UserHomeActivity;
import maes.tech.intentanim.CustomIntent;

public class SplashScreenActivity extends AppCompatActivity {

    // delay time for splash screen
    private final int SPLASH_SCREEN_DISPLAY_TIME = 5000;

    TextView tv_app_name, tv_watermark;

    CircleImageView logo;

    // Creating an object of the animation class
    Animation anim_4_text_view;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        anim_4_text_view = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

        // getting reference to views
        tv_app_name = findViewById(R.id.app_name);
        tv_watermark = findViewById(R.id.watermark);

        logo = findViewById(R.id.logo);

        // setting animation on app name
        YoYo.with(Techniques.BounceIn).duration(2000).playOn(tv_app_name);

        // setting animation on app logo
        YoYo.with(Techniques.BounceInDown).duration(2000).playOn(logo);

        // setting animation on watermark
        tv_watermark.startAnimation(anim_4_text_view);

        // getting instance of fireBaseAuth class
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    protected void onStart() {
        super.onStart();

        // checks if user is currently logged in
        /*if(SavedSharePreference.getEmail(SplashScreenActivity.this).length() == 0 && mAuth.getCurrentUser() == null){
            // open splash screen first
            splashScreen();
        }
        */
        if(SavedSharePreference.getEmail(SplashScreenActivity.this).length() != 0){

            // start the activity
            startActivity(new Intent(SplashScreenActivity.this,AdminHomeActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(SplashScreenActivity.this,"right-to-left");

            // finish the activity
            finish();

        }
        else if(mAuth.getCurrentUser() != null){
            // start the activity
            startActivity(new Intent(SplashScreenActivity.this,UserHomeActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");

            // finish the activity
            finish();
        }
        else{
            // open splash screen first
            splashScreen();
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
                    Intent intent = new Intent(SplashScreenActivity.this, OnBoardingScreenOneActivity.class);

                    // Add a custom animation ot the activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    // start activity
                    startActivity(intent);

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
