package com.wachat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.wachat.application.AppVhortex;
import com.wachat.data.DataProfile;
import com.wachat.data.DataValidateUser;
import com.wachat.services.ServiceXmppConnection;

/**
 * Created by Gourav Kundu on 24-09-2015.
 */
public class TableUserInfo {

    private Context mContext;
    private AppVhortex app;

    public TableUserInfo(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();
    }

    public synchronized boolean insertUser(DataValidateUser mDataValidateUser) {
        boolean isSuccessfull = false;
        String sql = "INSERT OR REPLACE INTO "
                + ConstantDB.TableUserInfo + " ( "
                + ConstantDB.UserId + ","
                + ConstantDB.UserName + ", " +
                ConstantDB.ProfileImage + ","
                + ConstantDB.PhoneNo + ", "
                + ConstantDB.CountryCode + ", "
                + ConstantDB.ChatId + ", "
                + ConstantDB.UserStatus + ", " +
                ConstantDB.IsRegistered + ", "
                + ConstantDB.IsVerified + ", "
                + ConstantDB.PairingValue + ", "
                + ConstantDB.Gender + ", " +
                ConstantDB.VerificationCode + ", "
                + ConstantDB.Language + ", "
                + ConstantDB.LanguageIdentifire + ", "
                + ConstantDB.CountryName + ", " +
                ConstantDB.IsTranslated + " , "
                + ConstantDB.IsFindByLocation + " , "
                + ConstantDB.IsFindByPhoneno + " , "
                + ConstantDB.isNotificationOn + " ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?,?,?,?)";
        if (mDataValidateUser != null && mDataValidateUser.getUserId().length() > 0) {
            try {
                app.mDbHelper.getDB().beginTransaction();
                SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);

                if (!TextUtils.isEmpty(mDataValidateUser.getUserId())) {
                    stmt.bindString(1, mDataValidateUser.getUserId());
                } else {
                    stmt.bindString(1, "");
                }
                stmt.bindString(2, mDataValidateUser.getUsername());
                stmt.bindString(3, mDataValidateUser.getProfileImage());
                stmt.bindString(4, mDataValidateUser.getPhoneNo());
                stmt.bindString(5, mDataValidateUser.getCountryCode());
                stmt.bindString(6, mDataValidateUser.getCountryCode() + mDataValidateUser.getPhoneNo() + "@" + ServiceXmppConnection.SERVICE_NAME);
                stmt.bindString(7, mDataValidateUser.getUserStatus());
                stmt.bindLong(8, mDataValidateUser.getIsRegistered());
                stmt.bindLong(9, mDataValidateUser.getIsVerified());
                stmt.bindLong(10, mDataValidateUser.getPairingValue());
                stmt.bindString(11, mDataValidateUser.getGender());
                stmt.bindString(12, mDataValidateUser.getVerificationCode());
                stmt.bindString(13, mDataValidateUser.getLanguage());
                stmt.bindString(14, mDataValidateUser.getLanguageIdentifire());
                stmt.bindString(15, mDataValidateUser.getCountryName());

                stmt.bindLong(16, Integer.parseInt(mDataValidateUser.getIstransalate()));
                stmt.bindLong(17, Integer.parseInt(mDataValidateUser.getIsfindbylocation()));
                stmt.bindLong(18, Integer.parseInt(mDataValidateUser.getIsfindbyphoneno()));
                stmt.bindLong(19, Integer.parseInt(mDataValidateUser.getIsNotificationOn()));
                stmt.execute();

                stmt.clearBindings();
                app.mDbHelper.getDB().setTransactionSuccessful();
                app.mDbHelper.getDB().endTransaction();
                isSuccessfull = true;

            } catch (Exception e) {
                e.printStackTrace();
                isSuccessfull = false;
            }
        }
        return isSuccessfull;
    }

    public synchronized DataProfile getUser() {
        DataProfile mDataProfile = null;
        String SQL = "SELECT * FROM " + ConstantDB.TableUserInfo + " ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                mDataProfile = new DataProfile();
                mDataProfile.setUserId(c.getString(c.getColumnIndex(ConstantDB.UserId)));
                mDataProfile.setUsername(c.getString(c.getColumnIndex(ConstantDB.UserName)));
                mDataProfile.setPhoneNo(c.getString(c.getColumnIndex(ConstantDB.PhoneNo)));
                mDataProfile.setUserStatus(c.getString(c.getColumnIndex(ConstantDB.UserStatus)));
                mDataProfile.setLanguage(c.getString(c.getColumnIndex(ConstantDB.Language)));
                mDataProfile.setProfileImage(c.getString(c.getColumnIndex(ConstantDB.ProfileImage)));
                mDataProfile.setLanguageIdentifire(c.getString(c.getColumnIndex(ConstantDB.LanguageIdentifire)));
                mDataProfile.setGender(c.getString(c.getColumnIndex(ConstantDB.Gender)));
                mDataProfile.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                mDataProfile.setCountryCode(c.getString(c.getColumnIndex(ConstantDB.CountryCode)));
                mDataProfile.setIstransalate(String.valueOf(c.getInt(c.getColumnIndex(ConstantDB.IsTranslated))));
                mDataProfile.setIsfindbylocation(String.valueOf(c.getInt(c.getColumnIndex(ConstantDB.IsFindByLocation))));
                mDataProfile.setIsfindbyphoneno(String.valueOf(c.getInt(c.getColumnIndex(ConstantDB.IsFindByPhoneno))));
                mDataProfile.setIsNotificationOn(String.valueOf(c.getInt(c.getColumnIndex(ConstantDB.isNotificationOn))));
                mDataProfile.setCountryName(c.getString(c.getColumnIndex(ConstantDB.CountryName)));
                mDataProfile.setVerificationCode(c.getString(c.getColumnIndex(ConstantDB.VerificationCode)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return mDataProfile;
    }


    public boolean updateStatus(String userId, String status) {
        int isSuccess = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.UserStatus, status);
        mContentValues.put(ConstantDB.UserId, userId);
        isSuccess = app.mDbHelper.getDB().update(ConstantDB.TableUserInfo, mContentValues, ConstantDB.UserId + "=?", new String[]{userId});
        return (isSuccess > 0) ? true : false;
    }

    public boolean updateStatusForLocationContactAndTranslate(ContentValues mContentValues, String userID) {
        int isSuccess = 0;
        isSuccess = app.mDbHelper.getDB().update(ConstantDB.TableUserInfo, mContentValues, ConstantDB.UserId + "=?",
                new String[]{userID});
        return (isSuccess > 0) ? true : false;
    }

  /*  public boolean editProfile(String userId, String userName, String image, String language, String gender) {
        int isSuccess = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.UserId, userId);
        mContentValues.put(ConstantDB.UserName, userName);
        mContentValues.put(ConstantDB.ProfileImage, image);
        mContentValues.put(ConstantDB.Language, language);
        mContentValues.put(ConstantDB.Gender, gender);
         isSuccess = app.mDbHelper.getDB().update(ConstantDB.TableUserInfo, mContentValues, ConstantDB.UserId + "=?", new String[]{userId});
       return (isSuccess > 0) ? true : false;
    }*/

    public boolean editProfile(ContentValues mContentValues, String userId) {
        int isSuccess = 0;
        isSuccess = app.mDbHelper.getDB().update(ConstantDB.TableUserInfo, mContentValues, ConstantDB.UserId + "=?", new String[]{userId});
        return (isSuccess > 0) ? true : false;
    }

    public void delete() {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableUserInfo, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
