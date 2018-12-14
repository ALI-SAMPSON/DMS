package io.zentechgh.dms.mobile.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.zentechgh.dms.mobile.app.R;

public class UserSignInFragment extends Fragment  implements View.OnClickListener {

    // Global views
    View view;

    EditText email;
    EditText password;
    Button login_button;
    Button forgotPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_sign_in,container,false);

        // getting reference to ids of views
        email = view.findViewById(R.id.editTextEmail);
        password = view.findViewById(R.id.editTextPassword);

        login_button =  view.findViewById(R.id.login_button);
        forgotPassword = view.findViewById(R.id.forgot_password);

        // onClickListener for loginButton
        login_button.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

        return view;
    }


    // OnClick Listener for both buttons
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.loginButton:
                // login process
                Toast.makeText(getContext(), "Sign In Button Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.forgot_password:
                // reset password;
                Toast.makeText(getContext(), "Forgot Password Button Clicked", Toast.LENGTH_SHORT).show();
                break;
        }

    }

}
