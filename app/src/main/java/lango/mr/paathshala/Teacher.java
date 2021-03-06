package lango.mr.paathshala;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Teacher extends AppCompatActivity {

    SQLiteDatabase db;
    private static final String TAG = "EmailPassword";
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Teacher.this,Create_class.class);
                Bundle bn=new Bundle();
                bn.putString("name",getIntent().getExtras().getString("name"));
                bn.putString("email",getIntent().getExtras().getString("email"));
                bn.putInt("id",getIntent().getExtras().getInt("id"));
                i.putExtras(bn);
                startActivity(i);
            }
        });
        findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
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
        db = openOrCreateDatabase("logged.db", MODE_PRIVATE, null);
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.execSQL("DROP TABLE USER;");
                db.close();
                Intent i=new Intent(Teacher.this,MainActivity.class);
                startActivity(i);
                finish();

            }
        });
    }
}
