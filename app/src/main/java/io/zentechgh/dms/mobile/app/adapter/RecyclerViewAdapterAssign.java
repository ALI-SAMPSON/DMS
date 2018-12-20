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

import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.ui.AssignDocumentToUserActivity;

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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // getting the position of each document
        final Documents documents = documentsList.get(position);

        // getting text from the database and setting them to respective views
        viewHolder.documentTitle.setText(" Title : " + documents.getTitle());
        viewHolder.documentTag.setText(" Tag : " + documents.getTag());
        viewHolder.documentComment.setText(" Comment : " + documents.getComment());

        // checking if the document is not equal to null
        if(documents.getDocumentUrl() == null){
            viewHolder.documentImage.setImageResource(R.drawable.scanned_file);
        }
        else {
            Glide.with(mCtx).load(documents.getDocumentUrl()).into(viewHolder.documentImage);
        }

        // getting details
        final String document_title = documents.getTitle();
        final String document_tag = documents.getTag();
        final String document_comment = documents.getComment();
        final String document_image_url = documents.getDocumentUrl();

        // set OnClick Listener for each item in cardview(document)
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
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
                        intentAssign.putExtra("documentTitle",document_title);
                        intentAssign.putExtra("documentTag",document_tag);
                        intentAssign.putExtra("documentComment",document_comment);
                        intentAssign.putExtra("documentImage",document_image_url);
                      intentAssign.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                      mCtx.startActivity(intentAssign);

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
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentImage = itemView.findViewById(R.id.document_image);
            documentTitle = itemView.findViewById(R.id.document_title);
            documentTag = itemView.findViewById(R.id.document_tag);
            documentComment = itemView.findViewById(R.id.document_comment);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }

}
