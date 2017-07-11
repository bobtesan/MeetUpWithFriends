package com.example.intern05.meetup.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.intern05.meetup.Activities.EventDetails;
import com.example.intern05.meetup.Activities.LocationActivity;
import com.example.intern05.meetup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by intern05 on 25.05.2017.
 */

public class FragmentEventDetails extends Fragment{



    private TextView eventName;
    private TextView eventDate;
    private TextView eventStartTime;
    private String name;
    private ImageView imageView;

    private DatabaseReference root;
    private View rootView;


    public static FragmentEventDetails newInstance(String eventName){
        FragmentEventDetails fragment = new FragmentEventDetails();
        Bundle args = new Bundle();
        args.putString(EventDetails.KEY_EVENT_NAME, eventName);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentEventDetails() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            name = getArguments().getString(EventDetails.KEY_EVENT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.fragment_event_details, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        eventName=(TextView)rootView.findViewById(R.id.eventName);
        eventDate=(TextView)rootView.findViewById(R.id.eventDate);
        eventStartTime=(TextView)rootView.findViewById(R.id.eventStartTime);
        imageView=(ImageView)rootView.findViewById(R.id.imageView3);

        eventName.setText(name);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), LocationActivity.class);
                i.putExtra(EventDetails.KEY_EVENT_NAME,name);
                startActivity(i);
            }
        });

        root = FirebaseDatabase.getInstance().getReference("Events").child(name);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventDate.setText(dataSnapshot.child("EventDate").getValue(String.class));
                eventStartTime.setText(dataSnapshot.child("StartTime").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
