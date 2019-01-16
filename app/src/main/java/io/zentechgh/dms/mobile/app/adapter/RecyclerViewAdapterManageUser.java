package io.zentechgh.dms.mobile.app.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Users;

public class RecyclerViewAdapterManageUser extends RecyclerView.Adapter<RecyclerViewAdapterManageUser.ViewHolder>{

    // global variables
    private Context mCtx;
    private List<Users> usersList;

    // default constructor
    public RecyclerViewAdapterManageUser(){}


    // defaultless constructor
    public RecyclerViewAdapterManageUser(Context mCtx, List<Users> usersList){
        this.mCtx = mCtx;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterManageUser.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_manage_user,viewGroup,false);

        // getting an instance of the viewHolder class
        ViewHolder viewHolder = new ViewHolder(view);

        // returning view
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        // getting the position of each document
        final Users users = usersList.get(position);

        // getting text from the database and setting them to respective views
        viewHolder.userName.setText(" Username: " + users.getUsername());
        viewHolder.userPhone.setText(" Phone : " + users.getPhone());

        // checking if the document is not equal to null
        if(users.getImageUrl() == null){
            viewHolder.userImage.setImageResource(R.drawable.profile_icon);
        }
        else{
            Glide.with(mCtx).load(users.getImageUrl()).into(viewHolder.userImage);
        }



        viewHolder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creating alertDialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx)
                        .setTitle(R.string.title_assign)
                        .setMessage(R.string.text_confirm + " " + users.getUsername());

                alertDialog.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       // delete user

                    }
                });

                alertDialog.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        // instance variables
        CircleImageView userImage;
        TextView userName;
        TextView userPhone;
        ImageView button_delete;


        DatabaseReference assignRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            userPhone = itemView.findViewById(R.id.userPhone);
            button_delete = itemView.findViewById(R.id.button_delete);

            assignRef  = FirebaseDatabase.getInstance().getReference("Users");
        }
    }

}
