package io.zentechgh.dms.mobile.app.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Users;

public class RecyclerViewAdapterUsers  extends RecyclerView.Adapter<RecyclerViewAdapterUsers.ViewHolder>{

    // global variables
    private Context mCtx;
    private List<Users> usersList;

    // default constructor
    public RecyclerViewAdapterUsers(){}

    // defaultless constructor
    public RecyclerViewAdapterUsers(Context mCtx, List<Users> usersList){
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
        viewHolder.userName.setText(" Username: " + users.getUsername());
        viewHolder.userPhone.setText(" Phone : " + users.getPhone());

        // checking if the document is not equal to null
        if(users.getImageUrl() == null){
            viewHolder.userImage.setImageResource(R.mipmap.avatar_placeholder_round);
        }
        else{
            Glide.with(mCtx).load(users.getDocumentUrl()).into(viewHolder.userImage);
        }

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);

                alertDialog.setTitle(R.string.title_assign)
                        .setMessage(R.string.text_confirm + users.getUsername());

                alertDialog.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        viewHolder.assignRef.child(users.getEmail());

                        HashMap<String,Object> assignDoc = new HashMap<>();
                        assignDoc.put("Title", users.getUid());
                        viewHolder.assignRef.push().setValue(assignDoc);

                    }
                });

                alertDialog.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss dialog
                        dialog.dismiss();
                    }
                });

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
        CardView cardView;

        DatabaseReference assignRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            userPhone = itemView.findViewById(R.id.userPhone);
            cardView = itemView.findViewById(R.id.cardView);

            assignRef  = FirebaseDatabase.getInstance().getReference("Users");
        }
    }


}
