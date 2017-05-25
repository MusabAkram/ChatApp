package com.example.musab.chatapp;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    EditText roomName;
    Button createRoom;
    ListView roomList;
    ArrayList<String> roomArrayList,members;
    ArrayAdapter<String> roomAdapter;
    DatabaseReference databaseReference;
    private String Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b= getIntent().getExtras();
        Name=b.getString("Name");
//        contactInfo=b.getString("Contact Info");

        roomList = (ListView) findViewById(R.id.chatroom_list);
        roomName = (EditText) findViewById(R.id.room_name);
        createRoom = (Button) findViewById(R.id.create_room);
        roomArrayList = new ArrayList<String>();
        roomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roomArrayList);
        members=new ArrayList<>();
        roomList.setAdapter(roomAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Rooms");


        //CREATE AN ENTRY FOR NEW ROOM IN FIREBASE DATABASE ON BUTTON CLICK
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put(roomName.getText().toString(), "");
                databaseReference.updateChildren(map);

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                members= (ArrayList<String>) dataSnapshot.child(roomName.toString()).child("Members").getValue();

                Iterator iterator = dataSnapshot.getChildren().iterator();

                Set<String> set = new HashSet<String>();

                while(iterator.hasNext())
                {
                    for (int i =0 ;i < members.size(); i++)
                    {
                        if(members.get(i).equals(Name)) {
                            //GET NAMES OF ALL THE ROOMS ONE BY ONE FROM YOUR DATABASE
                            set.add((String) ((DataSnapshot) iterator.next()).getKey());
                        }
                    }

                }

                roomArrayList.clear();
                roomArrayList.addAll(set);

                roomAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, Chat_room.class);
                intent.putExtra("Room_name", ((TextView)view).getText().toString());
                intent.putExtra("User_name", Name);
                startActivity(intent);

            }
        });


    }

}