package com.example.intern05.meetup.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.intern05.meetup.Fragments.FragmentChat;
import com.example.intern05.meetup.Fragments.FragmentEventDetails;
import com.example.intern05.meetup.Fragments.FragmentEvents;
import com.example.intern05.meetup.Fragments.FragmentParticipants;
import com.example.intern05.meetup.R;

import java.util.ArrayList;
import java.util.List;

public class EventDetails extends AppCompatActivity {

    public static final String KEY_EVENT_NAME = "eventName";

    private String eventName;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        eventName = getIntent().getStringExtra(FragmentEvents.KEY_EVENT_NAME);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().hide();

    }
    private void setupViewPager(ViewPager viewPager) {
        EventDetails.ViewPagerAdapter adapter = new EventDetails.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentEventDetails.newInstance(eventName), "Event details");
        adapter.addFragment(FragmentParticipants.newInstance(eventName), "Participants");
        adapter.addFragment(FragmentChat.newInstance(eventName), "Chat");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
