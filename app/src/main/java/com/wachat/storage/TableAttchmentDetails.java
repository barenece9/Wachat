package com.wachat.storage;

import android.content.Context;
import android.database.sqlite.SQLiteStatement;

import com.wachat.application.AppVhortex;
import com.wachat.dataClasses.AttachmentDetailsGetSet;

/**
 * Created by Argha on 22-12-2015.
 */
public class TableAttchmentDetails {

    private Context mContext;
    private AppVhortex app;


    public TableAttchmentDetails(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();
    }

    public synchronized  void insertAttachmentDetails(AttachmentDetailsGetSet mAttachmentDetailsGetSet) {

        String sql = "INSERT OR REPLACE INTO " + ConstantDB.TableAttachmentDetails + " ( "
                + ConstantDB.attachmentID + "," + ConstantDB.httpURL + ","
                + ConstantDB.filePath + "," + ConstantDB.fileType + ","
                + ConstantDB.thumbnailName + "," + ConstantDB.friendOrGrpID + "," + ConstantDB.assetURL + " ) " +
                "VALUES (?, ? , ? , ? , ? , ? , ?)";
        try {
            app.mDbHelper.getDB().beginTransaction();
            SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);

            if (mAttachmentDetailsGetSet.getAttachmentID() != null && !mAttachmentDetailsGetSet.getAttachmentID().equals(""))
                stmt.bindString(1, mAttachmentDetailsGetSet.getAttachmentID());
            else
                stmt.bindString(1, "");

            if (mAttachmentDetailsGetSet.getHttpURL() != null && !mAttachmentDetailsGetSet.getHttpURL().equals(""))
                stmt.bindString(2,mAttachmentDetailsGetSet.getHttpURL());
            else
                stmt.bindString(2, "");

            if (mAttachmentDetailsGetSet.getFilePath() != null && !mAttachmentDetailsGetSet.getFilePath().equals(""))
                stmt.bindString(3,mAttachmentDetailsGetSet.getFilePath());
            else
                stmt.bindString(3, "");

            if (mAttachmentDetailsGetSet.getFileType() != null && !mAttachmentDetailsGetSet.getFileType().equals(""))
                stmt.bindString(4,mAttachmentDetailsGetSet.getFileType());
            else
                stmt.bindString(4, "");

            if (mAttachmentDetailsGetSet.getThumbnailName()!= null && !mAttachmentDetailsGetSet.getThumbnailName().equals(""))
                stmt.bindString(5,mAttachmentDetailsGetSet.getThumbnailName());
            else
                stmt.bindString(5, "");

            if (mAttachmentDetailsGetSet.getFriendOrGrpID()!= null && !mAttachmentDetailsGetSet.getFriendOrGrpID().equals(""))
                stmt.bindString(6,mAttachmentDetailsGetSet.getFriendOrGrpID());
            else
                stmt.bindString(6, "");

            if (mAttachmentDetailsGetSet.getAssetURL()!= null && !mAttachmentDetailsGetSet.getAssetURL().equals(""))
                stmt.bindString(7,mAttachmentDetailsGetSet.getAssetURL());
            else
                stmt.bindString(7, "");
            stmt.execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void delete() {
        try{
            app.mDbHelper.getDB().delete(ConstantDB.TableAttachmentDetails, null, null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
