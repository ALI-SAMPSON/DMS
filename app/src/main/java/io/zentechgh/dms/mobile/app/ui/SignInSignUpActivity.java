package io.zentechgh.dms.mobile.app.ui;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.adapter.ViewPagerAdapter;
import io.zentechgh.dms.mobile.app.fragment.UserSignInFragment;
import io.zentechgh.dms.mobile.app.fragment.UserSignUpFragment;

public class SignInSignUpActivity extends AppCompatActivity {

    // Global variables or views
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);

        // getting reference
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // getting reference to tabLayout
        tabLayout = findViewById(R.id.tab_layout);
        // getting reference to viewPager
        viewPager = findViewById(R.id.view_pager);

        // method call to setUpViewPager with viewPager
        setupViewPager(viewPager);

        //sets tabLayout with viewPager
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        // class instantiation of ViewPagerAdapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // calling method to add fragments
        viewPagerAdapter.addFragment(new UserSignInFragment(),getString(R.string.text_sign_in));
        viewPagerAdapter.addFragment(new UserSignUpFragment(), getString(R.string.text_sign_up));
        // setting adapter to viewPager
        viewPager.setAdapter(viewPagerAdapter);
    }
}
