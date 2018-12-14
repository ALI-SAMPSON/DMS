package io.zentechgh.dms.mobile.app.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.adapter.ViewPagerAdapterHome;
import io.zentechgh.dms.mobile.app.fragment.ApproveDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.ScanDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.SendDocumentFragment;
import io.zentechgh.dms.mobile.app.fragment.UploadDocumentFragment;

public class HomeActivity extends AppCompatActivity {
    // Global views declaration
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.ic_scan,
            R.drawable.ic_file_upload,
            R.drawable.ic_sent,
            R.drawable.ic_approve
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setIcon(R.drawable.ic_menu);
        }

        tabLayout =  findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        // method call to setUpViewPager with viewPager
        setupViewPager(viewPager);
        // setting tabLayout with viewPager
        tabLayout.setupWithViewPager(viewPager);
        // method call to setUpTabLayout with Icons
        setupTabIcons();
    }

    private void setupViewPager(ViewPager viewPager) {
        // creating an object of the ViewPagerAdapter class
        ViewPagerAdapterHome viewPagerAdapter = new ViewPagerAdapterHome(getSupportFragmentManager());
        // calling method to add fragments using the viewPagerAdpater
        viewPagerAdapter.addFragment(new ScanDocumentFragment(),getString(R.string.text_scan),R.drawable.ic_scan);
        viewPagerAdapter.addFragment(new UploadDocumentFragment(), getString(R.string.text_upload),R.drawable.ic_file_upload);
        viewPagerAdapter.addFragment(new SendDocumentFragment(), getString(R.string.text_sent), R.drawable.ic_sent);
        viewPagerAdapter.addFragment(new ApproveDocumentFragment(), getString(R.string.text_approval), R.drawable.ic_approve);
        // setting adapter to viewPager
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
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
            case R.id.menu_search:
                // do something
                Toast.makeText(HomeActivity.this, "Search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_sign_out:
                // do something
                Toast.makeText(HomeActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
