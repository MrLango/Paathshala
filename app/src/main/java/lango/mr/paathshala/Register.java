package lango.mr.paathshala;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Register extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private EditText email;
    private EditText password;
    private EditText name;
    private Button register;
    private Button verify;
    private String e_mail,namee;
    private TeacherStudent ts;
    private String pass;
    private RadioButton teacher,student;
    private FirebaseAuth mAuth;
    int n,i;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    Firebase myRef1;
//    private TextView txtyour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        txtyour = findViewById(R.id.textView);
//        Typeface type = Typeface.createFromAsset(getAssets(),"font/bebas.ttf");
//        txtyour.setTypeface(type);
        Firebase.setAndroidContext(this);
        email=findViewById(R.id.email);
        name = findViewById(R.id.name);
        teacher=findViewById(R.id.TeacherRegister);
        student=findViewById(R.id.StudentRegister);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        verify=findViewById(R.id.Verify);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "incall");
        }
        if (!mWakeLock.isHeld()) {
            Log.d(TAG, "New call active : acquiring incall (CPU only) wake lock");
            mWakeLock.acquire();
        } else {
            Log.d(TAG, "New call active while incall (CPU only) wake lock already active");
        }
        mAuth = FirebaseAuth.getInstance();
        myRef1=new Firebase("https://paathshala-48aa9.firebaseio.com/no_of_users");
        i=0;
        //Toast.makeText(getApplicationContext(),myRef1.getParent()+"egergre",Toast.LENGTH_LONG);
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                String n1=dataSnapshot.getValue(String.class);
                n= Integer.parseInt(n1);
                i=1;
                Toast.makeText(getApplicationContext(),n+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(email.getText().toString(), password.getText().toString());
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });


    }

    private void createAccount(final String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        if(!(teacher.isChecked()||student.isChecked()))
        {
            Toast.makeText(Register.this,"Select either student or teacher",Toast.LENGTH_SHORT).show();
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Registered",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseDatabase database=FirebaseDatabase.getInstance();
                            DatabaseReference myRef=database.getReference("user");

                            //while(i==0){}
                            if (teacher.isChecked()) {
                                n+=1;
                                myRef=myRef.child(n+"");
                                myRef.child("email").setValue(email);
                                myRef.child("name").setValue(name.getText().toString());
                                myRef.child("type").setValue("teacher");
                            }
                            else if(student.isChecked()) {
                                n+=1;
                                myRef=myRef.child(n+"");
                                myRef.child("email").setValue(email);
                                myRef.child("name").setValue(name.getText().toString());
                                myRef.child("type").setValue("student");
                            }
                            myRef1.setValue((n)+"");



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        namee = name.getText().toString();
        if (TextUtils.isEmpty(namee)) {
            name.setError("Required.");
            valid = false;
        } else {
            name.setError(null);
        }

        e_mail = email.getText().toString();
        if (TextUtils.isEmpty(e_mail)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        pass = password.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.Verify).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.Verify).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(Register.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_LONG).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

}
