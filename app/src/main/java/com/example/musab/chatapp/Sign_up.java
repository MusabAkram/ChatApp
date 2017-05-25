package com.example.musab.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_up extends AppCompatActivity {
    EditText mail,name,pass,c_pass,contact;
    Button Proceed;
    FirebaseAuth mAuth;
    String Name;
    String contactInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        mail = (EditText) findViewById(R.id.s_mail);
        name = (EditText) findViewById(R.id.s_name);
        pass = (EditText) findViewById(R.id.s_pass);
        c_pass = (EditText) findViewById(R.id.s_c_pass);
        contact = (EditText) findViewById(R.id.s_contact);
        Proceed = (Button) findViewById(R.id.btn_proceed);

        name.setText(Name);
        contact.setText(contactInfo);
        mAuth=FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);

        Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Proceed.setActivated(true);
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                /* Create an Intent that will start the Menu-Activity. */


                        if (!mail.getText().toString().equals("") &&
                                !mail.getText().toString().equals("") &&
                                !name.getText().toString().equals("") &&
                                !pass.getText().toString().equals("") &&
                                !c_pass.getText().toString().equals("") &&
                                !contact.getText().toString().equals("")) {
                            if (pass.getText().toString().equals(c_pass.getText().toString())) {
                                Toast.makeText(Sign_up.this, "PROCEED", Toast.LENGTH_SHORT).show();
                                Check();
                                signup();

                            } else {
                                Toast.makeText(Sign_up.this, "Password Confirmation Error ", Toast.LENGTH_SHORT).show();
                                Check();


                            }
                        } else {
                            Toast.makeText(Sign_up.this, "Please Fill Information", Toast.LENGTH_SHORT).show();
                            Check();

                        }




                    }}, 1000);
            }
        });
    }




    void Check(){
        if(mail.getText().toString().equals(""))
            mail.setBackgroundColor(Color.parseColor("#ffa0ab"));
        if(name.getText().toString().equals(""))
            name.setBackgroundColor(Color.parseColor("#ffa0ab"));
        if(pass.getText().toString().equals(""))
            pass.setBackgroundColor(Color.parseColor("#ffa0ab"));
        if(c_pass.getText().toString().equals(""))
            c_pass.setBackgroundColor(Color.parseColor("#ffa0ab"));
        if(contact.getText().toString().equals(""))
            contact.setBackgroundColor(Color.parseColor("#ffa0ab"));

        if(!mail.getText().toString().equals(""))
            mail.setBackgroundColor(Color.parseColor("#ffffff"));
        if(!name.getText().toString().equals(""))
            name.setBackgroundColor(Color.parseColor("#ffffff"));
        if(!pass.getText().toString().equals(""))
            pass.setBackgroundColor(Color.parseColor("#ffffff"));
        if(!c_pass.getText().toString().equals(""))
            c_pass.setBackgroundColor(Color.parseColor("#ffffff"));
        if(!contact.getText().toString().equals(""))
            contact.setBackgroundColor(Color.parseColor("#ffffff"));

        if(!pass.getText().toString().equals(c_pass.getText().toString()))
            c_pass.setBackgroundColor(Color.parseColor("#ffa0ab"));
    }


    void signup()
    {
        final String email=mail.getText().toString();
        String password=pass.getText().toString();
        final String Name=name.getText().toString();
        final String contactinfo=contact.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            final DatabaseReference user_id = FirebaseDatabase.getInstance().getReference().getRoot();


                            String key=user_id.child("Users").push().getKey();
                            user_id.child("Users").child(key).child("Name").setValue(Name);
                            user_id.child("Users").child(key).child("Email").setValue(email);
                            user_id.child("Users").child(key).child("Contact Info").setValue(contactinfo);


                            Intent i = new Intent(Sign_up.this, MainActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Sign_up.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }
}
