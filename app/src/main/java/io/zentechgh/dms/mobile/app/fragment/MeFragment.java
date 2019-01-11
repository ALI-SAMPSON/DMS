package io.zentechgh.dms.mobile.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Received_Documents;
import io.zentechgh.dms.mobile.app.ui.HomeActivity;
import io.zentechgh.dms.mobile.app.ui.ReceivedDocumentActivity;
import io.zentechgh.dms.mobile.app.ui.SentDocumentActivity;
import maes.tech.intentanim.CustomIntent;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MeFragment extends Fragment implements
        View.OnClickListener, EasyPermissions.PermissionCallbacks{

    View view;

    RelativeLayout constraintLayout;

    ImageView pick_image;

    CircleImageView profile_image;

    CardView sent_files,received_files;

    TextView tv_role;

    private static final int PERMISSION_CODE = 124;

    private static final int REQUEST_CODE = 1;

    Uri imageUri;

    HomeActivity applicationContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (HomeActivity) context;
    }

    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_me, container, false);
        // Inflate the layout for this fragment
        //view =  inflater.inflate(R.layout.fragment_me, container, false);

        constraintLayout = view.findViewById(R.id.constraintLayout);

        pick_image = view.findViewById(R.id.pick_image);

        profile_image = view.findViewById(R.id.profile_image);

        tv_role = view.findViewById(R.id.tv_role);

        sent_files = view.findViewById(R.id.card_view_sent);

        received_files = view.findViewById(R.id.card_view_received);


        // setting onClickListener on views
        pick_image.setOnClickListener(this);
        sent_files.setOnClickListener(this);
        received_files.setOnClickListener(this);


        return view;

    }

    // onClick listener method for views
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.pick_image:

                // method call to open gallery
                openGallery();

                break;

            case R.id.card_view_sent:

                //Intent intent = new Intent(applicationContext, ReceivedDocumentActivity.class);

                startActivity(new Intent(applicationContext, SentDocumentActivity.class));

                CustomIntent.customType(applicationContext,"fadein-to-fadeout");

                break;

            case R.id.card_view_received:

                startActivity(new Intent(applicationContext,ReceivedDocumentActivity.class));

                CustomIntent.customType(applicationContext,"fadein-to-fadeout");

                break;
        }

    }


    public void openGallery(){
        String[] perms = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        // checks if permission is already enabled
        if(EasyPermissions.hasPermissions(applicationContext,perms)){
            // opens camera if permission has been granted already
            Intent intentPick = new Intent();
            intentPick.setAction(Intent.ACTION_GET_CONTENT);
            intentPick.setType("image/*");
            startActivityForResult(Intent.createChooser(intentPick,"Select Profile Picture"), REQUEST_CODE);

            // add a fading animation when opening gallery
            CustomIntent.customType(applicationContext,"fadein-to-fadeout");
        }
        else{
            EasyPermissions.requestPermissions(applicationContext,
                    getString(R.string.text_permission_select_profile), PERMISSION_CODE,perms);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null){

            // get image data passed
            imageUri = data.getData();

            // loads image Uri using picasso library
            Picasso.get().load(imageUri).centerCrop().into(profile_image);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
