package com.teamkassvi.ascend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import static com.teamkassvi.ascend.LoginActivity.EMAIL_ID;
import static com.teamkassvi.ascend.LoginActivity.USERNAME;
import static com.teamkassvi.ascend.LoginActivity.USER_IMAGE;

public class GetStartedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button btnGetStarted;
    TextView tvUsername, tvEmailId, tvWelcome;
    ImageView ivUserImage, ivShowcase;
    int pos = 0;
    public static final String ENTRY_FRAGMENT = "fragment";
    public static final String FRAG_TEST = "test";
    public static final String FRAG_RECORDINGS = "recordings";
    public static final String FRAG_DASHBOARD = "dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_get_started_content);
        relativeLayout.getBackground().setAlpha(30);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        tvUsername = (TextView) headerView.findViewById(R.id.tv_username);
        tvEmailId = (TextView) headerView.findViewById(R.id.tv_email_id);
        ivUserImage = (ImageView) headerView.findViewById(R.id.iv_user_image);

        ivShowcase = (ImageView) findViewById(R.id.iv_showcase);
        tvWelcome = (TextView) findViewById(R.id.tv_welcome);

        btnGetStarted =(Button) findViewById(R.id.btn_get_started);

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetStartedActivity.this, ViewpagerActivity.class);
                intent.putExtra(ENTRY_FRAGMENT, FRAG_TEST);
                startActivity(intent);

            }
        });

        addUserDetails();
        addShowcase();
    }

    private void addShowcase() {
//        final AnimationDrawable animation = new AnimationDrawable();
//        animation.addFrame(ContextCompat.getDrawable(this,R.drawable.album_cover), 1000);
//        animation.addFrame(ContextCompat.getDrawable(this,R.drawable.background), 1000);
//        animation.addFrame(ContextCompat.getDrawable(this,R.drawable.cover_logo), 1000);
//        animation.setOneShot(false);
//
//        ivShowcase.setBackgroundDrawable(animation);
//
//        // start the animation!
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                animation.start();
//            }
//        },1000);
//        ivShowcase.animate().alpha(0);
        final int[] imageArray = { R.drawable.showcase1, R.drawable.showcase2,
                R.drawable.showcase3, R.drawable.showcase4};


        final Handler outer = new Handler();
        outer.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivShowcase.animate().alpha(0).setDuration(500);
                Handler inner = new Handler();
                inner.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ivShowcase.setImageResource(imageArray[pos]);
                        ivShowcase.animate().alpha(1).setDuration(500);
                        pos++;
                        if (pos > imageArray.length - 1) {
                            pos = 0;
                        }
                    }
                },500);
                outer.postDelayed(this, 2500);
            }
        },2500);



//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            int i = 0;
//
//            public void run() {
//                ivShowcase.setImageResource(imageArray[i]);
//                ivShowcase.animate().alpha(1).setDuration(200);
//
//                i++;
//                if (i > imageArray.length - 1) {
//                    i = 0;
//                }
//                handler.postDelayed(this, 2000);
//            }
//        };
//        handler.postDelayed(runnable, 100);
    }

    private void addUserDetails() {

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Intent intent = getIntent();
        String username = "Your name";
        String emailId = "Your email Id";
        String userImageUri = null;
        try {
            username = intent.getStringExtra(USERNAME);
            emailId = intent.getStringExtra(EMAIL_ID);
            userImageUri = intent.getStringExtra(USER_IMAGE);
//            userImageUri = "http://i.imgur.com/DvpvklR.png";
            if(userImageUri!=null) {
                Picasso.with(this).load(userImageUri).into(ivUserImage);
            }
            else{
                ivUserImage.setImageResource(R.drawable.default_pic);
            }
            Log.d("taguserimage: ", userImageUri+"");
            tvWelcome.setText("Welcome, "+ username);
            tvUsername.setText(username);
            tvEmailId.setText(emailId);
        }
        catch (Exception e){
            Log.d("Tagintenterror", e+"");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Want To Exit ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
//                        hideStatusBar();
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
//                hideStatusBar();
                }
            });
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(USERNAME);
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        addUserDetails();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reports) {
            startActivity(new Intent(GetStartedActivity.this,WeeklyReports.class));
        } else if (id == R.id.nav_analyze) {
            startActivity(new Intent(GetStartedActivity.this,StaticActivity.class));
        } else if (id == R.id.nav_realtime) {
            startActivity(new Intent(GetStartedActivity.this,RealtimeActivity.class));
        } else if (id == R.id.nav_recordings) {
            Intent intent = new Intent(GetStartedActivity.this, ViewpagerActivity.class);
            intent.putExtra(ENTRY_FRAGMENT, FRAG_RECORDINGS);
            startActivity(intent);
        } else if (id == R.id.nav_dashboard) {
            Intent intent = new Intent(GetStartedActivity.this, ViewpagerActivity.class);
            intent.putExtra(ENTRY_FRAGMENT, FRAG_DASHBOARD);
            startActivity(intent);
        } else if (id == R.id.nav_account) {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_aboutus) {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
