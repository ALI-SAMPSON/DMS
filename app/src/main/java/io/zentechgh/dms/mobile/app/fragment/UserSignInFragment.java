package io.zentechgh.dms.mobile.app.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.user.HomeActivity;
import io.zentechgh.dms.mobile.app.ui.SignInSignUpActivity;
import maes.tech.intentanim.CustomIntent;

public class UserSignInFragment extends Fragment  implements View.OnClickListener {

    // Global views
    View view;

    ConstraintLayout constraintLayout;

    EditText editTextEmail;
    EditText editTextPassword;
    Button login_button;
    Button forgotPassword;

    FirebaseAuth mAuth;

    FirebaseUser user;

    Users users;

    DatabaseReference userRef;

    ProgressBar progressBar;

    SignInSignUpActivity applicationContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (SignInSignUpActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_sign_in,container,false);

        constraintLayout = view.findViewById(R.id.constraintLayout);

        // getting reference to ids of views
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        login_button =  view.findViewById(R.id.login_button);
        forgotPassword = view.findViewById(R.id.forgot_password);

        // onClickListener for loginButton
        login_button.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        users = new Users();

        return view;
    }


    // OnClick Listener for both buttons
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.login_button:
                // login process
                inputValidation();
                break;
            case R.id.forgot_password:
                // reset password;
                //resetPassword();
                break;
        }

    }

    private void inputValidation(){

        // getting text from editText fields
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            YoYo.with(Techniques.Shake).playOn(editTextEmail);
            editTextEmail.setError(getString(R.string.error_email));
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            YoYo.with(Techniques.Shake).playOn(editTextEmail);
            editTextEmail.setError(getString(R.string.error_invalid_email));
        }

        else if(TextUtils.isEmpty(password)){
            YoYo.with(Techniques.Shake).playOn(editTextPassword);
            editTextPassword.setError(getString(R.string.error_password));
        }

        else if(password.length() < 6){
            YoYo.with(Techniques.Shake).playOn(editTextPassword);
            editTextPassword.setError(getString(R.string.error_password_length));
        }

        else{loginUser();}
    }

    // method to login User
    private void loginUser() {

        progressBar.setVisibility(View.VISIBLE);

        // getting text from edtitext fields
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    checkIfEmailIsVerified();

                }
                else{
                    // display a error message
                    Snackbar.make(constraintLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                }

                // dismiss progressBar
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    // method to check if email is verified
    private void checkIfEmailIsVerified(){

        user = mAuth.getCurrentUser();

        boolean isEmailVerified = user.isEmailVerified();

        if(isEmailVerified){

            // display a successful login message
            Toast.makeText(applicationContext,getString(R.string.sign_in_successful),Toast.LENGTH_SHORT).show();

            // start the home activity
            startActivity(new Intent(getActivity(),HomeActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(applicationContext,"fadein-to-fadeout");

            // finishes this activity(prevents user from going back to this activity when back button is pressed)
            applicationContext.finish();

            // clear the text fields
            clearTextFields();

        }
        else{

            // display message if email is not verified
            Toast.makeText(applicationContext, getString(R.string.text_email_not_verified),
                    Toast.LENGTH_LONG).show();

            // signs user out and restarts the Login Activity
            mAuth.signOut();

            // restarts the activity
            startActivity(new Intent (getActivity(),SignInSignUpActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(applicationContext,"fadein-to-fadeout");

            // finish the activity
            applicationContext.finish();

        }

    }

    // method to clear editText fields
    private void clearTextFields(){
        editTextEmail.setText("");
        editTextPassword.setText("");
    }

}
