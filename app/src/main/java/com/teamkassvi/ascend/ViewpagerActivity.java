package com.teamkassvi.ascend;

/**
 * Created by Lenovo on 29-10-2017.
 */


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import static com.teamkassvi.ascend.GetStartedActivity.ENTRY_FRAGMENT;
import static com.teamkassvi.ascend.GetStartedActivity.FRAG_DASHBOARD;
import static com.teamkassvi.ascend.GetStartedActivity.FRAG_RECORDINGS;


public class ViewpagerActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    //This is our viewPager
    private ViewPager viewPager;
    boolean reloadAllowed = false;
    boolean network;


    //Fragments

    RecordingsFragment recordingsFragment;
    TestFragment testFragment;
    DashboardFragment dashboardFragment;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //Initializing the bottomNavigationView
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_test:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_recordings:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_dashboard:
                                checkNetworkAvailability();
                                viewPager.setCurrentItem(2);
                                if(reloadAllowed) {
                                    reloadFragment();
                                }
                                break;
                        }
                        return true;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==2){
                    checkNetworkAvailability();
                    if(reloadAllowed) {
                        reloadFragment();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

       /*  //Disable ViewPager Swipe

       viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        */

        setupViewPager(viewPager);

        Intent intent = getIntent();
        try{
            String entry = intent.getStringExtra(ENTRY_FRAGMENT);
            if(entry.equals(FRAG_RECORDINGS)){
                viewPager.setCurrentItem(1,true);
            }
            else if(entry.equals(FRAG_DASHBOARD)){
                viewPager.setCurrentItem(2,true);
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkNetworkAvailability() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        network = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if(!network){
            Toast.makeText(ViewpagerActivity.this, "Error loading, check your internet connection.", Toast.LENGTH_SHORT).show();
            reloadAllowed = true;
        }
    }

    private void reloadFragment() {
//        Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(dashboardFragment);
        ft.attach(dashboardFragment);
        ft.commit();
        if(network) {
            reloadAllowed = false;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        testFragment =new TestFragment();
        recordingsFragment =new RecordingsFragment();
        dashboardFragment =new DashboardFragment();
        adapter.addFragment(testFragment);
        adapter.addFragment(recordingsFragment);
        adapter.addFragment(dashboardFragment);
        viewPager.setAdapter(adapter);
    }
}
