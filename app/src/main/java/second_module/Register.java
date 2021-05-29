package second_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.remo.R;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

public class Register extends AppCompatActivity {

    private EditText etEmailAddress, etPassword, etIdNo;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        etEmailAddress = findViewById(R.id.etEmailAddress);
        etIdNo = findViewById(R.id.etIdNo);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Account Verification on going.");
                progressDialog.setMessage("Please wait as your account is being verified.");
                progressDialog.setCanceledOnTouchOutside(false);

                final String txtEmail = etEmailAddress.getText().toString();
                final String txtPassword = etPassword.getText().toString();
                final String txtIdNo = etIdNo.getText().toString();

                if (!TextUtils.isEmpty(txtEmail)
                        && !TextUtils.isEmpty(txtPassword) && txtPassword.length() > 6){


                    mAuth.fetchSignInMethodsForEmail(txtEmail)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean isNewUser = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).isEmpty();

                                    if (isNewUser) {

                                        if (!TextUtils.isEmpty(txtIdNo)){
                                            createUser(txtEmail, txtPassword, txtIdNo);
                                        }else {
                                            if (TextUtils.isEmpty(txtIdNo))etIdNo.setError("Id number cannot be empty..");
                                        }

                                    } else {

                                        LoginUser(txtEmail, txtPassword);
                                    }

                                }
                            });

                    progressDialog.show();


                }else {

                    progressDialog.dismiss();

                    if (TextUtils.isEmpty(txtEmail))etEmailAddress.setError("Email address cannot be empty..");
                    if (TextUtils.isEmpty(txtPassword))etPassword.setError("Password cannot be empty..");
                    if (txtPassword.length() <= 6)
                        Toast.makeText(Register.this, "Enter as stronger password.", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void LoginUser(String txtEmail, String txtPassword) {

        mAuth.signInWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    String userMail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                    assert userMail != null;

                    Intent intent = new Intent(getApplicationContext(), Calculator.class);
                    startActivity(intent);
                    finish();

                    progressDialog.dismiss();
                    Toast.makeText(Register.this, "Login successful..", Toast.LENGTH_SHORT).show();

                }else {

                    progressDialog.dismiss();
                    Log.w("TAG", "signUserWithEmail:failure", task.getException());
                    Toast.makeText(Register.this, "Authentication failed. Try again "+task.getException().getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void createUser(String txtEmail, String txtPassword, String txtIdNo) {

        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){


                    String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    DatabaseReference newPost = databaseReference.child("users").child(uid);
                    newPost.child("uid").setValue(uid);
                    newPost.child("id_number").setValue(txtIdNo);
                    newPost.child("email").setValue(txtEmail);

                    progressDialog.dismiss();

                    Intent intent = new Intent(getApplicationContext(), Calculator.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(Register.this, "Registration successfully..", Toast.LENGTH_SHORT).show();

                }else {
                    progressDialog.dismiss();
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Register.this, "Authentication failed. Try again "+task.getException().getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Calculator.class);
            startActivity(intent);
            finish();
        }


    }

}