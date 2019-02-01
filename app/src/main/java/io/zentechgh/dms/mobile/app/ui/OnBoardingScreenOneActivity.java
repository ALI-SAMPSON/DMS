package io.zentechgh.dms.mobile.app.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import maes.tech.intentanim.CustomIntent;

public class OnBoardingScreenOneActivity extends AppCompatActivity {

    // Creating an object of the views
    CircleImageView circleImageView;
    TextView title;
    TextView body;
    Button btn_continue;


    // Creating an object of the animation class
    Animation anim_4_image, anim_4_text_view, anim_4_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_screen_one);

        // getting reference to ids
        circleImageView = findViewById(R.id.circularImageView);

        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        btn_continue = findViewById(R.id.btn_continue);

        // getting reference to the animation drawable file
        anim_4_image = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        anim_4_text_view = AnimationUtils.loadAnimation(this, R.anim.anim_translate_tv);
        anim_4_button = AnimationUtils.loadAnimation(this, R.anim.anim_translate_btn);

        // attaching on click listener to button
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // opens the next second onBoarding screen
                Intent intentNext  = new Intent(OnBoardingScreenOneActivity.this, OnBoardingScreenTwoActivity.class);
                // clear default animation
                intentNext.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                // start the activity
                startActivity(intentNext);
            }
        });

        // method call to start animation on views
        beginAnimation();




    }

    // call back method to begin animation
    private void beginAnimation(){

        // starts animation on the views
        circleImageView.startAnimation(anim_4_image);

        title.startAnimation(anim_4_text_view);

        body.startAnimation(anim_4_text_view);

        btn_continue.startAnimation(anim_4_button);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        CustomIntent.customType(OnBoardingScreenOneActivity.this, "fadein-to-fadeout");

    }


}
