package lango.mr.paathshala;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


//import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private EditText email;
    private EditText password;
    FirebaseAuth mAuth;
    SQLiteDatabase db;
    DatabaseReference myRef1;
    int n;
    List<User> user;
    ListView l;

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    Firebase myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        db = openOrCreateDatabase("logged.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS USER(EMAIL VARCHAR(30),PASSWORD VARCHAR(30));");

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

        l=findViewById(R.id.listview);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        //myRef=FirebaseDatabase.getInstance().getReference().child("no_of_users");
        myRef=new Firebase("https://paathshala-48aa9.firebaseio.com/no_of_users");
        myRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
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
        myRef1 = FirebaseDatabase.getInstance().getReference().child("user");
        myRef1.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                user = new ArrayList<>();
                for (com.google.firebase.database.DataSnapshot d : dataSnapshot.getChildren())
                    user.add(new User(Integer.parseInt(d.getKey()), d.child("name").getValue(String.class), d.child("email").getValue(String.class), d.child("type").getValue(String.class)));
                l.setAdapter(new CustomAdapter());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Cursor c = db.rawQuery("SELECT * FROM USER;", null);

        try {
            if (c.moveToFirst()) {
                db.close();
                Intent i = new Intent(MainActivity.this, Teacher.class);
                startActivity(i);
                finish();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,Register.class);
                startActivity(i);
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn(email.getText().toString(), password.getText().toString());
            }
        });






    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this,ProximitySensor.class));
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return user.size();
        }

        @Override
        public Object getItem(int i) {
            return user.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null)
            {
                view=getLayoutInflater().inflate(android.R.layout.simple_list_item_1,null);
            }
            ((TextView)view.findViewById(android.R.id.text1)).setText(user.get(i).getEmail());
            return view;
        }
    }

    private void signIn(final String email, final String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }



        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    final String e = email;
                    final String p = password;
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser u = mAuth.getCurrentUser();
                            db.execSQL("INSERT INTO USER VALUES('"+email+"','"+password+"');");
                            Log.d(TAG,n+"");

                            Bundle bn=new Bundle();
                            String classs="";
                            for(int i=0;i<n;i++)
                            {
                                if(user.get(i).getEmail().equals(email))
                                {
                                    Toast.makeText(getApplicationContext(),user.get(i).getName(),Toast.LENGTH_LONG).show();
                                    bn.putString("name",user.get(i).getName());
                                    bn.putString("email",email);
                                    bn.putInt("id",user.get(i).getId());
                                    classs=user.get(i).getType();
                                    break;
                                }
                            }
                            Intent m;
                            if(classs.equals("teacher")){
                                m=new Intent(MainActivity.this,Teacher.class);
                                m.putExtras(bn);
                                startActivity(m);
                                finish();}
                            else if (classs.equals("student"))
                            {
                                m=new Intent(MainActivity.this,Student.class);
                                m.putExtras(bn);
                                startActivity(m);
                                finish();
                            }



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
