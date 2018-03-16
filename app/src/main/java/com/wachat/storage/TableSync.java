package com.wachat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.wachat.application.AppVhortex;

/**
 * Created by Gourav Kundu on 03-10-2015.
 */
public class TableSync {

    private Context mContext;
    private AppVhortex app;


    public TableSync(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();
    }

    public boolean setStatus(String status) {
        long isSuccess = 0;
        delete();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.status, status);
        try{
            isSuccess = app
                    .mDbHelper.getDB().insert(ConstantDB.TableContactSync, null, mContentValues);
        }catch(Exception e){
            e.printStackTrace();
        }
        return (isSuccess>0)?true:false;
    }

    /**
     * Check if Any Updation or alteration is done in the user phone book
     * If changes happened then return will be true else false;
     * @return
     */
    public boolean isSynced(){
        String status = "0";
        String SQL = "SELECT * FROM "+ConstantDB.TableContactSync+";";
        Cursor c = null;
        try{
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if(c != null && c.getCount()>0 && c.moveToFirst()){
                status = c.getString(c.getColumnIndex(ConstantDB.status));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(c!=null && !c.isClosed()){
                c.close();
                c = null;
            }
        }
        return (status.equals("0"))?false:true;
    }

    public void delete(){
        try{
             app.mDbHelper.getDB().delete(ConstantDB.TableContactSync, null, null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
