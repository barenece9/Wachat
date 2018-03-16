package com.wachat.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.wachat.application.AppVhortex;
import com.wachat.data.DataLanguages;

import java.util.ArrayList;

/**
 * Created by Argha on 07-11-2015.
 */
public class TableLanguage {

    private Context mContext;
    private AppVhortex app;


    public TableLanguage(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();
    }


    public synchronized boolean insertAllLang(ArrayList<DataLanguages> languagesArrayList) {

        boolean isSuccess = false;
        String sql = "INSERT OR REPLACE INTO " + ConstantDB.TableLanguage + " ( " + ConstantDB.languageCode + "," + ConstantDB.LanguageName + " ) " +
                "VALUES (?, ?)";
        try {
            app.mDbHelper.getDB().beginTransaction();
            SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);

            DataLanguages mDataLanguages;

            for (int i = 0; i < languagesArrayList.size(); i++) {
                mDataLanguages = languagesArrayList.get(i);

                if (mDataLanguages.getLanguage() != null && !mDataLanguages.getLanguage().equals(""))
                    stmt.bindString(1, mDataLanguages.getLanguage());
                else
                    stmt.bindString(1, "");

                if (mDataLanguages.getName() != null && !mDataLanguages.getName().equals(""))
                    stmt.bindString(2, mDataLanguages.getName());
                else
                    stmt.bindString(2, "");

                stmt.execute();

            }
            stmt.clearBindings();
            app.mDbHelper.getDB().setTransactionSuccessful();
            app.mDbHelper.getDB().endTransaction();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }
        return isSuccess;
    }

    public synchronized ArrayList<DataLanguages> getLangList(String langName) {
        ArrayList<DataLanguages> mListLang = new ArrayList<DataLanguages>();
        String SQL = "SELECT * FROM " + ConstantDB.TableLanguage + " WHERE "+ConstantDB.LanguageName+" LIKE '%"+ langName + "%' ORDER BY "+ConstantDB.LanguageName+" ASC ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                DataLanguages mDataLanguages;
                do {
                    mDataLanguages = new DataLanguages();
                    mDataLanguages.setLanguage(c.getString(c.getColumnIndex(ConstantDB.languageCode)));
                    mDataLanguages.setName(c.getString(c.getColumnIndex(ConstantDB.LanguageName)));
                    mListLang.add(mDataLanguages);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return mListLang;
    }

    public synchronized DataLanguages getParticularLang(String key , String value) {

        DataLanguages mDataLanguages = new DataLanguages();
        String SQL = "SELECT * FROM " + ConstantDB.TableLanguage + " WHERE "+key+" = '"+ value + "' ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {

                    mDataLanguages.setLanguage(c.getString(c.getColumnIndex(ConstantDB.languageCode)));
                    mDataLanguages.setName(c.getString(c.getColumnIndex(ConstantDB.LanguageName)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return mDataLanguages;
    }
}
