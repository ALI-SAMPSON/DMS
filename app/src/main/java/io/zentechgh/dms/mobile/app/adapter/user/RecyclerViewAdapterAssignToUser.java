package io.zentechgh.dms.mobile.app.adapter.user;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.ReceivedDocuments;
import io.zentechgh.dms.mobile.app.model.SentDocuments;
import io.zentechgh.dms.mobile.app.model.Users;

import static android.view.View.GONE;

public class RecyclerViewAdapterAssignToUser extends RecyclerView.Adapter<RecyclerViewAdapterAssignToUser.ViewHolder>{

    // global variables
    private Context mCtx;
    private List<Users> usersList;

    // default constructor
    public RecyclerViewAdapterAssignToUser(){}


    // defaultless constructor
    public RecyclerViewAdapterAssignToUser(Context mCtx, List<Users> usersList){
        this.mCtx = mCtx;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_assign_to_users,viewGroup,false);

        // getting an instance of the viewHolder class
        ViewHolder viewHolder = new ViewHolder(view);

        // returning view
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        // getting the position of each document
        final Users user = usersList.get(position);

        // getting text from the database and setting them to respective views
        viewHolder.userName.setText(user.getUsername());
        viewHolder.userPhone.setText(user.getPhone());

        // checking if the document is not equal to null
        if(user.getImageUrl() == null){
            viewHolder.userImage.setImageResource(R.drawable.profile_icon);
        }
        else{
            Glide.with(mCtx).load(user.getImageUrl()).into(viewHolder.userImage);
        }


        // getting string from sharePreference of Documents Details
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mCtx);
        final String documentUrl = preferences.getString("document_url","");
        final String documentTitle = preferences.getString("document_title","");
        final String documentType = preferences.getString("document_type","");
        final String documentTag = preferences.getString("document_tag","");
        final String documentComment = preferences.getString("document_comment","");
        final String documentSearch = preferences.getString("document_search","");
        final String distributee = preferences.getString("document_distributee","");

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creating alertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx,R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                        .setTitle(R.string.title_assign_user)
                        .setMessage(R.string.text_confirm);

                builder.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // sets visibility to visible when assigning document
                        viewHolder.progressBar.setVisibility(View.VISIBLE);

                        // setting fields for Model SentDocuments
                        viewHolder.sentDocuments.setDocumentUrl(documentUrl);
                        viewHolder.sentDocuments.setType(documentType);
                        viewHolder.sentDocuments.setTitle(documentTitle);
                        viewHolder.sentDocuments.setTag(documentTag);
                        viewHolder.sentDocuments.setComment(documentComment);
                        viewHolder.sentDocuments.setSearch(documentSearch);
                        viewHolder.sentDocuments.setDistributee(distributee);

                        // assigns document to currentUser (to help trace documents later)  and to the corresponding user
                        viewHolder.sendDocRef.child(viewHolder.currentUser.getUid()).push().setValue(viewHolder.sentDocuments)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            // setting fields for Model ReceivedDocuments
                                            viewHolder.receivedDocuments.setDocumentUrl(documentUrl);
                                            viewHolder.receivedDocuments.setTitle(documentTitle);
                                            viewHolder.receivedDocuments.setType(documentType);
                                            viewHolder.receivedDocuments.setTag(documentTag);
                                            viewHolder.receivedDocuments.setComment(documentComment);
                                            viewHolder.receivedDocuments.setDistributee(distributee);
                                            viewHolder.receivedDocuments.setSender(viewHolder.currentUser.getDisplayName());
                                            viewHolder.receivedDocuments.setSearch(documentSearch);

                                            viewHolder.receivedDocRef.child(user.getUid()).push().setValue(viewHolder.receivedDocuments)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                           if(task.isSuccessful()){

                                                               // display a success message is document is successfully assigned
                                                               Toast.makeText(mCtx,R.string.message_document_assigned,Toast.LENGTH_LONG).show();

                                                           }
                                                           else {
                                                               // display message if exception is thrown
                                                               Toast.makeText(mCtx,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                           }
                                                        }
                                                    });


                                        }
                                        else{
                                            // display message if exception is thrown
                                            Toast.makeText(mCtx,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        }

                                        // sets visibility to gone after document is assigned successfully
                                        viewHolder.progressBar.setVisibility(GONE);

                                        // clears sharePreferences after document is assigned successfully
                                        clearPrefs(mCtx);
                                    }

                                });

                    }
                });

                builder.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss dialog
                        dialog.dismiss();
                    }
                });

                // creating and showing alert dialog
                AlertDialog alert = builder.create();
                alert.show();

            }

        });

    }

    // method to clear sharePreference when admin log outs
    private  void clearPrefs(Context ctx){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        editor.clear(); // clear all stored data (email)
        editor.apply();
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

        ProgressBar progressBar;

        SentDocuments sentDocuments;

        ReceivedDocuments receivedDocuments;

        FirebaseUser currentUser;

        DatabaseReference assignRef;

        DatabaseReference sendDocRef;

        DatabaseReference receivedDocRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            userPhone = itemView.findViewById(R.id.userPhone);

            progressBar = itemView.findViewById(R.id.progressBar);

            sentDocuments = new SentDocuments();

            receivedDocuments = new ReceivedDocuments();

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            assignRef  = FirebaseDatabase.getInstance().getReference("Users");

            sendDocRef = FirebaseDatabase.getInstance().getReference("SentDocuments");

            receivedDocRef = FirebaseDatabase.getInstance().getReference("ReceivedDocuments");

        }
    }

}
