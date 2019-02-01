package io.zentechgh.dms.mobile.app.adapter.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.constants.Constants;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.ui.user.AssignDocumentToUserActivity;
import io.zentechgh.dms.mobile.app.ui.user.ViewDocumentUserActivity;
import maes.tech.intentanim.CustomIntent;

public class RecyclerViewAdapterAssign extends RecyclerView.Adapter<RecyclerViewAdapterAssign.ViewHolder> {

    // global variables
    private Context mCtx;
    private List<Documents> documentsList;

    // default constructor
    public RecyclerViewAdapterAssign(){}

    // defaultless constructor
    public RecyclerViewAdapterAssign(Context mCtx, List<Documents> documentsList){
        this.mCtx = mCtx;
        this.documentsList = documentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_assign_document,viewGroup,false);

        // getting an instance of the viewHolder class
        ViewHolder viewHolder = new ViewHolder(view);

        // returning view
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        // getting the position of each document
        final Documents documents = documentsList.get(position);

        // getting text from the database and setting them to respective views
        viewHolder.documentTitle.setText(" Title : " + documents.getTitle());
        viewHolder.documentType.setText(" Tag : " + documents.getType());

        // checking if the document is not equal to null
        if(documents.getDocumentUrl() != null && documents.getType().equals(Constants.DOC)){
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_XY);
            viewHolder.documentImage.setImageResource(R.mipmap.doc_image);
        }
        else if(documents.getDocumentUrl() != null && documents.getType().equals(Constants.PPT)){
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_XY);
            viewHolder.documentImage.setImageResource(R.mipmap.ppt_image);
        }
        else if(documents.getDocumentUrl() != null && documents.getType().equals(Constants.PDF)){
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            viewHolder.documentImage.setImageResource(R.mipmap.pdf_image);
        }
        else if(documents.getDocumentUrl() != null && documents.getType().equals(Constants.XLSX)){
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            viewHolder.documentImage.setImageResource(R.mipmap.xlsx_image);
        }
        else {
            // scaling image and setting it to imageView
            viewHolder.documentImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(mCtx).load(documents.getDocumentUrl()).into(viewHolder.documentImage);
        }


        // set OnClick Listener for share button on cardview(document)
        viewHolder.buttonAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something

                // creating alertDialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx)
                        .setTitle(R.string.title_assign)
                        .setMessage(R.string.text_confirm_assign);

                alertDialog.setPositiveButton(R.string.text_proceed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //start AssignDocument activity and passes document details to it
                      Intent intentAssign = new Intent(mCtx,AssignDocumentToUserActivity.class);
                        intentAssign.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentAssign.putExtra("document_url",documents.getDocumentUrl());
                        intentAssign.putExtra("document_title",documents.getTitle());
                        intentAssign.putExtra("document_tag",documents.getTag());
                        intentAssign.putExtra("document_type",documents.getType());
                        intentAssign.putExtra("document_comment", documents.getComment());
                        intentAssign.putExtra("document_search", documents.getSearch());
                        intentAssign.putExtra("document_distributee",documents.getDistributee());
                        ///intentAssign.putExtra("sent_by", viewHolder.currrentUser.getDisplayName());
                      mCtx.startActivity(intentAssign);

                      // storing the information in sharePreferences
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mCtx).edit();
                        editor.putString("document_url",documents.getDocumentUrl());
                        editor.putString("document_type",documents.getType());
                        editor.putString("document_title",documents.getTitle());
                        editor.putString("document_tag",documents.getTag());
                        editor.putString("document_comment",documents.getComment());
                        editor.putString("document_search",documents.getSearch());
                        editor.putString("document_distributee",documents.getDistributee());
                        editor.apply();


                    }
                });

                alertDialog.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
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

        // set OnClick Listener for onItem click(document)
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something

                // creating alertDialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx)
                        .setTitle(R.string.title_assign)
                        .setMessage(R.string.text_confirm_assign);

                alertDialog.setPositiveButton(R.string.text_proceed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //start AssignDocument activity and passes document details to it
                        Intent intentAssign = new Intent(mCtx,AssignDocumentToUserActivity.class);
                        intentAssign.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentAssign.putExtra("document_url",documents.getDocumentUrl());
                        intentAssign.putExtra("document_title",documents.getTitle());
                        intentAssign.putExtra("document_tag",documents.getTag());
                        intentAssign.putExtra("document_type",documents.getType());
                        intentAssign.putExtra("document_comment", documents.getComment());
                        intentAssign.putExtra("document_search", documents.getSearch());
                        intentAssign.putExtra("document_distributee",documents.getDistributee());
                        ///intentAssign.putExtra("sent_by", viewHolder.currrentUser.getDisplayName());
                        mCtx.startActivity(intentAssign);

                        // storing the information in sharePreferences
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mCtx).edit();
                        editor.putString("document_url",documents.getDocumentUrl());
                        editor.putString("document_title",documents.getTitle());
                        editor.putString("document_tag",documents.getTag());
                        editor.putString("document_type",documents.getType());
                        editor.putString("document_comment",documents.getComment());
                        editor.putString("document_search",documents.getSearch());
                        editor.putString("document_distributee",documents.getDistributee());
                        editor.apply();


                    }
                });

                alertDialog.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
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

        // on click listener for button to view document
        viewHolder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open file to view
                Intent intent = new Intent(mCtx,ViewDocumentUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // passing strings
                intent.putExtra("document_url",documents.getDocumentUrl());
                intent.putExtra("document_title",documents.getTitle());
                intent.putExtra("document_tag",documents.getTag());
                intent.putExtra("document_type",documents.getType());
                intent.putExtra("document_comment", documents.getComment());
                intent.putExtra("document_distributee", documents.getDistributee());
                mCtx.startActivity(intent);

                // adding an intent transition from left-to-right
                CustomIntent.customType(mCtx,"fadein-to-fadeout");
            }
        });


        // on click listener for imageView
        viewHolder.documentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open file to view
                Intent intent = new Intent(mCtx,ViewDocumentUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // passing strings
                intent.putExtra("document_url",documents.getDocumentUrl());
                intent.putExtra("document_title",documents.getTitle());
                intent.putExtra("document_tag",documents.getTag());
                intent.putExtra("document_type",documents.getType());
                intent.putExtra("document_comment", documents.getComment());
                intent.putExtra("document_distributee", documents.getDistributee());
                mCtx.startActivity(intent);

                // adding an intent transition from left-to-right
                CustomIntent.customType(mCtx,"fadein-to-fadeout");
            }
        });

    }

    @Override
    public int getItemCount() {
        return documentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        // instance variables
        // instance variables
        ImageView documentImage;
        TextView documentTitle;
        TextView documentType;
        TextView buttonView;
        TextView buttonAssign;
        CardView cardView;

        FirebaseUser currentUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentImage = itemView.findViewById(R.id.document_image);
            documentTitle = itemView.findViewById(R.id.tv_document_title);
            documentType = itemView.findViewById(R.id.tv_document_type);
            buttonView = itemView.findViewById(R.id.button_view);
            buttonAssign = itemView.findViewById(R.id.button_assign);
            cardView = itemView.findViewById(R.id.cardView);

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

        }
    }

}
