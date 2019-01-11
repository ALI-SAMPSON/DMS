package io.zentechgh.dms.mobile.app.fragment;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.SignInSignUpActivity;


public class UserSignUpFragment extends Fragment implements View.OnClickListener {

    // Global views
    View view;

    ConstraintLayout constraintLayout;

    FirebaseAuth mAuth;

    FirebaseUser user;

    Users users;

    DatabaseReference userRef;

    ProgressBar progressBar;

    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextPhone;

    Button sign_up_button;

    private String CHANNEL_ID = "notification_channel_id";

    private int notificationId = 0;

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
        view = inflater.inflate(R.layout.fragment_user_sign_up,container,false);

        constraintLayout = view.findViewById(R.id.constraintLayout);

        // getting reference to ids of views
        editTextUsername =  view.findViewById(R.id.editTextUsername);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextPhone = view.findViewById(R.id.editTextPhone);

        sign_up_button = view.findViewById(R.id.sign_up_button);

        // setting onClickListener on this context
        sign_up_button.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        users = new Users();

        return view;
    }

    // onClickListener for signUp Button
    @Override
    public void onClick(View view) {

        // getting text from editText fields
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String phone = editTextPhone.getText().toString();

        if(TextUtils.isEmpty(username)){
            YoYo.with(Techniques.Shake).playOn(editTextUsername);
            editTextUsername.setError(getString(R.string.error_username));
        }

        else if(TextUtils.isEmpty(email)){
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

        else if(phone.length() != 10){
            YoYo.with(Techniques.Shake).playOn(editTextPhone);
            editTextPhone.setError(getString(R.string.error_invalid_phone));
        }

        else{signUpUser();}

    }

    // method to sign up user
    private void signUpUser() {

        progressBar.setVisibility(View.VISIBLE);

        // getting text from editText fields
        final String username = editTextUsername.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String phone = editTextPhone.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    user = mAuth.getCurrentUser();

                    users.setUid(user.getUid());
                    users.setUsername(username);
                    users.setEmail(email);
                    users.setPhone(phone);
                    users.setSearchName(username.toLowerCase());
                    users.setRole("Distributor");

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(user.getUid())
                            .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                // method call
                                saveUsername();

                                //method call to send Email verification link to user
                                sendVerificationEmail();

                                // display a success message
                                Snackbar.make(constraintLayout,getString(R.string.sign_up_successful),Snackbar.LENGTH_LONG).show();

                                //method call to display  notification to user
                                sendNotification();

                                // method call to clear textfields
                                clearTextFields();

                            }
                            else {
                                // display a error message
                                Snackbar.make(constraintLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            }

                            // dismiss progressBar
                            //progressBar.setVisibility(View.GONE);
                        }
                    });

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

    private void saveUsername(){

        String _username = editTextUsername.getText().toString().trim();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){

            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(_username)
                    .build();

            // updates user info with the passed username
            user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       // do nothing
                   }
                   else {
                       // display a error message
                       Snackbar.make(constraintLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                   }
                }
            });

        }
    }

    private void clearTextFields(){
        editTextUsername.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextPhone.setText("");
    }

    // Method to send verification link to email to user after sign Up
    private void sendVerificationEmail(){

        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            // sign user out after verification link is sent successfully
                            mAuth.signOut();

                        }
                        else {

                            // sign user out after verification link is sent successfully
                            mAuth.signOut();

                            // display error message
                            Snackbar.make(constraintLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();

                        }
                    }
                });

    }

    private void sendNotification(){

        String my_username = user.getDisplayName();
        String my_email = user.getEmail();

        // Sends a notification to the user after signing up successfully
        // Creating an explicit intent for the activity in the app
        Intent intent = new Intent(applicationContext, SignInSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.app_logo_round)
                .setContentTitle(getString(R.string.app_name))
                .setContentText( my_username + " you have successfully Signed up")

                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText( my_username + "you have successfully Signed up. A verification link has been sent to "
                        + my_email + " Please visit your inbox to verify your email address."))
                // Set the intent that will fire when the user taps the notification
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(applicationContext);
        notificationManager.notify(notificationId,mBuilder.build());
    }

}
