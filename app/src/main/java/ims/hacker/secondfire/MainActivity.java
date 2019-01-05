package ims.hacker.secondfire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    EditText username;
    EditText password;

    Button loginbtn,signupbtn,imagebtn;


    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        loginbtn = (Button) findViewById(R.id.button);
        signupbtn = (Button) findViewById(R.id.button2);


        loginbtn.setOnClickListener(this);
        signupbtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null)
                {
                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                }
            }
        };
    }
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.button)
        {
            StartSignIn();
        }
        if (view.getId()==R.id.button2)
        {
            startActivity(new Intent(MainActivity.this, SignUp.class));
        }

    }

    private void StartSignIn() {
        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,"Please wait......", "processing....", true);
        String email = username.getText().toString();
        String passwordd = password.getText().toString();

//        mAuth.signInWithEmailAndPassword(email, passwordd)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });


        mAuth.signInWithEmailAndPassword(email,passwordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                    if(!task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this,"Sign In problem Please check your email/password ",Toast.LENGTH_LONG).show();
                    }
            }
        });

    }
}
