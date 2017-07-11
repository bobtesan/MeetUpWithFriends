package com.example.intern05.meetup.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intern05.meetup.Activities.EventDetails;
import com.example.intern05.meetup.Activities.MapsActivity;
import com.example.intern05.meetup.Adapters.MyAdapter;
import com.example.intern05.meetup.Models.Events;
import com.example.intern05.meetup.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by intern05 on 26.05.2017.
 */

public class FragmentEvents extends Fragment implements MyAdapter.EventItemSelectionListener {

    public static final String KEY_EVENT_NAME = "event_name";
    private View rootView;

    private DatabaseReference root;
    private List<Events> eventsList = new ArrayList<>();

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private FloatingActionButton fab;


    private DateFormat df;
    private String date;

    public FragmentEvents() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_events, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        //date = df.format(Calendar.getInstance().getTime());

        root = FirebaseDatabase.getInstance().getReference("Events");
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        myAdapter = new MyAdapter(eventsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getActivity(), EventCreateActivity.class);
                if(isNetworkAvailable()) {
                    Intent i = new Intent(getActivity(), MapsActivity.class);
                    startActivity(i);
                }
                else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("You don't have internet connection. Please connect to the internet.");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
        prepareEventData();

    }

    private void prepareEventData() {
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                String date = (String) map.get("EventDate");
                String time = (String) map.get("StartTime");

                eventsList.add(new Events(dataSnapshot.getKey(), date, time));

                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onEventSelected(int position, Events event) {
        Intent i = new Intent(getActivity(), EventDetails.class);
        i.putExtra(KEY_EVENT_NAME, event.getTitle());
        startActivity(i);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


