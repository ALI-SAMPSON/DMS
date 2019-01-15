package io.zentechgh.dms.mobile.app.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import io.zentechgh.dms.mobile.app.R;
import maes.tech.intentanim.CustomIntent;

public class ForgotPasswordActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;

    Toolbar toolbar;

    TextView toolbar_title;

    EditText editTextEmail;

    FirebaseAuth mAuth;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // getting ref to parent layout
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        // getting reference to views
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar_title =  findViewById(R.id.toolbar_title);
        // setting supportActionBar to custom toolbar
        setSupportActionBar(toolbar);
        // setting navigation icon and onClickListener
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // starts the Login activity
                startActivity(new Intent(ForgotPasswordActivity.this,SignInActivity.class));

                // Add a custom animation ot the activity
                CustomIntent.customType(ForgotPasswordActivity.this,"right-to-left");

                // finish the activity
                finish();
            }
        });

        // getting reference to view
        editTextEmail =  findViewById(R.id.editTextEmail);

        // initializing FireBaseAuth class
        mAuth = FirebaseAuth.getInstance();

        // getting reference to view
        progressBar = findViewById(R.id.progressBar);

    }

    //OnClickListener for password reset button
    public void resetPasswordButton(View view) {

        // getting text from editText field
        String email  = editTextEmail.getText().toString();

        if(TextUtils.isEmpty(email)){
            // trigger's a shaking animation to alert user of no email entered
            YoYo.with(Techniques.Shake).playOn(editTextEmail);
            editTextEmail.setError(getString(R.string.error_empty_field));
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // trigger's a shaking animation to alert user of no email entered
            YoYo.with(Techniques.Shake).playOn(editTextEmail);
            editTextEmail.setError(getString(R.string.error_invalid_email));
        }

        else{
            // method call to reset password
            resetPassword();
        }

    }

    // method that send a password reset link to user's email to reset password
    private void resetPassword(){

        // displays progressBar
        progressBar.setVisibility(View.VISIBLE);

        // getting text from editText field
        String email  = editTextEmail.getText().toString();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    // display successful message along with instructions to reset password
                    Snackbar.make(coordinatorLayout,getString(R.string.reset_password_instruction),5000).show();
                }
                else{
                    // display message if there is an exception
                    Snackbar.make(coordinatorLayout,task.getException().getMessage(),3000).show();
                }

                // hides the progressBar
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // starts the Login activity
        startActivity(new Intent(ForgotPasswordActivity.this,SignInActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(ForgotPasswordActivity.this,"right-to-left");

        // finish the activity
        finish();

    }

}
