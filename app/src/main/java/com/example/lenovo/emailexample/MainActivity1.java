package com.example.lenovo.emailexample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends AppCompatActivity {
    public DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("root").child("users");
    public FirebaseAuth mAuth=FirebaseAuth.getInstance();
    public FirebaseUser user;
    DatabaseReference key = FirebaseDatabase.getInstance().getReference().child("root").child("keys");
    EditText username,phone,uniquecode;
    String usern,ph,uniquec;
    int a = 0;
    List<String> keyc = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        user = mAuth.getCurrentUser();
        username = (EditText)findViewById(R.id.user);
        phone = (EditText)findViewById(R.id.phone);
        uniquecode = (EditText)findViewById(R.id.uniquecode);
        final String name = user.getUid().toString();
        Button button=(Button)findViewById(R.id.ok);


        key.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String k = snapshot.child("key").getValue().toString();
                    keyc.add(k);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                int flag =0;
                ph = phone.getText().toString();
                usern = username.getText().toString();
                uniquec = uniquecode.getText().toString();
                for (String n : keyc)
                {

                    if (n.equals(uniquec)) {
                        flag = 1;
                        Intent i = new Intent(MainActivity1.this, Main2Activity.class);
                        ref.child(name).child("profile").setValue("1");
                        ref.child(name).child("keystatus").setValue("0");
                        ref.child(name).child("username").setValue(usern);
                        ref.child(name).child("phone no").setValue(ph);
                        startActivity(i);
                        finishAffinity();
                    }
                }
                if (flag == 0) {
                    Toast.makeText(MainActivity1.this, "Unique Code is not valid", Toast.LENGTH_SHORT).show();
                    uniquecode.setText("");
                }
            }
        });
    }
}
