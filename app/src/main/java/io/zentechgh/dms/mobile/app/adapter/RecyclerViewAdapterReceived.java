package io.zentechgh.dms.mobile.app.adapter;

import android.content.Context;
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
import io.zentechgh.dms.mobile.app.model.Received_Documents;
import io.zentechgh.dms.mobile.app.ui.ViewDocumentActivity;
import maes.tech.intentanim.CustomIntent;

public class RecyclerViewAdapterReceived extends RecyclerView.Adapter<RecyclerViewAdapterReceived.ViewHolder> {

    // global variables
    private Context mCtx;
    private List<Received_Documents> documentsList;

    // default constructor
    public RecyclerViewAdapterReceived(){}

    // defaultless constructor
    public RecyclerViewAdapterReceived(Context mCtx, List<Received_Documents> documentsList){
        this.mCtx = mCtx;
        this.documentsList = documentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_documents,viewGroup,false);

        // getting an instance of the viewHolder class
        ViewHolder viewHolder = new ViewHolder(view);

        // returning view
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // getting the position of each document
        final Received_Documents documents = documentsList.get(position);

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

        // onclick listener for view button
        viewHolder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open file to view
                Intent intent = new Intent(mCtx,ViewDocumentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // passing strings
                intent.putExtra("file",documents.getDocumentUrl());
                intent.putExtra("title",documents.getTitle());
                intent.putExtra("tag",documents.getTag());
                intent.putExtra("comment", documents.getComment());
                mCtx.startActivity(intent);

                // adding an intent transition from left-to-right
                CustomIntent.customType(mCtx,"left-to-right");

            }
        });

        // set OnClick Listener for each item in cardview(document)
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
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
        Button buttonView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentImage = itemView.findViewById(R.id.document_image);
            documentTitle = itemView.findViewById(R.id.document_title);
            documentTag = itemView.findViewById(R.id.document_tag);
            documentComment = itemView.findViewById(R.id.document_comment);
            buttonView = itemView.findViewById(R.id.button_view);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }

}
