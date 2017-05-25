package com.example.musab.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_in extends AppCompatActivity {

    EditText Email,Password;
    Button Sign_in,Sign_up;
    CheckBox Show_Pass;
    FirebaseAuth firebaseAuth;
    String name,contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        firebaseAuth= FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);
        if (firebaseAuth.getCurrentUser() != null)
            Toast.makeText(Sign_in.this,firebaseAuth.getCurrentUser().getEmail().toString(),Toast.LENGTH_LONG).show();


        Sign_in = (Button) findViewById(R.id.signin);
        Show_Pass = (CheckBox) findViewById(R.id.show_pass);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);

        final int inputtype = Password.getInputType();
        Show_Pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Show_Pass.isChecked()){
                    Password.setInputType(1);
                }
                else{
                    Password.setInputType(inputtype);
                }
            }
        });
        Sign_up = (Button) findViewById(R.id.signup);

        Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(Sign_in.this, Sign_up.class);
                startActivity(i);
            }
        });
        Sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin=new Intent(Sign_in.this,MainActivity.class);
//                startActivity(signin);
                Toast.makeText(Sign_in.this,"Checking",Toast.LENGTH_LONG).show();
                userLogin();
            }
        });
    }


    private void userLogin() {
        final String username = Email.getText().toString();
        String password = Password.getText().toString();

       final DatabaseReference user_id = FirebaseDatabase.getInstance().getReference().getRoot().child("Users");

        if (!(username.equals("") || password.equals(""))) {


            firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {

//                        Toast.makeText(Sign_in.this, username, Toast.LENGTH_SHORT).show();
                        if (firebaseAuth.getCurrentUser() != null) {

                            user_id.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                                    Toast.makeText(Sign_in.this, dataSnapshot.child("Email").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    if (dataSnapshot.child("Email").getValue().toString().equals(username)) {
                                        name = dataSnapshot.child("Name").getValue().toString();
                                        Toast.makeText(Sign_in.this,name,Toast.LENGTH_LONG).show();
                                        contact = dataSnapshot.child("Contact Info").getValue().toString();
                                    } else
                                    {name = "Default";
                                        contact = "000";}


                                    Toast.makeText(Sign_in.this, "Logged in", Toast.LENGTH_LONG).show();
                                    Intent signin = new Intent(Sign_in.this, MainActivity.class);
                                    Bundle b = new Bundle();
                                    b.putString("Name", name);
                                    b.putString("Contact Info", contact);
                                    signin.putExtras(b);
                                    finish();
                                    startActivity(signin);
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



                    }

                }
            });
        }
        else
        {
            Toast.makeText(Sign_in.this, "Fill in the Username and Password", Toast.LENGTH_LONG).show();
        }



    }


}
