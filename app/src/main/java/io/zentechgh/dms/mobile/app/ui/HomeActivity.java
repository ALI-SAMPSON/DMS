package io.zentechgh.dms.mobile.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.adapter.ViewPagerAdapterHome;
import io.zentechgh.dms.mobile.app.fragment.AddDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.DeleteDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.AssignDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.ManageDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.MeFragment;
import maes.tech.intentanim.CustomIntent;

@SuppressWarnings("ALL")
public class HomeActivity extends AppCompatActivity {
    // Global views declaration
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;

    FirebaseAuth mAuth;

    ProgressBar progressBar;

    private int[] tabIcons = {
            R.drawable.ic_add,
            R.drawable.ic_manage,
            R.drawable.ic_delete,
            R.drawable.ic_assign,
            R.drawable.ic_me
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);

        tabLayout =  findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        // method call to setUpViewPager with viewPager
        setupViewPager(viewPager);
        // setting tabLayout with viewPager
        tabLayout.setupWithViewPager(viewPager);
        // method call to setUpTabLayout with Icons
        setupTabIcons();

        // changes tabColor on selected tab
        onTabSelectedColor();

    }

    private void setupViewPager(ViewPager viewPager) {
        // creating an object of the ViewPagerAdapter class
        ViewPagerAdapterHome viewPagerAdapter = new ViewPagerAdapterHome(getSupportFragmentManager());
        // calling method to add fragments using the viewPagerAdapter
        viewPagerAdapter.addFragment(new AddDocumentFragment(),getString(R.string.text_add));
        viewPagerAdapter.addFragment(new ManageDocumentFragment(), getString(R.string.text_manage));
        viewPagerAdapter.addFragment(new DeleteDocumentFragment(), getString(R.string.text_delete));
        viewPagerAdapter.addFragment(new AssignDocumentFragment(),getString(R.string.text_assign));
        viewPagerAdapter.addFragment(new MeFragment(), getString(R.string.text_me));
        // setting adapter to viewPager
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }


    // changes color of tab selected
    private void onTabSelectedColor(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(HomeActivity.this, R.color.materialYellow);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(HomeActivity.this, R.color.colorWhite);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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

                // display a toast to user
                Toast.makeText(HomeActivity.this, "Enjoy the app with smiles", Toast.LENGTH_SHORT).show();

                return true;

            case R.id.menu_sign_out:

                // method call to signOut User
                signOutUser();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // method that signs user out of the system
    private void signOutUser(){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getString(R.string.text_sign_out));
        builder.setMessage(getString(R.string.sign_out_msg));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // show dialog
                progressBar.setVisibility(View.VISIBLE);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // dismiss dialog
                        progressBar.setVisibility(View.GONE);

                        // do something
                        mAuth.signOut();

                        // restarts the activity
                        startActivity(new Intent(HomeActivity.this,SignInSignUpActivity.class));

                        // Add a custom animation ot the activity
                        CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");

                        // finish the activity
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

}
