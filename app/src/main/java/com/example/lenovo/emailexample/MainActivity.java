package com.example.lenovo.emailexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {

    public DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("root").child("users");
    public FirebaseAuth mAuth=FirebaseAuth.getInstance();
    public FirebaseUser user;
    DatabaseReference key = FirebaseDatabase.getInstance().getReference().child("root").child("keys");
    EditText username,phone,uniquecode;
    String usern,ph,uniquec,recievermail,usermail;
    int a = 0;
    List<String> keyc = new ArrayList<String>();
    List<String> emailc = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main1 );
        recievermail = "milan.pokharna1998@gmail.com";
        user = mAuth.getCurrentUser();
        usermail = user.getEmail().toString();
        username = (EditText)findViewById(R.id.user);
        phone = (EditText)findViewById(R.id.phone);
        uniquecode = (EditText)findViewById(R.id.uniquecode);
        final String name = user.getUid().toString();
        Button button=(Button)findViewById(R.id.ok);


        key.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                keyc.clear();
                emailc.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String k = snapshot.child("key").getValue().toString();
                    String ke = snapshot.child("email").getValue().toString();
                    emailc.add(ke);
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
                int f = 0;
                ph = phone.getText().toString();
                usern = username.getText().toString();
                uniquec = uniquecode.getText().toString();
                for (String n : keyc)
                {
                    if (n.equals(uniquec)) {
                        flag = 1;
                        recievermail = emailc.get(f);
                        ref.child(name).child("profile").setValue("1");
                        ref.child(name).child("keystatus").setValue("0");
                        ref.child(name).child("username").setValue(usern);
                        ref.child(name).child("phone no").setValue(ph);

                        new SendMail().execute("");

                    }
                    f++;
                }
                if (flag == 0) {
                    Toast.makeText(MainActivity.this, "Unique Code is not valid", Toast.LENGTH_SHORT).show();
                    uniquecode.setText("");
                }
            }
        });
//        Button send = (Button) this.findViewById(R.id.send);
//        send.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                try {
//                    GMailSender sender = new GMailSender("developersniper@gmail.com", "sniper developer");
//                    sender.sendMail("This is Subject",
//                            "This is Body",
//                            "developersniper@gmail.com",
//                            "tusharjn16@gmail.com");
//                    Toast.makeText( MainActivity.this, "yes", Toast.LENGTH_SHORT ).show();
//                } catch (Exception e) {
//                    Log.e("SendMail", e.getMessage(), e);
//                }
//
//            }
//        });
//        Button send2 = (Button) this.findViewById(R.id.send2);
//        send2.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                new SendMail().execute("");
//
//            }
//        });

    }

    private class SendMail extends AsyncTask<String, Integer, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Intent i = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(i);
            finishAffinity();
        }

        protected Void doInBackground(String... params) {
            Mail m = new Mail("developersniper@gmail.com", "sniper developer");

            String[] toArr = {recievermail};
            m.setTo(toArr);
            m.setFrom("developersniper@gmail.com");
            m.setSubject("Unique_Key App");
            m.setBody(" You are Recieving this mail because "+usern+" ("+usermail+") has used your Unique Code"+uniquec);

            try {
                if(m.send()) {
                    Toast.makeText(MainActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                }
            } catch(Exception e) {
                Log.e("MailApp", "Could not send email", e);
            }
            return null;
        }
    }
}
