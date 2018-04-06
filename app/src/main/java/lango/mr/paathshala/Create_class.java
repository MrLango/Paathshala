package lango.mr.paathshala;

import android.content.Context;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Create_class extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    Button add,submit;
    ListView listView;
    EditText e1,e2;
    DatabaseReference myRef1,myRef,n1;
    int n,n3;
    List<User> user;
    ArrayList<String> student;
    ArrayAdapter d1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        add=findViewById(R.id.add);
        submit=findViewById(R.id.submit);
        listView=findViewById(R.id.list);
        e1=findViewById(R.id.e1);
        e2=findViewById(R.id.e2);
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
        myRef1 = FirebaseDatabase.getInstance().getReference().child("user");
        myRef1.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                user = new ArrayList<>();
                for (com.google.firebase.database.DataSnapshot d : dataSnapshot.getChildren()) {
                    user.add(new User(Integer.parseInt(d.getKey()), d.child("name").getValue(String.class), d.child("email").getValue(String.class), d.child("type").getValue(String.class)));
                    n += 1;
                }
                //listView.setAdapter(new CustomAdapter());
                Toast.makeText(getApplication(),"Length:"+n,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        myRef=FirebaseDatabase.getInstance().getReference().child("class");
        n1=FirebaseDatabase.getInstance().getReference().child("class/no_of_class");
        n1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) throws NumberFormatException{
                String n2=dataSnapshot.getValue(String.class);
                n3= Integer.parseInt(n2);
                Toast.makeText(getApplicationContext(),n3+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        student=new ArrayList<>();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=new String(e2.getText().toString());
                Log.d(TAG,"Email : "+email);
                int flag=0;
                for(int i=0;i<n;i++)
                {
                    Log.d(TAG,"QQWWEE"+user.get(i).getEmail().compareTo(email));
                    if(user.get(i).getEmail().compareTo(email)==0)
                    {
                        student.add(email);
                        flag=1;
                        Toast.makeText(getApplicationContext(),"User exist",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(flag==0)
                {
                    Toast.makeText(getApplicationContext(),"User does not exist",Toast.LENGTH_SHORT).show();
                }
                d1=new ArrayAdapter(Create_class.this,android.R.layout.simple_list_item_1,student);
                listView.setAdapter(d1);

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!e1.getText().toString().equals(""))
                {
                    String userlist="";
                    userlist=userlist.concat(getIntent().getExtras().getString("email"));
                    userlist=userlist.concat(":");
                    int i;
                    for(i=0;i<student.size()-1;i++)
                    {
                        userlist=userlist.concat(student.get(i));
                        userlist=userlist.concat(":");
                    }
                    userlist=userlist.concat(student.get(i));
                    n3+=1;
                    myRef.child(e1.getText().toString()+"class"+n3).setValue(userlist);
                    n1.setValue((n3)+"");
                    Toast.makeText(getApplicationContext(),"Class Created",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Name Required",Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }
    class CustomAdapter extends BaseAdapter {

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
}
