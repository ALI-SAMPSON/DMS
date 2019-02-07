package io.zentechgh.dms.mobile.app.adapter.user;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.constants.Constants;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.ui.user.ViewDocumentUserActivity;
import io.zentechgh.dms.mobile.app.ui.user.ViewOtherDocumentsUserActivity;
import maes.tech.intentanim.CustomIntent;

import static android.content.Context.DOWNLOAD_SERVICE;

public class RecyclerViewAdapterManage extends RecyclerView.Adapter<RecyclerViewAdapterManage.ViewHolder> {

    // global variables
    private Context mCtx;
    private List<Documents> documentsList;

    ProgressDialog progressDialog;

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
        viewHolder.documentType.setText(" Type : " + documents.getType());

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


        // onclick listener for image view
        viewHolder.documentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(documents.getType().equals(Constants.PDF) || documents.getType().equals(Constants.PPT)
                        || documents.getType().equals(Constants.DOC) || documents.getType().equals(Constants.XLSX)){

                    // open file to view
                    Intent intent = new Intent(mCtx,ViewOtherDocumentsUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    // passing strings
                    intent.putExtra("document_url",documents.getDocumentUrl());
                    intent.putExtra("document_title",documents.getTitle());
                    intent.putExtra("document_tag",documents.getTag());
                    intent.putExtra("document_type",documents.getType());
                    intent.putExtra("document_comment", documents.getComment());
                    intent.putExtra("document_distributee", documents.getDistributee());
                    mCtx.startActivity(intent);

                    // adding an intent transition from left-to-right
                    //CustomIntent.customType(mCtx,"fadein-to-fadeout");


                }
                else{
                    // open file to view
                    Intent intent = new Intent(mCtx,ViewDocumentUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    // passing strings
                    intent.putExtra("document_url",documents.getDocumentUrl());
                    intent.putExtra("document_title",documents.getTitle());
                    intent.putExtra("document_tag",documents.getTag());
                    intent.putExtra("document_type",documents.getType());
                    intent.putExtra("document_comment", documents.getComment());
                    intent.putExtra("document_distributee", documents.getDistributee());
                    mCtx.startActivity(intent);

                    // adding an intent transition from left-to-right
                    //CustomIntent.customType(mCtx,"fadein-to-fadeout");
                }

            }
        });


        // onclick listener for view button
        viewHolder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(documents.getType().equals(Constants.PDF) || documents.getType().equals(Constants.PPT)
                        || documents.getType().equals(Constants.DOC) || documents.getType().equals(Constants.XLSX)){

                    // open file to view
                    Intent intent = new Intent(mCtx,ViewOtherDocumentsUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    // passing strings
                    intent.putExtra("document_url",documents.getDocumentUrl());
                    intent.putExtra("document_title",documents.getTitle());
                    intent.putExtra("document_tag",documents.getTag());
                    intent.putExtra("document_type",documents.getType());
                    intent.putExtra("document_comment", documents.getComment());
                    intent.putExtra("document_distributee", documents.getDistributee());
                    mCtx.startActivity(intent);

                    // adding an intent transition from left-to-right
                    //CustomIntent.customType(mCtx,"fadein-to-fadeout");

                }
                else{
                    // open file to view
                    Intent intent = new Intent(mCtx,ViewDocumentUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    // passing strings
                    intent.putExtra("document_url",documents.getDocumentUrl());
                    intent.putExtra("document_title",documents.getTitle());
                    intent.putExtra("document_tag",documents.getTag());
                    intent.putExtra("document_type",documents.getType());
                    intent.putExtra("document_comment", documents.getComment());
                    intent.putExtra("document_distributee", documents.getDistributee());
                    mCtx.startActivity(intent);

                    // adding an intent transition from left-to-right
                    //CustomIntent.customType(mCtx,"fadein-to-fadeout");
                }

            }
        });

        // onclick listener for download button
        viewHolder.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** Code to download file to phone storage***/

                DownloadManager downloadManager = (DownloadManager)mCtx.getSystemService(DOWNLOAD_SERVICE);

                //document Url here
                String downloadUrl = documents.getDocumentUrl();

                Uri uri = Uri.parse(downloadUrl);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                //request.setDescription("Downloading File");
                long id =  downloadManager.enqueue(request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle(uri.toString()) // passing the uri of the file as the title
                        .setDescription("Document File Downloading...!")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/ZDMS/"+uri
                                +"."+getFileExtension(Uri.parse(downloadUrl)))
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED));

            }
        });


    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = mCtx.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public long downloadFile(Context context, String fileName,
                             String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        return downloadmanager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return documentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        // instance variables
        ImageView documentImage;
        TextView documentTitle;
        TextView documentType;
        TextView buttonView;
        TextView buttonDownload;
        CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            documentImage = itemView.findViewById(R.id.document_image);
            documentTitle = itemView.findViewById(R.id.tv_document_title);
            documentType = itemView.findViewById(R.id.tv_document_type);
            buttonView = itemView.findViewById(R.id.button_view);
            buttonDownload = itemView.findViewById(R.id.button_download);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }

}
