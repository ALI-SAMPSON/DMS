package io.zentechgh.dms.mobile.app.fragment.user;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.constants.Constants;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.user.UserHomeActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddScannedDocumentFragment extends Fragment implements
        View.OnClickListener,EasyPermissions.PermissionCallbacks {

    // Global variables
    View view;

    int REQUEST_CODE = 99;

    private static final int PERMISSION_CODE = 123;

    ProgressBar progressBar;

    ProgressDialog progressDialog;

    // uri of the document scanned
    Uri documentUri;

    // uri of the document scanned
    String documentUrl;

    FirebaseAuth mAuth;

    FirebaseUser currentUser;

    DatabaseReference documentRef, userRef;

    private StorageReference mStorageReference;

    private StorageTask mDocumentUploadTask;

    // models
    Users users;
    Documents documents;

    FloatingActionButton fab_main;
    FloatingActionButton fab_camera;
    FloatingActionButton fab_gallery;
    // boolean to check if fab_main is open
    boolean isOpen = false;

    Animation fab_open_animation;
    Animation fab_close_animation;
    Animation fab_clockwise_animation;
    Animation fab_anticlockwise_animation;

    ImageView scannedImageView;

    EditText editTextTitle,editTextComment;

    Button uploadButton,cancelButton;

    private AppCompatSpinner spinnerTag;
    private ArrayAdapter<CharSequence> arrayAdapter;

    UserHomeActivity applicationContext;

    public AddScannedDocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (UserHomeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_add_scanned_document, container, false);

        // initialization of the FirebaseAuth and FirebaseUser classes
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        //initializing models
        users = new Users();

        documents = new Documents();

        documentRef = FirebaseDatabase.getInstance().getReference("Documents");

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        // getting instance of Storage Reference
        mStorageReference = FirebaseStorage.getInstance().getReference(Constants.DOC_STORAGE_PATH);

        scannedImageView = view.findViewById(R.id.scannedImageView);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextComment = view.findViewById(R.id.editTextComment);

        uploadButton = view.findViewById(R.id.upload_btn);
        cancelButton = view.findViewById(R.id.cancel_btn);

        // initializing the spinnerView and adapter
        spinnerTag = view.findViewById(R.id.spinnerTag);
        arrayAdapter = ArrayAdapter.createFromResource(applicationContext,R.array.tags,R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTag.setAdapter(arrayAdapter);

        // getting reference to views
        fab_main = view.findViewById(R.id.fab_main);
        fab_camera = view.findViewById(R.id.fab_camera);
        fab_gallery = view.findViewById(R.id.fab_gallery);

        // initializing the animation variables
        fab_open_animation = AnimationUtils.loadAnimation(applicationContext,R.anim.fab_open);
        fab_close_animation = AnimationUtils.loadAnimation(applicationContext,R.anim.fab_close);
        fab_clockwise_animation = AnimationUtils.loadAnimation(applicationContext,R.anim.rotate_fab_clockwise);
        fab_anticlockwise_animation = AnimationUtils.loadAnimation(applicationContext,R.anim.rotate_fab_anticlockwise);

        //setting onClickListeners on fab(s)
        fab_gallery.setOnClickListener(this);
        fab_camera.setOnClickListener(this);

        //setting onClickListeners on fab(s)
        uploadButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        // onClickListener for main fab
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checking if floating button main is opened
                if(isOpen){
                    fab_gallery.startAnimation(fab_close_animation);
                    fab_camera.startAnimation(fab_close_animation);
                    fab_main.startAnimation(fab_anticlockwise_animation);
                    // making the two fab(s) clickable
                    fab_gallery.setClickable(false);
                    fab_camera.setClickable(false);
                    isOpen = false;
                }
                else{
                    // starting animation on floating action buttons
                    fab_gallery.startAnimation(fab_open_animation);
                    fab_camera.startAnimation(fab_open_animation);
                    fab_main.startAnimation(fab_clockwise_animation);
                    // making the two fab(s) clickable
                    fab_gallery.setClickable(true);
                    fab_camera.setClickable(true);
                    isOpen = true;
                }
            }
        });

        progressBar = view.findViewById(R.id.progressBar);

        // method call
        changeProgressDialogBackground();

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.fab_camera:
                // method call
                openCamera();
                break;
            case R.id.fab_gallery:

                // method call
                openGallery();

                break;

            case R.id.upload_btn:
                // method call
                checkIfFieldEmpty();
                break;
            case R.id.cancel_btn:
                cancelUpload();
                break;

        }

    }

    // Method to start camera to begin the scanning process
    @AfterPermissionGranted(PERMISSION_CODE)
    private void openCamera(){

        String[] perms = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        // checks if permission is already enabled
        if(EasyPermissions.hasPermissions(applicationContext,perms)){
            // opens camera if permission has been granted already
            int REQUEST_CODE = 99;
            int preference = ScanConstants.OPEN_CAMERA;
            Intent intent = new Intent(applicationContext, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, REQUEST_CODE);
        }
        else{
            EasyPermissions.requestPermissions(this,
                    getString(R.string.text_permission), PERMISSION_CODE,perms);
        }


    }


    // clear fields
    private void cancelUpload(){
        editTextTitle.setText("");
        editTextComment.setText("");
    }

    // Method to open gallery to begin the scanning process
    private void openGallery(){

        String[] perms = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if(EasyPermissions.hasPermissions(applicationContext,perms)){
            // opens camera if permission has been granted already
            int REQUEST_CODE = 99;
            int preference = ScanConstants.OPEN_MEDIA;
            Intent intent = new Intent(applicationContext, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, REQUEST_CODE);
        }
        else{
            EasyPermissions.requestPermissions(this,
                    getString(R.string.text_permission),PERMISSION_CODE, perms);
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == Activity.RESULT_OK) {
            // getting uri from scanned image
            documentUri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            // library to load image uri into imageView
            Picasso.get().load(documentUri).into(scannedImageView);

        }

        // open AppSettingDialog
        if(requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //openCamera();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    // method to change ProgressDialog background color based on the android version of user's phone
    private void changeProgressDialogBackground(){

        // if the build sdk version >= android 5.0
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //sets the background color according to android version
            progressDialog = new ProgressDialog(applicationContext, ProgressDialog.THEME_HOLO_DARK);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(getString(R.string.text_adding_document));
            progressDialog.setMessage(getString(R.string.text_please_wait));
        }
        //else do this
        else{
            //sets the background color according to android version
            progressDialog = new ProgressDialog(applicationContext, ProgressDialog.THEME_HOLO_LIGHT);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(getString(R.string.text_adding_document));
            progressDialog.setMessage(getString(R.string.text_please_wait));
        }

    }

    // check to make sure fields are not left empty
    private void checkIfFieldEmpty(){

        String title = editTextTitle.getText().toString();
        String comment = editTextComment.getText().toString();

        if(scannedImageView.getDrawable() == null){
            YoYo.with(Techniques.Flash).playOn(scannedImageView);
            Toast.makeText(applicationContext, getString(R.string.text_add_image),
                    Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(title)){
            YoYo.with(Techniques.Shake).playOn(editTextTitle);
            editTextTitle.setError(getString(R.string.error_empty_field));
        }

        if(TextUtils.isEmpty(comment)){
            YoYo.with(Techniques.Shake).playOn(editTextComment);
            editTextComment.setError(getString(R.string.error_empty_field));
        }

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(comment) && scannedImageView.getDrawable() != null){
            // method call to upload document
            uploadDocument();
        }


    }

    // method to return file extension
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = applicationContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // method to upload the document file only
    private void  uploadDocument(){

        // checks to make sure document uri is not empty
        if(documentUri != null){

            // display dialog
            progressDialog.show();

            final StorageReference documentFileRef = mStorageReference
                    .child(System.currentTimeMillis() + "." + getFileExtension(documentUri));

            mDocumentUploadTask = documentFileRef.putFile(documentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // get the image Url of the file uploaded
                    documentFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // getting image uri and converting into string
                            Uri downloadUrl = uri;
                            documentUrl = downloadUrl.toString();

                            // method call to add upload file details to database
                            uploadDocumentDetails();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // display message if an exception occurs
                    Toast.makeText(applicationContext, " Failed : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            /*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });;
            */

        }
        else{
            // display a toast to notify user of no file selected
            Toast.makeText(applicationContext,"No document file selected",Toast.LENGTH_LONG).show();
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
        documents.setType(Constants.SCANNED_FILE);
        documents.setDistributee(distributee);
        documents.setSearch(searchField);

        documentRef.push().setValue(documents)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    // display an error message
                    Toast.makeText(applicationContext, getResources().getString(R.string.document_added), Toast.LENGTH_LONG).show();

                    // clear url after successful addition
                    clearDocumentUrl();

                }
                else{
                    // display an error message
                    Toast.makeText(applicationContext, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }

                // dismiss the dialog
                progressDialog.dismiss();
            }
        });


    }

    private void clearDocumentUrl(){
        documentUrl = null;
        documentUri = null;
    }

}

