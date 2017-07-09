package com.example.intern05.meetup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.intern05.meetup.Models.Events;
import com.example.intern05.meetup.R;

import java.util.List;

/**
 * Created by intern05 on 15.05.2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Events> eventsList;
    private EventItemSelectionListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, eventDate;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            eventDate = (TextView) view.findViewById(R.id.eventDate);
        }

    }

    public MyAdapter(List<Events> eventsList, EventItemSelectionListener listener) {
        this.eventsList = eventsList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Events events = eventsList.get(position);
        holder.title.setText(events.getTitle());
        holder.eventDate.setText(events.getEventDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEventSelected(holder.getAdapterPosition(), events);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }


    public interface EventItemSelectionListener{
        void onEventSelected(int position, Events event);
    }


}
