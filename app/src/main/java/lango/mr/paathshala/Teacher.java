package lango.mr.paathshala;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Teacher extends AppCompatActivity {

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);


        db = openOrCreateDatabase("logged.db", MODE_PRIVATE, null);
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.execSQL("DROP TABLE USER;");
                db.close();
                Intent i=new Intent(Teacher.this,MainActivity.class);
                startActivity(i);

            }
        });
    }
}
