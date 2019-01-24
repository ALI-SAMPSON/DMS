package io.zentechgh.dms.mobile.app.adapter.user;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.ReceivedDocuments;
import io.zentechgh.dms.mobile.app.model.SentDocuments;
import io.zentechgh.dms.mobile.app.ui.user.ViewDocumentUserActivity;
import maes.tech.intentanim.CustomIntent;

public class RecyclerViewAdapterReceived extends RecyclerView.Adapter<RecyclerViewAdapterReceived.ViewHolder> {

    // global variables
    private Context mCtx;
    private List<ReceivedDocuments> documentsList;

    // default constructor
    public RecyclerViewAdapterReceived(){}

    // defaultless constructor
    public RecyclerViewAdapterReceived(Context mCtx, List<ReceivedDocuments> documentsList){
        this.mCtx = mCtx;
        this.documentsList = documentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_my_documents,viewGroup,false);

        // getting an instance of the viewHolder class
        ViewHolder viewHolder = new ViewHolder(view);

        // returning view
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        // getting the position of each document
        final ReceivedDocuments receivedDocuments = documentsList.get(position);

        // getting text from the database and setting them to respective views
        viewHolder.documentTitle.setText(" Title : " + receivedDocuments.getTitle());
        viewHolder.documentTag.setText(" Tag : " + receivedDocuments.getTag());
        viewHolder.documentComment.setText(" Comment : " + receivedDocuments.getComment());
        viewHolder.documentDistributee.setText(" Distributee : " + receivedDocuments.getDistributee());

        // checking if the document is not equal to null
        if(receivedDocuments.getDocumentUrl() == null){
            viewHolder.documentImage.setImageResource(R.drawable.scanned_file);
        }
        else {
            Glide.with(mCtx).load(receivedDocuments.getDocumentUrl()).into(viewHolder.documentImage);
        }

        // onclick listener for view button to view document
        viewHolder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open file to view
                Intent intent = new Intent(mCtx,ViewDocumentUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // passing strings
                intent.putExtra("file",receivedDocuments.getDocumentUrl());
                intent.putExtra("title",receivedDocuments.getTitle());
                intent.putExtra("tag",receivedDocuments.getTag());
                intent.putExtra("comment", receivedDocuments.getComment());
                mCtx.startActivity(intent);

                // adding an intent transition from left-to-right
                CustomIntent.customType(mCtx,"left-to-right");

            }
        });

        // set OnClick Listener to delete an item from received document table(document)
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ReceivedDocuments selectedDocument = documentsList.get(position);

                String selectedKey = selectedDocument.getKey();

                viewHolder.dbRef.child(selectedKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           // display success message
                           Toast.makeText(mCtx, R.string.message_document_deleted, Toast.LENGTH_SHORT).show();
                       }
                       else {
                           // display message if error occurs
                           Toast.makeText(mCtx, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                       }
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return documentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        // instance variables
        ImageView documentImage;
        TextView documentTitle;
        TextView documentTag;
        TextView documentComment;
        TextView documentDistributee;
        ImageButton buttonDelete;
        ImageButton buttonView;
        CardView cardView;

        FirebaseUser currentUser;

        // getting ref to all files user sent
        DatabaseReference dbRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentImage = itemView.findViewById(R.id.document_image);
            documentTitle = itemView.findViewById(R.id.document_title);
            documentTag = itemView.findViewById(R.id.document_tag);
            documentComment = itemView.findViewById(R.id.document_comment);
            documentDistributee = itemView.findViewById(R.id.document_distributee);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonView = itemView.findViewById(R.id.button_view);
            cardView = itemView.findViewById(R.id.cardView);

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            dbRef = FirebaseDatabase.getInstance().getReference("ReceivedDocuments").child(currentUser.getUid());

        }
    }

}
