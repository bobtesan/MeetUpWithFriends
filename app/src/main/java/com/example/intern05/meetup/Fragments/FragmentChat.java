package com.example.intern05.meetup.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intern05.meetup.Activities.EventDetails;
import com.example.intern05.meetup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class FragmentChat extends Fragment  {

    private View rootView;
    private Button sendMessage;
    private String temp_key;
    private DatabaseReference root;
    private String eventName;
    private EditText message;
    private String userID;
    private String username="test";
    private TextView conversation;
    private DatabaseReference root_users= FirebaseDatabase.getInstance().getReference("Users");

    public static FragmentChat newInstance(String eventName){
        FragmentChat fragment = new FragmentChat();
        Bundle args = new Bundle();
        args.putString(EventDetails.KEY_EVENT_NAME, eventName);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentChat() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        eventName=getArguments().getString(EventDetails.KEY_EVENT_NAME);
        message=(EditText)rootView.findViewById(R.id.message);

        sendMessage=(Button)rootView.findViewById(R.id.sendMessageB);
        conversation=(TextView)rootView.findViewById(R.id.conversation);
        conversation.setMovementMethod(new ScrollingMovementMethod());

        root= FirebaseDatabase.getInstance().getReference("Events").child(eventName).child("Chat");
        userID= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();


        root_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    if (children.getKey().equals(userID)) {
                        for (DataSnapshot child : children.getChildren()) {
                            if (child.getKey().equals("Username")) {
                                username = child.getValue(String.class);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getText().toString().equals("")) {
                    Toast.makeText(getActivity(),"Please write something",Toast.LENGTH_SHORT).show();
                }
                else{
                    temp_key = root.push().getKey();
                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("Username", username);
                    map.put("MSG", message.getText().toString());
                    message_root.updateChildren(map);
                    message.setText("");
                }
            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_conversation(dataSnapshot);
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
    private String chat_msg,chat_user;
    private void append_conversation(DataSnapshot ds){
        Iterator i=ds.getChildren().iterator();

        while(i.hasNext()){
            chat_msg=(String)((DataSnapshot)i.next()).getValue();
            chat_user=(String)((DataSnapshot)i.next()).getValue();
            conversation.append(chat_user+": "+chat_msg+"\n");
        }
    }




}
