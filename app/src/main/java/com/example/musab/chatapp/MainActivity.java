package com.example.musab.chatapp;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    Button createRoom, btn_signOut;
    ListView roomList;
    String memberName;
    ArrayList<String> roomArrayList,members;
    ArrayAdapter<String> roomAdapter;
    DatabaseReference databaseReference;
    Map<String, Object> data;
    FirebaseAuth firebaseAuth;

    private String Name;
    Set<String> set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b= getIntent().getExtras();
        Name=b.getString("Name");
//        contactInfo=b.getString("Contact Info");
        btn_signOut = (Button) findViewById(R.id.btn_signOut);
        roomList = (ListView) findViewById(R.id.chatroom_list);
        roomName = (EditText) findViewById(R.id.room_name);
        createRoom = (Button) findViewById(R.id.create_room);
        roomArrayList = new ArrayList<String>();
        roomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roomArrayList);
        members=new ArrayList<>();
        roomList.setAdapter(roomAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Rooms");
        firebaseAuth=firebaseAuth.getInstance();


        roomArrayList.clear();

        btn_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent signin = new Intent(MainActivity.this,Sign_in.class);

                startActivity(signin);
            }
        });

        //CREATE AN ENTRY FOR NEW ROOM IN FIREBASE DATABASE ON BUTTON CLICK
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put(roomName.getText().toString(), "");
                databaseReference.updateChildren(map);

            }
        });



        databaseReference.addChildEventListener(new ChildEventListener() {



                //Log.i("Members",data.toString());
                //Toast.makeText(MainActivity.this,dataSnapshot.child("Members").child("0").getValue().toString(),Toast.LENGTH_SHORT).show();



//                for (int i = 0; i<data.size();i++) {
//                    memberName = (String) data.get(i);
//
//
//                    Iterator iterator = dataSnapshot.getChildren().iterator();
//
//                    set = new HashSet<String>();
//
//                    while (iterator.hasNext()) {
//
//                            if (memberName.equals(Name)) {
//                                //GET NAMES OF ALL THE ROOMS ONE BY ONE FROM YOUR DATABASE
//                                set.add((String) ((DataSnapshot) iterator.next()).getKey());
//                            }
//                        }
//
//                    }

//                roomArrayList.clear();
//                roomArrayList.addAll(set);
//
//                roomAdapter.notifyDataSetChanged();


            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                DatabaseReference members = databaseReference.child(dataSnapshot.getKey()).child("Members");

                members.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot2, String s) {
                        memberName = dataSnapshot2.getValue().toString();

                        //Toast.makeText(MainActivity.this,Name + " : " + memberName,Toast.LENGTH_SHORT).show();

                        if (memberName.equals(Name)) {
//                                //GET NAMES OF ALL THE ROOMS ONE BY ONE FROM YOUR DATABASE
                               //set.add( dataSnapshot2.getKey().toString());


                            Toast.makeText(MainActivity.this,"User in Room: " + dataSnapshot.getKey().toString(),Toast.LENGTH_SHORT).show();


                            roomArrayList.add(dataSnapshot.getKey().toString());
                            roomAdapter.notifyDataSetChanged();
                            }


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

                data= new HashMap<>();

                data.put("Value",dataSnapshot.getValue());
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