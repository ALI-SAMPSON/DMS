package io.zentechgh.dms.mobile.app.ui.user;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.storage.UploadTask;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.constants.Constants;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.model.Users;

public class AddOtherDocumentsActivity extends AppCompatActivity implements View.OnClickListener{

    ConstraintLayout constraintLayout;

    Toolbar toolbar;

    TextView toolbar_title;

    //this is the pic pdf code used in file chooser
    final int PICK_FILE_CODE = 86;

    private static final int PERMISSION_CODE = 9;

    ProgressBar progressBar;

    ProgressDialog progressDialog;

    // uri of the document scanned
    Uri documentUri; //URIS are URLS meant for local storage

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

    TextView tv_selected_file_name;

    EditText editTextTitle,editTextComment;


    Button chooseFile,submitButton,cancelButton;

    private AppCompatSpinner spinnerTag;
    private ArrayAdapter<CharSequence> arrayAdapterTag;

    private AppCompatSpinner spinnerType;
    private ArrayAdapter<CharSequence> arrayAdapterType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_other_documents);

        constraintLayout = findViewById(R.id.constraintLayout);

        toolbar = findViewById(R.id.toolbar);

        toolbar_title =  findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        // setting support action bar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        // attaching listener for toolbar navigation
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // initialization of the FirebaseAuth and FirebaseUser classes
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        //initializing models
        users = new Users();

        documents = new Documents();

        // getting firebase objects
        documentRef = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference(Constants.DOC_STORAGE_PATH);

        // getting reference to ids of views
        tv_selected_file_name = findViewById(R.id.tv_selected_file_name);

        editTextTitle = findViewById(R.id.editTextTitle);

        editTextComment = findViewById(R.id.editTextComment);

        progressBar = findViewById(R.id.progressBar);

        // getting reference to ids of views
        fab_main = findViewById(R.id.fab_main);
        chooseFile = findViewById(R.id.choose_file);
        submitButton = findViewById(R.id.submit_btn);
        cancelButton = findViewById(R.id.cancel_btn);

        // initializing the spinnerView and adapter
        spinnerTag = findViewById(R.id.spinnerTag);
        arrayAdapterTag = ArrayAdapter.createFromResource(this,R.array.tags,R.layout.spinner_item);
        arrayAdapterTag.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTag.setAdapter(arrayAdapterTag);

        spinnerType = findViewById(R.id.spinnerType);
        arrayAdapterType = ArrayAdapter.createFromResource(this,R.array.type,R.layout.spinner_item);
        arrayAdapterType.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerType.setAdapter(arrayAdapterType);


        //attaching listeners to views
        chooseFile.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        // method call to change theme of progress Dialog
        progressDialogStyle();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose_file:

                // method call to check for permission
                isPermissionGranted();

                break;

            case R.id.submit_btn:

                // uploads file to database and storage
                checkIfFieldEmpty();

                break;

            case R.id.cancel_btn:

                // method to clear fields
                clearFields();

                break;
        }
    }

    // check to make sure fields are not left empty (input validation)
    private void checkIfFieldEmpty(){

        String title = editTextTitle.getText().toString();
        String comment = editTextComment.getText().toString();

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
    private void isPermissionGranted(){

        //for greater than lollipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if(ContextCompat.checkSelfPermission(AddOtherDocumentsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            // method call top select pdf from storage
            browseDocuments();
        }
        else{
            // code to request permission from user to use external storage
            ActivityCompat.requestPermissions(AddOtherDocumentsActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_CODE);
        }


    }

    // method called if user has not granted permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // method call top select pdf from storage
            browseDocuments();
        }
        else{
            Toast.makeText(AddOtherDocumentsActivity.this,
                    getString(R.string.text_permission_read_storage), Toast.LENGTH_LONG).show();
        }
    }

    // allow user to select file from storage
    private void browseDocuments(){

        // array of documents formats to pick from device storage
        String [] mimeTypes = {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",  // .ppt & .pptx
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                "application/vnd.ms-access", // .cs (access file)
                "text/plain", // .txt
                "application/pdf", // .pdf
                "application/zip", // .zip
                "application/*" // .apk and other files
        };

        Intent intentBrowse = new Intent();
        intentBrowse.setAction(Intent.ACTION_GET_CONTENT);
        intentBrowse.addCategory(Intent.CATEGORY_OPENABLE);

        // checks os build version of device
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            intentBrowse.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if(mimeTypes.length > 0){
                intentBrowse.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            }
        }
        else{
            String mimeTypesStr = "";
            for(String mimeType : mimeTypes){
                mimeTypesStr += mimeType + "|";
            }
            intentBrowse.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }

        startActivityForResult(Intent.createChooser(intentBrowse,getString(R.string.choose_file)),PICK_FILE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // Check whether user has selected a file
        if(requestCode == PICK_FILE_CODE && resultCode == RESULT_OK && data != null){

            // return the uri of selected file
            documentUri = data.getData();

            // sets the text of the textView (no selected file) to the name of the file selected
            tv_selected_file_name.setText(data.getData().getLastPathSegment());

        }
        else{

            // display message if no file is chosen
            Toast.makeText(getApplicationContext(),R.string.no_file_selected,Toast.LENGTH_LONG).show();
        }
    }

    // method to return file extension
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //this method is uploading the file
    private void uploadDocument(){


        // display progress Dialog
        progressDialog.show();

        if(documentUri != null){

            // display the progressBar
            //progressBar.setVisibility(View.VISIBLE);

            final StorageReference documentStorageRef =
                    mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(documentUri));

            documentStorageRef.putFile(documentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    documentStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // getting the uri of uploaded file
                            Uri fileUri = uri;
                            // convert fileUri to string
                            documentUrl = fileUri.toString();

                           // method call to add upload file details to database
                            addDocumentDetailsToDatabase();

                        }
                    });

                    // dismiss dialog
                    progressDialog.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // display message if an exception occurs
                    Snackbar.make(constraintLayout," Failed : " + e.getMessage(),Snackbar.LENGTH_LONG).show();

                    // dismiss dialog
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // getting the upload progress of the file
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    // setting progress of the ProgressBar to the current progress of the file
                    progressDialog.setProgress(progress);
                }
            });
        }

        else{
            // display a toast to notify user of no file selected
            Toast.makeText(AddOtherDocumentsActivity.this,
                    getResources().getString(R.string.no_file_selected),Toast.LENGTH_LONG).show();
        }


    }

    // method to upload entire document
    private void addDocumentDetailsToDatabase(){

        // stores the name of the user who uploaded the file
        String distributee = currentUser.getDisplayName();

        // getting text
        final String title = editTextTitle.getText().toString();
        final String comment = editTextComment.getText().toString();
        final String tag = spinnerTag.getSelectedItem().toString();
        final String type = spinnerType.getSelectedItem().toString();
        // convert title to lowercase to help in easy search
        final String searchField = title.toLowerCase();

        documents.setTitle(title);
        documents.setType(type);
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

                    }
                });

    }

    // clear memory of variables
    private void clearDocumentUrl(){
        documentUrl = null;
        documentUri = null;
    }

    // clear memory of variables
    private void clearFields(){
        editTextTitle.setText("");
        editTextComment.setText("");
    }

    // change Progress Dialog style according build version
    private void progressDialogStyle(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            // display dark progress Dialog
            progressDialog = new ProgressDialog(AddOtherDocumentsActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(R.string.title_adding_document);
            progressDialog.setMessage(getString(R.string.text_please_wait));
            progressDialog.setProgress(0);
        }
        else{
            // display progress Bar
            progressDialog = new ProgressDialog(AddOtherDocumentsActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(R.string.title_adding_document);
            progressDialog.setMessage(getString(R.string.text_please_wait));
            progressDialog.setProgress(0);
        }


    }

}
