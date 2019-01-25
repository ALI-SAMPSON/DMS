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

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.ReceivedDocuments;
import io.zentechgh.dms.mobile.app.model.SentDocuments;
import io.zentechgh.dms.mobile.app.model.Users;

import static android.view.View.GONE;

public class RecyclerViewAdapterUsers extends RecyclerView.Adapter<RecyclerViewAdapterUsers.ViewHolder>{

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
        final String documentImage = preferences.getString("document_image","");
        final String documentTitle = preferences.getString("document_title","");
        final String documentTag = preferences.getString("document_tag","");
        final String documentComment = preferences.getString("document_comment","");
        final String documentSearch = preferences.getString("document_search","");
        final String distributee = preferences.getString("document_distributee","");

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
