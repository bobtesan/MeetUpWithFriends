package com.example.intern05.meetup.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intern05.meetup.R;


/**
 * Created by inter04 on 29.05.2017.
 */

public class FragmentEventsHistory extends android.support.v4.app.Fragment {

    public FragmentEventsHistory() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events_history, container, false);
    }

}
