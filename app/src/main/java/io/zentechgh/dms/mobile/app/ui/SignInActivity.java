package io.zentechgh.dms.mobile.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Admin;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.prefs.SavedSharePreference;
import io.zentechgh.dms.mobile.app.ui.admin.AdminHomeActivity;
import io.zentechgh.dms.mobile.app.ui.user.UserHomeActivity;
import maes.tech.intentanim.CustomIntent;

import static android.view.View.GONE;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout constraintLayout;

    EditText editTextEmail;
    EditText editTextPassword;
    Button sign_in_button;
    Button forgotPassword;

    // variable to store Decryption algorithm name
    String AES = "AES";

    // variable to store encrypted password
    String decryptedPassword;

    FirebaseAuth mAuth;

    FirebaseUser user;

    Users users;

    DatabaseReference adminRef;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        constraintLayout = findViewById(R.id.constraintLayout);

        // getting reference to ids of views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        sign_in_button =  findViewById(R.id.sign_in_button);
        forgotPassword = findViewById(R.id.forgot_password);

        progressBar = findViewById(R.id.progressBar);

        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        mAuth = FirebaseAuth.getInstance();

        users = new Users();

        // onClickListener for sign in and forgot password Button
        sign_in_button.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // checks if there is any currently logged in user
        if(mAuth.getCurrentUser() != null){
            // start the home activity
            startActivity(new Intent(SignInActivity.this,UserHomeActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(SignInActivity.this,"fadein-to-fadeout");

            // finishes this activity(prevents user from going back to this activity when back button is pressed)
            finish();
        }

        // checks if there is any currently logged in admin
        if(SavedSharePreference.getEmail(SignInActivity.this).length() != 0){
            // starts the UserHomeActivity
            startActivity(new Intent(SignInActivity.this,AdminHomeActivity.class));
            // Adds a fadein-fadeout animations to the activity
            CustomIntent.customType(SignInActivity.this,"fadein-to-fadeout");
            // finish current activity
            finish();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.sign_in_button:

                // login process
                inputValidation();

                break;
            case R.id.forgot_password:

                // method call to open forgotPassword activity
                forgotPassword();

                break;
        }

    }

    public void forgotPassword(){

        // start the home activity
        startActivity(new Intent(SignInActivity.this,ForgotPasswordActivity.class));

        // Add a custom animation ot the activity
        CustomIntent.customType(SignInActivity.this,"left-to-right");

        // finishes this activity(prevents user from going back to this activity when back button is pressed)
        finish();

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

        else{
            // method call to open alert dialog before login
            openLoginDialog();
        }
    }


    private void openLoginDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this, R.style.DialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        // inflating the layout resource file
        final View dialogView = inflater.inflate(R.layout.custom_dialog_sign_in,null);
        builder.setView(dialogView);

        // getting reference to the buttons in the custom layout file
        final Button user_login_btn = dialogView.findViewById(R.id.user_login_btn);
        final Button admin_login_btn = dialogView.findViewById(R.id.admin_login_btn);

        // setting title and message on the alertDialog
        builder.setTitle(R.string.title_sign_in);
        builder.setMessage(R.string.msg_sign_in);


        final AlertDialog alertDialog = builder.create();

        // on click listener for button to login user
        user_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // dismiss the dialog
                alertDialog.dismiss();

                // method call to login user
                signInUser();
            }
        });

        // on click listener for button to login user
        admin_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // dismiss the dialog
                alertDialog.dismiss();

                // method call to login user
                signInAdmin();
            }
        });

        alertDialog.show();


    }

    // method to sign in user into the system
    private void signInUser() {

        // hides sign in button
        sign_in_button.setVisibility(View.INVISIBLE);

        // shows progressBar
        progressBar.setVisibility(View.VISIBLE);

        // getting text from edittext fields
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // display a successful login message
                            Toast.makeText(SignInActivity.this,getString(R.string.sign_in_successful),Toast.LENGTH_SHORT).show();

                            // start the home activity
                            startActivity(new Intent(SignInActivity.this,UserHomeActivity.class));

                            // Add a custom animation ot the activity
                            CustomIntent.customType(SignInActivity.this,"fadein-to-fadeout");

                            // finishes this activity(prevents user from going back to this activity when back button is pressed)
                            finish();

                            // clear the text fields
                            clearTextFields();

                        }
                        else{
                            // display a error message
                            Snackbar.make(constraintLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                        }

                        // dismiss progressBar
                        progressBar.setVisibility(GONE);

                        // displays sign in button
                        sign_in_button.setVisibility(View.VISIBLE);
                    }
                });

    }


    // method to sign in admin into the system
    private void signInAdmin() {

        // hides sign in button
        sign_in_button.setVisibility(View.INVISIBLE);


        progressBar.setVisibility(View.VISIBLE);

        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Admin admin = snapshot.getValue(Admin.class);

                    assert admin != null;
                    String admin_username = admin.getUsername();
                    String adminEmail = admin.getEmail();
                    String encryptedPassword = admin.getPassword();

                    // decrypt password
                    try {
                        decryptedPassword = decryptPassword(encryptedPassword,email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // compares decrypted password to the current password entered and
                    if(password.equals(decryptedPassword) && email.equals(adminEmail)){

                        // dismiss the dialog
                        progressBar.setVisibility(View.GONE);

                        // displays sign in button
                        sign_in_button.setVisibility(View.VISIBLE);

                        // display a successful login message
                        Toast.makeText(SignInActivity.this,getString(R.string.sign_in_successful),Toast.LENGTH_SHORT).show();

                        // storing email in shared preference
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(SignInActivity.this).edit();
                        editor.putString("email", email);
                        editor.putString("username", admin.getUsername());
                        editor.putString("uid", admin.getUid());
                        editor.apply();

                        // setting uid and email to getter method in sharedPreferences
                        SavedSharePreference.setEmail(SignInActivity.this, email);
                        SavedSharePreference.setUid(SignInActivity.this, admin.getUid());

                        // start the home activity
                        startActivity(new Intent(SignInActivity.this,AdminHomeActivity.class));

                        // Add a custom animation ot the activity
                        CustomIntent.customType(SignInActivity.this,"fadein-to-fadeout");

                        // finishes this activity(prevents user from going back to this activity when back button is pressed)
                        finish();

                        // clear the text fields
                        clearTextFields();

                    }
                    else{

                        // hides the progressBar
                        progressBar.setVisibility(View.GONE);

                        // displays sign in button
                        sign_in_button.setVisibility(View.VISIBLE);

                        // display a message if there is an error
                        Snackbar.make(constraintLayout,"Incorrect email or password. Please Try Again",Snackbar.LENGTH_LONG).show();
                        //Toast.makeText(SignInActivity.this,"Incorrect email or password. Please Try Again",Toast.LENGTH_LONG).show();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });


    }

    // method to decrypt password
    private String decryptPassword(String password, String email) throws Exception {
        SecretKeySpec key  = generateKey(email);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodedValue = Base64.decode(password,Base64.DEFAULT);
        byte[] decVal = c.doFinal(decodedValue);
        String decryptedValue = new String(decVal);
        return decryptedValue;

    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0 , bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec keySpec = new SecretKeySpec(key,"AES");
        return keySpec;
    }

    // method to check if email is verified
    private void checkIfEmailIsVerified(){

        user = mAuth.getCurrentUser();

        boolean isEmailVerified = user.isEmailVerified();

        if(isEmailVerified){

            // display a successful login message
            Toast.makeText(SignInActivity.this,getString(R.string.sign_in_successful),Toast.LENGTH_SHORT).show();

            // start the home activity
            startActivity(new Intent(SignInActivity.this,UserHomeActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(SignInActivity.this,"fadein-to-fadeout");

            // finishes this activity(prevents user from going back to this activity when back button is pressed)
            finish();

            // clear the text fields
            clearTextFields();

        }
        else{

            // display message if email is not verified
            Toast.makeText(SignInActivity.this, getString(R.string.text_email_not_verified),
                    Toast.LENGTH_LONG).show();

            // signs user out and restarts the Login Activity
            mAuth.signOut();

            // restarts the activity
            startActivity(new Intent (SignInActivity.this,SignInSignUpActivity.class));

            // Add a custom animation ot the activity
            CustomIntent.customType(SignInActivity.this,"fadein-to-fadeout");

            // finish the activity
            finish();

        }

    }

    // method to clear editText fields
    private void clearTextFields(){
        editTextEmail.setText("");
        editTextPassword.setText("");
    }

}
