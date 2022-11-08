package com.example.utilities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DiskDB diskDB = new DiskDB(this);
        // MemoryDB memDB = MemoryDB.getInstance(this);
        diskDB.getWritableDatabase();

        String basePath = "/storage/emulated/0/";
        try{
            long stime = System.currentTimeMillis();

            // diskDB.addTag("Tag8");
            // diskDB.attachTag(basePath+"Download/resume.pdf", 8, true);
            // diskDB.updateTag("Tag8", "Tag9");
            // diskDB.detachTag(8, basePath+"Download/resume.pdf");

            long etime = System.currentTimeMillis();
            Log.i("test_run_time", String.valueOf(etime-stime));
        } catch (Exception e){
            e.printStackTrace();
            Log.e("test_run", e.toString());
        }
    }
}