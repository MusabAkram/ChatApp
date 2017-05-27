package com.example.musab.chatapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat_room extends AppCompatActivity {

    //Fields for out Views
    Button sendBtn, btn_attach;
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
        btn_attach= (Button) findViewById(R.id.btn_attach);

        btn_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("*/*");      //all files
                intent.setType("text/xml");   //XML file only
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 0);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(Chat_room.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });


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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        String path     = "";
        if(requestCode == 0)
        {
            Uri uri = data.getData();
            String FilePath = getFileName(uri); // should the path be here in this string
           Toast.makeText(Chat_room.this,"Path  = " + FilePath,Toast.LENGTH_LONG).show();

            upload(FilePath, uri);

        }
    }

    public void upload(String filepath, Uri file)
    {

        //Uploading  :-----------------

        StorageReference storageRef = FirebaseStorage.getInstance("gs://poponfa-8a11a.appspot.com/").getReference();

        StorageReference riversRef = storageRef.child("images/"+ filepath);

        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Toast.makeText(Chat_room.this,"Uploaded",Toast.LENGTH_SHORT).show();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}