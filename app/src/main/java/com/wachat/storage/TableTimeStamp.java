package com.wachat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.wachat.application.AppVhortex;

/**
 * Created by Argha on 08-10-2015.
 */
public class TableTimeStamp {

    private Context mContext;
    private AppVhortex app;


    public TableTimeStamp(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();
    }

    public boolean setStatus(String timeStamp) {
        long isSuccess = 0;
        delete();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.strTimeStamp, timeStamp);
        try{
            isSuccess = app
                    .mDbHelper.getDB().insert(ConstantDB.TableTimeStamp, null, mContentValues);
        }catch(Exception e){
            e.printStackTrace();
        }

        return (isSuccess>0)?true:false;
    }

    public String getTimeStamp(){
        String status = "0";
        String SQL = "SELECT * FROM "+ConstantDB.TableTimeStamp+";";
        Cursor c = null;
        try{
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if(c != null && c.getCount()>0 && c.moveToFirst()){
                status = c.getString(c.getColumnIndex(ConstantDB.strTimeStamp));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(c!=null && !c.isClosed()){
                c.close();
                c = null;
            }
        }
        return (status.equals("") || status == null || status.equals("0"))?"all":status;
    }

    public void delete(){
        try{
            app.mDbHelper.getDB().delete(ConstantDB.TableTimeStamp, null, null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
