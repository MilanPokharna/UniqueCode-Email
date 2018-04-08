package com.example.lenovo.emailexample;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity {

    DatabaseReference mref,keyref;
    FirebaseUser user;
    ProgressBar bar;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    String key = "no id till now";
    String name,value;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        bar = (ProgressBar)findViewById(R.id.progress2);
        bar.setVisibility(View.VISIBLE);
        textView = (TextView)findViewById(R.id.id);
        user = mAuth.getCurrentUser();
        name = user.getUid().toString();
        mref=FirebaseDatabase.getInstance().getReference().child("root");
        keyref=FirebaseDatabase.getInstance().getReference().child("root").child("key");
        callme();

    }
    void callme()
    {
        mref.child("users").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               value = dataSnapshot.child("keystatus").getValue().toString();
                //Toast.makeText(Main2Activity.this, "key status is:"+value, Toast.LENGTH_SHORT).show();
                upload();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
    void upload()
    {
        if (value.equals("0"))
        {
            keyref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    key = dataSnapshot.getValue().toString();
                    key = key.replaceAll("[^\\d.]", "");
                    int i = Integer.parseInt(key.toString());
                //    Toast.makeText(Main2Activity.this, "key value:"+key, Toast.LENGTH_SHORT).show();
                    i++;
                    key = String.valueOf(i).toString();
                    key = "MG00"+key;
                    keyref.setValue(key);
                    mref.child("users").child(name).child("key").setValue(key);
                    value = "2";
                    mref.child("users").child(name).child("keystatus").setValue("1");
                    mref.child("keys").child(user.getUid().toString()).child("key").setValue(key);
                    mref.child("keys").child(user.getUid().toString()).child("email").setValue(user.getEmail().toString());
                    bar.setVisibility(View.INVISIBLE);
                    textView.setText(key);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }
        else if(value.equals("1"))
        {
            mref.child("users").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    key = dataSnapshot.child("key").getValue().toString();
              //      Toast.makeText(Main2Activity.this, "key value is "+key, Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.INVISIBLE);
                    textView.setText(key);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
