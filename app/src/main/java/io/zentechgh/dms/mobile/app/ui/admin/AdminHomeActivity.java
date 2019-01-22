package io.zentechgh.dms.mobile.app.ui.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.fragment.admin.AddUsersFragment;
import io.zentechgh.dms.mobile.app.fragment.admin.ArchiveDocumentsFragment;
import io.zentechgh.dms.mobile.app.fragment.admin.ManageUsersFragment;
import io.zentechgh.dms.mobile.app.ui.SignInActivity;
import maes.tech.intentanim.CustomIntent;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;

    TextView toolbar_title;

    TextView tv_welcome_title;

    DrawerLayout drawer;

    NavigationView nav_view;

    ActionBarDrawerToggle toggle;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        tv_welcome_title = findViewById(R.id.tv_welcome_title);

        drawer =  findViewById(R.id.drawer_layout);

        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this,drawer,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        // take care of the rotation of the navigation icon
        toggle.syncState();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // sets visibility to visible
        tv_welcome_title.setVisibility(View.VISIBLE);

        /*if(savedInstanceState == null){
            // starting AddUsers Fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddUsersFragment()).commit();
            nav_view.setCheckedItem(R.id.menu_add);
        }
        */

        // method call to change progressDialog style according to build version
        changeProgressDialogStyle();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.menu_add:

                // sets visibility to visible
                tv_welcome_title.setVisibility(View.GONE);

                // starting AddUsers Fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddUsersFragment()).commit();

                break;

            case R.id.menu_manage:

                // sets visibility to visible
                tv_welcome_title.setVisibility(View.GONE);

                // starts ManageUsers Fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ManageUsersFragment()).commit();

                break;

            case R.id.menu_archive:

                // sets visibility to visible
                tv_welcome_title.setVisibility(View.GONE);

                // starts ArchiveUsers Fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ArchiveDocumentsFragment()).commit();

                break;

            case R.id.menu_archived_documents:

                // opens the ArchivedDocumentsActivity for admin
                startActivity(new Intent(AdminHomeActivity.this, ArchivedDocumentsActivity.class));

                CustomIntent.customType(AdminHomeActivity.this, "left-to-right");

            case R.id.menu_share:

                // share app link to others
                shareApp();

                break;

            case R.id.menu_sign_out:

                // sign out admin
               signOutAdmin();

                break;
        }

        // closes navigation drawer to the left
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater  = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_smile:

                // display a welcome message
                Toast.makeText(this, getString(R.string.text_welcome_admin), Toast.LENGTH_LONG).show();

                break;

            case R.id.menu_share:

                // share app
                shareApp();

                break;


            case R.id.menu_sign_out:

                // sign out admin
                signOutAdmin();

                break;
        }

        return super.onOptionsItemSelected(item);
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

    private void signOutAdmin(){

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminHomeActivity.this);
        builder.setTitle(getString(R.string.text_sign_out));
        builder.setMessage(getString(R.string.sign_out_msg));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // show dialog
                progressDialog.show();

                // delays the running of the ProgressBar for 3 secs
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // dismiss dialog
                        progressDialog.dismiss();

                        // log admin out of the system and clear all stored data
                        clearEmail(AdminHomeActivity.this);

                        // send admin to login activity
                        startActivity(new Intent(AdminHomeActivity.this, SignInActivity.class));

                        // adds an intent transition
                        CustomIntent.customType(AdminHomeActivity.this, "fadein-to-fadeout");

                        // finish activity
                        finish();

                    }
                },3000);

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    // method to clear sharePreference when admin log outs
    private  void clearEmail(Context ctx){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        editor.clear(); // clear all stored data (email)
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        // check if drawer is opened
        if(drawer.isDrawerOpen(GravityCompat.START)){
            // closes the drawer
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
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
