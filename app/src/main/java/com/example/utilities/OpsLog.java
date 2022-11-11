package com.example.utilities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class OpsLog {

    public static final int TAG_DELETED = 1;
    public static final int DETACH_TAG = 2;

    public static final int FILE_MOVED = 11;
    public static final int FILE_DELETED = 12;
    public static final int FILE_RENAMED = 13;

    SQLiteDatabase db = null;
    Context ctx = null;

    public OpsLog(Context ctx){
        this.ctx = ctx;
        this.db = new DiskDB(ctx).getWritableDatabase();
        OpsLog self = this;

        ScheduledExecutorService operationScheduler = Executors.newSingleThreadScheduledExecutor();
        operationScheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Cursor c = self.db.query(Schema.tableOps, new String[]{Schema.OpsLog.op, Schema.OpsLog.data, "id"}, null, null, null, null, null, "1");
                if(c.getCount() == 0){
                    return;
                }
                c.moveToFirst();
                try {
                    JSONObject data = null;
                    data = new JSONObject(c.getString(1));
                    String taskCompletedMsg = "";
                    switch(c.getInt(0)){
                        case DETACH_TAG: {
                            int tagId = data.getInt("tag_id");
                            String filePath = data.getString("old_path");
                            self.detachTag(tagId, filePath);
                            break;
                        }
                        case TAG_DELETED:{
                            int tagId = data.getInt("tag_id");
                            self.deleteTagData(tagId);
                            taskCompletedMsg = "Data for the tag cleared";
                            break;
                        }
                    }
                    self.db.delete(Schema.tableOps, "id="+c.getString(2), null);
                    c.close();
                    if(taskCompletedMsg.length() == 0) return;
                    ((Activity)self.ctx).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(self.ctx, "Task Completed", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e("test_run_ops", e.toString());
                    e.printStackTrace();
                    self.db.delete(Schema.tableOps, "id="+c.getString(2), null);
                    return;
                }
            }
        },0, 2, TimeUnit.SECONDS);
    }

    public void enqueue(int op, Integer tagId, String old_path, String new_path) throws IllegalArgumentException, JSONException {
        ContentValues r = new ContentValues();
        JSONObject data = new JSONObject();
        r.put(Schema.OpsLog.op, op);
        r.put(Schema.OpsLog.completed, false);
        switch(op){
            case DETACH_TAG: {
                if(tagId == null || old_path == null) throw new IllegalArgumentException("Argument tagId and old_path required");

                data.put("tag_id", tagId);
                data.put("old_path", old_path);

                r.put(Schema.OpsLog.data, data.toString());
                break;
            }
            case TAG_DELETED: {
                if(tagId == null) throw new IllegalArgumentException("Argument tagId required");

                data.put("tag_id", tagId);

                r.put(Schema.OpsLog.data, data.toString());
                break;
            }
        }
        this.db.insert(Schema.tableOps, null, r);
    }

    private String removeFromTagList(int tagId, String[] tagList){
        ArrayList<String> list = new ArrayList<>(Arrays.asList(tagList));
        list.remove(String.valueOf(tagId));
        String updatedTagList = list.toString().replaceAll("\\s", "");
        updatedTagList = updatedTagList.substring(1, updatedTagList.length()-1);
        return updatedTagList;
    }

    public void detachTag(int tagId, String filePath){
        Cursor c = this.db.query(Schema.tableFileToTags, new String[]{Schema.FileToTags.tags}, Schema.FileToTags.filePath+"=?", new String[]{filePath}, null, null, null);
        c.moveToFirst();
        String[] tagList = c.getString(0).split(",");
        c.close();
        if(tagList.length > 1){
            String updatedTagList = removeFromTagList(tagId, tagList);
            String q = "UPDATE "+Schema.tableFileToTags+" SET "+Schema.FileToTags.tags+"='"+updatedTagList+"' WHERE "+Schema.FileToTags.filePath+"='"+filePath+"'";
            db.execSQL(q);
        } else {
            db.delete(Schema.tableFileToTags, Schema.FileToTags.filePath+"='"+filePath+"'", null);
        }
    }

    private Cursor getTaggedFileByPage(int limit, int offset){
        return this.db.query(Schema.tableFileToTags, new String[]{Schema.FileToTags.tags, Schema.FileToTags.filePath}, null, null, null, null, null, offset+","+limit);
    }

    public void deleteTagData(int tagId){
        int limit = 1;
        Cursor c = getTaggedFileByPage(limit, 0);
        boolean keepFetching = true;
        int p = 0;
        while(keepFetching){
            Log.d("test_run-count", String.valueOf(c.getCount()));
            if(c.getCount() < limit){
                keepFetching = false;
            }
            if(c.getCount() == 0) return;
            c.moveToFirst();
            for(int i = 0; i < c.getCount(); i++){
                String filePath = c.getString(1);
                String tags = c.getString(0);
                Log.d("test_run_delete-tag", filePath);
                String tagList[] = tags.split(",");
                String updatedTags = removeFromTagList(tagId, tagList);
                if(!tags.equals(updatedTags)){
                    String q = "UPDATE "+Schema.tableFileToTags+" SET "+Schema.FileToTags.tags+"='"+updatedTags+"' WHERE "+Schema.FileToTags.filePath+"='"+filePath+"'";
                    db.execSQL(q);
                }
            }
            c = getTaggedFileByPage(limit, ++p*limit);
        }
        c.close();
    }
}
