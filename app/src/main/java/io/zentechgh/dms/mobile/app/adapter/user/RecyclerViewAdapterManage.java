package io.zentechgh.dms.mobile.app.adapter.user;

import android.app.DownloadManager;
import android.app.ProgressDialog;
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
                // method call to download file
                /*downloadFile(mCtx,documents.getTitle(),
                        getFileExtension(Uri.parse(documents.getDocumentUrl())),
                        "",documents.getDocumentUrl());
                        */

                progressDialog = new ProgressDialog(mCtx);
                progressDialog.setTitle(R.string.title_download);
                progressDialog.setMessage("please wait...");
                progressDialog.setProgress(0);
                progressDialog.show();

                // code to download file to phone storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://d-m-s-ca8f0.appspot.com/");
                StorageReference  islandRef = storageRef.child(documents.getTitle());

                File directory = new File(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                if(!directory.exists()) {
                    directory.mkdirs();
                }

                final File localFile = new File(directory,documents.getTitle());

                //final File localFile1 = File.createTempFile(documents.getTitle(),".pdf",directory);

                islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //Log.e("firebase ",";local item file created  created " +localFile.toString());
                        //  updateDb(timestamp,localFile.toString(),position);
                        Toast.makeText(mCtx, localFile.toString() + " downloaded successfully", Toast.LENGTH_LONG).show();

                        // dismiss dialog
                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //Log.e("firebase ",";local tem file not created  created " +exception.toString());
                        Toast.makeText(mCtx, "Failed : " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // getting the download progress of the file
                        int progress = (int)((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        // setting progress of the ProgressBar to the current progress of the file
                        progressDialog.setProgress(progress);
                    }
                });


            }
        });


    }

    /*private void downloadFile(String fileName){
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference downloadRef = storageRef.child(userPath+fileName);
        File fileNameOnDevice = new File(DOWNLOAD_DIR+"/"+fileName);

        downloadRef.getFile(fileNameOnDevice).addOnSuccessListener(
                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context,
                                "Downloaded the file",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context,
                        "Couldn't be downloaded",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

    private void downloadFile() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://d-m-s-ca8f0.appspot.com/");
        StorageReference  islandRef = storageRef.child("file.txt");

        File rootPath = new File(Environment.getExternalStorageDirectory(), "file_name");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,"imageName.txt");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local tem file created  created " +localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
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
                getSystemService(Context.DOWNLOAD_SERVICE);
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
        ImageButton buttonDownload;
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
