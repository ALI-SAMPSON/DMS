package io.zentechgh.dms.mobile.app.adapter.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import io.zentechgh.dms.mobile.app.constants.Constants;
import io.zentechgh.dms.mobile.app.model.SentDocuments;
import io.zentechgh.dms.mobile.app.ui.user.ViewDocumentUserActivity;
import maes.tech.intentanim.CustomIntent;

public class RecyclerViewAdapterSent extends RecyclerView.Adapter<RecyclerViewAdapterSent.ViewHolder> {

    // global variables
    private Context mCtx;
    private List<SentDocuments> documentsList;

    // default constructor
    public RecyclerViewAdapterSent(){}

    // defaultless constructor
    public RecyclerViewAdapterSent(Context mCtx, List<SentDocuments> documentsList){
        this.mCtx = mCtx;
        this.documentsList = documentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_my_documents_sent,viewGroup,false);

        // getting an instance of the viewHolder class
        ViewHolder viewHolder = new ViewHolder(view);

        // returning view
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        // getting the position of each document
        final SentDocuments sentDocuments = documentsList.get(position);

        // getting text from the database and setting them to respective views
        viewHolder.documentTitle.setText(" Title : " + sentDocuments.getTitle());
        viewHolder.documentTag.setText(" Tag : " + sentDocuments.getTag());
        viewHolder.documentComment.setText(" Comment : " + sentDocuments.getComment());
        viewHolder.documentDistributee.setText(" Distributee : " + sentDocuments.getDistributee());

        // checking if the document is not equal to null
        if(sentDocuments.getDocumentUrl() == null){
            viewHolder.documentImage.setImageResource(R.drawable.scanned_file);
        }
        else {
            Glide.with(mCtx).load(sentDocuments.getDocumentUrl()).into(viewHolder.documentImage);
        }

        // checking if the document is not equal to null
        if(sentDocuments.getDocumentUrl() != null && sentDocuments.getType().equals(Constants.DOC)){
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_XY);
            viewHolder.documentImage.setImageResource(R.mipmap.doc_image);
        }
        else if(sentDocuments.getDocumentUrl() != null && sentDocuments.getType().equals(Constants.PPT)){
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_XY);
            viewHolder.documentImage.setImageResource(R.mipmap.ppt_image);
        }
        else if(sentDocuments.getDocumentUrl() != null && sentDocuments.getType().equals(Constants.PDF)){
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            viewHolder.documentImage.setImageResource(R.mipmap.pdf_image);
        }
        else if(sentDocuments.getDocumentUrl() != null && sentDocuments.getType().equals(Constants.XLSX)){
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            viewHolder.documentImage.setImageResource(R.mipmap.xlsx_image);
        }
        else {
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(mCtx).load(sentDocuments.getDocumentUrl()).into(viewHolder.documentImage);
        }

        // onclick listener for image view to open document viewer
        viewHolder.documentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open file to view
                Intent intent = new Intent(mCtx,ViewDocumentUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // passing strings
                intent.putExtra("document_url",sentDocuments.getDocumentUrl());
                intent.putExtra("document_title",sentDocuments.getTitle());
                intent.putExtra("document_tag",sentDocuments.getTag());
                intent.putExtra("document_comment", sentDocuments.getComment());
                intent.putExtra("document_distributee", sentDocuments.getDistributee());
                mCtx.startActivity(intent);

                // adding an intent transition from left-to-right
                CustomIntent.customType(mCtx,"left-to-right");

            }
        });

        // onclick listener for view button
        viewHolder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open file to view
                Intent intent = new Intent(mCtx,ViewDocumentUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // passing strings
                intent.putExtra("document_url",sentDocuments.getDocumentUrl());
                intent.putExtra("document_title",sentDocuments.getTitle());
                intent.putExtra("document_tag",sentDocuments.getTag());
                intent.putExtra("document_comment", sentDocuments.getComment());
                intent.putExtra("document_distributee", sentDocuments.getDistributee());
                mCtx.startActivity(intent);

                // adding an intent transition from left-to-right
                CustomIntent.customType(mCtx,"left-to-right");

            }
        });

        // set OnClick Listener for each item in cardview(document)
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* set theme style according the os version of device.
                   e.g (sets theme of alert dialog to dark for all devices
                   with os version 5.0 and above)*/
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                    AlertDialog.Builder builder = new AlertDialog.Builder(mCtx,AlertDialog.THEME_DEVICE_DEFAULT_DARK);

                    builder.setTitle(R.string.text_delete);
                    builder.setMessage(R.string.message_confirm_delete);

                    builder.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            /**
                             * Code to delete selected room from database
                             */

                            SentDocuments selectedDocument = documentsList.get(position);

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

                    builder.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // dismiss dialog
                            dialog.dismiss();
                        }
                    });

                    //creates the alert dialog
                    AlertDialog alertDialog = builder.create();

                    // display the alertDialog
                    alertDialog.show();

                }

                 /* set theme style according the os version of device.
                   e.g (sets theme of alert dialog to light for all devices
                   with os version 5.0 and below)*/
                else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(mCtx,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

                    builder.setTitle(R.string.text_delete);
                    builder.setMessage(R.string.message_confirm_delete);

                    builder.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            /**
                             * Code to delete selected room from database
                             */

                            SentDocuments selectedDocument = documentsList.get(position);

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

                    builder.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // dismiss dialog
                            dialog.dismiss();
                        }
                    });

                    //creates the alert dialog
                    AlertDialog alertDialog = builder.create();

                    // display the alertDialog
                    alertDialog.show();
                }


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

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            dbRef = FirebaseDatabase.getInstance().getReference("SentDocuments").child(currentUser.getUid());

        }
    }

}
