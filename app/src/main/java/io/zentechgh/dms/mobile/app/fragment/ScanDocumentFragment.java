package io.zentechgh.dms.mobile.app.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.UploadTask;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Documents;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.HomeActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("ALL")
public class ScanDocumentFragment extends Fragment implements
        View.OnClickListener,EasyPermissions.PermissionCallbacks {

    // Global variables
    View view;

    int REQUEST_CODE = 99;

    private static final int PERMISSION_CODE = 123;

    ProgressDialog progressDialog;

    // uri of the document scanned
    Uri documentUri;

    // uri of the document scanned
    String documentUrl;

    FirebaseAuth mAuth;

    FirebaseUser currentUser;

    DatabaseReference documentRef, userRef;

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

    EditText editTextName,editTextComment;

    Button uploadButton,cancelButton;

    private AppCompatSpinner spinnerTag;
    private ArrayAdapter<CharSequence> arrayAdapter;

    HomeActivity applicationContext;

    public ScanDocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (HomeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_scan_document, container, false);

        // initialization of the FirebaseAuth and FirebaseUser classes
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        //initializing models
        users = new Users();
        documents = new Documents();

        documentRef = FirebaseDatabase.getInstance().getReference("Documents");

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        scannedImageView = view.findViewById(R.id.scannedImageView);

        editTextName = view.findViewById(R.id.editTextName);
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

        scannedImageView = view.findViewById(R.id.scannedImageView);

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

    private void checkIfFieldEmpty(){

        String name = editTextName.getText().toString();
        String comment = editTextComment.getText().toString();

        if(scannedImageView.getDrawable() == null){
            YoYo.with(Techniques.Wobble).playOn(scannedImageView);
            Toast.makeText(applicationContext, "please add an image of the document to proceed!",
                    Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(name)){
            YoYo.with(Techniques.Shake).playOn(editTextName);
            editTextName.setError(getString(R.string.error_empty_field));
        }
        if(TextUtils.isEmpty(comment)){
            YoYo.with(Techniques.Shake).playOn(editTextComment);
            editTextComment.setError(getString(R.string.error_empty_field));
        }
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(comment) && scannedImageView.getDrawable() != null){
            // method call
            uploadDocument();
        }

    }

    // method to upload entire document
    private void uploadDocument(){

        // display dialog
        progressDialog.show();

        // getting text
        final String name = editTextName.getText().toString();
        final String comment = editTextComment.getText().toString();
        final String tag = spinnerTag.getSelectedItem().toString();

        documents.setName(name);
        documents.setComment(comment);
        documents.setTag(tag);
        documents.setDocumentUrl(documentUrl);

        documentRef.child(currentUser.getDisplayName())
                .setValue(documents).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    // updates users field with document uploaded
                    HashMap<String,Object> updateUserField = new HashMap<>();
                    updateUserField.put("documentName",name);
                    updateUserField.put("documentComment",comment);
                    updateUserField.put("documentTag",tag);
                    updateUserField.put("documentUrl",documentUrl);
                    userRef.child("Uploaded Documents").push().setValue(updateUserField);

                    // method call to upload document file
                    uploadDocumentFile();

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

    // method to upload the document file only
    private void uploadDocumentFile(){

        final StorageReference documentFileRef = FirebaseStorage.getInstance()
                .getReference(" Documents /" + System.currentTimeMillis() + ".jpg");

        if(documentUri != null){

            documentFileRef.putFile(documentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    documentFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // getting image uri and converting into string
                            Uri downloadUrl = uri;
                            documentUrl = downloadUrl.toString();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // display an error message
                    Toast.makeText(applicationContext, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }


    }

    // clear fields
    private void cancelUpload(){
        editTextName.setText("");
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
            documentUri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), documentUri);
                applicationContext.getContentResolver().delete(documentUri, null, null);
                scannedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            progressDialog.setTitle("Uploading Document");
            progressDialog.setMessage("please wait...");
        }
        //else do this
        else{
            //sets the background color according to android version
            progressDialog = new ProgressDialog(applicationContext, ProgressDialog.THEME_HOLO_LIGHT);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Uploading Document...");
            progressDialog.setMessage("please wait...");
        }

    }
}

