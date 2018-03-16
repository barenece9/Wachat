package com.wachat.storage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;

import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DBHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(DBHelper.class);

    private Context context;

    private final static int DB_Version_OLD = 5;
    private static final int DB_VERSION_2_FOR_PLAYSTORE_1_2 = 6;
    private static final int DB_VERSION_FOR_PLAYSTORE_1_3 = 8;
    private static final int DB_VERSION_FOR_PLAYSTORE_1_4 = 9;
    private static final int CURRENT_DB_VERSION = DB_VERSION_FOR_PLAYSTORE_1_4;
    private String DB_PATH = "";
    private SQLiteDatabase dB;

    public DBHelper(Context context) {
        super(context, ConstantDB.DB_NAME, null, CURRENT_DB_VERSION);
        this.context = context;
        DB_PATH = context.getDatabasePath(ConstantDB.DB_NAME).getPath();
        this.context.openOrCreateDatabase(ConstantDB.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY
                | SQLiteDatabase.OPEN_READWRITE
                | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING, null);
    }

    public SQLiteDatabase getDB() {
        return dB;
    }

    public synchronized void openDataBase() throws SQLException {
//        dB = SQLiteDatabase.openDatabase(DB_PATH, null,
//                SQLiteDatabase.CREATE_IF_NECESSARY
//                        |SQLiteDatabase.OPEN_READWRITE
//                        |SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);
        dB = this.getWritableDatabase();
//        dB.execSQL("PRAGMA read_uncommitted = true;");
//        dB.execSQL("PRAGMA synchronous=OFF;");
//        dB.enableWriteAheadLogging();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            // CREATE TABLE Country
            db.execSQL("CREATE TABLE " + ConstantDB.TableCountry
                    + " (" + ConstantDB.countryCode + " VARCHAR , "
                    + ConstantDB.countryName + " VARCHAR  PRIMARY KEY NOT NULL)");
            db.execSQL("CREATE TABLE " + ConstantDB.TableChat + " ("
                    + ConstantDB.messageID + " VARCHAR PRIMARY KEY NOT NULL , "
                    + ConstantDB.messageBody + " VARCHAR , "
                    + ConstantDB.senderChatID + " VARCHAR , "
                    + ConstantDB.chatType + " VARCHAR , "
                    + ConstantDB.messageType + "  VARCHAR , "
                    + ConstantDB.strGroupID + " VARCHAR, "
                    + ConstantDB.messageDateTime + " VARCHAR, "
                    + ConstantDB.deliveryStatus + " INTEGER, "
                    + ConstantDB.strAttachmentID + " VARCHAR, "
                    + ConstantDB.strTranslatedText + " VARCHAR, "
                    + ConstantDB.friendPhoneNo + " VARCHAR ,"
                    + ConstantDB.friendChatID + " VARCHAR ,"
                    + ConstantDB.userID + " VARCHAR , "
                    + ConstantDB.attachContactName + " VARCHAR , "
                    + ConstantDB.attachContactNo + " VARCHAR ,"
                    + ConstantDB.attachBase64str4Img + " LONGTEXT ,"
                    + ConstantDB.loc_lat + " VARCHAR , "
                    + ConstantDB.loc_long + " VARCHAR , "
                    + ConstantDB.loc_address + " VARCHAR ,"
                    + ConstantDB.fileLocalPath + " VARCHAR ,"
                    + ConstantDB.maskImagePath + " VARCHAR ,"
                    + ConstantDB.fileUrl + " VARCHAR ,"
                    + ConstantDB.thumbBase64 + " VARCHAR ,"
                    + ConstantDB.thumbFilePath + " VARCHAR ,"
                    + ConstantDB.isMasked + " VARCHAR ,"
                    + ConstantDB.maskEnabled + " VARCHAR ,"
                    + ConstantDB.downloadStatus + " VARCHAR ,"
                    + ConstantDB.uploadStatus + " VARCHAR ,"
                    + ConstantDB.youtubeTitle + " VARCHAR ,"
                    + ConstantDB.youtubeDesc + " VARCHAR ,"
                    + ConstantDB.youtubePublishTime + " VARCHAR ,"
                    + ConstantDB.youtubeThumbUrl + " VARCHAR ,"
                    + ConstantDB.youtubeVidId + " VARCHAR ,"
                    + ConstantDB.yahooTitle + " VARCHAR ,"
                    + ConstantDB.yahooDesc + " VARCHAR ,"
                    + ConstantDB.yahooImageUrl + " VARCHAR ,"
                    + ConstantDB.yahooPublishTime + " VARCHAR ,"
                    + ConstantDB.yahooShareLink + " VARCHAR ,"
                    + ConstantDB.friend_name + " VARCHAR ,"
                    + ConstantDB.sender_id + " VARCHAR ,"
                    + ConstantDB.sender_name + " VARCHAR,"
                    + ConstantDB.FriendId + " VARCHAR ,"
                    + " UNIQUE (" + ConstantDB.messageID + ") ON CONFLICT REPLACE);");

            db.execSQL("CREATE TABLE " + ConstantDB.TableAttachmentDetails
                    + " (" + ConstantDB.attachmentID + " VARCHAR,"
                    + ConstantDB.httpURL + " VARCHAR,"
                    + ConstantDB.filePath
                    + " VARCHAR DEFAULT (null) ,"
                    + ConstantDB.fileType + " VARCHAR, "
                    + ConstantDB.thumbnailName + " VARCHAR, "
                    + ConstantDB.friendOrGrpID + " VARCHAR, "
                    + ConstantDB.assetURL + " VARCHAR);");

            db.execSQL("CREATE TABLE " + ConstantDB.TableChatLocation
                    + " (" + ConstantDB.strID + " VARCHAR, "
                    + ConstantDB.lat + " FLOAT, " + ConstantDB.lng + " FLOAT);");

            db.execSQL("CREATE TABLE " + ConstantDB.TableContactList
                    + " ("
                    + ConstantDB.PhoneNumber + " VARCHAR PRIMARY KEY NOT NULL , "
                    + ConstantDB.CountryCode + " VARCHAR , "
                    + ConstantDB.Name + " VARCHAR , "
                    + ConstantDB.FriendId + " VARCHAR , "
                    + ConstantDB.IsRegistered + " VARCHAR , "
                    + ConstantDB.FriendImageLink + " VARCHAR , "
                    + ConstantDB.ChatId + " VARCHAR , "
                    + ConstantDB.Status + " VARCHAR, "
                    + ConstantDB.IsDeleted + " VARCHAR, "
                    + ConstantDB.relation + " VARCHAR, "
                    + ConstantDB.AppName + " VARCHAR, "
                    + ConstantDB.AppFriendImageLink + " VARCHAR, "
                    + ConstantDB.imageUrl + " VARCHAR, "
                    + ConstantDB.isfindbyphoneno + " VARCHAR, "
                    + ConstantDB.gender + " VARCHAR, "
                    + ConstantDB.IsBlocked + " INTEGER DEFAULT (0) ,"
                    + ConstantDB.IsFavorite + " INTEGER DEFAULT (0) , "
                    + ConstantDB.isSynced + " INTEGER, "
                    + ConstantDB.isFriend + " INTEGER DEFAULT (1) , "
                    + ConstantDB.isSelectedForGroup + " INTEGER DEFAULT (0),"
                    + " UNIQUE (" + ConstantDB.PhoneNumber + ") ON CONFLICT REPLACE);");

            db.execSQL("CREATE TABLE " + ConstantDB.TableFriendAvailableStatus
                    + " (" + ConstantDB.FriendChatId + " VARCHAR PRIMARY KEY  NOT NULL ,"
                    + ConstantDB.AvailableStatus + "VARCHAR, "
                    + ConstantDB.InvisibleStatus + " INTEGER DEFAULT 0);");

            db.execSQL("CREATE TABLE "
                    + ConstantDB.TableGroupDetails
                    + " ("
                    + ConstantDB.GroupId + " VARCHAR PRIMARY KEY  NOT NULL ,"
                    + ConstantDB.GroupName + " TEXT, "
                    + ConstantDB.GroupJId + " TEXT, "
                    + ConstantDB.GroupImage + " TEXT, "
                    + ConstantDB.CreationDateTime + " VARCHAR, "
                    + ConstantDB.OwnerId + " VARCHAR);");

            db.execSQL("CREATE TABLE "
                    + ConstantDB.TableGroupMember
                    + " ("
                    + ConstantDB.GroupId + " VARCHAR, "
                    + ConstantDB.MemberName + " TEXT, "
                    + ConstantDB.MemberId + " VARCHAR, "
                    + ConstantDB.MemberProfilePic + " TEXT, "
                    + ConstantDB.ChatId + " VARCHAR, "
                    + ConstantDB.MemberPhoneNo + " TEXT, "
                    + ConstantDB.MemberCountryId + " VARCHAR, "
                    + ConstantDB.IsOwner + " BOOL, "
                    + ConstantDB.IsGrpadmin + " BOOL, "
                    + ConstantDB.IsGrpblock + " BOOL, "
                    + ConstantDB.MemberStatus + " VARCHAR, PRIMARY KEY("
                    + ConstantDB.GroupId + "," + ConstantDB.MemberId + "));");

            db.execSQL("CREATE TABLE " + ConstantDB.TableUserInfo
                    + " (" + ConstantDB.UserId + " VARCHAR PRIMARY KEY  NOT NULL ,"
                    + ConstantDB.UserName + " VARCHAR," +
                    ConstantDB.ProfileImage + " VARCHAR," + ConstantDB.PhoneNo
                    + " VARCHAR," + ConstantDB.CountryCode + " VARCHAR,"
                    + ConstantDB.ChatId + " VARCHAR," + ConstantDB.Gender + " VARCHAR," +
                    ConstantDB.UserStatus + " VARCHAR," + ConstantDB.IsRegistered
                    + " INTEGER DEFAULT (0) ," + ConstantDB.IsVerified
                    + " INTEGER DEFAULT (0) ," +
                    ConstantDB.PairingValue + " INTEGER DEFAULT (0) ,"
                    + ConstantDB.VerificationCode + " VARCHAR, "
                    + ConstantDB.Language + " VARCHAR, " +
                    ConstantDB.LanguageIdentifire + " VARCHAR, "
                    + ConstantDB.CountryName + " VARCHAR, " + ConstantDB.IsTranslated
                    + " INTEGER DEFAULT 0, " +
                    ConstantDB.IsFindByLocation + " INTEGER DEFAULT 1, "
                    + ConstantDB.isNotificationOn + " INTEGER DEFAULT 1, "
                    + ConstantDB.IsFindByPhoneno + " INTEGER DEFAULT 1 );");

            db.execSQL("CREATE TABLE " + ConstantDB.UserStatus
                    + " (" + ConstantDB.statusId + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
                    +
                    ConstantDB.status + " VARCHAR, " + ConstantDB.selected + " BOOL);");

            db.execSQL("CREATE TABLE " + ConstantDB.TableContactSync
                    + " (" + ConstantDB.status + " VARCHAR PRIMARY KEY  NOT NULL, UNIQUE("
                    + ConstantDB.status + ") ON CONFLICT REPLACE);");

            db.execSQL("CREATE TABLE " + ConstantDB.TableTimeStamp + " ("
                    + ConstantDB.strTimeStamp + " VARCHAR );");

            db.execSQL("CREATE TABLE " + ConstantDB.TableLanguage + " ("
                    + ConstantDB.languageCode + " VARCHAR PRIMARY KEY, "
                    + ConstantDB.LanguageName + " VARCHAR);");


        } catch (SQLException e) {
            LogUtils.i(LOG_TAG, "Table Creation Exception");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.i(LOG_TAG, "onUpgrade:oldVersion: " + oldVersion + " newVersion:" + newVersion);
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (!TextUtils.isEmpty(versionName) && versionName.equals("1.1")) {
            //On this version we made a lot changes in login/registration and
            //user fields. So its required to clear all data and redirect user to
            //register again
            Prefs.getInstance(context).clearPreferences();
        }

        if (oldVersion < DB_VERSION_FOR_PLAYSTORE_1_3) {
            try {

                String sql = "ALTER TABLE " + ConstantDB.TableChat
                        + "  ADD COLUMN " + ConstantDB.maskImagePath + " VARCHAR; ";
                LogUtils.i(LOG_TAG, "onUpgrade:oldVersion < DB_VERSION_FOR_PLAYSTORE_1_3:sql: "+sql);
                db.execSQL(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                String sql = "ALTER TABLE " + ConstantDB.TableContactList
                        + "  ADD COLUMN " + ConstantDB.relation + " VARCHAR; ";
                LogUtils.i(LOG_TAG, "onUpgrade:oldVersion < DB_VERSION_FOR_PLAYSTORE_1_3:sql: "+sql);
                db.execSQL(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (oldVersion < DB_VERSION_FOR_PLAYSTORE_1_4) {
            try {

                String sql = "ALTER TABLE " + ConstantDB.TableChat
                        + "  ADD COLUMN " + ConstantDB.FriendId + " VARCHAR; ";
                LogUtils.i(LOG_TAG, "onUpgrade:oldVersion < DB_VERSION_FOR_PLAYSTORE_1_4:sql: "+sql);
                db.execSQL(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
//        }
    }


    // TODO IsOpen() Checking implemented
    @Override
    public synchronized void close() {

        if (getDB() != null && getDB().isOpen())
            getDB().close();

        super.close();

    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }


    public boolean copyFromDataPackgeToSdCard() throws Exception {
        File newDb = null;
        File newDbDir = null;
        String dirPath = "";

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

        }


        newDbDir = new File(dirPath);

        if (!newDbDir.exists()) {
            if (!newDbDir.mkdirs()) {
                // Log.d("LocaLoca", "failed to create directory");
                // throw new
                // FileNotFoundException("failed to create directory");
            }
        }

//        close();
        newDb = new File(newDbDir.getPath() + File.separator + "Copy" + ConstantDB.DB_NAME);
        File oldDb = new File(DB_PATH);
        if (oldDb.exists()) {
            copyFile(new FileInputStream(oldDb), new FileOutputStream(newDb));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
//            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    private static void copyFile(FileInputStream fromFile,
                                 FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }
}
