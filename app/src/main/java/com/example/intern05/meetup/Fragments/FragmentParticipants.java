package com.example.intern05.meetup.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intern05.meetup.Activities.EventDetails;
import com.example.intern05.meetup.R;

public class FragmentParticipants extends Fragment {

    private String name;

    public static FragmentParticipants newInstance(String eventName){
        FragmentParticipants fragment = new FragmentParticipants();
        Bundle args = new Bundle();
        args.putString(EventDetails.KEY_EVENT_NAME, eventName);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentParticipants() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            name = getArguments().getString(EventDetails.KEY_EVENT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_participants, container, false);
    }
}
