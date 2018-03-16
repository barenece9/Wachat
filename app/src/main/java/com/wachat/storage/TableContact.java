package com.wachat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.wachat.application.AppVhortex;
import com.wachat.data.DataContact;
import com.wachat.data.DataContactForGroup;
import com.wachat.data.DataProfile;
import com.wachat.data.DataTextChat;
import com.wachat.services.ServiceXmppConnection;
import com.wachat.util.CommonMethods;
import com.wachat.util.LogUtils;
import com.wachat.util.ValidationUtils;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 28-09-2015.
 */
public class TableContact {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(TableContact.class);
    private Context mContext;
    private AppVhortex app;

    public TableContact(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();
    }

    public boolean insertBulk(ArrayList<DataContact> arrDataCategories) {

        LogUtils.d("TableContact","insertBulk:Started");
        boolean isSuccess = false;
        String sql = "INSERT OR REPLACE INTO " + ConstantDB.TableContactList + " ( " + ConstantDB.PhoneNumber + ","
                + ConstantDB.CountryCode + ","
                + ConstantDB.Name + ","
                + ConstantDB.FriendId + ","
                + ConstantDB.IsRegistered + ","
                + ConstantDB.FriendImageLink + ","
                + ConstantDB.ChatId + ","
                + ConstantDB.Status + ","
                + ConstantDB.IsDeleted + ","
                + ConstantDB.AppName + ","
                + ConstantDB.AppFriendImageLink + ","
                + ConstantDB.IsBlocked + ","
                + ConstantDB.IsFavorite + ","
                + ConstantDB.imageUrl + ","
                + ConstantDB.gender + ","
                + ConstantDB.isfindbyphoneno + ","
                + ConstantDB.isSynced + ","
                + ConstantDB.isFriend + ","
                + ConstantDB.relation + " ) " +
                "VALUES (?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";

        try {

            app.mDbHelper.getDB().beginTransactionNonExclusive();
            SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);

            DataContact mDataContact;

            for (int i = 0; i < arrDataCategories.size(); i++) {
                mDataContact = arrDataCategories.get(i);
                stmt.clearBindings();
                if (mDataContact.getPhoneNumber() != null && !mDataContact.getPhoneNumber().equals(""))
                    stmt.bindString(1, mDataContact.getPhoneNumber());
                else
                    stmt.bindString(1, "");

                if (mDataContact.getCountryCode() != null && !mDataContact.getCountryCode().equals(""))
                    stmt.bindString(2, mDataContact.getCountryCode());
                else
                    stmt.bindString(2, "");

                if (mDataContact.getName() != null && !mDataContact.getName().equals(""))
                    stmt.bindString(3, mDataContact.getName());
                else
                    stmt.bindString(3, "");

                if (mDataContact.getFriendId() != null && !mDataContact.getFriendId().equals(""))
                    stmt.bindString(4, mDataContact.getFriendId());
                else
                    stmt.bindString(4, "");

                stmt.bindLong(5, mDataContact.getIsRegistered());

                if (mDataContact.getFriendImageLink() != null && !mDataContact.getFriendImageLink().equals(""))
                    stmt.bindString(6, mDataContact.getFriendImageLink());
                else
                    stmt.bindString(6, "");

                if (mDataContact.getChatId() != null && !mDataContact.getChatId().equals(""))
                    stmt.bindString(7, mDataContact.getChatId());
                else
                    stmt.bindString(7, "");

                if (mDataContact.getStatus() != null && !mDataContact.getStatus().equals(""))
                    stmt.bindString(8, CommonMethods.getUTFDecodedString(mDataContact.getStatus()));
                else
                    stmt.bindString(8, "");

                if (mDataContact.getIsDeleted() != null && !mDataContact.getIsDeleted().equals(""))
                    stmt.bindString(9, mDataContact.getIsDeleted());
                else
                    stmt.bindString(9, "");

                if (mDataContact.getAppName() != null && !mDataContact.getAppName().equals(""))
                    stmt.bindString(10, mDataContact.getAppName());
                else
                    stmt.bindString(10, "");

                if (mDataContact.getAppFriendImageLink() != null && !mDataContact.getAppFriendImageLink().equals(""))
                    stmt.bindString(11, mDataContact.getAppFriendImageLink());
                else
                    stmt.bindString(11, "");

                stmt.bindLong(12, mDataContact.getIsBlocked());

                stmt.bindLong(13, mDataContact.getIsFavorite());

                if (mDataContact.getImageUrl() != null && !mDataContact.getImageUrl().equals(""))
                    stmt.bindString(14, mDataContact.getImageUrl());
                else
                    stmt.bindString(14, "");
                if (!TextUtils.isEmpty(mDataContact.getGender()))
                    stmt.bindString(15, mDataContact.getGender());
                else
                    stmt.bindString(15, "");
                if (!TextUtils.isEmpty(mDataContact.getIsfindbyphoneno()))
                    stmt.bindString(16, mDataContact.getIsfindbyphoneno());
                else
                    stmt.bindString(16, "");

                stmt.bindLong(17, mDataContact.getIsSynced());
                stmt.bindLong(18, mDataContact.getIsFriend());
                stmt.bindString(19, mDataContact.getRelation());

                stmt.execute();

            }

            app.mDbHelper.getDB().setTransactionSuccessful();
            app.mDbHelper.getDB().endTransaction();

            isSuccess = true;
//            deleteOwnNumber();
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }
        LogUtils.d("TableContact","insertBulk:isSuccess:"+isSuccess);
        return isSuccess;
    }

    public boolean insertBulkForUnknownNUmber(DataContact mDataContact) {

        boolean isSuccess = false;
        String sql = "INSERT OR REPLACE INTO " + ConstantDB.TableContactList + " ( " + ConstantDB.PhoneNumber + ","
                + ConstantDB.CountryCode + ","
                + ConstantDB.Name + ","
                + ConstantDB.FriendId + ","
                + ConstantDB.IsRegistered + ","
                + ConstantDB.FriendImageLink + ","
                + ConstantDB.ChatId + ","
                + ConstantDB.Status + ","
                + ConstantDB.IsDeleted + ","
                + ConstantDB.AppName + ","
                + ConstantDB.AppFriendImageLink + ","
                + ConstantDB.IsBlocked + ","
                + ConstantDB.IsFavorite + ","
                + ConstantDB.imageUrl + ","
                + ConstantDB.gender + ","
                + ConstantDB.isfindbyphoneno + ","
                + ConstantDB.isSynced + ","
                + ConstantDB.isFriend + ","
                + ConstantDB.relation + " ) " +
                "VALUES (?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";

        try {
            app.mDbHelper.getDB().beginTransactionNonExclusive();
            SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);

            if (mDataContact.getPhoneNumber() != null && !mDataContact.getPhoneNumber().equals(""))
                stmt.bindString(1, /*mDataContact.getCountryCode() +*/ mDataContact.getPhoneNumber());
            else
                stmt.bindString(1, "");

            if (mDataContact.getCountryCode() != null && !mDataContact.getCountryCode().equals(""))
                stmt.bindString(2, mDataContact.getCountryCode());
            else
                stmt.bindString(2, "");

            if (mDataContact.getName() != null && !mDataContact.getName().equals(""))
                stmt.bindString(3, mDataContact.getName());
            else
                stmt.bindString(3, "");

            if (mDataContact.getFriendId() != null && !mDataContact.getFriendId().equals(""))
                stmt.bindString(4, mDataContact.getFriendId());
            else
                stmt.bindString(4, "");

            stmt.bindLong(5, mDataContact.getIsRegistered());

            if (mDataContact.getFriendImageLink() != null && !mDataContact.getFriendImageLink().equals(""))
                stmt.bindString(6, mDataContact.getFriendImageLink());
            else
                stmt.bindString(6, "");

            if (mDataContact.getChatId() != null && !mDataContact.getChatId().equals(""))
                stmt.bindString(7, mDataContact.getChatId());
            else
                stmt.bindString(7, "");

            if (mDataContact.getStatus() != null && !mDataContact.getStatus().equals(""))
                stmt.bindString(8, CommonMethods.getUTFDecodedString(mDataContact.getStatus()));
            else
                stmt.bindString(8, "");

            if (mDataContact.getIsDeleted() != null && !mDataContact.getIsDeleted().equals(""))
                stmt.bindString(9, mDataContact.getIsDeleted());
            else
                stmt.bindString(9, "");

            if (mDataContact.getAppName() != null && !mDataContact.getAppName().equals(""))
                stmt.bindString(10, mDataContact.getAppName());
            else
                stmt.bindString(10, "");

            if (mDataContact.getAppFriendImageLink() != null && !mDataContact.getAppFriendImageLink().equals(""))
                stmt.bindString(11, mDataContact.getAppFriendImageLink());
            else
                stmt.bindString(11, "");

            stmt.bindLong(12, mDataContact.getIsBlocked());

            stmt.bindLong(13, mDataContact.getIsFavorite());

            if (mDataContact.getImageUrl() != null && !mDataContact.getImageUrl().equals(""))
                stmt.bindString(14, mDataContact.getImageUrl());
            else
                stmt.bindString(14, "");
            if (!TextUtils.isEmpty(mDataContact.getGender()))
                stmt.bindString(15, mDataContact.getGender());
            else
                stmt.bindString(15, "");
            if (!TextUtils.isEmpty(mDataContact.getIsfindbyphoneno()))
                stmt.bindString(16, mDataContact.getIsfindbyphoneno());
            else
                stmt.bindString(16, "");

            stmt.bindLong(17, mDataContact.getIsSynced());
            stmt.bindLong(18, mDataContact.getIsFriend());
            stmt.bindString(19, mDataContact.getRelation());
            stmt.execute();

            stmt.clearBindings();
            app.mDbHelper.getDB().setTransactionSuccessful();
            app.mDbHelper.getDB().endTransaction();
            isSuccess = true;
//            deleteOwnNumber();
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }
        return isSuccess;
    }


//    public void deleteOwnNumber() {
//
//        DataProfile mDataProfile = new TableUserInfo(mContext).getUser();
//        String SQL;
//        if(mDataProfile != null){
//            SQL = "DELETE FROM " + ConstantDB.TableContactList + " WHERE " + ConstantDB.PhoneNumber + "='" + mDataProfile.getPhoneNo() + "'";
//            Cursor c = null;
//            try {
//                c = app.mDbHelper.getDB().rawQuery(SQL, null);
//                if (c != null && c.getCount() > 0) {
//                    Log.i(getClass().getSimpleName(), "My Number Deleted");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (c != null) {
//                    c.close();
//                }
//            }
//        }
//    }


//    public synchronized boolean updateUser(ArrayList<DataContact> mListContacts) {

    public boolean updateUser(ArrayList<DataContact> mListContacts) {
        boolean isSuccess = false;
        String sql = "UPDATE " + ConstantDB.TableContactList + " SET "
                + ConstantDB.FriendId + "=?,"
                + ConstantDB.ChatId + "=?,"
                + ConstantDB.AppName + "=?,"
                + ConstantDB.Status + "=?,"
                + ConstantDB.AppFriendImageLink + "=?,"
                + ConstantDB.IsBlocked + "=?,"
                + ConstantDB.IsFavorite + "=?,"
                + ConstantDB.IsRegistered + "=?,"
                + ConstantDB.isSynced + "=?,"
                + ConstantDB.isFriend + "=?,"
                + ConstantDB.IsDeleted + "=?,"
                + ConstantDB.gender + "=?,"
                + ConstantDB.isfindbyphoneno + "=?,"
                + ConstantDB.relation + "=?,"
                + ConstantDB.PhoneNumber + "=?,"
                + ConstantDB.CountryCode + "=?"
                + " WHERE " + ConstantDB.PhoneNumber + "=? OR "
                + ConstantDB.PhoneNumber + "=?";
        DataProfile mDataProfile = new TableUserInfo(mContext).getUser();
        try {
            app.mDbHelper.getDB().beginTransactionNonExclusive();
            SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);

            DataContact mDataContact;
            LogUtils.d("TableContact","updateUser:Started");
            for (int i = 0; i < mListContacts.size(); i++) {
                mDataContact = mListContacts.get(i);
                stmt.clearBindings();
                stmt.bindString(1, mDataContact.getFriendId());
                if (ValidationUtils.containsXmppDomainName(mDataContact.getChatId())) {
                    stmt.bindString(2, mDataContact.getChatId());
                } else {
                    stmt.bindString(2, mDataContact.getChatId() + ServiceXmppConnection.SERVICE_NAME);
                }
                stmt.bindString(3, mDataContact.getName());
                stmt.bindString(4, CommonMethods.getUTFDecodedString(mDataContact.getStatus()));
                stmt.bindString(5, mDataContact.getAppFriendImageLink());
                stmt.bindString(6, String.valueOf(mDataContact.getIsBlocked()));
                stmt.bindString(7, String.valueOf(mDataContact.getIsFavorite()));
//                if(mDataProfile!=null) {
//                    if (!TextUtils.isEmpty(mDataProfile.getPhoneNo())
//                            &&mDataContact.getPhoneNumber().equalsIgnoreCase(mDataProfile.getPhoneNo())){
//                        stmt.bindString(8, "0");
//                    }else{
//                        stmt.bindString(8, String.valueOf(mDataContact.getIsRegistered()));
//                    }
//                }else {
                    stmt.bindString(8, String.valueOf(mDataContact.getIsRegistered()));
//                }
                stmt.bindString(9, String.valueOf(mDataContact.getIsSynced()));
                stmt.bindString(10, String.valueOf(mDataContact.getIsFriend()));
                stmt.bindString(11, String.valueOf(mDataContact.getIsDeleted()));
                stmt.bindString(12, String.valueOf(mDataContact.getGender()));
                stmt.bindString(13, String.valueOf(mDataContact.getIsfindbyphoneno()));
                stmt.bindString(14, String.valueOf(mDataContact.getRelation()));
                stmt.bindString(15, mDataContact.getCountryCode().replace("+", "") + mDataContact.getPhoneNumber());
                stmt.bindString(16, mDataContact.getCountryCode().replace("+", ""));
                stmt.bindString(17, mDataContact.getCountryCode().replace("+", "") + mDataContact.getPhoneNumber());
                stmt.bindString(18, mDataContact.getPhoneNumber());
                stmt.execute();
            }
            app.mDbHelper.getDB().setTransactionSuccessful();
            app.mDbHelper.getDB().endTransaction();
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        LogUtils.d("TableContact","updateUser:isSuccess:"+isSuccess);
        return isSuccess;
    }
//        int count = 0;
//        String condition;
//        int k;
//        DataContact mDataContact;
//
//
//        for (int i = 0; i < mListContacts.size(); i++) {
//            mDataContact = mListContacts.get(i);
//            ContentValues mContentValues = new ContentValues();
//
//            mContentValues.put(ConstantDB.FriendId, mDataContact.getFriendId());
//
//            if (ValidationUtils.containsXmppDomainName(mDataContact.getChatId())) {
//
//                mContentValues.put(ConstantDB.ChatId, mDataContact.getChatId());
//            } else {
//                mContentValues.put(ConstantDB.ChatId, mDataContact.getChatId() + ServiceXmppConnection.SERVICE_NAME);
//            }
//
////            mContentValues.put(ConstantDB.PhoneNumber, mDataContact.getCountryCode().replace("+", "") + mDataContact.getPhoneNumber());
//            if (!TextUtils.isEmpty(mDataContact.getName())) {
//                mContentValues.put(ConstantDB.AppName, mDataContact.getName());
//            }
//            mContentValues.put(ConstantDB.Status, CommonMethods.getUTFDecodedString(mDataContact.getStatus()));
//            mContentValues.put(ConstantDB.AppFriendImageLink, mDataContact.getAppFriendImageLink());
//            mContentValues.put(ConstantDB.IsBlocked, mDataContact.getIsBlocked());
//            mContentValues.put(ConstantDB.IsFavorite, mDataContact.getIsFavorite());
//            mContentValues.put(ConstantDB.IsRegistered, mDataContact.getIsRegistered());
//            mContentValues.put(ConstantDB.isSynced, mDataContact.getIsSynced());
//            mContentValues.put(ConstantDB.isFriend, mDataContact.getIsFriend());
//            mContentValues.put(ConstantDB.IsDeleted, mDataContact.getIsDeleted());
//            mContentValues.put(ConstantDB.gender, mDataContact.getGender());
//            mContentValues.put(ConstantDB.isfindbyphoneno, mDataContact.getIsfindbyphoneno());
//            try {
////                if (checkExistance(mDataContact.getPhoneNumber())) {
////                    condition = ConstantDB.PhoneNumber + " = " + mDataContact.getPhoneNumber();
////                } else {
//                condition = ConstantDB.PhoneNumber + " = ?";
////                }
//
//                String[] args = new String[]{mDataContact.getCountryCode().replace("+", "") + mDataContact.getPhoneNumber()};
//
//                LogUtils.i(LOG_TAG, "contct update" + args[0] + " : " + condition);
//
//                k = app.mDbHelper.getDB().update(ConstantDB.TableContactList, mContentValues, condition, args);
//                count = count + k;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return (count > 0) ? true : false;
//    }

//    public void updateDeletedStatus(ArrayList<DataContact> mListContactPhone) {
//
//        String inIds = getCommaSeperatedString(mListContactPhone);
//        String sql;
//        sql = "DELETE FROM " + ConstantDB.TableContactList + " WHERE " + ConstantDB.isFriend
//                + " = 1 AND " + ConstantDB.PhoneNumber
//                + " NOT IN (?) ;";
//
//        try {
//            LogUtils.d("TableContact","updateDeletedStatus: started:sql:"+sql);
//            app.mDbHelper.getDB().beginTransactionNonExclusive();
//            SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);
//
//            stmt.clearBindings();
//            stmt.bindString(1, inIds);
//            stmt.execute();
//            app.mDbHelper.getDB().setTransactionSuccessful();
//            app.mDbHelper.getDB().endTransaction();
//
//            LogUtils.d("TableContact","updateDeletedStatus: Success");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void updateDeletedStatus(ArrayList<DataContact> mListContactPhone) {

        try {
            String inIds = getCommaSeperatedString(mListContactPhone);
            String sql;
            sql = "DELETE FROM " + ConstantDB.TableContactList + " WHERE " + ConstantDB.isFriend
                    + " = 1 AND " + ConstantDB.PhoneNumber
                    + " NOT IN (" + inIds + ") ;";
            LogUtils.i(LOG_TAG, "updateUser: Delete first " + sql);
            Cursor cursor = app.mDbHelper.getDB().rawQuery(sql, null);

            if (cursor != null) {
                cursor.moveToFirst();
                LogUtils.i(LOG_TAG, "updateUser: Deleted " + cursor.getCount());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int updateRegisteredStatusForFriendsWhoAreNotRegistered(String currentFriendNumbersWithoutCountryCode) {

        int updateCount = 0;
        try {
            String sql;
            sql = "UPDATE " + ConstantDB.TableContactList + " SET " + ConstantDB.IsRegistered+" = 0 WHERE " + ConstantDB.PhoneNumber
                    + " NOT IN (" + currentFriendNumbersWithoutCountryCode + ") ;";
            LogUtils.i(LOG_TAG, "updateRegisteredStatusForFriendsWhoAreNotRegistered " + sql);
            Cursor cursor = app.mDbHelper.getDB().rawQuery(sql, null);

            if (cursor != null) {
                cursor.moveToFirst();
                updateCount = cursor.getCount();
                LogUtils.i(LOG_TAG, "updateRegisteredStatusForFriendsWhoAreNotRegistered: count " + updateCount);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return updateCount;
    }

    public ArrayList<DataContact> getList(String searchTxt) {
        ArrayList<DataContact> mListContact = new ArrayList<DataContact>();
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList + " WHERE "
                + ConstantDB.IsRegistered + " = 0  AND (" + ConstantDB.PhoneNumber
                + "  LIKE '%" + searchTxt + "%'" +
                " OR " + ConstantDB.Name + "  LIKE '%" + searchTxt
                + "%') ORDER BY " + ConstantDB.Name + " COLLATE NOCASE ASC, " + ConstantDB.AppName
                + " COLLATE NOCASE ASC ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            DataContact mDataContact;
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    mDataContact = new DataContact();
                    mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                    mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                    mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataContact.setIsRegistered(c.getInt(c.getColumnIndex(ConstantDB.IsRegistered)));
                    mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                    mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                    mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                    mDataContact.setIsDeleted(c.getString(c.getColumnIndex(ConstantDB.IsDeleted)));
                    mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                    mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                    mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                    mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                    mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                    mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                    mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                    mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                    mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                    mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));
                    mListContact.add(mDataContact);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mListContact;
    }

    public ArrayList<DataContact> getAllList() {
        ArrayList<DataContact> mListContact = new ArrayList<DataContact>();
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList + " where "
                + ConstantDB.isFriend + " = 1 ORDER BY " /*+ ConstantDB.Name + " COLLATE NOCASE ASC, "*/
                + ConstantDB.AppName + " COLLATE NOCASE ASC ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            DataContact mDataContact;
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    mDataContact = new DataContact();
                    mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                    mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                    mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataContact.setIsRegistered(c.getInt(c.getColumnIndex(ConstantDB.IsRegistered)));
                    mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                    mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                    mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                    mDataContact.setIsDeleted(c.getString(c.getColumnIndex(ConstantDB.IsDeleted)));
                    mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                    mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                    mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                    mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                    mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                    mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                    mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                    mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                    mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                    mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));
                    mListContact.add(mDataContact);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mListContact;
    }


    public ArrayList<DataContactForGroup> getRegisteredUserForGroup(String searchTxt, String groupId) {
        ArrayList<DataContactForGroup> mListContact = new ArrayList<DataContactForGroup>();
        DataProfile mDataProfile = new TableUserInfo(mContext).getUser();
        TableGroup tableGroup = new TableGroup(mContext);
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList + " WHERE "
                + ConstantDB.IsRegistered + " = 1 AND " + ConstantDB.isFriend + " = 1 AND ("
                + ConstantDB.PhoneNumber + "  LIKE '%" + searchTxt + "%'" +
                " OR " + ConstantDB.Name + "  LIKE '%" + searchTxt + "%') ORDER BY "
                /*+ ConstantDB.Name + " COLLATE NOCASE ASC, " */ + ConstantDB.AppName + " COLLATE NOCASE ASC ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            DataContactForGroup mDataContact;
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    mDataContact = new DataContactForGroup();
                    mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                    mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                    mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataContact.setIsRegistered(c.getInt(c.getColumnIndex(ConstantDB.IsRegistered)));
                    mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                    mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                    mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                    mDataContact.setIsDeleted(c.getString(c.getColumnIndex(ConstantDB.IsDeleted)));
                    mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                    mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                    mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                    mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                    mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                    mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                    mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                    mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                    mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                    mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));


                    if (!TextUtils.isEmpty(groupId) && tableGroup.checkExistance(mDataContact.getFriendId(), groupId)) {
                        mDataContact.setIsSelectedForGroup(1);
                    } else {
                        mDataContact.setIsSelectedForGroup(0);
                    }
                    if (mDataProfile != null && mDataProfile.getPhoneNo().contains(mDataContact.getPhoneNumber())) {

                    } else {

                        mListContact.add(mDataContact);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mListContact;
    }


    //add people in group selection--setting value 1
    public boolean setSelectedForGroup(DataContact mDataContact) {
        int count = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.isSelectedForGroup, 1);
        count = app.mDbHelper.getDB().update(ConstantDB.TableContactList, mContentValues, ConstantDB.PhoneNumber + "=?", new String[]{mDataContact.getPhoneNumber()});
        return (count > 0) ? true : false;
    }

    //add people in group selection--setting value 0
    public boolean removeSelectionForGroup() {
        int count = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.isSelectedForGroup, 0);
        count = app.mDbHelper.getDB().update(ConstantDB.TableContactList, mContentValues, ConstantDB.isSelectedForGroup + "=?", new String[]{"1"});
        return (count > 0) ? true : false;
    }


    public ArrayList<DataContact> getRegisteredUser() {
        DataProfile mDataProfile = new TableUserInfo(mContext).getUser();
        ArrayList<DataContact> mListContact = new ArrayList<DataContact>();
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList + " WHERE " +
                ConstantDB.IsRegistered + " = 1 AND "
                + ConstantDB.isFriend + " = 1 ORDER BY "
                /*+ ConstantDB.Name + " COLLATE NOCASE ASC, "*/ + ConstantDB.AppName + " COLLATE NOCASE ASC ;";
        LogUtils.d(LOG_TAG,"getRegisteredUser() call starts");
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            DataContact mDataContact;
            if (c != null && c.getCount() > 0) {
                LogUtils.d(LOG_TAG,"getRegisteredUser() c != null && c.getCount() > 0");
                c.moveToFirst();
                do {
                    mDataContact = new DataContact();
                    mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                    mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                    mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataContact.setIsRegistered(c.getInt(c.getColumnIndex(ConstantDB.IsRegistered)));
                    mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                    mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                    mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                    mDataContact.setIsDeleted(c.getString(c.getColumnIndex(ConstantDB.IsDeleted)));
                    mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                    mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                    mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                    mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                    mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                    mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                    mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                    mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                    mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                    mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));
                    if (mDataProfile != null && mDataProfile.getPhoneNo().contains(mDataContact.getPhoneNumber())) {

                    } else {
                        mListContact.add(mDataContact);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }

        LogUtils.d(LOG_TAG,"getRegisteredUser() mListContact:"+(mListContact!=null?mListContact.size():"null"));
        return mListContact;
    }

    public String getNumbers() {
        String numbers = "";
        String SQL = "SELECT GROUP_CONCAT(" + ConstantDB.PhoneNumber + " , ',') FROM "
                + ConstantDB.TableContactList + " WHERE " + ConstantDB.isFriend + "= 1;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                numbers = c.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return numbers;
    }


    public boolean updateBlock(DataContact mDataContact) {
        int count = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.IsBlocked, mDataContact.getIsBlocked());
        mContentValues.put(ConstantDB.IsFavorite, mDataContact.getIsFavorite());
        mContentValues.put(ConstantDB.IsDeleted, mDataContact.getIsDeleted());
        count = app.mDbHelper.getDB().update(ConstantDB.TableContactList, mContentValues, ConstantDB.PhoneNumber + "=?", new String[]{mDataContact.getPhoneNumber()});
        return (count > 0) ? true : false;
    }

    public boolean updateBlockFromChat(DataTextChat mDataTextChat) {
        int count = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.IsBlocked, mDataTextChat.getIsBlocked());
        mContentValues.put(ConstantDB.IsFavorite, mDataTextChat.getIsFavorite());
        mContentValues.put(ConstantDB.IsDeleted, mDataTextChat.getIsDeleted());
        count = app.mDbHelper.getDB().update(ConstantDB.TableContactList, mContentValues, ConstantDB.PhoneNumber + "=?", new String[]{mDataTextChat.getFriendPhoneNo()});
        return (count > 0) ? true : false;
    }


    public ArrayList<DataContact> getBlockUser() {
        ArrayList<DataContact> mListContact = new ArrayList<DataContact>();
        DataProfile mDataProfile = new TableUserInfo(mContext).getUser();
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList
                + " WHERE " /*+ ConstantDB.IsRegistered + " = 1 AND "*/
                + ConstantDB.IsBlocked + " = 1 ORDER BY "
               /* + ConstantDB.Name + " COLLATE NOCASE ASC, " */ + ConstantDB.AppName + " COLLATE NOCASE ASC ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            DataContact mDataContact;
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    mDataContact = new DataContact();
                    mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                    mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                    mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                    mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                    mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                    mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                    mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                    mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                    mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                    mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                    mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                    mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                    mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                    mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                    mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));
                    if (mDataProfile != null && mDataProfile.getPhoneNo().contains(mDataContact.getPhoneNumber())) {

                    } else {
                        mListContact.add(mDataContact);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mListContact;
    }

    public ArrayList<DataContact> getFavoriteUser() {
        ArrayList<DataContact> mListContact = new ArrayList<DataContact>();
        DataProfile mDataProfile = new TableUserInfo(mContext).getUser();
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList
                + " WHERE " + ConstantDB.IsRegistered + " = 1 AND " + ConstantDB.IsFavorite
                + " = 1 ORDER BY "
                /*+ ConstantDB.Name + " COLLATE NOCASE ASC, " */ + ConstantDB.AppName + " COLLATE NOCASE ASC ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            DataContact mDataContact;
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    mDataContact = new DataContact();
                    mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                    mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                    mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                    mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                    mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                    mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                    mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                    mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                    mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                    mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                    mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                    mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                    mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                    mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                    mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));
//                    mDataContact.setCountryCode(c.getString(c.getColumnIndex(ConstantDB.CountryCode)));
                    if (mDataProfile != null && mDataProfile.getPhoneNo().contains(mDataContact.getPhoneNumber())) {

                    } else {
                        mListContact.add(mDataContact);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mListContact;
    }

    public boolean checkExistance(String phoneNumber) {
        boolean status = false;
        String SQL = "SELECT " + ConstantDB.PhoneNumber + " FROM "
                + ConstantDB.TableContactList + " WHERE "
                + ConstantDB.PhoneNumber + " LIKE '%" + phoneNumber + "%'";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.moveToFirst()) {
                if (c.getCount() > 0) {
                    status = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return status;
    }

    public boolean checkExistanceByUserId(String userId) {
        boolean status = false;
        String SQL = "SELECT " + ConstantDB.PhoneNumber + " FROM "
                + ConstantDB.TableContactList + " WHERE " + ConstantDB.FriendId + " ='" + userId + "'  ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.moveToFirst()) {
                if (c.getCount() > 0) {
                    status = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return status;
    }

    public DataContact getFriendDetailsByID(String friendId) {
        DataContact mDataContact = new DataContact();
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList + " WHERE "
                + ConstantDB.FriendId + " ='" + friendId + "'  ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));
                mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mDataContact;
    }

    public DataContact getFriendDetailsByPhoneNumber(String PhoneNumber) {
        DataContact mDataContact = null;
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList + " WHERE "
                + ConstantDB.PhoneNumber + " ='" + PhoneNumber + "'  ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                mDataContact = new DataContact();
                mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mDataContact;
    }

    public DataContact getFriendDetailsByFrienChatID(String friendChatId) {
        DataContact mDataContact = null;
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList + " WHERE "
                + ConstantDB.ChatId + " ='" + friendChatId + "'  ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                mDataContact = new DataContact();
                mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mDataContact;
    }


    public ArrayList<DataContact> getRegisteredFindMeUser(String searchTxt, String gender) {
        ArrayList<DataContact> mListContact = new ArrayList<DataContact>();
        DataProfile mDataProfile = new TableUserInfo(mContext).getUser();
        String genderSelection = TextUtils.isEmpty(gender) ? "" : " AND " + ConstantDB.gender + " = '" + gender + "'";
        String searchTextSelection = TextUtils.isEmpty(searchTxt)?"":(" AND ("+ConstantDB.PhoneNumber + "  LIKE '%" + searchTxt + "%'" +
                " OR " + ConstantDB.Name + "  LIKE '%" + searchTxt + "%'" +
                " OR " + ConstantDB.AppName + "  LIKE '%" + searchTxt + "%')");
        String SQL = "SELECT * FROM " + ConstantDB.TableContactList + " WHERE "
                + ConstantDB.IsRegistered + " = 1 AND "
                + ConstantDB.isFriend + " = 1 "
                + genderSelection +
                searchTextSelection +
                " ORDER BY " /*+ ConstantDB.Name + " COLLATE NOCASE ASC, "*/ + ConstantDB.AppName + " COLLATE NOCASE ASC ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            DataContactForGroup mDataContact;
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    mDataContact = new DataContactForGroup();
                    mDataContact.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                    mDataContact.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                    mDataContact.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataContact.setIsRegistered(c.getInt(c.getColumnIndex(ConstantDB.IsRegistered)));
                    mDataContact.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                    mDataContact.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                    mDataContact.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                    mDataContact.setIsDeleted(c.getString(c.getColumnIndex(ConstantDB.IsDeleted)));
                    mDataContact.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                    mDataContact.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                    mDataContact.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                    mDataContact.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                    mDataContact.setIsSynced(c.getInt(c.getColumnIndex(ConstantDB.isSynced)));
                    mDataContact.setIsFriend(c.getInt(c.getColumnIndex(ConstantDB.isFriend)));
                    mDataContact.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                    mDataContact.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                    mDataContact.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                    mDataContact.setRelation(c.getString(c.getColumnIndex(ConstantDB.relation)));
                    if (mDataProfile != null && mDataProfile.getPhoneNo().contains(mDataContact.getPhoneNumber())) {

                    } else {

                        mListContact.add(mDataContact);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mListContact;
    }


    private String getCommaSeperatedString(ArrayList<DataContact> mListContacts) {
        String ids = "";
        for (DataContact dc : mListContacts) {
            if (!TextUtils.isEmpty(dc.getPhoneNumber())) {
                if (TextUtils.isEmpty(ids)) {
                    ids = ids + "'" + dc.getCountryCode().replace("+", "") + dc.getPhoneNumber() + "'";
                } else {
                    ids = ids + "," + dc.getCountryCode().replace("+", "") + dc.getPhoneNumber();
                }
            }
        }
        return ids;
    }

    public void delete() {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableContactList, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFriendProfile(ContentValues mContentValues, DataContact mDataContact) {
        try {
//                if (checkExistance(mDataContact.getPhoneNumber())) {
//                    condition = ConstantDB.PhoneNumber + " = " + mDataContact.getPhoneNumber();
//                } else {
            String condition = ConstantDB.PhoneNumber + " = ?";
//                }

            String[] args = new String[]{mDataContact.getCountryCode().replace("+", "")
                    + mDataContact.getPhoneNumber()};

            LogUtils.i(LOG_TAG, "contact update" + args[0] + " : " + condition);

            app.mDbHelper.getDB().update(ConstantDB.TableContactList, mContentValues, condition, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
