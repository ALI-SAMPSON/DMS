package io.zentechgh.dms.mobile.app.adapter;

import android.content.Context;
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

public class RecyclerViewAdapterDelete extends RecyclerView.Adapter<RecyclerViewAdapterDelete.ViewHolder> {

    // global variables
    private Context mCtx;
    private List<Documents> documentsList;

    // default constructor
    public RecyclerViewAdapterDelete(){}

    // defaultless constructor
    public RecyclerViewAdapterDelete(Context mCtx, List<Documents> documentsList){
        this.mCtx = mCtx;
        this.documentsList = documentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        // inflating layout resource file
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_delete_document,viewGroup,false);

        // getting an instance of the viewHolder class
        ViewHolder viewHolder = new ViewHolder(view);

        // returning view
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // getting the position of each document
        Documents documents = documentsList.get(position);

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
