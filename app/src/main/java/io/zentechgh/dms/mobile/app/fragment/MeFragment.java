package io.zentechgh.dms.mobile.app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.model.Received_Documents;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.HomeActivity;
import io.zentechgh.dms.mobile.app.ui.ReceivedDocumentActivity;
import io.zentechgh.dms.mobile.app.ui.SentDocumentActivity;
import maes.tech.intentanim.CustomIntent;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MeFragment extends Fragment implements
        View.OnClickListener, EasyPermissions.PermissionCallbacks{

    View view;

    ConstraintLayout constraintLayout;

    ImageView pick_image;

    CircleImageView profile_image;

    CardView sent_files,received_files;

    TextView tv_username, tv_role;

    private static final int PERMISSION_CODE = 124;

    private static final int REQUEST_CODE = 1;

    FirebaseUser currentUser;

    DatabaseReference userRef;

    Uri imageUri;

    String profileImageUrl;

    ProgressBar progressBar;

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

        tv_username = view.findViewById(R.id.tv_username);

        tv_role = view.findViewById(R.id.tv_role);

        sent_files = view.findViewById(R.id.card_view_sent);

        received_files = view.findViewById(R.id.card_view_received);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //userRef = FirebaseDatabase.getInstance().getReference("Users");

        progressBar =  view.findViewById(R.id.progressBar);

        // setting onClickListener on views
        pick_image.setOnClickListener(this);
        sent_files.setOnClickListener(this);
        received_files.setOnClickListener(this);

        // method call to load user information
        loadUserDetails();

        // returning view
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

            try {
                // loads image Uri using picasso library
                Picasso.get().load(imageUri).into(profile_image);

                // method call to update user profile
                updateProfile();
            }
            catch (Exception e){
                // display exception caught
                Toast.makeText(applicationContext,e.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }

    // method to return file extension
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = applicationContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void updateProfile(){

        final StorageReference imageRef = FirebaseStorage.getInstance()
                .getReference("Profile Pictures/" + System.currentTimeMillis() + getFileExtension(imageUri));

        if(imageUri != null && currentUser != null){

            // displays progressBar
            progressBar.setVisibility(View.VISIBLE);

            imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           Uri downloadUrl = uri;

                            // converting imageUri to string
                            profileImageUrl = downloadUrl.toString();

                            // method call to save profile
                            saveProfileInfo();

                        }
                    });

                    // hides progressBar
                    progressBar.setVisibility(View.GONE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // hides progressBar
                    progressBar.setVisibility(View.GONE);

                    // display an error message
                    Snackbar.make(constraintLayout,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            });

        }


    }

    // save profile info
    private void saveProfileInfo(){

        // test condition
        if(currentUser != null && imageUri!= null){

            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            currentUser.updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        // update imageUrl field anytime user update photo
                        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                        HashMap<String,Object> updateProfile = new HashMap<>();
                        updateProfile.put("imageUrl",profileImageUrl);
                        userRef.updateChildren(updateProfile);

                        // display a toast if a successful update is made
                        Toast.makeText(applicationContext, "Profile Photo updated successfully", Toast.LENGTH_LONG).show();

                    }
                    else{

                        // display an error message
                        Snackbar.make(constraintLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                    }


                }
            });

        }

    }

    private void loadUserDetails(){

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.getValue(Users.class);

                assert user != null;

                // set username to textView
                tv_username.setText(user.getUsername());

                if(user.getImageUrl() != null){
                    // load image uri if there is imageUrl
                    Glide.with(applicationContext).load(user.getImageUrl()).into(profile_image);
                }
                else{
                    // load the default icon
                    profile_image.setImageResource(R.drawable.profile_icon);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display an error message
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });

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
