package ims.hacker.secondfire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    EditText emailedt, passwordedt;
    Button signupbtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailedt=(EditText) findViewById(R.id.emailedt);
        passwordedt=(EditText) findViewById(R.id.passwordedt);

        signupbtn = (Button) findViewById(R.id.signupbtn);
        firebaseAuth = FirebaseAuth.getInstance();
        signupbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.signupbtn)
        {
            usersignup();
        }
    }

    private void usersignup() {
        final ProgressDialog progressDialog = ProgressDialog.show(SignUp.this,"Please wait......", "processing....", true);
        (firebaseAuth.createUserWithEmailAndPassword(emailedt.getText().toString(), passwordedt.getText().toString())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    Toast.makeText(SignUp.this, "Registration Successfull", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(SignUp.this, AccountActivity.class);
                    i.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                    startActivity(i);
                }
                else {
                    Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
