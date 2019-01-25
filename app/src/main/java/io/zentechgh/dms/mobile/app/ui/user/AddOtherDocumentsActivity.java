package io.zentechgh.dms.mobile.app.ui.user;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.helper.Constants;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.model.Users;

public class AddOtherDocumentsActivity extends AppCompatActivity implements View.OnClickListener{

    ConstraintLayout constraintLayout;

    //this is the pic pdf code used in file chooser
    final int PICK_PDF_CODE = 2342;

    private static final int PERMISSION_CODE = 123;

    ProgressBar progressBar;

    ProgressDialog progressDialog;

    // uri of the document scanned
    Uri documentUri;

    // uri of the document scanned
    String documentUrl;

    FirebaseAuth mAuth;

    FirebaseUser currentUser;

    //the firebase objects for storage and database
    DatabaseReference documentRef, userRef;

    StorageReference mStorageReference;


    // models
    Users users;
    Documents documents;

    FloatingActionButton fab_main;

    // boolean to check if fab_main is open
    boolean isOpen = false;

    // these are the views
    ImageView imageView;

    EditText editTextTitle,editTextComment;

    Button uploadButton,cancelButton;

    private AppCompatSpinner spinnerTag;
    private ArrayAdapter<CharSequence> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_other_documents);

        constraintLayout = findViewById(R.id.constraintLayout);

        // initialization of the FirebaseAuth and FirebaseUser classes
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        //initializing models
        users = new Users();

        documents = new Documents();

        // getting firebase objects
        documentRef = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH);

        // getting reference to ids of views
        imageView = findViewById(R.id.scannedImageView);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextComment = findViewById(R.id.editTextComment);

        progressBar = findViewById(R.id.progressBar);

        uploadButton = findViewById(R.id.upload_btn);
        cancelButton = findViewById(R.id.cancel_btn);

        // initializing the spinnerView and adapter
        spinnerTag = findViewById(R.id.spinnerTag);
        arrayAdapter = ArrayAdapter.createFromResource(this,R.array.tags,R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTag.setAdapter(arrayAdapter);

        // getting reference to views
        fab_main = findViewById(R.id.fab_main);

        //attaching listeners to views
        fab_main.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_main:

                // method to get pdf file
                getFileFromStorage();

                break;

            case R.id.upload_btn:

                // uploads file to database and storage
                checkIfFieldEmpty();

                break;

            case R.id.cancel_btn:

                break;
        }
    }

    // check to make sure fields are not left empty (input validation)
    private void checkIfFieldEmpty(){

        String title = editTextTitle.getText().toString();
        String comment = editTextComment.getText().toString();

        /*if(imageView.getDrawable() == null){
            YoYo.with(Techniques.Flash).playOn(imageView);
            Toast.makeText(AddOtherDocumentsActivity.this, getString(R.string.text_add_image),
                    Toast.LENGTH_SHORT).show();
        }
        */

        if(TextUtils.isEmpty(title)){
            YoYo.with(Techniques.Shake).playOn(editTextTitle);
            editTextTitle.setError(getString(R.string.error_empty_field));
        }

        if(TextUtils.isEmpty(comment)){
            YoYo.with(Techniques.Shake).playOn(editTextComment);
            editTextComment.setError(getString(R.string.error_empty_field));
        }

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(comment)){
            // method call to upload document
            uploadDocument();
        }


    }

    //this function will get the pdf from the storage
    private void getFileFromStorage(){
        //for greater than lollipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intentPick = new Intent();
        intentPick.setType("application/*");
        intentPick.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentPick,"Select File"),PICK_PDF_CODE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //when the user choses the file
        if(requestCode == PICK_PDF_CODE && requestCode == RESULT_OK && data != null && data.getData() != null){
            //if a file is selected
            if(data.getData() != null){
                // storing file in uri format
                documentUri = data.getData();
                // load file into image view
                Picasso.get().load(documentUri).into(imageView);
            }
            else{

                // display message if no file is chosen
                Toast.makeText(getApplicationContext(),R.string.no_file_chosen,Toast.LENGTH_LONG).show();
            }
        }

    }

    //this method is uploading the file
    private void uploadDocument(){

        if(documentUri != null){
            // display the progressBar
            progressBar.setVisibility(View.VISIBLE);

            final StorageReference documentStorageRef = mStorageReference.child(Constants.STORAGE_PATH + System.currentTimeMillis() + ".pdf");

            documentStorageRef.putFile(documentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    documentStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri fileUri = uri;
                            // convert fileUri to string
                            documentUrl = fileUri.toString();

                           // method call to add upload file details to database
                           uploadDocumentDetails();

                        }
                    });

                    // hides the progressBar
                    progressBar.setVisibility(View.GONE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // display message if an exception occurs
                    Snackbar.make(constraintLayout," Failed : " + e.getMessage(),Snackbar.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // getting the upload progress of the file
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressBar.setProgress(Integer.parseInt( progress + "% Uploading..."));
                }
            });
        }

        else{
            // display a toast to notify user of no file selected
            Toast.makeText(AddOtherDocumentsActivity.this,
                    getResources().getString(R.string.no_file_chosen),Toast.LENGTH_LONG).show();
        }


    }

    // method to upload entire document
    private void uploadDocumentDetails(){

        // display dialog
        ///progressDialog.show();

        // stores the name of the user who uploaded the file
        String distributee = currentUser.getDisplayName();

        // getting text
        final String title = editTextTitle.getText().toString();
        final String comment = editTextComment.getText().toString();
        final String tag = spinnerTag.getSelectedItem().toString();
        // convert title to lowercase to help in easy search
        final String searchField = title.toLowerCase();

        documents.setTitle(title);
        documents.setTag(tag);
        documents.setComment(comment);
        documents.setDocumentUrl(documentUrl);
        documents.setDistributee(distributee);
        documents.setSearch(searchField);

        documentRef.push().setValue(documents)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            // display an error message
                            Toast.makeText(AddOtherDocumentsActivity.this,
                                    getResources().getString(R.string.document_added), Toast.LENGTH_LONG).show();

                            // clear url after successful addition
                            clearDocumentUrl();

                        }
                        else{
                            // display an error message
                            Toast.makeText(AddOtherDocumentsActivity.this,
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                        // hides the progressBar
                        progressBar.setVisibility(View.GONE);
                    }
                });


    }

    // clear memory of variables
    private void clearDocumentUrl(){
        documentUrl = null;
        documentUri = null;
    }


}
