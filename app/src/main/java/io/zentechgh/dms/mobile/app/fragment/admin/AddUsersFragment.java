package io.zentechgh.dms.mobile.app.fragment.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Admin;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.admin.AdminHomeActivity;

public class AddUsersFragment extends Fragment implements View.OnClickListener{

    View view;

    ConstraintLayout constraintLayout;

    // variable to store Decryption algorithm name
    String AES = "AES";

    // variable to store encrypted password
    String encryptedPassword;

    FirebaseAuth mAuth;

    FirebaseUser user;

    Users users;

    DatabaseReference userRef;

    Admin admin;

    DatabaseReference adminRef;

    ProgressBar progressBar;

    TextView tv_title;

    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextPhone;

    private AppCompatSpinner spinnerUserType;
    private ArrayAdapter<CharSequence> arrayAdapter;

    Button sign_up_button, cancel_button;

    private String CHANNEL_ID = "notification_channel_id";

    private int notificationId = 0;

    AdminHomeActivity applicationContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (AdminHomeActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add_users, container,false);

        constraintLayout = view.findViewById(R.id.constraintLayout);

        users = new Users();

        admin = new Admin();

        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        // getting reference to ids of views
        editTextUsername =  view.findViewById(R.id.editTextUsername);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextPhone = view.findViewById(R.id.editTextPhone);

        tv_title = view.findViewById(R.id.tv_title);
        // set the text to auto-scrolling( it scrolls automatically)
        tv_title.setSelected(true);

        // initializing the spinnerView and adapter
        spinnerUserType = view.findViewById(R.id.spinnerUserType);
        arrayAdapter = ArrayAdapter.createFromResource(applicationContext,R.array.user_type,R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerUserType.setAdapter(arrayAdapter);

        sign_up_button = view.findViewById(R.id.sign_up_button);
        cancel_button = view.findViewById(R.id.cancel_button);

        // setting onClickListener on this context
        sign_up_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


        return view;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.sign_up_button:

                // method call
                inputValidation();

                break;

            case R.id.cancel_button:

                // clears fields
                clearTextFields();

                break;

        }
    }

    // method to check if inputs are valid
    private void inputValidation(){
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

        else{addUser();}
    }

    // method to sign up users
    private void addUser() {

        // get text from spinner view
        final String userType = spinnerUserType.getSelectedItem().toString();

        if(userType.equals("Admin")){
            // method to add new admin
            addNewAdmin();
        }
        else {
            // method to add new user
            addNewUser();
        }


    }

    // method to add new user to the system
    private void addNewUser(){

        progressBar.setVisibility(View.VISIBLE);

        // getting text from editText fields
        final String username = editTextUsername.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String phone = editTextPhone.getText().toString();
        final String userType = spinnerUserType.getSelectedItem().toString();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    user = mAuth.getCurrentUser();

                    users.setUid(user.getUid());
                    users.setUsername(username);
                    users.setEmail(email);
                    users.setPhone(phone);
                    users.setSearch(username.toLowerCase());
                    users.setUserType(userType);
                    users.setImageUrl("");

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(user.getUid())
                            .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                // method call
                                saveUsername();

                                //method call to send Email verification link to user
                                //sendVerificationEmail();

                                // display a success message
                                Snackbar.make(constraintLayout,getString(R.string.message_user_added),Snackbar.LENGTH_LONG).show();

                                // method call to clear textfields
                                clearTextFields();

                            }
                            else {

                                // display a error message
                                Snackbar.make(constraintLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            }

                            // sign out user
                            mAuth.signOut();

                        }
                    });

                }
                else{

                    // display a error message
                    Snackbar.make(constraintLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                }

                // sign out user
                mAuth.signOut();

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

    // method to add new admin to the system
    private void addNewAdmin(){

        progressBar.setVisibility(View.VISIBLE);

        // getting text from editText fields
        final String username = editTextUsername.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String phone = editTextPhone.getText().toString();
        final String userType = spinnerUserType.getSelectedItem().toString();

        try {
            encryptedPassword = encryptPassword(password,email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String uid = adminRef.push().getKey();

        admin.setUid(uid);
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(encryptedPassword);
        admin.setPhone(phone);
        admin.setUserType(userType);

        //String uid = reference.push().getKey();

        adminRef.child(uid).setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    // display success message
                    Snackbar.make(constraintLayout,getString(R.string.message_admin_added), Snackbar.LENGTH_LONG).show();

                }
                else{

                    // display message if exception is thrown
                    Snackbar.make(constraintLayout, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                }

                // sets visibility to gone
                progressBar.setVisibility(View.GONE);
            }
        });


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


    // method to encrypt key
    private String encryptPassword(String password, String email) throws Exception{
        SecretKeySpec key = generateKey(email);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(password.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0 , bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec keySpec = new SecretKeySpec(key,"AES");
        return keySpec;
    }

}
