package com.wachat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.wachat.application.AppVhortex;
import com.wachat.data.DataStatus;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 24-09-2015.
 */
public class TableUserStatus {

    private Context mContext;
    private AppVhortex app;

    public TableUserStatus(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();
    }
    public  synchronized int isDataAvailable(String tableName) {
        int check = 0;
        Cursor cur = null;
        try {
            cur = app.mDbHelper.getDB().rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            if (cur != null) {
                cur.moveToFirst(); // Always one row returned.
                if (cur.getInt(0) == 0) // Zero count means empty table.
                    check = 0;
                else
                    check = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cur!=null && !cur.isClosed()){
                cur.close();
                cur = null;
            }
        }
        return check;
    }



    public synchronized ArrayList<DataStatus> getStatus() {
        ArrayList<DataStatus> mList=new ArrayList<DataStatus>();
         DataStatus mDataStatus = null;
        String SQL = "SELECT * FROM " + ConstantDB.TableUserStatus + " ORDER BY "+ConstantDB.statusId+" ASC;" ;
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToLast()) {
                c.moveToLast();
                do {
                    mDataStatus = new DataStatus();
                    mDataStatus.setStatusId(c.getString(c.getColumnIndex(ConstantDB.statusId)));
                    mDataStatus.setUserStatus(c.getString(c.getColumnIndex(ConstantDB.status)));
                    mDataStatus.setIsSelected(c.getString(c.getColumnIndex(ConstantDB.selected)));
                    mList.add(mDataStatus);
                }while (c.moveToPrevious());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return mList;
    }

    public synchronized boolean insertNewStatus(DataStatus mDataStatus) {
        long isSuccess = 0;

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.status, mDataStatus.getUserStatus());
        mContentValues.put(ConstantDB.selected , mDataStatus.getIsSelected());
        // mContentValues.put(ConstantDB.UserId, userId);
        isSuccess = isSuccess + app.mDbHelper.getDB().insert(ConstantDB.TableUserStatus, null, mContentValues);

        return (isSuccess > 0) ? true : false;
    }

    //insert default status
   /* public boolean insertStatus() {
        long isSuccess = 0;
        for (int i = 0; i < Constants.status.length(); i++) {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(ConstantDB.status, Constants.status.get(i));
           // mContentValues.put(ConstantDB.UserId, userId);
            isSuccess = isSuccess + app.mDbHelper.getDB().insert(ConstantDB.TableUserStatus, null, mContentValues);

        }
        return (isSuccess > 0) ? true : false;
    }*/


    public synchronized boolean updateStatusSelected(String statusID) {
        int isSuccess = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.selected, "1");
        isSuccess = app.mDbHelper.getDB().update(ConstantDB.TableUserStatus, mContentValues, ConstantDB.statusId + "=?", new String[]{statusID});
        return (isSuccess > 0) ? true : false;
    }

    public synchronized boolean updateAllStatusDeSelected() {
        int isSuccess = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.selected, "0");
        isSuccess = app.mDbHelper.getDB().update(ConstantDB.TableUserStatus, mContentValues, ConstantDB.selected + "=?", new String[]{"1"});
        return (isSuccess > 0) ? true : false;
    }

    public void delete(){
        try{
            app.mDbHelper.getDB().delete(ConstantDB.TableUserStatus, null, null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
