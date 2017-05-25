package com.example.musab.chatapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.MenuPopupWindow;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat_room extends AppCompatActivity {

    //Fields for out Views
    Button sendBtn;
    TextView receivedMsg;
    EditText sendMsg;
    String sendUserName;
    ArrayList<String> users = new ArrayList<String>();
    //Database reference
    DatabaseReference rootRoomName;

    //String fields
    String roomName;
    String userName;
    private String chatUserName;
    private String chatMessage;
    ArrayList<String> marked=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //Find the views by their ID
        sendBtn = (Button) findViewById(R.id.sendMsgBtn);
        receivedMsg = (TextView) findViewById(R.id.receivedMsg);
        sendMsg = (EditText) findViewById(R.id.sendMsgEdit);

        //Get intent extras
        roomName = getIntent().getExtras().get("Room_name").toString();
        userName = getIntent().getExtras().get("User_name").toString();

        //Set activity title to room name
        setTitle(roomName);



        rootRoomName = FirebaseDatabase.getInstance().getReference().getRoot().child("Rooms").child(roomName).child("Messages");

        //On click listener on btn
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference childRoot = rootRoomName.push();

                Map<String, Object> map = new HashMap<String, Object>();

                map.put("name", userName);
                map.put("message", sendMsg.getText().toString());
                sendMsg.setText("");
                childRoot.updateChildren(map);

            }
        });


        rootRoomName.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                update_Message(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                update_Message(dataSnapshot);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_member:
                final Dialog builder = new Dialog(this);

                builder.setContentView(R.layout.users_names_listview);
                final ListView list = (ListView) builder.findViewById(R.id.users_names);
                Button add = (Button) builder.findViewById(R.id.add);

                final DatabaseReference selected=FirebaseDatabase.getInstance().getReference().getRoot().child("Rooms").child(roomName);
                final DatabaseReference user=FirebaseDatabase.getInstance().getReference().getRoot().child("Users");


                user.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        users.add((String) dataSnapshot.child("Name").getValue());
                        final Adapter_User_names adapter=new Adapter_User_names(Chat_room.this,users);

                        list.setAdapter(adapter);
                        marked=adapter.Users_Selected();
                        list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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

                builder.show();



                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selected.child("Members").setValue(marked);
                        Toast.makeText(Chat_room.this, marked.toString(),Toast.LENGTH_SHORT).show();
                        builder.dismiss();
                        users.clear();

                    }
                });





                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Load messages
    private void update_Message(DataSnapshot dataSnapshot) {

        chatUserName = (String) dataSnapshot.child("name").getValue();
        chatMessage = (String) dataSnapshot.child("message").getValue();

        receivedMsg.append(chatUserName + ":" + chatMessage + "\n");

    }

}