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

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.ui.user.ViewDocumentUserActivity;
import maes.tech.intentanim.CustomIntent;

public class RecyclerViewAdapterManage extends RecyclerView.Adapter<RecyclerViewAdapterManage.ViewHolder> {

    // global variables
    private Context mCtx;
    private List<Documents> documentsList;

    // default constructor
    public RecyclerViewAdapterManage(){}

    // defaultless constructor
    public RecyclerViewAdapterManage(Context mCtx, List<Documents> documentsList){
        this.mCtx = mCtx;
        this.documentsList = documentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_manage_document,viewGroup,false);

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
        viewHolder.documentTag.setText(" Tag : " + documents.getTag());
        viewHolder.documentComment.setText(" Comment : " + documents.getComment());
        viewHolder.distributee.setText(" Distributee : " + documents.getDistributee());

        // checking if the document is not equal to null
        if(documents.getDocumentUrl() == null){
            viewHolder.documentImage.setImageResource(R.drawable.scanned_file);
        }
        else {
            Glide.with(mCtx).load(documents.getDocumentUrl()).into(viewHolder.documentImage);
        }

        // onclick listener for image view
        viewHolder.documentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add a smooth zoom in animation before opening image in preview
                YoYo.with(Techniques.ZoomInUp).playOn(viewHolder.documentImage);

                // open file to view
                Intent intent = new Intent(mCtx,ViewDocumentUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // passing strings
                intent.putExtra("file",documents.getDocumentUrl());
                intent.putExtra("title",documents.getTitle());
                intent.putExtra("tag",documents.getTag());
                intent.putExtra("comment", documents.getComment());
                mCtx.startActivity(intent);

                // adding an intent transition from left-to-right
                CustomIntent.customType(mCtx,"fadein-to-fadeout");
            }
        });


        // onclick listener for view button
        viewHolder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // open file to view
                Intent intent = new Intent(mCtx,ViewDocumentUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // passing strings
                intent.putExtra("file",documents.getDocumentUrl());
                intent.putExtra("title",documents.getTitle());
                intent.putExtra("tag",documents.getTag());
                intent.putExtra("comment", documents.getComment());
                mCtx.startActivity(intent);

                // adding an intent transition from left-to-right
                CustomIntent.customType(mCtx,"fadein-to-fadeout");

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
        TextView distributee;
        ImageButton buttonView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentImage = itemView.findViewById(R.id.document_image);
            documentTitle = itemView.findViewById(R.id.tv_document_title);
            documentTag = itemView.findViewById(R.id.tv_document_tag);
            documentComment = itemView.findViewById(R.id.tv_document_comment);
            distributee = itemView.findViewById(R.id.tv_distributee);
            buttonView = itemView.findViewById(R.id.button_view);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }

}