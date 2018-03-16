package com.wachat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.wachat.activity.ActivityChat;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.chatUtils.NotificationUtils;
import com.wachat.chatUtils.OneToOneChatJSonCreator;
import com.wachat.data.AdminActionResponse.GroupDetails;
import com.wachat.data.AdminActionResponse.Memberdetail;
import com.wachat.data.DataCreateOrEditGroup;
import com.wachat.data.DataGroup;
import com.wachat.data.DataGroupMember;
import com.wachat.data.DataGroupMembers;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gourav Kundu on 01-10-2015.
 */
public class TableGroup {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(TableGroup.class);
    private final TableChat tableChat;

    private Context mContext;
    private AppVhortex app;

    private Prefs mPrefs;

    public TableGroup(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();
        mPrefs = Prefs.getInstance(mContext);
        tableChat = new TableChat(mContext);
    }


    public boolean bulkInsertGroup(ArrayList<DataGroup> mListGroups) {
        boolean isSuccess = false;
        String sql = "INSERT OR REPLACE INTO " + ConstantDB.TableGroupDetails
                + " ( " + ConstantDB.GroupId + ","
                + ConstantDB.GroupJId + ","
                + ConstantDB.GroupName + ","
                + ConstantDB.GroupImage + ","
                + ConstantDB.CreationDateTime + ","
                + ConstantDB.OwnerId + " ) " +
                "VALUES (?, ?,?, ?, ?, ?)";
        app.mDbHelper.getDB().beginTransactionNonExclusive();
        SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);
        try {
            for (DataGroup mDataGroup : mListGroups) {
                if (mDataGroup.getGroupId() != null && mDataGroup.getGroupId().trim().length() > 0) {
                    stmt.bindString(1, mDataGroup.getGroupId());
                } else {
                    stmt.bindString(1, "");
                }

                if (mDataGroup.getGroupJId() != null && mDataGroup.getGroupJId().trim().length() > 0) {
                    stmt.bindString(2, mDataGroup.getGroupJId());
                } else {
                    stmt.bindString(2, "");
                }

                if (mDataGroup.getGroupName() != null && mDataGroup.getGroupName().trim().length() > 0) {
                    stmt.bindString(3, mDataGroup.getGroupName());
                } else {
                    stmt.bindString(3, "");
                }

                if (mDataGroup.getGroupImage() != null && mDataGroup.getGroupImage().trim().length() > 0) {
                    stmt.bindString(4, mDataGroup.getGroupImage());
                } else {
                    stmt.bindString(4, "");
                }

                if (mDataGroup.getCreationdateTime() != null && mDataGroup.getCreationdateTime().trim().length() > 0) {
                    stmt.bindString(5, mDataGroup.getCreationdateTime());
                } else {
                    stmt.bindString(5, "");
                }

                if (mDataGroup.getOwnerId() != null && mDataGroup.getOwnerId().trim().length() > 0) {
                    stmt.bindString(6, mDataGroup.getOwnerId());
                } else {
                    stmt.bindString(6, "");
                }

                /**First delete all group members before syncing with new/updated data*/
                deleteAllGroupMembers(mDataGroup.getGroupId());

                if (mDataGroup.getMemberdetails() != null && mDataGroup.getMemberdetails().size() > 0) {
                    bulkInsertMembers(mDataGroup.getMemberdetails(), mDataGroup.getGroupId());

                    insertGroupActionNotificationChat(mDataGroup);
                }

                stmt.execute();
            }
            stmt.clearBindings();
            app.mDbHelper.getDB().setTransactionSuccessful();
            app.mDbHelper.getDB().endTransaction();
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    private void insertGroupActionNotificationChat(DataGroup mDataGroup) {
        if (mDataGroup != null && mDataGroup.getMemberdetails() != null) {

            for (DataGroupMembers member : mDataGroup.getMemberdetailsdelete()) {
                if (member.type.equals("remove")) {
                    if (member.getUserId().equals(mPrefs.getUserId())) {
                        insertGroupNotificationMessageToChatTable(mDataGroup, member,
                                ConstantChat.NOTIFICATION_TYPE_REMOVED, "You have been removed from this group");
                    } else {
                        insertGroupNotificationMessageToChatTable(mDataGroup, member,
                                ConstantChat.NOTIFICATION_TYPE_REMOVED, member.getUserName() + " has been removed from this group");
                    }
                } else if (member.type.equals("leave")) {
                    if (member.getUserId().equals(mPrefs.getUserId())) {
                        insertGroupNotificationMessageToChatTable(mDataGroup, member,
                                ConstantChat.NOTIFICATION_TYPE_LEFT, "You have left this group");
                    } else {
                        insertGroupNotificationMessageToChatTable(mDataGroup, member,
                                ConstantChat.NOTIFICATION_TYPE_LEFT, member.getUserName() + " has left this group");
                    }
                }
            }


            for (DataGroupMembers member : mDataGroup.getMemberdetails()) {
                if (!mDataGroup.getCreationdateTime().equals(member.addedTime)) {
                    if (member.getUserId().equals(mPrefs.getUserId())) {
                        insertGroupNotificationMessageToChatTable(mDataGroup, member,
                                ConstantChat.NOTIFICATION_TYPE_ADDED, "You have been added to this group");
                    } else {
                        insertGroupNotificationMessageToChatTable(mDataGroup, member,
                                ConstantChat.NOTIFICATION_TYPE_ADDED, member.getUserName() + " has been added to this group");
                    }
                } else {
                    if (member.getUserId().equals(mPrefs.getUserId())) {
                        if (member.isOwner.equals("1")) {
                            insertGroupNotificationMessageToChatTable(mDataGroup, member,
                                    ConstantChat.NOTIFICATION_TYPE_CREATED, "You have created this group");
                        } else {
                            insertGroupNotificationMessageToChatTable(mDataGroup, member,
                                    ConstantChat.NOTIFICATION_TYPE_ADDED, "You have been added to this group");
                        }
                    } else {
                        if (member.isOwner.equals("1")) {
                            insertGroupNotificationMessageToChatTable(mDataGroup, member,
                                    ConstantChat.NOTIFICATION_TYPE_CREATED, member.getUserName() + " has created this group");
                        }
                    }
                }

            }


        }
    }

    private void insertGroupNotificationMessageToChatTable(DataGroup mDataGroup,
                                                           DataGroupMembers member,
                                                           String notificationType, String messageBody) {

        DataTextChat chat = OneToOneChatJSonCreator.getGroupNotificationMessage(mDataGroup, member, notificationType, messageBody);


        if (chat != null) {
            if(!tableChat.checkIfMessageExists(chat.getMessageid())) {
                chat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);
                tableChat.insertChat(chat);

                try {
                    if(AppVhortex.getInstance().mBaseActivity!=null){
                        BaseActivity activity = (BaseActivity) AppVhortex.getInstance().
                                mBaseActivity.get();
                        if(activity!=null){
                            activity.onMessageReceiveFromGroupSync(chat);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void addNotification(DataTextChat mDataTextChat) {

        /*add intent to notificaition*/
        Intent notificationIntent = new Intent(AppVhortex.getInstance().getApplicationContext(), ActivityChat.class);

//        get data using friend id
//        DataContact mDataContact = new TableContact(getBaseContext()).getFriendDetailsByFrienChatID(mDataTextChat.getFriendChatID());
//        notificationIntent.putExtra(Constants.B_ID, mDataContact.getFriendId());
//        notificationIntent.putExtra(ConstantDB.ChatId, mDataContact.getChatId());
//        notificationIntent.putExtra(ConstantDB.PhoneNo, mDataContact.getFriendId());

//        if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
//            notificationIntent.putExtra(Constants.B_NAME, mDataTextChat.getFriendName());
//            notificationIntent.putExtra(Constants.B_ID, mDataContact.getFriendId());
//            notificationIntent.putExtra(Constants.B_RESULT, mDataTextChat.getFriendChatID());
//            notificationIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_SINGLECHAT);
//            notificationIntent.putExtra(ConstantDB.ChatId, mDataTextChat.getFriendChatID());
//            notificationIntent.putExtra(ConstantDB.PhoneNo, mDataTextChat.getFriendPhoneNo());
//            notificationIntent.putExtra(ConstantDB.FriendImageLink, mDataContact.getAppFriendImageLink());
//        } else if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            notificationIntent.putExtra(Constants.B_ID, mDataTextChat.getStrGroupID());

            notificationIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_GROUPCHAT);
//        }
        notificationIntent.putExtra("fromNotification", "true");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//        if (TextUtils.isEmpty(friendName)) {
//            if(!TextUtils.isEmpty(mDataTextChat.getSenderName())) {
//                friendName = "+" + mDataTextChat.getSenderName();
//            }else{
//                friendName = "+" + mDataTextChat.getFriendPhoneNo();
//            }
//        }
//        mDataTextChat.setFriendName(friendName);
//        mDataTextChat.setSenderName(friendName);


        NotificationUtils.showNotificationMessage(notificationIntent, AppVhortex.getInstance().getApplicationContext(), mDataTextChat);
    }

    private void deleteAllGroupMembers(String groupId) {

        String sql;
        sql = "DELETE FROM " + ConstantDB.TableGroupMember + " WHERE "
                + ConstantDB.GroupId + " = '" + groupId + "' ;";
        LogUtils.i(LOG_TAG, "TableGroupMember : Delete groups members " + sql);
        Cursor cursor = null;
        try {
            cursor = app.mDbHelper.getDB().rawQuery(sql, null);

            if (cursor != null) {
                cursor.moveToFirst();
                LogUtils.i(LOG_TAG, "TableGroupMember : Deleted " + cursor.getCount());
            }
        } catch (Exception e) {
            LogUtils.d(LOG_TAG, e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
    }

    private boolean bulkInsertMembers(ArrayList<DataGroupMembers> mListMembers, String groupId) throws Exception {
        boolean isSuccess = false;
        String sql = "INSERT OR REPLACE INTO " + ConstantDB.TableGroupMember + " ( "
                + ConstantDB.GroupId + ","
                + ConstantDB.MemberName + ","
                + ConstantDB.MemberId + ","
                + ConstantDB.MemberProfilePic + ","
                + ConstantDB.ChatId + ","
                + ConstantDB.MemberPhoneNo + ","
                + ConstantDB.MemberCountryId + ","
                + ConstantDB.IsOwner + " ,"
                + ConstantDB.IsGrpadmin + " ,"
                + ConstantDB.IsGrpblock + " ,"
                + ConstantDB.MemberStatus + " ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

        app.mDbHelper.getDB().beginTransactionNonExclusive();
        SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);

        try {
            for (DataGroupMembers mDataGroupMembers : mListMembers) {

                stmt.bindString(1, groupId);

                if (mDataGroupMembers.getUserName() != null || mDataGroupMembers.getUserName().trim().length() > 0) {
                    stmt.bindString(2, mDataGroupMembers.getUserName());
                } else {
                    stmt.bindString(2, "");
                }

                if (mDataGroupMembers.getUserId() != null || mDataGroupMembers.getUserId().trim().length() > 0) {
                    stmt.bindString(3, mDataGroupMembers.getUserId());
                } else {
                    stmt.bindString(3, "");
                }

                if (mDataGroupMembers.getUserImage() != null || mDataGroupMembers.getUserImage().trim().length() > 0) {
                    stmt.bindString(4, mDataGroupMembers.getUserImage());
                } else {
                    stmt.bindString(4, "");
                }

                if (mDataGroupMembers.getChatId() != null || mDataGroupMembers.getChatId().trim().length() > 0) {
                    stmt.bindString(5, mDataGroupMembers.getChatId());
                } else {
                    stmt.bindString(5, "");
                }

                if (mDataGroupMembers.getUserPhNo() != null || mDataGroupMembers.getUserPhNo().trim().length() > 0) {
                    stmt.bindString(6, mDataGroupMembers.getUserPhNo());
                } else {
                    stmt.bindString(6, "");
                }

                if (mDataGroupMembers.getUserCountryId() != null || mDataGroupMembers.getUserCountryId().trim().length() > 0) {
                    stmt.bindString(7, mDataGroupMembers.getUserCountryId());
                } else {
                    stmt.bindString(7, "");
                }

                if (mDataGroupMembers.getIsOwner() != null || mDataGroupMembers.getIsOwner().trim().length() > 0) {
                    stmt.bindString(8, mDataGroupMembers.getIsOwner());
                } else {
                    stmt.bindString(8, "");
                }

                if (mDataGroupMembers.getIsGrpadmin() != null || mDataGroupMembers.getIsGrpadmin().trim().length() > 0) {
                    stmt.bindString(9, mDataGroupMembers.getIsGrpadmin());
                } else {
                    stmt.bindString(9, "");
                }

                if (mDataGroupMembers.getIsGrpblock() != null || mDataGroupMembers.getIsGrpblock().trim().length() > 0) {
                    stmt.bindString(10, mDataGroupMembers.getIsGrpblock());
                } else {
                    stmt.bindString(10, "");
                }

                if (mDataGroupMembers.getStatus() != null || mDataGroupMembers.getStatus().trim().length() > 0) {
                    stmt.bindString(11, mDataGroupMembers.getStatus());
                } else {
                    stmt.bindString(11, "");
                }

                stmt.execute();
            }
            stmt.clearBindings();
            app.mDbHelper.getDB().setTransactionSuccessful();
            app.mDbHelper.getDB().endTransaction();
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }


    public boolean bulkInsertCreatedGroup(ArrayList<DataCreateOrEditGroup> mListGroups) {
        boolean isSuccess = false;
        String sql = "INSERT OR REPLACE INTO " + ConstantDB.TableGroupDetails + " ( "
                + ConstantDB.GroupId + ","
                + ConstantDB.GroupJId + ","
                + ConstantDB.GroupName + ","
                + ConstantDB.GroupImage + ","
                + ConstantDB.CreationDateTime + ","
                + ConstantDB.OwnerId + " ) " +
                "VALUES (?, ?,?, ?, ? ,?)";
        app.mDbHelper.getDB().beginTransactionNonExclusive();
        SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);
        try {
            for (DataCreateOrEditGroup mDataGroup : mListGroups) {
                if (mDataGroup.getGroupId() != null && mDataGroup.getGroupId().trim().length() > 0) {
                    stmt.bindString(1, mDataGroup.getGroupId());
                } else {
                    stmt.bindString(1, "");
                }

                if (mDataGroup.getGroupJId() != null && mDataGroup.getGroupJId().trim().length() > 0) {
                    stmt.bindString(2, mDataGroup.getGroupJId());
                } else {
                    stmt.bindString(2, "");
                }
                if (mDataGroup.getGroupName() != null && mDataGroup.getGroupName().trim().length() > 0) {
                    stmt.bindString(3, mDataGroup.getGroupName());
                } else {
                    stmt.bindString(3, "");
                }

                if (mDataGroup.getGroupImage() != null && mDataGroup.getGroupImage().trim().length() > 0) {
                    stmt.bindString(4, mDataGroup.getGroupImage());
                } else {
                    stmt.bindString(4, "");
                }

                if (mDataGroup.getCreationDateTime() != null && mDataGroup.getCreationDateTime().trim().length() > 0) {
                    stmt.bindString(5, mDataGroup.getCreationDateTime());
                } else {
                    stmt.bindString(5, "");
                }

                if (mDataGroup.getUserId() != null && mDataGroup.getUserId().trim().length() > 0) {
                    stmt.bindString(6, mDataGroup.getUserId());
                } else {
                    stmt.bindString(6, "");
                }

                /**First delete all group members before syncing with new/updated data*/
                deleteAllGroupMembers(mDataGroup.getGroupId());

                if (mDataGroup.getDataGroupMemberArrayList() != null && mDataGroup.getDataGroupMemberArrayList().size() > 0) {
                    bulkInsertCreatedMembers(mDataGroup.getDataGroupMemberArrayList(), mDataGroup.getGroupId());
                }
                stmt.execute();
            }
            stmt.clearBindings();
            app.mDbHelper.getDB().setTransactionSuccessful();
            app.mDbHelper.getDB().endTransaction();
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    public int deleteGroupApartFromCurrentGroupIds(String currentGroupIds) {

        int deletedGroupCount = 0;
        String sql;
        sql = "DELETE FROM " + ConstantDB.TableGroupDetails + " WHERE "
                + ConstantDB.GroupId + " NOT IN (" + currentGroupIds + ") ;";
        LogUtils.i(LOG_TAG, "TableGroupDetails: Delete groups " + sql);
        Cursor cursor = null;
        try {
            cursor = app.mDbHelper.getDB().rawQuery(sql, null);

            if (cursor != null) {
                cursor.moveToFirst();
                deletedGroupCount = cursor.getCount();
                LogUtils.i(LOG_TAG, "TableGroupDetails: Deleted " + cursor.getCount());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        sql = "DELETE FROM " + ConstantDB.TableGroupMember + " WHERE "
                + ConstantDB.GroupId + " NOT IN (" + currentGroupIds + ") ;";
        LogUtils.i(LOG_TAG, "TableGroupMember : Delete groups members " + sql);
        try {
            cursor = app.mDbHelper.getDB().rawQuery(sql, null);

            if (cursor != null) {
                cursor.moveToFirst();
                LogUtils.i(LOG_TAG, "TableGroupMember : Deleted " + cursor.getCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return deletedGroupCount;
    }

    public boolean removeGroupMemberAfterAdminActionChange(String groupId, String memberId) {
        int k = 0;

        try {
            k = app.mDbHelper.getDB().delete(ConstantDB.TableGroupMember, ConstantDB.MemberId
                    + " = ? AND " + ConstantDB.GroupId + " = ?", new String[]{memberId, groupId});
            LogUtils.i(LOG_TAG, "TableGroupMember: members Deleted: groupId" + groupId + " memberId:" + memberId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return k > 0;
    }


    public boolean blockMemberAfterAdminActionChange(String groupId, String memberId, String flag) {
        int k = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.IsGrpblock, flag);


        try {
            k = app.mDbHelper.getDB().update(ConstantDB.TableGroupMember, mContentValues, ConstantDB.MemberId + " = ? AND " + ConstantDB.GroupId + " = ?", new String[]{memberId, groupId});
        } catch (Exception e) {
            e.printStackTrace();
        }


        return k > 0;
    }

    public boolean promoteMemberAfterAdminActionChange(String groupId, String memberId, String flag) {
        int k = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.IsGrpadmin, flag);


        try {
            k = app.mDbHelper.getDB().update(ConstantDB.TableGroupMember, mContentValues, ConstantDB.MemberId + " = ? AND " + ConstantDB.GroupId + " = ?", new String[]{memberId, groupId});
        } catch (Exception e) {
            e.printStackTrace();
        }


        return k > 0;
    }

    public boolean updateGroupAfterAdminActionChange(GroupDetails groupDetails) {
        int k = 0;
        if (groupDetails != null) {

            List<Memberdetail> memberDetailsList = groupDetails.getMemberdetails();
            if (memberDetailsList != null) {
                String memberIdCommaSeparatedString = "";
                for (Memberdetail member : memberDetailsList) {
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(ConstantDB.IsGrpadmin, member.getIsGrpadmin());
                    mContentValues.put(ConstantDB.IsGrpblock, member.getIsGrpblock());


                    try {
                        k = app.mDbHelper.getDB().update(ConstantDB.TableGroupMember, mContentValues, ConstantDB.MemberId + " = ? AND " + ConstantDB.GroupId + " = ?", new String[]{member.getUserId(), groupDetails.getGroupId()});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    memberIdCommaSeparatedString = memberIdCommaSeparatedString
                            + (memberIdCommaSeparatedString.length() > 0 ? ",'" : "'")
                            + member.getUserId() + "'";
                }

                //remove the members that are not in the list from GroupMemberTable

                String sql;
                sql = "DELETE FROM " + ConstantDB.TableGroupMember + " WHERE "
                        + ConstantDB.GroupId + " = '" + groupDetails.getGroupId()
                        + "' AND " + ConstantDB.MemberId
                        + " NOT IN (" + memberIdCommaSeparatedString + ") ;";
                LogUtils.i(LOG_TAG, "TableGroupMember: Delete members " + sql);
                Cursor cursor = null;
                try {
                    cursor = app.mDbHelper.getDB().rawQuery(sql, null);

                    if (cursor != null) {
                        cursor.moveToFirst();
                        LogUtils.i(LOG_TAG, "TableGroupMember: members Deleted " + cursor.getCount());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                        cursor = null;
                    }
                }
            }
        }


        return k > 0;
    }


    private boolean bulkInsertCreatedMembers(ArrayList<DataGroupMember> mListMembers, String groupId) throws Exception {
        boolean isSuccess = false;
        String sql = "INSERT OR REPLACE INTO " + ConstantDB.TableGroupMember + " ( "
                + ConstantDB.GroupId + ","
                + ConstantDB.MemberName + ","
                + ConstantDB.MemberId + ","
                + ConstantDB.MemberProfilePic + ","
                + ConstantDB.ChatId + ","
                + ConstantDB.MemberPhoneNo + ","
                + ConstantDB.MemberCountryId + ","
                + ConstantDB.IsOwner + " ,"
                + ConstantDB.IsGrpadmin + " ,"
                + ConstantDB.IsGrpblock + " ,"
                + ConstantDB.MemberStatus + " ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

        app.mDbHelper.getDB().beginTransactionNonExclusive();
        SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sql);

        try {
            for (DataGroupMember mDataGroupMembers : mListMembers) {

                stmt.bindString(1, groupId);

                if (mDataGroupMembers.getMemberName() != null || mDataGroupMembers.getMemberName().trim().length() > 0) {
                    stmt.bindString(2, mDataGroupMembers.getMemberName());
                } else {
                    stmt.bindString(2, "");
                }

                if (mDataGroupMembers.getUserId() != null || mDataGroupMembers.getUserId().trim().length() > 0) {
                    stmt.bindString(3, mDataGroupMembers.getUserId());
                } else {
                    stmt.bindString(3, "");
                }

                if (mDataGroupMembers.getMemberImage() != null || mDataGroupMembers.getMemberImage().trim().length() > 0) {
                    stmt.bindString(4, mDataGroupMembers.getMemberImage());
                } else {
                    stmt.bindString(4, "");
                }

                if (mDataGroupMembers.getChatId() != null || mDataGroupMembers.getChatId().trim().length() > 0) {
                    stmt.bindString(5, mDataGroupMembers.getChatId());
                } else {
                    stmt.bindString(5, "");
                }

                if (mDataGroupMembers.getMemberPhNo() != null || mDataGroupMembers.getMemberPhNo().trim().length() > 0) {
                    stmt.bindString(6, mDataGroupMembers.getMemberPhNo());
                } else {
                    stmt.bindString(6, "");
                }

                if (mDataGroupMembers.getMemberCountryId() != null || mDataGroupMembers.getMemberCountryId().trim().length() > 0) {
                    stmt.bindString(7, mDataGroupMembers.getMemberCountryId());
                } else {
                    stmt.bindString(7, "");
                }

                if (mDataGroupMembers.getIsOwner() != null || mDataGroupMembers.getIsOwner().trim().length() > 0) {
                    stmt.bindString(8, mDataGroupMembers.getIsOwner());
                } else {
                    stmt.bindString(8, "");
                }

                if (mDataGroupMembers.getIsGrpadmin() != null || mDataGroupMembers.getIsGrpadmin().trim().length() > 0) {
                    stmt.bindString(9, mDataGroupMembers.getIsGrpadmin());
                } else {
                    stmt.bindString(9, "");
                }

                if (mDataGroupMembers.getIsGrpblock() != null || mDataGroupMembers.getIsGrpblock().trim().length() > 0) {
                    stmt.bindString(10, mDataGroupMembers.getIsGrpblock());
                } else {
                    stmt.bindString(10, "");
                }

                if (mDataGroupMembers.getStatus() != null || mDataGroupMembers.getStatus().trim().length() > 0) {
                    stmt.bindString(11, mDataGroupMembers.getStatus());
                } else {
                    stmt.bindString(11, "");
                }

                stmt.execute();
            }
            stmt.clearBindings();
            app.mDbHelper.getDB().setTransactionSuccessful();
            app.mDbHelper.getDB().endTransaction();
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }


    public ArrayList<DataGroup> getGroupList() {
        ArrayList<DataGroup> mListGroup = new ArrayList<DataGroup>();

//        SELECT * , (Select messageBody from Chat where strGroupID = GroupId AND strGroupID<>'' LIMIT 0,1) as msgBody,(Select messageDateTime, strftime() from Chat where strGroupID = GroupId AND strGroupID<>'' LIMIT 0,1) AS msgTime,  FROM GroupDetails where GroupId!='' ORDER BY msgTime DESC
//        SELECT * FROM GroupDetails AS GD LEFT JOIN Chat as C ON GD.GroupId= C.strGroupID where GroupId!=''

//        String SQL = "SELECT * , (Select "+ConstantDB.messageBody+" from "+ConstantDB.TableChat+" where "+ConstantDB.strGroupID+" = "+ConstantDB.GroupId+" AND "+ConstantDB.strGroupID+"<>'' LIMIT 0,1) as msgBody,(Select "+ConstantDB.messageDateTime +" from "+ConstantDB.TableChat+" where "+ConstantDB.strGroupID+" = "+ConstantDB.GroupId+" AND "+ConstantDB.strGroupID+" <>'' LIMIT 0,1) AS msgTime FROM "+ConstantDB.GroupDetails where GroupId!='' ORDER BY msgTime DESC
//        String SQL = "SELECT * FROM "
//                + ConstantDB.TableGroupDetails
//                + "AS GD LEFT JOIN "
//                +ConstantDB.TableChat+"AS C ON GD."+ConstantDB.GroupId+" = C."+ConstantDB.strGroupID
//                +" WHERE "+ConstantDB.GroupId+" != '' ORDER BY GD."+ConstantDB.CreationDateTime+";";
        String SQL = "SELECT * , (Select messageBody from Chat where strGroupID = GroupId AND strGroupID<>'' LIMIT 0,1) " +
                "as msgBody,(Select sender_id from Chat where strGroupID = GroupId AND strGroupID<>'' LIMIT 0,1) " +
                "as sender_id,(Select messageDateTime from Chat where strGroupID = GroupId AND strGroupID<>''  ORDER BY messageDateTime DESC LIMIT 0,1) " +
                "AS msgTime FROM GroupDetails where GroupId!='' ORDER BY msgTime DESC";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                DataGroup mDataGroup;
                do {
                    mDataGroup = new DataGroup();
                    mDataGroup.setGroupId(c.getString(c.getColumnIndex(ConstantDB.GroupId)));
                    mDataGroup.setGroupJId(c.getString(c.getColumnIndex(ConstantDB.GroupJId)));
                    mDataGroup.setGroupName(CommonMethods.getUTFDecodedString(c.getString(c.getColumnIndex(ConstantDB.GroupName))));
                    mDataGroup.setGroupImage(c.getString(c.getColumnIndex(ConstantDB.GroupImage)));
                    mDataGroup.setCreationdateTime(c.getString(c.getColumnIndex(ConstantDB.CreationDateTime)));
                    mDataGroup.setOwnerId(c.getString(c.getColumnIndex(ConstantDB.OwnerId)));
                    mDataGroup.setMemberdetails(getMemberList(mDataGroup.getGroupId()));

                    DataTextChat lastMessage = new TableChat(mContext).
                            getLastGroupChat(mDataGroup.getGroupId());
                    if (lastMessage != null) {
                        String message = "";
                        if (lastMessage.getMessageType().equalsIgnoreCase(ConstantChat.MESSAGE_TYPE)
                                || lastMessage.getMessageType().equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_ADDED)
                                || lastMessage.getMessageType().equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_REMOVED)
                                || lastMessage.getMessageType().equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_LEFT)
                                || lastMessage.getMessageType().equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_CREATED)) {
                            if(!lastMessage.getStrTranslatedText().isEmpty()){
                                message = lastMessage.getStrTranslatedText();
                            }else {
                                message = lastMessage.getBody();
                            }
                        } else {
                            message = lastMessage.getMessageType();
                        }
                        mDataGroup.lastMessageType = lastMessage.getMessageType();
                        mDataGroup.setLastMessage(message);
                        mDataGroup.setLastMessageTime(lastMessage.getTimestamp());
                        mDataGroup.setLastMessageSenderId(lastMessage.getSenderId());
                        mDataGroup.setLastMessageSenderName(lastMessage.getSenderName());


//                        DataContact mDataContact = new TableContact(mContext).
//                                getFriendDetailsByID(lastMessage.getFriendId());
                        if (TextUtils.isEmpty(mDataGroup.getLastMessageSenderName())) {

                            DataGroupMembers dataGroupMember = getMemberInAGroup(mDataGroup.getGroupId(), lastMessage.getSenderId());
                            if (dataGroupMember != null) {
                                String name = dataGroupMember.getUserName();

                                if (!TextUtils.isEmpty(name)) {
                                    if (!TextUtils.isEmpty(name)) {
                                        mDataGroup.setLastMessageSenderName(name);
                                    } else {
                                        mDataGroup.setLastMessageSenderName("");
                                    }
                                }
                            }
                        }
                    }
                    int unreadCount = tableChat.getUnreadCountForThisGroup(mDataGroup.getGroupId());
                    mDataGroup.unreadCount = unreadCount;
                    mListGroup.add(mDataGroup);
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
        return mListGroup;
    }

    public ArrayList<DataGroupMembers> getMemberList(String groupId) {

        ArrayList<DataGroupMembers> mListMembers = new ArrayList<DataGroupMembers>();
        if (!TextUtils.isEmpty(groupId)) {
            String SQL = "SELECT * FROM " + ConstantDB.TableGroupMember + " WHERE "
                    + ConstantDB.GroupId + " = " + groupId + ";";
            Cursor c = null;
            try {
                c = app.mDbHelper.getDB().rawQuery(SQL, null);
                if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                    DataGroupMembers mDataGroupMembers;
                    do {
                        mDataGroupMembers = new DataGroupMembers();
                        mDataGroupMembers.setUserName(CommonMethods.getUTFDecodedString(c.getString(c.getColumnIndex(ConstantDB.MemberName))));
                        mDataGroupMembers.setUserId(c.getString(c.getColumnIndex(ConstantDB.MemberId)));
                        mDataGroupMembers.setUserImage(c.getString(c.getColumnIndex(ConstantDB.MemberProfilePic)));
                        mDataGroupMembers.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                        mDataGroupMembers.setUserPhNo(c.getString(c.getColumnIndex(ConstantDB.MemberPhoneNo)));
                        mDataGroupMembers.setUserCountryId(c.getString(c.getColumnIndex(ConstantDB.MemberCountryId)));
                        mDataGroupMembers.setIsOwner(c.getString(c.getColumnIndex(ConstantDB.IsOwner)));
                        mDataGroupMembers.setIsGrpadmin(c.getString(c.getColumnIndex(ConstantDB.IsGrpadmin)));
                        mDataGroupMembers.setIsGrpblock(c.getString(c.getColumnIndex(ConstantDB.IsGrpblock)));
                        mDataGroupMembers.setStatus(c.getString(c.getColumnIndex(ConstantDB.MemberStatus)));
                        mListMembers.add(mDataGroupMembers);
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
        }
        return mListMembers;
    }

    public DataGroupMembers getMemberInAGroup(String groupId, String memberUserId) {

        DataGroupMembers groupMember = null;
        if (!TextUtils.isEmpty(groupId)) {
            String SQL = "SELECT * FROM " + ConstantDB.TableGroupMember + " WHERE "
                    + ConstantDB.GroupId + " = '" + groupId + "' AND " + ConstantDB.MemberId
                    + " = '" + memberUserId + "' ORDER BY " + ConstantDB.MemberName + ";";
            Cursor c = null;
            try {
                c = app.mDbHelper.getDB().rawQuery(SQL, null);
                if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                    do {
                        groupMember = new DataGroupMembers();
                        groupMember.setUserName(CommonMethods.getUTFDecodedString(c.getString(c.getColumnIndex(ConstantDB.MemberName))));
                        groupMember.setUserId(c.getString(c.getColumnIndex(ConstantDB.MemberId)));
                        groupMember.setUserImage(c.getString(c.getColumnIndex(ConstantDB.MemberProfilePic)));
                        groupMember.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                        groupMember.setUserPhNo(c.getString(c.getColumnIndex(ConstantDB.MemberPhoneNo)));
                        groupMember.setUserCountryId(c.getString(c.getColumnIndex(ConstantDB.MemberCountryId)));
                        groupMember.setIsOwner(c.getString(c.getColumnIndex(ConstantDB.IsOwner)));
                        groupMember.setIsGrpadmin(c.getString(c.getColumnIndex(ConstantDB.IsGrpadmin)));
                        groupMember.setIsGrpblock(c.getString(c.getColumnIndex(ConstantDB.IsGrpblock)));
                        groupMember.setStatus(c.getString(c.getColumnIndex(ConstantDB.MemberStatus)));
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
        }
        return groupMember;
    }

    public ArrayList<DataGroupMember> getMemberListbyID(String groupId) {
        ArrayList<DataGroupMember> mListMembers = new ArrayList<DataGroupMember>();
        String SQL = "SELECT * FROM " + ConstantDB.TableGroupMember + " WHERE "
                + ConstantDB.GroupId + " = " + groupId + " ORDER BY " + ConstantDB.MemberName + ";";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {
                DataGroupMember mDataGroupMember;
                do {
                    mDataGroupMember = new DataGroupMember();
                    mDataGroupMember.setMemberName(CommonMethods.getUTFDecodedString(c.getString(c.getColumnIndex(ConstantDB.MemberName))));
                    mDataGroupMember.setGroupMemberUserId(c.getString(c.getColumnIndex(ConstantDB.MemberId)));
                    mDataGroupMember.setMemberImage(c.getString(c.getColumnIndex(ConstantDB.MemberProfilePic)));
                    mDataGroupMember.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                    mDataGroupMember.setMemberPhNo(c.getString(c.getColumnIndex(ConstantDB.MemberPhoneNo)));
                    mDataGroupMember.setMemberCountryId(c.getString(c.getColumnIndex(ConstantDB.MemberCountryId)));
                    mDataGroupMember.setIsOwner(c.getString(c.getColumnIndex(ConstantDB.IsOwner)));
                    mDataGroupMember.setIsGrpadmin(c.getString(c.getColumnIndex(ConstantDB.IsGrpadmin)));
                    mDataGroupMember.setIsGrpblock(c.getString(c.getColumnIndex(ConstantDB.IsGrpblock)));
                    mDataGroupMember.setStatus(c.getString(c.getColumnIndex(ConstantDB.MemberStatus)));
                    mListMembers.add(mDataGroupMember);
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
        return mListMembers;
    }

    public DataGroup getGroupById(String groupId) {
        DataGroup mDataGroup = new DataGroup();
        String SQL = "SELECT * FROM " + ConstantDB.TableGroupDetails + " WHERE "
                + ConstantDB.GroupId + " = '" + groupId + "';";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {


                mDataGroup = new DataGroup();
                mDataGroup.setGroupId(c.getString(c.getColumnIndex(ConstantDB.GroupId)));
                mDataGroup.setGroupJId(c.getString(c.getColumnIndex(ConstantDB.GroupJId)));
                mDataGroup.setGroupName(CommonMethods.getUTFDecodedString(c.getString(c.getColumnIndex(ConstantDB.GroupName))));
                mDataGroup.setGroupImage(c.getString(c.getColumnIndex(ConstantDB.GroupImage)));
                mDataGroup.setCreationdateTime(c.getString(c.getColumnIndex(ConstantDB.CreationDateTime)));
                mDataGroup.setOwnerId(c.getString(c.getColumnIndex(ConstantDB.OwnerId)));
                mDataGroup.setMemberdetails(getMemberList(mDataGroup.getGroupId()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mDataGroup;
    }


    public DataGroup getGroupByGroupJId(String groupJId) {
        DataGroup mDataGroup = new DataGroup();
        String SQL = "SELECT * FROM " + ConstantDB.TableGroupDetails + " WHERE "
                + ConstantDB.GroupJId + " = '" + groupJId + "';";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {


                mDataGroup = new DataGroup();
                mDataGroup.setGroupId(c.getString(c.getColumnIndex(ConstantDB.GroupId)));
                mDataGroup.setGroupJId(c.getString(c.getColumnIndex(ConstantDB.GroupJId)));
                mDataGroup.setGroupName(CommonMethods.getUTFDecodedString(c.getString(c.getColumnIndex(ConstantDB.GroupName))));
                mDataGroup.setGroupImage(c.getString(c.getColumnIndex(ConstantDB.GroupImage)));
                mDataGroup.setCreationdateTime(c.getString(c.getColumnIndex(ConstantDB.CreationDateTime)));
                mDataGroup.setOwnerId(c.getString(c.getColumnIndex(ConstantDB.OwnerId)));
                mDataGroup.setMemberdetails(getMemberList(mDataGroup.getGroupId()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mDataGroup;
    }

    public void deleteMember() {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableGroupDetails, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteGroup() {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableGroupMember, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteGroup(String groupId) {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableGroupDetails, ConstantDB.GroupId + " = ?", new String[]{groupId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableGroupMember, ConstantDB.GroupId + " = ?", new String[]{groupId});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkExistance(String memberId, String groupId) {
        boolean status = false;
        String SQL = "SELECT * FROM " + ConstantDB.TableGroupMember + " WHERE "
                + ConstantDB.GroupId + " = '" + groupId + "' AND "
                + ConstantDB.MemberId + " = '" + memberId + "';";
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
}
