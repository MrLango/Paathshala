package lango.mr.paathshala;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LogIn extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private EditText email;
    private EditText password;
    private Button login;
    FirebaseAuth mAuth;
    Firebase myRef1;
    int n,i=0;
    List<User> user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Firebase.setAndroidContext(this);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        Toast.makeText(this,i+"",Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        myRef1=new Firebase("https://paathshala-48aa9.firebaseio.com/no_of_users");

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                String n1=dataSnapshot.getValue(String.class);
                n= Integer.parseInt(n1);
                Toast.makeText(getApplicationContext(),n+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        user=new ArrayList<>();
        myRef1=new Firebase("https://paathshala-48aa9.firebaseio.com/user");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                user.clear();
                int i=0;
                for(DataSnapshot user_info: dataSnapshot.getChildren()){
                    user=user_info.getValue(List.class);
                    Toast.makeText(getApplicationContext(),"Soap:"+(i++)+"",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(email.getText().toString(), password.getText().toString());
            }
        });

    }



            private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }



        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent i=new Intent(LogIn.this,Teacher.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Login failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Networks Error.",
                                    Toast.LENGTH_SHORT).show();
                        }
                       ;
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }
    private boolean validateForm() {
        boolean valid = true;

        String email1 = email.getText().toString();
        if (TextUtils.isEmpty(email1)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String pass = password.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;

    }
}
