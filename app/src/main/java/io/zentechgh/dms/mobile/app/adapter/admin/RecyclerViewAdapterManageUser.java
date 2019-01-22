package io.zentechgh.dms.mobile.app.adapter.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Admin;
import io.zentechgh.dms.mobile.app.model.Users;

public class RecyclerViewAdapterManageUser extends RecyclerView.Adapter<RecyclerViewAdapterManageUser.ViewHolder>{

    // global variables
    private Context mCtx;
    private List<Users> usersList;

    // variable to store Decryption algorithm name
    String AES = "AES";

    // field to stored decrypted password
    String decryptedPassword;

    // default constructor
    public RecyclerViewAdapterManageUser(){}

    // defaultless constructor
    public RecyclerViewAdapterManageUser(Context mCtx, List<Users> usersList){
        this.mCtx = mCtx;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_manage_user,viewGroup,false);

        // getting an instance of the viewHolder class
        ViewHolder viewHolder = new ViewHolder(view);

        // returning view
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        // getting the position of each document
        final Users users = usersList.get(position);

        // getting text from the database and setting them to respective views
        viewHolder.userName.setText(users.getUsername());
        viewHolder.userPhone.setText(users.getPhone());

        // checking if the document is not equal to null
        if(users.getImageUrl() == null){
            //viewHolder.userImage.setImageResource(R.drawable.profile_icon);
            Glide.with(mCtx).load(R.drawable.profile_icon).into(viewHolder.userImage);
        }
        else{
            Glide.with(mCtx).load(users.getImageUrl()).into(viewHolder.userImage);
        }


        // button to delete user from the system
        viewHolder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creating alertDialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx)
                        .setTitle(R.string.title_delete)
                        .setMessage("Deleting this account will result in " +
                                "completely removing user account from the system\n" +
                                "and user won't be able to access the app.");

                alertDialog.setPositiveButton(R.string.text_delete_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mCtx);
                        LayoutInflater inflater = LayoutInflater.from(mCtx);
                        final View dialogView  = inflater.inflate(R.layout.custom_dialog_confirm_password,null);
                        dialogBuilder.setView(dialogView);

                        // reference to the EditText in the layout file (custom_dialog)
                        final EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);

                        dialogBuilder.setTitle("Delete Document?");
                        dialogBuilder.setMessage("Please enter your password");
                        dialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // getting text from EditText
                                final String password = editTextPassword.getText().toString();

                                DatabaseReference adminRef  = FirebaseDatabase.getInstance().getReference("Admin");

                                adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                                            Admin admin = snapshot.getValue(Admin.class);

                                            assert admin != null;
                                            // getting admin email and password and storing them in variables
                                            String adminEmail = admin.getEmail();
                                            String encryptedPassword = admin.getPassword();

                                            // getting string email from sharePreference
                                            SharedPreferences preferences =
                                                    PreferenceManager.getDefaultSharedPreferences(mCtx);
                                            String email = preferences.getString("email","");

                                            // decrypt password
                                            try {
                                                decryptedPassword = decryptPassword(encryptedPassword,email);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            if(password.equals(decryptedPassword) && adminEmail.equals(email)){

                                                /**
                                                 * Code to delete selected room from database
                                                 */

                                                // delete user
                                                Users selectedUser = usersList.get(position);

                                                String selectedKey = selectedUser.getKey();

                                                // getting user email credentials

                                                viewHolder.usersRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(mCtx," user deleted successfully ",Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            // display message if error occurs
                                                            Toast.makeText(mCtx,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });


                                            }
                                            else{
                                                // display a message if there is an error
                                                Toast.makeText(mCtx,"Incorrect password. Please Try Again!",Toast.LENGTH_LONG).show();
                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // display error message
                                        Toast.makeText(mCtx, databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });


                            }

                        });


                        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // dismiss the DialogInterface
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog alert = dialogBuilder.create();
                        alert.show();


                    }
                });

                alertDialog.setNegativeButton(R.string.text_dismiss_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss dialog
                        dialog.dismiss();
                    }
                });

                // creating and showing alert dialog
                AlertDialog alert = alertDialog.create();
                alert.show();

            }

        });

    }

    // method to confirm password
    public void confirmPassword(){

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

    @Override
    public int getItemCount() {
        // return list size
        return usersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        // instance variables
        CircleImageView userImage;
        TextView userName;
        TextView userPhone;
        ImageButton button_delete;

        DatabaseReference usersRef;

        DatabaseReference adminRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            userPhone = itemView.findViewById(R.id.userPhone);
            button_delete = itemView.findViewById(R.id.button_delete);

            usersRef  = FirebaseDatabase.getInstance().getReference("Users");

            adminRef  = FirebaseDatabase.getInstance().getReference("Admin");

        }
    }

}
