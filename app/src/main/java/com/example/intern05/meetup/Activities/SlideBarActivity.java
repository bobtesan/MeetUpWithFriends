package com.example.intern05.meetup.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.intern05.meetup.Fragments.FragmentEvents;
import com.example.intern05.meetup.Fragments.FragmentEventsHistory;
import com.example.intern05.meetup.Fragments.FragmentIncomingInv;
import com.example.intern05.meetup.Other.CircleTransform;
import com.example.intern05.meetup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SlideBarActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName;
    private Toolbar toolbar;
    private String userID;
    private String username;
    private Uri imageUri;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();

    private static final String urlProfileImg = "https://www.wnmlive.com/images/Default-Profile.jpg";

    public static int navItemIndex = 0;

    private static final String TAG_HOME = "home";
    private static final String TAG_Invites = "incoming invites";
    private static final String TAG_Events_History = "events history";
    public static String CURRENT_TAG = TAG_HOME;

    private String[] activityTitles;

    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private DatabaseReference root= FirebaseDatabase.getInstance().getReference("Users");
    DatabaseReference root2= FirebaseDatabase.getInstance().getReference("Users");
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_bar_activity);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


        navHeader = navigationView.getHeaderView(0);


        txtName = (TextView) navHeader.findViewById(R.id.nameUser);


        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SlideBarActivity.this,MyProfileActivity.class);
                startActivity(i);
            }
        });

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        loadNavHeader();
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
        userID=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    if (children.getKey().equals(userID)) {
                        for (DataSnapshot child : children.getChildren()) {
                            if (child.getKey().equals("Username")) {
                                username = child.getValue(String.class);
                                txtName.setText(username);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //LogOut
    FirebaseAuth auth=FirebaseAuth.getInstance();

    private void loadNavHeader() {
        root2 = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        root2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uri=dataSnapshot.child("ImageUri").getValue(String.class);
                if(uri.equals("null")){
                    Glide.with(getApplicationContext())
                            .load(urlProfileImg)
                            .crossFade()
                            .override(300,300)
                            .thumbnail(0.5f)
                            .bitmapTransform(new CircleTransform(getApplicationContext()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgProfile);
                }
                else {
                    imageUri = Uri.parse(uri);

                    Glide.with(getApplicationContext())
                            .load(imageUri)
                            .crossFade()
                            .override(300, 300)
                            .thumbnail(0.5f)
                            .bitmapTransform(new CircleTransform(getApplicationContext()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    private void loadHomeFragment() {

        selectNavMenu();
        setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {

                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };


        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers();
        invalidateOptionsMenu();
    }



    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                FragmentEvents fragmentEvents = new FragmentEvents();
                return fragmentEvents;
            case 1:
                FragmentIncomingInv fragmentInv=new FragmentIncomingInv();
                return fragmentInv;
            case 2:
                FragmentEventsHistory fragmentHist=new FragmentEventsHistory();
                return fragmentHist;
            default:
                return new FragmentEvents();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.events_screen:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.incoming_invites:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_Invites;
                        break;
                    case R.id.events_history:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_Events_History;
                        break;
                    case R.id.terms_conditions:
                        navItemIndex = 3;
                        startActivity(new Intent(SlideBarActivity.this,TermsConditionsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.about_app:
                        navItemIndex = 4;
                        startActivity(new Intent(SlideBarActivity.this,AboutAppActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.log_out:
                        navItemIndex = 5;
                        auth.signOut();
                        Intent i=new Intent(SlideBarActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }


        };
        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        if (navItemIndex >= 0) {
            getMenuInflater().inflate(R.menu.my_profile_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_my_profile) {
            Intent i=new Intent(SlideBarActivity.this,MyProfileActivity.class);
            startActivity(i);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
