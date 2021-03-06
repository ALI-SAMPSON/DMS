package io.zentechgh.dms.mobile.app.ui.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.fragment.user.AddScannedDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.user.AssignDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.user.ManageDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.user.MeFragment;
import io.zentechgh.dms.mobile.app.helper.BottomNavigationViewHelper;
import io.zentechgh.dms.mobile.app.model.Users;
import io.zentechgh.dms.mobile.app.ui.SignInActivity;
import maes.tech.intentanim.CustomIntent;

@SuppressWarnings("ALL")
public class UserHomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{

    // Global views declaration
    Toolbar toolbar;
    TextView toolbar_title;

    TextView tv_welcome_title;

    BottomNavigationView bottom_nav_view;

    CircleImageView profile_image;

    FirebaseAuth mAuth;

    FirebaseUser currentUser;

    DatabaseReference userRef;

    ProgressBar progressBar;

    ProgressDialog progressDialog;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar_title = findViewById(R.id.toolbar_title);
        // reference to imageView
        profile_image = findViewById(R.id.profile_image);
        setSupportActionBar(toolbar);


        bottom_nav_view =  findViewById(R.id.bottom_nav_view);

        // setting on navigationitemSelectedListener
        bottom_nav_view.setOnNavigationItemSelectedListener(this);

        // disables default shift animation
        BottomNavigationViewHelper.disableShiftMode(bottom_nav_view);
        bottom_nav_view.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        mAuth =  FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        tv_welcome_title = findViewById(R.id.tv_welcome_title);

        // sets visibility to visible
        tv_welcome_title.setVisibility(View.VISIBLE);

        progressBar = findViewById(R.id.progressBar);

        // method call to load user info
        loadUserProfile();

        // method call to change progressDialog style according to build version
        changeProgressDialogStyle();


    }

    // load user image into image View
    private void loadUserProfile(){

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                assert user != null;

                // display a welcome message together with users name
                tv_welcome_title.setText(" Welcome " + user.getUsername());

                if(user.getImageUrl() == null){

                    // loads default icon if user imageUrl is null
                    Glide.with(getApplicationContext()).load(R.drawable.profile_icon).into(profile_image);
                }
                else{

                    // loads user imageUrl into imageView if user imageUrl is not null
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display message if exception is thrown
                Toast.makeText(UserHomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_smile:

                // display a toast with the user's name
                Toast.makeText(UserHomeActivity.this, " Welcome " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();

                return true;

            case R.id.menu_share:

                // method call
                shareApp();

                return true;

            case R.id.menu_add_other_document:

                // opens the screen to add other document formats
                startActivity(new Intent(getApplicationContext(),AddOtherDocumentsActivity.class));

                CustomIntent.customType(this, "fadein-to_fadeout");

                break;

            case R.id.menu_sign_out:

                // method call to signOut User
                signOutUser();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        // reserving fragment for fragment selected
        Fragment selectedFragment = null;

        switch (menuItem.getItemId()){

            case R.id.menu_add:
                // assign resepective fragment to the fragment variable
                selectedFragment = new AddScannedDocumentFragment();
                toolbar_title.setText(getString(R.string.title_add_documents));
                break;

            case R.id.menu_manage:
                // assign resepective fragment to the fragment variable
                selectedFragment = new ManageDocumentFragment();
                toolbar_title.setText(getString(R.string.title_manage_documents));
                break;

            case R.id.menu_assign:
                // assign resepective fragment to the fragment variable
                selectedFragment = new AssignDocumentFragment();
                toolbar_title.setText(getString(R.string.title_assign_document));
                break;

            case R.id.menu_me:
                // assign resepective fragment to the fragment variable
                selectedFragment = new MeFragment();
                toolbar_title.setText(getString(R.string.title_my_account));
                break;

        }

        tv_welcome_title.setVisibility(View.GONE);

        // starts fragment depending on the one clicked
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();

        return true;
    }

    // method to share app link to other users
    public void shareApp(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String sharingSubject = "ZDMS";
        String sharingText = "https://play.google.com/store/apps/details?id=io.zentechgh.dms.mobile.app";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,sharingSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT,sharingText);
        startActivity(Intent.createChooser(sharingIntent,"Share with"));
    }

    // method that signs user out of the system
    private void signOutUser() {

        /* set theme style according the os version of device.
            e.g (sets theme of alert dialog to dark for all devices
           with os version 5.0 and above)*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            builder.setTitle(getString(R.string.text_sign_out));
            builder.setMessage(getString(R.string.sign_out_msg));

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    // show dialog
                    progressDialog.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // dismiss dialog
                            progressDialog.dismiss();

                            // do something
                            mAuth.signOut();

                            // restarts the activity
                            startActivity(new Intent(UserHomeActivity.this, SignInActivity.class));

                            // Add a custom animation ot the activity
                            CustomIntent.customType(UserHomeActivity.this, "fadein-to-fadeout");

                            // finish the activity
                            finish();

                        }
                    }, 3000);

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            //creates the alert dialog
            AlertDialog alertDialog = builder.create();

            // display the alertDialog
            alertDialog.show();

        }
         /* set theme style according the os version of device.
          e.g (sets theme of alert dialog to dark for all devices
          with os version 5.0 and below)*/
        else {

            builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setTitle(getString(R.string.text_sign_out));
            builder.setMessage(getString(R.string.sign_out_msg));

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    // show dialog
                    progressDialog.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // dismiss dialog
                            progressDialog.dismiss();

                            // do something
                            mAuth.signOut();

                            // restarts the activity
                            startActivity(new Intent(UserHomeActivity.this, SignInActivity.class));

                            // Add a custom animation ot the activity
                            CustomIntent.customType(UserHomeActivity.this, "fadein-to-fadeout");

                            // finish the activity
                            finish();

                        }
                    }, 3000);

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            //creates the alert dialog
            AlertDialog alertDialog = builder.create();

            // display the alertDialog
            alertDialog.show();

        }
    }

    // method to change ProgressDialog style based on the android version of user's phone
    private void changeProgressDialogStyle(){

        // if the build sdk version >= android 5.0
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //sets the background color according to android version
            progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("");
            progressDialog.setMessage("signing out...");
        }
        //else do this
        else{
            //sets the background color according to android version
            progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("");
            progressDialog.setMessage("signing out...");
        }

    }

}
