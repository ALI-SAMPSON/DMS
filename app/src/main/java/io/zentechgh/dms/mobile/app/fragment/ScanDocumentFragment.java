package io.zentechgh.dms.mobile.app.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.IOException;
import java.util.List;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.ui.HomeActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class ScanDocumentFragment extends Fragment implements
        View.OnClickListener,EasyPermissions.PermissionCallbacks {

    // Global variables
    View view;

    int REQUEST_CODE = 99;

    private static final int PERMISSION_CODE = 123;

    FloatingActionButton fab_main;
    FloatingActionButton fab_camera;
    FloatingActionButton fab_gallery;
    // boolean to check if fab_main is open
    boolean isOpen = false;

    Animation fab_open_animation;
    Animation fab_close_animation;
    Animation fab_clockwise_animation;
    Animation fab_anticlockwise_animation;

    Button scan_button;
    Button gallery_button;
    ImageView scannedImageView;

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

        // getting reference to views
        fab_main = view.findViewById(R.id.fab_main);
        fab_camera = view.findViewById(R.id.fab_camera);
        fab_gallery = view.findViewById(R.id.fab_gallery);

        // initializing the animation variables
        fab_open_animation = AnimationUtils.loadAnimation(applicationContext,R.anim.fab_open);
        fab_close_animation = AnimationUtils.loadAnimation(applicationContext,R.anim.fab_close);
        fab_clockwise_animation = AnimationUtils.loadAnimation(applicationContext,R.anim.rotate_fab_clockwise);
        fab_anticlockwise_animation = AnimationUtils.loadAnimation(applicationContext,R.anim.rotate_fab_anticlockwise);

        //scan_button = view.findViewById(R.id.scan_button);
        //gallery_button = view.findViewById(R.id.gallery_button);
        scannedImageView = view.findViewById(R.id.scanned_file);

        //setting onClickListeners on views
        fab_gallery.setOnClickListener(this);
        fab_camera.setOnClickListener(this);
        //scan_button.setOnClickListener(this);
        //gallery_button.setOnClickListener(this);

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
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), uri);
                applicationContext.getContentResolver().delete(uri, null, null);
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
}

