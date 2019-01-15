package io.zentechgh.dms.mobile.app.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.fragment.AddUsersFragment;
import io.zentechgh.dms.mobile.app.fragment.ArchiveUsersFragment;
import io.zentechgh.dms.mobile.app.fragment.ManageUsersFragment;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;

    DrawerLayout drawer;

    NavigationView nav_view;

    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        drawer =  findViewById(R.id.drawer_layout);

        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this,drawer,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        // take care of the rotation of the navigation icon
        toggle.syncState();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            // starting AddUsers Fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddUsersFragment()).commit();
            nav_view.setCheckedItem(R.id.menu_add);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.menu_add:

                // starting AddUsers Fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddUsersFragment()).commit();

                break;

            case R.id.menu_manage:

                // starts ManageUsers Fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ManageUsersFragment()).commit();

                break;

            case R.id.menu_archive:

                // starts ArchiveUsers Fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ArchiveUsersFragment()).commit();

                break;

            case R.id.menu_share:

                // share app link to others

                break;

            case R.id.menu_sign_out:

                // sign out admin

                Toast.makeText(this, "Sign Out", Toast.LENGTH_LONG).show();

                break;
        }

        // closes navigation drawer to the left
        drawer.closeDrawer(GravityCompat.START);

        return true;
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

}
