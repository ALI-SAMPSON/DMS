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

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<RecyclerViewAdapterUser.ViewHolder>{

    // global variables
    private Context mCtx;
    private List<Users> usersList;

    // default constructor
    public RecyclerViewAdapterUser(){}


    // defaultless constructor
    public RecyclerViewAdapterUser(Context mCtx, List<Users> usersList){
        this.mCtx = mCtx;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_users,viewGroup,false);

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
        viewHolder.userName.setText(users.getUsername());
        viewHolder.userPhone.setText(users.getPhone());

        // checking if the document is not equal to null
        if(users.getImageUrl() == null){
            viewHolder.userImage.setImageResource(R.drawable.profile_icon);
        }
        else{
            Glide.with(mCtx).load(users.getImageUrl()).into(viewHolder.userImage);
        }


        // getting string from sharePreference of Documents Details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mCtx);
        final String documentTitle = preferences.getString("documentTitle","");
        final String documentTag = preferences.getString("documentTag","");
        final String documentComment = preferences.getString("documentComment","");
        final String documentImage = preferences.getString("documentImage","");
        final String distributor = preferences.getString("distributor","");

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creating alertDialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx)
                        .setTitle(R.string.title_assign)
                        .setMessage(R.string.text_confirm + " " + users.getUsername());

                alertDialog.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // assigns document to currentUser (to help trace documents later) - to be seen as "Sent Documents"
                        viewHolder.assignRef.child(viewHolder.currentUser.getUid())
                                .child("Sent Document");
                        HashMap<String,Object> assignDocToCurrentUser = new HashMap<>();
                        assignDocToCurrentUser.put("title", documentTitle);
                        assignDocToCurrentUser.put("tag", documentTag);
                        assignDocToCurrentUser.put("comment", documentComment);
                        assignDocToCurrentUser.put("documentUrl", documentImage);
                        assignDocToCurrentUser.put("distributor",viewHolder.currentUser.getDisplayName());
                        assignDocToCurrentUser.put("search",documentTitle.toLowerCase());
                        viewHolder.assignRef.push().updateChildren(assignDocToCurrentUser);

                        // assigns document to another user (to help trace documents later) - to be seen as "Received Documents"
                        viewHolder.assignRef.child(users.getUid()).child("Received Documents");
                        HashMap<String,Object> assignDocToUser = new HashMap<>();
                        assignDocToUser.put("title", documentTitle);
                        assignDocToUser.put("tag", documentTag);
                        assignDocToUser.put("comment", documentComment);
                        assignDocToUser.put("documentUrl", documentImage);
                        assignDocToUser.put("distributor",viewHolder.currentUser.getDisplayName());
                        assignDocToUser.put("search",documentTitle.toLowerCase());
                        viewHolder.assignRef.push().updateChildren(assignDocToUser);

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

        FirebaseUser currentUser;

        DatabaseReference assignRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            userPhone = itemView.findViewById(R.id.userPhone);

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            assignRef  = FirebaseDatabase.getInstance().getReference("Users");
        }
    }

}
