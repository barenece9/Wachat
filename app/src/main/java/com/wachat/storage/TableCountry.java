package com.wachat.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.wachat.application.AppVhortex;
import com.wachat.data.DataCountry;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public class TableCountry {

    private Context mContext;
    private AppVhortex app;

    public TableCountry(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();

    }

    public synchronized boolean insertCategory(ArrayList<DataCountry> arrDataCategories) {

        boolean isSuccess = false;
        String sql = "INSERT OR REPLACE INTO " + ConstantDB.TableCountry + " ( " + ConstantDB.countryCode + "," + ConstantDB.countryName + " ) " +
                "VALUES (?, ?)";
            try {
                app.mDbHelper.getDB().beginTransaction();
                SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);

                DataCountry mDataCountry;

                for (int i = 0; i < arrDataCategories.size(); i++) {
                    mDataCountry = arrDataCategories.get(i);

                    if (mDataCountry.getCountryCode() != null && !mDataCountry.getCountryCode().equals(""))
                        stmt.bindString(1, mDataCountry.getCountryCode());
                    else
                        stmt.bindString(1, "");

                    if (mDataCountry.getCountryName() != null && !mDataCountry.getCountryName().equals(""))
                        stmt.bindString(2, mDataCountry.getCountryName());
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

    public synchronized ArrayList<DataCountry> getList(String countryName) {
        ArrayList<DataCountry> mListCountry = new ArrayList<DataCountry>();
        String SQL = "SELECT * FROM " + ConstantDB.TableCountry + " WHERE "+ConstantDB.countryName+" LIKE '%"+ countryName + "%' ORDER BY "+ConstantDB.countryName+" ASC ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                DataCountry mDataCountry;
                do {
                    mDataCountry = new DataCountry();
                    mDataCountry.setCountryCode(c.getString(c.getColumnIndex(ConstantDB.countryCode)));
                    mDataCountry.setCountryName(c.getString(c.getColumnIndex(ConstantDB.countryName)));
                    mListCountry.add(mDataCountry);
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
        return mListCountry;
    }

    public synchronized DataCountry getCountryCode(String country) {

        DataCountry mDataCountry = null;

        String SQL = "SELECT * FROM " + ConstantDB.TableCountry + " WHERE " + ConstantDB.countryCode + " = '" + country + "';";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                mDataCountry = new DataCountry();
                do {
                    mDataCountry.setCountryCode(c.getString(c.getColumnIndex(ConstantDB.countryCode)));
                    mDataCountry.setCountryName(c.getString(c.getColumnIndex(ConstantDB.countryName)));

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
        return mDataCountry;
    }
}
