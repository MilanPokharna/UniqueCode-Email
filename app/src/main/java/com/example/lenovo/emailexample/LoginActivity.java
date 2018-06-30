package com.example.lenovo.emailexample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    public DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("root").child("users") ;
    public DatabaseReference myref = FirebaseDatabase.getInstance().getReference().child("root").child("users");
    private GoogleSignInClient mGoogleSignInClient;
    Button googleButton;
    String name,profile = "0";
    String e;
    int flag = 0;
    private FirebaseAuth mAuth;
    List<String> email = new ArrayList<>();
    ProgressDialog progressDialog;
    ProgressBar bar;
    int a=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        progressDialog = new ProgressDialog(LoginActivity.this);
        bar = (ProgressBar)findViewById(R.id.progress1);
        open();
        
        mAuth=FirebaseAuth.getInstance();

        googleButton = (Button)findViewById(R.id.googleButton);
        googleButton.setVisibility(View.VISIBLE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bar.setVisibility(View.VISIBLE);

                signIn();
            }
        });

    }


    public void signIn()
    {
            googleButton.setVisibility(View.GONE);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent,RC_SIGN_IN);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                googleButton.setVisibility(View.VISIBLE);
                bar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);
    }

    private void updateUI(final FirebaseUser user) {

        if (user != null)
        {
            googleButton.setVisibility(View.INVISIBLE);
            bar.setVisibility(View.VISIBLE);
            {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name = user.getDisplayName();
                        e = user.getUid().toString();
                        profile = dataSnapshot.child(e).child("profile").getValue().toString();
        
                        if (profile.equals("1")) {
                            Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                            intent.putExtra("id", "def");
                            bar.setVisibility(View.INVISIBLE);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else if (profile.equals("0")){
                            if (user != null) {

                                //Toast.makeText(LoginActivity.this, "changed status:"+profile, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                bar.setVisibility(View.INVISIBLE);
                                startActivity(intent);
                                finishAffinity();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        }
        else
        {

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            final String name = user.getDisplayName().toString();
                            e = user.getUid().toString();

                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                    {
                                        String n = snapshot.getKey().toString();
                                        if (n.equals(e))
                                        {
                                            flag = 1;
                                            updateUI(user);
                                        }
                                    }
                                    if (flag != 1)
                                    {
                                        ref.child(e).child("profile").setValue("0");
                                        ref.child(e).child("email").setValue(user.getEmail().toString());
                                        //updateUI(user);
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateUI(user);
                                            }
                                        },4000);
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            updateUI(user);

                        }

                        else {
                            // If sign in fails, display a message to the user.
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            googleButton.setVisibility(View.VISIBLE);
                            bar.setVisibility(View.INVISIBLE);
                            updateUI(null);
                        }

                    
                    }
                });
    }

    public void open()
    {
        if (isNetworkConnected())
        {

        }
        else
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setTitle("Connection Error ");
            dialog.setMessage("Unable to connect with the server.\n Check your Internet connection and try again." );
            dialog.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }
            }).show();
        }

//        if(checkPermission())
//        {
//        }
//        else
//        {
//            Toast.makeText(this, "Please on the Internet", Toast.LENGTH_SHORT).show();
//            //requestPermission();
//        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

//    public Boolean checkPermission()
//    {
//        int result = ContextCompat.checkSelfPermission(getApplicationContext(),INTERNET);
////        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
////        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
//
//        return result == PackageManager.PERMISSION_GRANTED ;
//    }
//
//    public  void requestPermission()
//    {
//        int requestCode;
//        ActivityCompat.requestPermissions(this,new String[]{CAMERA,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,INTERNET},requestCode=1);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0) {
//
//                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (shouldShowRequestPermissionRationale( CAMERA )) {
//                            showMessageOKCancel( "You need to allow access to both the permissions",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                requestPermissions( new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, INTERNET},
//                                                        1 );
//                                            }
//                                        }
//                                    } );
//                            return;
//                        }
//                    }
//
//                }
//
//
//                break;
//        }
//    }

//
//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder( LoginActivity.this )
//                .setMessage( message )
//                .setPositiveButton( "OK", okListener )
//                .create()
//                .show();
//    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
