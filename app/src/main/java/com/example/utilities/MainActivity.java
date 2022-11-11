package com.example.utilities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DiskDB diskDB = new DiskDB(this);
        diskDB.getWritableDatabase();
        String basePath = "/storage/emulated/0/";
        try{

            MemoryDB memDB = MemoryDB.getInstance(this);
            long stime = System.currentTimeMillis();

            // /storage/emulated/0/Download/resume.pdf;/storage/emulated/0/Download/cs.pdf

            // diskDB.addTag("Tag8");
            // diskDB.attachTag(basePath+"Download/resume.pdf", 8, true);
            // diskDB.updateTag("Tag8", "Tag9");
            // diskDB.detachTag(8, basePath+"Download/resume.pdf");
            // memDB.removeTag("Tag8");
            /*FileItem[] lists = diskDB.listFilesFor(8, null, null).sortList("date").getFileArray();
            for(FileItem l : lists){
                Log.d("test_run_list", String.valueOf(l.fileName));
            }*/
            Log.d("test_run", Arrays.toString(memDB.getTagsForFile(basePath+"Download/cs.pdf")));
            long etime = System.currentTimeMillis();
            Log.i("test_run_time", String.valueOf(etime-stime));

            /*Activity self = this;
            Thread deleterThread = diskDB.deleteTags(5);
            Toast.makeText(self, "Thread is running you will be notified once complete", Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean completed = false;
                    while(!completed){
                        if(deleterThread.getState() == Thread.State.TERMINATED) {
                            completed = true;
                            self.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(self, "Deleter Thread complete", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            }).start();*/
        } catch (Exception e){
            e.printStackTrace();
            Log.e("test_run", e.toString());
        }
    }
}