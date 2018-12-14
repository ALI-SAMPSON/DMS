package io.zentechgh.dms.mobile.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import io.zentechgh.dms.mobile.app.R;


public class UserSignUpFragment extends Fragment implements View.OnClickListener {

    // Global views
    View view;

    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextPhone;

    Button sign_up_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_sign_up,container,false);

        // getting reference to ids of views
        editTextUsername =  view.findViewById(R.id.editTextUsername);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextPhone = view.findViewById(R.id.editTextPhone);

        sign_up_button = view.findViewById(R.id.sign_up_button);

        // setting onClickListener on this context
        sign_up_button.setOnClickListener(this);

        return view;
    }

    // onClickListener for signUp Button
    @Override
    public void onClick(View view) {

        // getting text from edtitextfields
        String username = editTextUsername.getText().toString();
        String email = editTextUsername.getText().toString();
        String password = editTextUsername.getText().toString();
        String phone = editTextUsername.getText().toString();

        if(TextUtils.isEmpty(username)){
            YoYo.with(Techniques.Shake).playOn(editTextUsername);
            editTextUsername.setError(getString(R.string.error_username));
        }

        else if(TextUtils.isEmpty(email)){
            YoYo.with(Techniques.Shake).playOn(editTextEmail);
            editTextEmail.setError(getString(R.string.error_email));
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

        else{

            // method call to signUp User
            signUpUser();

        }

    }

    // method to sign up user
    private void signUpUser() {

    }
}
