package com.wachat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.wachat.R;
import com.wachat.adapter.AdapterChat;
import com.wachat.adapter.AdapterChatSection;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncBlockUnknownNumber;
import com.wachat.async.AsyncForUserTranslate;
import com.wachat.async.AsyncSetFriendStatus;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.chatUtils.NotificationUtils;
import com.wachat.chatUtils.OneToOneChatJSonCreator;
import com.wachat.customViews.DividerItemDecoration;
import com.wachat.customViews.components.Cell;
import com.wachat.customViews.tooltippopupwindow.FirstTimeChatOptionsPopUpWindow;
import com.wachat.customViews.tooltippopupwindow.TooltipItemClickListener;
import com.wachat.customViews.tooltippopupwindow.TooltipPopUpWindow;
import com.wachat.data.BaseResponse;
import com.wachat.data.DataContact;
import com.wachat.data.DataGroup;
import com.wachat.data.DataGroupMembers;
import com.wachat.data.DataProfile;
import com.wachat.data.DataShareImage;
import com.wachat.data.DataTextChat;
import com.wachat.data.DataYahooNews;
import com.wachat.data.YouTubeVideo;
import com.wachat.dataClasses.ContactSetget;
import com.wachat.dataClasses.LocationGetSet;
import com.wachat.helper.HelperChat;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.interfaces.WebServiceCallBack;
import com.wachat.runnables.RunnableProcessVideoChat;
import com.wachat.services.ConstantXMPP;
import com.wachat.services.ServiceXmppConnection;
import com.wachat.storage.ConstantDB;
import com.wachat.storage.TableChat;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableGroup;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.DateTimeUtil;
import com.wachat.util.ImageUtils;
import com.wachat.util.LogUtils;
import com.wachat.util.MediaUtils;
import com.wachat.util.RunnableProcessImageChat;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;
import com.wachat.util.ValidationUtils;
import com.wachat.util.VolleyErrorHelper;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smackx.chatstates.ChatState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * Created by Priti Chatterjee on 18-08-2015.
 */
public class ActivityChat extends BaseActivity implements HelperChat.HelperCallback, TextWatcher,
        EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        Cell.OnCellItemClickListener, InterfaceResponseCallback {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityChat.class);
    private static final int REQUEST_ADD_TO_CONTACT = 1234;
    public Toolbar appBar;
    LocationManager locationManager;
    String provider;
    RelativeLayout emojicons;
    int smileyHeight = 10;
    int blockStatus;
    String getValueFromDbCalled = "";
    Handler handler = new Handler();
    private RecyclerView recycler_view_common;
    private AdapterChat mAdapter;
    private final BroadcastReceiver fileUploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle nBundle = intent.getExtras();
                if (nBundle != null) {
                    DataTextChat fileChat = (DataTextChat) nBundle.getSerializable(Constants.B_OBJ);
                    String action = intent.getAction();
                    if (action != null) {
                        if (Constants.ACTION_FILE_UPLOAD_COMPLETE.equals(action)) {
                            String status = nBundle.getString(Constants.KEY_FILE_UPLOAD_STATUS);

                            if (fileChat != null) {
                                notifyChatListUI(fileChat);

                            }
                            if (Constants.UPLOAD_STATUS_SUCCESS.equals(status)) {
                                if (fileChat != null) {
                                    fileChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
                                    if (fileChat.getMessageType().equals(ConstantChat.VIDEO_TYPE)) {
                                        sendVideoMessageToXMPP(fileChat);
                                    } else if (fileChat.getMessageType().equals(ConstantChat.IMAGE_TYPE)) {
                                        sendImageMessageToXMPP(fileChat);
                                    } else if (fileChat.getMessageType().equals(ConstantChat.IMAGECAPTION_TYPE)) {
                                        sendImageMessageToXMPP(fileChat);
                                    } else if (fileChat.getMessageType().equals(ConstantChat.SKETCH_TYPE)) {
                                        sendImageMessageToXMPP(fileChat);
                                    }

                                }
                            } else if (Constants.UPLOAD_STATUS_FAILED_SERVER_ERROR.equals(status)) {

                                BaseResponse baseResponse = (BaseResponse) nBundle.getSerializable(Constants.B_RESPONSE_OBJ);
                                String errorMessage = "Error code: " + baseResponse.getResponseCode() + " Message: " + baseResponse.getResponseDetails();
                                Toast.makeText(ActivityChat.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } else if (Constants.UPLOAD_STATUS_FAILED_NETWORK_ERROR.equals(status)) {
                                VolleyError error = (VolleyError) nBundle.getSerializable(Constants.B_ERROR_OBJ);
                                Toast.makeText(ActivityChat.this,
                                        VolleyErrorHelper.getMessage(error, ActivityChat.this), Toast.LENGTH_SHORT).show();
                            }

                        } else if (Constants.ACTION_FILE_UPLOAD_PROGRESS.equals(action)) {

                            int progress = nBundle.getInt(Constants.KEY_FILE_UPLOAD_PROGRESS);
                            LogUtils.i(LOG_TAG, "fileUploadReceiver: progress: " + progress);
                        }
                    }

                }
            }
        }
    };
    private final Handler imageBlurHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            DataTextChat mDataFileChat = (DataTextChat) bundle.getSerializable(Constants.B_RESULT);

            new TableChat(getApplicationContext()).updateFileChatAfterBlurComplete(mDataFileChat);
            notifyChatListUI(mDataFileChat);
        }
    };
    private ProgressDialog mPageFilterDialog;
    private AdapterChatSection mSectionedAdapter;
    private RelativeLayout frm_container;
    private RelativeLayout rel_bottom;
    private HelperChat mHelperChat;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayout main_container, lnr_Section, lnr_Share;
    private TextView tvSection;
    private ScrollGesture mScrollGesture;
    private View view;
    private EmojiconEditText et_chatText;
    private ImageView iv_camera, iv_smiley;
    private View rl_camera;
    private View rl_smiley;
    private boolean isSmiley = false;
    private boolean keyboardVisible = false;
    private String friendId = "", friendChatId = "", friendPhoneNum = "", myPhoneNum = "", myChatId = "", FriendImageLink = "";
    private com.wachat.data.DataContact mDataContact;
    private DisplayImageOptions options;
    private String isFromNotification = "false";
    private ArrayList<DataTextChat> mListChats = new ArrayList<DataTextChat>();
    private String userId = "";
    private DataProfile mDataProfile;
    private String mGroupId = "";
    private String mChatType = "";
    private TableChat mTableChat;
    private boolean isVisible = true;
    private DataGroup mDataGroup = new DataGroup();
    private String groupMemberName = "";
    private boolean shouldShowAcceptRejectFooter = false;
    private String firstTimeChatOptionSelection = "";
    private FirstTimeChatOptionsPopUpWindow popUpWindow;
    private String friendName = "";
    private final Handler videoUploadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            DataTextChat mDataFileChat = (DataTextChat) bundle.getSerializable(Constants.B_RESULT);
            switch (msg.what) {
                case 1:
                    //start upload

                    mDataFileChat.setFriendPhoneNo(friendPhoneNum);
                    mDataFileChat.setUploadStatus(DataTextChat.UploadStatus.UPLOADING);
                    updateChatListUI(mDataFileChat);
                    insertImageMessageToDB(mDataFileChat);
                    AppVhortex.getInstance().addFileUploadRequestToQueue(mDataFileChat);
                    break;
                case 2:
                    //cancel upload
                    mDataFileChat.setFriendPhoneNo(friendPhoneNum);
                    AppVhortex.getInstance().cancelFileUploadRequest(mDataFileChat);
                    mDataFileChat.setUploadStatus(DataTextChat.UploadStatus.NOT_UPLOADED);
                    new TableChat(getApplicationContext()).updateUploadStatus(mDataFileChat);
                    notifyChatListUI(mDataFileChat);
                    break;
                case 3:
                    //Retry upload
                    mDataFileChat.setFriendPhoneNo(friendPhoneNum);
                    mDataFileChat.setUploadStatus(DataTextChat.UploadStatus.UPLOADING);
                    new TableChat(getApplicationContext()).updateUploadStatus(mDataFileChat);
                    AppVhortex.getInstance().addFileUploadRequestToQueue(mDataFileChat);
                    notifyChatListUI(mDataFileChat);

                    break;
                case 4:
                    //cancel download
                    mDataFileChat.setFriendPhoneNo(friendPhoneNum);
                    int result = AppVhortex.getInstance().cancelDownloadRequest(mDataFileChat);
                    if (result == 1) {
                        AppVhortex.getInstance().removeFromDownloadQueueMap(mDataFileChat);
                        notifyChatListUI(mDataFileChat);
                    } else {

                    }
                    break;

                default:
                    break;
            }
        }
    };
    private final Handler imageUploadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            DataTextChat mDataFileChat = (DataTextChat) bundle.getSerializable(Constants.B_RESULT);
            switch (msg.what) {
                case 1:
                    //start upload

                    mDataFileChat.setFriendPhoneNo(friendPhoneNum);
                    mDataFileChat.setUploadStatus(DataTextChat.UploadStatus.UPLOADING);
                    updateChatListUI(mDataFileChat);
                    insertImageMessageToDB(mDataFileChat);
                    AppVhortex.getInstance().addFileUploadRequestToQueue(mDataFileChat);

                    if (mDataFileChat.getIsMasked().equalsIgnoreCase("1")) {
                        startImageBlur(mDataFileChat);
                    }

                    break;
                case 2:
                    //cancel upload
                    mDataFileChat.setFriendPhoneNo(friendPhoneNum);
                    AppVhortex.getInstance().cancelFileUploadRequest(mDataFileChat);
                    mDataFileChat.setUploadStatus(DataTextChat.UploadStatus.NOT_UPLOADED);
                    new TableChat(getApplicationContext()).updateUploadStatus(mDataFileChat);
                    notifyChatListUI(mDataFileChat);
                    break;
                case 3:
                    //Retry upload
                    mDataFileChat.setFriendPhoneNo(friendPhoneNum);
                    mDataFileChat.setUploadStatus(DataTextChat.UploadStatus.UPLOADING);
                    new TableChat(getApplicationContext()).updateUploadStatus(mDataFileChat);
                    AppVhortex.getInstance().addFileUploadRequestToQueue(mDataFileChat);
                    notifyChatListUI(mDataFileChat);

                    break;
                default:
                    break;
            }
        }
    };
    private final BroadcastReceiver contactUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i(LOG_TAG, "contactUpdatedReceiver");
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
                if (mDataContact != null) {
                    if (mTableContact.checkExistance(mDataContact.getPhoneNumber())) {
                        mDataContact = mTableContact.getFriendDetailsByFrienChatID(friendChatId);
                        if (mDataContact != null) {
                            setTopBarValue();
                            if (mDataContact.getIsFriend() == 1) {

                                shouldShowAcceptRejectFooter = false;
                                mAdapter.refreshChatList(shouldShowAcceptRejectFooter);
                            }
                        }
                    }
                }

            } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                if (!checkIfGroupStillValid()) {
                    showGroupRemovedAlert();
                } else {
//                    TableGroup tableGroup = new TableGroup(ActivityChat.this);
//                    mDataGroup = tableGroup.getGroupById(mGroupId);
//
//                    mListChats = new TableChat(mActivity).getAllGroupChat(mGroupId);
//
//                    DataGroupMembers member = tableGroup.getMemberInAGroup(mDataGroup.getGroupId(), mDataProfile.getUserId());
//
//                    if (member != null) {
//                        blockStatus = Integer.parseInt(member.getIsGrpblock());
//                    }
//
//                    checkIsJoined();
//                    mAdapter.refreshChatList(false);
//                    updateChatListUIForGroupChat();
//                    groupUpdatedUpdateChatList();
                    boolean group_changed = intent.getBooleanExtra("group_changed", false);
                    LogUtils.i(LOG_TAG, "contactUpdatedReceiver: group_changed: " + group_changed);
                    if (group_changed)
                        onNewIntent(ActivityChat.this.getIntent());
                }
            }
        }
    };
    private boolean isTyping = false;

    private void startImageBlur(DataTextChat mDataFileChat) {
//        new TableChat(getApplicationContext()).updateFileChatAfterBlurComplete(mDataFileChat);
        notifyChatListUI(mDataFileChat);
//        Thread imageHandlerThread = new Thread(new RunnableProcessImageBlur(this, mDataFileChat, imageBlurHandler));
//        imageHandlerThread.start();
    }

    private boolean checkIfGroupStillValid() {
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            if (mDataGroup != null) {
                DataGroup updatedGroup = new TableGroup(ActivityChat.this).getGroupById(mDataGroup.getGroupId());

                if (updatedGroup != null) {
                    return !TextUtils.isEmpty(updatedGroup.getGroupId());
                } else {
                    return false;
                }

            } else {
                return false;
            }

        } else {
            return true;
        }
    }

    private void showGroupRemovedAlert() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
            builder.setMessage("You have been removed from the group");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    finish();
                }
            });

            builder.setCancelable(false);

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyChatListUI(DataTextChat mDataTextChat) {
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            if (mAdapter.getData().get(i).getMessageid().equals(mDataTextChat.getMessageid())) {
                mAdapter.getData().get(i).setUploadStatus(mDataTextChat.getUploadStatus());
                mAdapter.getData().get(i).setProgress(mDataTextChat.getProgress());
                mAdapter.getData().get(i).setFileUrl(mDataTextChat.getFileUrl());
                mAdapter.getData().get(i).setFilePath(mDataTextChat.getFilePath());
                mAdapter.getData().get(i).setMaskEnabled(mDataTextChat.getMaskEnabled());
            }
        }


//        mAdapter.getData().set(mAdapter.getData().indexOf(mDataTextChat),mDataTextChat);
        mAdapter.notifyDataSetChanged();
    }

    /* IF DOLPHINS ARE SO SMART, HOW COME THEY LIVE IN IGLOOS? */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mTableChat = new TableChat(this);
        onNewIntent(getIntent());
        keyboardEvent();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_FILE_UPLOAD_COMPLETE);
        intentFilter.addAction(Constants.ACTION_FILE_UPLOAD_PROGRESS);
        registerReceiver(fileUploadReceiver, intentFilter);

        IntentFilter contactUpdatedIntentFilter = new IntentFilter();
        contactUpdatedIntentFilter.addAction(getString(R.string.ContactBroadcast));
        registerReceiver(contactUpdatedReceiver, contactUpdatedIntentFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getValuesFromIntent(intent)) {
            getValueFromDB(intent);
            initViews();
            initComponent();
            smileyHeight = CommonMethods.getScreen(this).heightPixels / 3 - (int) CommonMethods.convertDpToPixel(50, ActivityChat.this);
            setEmojiconFragment(false);
            showSmiley(false);
            createReadStatusForDeliveredChats();
//            String notificationId = "";
//            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
//
//            }else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)){
//                notificationId = mGroupId;
//            }
//            if(!TextUtils.isEmpty(notificationId)) {
//                NotificationUtils.clearNotificationWithId(this, notificationId);
//            }
            NotificationUtils.clearAllNotification(this);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(fileUploadReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            unregisterReceiver(contactUpdatedReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (handler != null) {
            handler.removeMessages(0);
            handler = null;
        }


        try {
            ShortcutBadger.applyCount(getApplicationContext(), getBadgeCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public int getBadgeCount() {
        try {
            TableChat tableChat = new TableChat(this);
            String msgCount = tableChat.getUnreadOnetoOneMessagesCount(ConstantChat.STATUS_DELIVERED,
                    mPrefs.getChatId());
            int oneToOneCount = 0;
            try {
                oneToOneCount = Integer.parseInt(msgCount);
            } catch (NumberFormatException e) {
            }

            String groupMessageCount = tableChat.getUnreadGroupMessagesCount(ConstantChat.STATUS_DELIVERED,
                    mPrefs.getChatId());

            int groupCount = 0;
            try {
                groupCount = Integer.parseInt(groupMessageCount);
            } catch (NumberFormatException e) {
            }


            return oneToOneCount + groupCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    @Override
    protected void sucessfullyServiceConnectionConnected() {
        super.sucessfullyServiceConnectionConnected();
        createReadStatusForDeliveredChats();
    }

    private void createReadStatusForDeliveredChats() {
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            String CommaseperatedUnreadMessageIDs = mTableChat.
                    getUnredButDeliveredMessages(ConstantChat.STATUS_DELIVERED, friendChatId);
            if (!TextUtils.isEmpty(CommaseperatedUnreadMessageIDs)) {
                SendReadStatusToXMPP(CommaseperatedUnreadMessageIDs, friendChatId);
                UpdateReadStatusToDB(CommaseperatedUnreadMessageIDs);
            }
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            String CommaseperatedUnreadMessageIDs = mTableChat.
                    getUnredButDeliveredGroupMessages(ConstantChat.STATUS_DELIVERED, mDataGroup.getGroupId());
            if (!TextUtils.isEmpty(CommaseperatedUnreadMessageIDs)) {
//                SendReadStatusToXMPP(CommaseperatedUnreadMessageIDs, friendChatId);
                UpdateReadStatusToDB(CommaseperatedUnreadMessageIDs);
            }
        }
    }

    private void UpdateReadStatusToDB(String commaseperatedUnreadMessageIDs) {
        String[] arrMessageIds = CommonMethods.getCommaseperateToArray(commaseperatedUnreadMessageIDs);
        for (int i = 0; i < arrMessageIds.length; i++) {
            DataTextChat mDataTextChat = new DataTextChat();
            mDataTextChat.setMessageid(arrMessageIds[i]);
            mDataTextChat.setDeliveryStatus(ConstantChat.STATUS_READ);
            mTableChat.updateChatDeliveryStatus(mDataTextChat);
        }
    }

    private void SendReadStatusToXMPP(String commaseperatedUnreadMessageIDs, String friendChatId) {
        DataTextChat mDataTextChat = new DataTextChat();
        mDataTextChat.setMessageid(commaseperatedUnreadMessageIDs);
        mDataTextChat.setDeliveryStatus(ConstantChat.STATUS_READ);
        mDataTextChat.setFriendChatID(friendChatId);
        if (mBound) {
            Message chatDeliveryMessage = Message.obtain();
            chatDeliveryMessage.what = ConstantXMPP.SEND_DELVERY_STATUS;
            String DeliveryStatusMessageJSon = OneToOneChatJSonCreator.getDeliveryMessageToJSON(mDataTextChat);
            //make bundle
            Bundle mBundle = new Bundle();
            mBundle.putString(ConstantChat.SEND_DELIVERY_DTATUS_B_MESSAGE, DeliveryStatusMessageJSon);
            mBundle.putSerializable(ConstantChat.SEND_DELIVERY_OBJ_B_MESSAGE, mDataTextChat);
            chatDeliveryMessage.setData(mBundle);
            try {
                messengerConnection.send(chatDeliveryMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
        if (mBound) {
            Message message = Message.obtain();
            message.what = 110;
            message.replyTo = uiMessenger;

            try {
                messengerConnection.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void onStop() {
        isVisible = false;
        super.onStop();
    }

    private boolean getValuesFromIntent(Intent intent) {
        if (intent == null) {
            finish();
            return false;
        }
        Bundle mBundle = intent.getExtras();
        String chatType = "";
        chatType = intent.getStringExtra(Constants.B_TYPE);
        if (TextUtils.isEmpty(chatType)) {
            CommonMethods.MYToast(this, "No valid Chat Type");
            return false;
        }
        if (mBundle != null && mBundle.containsKey("fromNotification"))
            isFromNotification = mBundle.getString("fromNotification");
        if (chatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mChatType = ConstantChat.TYPE_SINGLECHAT;
            if (mBundle != null) {

                if (mBundle != null && mBundle.containsKey(Constants.B_ID))
                    friendId = mBundle.getString(Constants.B_ID);
                if (mBundle != null && mBundle.containsKey(Constants.B_NAME))
                    friendName = mBundle.getString(Constants.B_NAME);
                if (mBundle != null && mBundle.containsKey("fromNotification"))
                    isFromNotification = mBundle.getString("fromNotification");
                if (mBundle != null && mBundle.containsKey(ConstantDB.ChatId))
                    friendChatId = mBundle.getString(ConstantDB.ChatId);
                if (mBundle != null && mBundle.containsKey(ConstantDB.PhoneNo))
                    friendPhoneNum = mBundle.getString(ConstantDB.PhoneNo);
                if (mBundle != null && mBundle.containsKey(ConstantDB.FriendImageLink))
                    FriendImageLink = mBundle.getString(ConstantDB.FriendImageLink);
            }

            if (TextUtils.isEmpty(friendId)) {
                String intentFriendId = intent.getStringExtra(Constants.B_ID);
                friendId = TextUtils.isEmpty(intentFriendId) ? "" : intentFriendId;
            }

            if (TextUtils.isEmpty(friendName)) {
                String intentFriendName = intent.getStringExtra(Constants.B_NAME);
                friendName = TextUtils.isEmpty(intentFriendName) ? "" : intentFriendName;
            }

            if (TextUtils.isEmpty(isFromNotification)) {
                String intentValueIsFromNotification = intent.getStringExtra("fromNotification");
                isFromNotification = TextUtils.isEmpty(intentValueIsFromNotification) ? "" : intentValueIsFromNotification;
            }

            if (TextUtils.isEmpty(friendChatId)) {
                String intentFriendChatId = intent.getStringExtra(ConstantDB.ChatId);
                friendChatId = TextUtils.isEmpty(intentFriendChatId) ? "" : intentFriendChatId;
            }

            if (TextUtils.isEmpty(friendPhoneNum)) {
                String intentfriendPhoneNum = intent.getStringExtra(ConstantDB.PhoneNo);
                friendPhoneNum = TextUtils.isEmpty(intentfriendPhoneNum) ? "" : intentfriendPhoneNum;
            }

            if (TextUtils.isEmpty(FriendImageLink)) {
                String intentFriendImageLink = intent.getStringExtra(ConstantDB.FriendImageLink);
                FriendImageLink = TextUtils.isEmpty(intentFriendImageLink) ? "" : intentFriendImageLink;
            }
        } else if (chatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            mGroupId = intent.getStringExtra(Constants.B_ID);
            if (TextUtils.isEmpty(mGroupId)) {
                return false;
            }
            mChatType = ConstantChat.TYPE_GROUPCHAT;


        }
        if (TextUtils.isEmpty(isFromNotification)) {
            String intentValueIsFromNotification = intent.getStringExtra("fromNotification");
            isFromNotification = TextUtils.isEmpty(intentValueIsFromNotification) ? "" : intentValueIsFromNotification;
        }
        return true;
    }

    private void getValueFromDB(Intent intent) {
        mDataProfile = mTableUserInfo.getUser();
//        myPhoneNum = mDataProfile.getPhoneNo();
        myPhoneNum = mDataProfile.getCountryCode() + mDataProfile.getPhoneNo();
        myChatId = mDataProfile.getChatId();

        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mDataContact = mTableContact.getFriendDetailsByFrienChatID(friendChatId);
            mListChats = mTableChat.getAllChat(friendChatId);
            int myChatCount = mListChats != null ? mListChats.size() : 0;
            LogUtils.d(LOG_TAG
                    + " : getValueFromDB", "mDataContact"
                    + mDataContact != null ? "!null" : "=null");

            if (mDataContact == null) {
                //contact doesnt exist in db. Then it may have come from search
                DataContact contactFromIntent = null;
                if (intent != null) {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        contactFromIntent = (DataContact) bundle.getSerializable(Constants.B_OBJ);


                    }
                }
                if (contactFromIntent != null) {
                    LogUtils.d(LOG_TAG + " contactFromIntent != null");
                    contactFromIntent.setIsFriend(0);
                    contactFromIntent.setIsRegistered(1);
                    contactFromIntent.setFriendId(friendId);
                    contactFromIntent.setPhoneNumber(friendPhoneNum);
                    contactFromIntent.setChatId(friendChatId);
                    contactFromIntent.setName(friendName);
                    contactFromIntent.setAppName(friendName);
                    mDataContact = contactFromIntent;
                    new TableContact(this).insertBulkForUnknownNUmber(contactFromIntent);
                } else {
                    LogUtils.d(LOG_TAG + " contactFromIntent == null");
                    //THIS case shouldnt happen
                    mDataContact = new DataContact();
                    mDataContact.setPhoneNumber(friendPhoneNum);
                    mDataContact.setChatId(friendChatId);
                    mDataContact.setIsBlocked(0);
                    mDataContact.setIsFriend(1);
                    mDataContact.setIsRegistered(1);
                    mDataContact.setFriendId(friendId);
                    mDataContact.setName(friendName);
                    mDataContact.setAppName(friendName);

                }
            }


            if (!TextUtils.isEmpty(mDataContact.getChatId()))
                friendChatId = mDataContact.getChatId();
            if (!TextUtils.isEmpty(mDataContact.getPhoneNumber()))
                friendPhoneNum = mDataContact.getPhoneNumber();
            if (!TextUtils.isEmpty(mDataContact.getAppName()))
                friendName = mDataContact.getAppName();

            blockStatus = mDataContact.getIsBlocked();


//            if (mDataContact != null) {
//                ValidationUtils.containsXmppDomainName(mDataContact.getChatId());
            if (mDataContact.getIsFriend() == 0) {
                LogUtils.d(LOG_TAG + " : getValueFromDB",
                        "mDataContact.getIsFriend() == 0, myChatCount=" + myChatCount);
                if (TextUtils.isEmpty(mDataContact.getRelation())) {
                    if (myChatCount == 0) {
                        LogUtils.d(LOG_TAG + "first time sticker true");
                        shouldShowFirstTimeChatSendOptionAtTopBar = true;
                    } else {
                        boolean didISendFirstTimeChat = false;
                        for (DataTextChat chatItem : mListChats) {
                            if (chatItem.getSenderId().equals(mPrefs.getUserId())
                                    && chatItem.getMessageType().equals(ConstantChat.FIRST_TIME_STICKER_TYPE)) {
                                didISendFirstTimeChat = true;
                            }
                        }
                        LogUtils.d(LOG_TAG + "didISendFirstTimeChat:" + didISendFirstTimeChat);
                        if (!didISendFirstTimeChat) {
                            if (mDataContact.getIsBlocked() == 1) {
                                LogUtils.d(LOG_TAG + "mDataContact.getIsBlocked() == 1");
                                shouldShowAcceptRejectFooter = false;
                            } else {
                                LogUtils.d(LOG_TAG + "mDataContact.getIsBlocked() != 1");
                                shouldShowAcceptRejectFooter = true;
                            }
                        }
                    }
                }
            } else {
                LogUtils.d(LOG_TAG + " : getValueFromDB", "mDataContact.getIsFriend() != 0, myChatCount="
                        + myChatCount);
                if (TextUtils.isEmpty(mDataContact.getRelation())) {
                    if (myChatCount == 0) {
                        shouldShowFirstTimeChatSendOptionAtTopBar = true;
                    } else {
                        //TODO check if the friend has sent me first time sticker
                        //internally accept it and set the friend status


                        boolean didISendFirstTimeChat = false;
                        for (DataTextChat chatItem : mListChats) {
                            if (chatItem.getSenderId().equals(mPrefs.getUserId())
                                    && chatItem.getMessageType().equals(ConstantChat.FIRST_TIME_STICKER_TYPE)) {
                                didISendFirstTimeChat = true;
                            }
                        }
                        LogUtils.d(LOG_TAG + "didISendFirstTimeChat:" + didISendFirstTimeChat);
                        if (!didISendFirstTimeChat) {
                            if (mDataContact.getIsBlocked() != 1) {
                                LogUtils.d(LOG_TAG + "mDataContact.getIsBlocked() != 1");
                                //api call and send an internal sticker acceptance message to the friend
                                handleFirstTimeStickerMessageForContactsAlreadySaved(mDataContact, false);
                            }
                        }
                    }
                }
            }


        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            TableGroup tableGroup = new TableGroup(this);
            mDataGroup = tableGroup.getGroupById(mGroupId);

            mListChats = mTableChat.getAllGroupChat(mGroupId);

            DataGroupMembers member = tableGroup.getMemberInAGroup(mDataGroup.getGroupId(), mDataProfile.getUserId());

            if (member != null) {
                blockStatus = Integer.parseInt(member.getIsGrpblock());
            }

        }
        getValueFromDbCalled = "getValueFromDbCalled";

        if (shouldShowFirstTimeChatSendOptionAtTopBar) {
//            ToastUtils.showAlertToast(mActivity,
//                    "Please select your relation with this person", ToastType.FAILURE_ALERT);
//            openFirstTimeChatOptionWindowAfterAFewSecond(2000);
        } else {
            if (popUpWindow != null) {
                popUpWindow.dismiss();
            }
        }

    }

    private void showAcceptRejectView() {
        //TODO
    }

    /**
     * Used to Initilize the Components and Adapters and Adding Scroll Gesture
     */
    private void initComponent() {

        if (options == null)
            options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                    .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                    .cacheOnDisk(true)
                    .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                    .considerExifParams(true).build();
        mImageLoader = ImageLoader.getInstance();
//        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));

        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            if (mDataContact != null && !TextUtils.isEmpty(mDataContact.getAppFriendImageLink())) {
                imagePath = mDataContact.getAppFriendImageLink();

            } else {
                imagePath = FriendImageLink;
            }
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            imagePath = mDataGroup.getGroupImage();
        }
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();
        setTopBarValue();
        if (mLinearLayoutManager == null) {
            mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recycler_view_common.setLayoutManager(mLinearLayoutManager);
            recycler_view_common.setHasFixedSize(false);
        }
        if (mScrollGesture == null)
            mScrollGesture = new ScrollGesture(lnr_Section, tvSection, mLinearLayoutManager);
        String friendOrGroupChatID = "";
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            friendOrGroupChatID = friendChatId;
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            friendOrGroupChatID = mGroupId;
        }
        if (mAdapter == null) {

            mAdapter = new AdapterChat(mListChats, mChatType, friendOrGroupChatID, myChatId,
                    this, this, new Cell.OnCellItemLongClickListener() {

                @Override
                public void onCellItemLongClick(View v, final DataTextChat dataItem) {
                    TooltipPopUpWindow filterMenuWindow = new TooltipPopUpWindow(ActivityChat.this,
                            new TooltipItemClickListener() {
                                @Override
                                public void onTooltipItemClick(int which) {
                                    switch (which) {
                                        case R.id.menu_tooltip_copy:
                                            String msg = "";
                                            if (TextUtils.isEmpty(dataItem.getStrTranslatedText())) {
                                                msg = CommonMethods.getUTFDecodedString(dataItem.getBody());
                                            } else {
                                                msg = dataItem.getStrTranslatedText();
                                            }
                                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                                                android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                                                        getSystemService(Context.CLIPBOARD_SERVICE);
                                                clipboard.setText(msg);
                                            } else {
                                                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                                                        getSystemService(Context.CLIPBOARD_SERVICE);
                                                android.content.ClipData clip = android.content.ClipData.newPlainText("Message", msg);
                                                clipboard.setPrimaryClip(clip);
                                            }

                                            CommonMethods.MYToast(ActivityChat.this, "Text copied to Clipboard");
                                            break;
                                        case R.id.menu_tooltip_delete:
                                            confirmDeleteChatItem(dataItem);
                                            break;
                                        case R.id.menu_tooltip_cancel:
                                            break;
                                    }
                                }
                            }, null, dataItem);
                    filterMenuWindow.showAsDropDown(v, 0, 0);// showing
//                 Gravity.CENTER,
                }
            }, shouldShowAcceptRejectFooter);

        } else {
            mAdapter.constructorData(mChatType, friendOrGroupChatID, myChatId, shouldShowAcceptRejectFooter);
            mAdapter.setData(mListChats);
        }
        List<AdapterChatSection.Section> sections = new ArrayList<AdapterChatSection.Section>();
        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mArrDataTextChats = mTableChat.getChatSections(friendChatId);
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            mArrDataTextChats = mTableChat.getGroupChatSections(mGroupId);
        }
        int sectionPosition = 0;
        for (int i = 0; i < mArrDataTextChats.size(); i++) {
            if (i == 0) {
                sectionPosition = i;
            } else {
                sectionPosition = sectionPosition + (Integer.parseInt(mArrDataTextChats.get(i - 1).getSectionCount()));
            }

            long thenTime = 0;
            String dayDiff = "";
            try {
                thenTime = Long.parseLong(DateTimeUtil.
                        convertFormattedDateToMillisecond(DateTimeUtil.
                                getFormattedDate(mArrDataTextChats.get(i).getSectionTime(), "MM-dd-yyyy HH:mm:ss"
                                        , "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (DateUtils.isToday(thenTime)) {
                dayDiff = "Today";
            } else {

            }

            String sectionDateString = mArrDataTextChats.get(i).getSectionTime();

            sectionDateString = DateTimeUtil.convertUtcToLocal(sectionDateString, "MM-dd-yyyy HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss",
                    getResources().getConfiguration().locale);

            String headerTimeFormatted = "";
            if (TextUtils.isEmpty(dayDiff)) {
                String date_time = DateTimeUtil.getFormattedDate(sectionDateString, "yyyy-MM-dd HH:mm:ss"
                        , "MMMM d, yyyy hh:mm a");
                String[] dateTimeArray = StringUtils.split(date_time, " ");
                if (dateTimeArray != null && dateTimeArray.length >= 5) {
                    String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                    headerTimeFormatted = date;
                }

            } else {
                //if today show time only
                if (StringUtils.equalsIgnoreCase(dayDiff, "Today")) {
                    headerTimeFormatted = dayDiff;
                } else {
                    String date_time = DateTimeUtil.getFormattedDate(sectionDateString, "yyyy-MM-dd HH:mm:ss"
                            , "MMMM d, yyyy hh:mm a");
                    String[] dateTimeArray = StringUtils.split(date_time, " ");
                    if (dateTimeArray != null && dateTimeArray.length >= 5) {
                        String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                        headerTimeFormatted = date;
                    }
                }
            }
            sections.add(new AdapterChatSection.Section(sectionPosition, headerTimeFormatted));
        }

        AdapterChatSection.Section[] dummy = new AdapterChatSection.Section[sections.size()];
        if (mSectionedAdapter == null) {
            mSectionedAdapter = new
                    AdapterChatSection(this, R.layout.section_chat, R.id.tvSection, mAdapter);
            recycler_view_common.setAdapter(mSectionedAdapter);
            recycler_view_common.setOnScrollListener(mScrollGesture);
            recycler_view_common.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(
                    R.drawable.d_line_horizontal), false, false));
        }
        mSectionedAdapter.setSections(sections.toArray(dummy));


        recycler_view_common.requestFocus();
        recycler_view_common.post(new Runnable() {
            @Override
            public void run() {
                mScrollGesture.onScrollStateChanged(recycler_view_common, RecyclerView.SCROLL_STATE_IDLE);
                mLinearLayoutManager.scrollToPosition(mSectionedAdapter.getItemCount() - 1);
            }
        });

        loadTopBarProfileImage();

    }

    /**
     * Used To initialize the Views
     */
    private void initViews() {
        emojicons = (RelativeLayout) findViewById(R.id.emojicons);

        rel_bottom = (RelativeLayout) findViewById(R.id.rel_bottom);
        lnr_Section = (LinearLayout) findViewById(R.id.lnr_Section);
        lnr_Section.setVisibility(View.INVISIBLE);
        tvSection = (TextView) findViewById(R.id.tvSection);
        lnr_Share = (LinearLayout) findViewById(R.id.lnr_Share);

        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        rl_camera = findViewById(R.id.rl_camera);
        rl_camera.setOnClickListener(this);

        iv_smiley = (ImageView) findViewById(R.id.iv_smiley);
        rl_smiley = findViewById(R.id.rl_smiley);
        rl_smiley.setOnClickListener(this);

        et_chatText = (EmojiconEditText) findViewById(R.id.et_chatText);
        et_chatText.addTextChangedListener(this);
        et_chatText.setOnClickListener(this);
        view = findViewById(R.id.view);
        view.setVisibility(View.GONE);
        view.setOnClickListener(this);
        recycler_view_common = (RecyclerView) findViewById(R.id.rv);

        frm_container = (RelativeLayout) findViewById(R.id.frm_container);

        if (mHelperChat == null)
            mHelperChat = new HelperChat(this, this);

    }

    private void deleteChatItem(DataTextChat dataItem) {
        mTableChat.deleteChatItem(dataItem);

        mAdapter.refreshChatList(shouldShowAcceptRejectFooter);


        List<AdapterChatSection.Section> sections =
                new ArrayList<AdapterChatSection.Section>();
        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();

        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mArrDataTextChats = mTableChat.getChatSections(friendChatId);
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            mArrDataTextChats = mTableChat.getGroupChatSections(mGroupId);
        }
        int sectionPosition = 0;
        for (int i = 0; i < mArrDataTextChats.size(); i++) {
            if (i == 0) {
                sectionPosition = i;
            } else {
                sectionPosition = sectionPosition + (Integer.parseInt(mArrDataTextChats.get(i - 1).getSectionCount()));
            }
            long thenTime = 0;
            String dayDiff = "";
            try {
                thenTime = Long.parseLong(DateTimeUtil.
                        convertFormattedDateToMillisecond(DateTimeUtil.
                                getFormattedDate(mArrDataTextChats.get(i).getSectionTime(), "MM-dd-yyyy HH:mm:ss"
                                        , "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (DateUtils.isToday(thenTime)) {
                dayDiff = "Today";
            } else {

            }

            String sectionDateString = mArrDataTextChats.get(i).getSectionTime();

            sectionDateString = DateTimeUtil.convertUtcToLocal(sectionDateString, "MM-dd-yyyy HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss",
                    getResources().getConfiguration().locale);

            String headerTimeFormatted = "";
            if (TextUtils.isEmpty(dayDiff)) {
                String date_time = DateTimeUtil.getFormattedDate(sectionDateString, "yyyy-MM-dd HH:mm:ss"
                        , "MMMM d, yyyy hh:mm a");
                String[] dateTimeArray = StringUtils.split(date_time, " ");
                if (dateTimeArray != null && dateTimeArray.length >= 5) {
                    String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                    headerTimeFormatted = date;
                }

            } else {
                //if today show time only
                if (StringUtils.equalsIgnoreCase(dayDiff, "Today")) {
                    headerTimeFormatted = dayDiff;
                } else {
                    String date_time = DateTimeUtil.getFormattedDate(sectionDateString, "yyyy-MM-dd HH:mm:ss"
                            , "MMMM d, yyyy hh:mm a");
                    String[] dateTimeArray = StringUtils.split(date_time, " ");
                    if (dateTimeArray != null && dateTimeArray.length >= 5) {
                        String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                        headerTimeFormatted = date;
                    }
                }
            }
            sections.add(new AdapterChatSection.Section(sectionPosition, headerTimeFormatted));
        }

     /* sections.add(new AdapterChatSection.Section(5, "Section 2"));
        sections.add(new AdapterChatSection.Section(12, "Section 3"));
        sections.add(new AdapterChatSection.Section(14, "Section 4"));
        sections.add(new AdapterChatSection.Section(20, "Section 5"));*/

        AdapterChatSection.Section[] dummy = new AdapterChatSection.Section[sections.size()];
        mSectionedAdapter.setSections(sections.toArray(dummy));


//        mAdapter.AddNewMessageInUI(mdDataTextChat);
//
//        recycler_view_common.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                recycler_view_common.scrollToPosition(recycler_view_common.getAdapter().getItemCount() - 1);
//            }
//        }, 500);


    }

    private String getChatTypeForDelete(DataTextChat chatItem) {
        if (chatItem != null) {
            if (chatItem.getMessageType().equalsIgnoreCase(ConstantChat.MESSAGE_TYPE)) {
                return "message";
            } else if (chatItem.getMessageType().equalsIgnoreCase(ConstantChat.IMAGE_TYPE)) {
                return "image";
            } else if (chatItem.getMessageType().equalsIgnoreCase(ConstantChat.IMAGECAPTION_TYPE)) {
                return "image";
            } else if (chatItem.getMessageType().equalsIgnoreCase(ConstantChat.SKETCH_TYPE)) {
                return "sketch";
            } else if (chatItem.getMessageType().equalsIgnoreCase(ConstantChat.CONTACT_TYPE)) {
                return "shared contact";
            } else if (chatItem.getMessageType().equalsIgnoreCase(ConstantChat.LOCATION_TYPE)) {
                return "shared location";
            } else if (chatItem.getMessageType().equalsIgnoreCase(ConstantChat.VIDEO_TYPE)) {
                return "video";
            } else if (chatItem.getMessageType().equalsIgnoreCase(ConstantChat.YOUTUBE_TYPE)) {
                return "shared youtube video";
            } else if (chatItem.getMessageType().equalsIgnoreCase(ConstantChat.YAHOO_TYPE)) {
                return "shared yahoo news";
            }
        }
        return "message";
    }

    private void confirmDeleteChatItem(final DataTextChat dataItem) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
            builder.setMessage(String.format(getString(R.string.alert_delete_chat_item), getChatTypeForDelete(dataItem)));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteChatItem(dataItem);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCellItemClick(int viewId, final DataTextChat dataItem) {
        switch (viewId) {
            case R.id.cell_photo_download:

                AppVhortex.getInstance().addToDownloadQueueMap(dataItem);
                notifyChatListUI(dataItem);

                AppVhortex.getInstance().onFileDownloadForChat(dataItem);
                break;
            case R.id.cell_photo_cross:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = imageUploadHandler.obtainMessage();
                        message.what = 2;
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable(Constants.B_RESULT, dataItem);
                        message.setData(mBundle);
                        imageUploadHandler.sendMessage(message);
                    }
                }).start();

                break;
            case R.id.cell_photo_retry:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = imageUploadHandler.obtainMessage();
                        message.what = 3;
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable(Constants.B_RESULT, dataItem);
                        message.setData(mBundle);
                        imageUploadHandler.sendMessage(message);
                    }
                }).start();
                break;

            case R.id.cellPhoto:
                if (!TextUtils.isEmpty(dataItem.getFilePath())) {
//                    if (dataItem.getMaskEnabled().equalsIgnoreCase("0")) {
                    Intent mIntent = new Intent(this, ActivityPinchToZoom.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString(Constants.B_RESULT, dataItem.getFilePath());
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
//                    }
                }
                break;
            case R.id.cell_photo_mask:
                //done mask and unmask;
                if (dataItem.getMaskEnabled().equalsIgnoreCase("1")) {
                    dataItem.setMaskEnabled("0");
                    mTableChat.updateFileChatMaskEnabled(dataItem);

                } else {
                    dataItem.setMaskEnabled("1");
                    mTableChat.updateFileChatMaskEnabled(dataItem);
                }
                notifyChatListUI(dataItem);
                break;

            case R.id.cell_video_download:

                dataItem.setDownloadId(AppVhortex.getInstance().addDownloadRequest(dataItem));
                notifyChatListUI(dataItem);
                break;

            case R.id.cell_video_cross:
                if (dataItem.getSenderChatID().equalsIgnoreCase(myChatId)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = videoUploadHandler.obtainMessage();
                            //its an upload scenario
                            message.what = 2;
                            Bundle mBundle = new Bundle();
                            mBundle.putSerializable(Constants.B_RESULT, dataItem);
                            message.setData(mBundle);
                            videoUploadHandler.sendMessage(message);
                        }
                    }).start();
                } else {
                    //its a download scenario
                    dataItem.setFriendPhoneNo(friendPhoneNum);
                    int result = AppVhortex.getInstance().cancelDownloadRequest(dataItem);
                    if (result == 1) {
                        AppVhortex.getInstance().removeFromDownloadQueueMap(dataItem);
                        notifyChatListUI(dataItem);
                    } else {

                    }
                }
                break;

            case R.id.cell_video_play:
                if (!TextUtils.isEmpty(dataItem.getFilePath())) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            File file = new File(dataItem.getFilePath());
                            intent.setDataAndType(Uri.fromFile(file), "video/*");
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtils.showAlertToast(this, getString(R.string.alert_failure_video_path_empty), ToastType.FAILURE_ALERT);
                        }
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        ToastUtils.showAlertToast(this, getString(R.string.alert_failure_video_player_not_found), ToastType.FAILURE_ALERT);
                    }
                } else {
                    ToastUtils.showAlertToast(this, getString(R.string.alert_failure_video_path_empty), ToastType.FAILURE_ALERT);
                }
                break;

            case R.id.cell_video_retry:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = imageUploadHandler.obtainMessage();
                        message.what = 3;
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable(Constants.B_RESULT, dataItem);
                        message.setData(mBundle);
                        videoUploadHandler.sendMessage(message);
                    }
                }).start();
                break;
            case R.id.cell_youtube_video:

                try {
                    Intent intent = new Intent(this, ActivityYoutubePlayer.class);
                    intent.putExtra("youtube_video_id", dataItem.getYoutubeVideoId());
                    intent.putExtra("youtube_video_title", dataItem.getYoutubeTitle());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showAlertToast(this, getString(R.string.alert_failure_video_player_not_found), ToastType.FAILURE_ALERT);
                }
                break;
            case R.id.cell_yahoo_news:
                try {
                    Intent yahooClient = new Intent(Intent.ACTION_VIEW);
                    yahooClient.setData(Uri.parse(dataItem.getYahooShareUrl()));
                    startActivityForResult(yahooClient, 1234);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showAlertToast(this, getString(R.string.alert_failure_yahoo_not_found), ToastType.FAILURE_ALERT);
                }

                break;

            case R.id.tv_cell_footer_accept:
                acceptTheUnknownFriend();

                break;
            case R.id.tv_cell_footer_reject:
                rejectTheUnknownFriend();

                break;
            default:

                break;
        }
    }

    private void rejectTheUnknownFriend() {
        if (!CommonMethods.isOnline(this)) {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
            return;
        }
        AsyncBlockUnknownNumber mAsyncBlockUnknownUser = new AsyncBlockUnknownNumber(mPrefs.getUserId(),
                mDataContact.getChatId(), "0",
                new WebServiceCallBack<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse result) {

                        onUnknownUserBlockSuccess(result);

                    }

                    @Override
                    public void onFailure(BaseResponse result) {
                        onUnknownUserBlockFailure(result.getResponseDetails());
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        onUnknownUserBlockFailure(CommonMethods.getAlertMessageFromException(ActivityChat.this, e));
                    }
                });
        showPageFilterProgress(true);

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncBlockUnknownUser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncBlockUnknownUser.execute();
        }

    }

    private void onUnknownUserBlockFailure(String failureAlert) {
        showPageFilterProgress(false);
        ToastUtils.showAlertToast(this, TextUtils.isEmpty(failureAlert) ? getString(R.string.alert_failure_unknown_error) : failureAlert, ToastType.FAILURE_ALERT);
    }

    private void onUnknownUserBlockSuccess(BaseResponse result) {
        ToastUtils.showAlertToast(this,
                "Successfully blocked this person", ToastType.FAILURE_ALERT);
        if (mDataContact != null) {
            mDataContact.setIsBlocked(1);
            mTableContact.updateBlock(mDataContact);
        }
        blockStatus = 1;
        showPageFilterProgress(false);
        shouldShowAcceptRejectFooter = false;
        mAdapter.refreshChatList(shouldShowAcceptRejectFooter);
    }

    private void acceptTheUnknownFriend() {

        handleFirstTimeStickerMessageForContactsAlreadySaved(mDataContact, true);

    }

    public String getFirstTimeFriendStatus() {
        if (mAdapter != null) {
            if (mAdapter.getData() != null && mAdapter.getData().size() > 0) {
                for (DataTextChat item : mAdapter.getData()) {
                    if (item.getMessageType().equalsIgnoreCase(ConstantChat.FIRST_TIME_STICKER_TYPE)) {
                        if (TextUtils.isEmpty(item.getFilePath())) {
                            return FirstTimeChatOptionsPopUpWindow.FIRST_TIME_CHAT_OPTION_FRIEND;
                        }

                        return item.getFilePath();
                    }
                }
            }
        }
        return FirstTimeChatOptionsPopUpWindow.FIRST_TIME_CHAT_OPTION_FRIEND;
    }

    private void handleFirstTimeStickerMessageForContactsAlreadySaved(final DataContact mDataContact,
                                                                      final boolean openDeviceContactToSaveThisNumber) {
        if (!CommonMethods.isOnline(this)) {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
            return;
        }
        AsyncSetFriendStatus asyncSetFriendStatus = new AsyncSetFriendStatus(mDataProfile.getUserId(),
                mDataContact.getFriendId(), getFirstTimeFriendStatus(), new WebServiceCallBack<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse result) {
                showPageFilterProgress(false);
                onSetFriendStatusApiCallSuccess(result, mDataContact, openDeviceContactToSaveThisNumber);

            }

            @Override
            public void onFailure(BaseResponse result) {
                showPageFilterProgress(false);
                onSetFriendStatusApiCallFailed(result.getResponseDetails(), mDataContact);
            }

            @Override
            public void onFailure(Throwable e) {
                showPageFilterProgress(false);
                onSetFriendStatusApiCallFailed(CommonMethods.getAlertMessageFromException(getBaseContext(), e), mDataContact);
            }
        });

        if (openDeviceContactToSaveThisNumber) {
            showPageFilterProgress(true);
        }
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            asyncSetFriendStatus.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncSetFriendStatus.execute();
        }
    }

    private void onSetFriendStatusApiCallSuccess(BaseResponse result, final DataContact mDataContact,
                                                 final boolean openDeviceContactToSaveThisNumber) {
        if (mBound) {
            Message chatDeliveryMessage = Message.obtain();
            chatDeliveryMessage.what = ConstantXMPP.SEND_FIRST_TIME_STICKER_ACCEPT_MESSAGE;
            String firstTimeStickerAcceptMessageJSON = OneToOneChatJSonCreator.
                    getFirstTimeStickerAcceptMessageJSON(mDataProfile.getCountryCode()
                                    + mDataProfile.getPhoneNo(),
                            mDataProfile.getUsername());

            //make bundle
            DataTextChat mDataTextChat = new DataTextChat();
            mDataTextChat.setFriendChatID(friendChatId);
            Bundle mBundle = new Bundle();
            mBundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, firstTimeStickerAcceptMessageJSON);
            mBundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
            chatDeliveryMessage.setData(mBundle);
            try {
                messengerConnection.send(chatDeliveryMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        if (openDeviceContactToSaveThisNumber) {

            if (mDataContact != null) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME,
                        CommonMethods.getUTFDecodedString(mDataContact.getAppName()));
                //gupi for phone number as it is coming without country code
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, "+" + mDataContact.getPhoneNumber());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    intent.putExtra("finishActivityOnSaveCompleted", true);
                }
                startActivityForResult(intent, REQUEST_ADD_TO_CONTACT);
            }
        }
    }

    private void onSetFriendStatusApiCallFailed(String failureAlert, final DataContact mDataContact) {
        ToastUtils.showAlertToast(this, TextUtils.isEmpty(failureAlert) ? getResources().getString(R.string.alert_failure_unknown_error) : failureAlert, ToastType.FAILURE_ALERT);
    }

    private void showPageFilterProgress(boolean show) {
        if (show) {

            if (mPageFilterDialog == null) {
                initPageFilterProgressDialog();
            }
            mPageFilterDialog.show();
        } else {
            if (mPageFilterDialog != null && mPageFilterDialog.isShowing()) {
                mPageFilterDialog.dismiss();
            }
        }
    }

    private void initPageFilterProgressDialog() {
        mPageFilterDialog = new ProgressDialog(this);
        mPageFilterDialog.setMessage(getString(R.string.please_wait));
        mPageFilterDialog.setCanceledOnTouchOutside(false);
        mPageFilterDialog.setCancelable(false);
    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String search) {
        Intent mIntent = new Intent(this, ActivityFindPeople.class);
        LogUtils.i(LOG_TAG, search);
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.B_RESULT, search);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }

    @Override
    protected void CommonMenuClcik() {
        if (lnr_Share.getVisibility() == View.VISIBLE) {
            lnr_Share.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        } else {
            CommonMethods.hideSoftKeyboard(this);
            lnr_Share.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onGlobeIconClick(String str, MenuItem item) {

        if (str.equalsIgnoreCase("1")) {
            item.setIcon(R.drawable.ic_chats_translator_icon_deselect);
            callISTranslatedtWS("0");
            ToastUtils.showAlertToast(this, getResources().getString(R.string.translation_off), ToastType.SUCESS_ALERT);
        } else {
            item.setIcon(R.drawable.ic_chats_translator_icon_select);

            callISTranslatedtWS("1");
            ToastUtils.showAlertToast(this, getResources().getString(R.string.translation_on), ToastType.SUCESS_ALERT);
        }

    }

    private void callISTranslatedtWS(String status) {

        mDataProfile = new TableUserInfo(this).getUser();
        userId = mDataProfile.getUserId();
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.IsTranslated, status);
        mTableUserInfo.updateStatusForLocationContactAndTranslate(mContentValues, userId);

        AsyncForUserTranslate mAsyncForUserTranslate =
                new AsyncForUserTranslate(userId, status, this);

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncForUserTranslate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncForUserTranslate.execute();
        }


    }

    @Override
    public void onContactSelect() {
        if (!checkIfGroupStillValid()) {
            showGroupRemovedAlert();
            return;
        }
        if (blockStatus != 0) {
            String blockAlert = "";
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                blockAlert = "You have been blocked in this group";
            } else {
                blockAlert = getResources().getString(R.string.toast_block_user);
            }
            ToastUtils.showAlertToast(this, blockAlert, ToastType.FAILURE_ALERT);
            return;
        }

        startActivityForResult(new Intent(this, ActivityContactDetails.class), Constants.ContactPickerChat);
        lnr_Share.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onSketchSelect() {
        if (!checkIfGroupStillValid()) {
            showGroupRemovedAlert();
            return;
        }
        if (blockStatus != 0) {
            String blockAlert = "";
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                blockAlert = "You have been blocked in this group";
            } else {
                blockAlert = getResources().getString(R.string.toast_block_user);
            }
            ToastUtils.showAlertToast(this, blockAlert, ToastType.FAILURE_ALERT);
            return;
        }

        Intent mInt = new Intent(this, ActivitySketch.class);
        startActivityForResult(mInt, Constants.SketchToSelection);
        lnr_Share.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onNewsSelect() {
        if (!checkIfGroupStillValid()) {
            showGroupRemovedAlert();
            return;
        }
        if (blockStatus != 0) {
            String blockAlert = "";
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                blockAlert = "You have been blocked in this group";
            } else {
                blockAlert = getResources().getString(R.string.toast_block_user);
            }
            ToastUtils.showAlertToast(this, blockAlert, ToastType.FAILURE_ALERT);
            return;
        }
        Intent mIn = new Intent(this, ActivityYahooNews.class);
        startActivityForResult(mIn, Constants.YahooNewsPicker);
        lnr_Share.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onLocationSelect() {
        if (!checkIfGroupStillValid()) {
            showGroupRemovedAlert();
            return;
        }
        if (blockStatus != 0) {
            String blockAlert = "";
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                blockAlert = "You have been blocked in this group";
            } else {
                blockAlert = getResources().getString(R.string.toast_block_user);
            }
            ToastUtils.showAlertToast(this, blockAlert, ToastType.FAILURE_ALERT);
            return;
        }
      /*  Intent mIn = new Intent(mActivity, ActivityMap.class);
        startActivityForResult(mIn, Constants.LocationPikerChat);
        lnr_Share.setVisibility(View.GONE);
        view.setVisibility(View.GONE);*/
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(ActivityChat.this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, Constants.LocationPikerChat);
            lnr_Share.setVisibility(View.GONE);
            view.setVisibility(View.GONE);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
            LogUtils.d(LOG_TAG, e);
            ToastUtils.showAlertToast(this, "Failed to fetch the location", ToastType.FAILURE_ALERT);
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
            LogUtils.d(LOG_TAG, e);
            ToastUtils.showAlertToast(this, "Failed to fetch the location", ToastType.FAILURE_ALERT);
        }
    }

    @Override
    public void onVideoSelect() {
        if (!checkIfGroupStillValid()) {
            showGroupRemovedAlert();
            return;
        }
        if (blockStatus != 0) {
            String blockAlert = "";
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                blockAlert = "You have been blocked in this group";
            } else {
                blockAlert = getResources().getString(R.string.toast_block_user);
            }
            ToastUtils.showAlertToast(this, blockAlert, ToastType.FAILURE_ALERT);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.VideoPickerChat);
        lnr_Share.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onGallerySelect() {
        if (!checkIfGroupStillValid()) {
            showGroupRemovedAlert();
            return;
        }
        if (blockStatus != 0) {
            String blockAlert = "";
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                blockAlert = "You have been blocked in this group";
            } else {
                blockAlert = getResources().getString(R.string.toast_block_user);
            }
            ToastUtils.showAlertToast(this, blockAlert, ToastType.FAILURE_ALERT);
            return;
        }
        if (ActivityGallery.arrGallerySetGets_AllImages != null) {
            ActivityGallery.arrGallerySetGets_AllImages = null;
        }
        Intent mIntent = new Intent(this, ActivityGallery.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.B_TYPE, Constants.SELECTION.CHAT_TO_IMAGE);
        mIntent.putExtras(mBundle);
        startActivityForResult(mIntent, Constants.ImagePickerChat);
        lnr_Share.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onYouTubeSelect() {
        if (!checkIfGroupStillValid()) {
            showGroupRemovedAlert();
            return;
        }
        if (blockStatus != 0) {
            String blockAlert = "";
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                blockAlert = "You have been blocked in this group";
            } else {
                blockAlert = getResources().getString(R.string.toast_block_user);
            }
            ToastUtils.showAlertToast(this, blockAlert, ToastType.FAILURE_ALERT);
            return;
        }
        Intent mIntent = new Intent(this, ActivityYouTubeVideoList.class);
        Bundle mBundle = new Bundle();
        startActivityForResult(mIntent, Constants.YouTubeVideoPicker);
        lnr_Share.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onCameraSelect() {
        if (!checkIfGroupStillValid()) {
            showGroupRemovedAlert();
            return;
        }
        if (blockStatus != 0) {
            String blockAlert = "";
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                blockAlert = "You have been blocked in this group";
            } else {
                blockAlert = getResources().getString(R.string.toast_block_user);
            }
            ToastUtils.showAlertToast(this, blockAlert, ToastType.FAILURE_ALERT);
            return;
        }
        /**
         * use the baseActivity camera picker code
         */
        startImageIntentCamra();
        lnr_Share.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    @Override
    protected void onProfileClick(int id) {
        super.onProfileClick(id);
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            Intent mIntent = new Intent(this, ActivityFriendProfile.class);

            Bundle mBundle = new Bundle();
            if (mDataContact != null) {
                mBundle.putSerializable(Constants.B_OBJ, mDataContact);
                mBundle.putString(Constants.B_ID, mDataContact.getFriendId());
                mBundle.putString(Constants.B_RESULT, mDataContact.getChatId());
            } else {
                mBundle.putString(Constants.B_ID, friendId);
                mBundle.putString(Constants.B_RESULT, friendChatId);
            }
            mIntent.putExtras(mBundle);
            startActivity(mIntent);
            lnr_Share.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {

            Intent mIntent = new Intent(this, ActivityViewGroupDetails.class);
            mIntent.putExtra(Constants.B_RESULT, mDataGroup);
            startActivity(mIntent);

            lnr_Share.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onMenuProfileImageClick() {
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            Intent mIntent = new Intent(this, ActivityFriendProfile.class);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(Constants.B_OBJ, mDataContact);
            mBundle.putString(Constants.B_ID, mDataContact.getFriendId());
            mBundle.putString(Constants.B_RESULT, mDataContact.getChatId());
            mIntent.putExtras(mBundle);
            startActivity(mIntent);
            lnr_Share.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {

            Intent mIntent = new Intent(this, ActivityViewGroupDetails.class);
            mIntent.putExtra(Constants.B_RESULT, mDataGroup);
            startActivity(mIntent);

            lnr_Share.setVisibility(View.GONE);
            view.setVisibility(View.GONE);

        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(et_chatText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View view) {
        EmojiconsFragment.backspace(et_chatText);
    }

    private void setTopBarValue() {
        lnr_title_subtitle.setVisibility(View.VISIBLE);
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            if (TextUtils.isEmpty(friendName)) {
                if (mDataContact != null) {
                    friendName = (mDataContact.getAppName().equals("")) ?
                            ((mDataContact.getName().equals("")) ? mDataContact.getPhoneNumber() :
                                    mDataContact.getName()) : mDataContact.getAppName();
                }
            }
            if (TextUtils.isEmpty(friendName))
                tv_title.setText("+" + friendPhoneNum);
            else
                tv_title.setText(CommonMethods.getUTFDecodedString(friendName));
            tv_subTitle.setText("");
            tv_subTitle.setVisibility(View.GONE);
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {


            String groupName = mDataGroup.getGroupName();
            if (TextUtils.isEmpty(groupName)) {
                tv_title.setText(mDataGroup.getGroupId());
            } else {
                tv_title.setText(CommonMethods.getUTFDecodedString(groupName));
            }

            ArrayList<String> mArrStr = new ArrayList<String>();
            for (int i = 0; i < mDataGroup.getMemberdetails().size(); i++) {
                String nameOrPhone = "";
                if (mDataGroup.getMemberdetails().get(i).getUserId().equals(mPrefs.getUserId())) {
                    nameOrPhone = "You";
                } else {
                    String name = mTableContact.getFriendDetailsByID(mDataGroup.getMemberdetails().get(i).getUserId()).getName();
                    if (TextUtils.isEmpty(mDataGroup.getMemberdetails().get(i).getUserName())) {
                        if (TextUtils.isEmpty(name)) {
                            nameOrPhone = "+" + mDataGroup.getMemberdetails().get(i).getUserCountryId()
                                    + mDataGroup.getMemberdetails().get(i).getUserPhNo();
                        } else
                            nameOrPhone = name;
                    } else
                        nameOrPhone = mDataGroup.getMemberdetails().get(i).getUserName();
                }
                mArrStr.add(nameOrPhone);

            }
            groupMemberName = CommonMethods.commaSeperatedsStringsFromArray(mArrStr);
            tv_subTitle.setText(groupMemberName);
            tv_subTitle.setVisibility(View.VISIBLE);

        }

    }

    private void loadTopBarProfileImage() {
        mImageLoader.loadImage(imagePath, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                LogUtils.i(LOG_TAG, "Topbar image load:onLoadingComplete");
                if (loadedImage != null) {
                    Drawable d = new BitmapDrawable(getResources(),
                            CommonMethods.getCircularBitmap(Bitmap.createScaledBitmap(loadedImage,
                                    getResources().getInteger(R.integer.imageWH),
                                    getResources().getInteger(R.integer.imageWH), true)));
                    if (item_right_image != null) {
                        item_right_image.setIcon(d);
                    }
                } else {
                    if (item_right_image != null) {
                        item_right_image.setIcon(getResources().getDrawable(R.drawable.ic_chats_noimage_profile));
                    }
                    LogUtils.i(LOG_TAG, "Topbar image load:onLoadingComplete: bitmap = null");
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        LogUtils.i(LOG_TAG, "Topbar image load:" + imagePath);

        loadTopBarProfileImage();

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            lnr_right.setVisibility(View.GONE);
            lnr_title_subtitle.setVisibility(View.VISIBLE);
            tv_act_name.setVisibility(View.GONE);

        }

        try {
            ShortcutBadger.applyCount(getApplicationContext(), getBadgeCount());
        } catch (Exception e) {
            LogUtils.e(LOG_TAG, e.getLocalizedMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_ADD_TO_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    mDataContact.setIsFriend(1);
                    mDataContact.setRelation(getFirstTimeFriendStatus());
                    mTableContact.insertBulkForUnknownNUmber(mDataContact);
                    shouldShowAcceptRejectFooter = false;
                    mAdapter.refreshChatList(shouldShowAcceptRejectFooter);
                    ToastUtils.showAlertToast(this, "Please wait while the contact is being synced", ToastType.FAILURE_ALERT);
                }
                break;
            case (Constants.ContactPickerChat):
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Bundle mBundle = data.getExtras();
                        ContactSetget mContactSetget = (ContactSetget) mBundle.getSerializable(Constants.B_RESULT);
                        createContact(mContactSetget);
                    }
                }
                break;
            case (Constants.VideoPickerChat):
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            String selectedVideoPath = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), uri);
                            if (TextUtils.isEmpty(selectedVideoPath)) {
                                selectedVideoPath = MediaUtils.getPath(
                                        this, uri);
                            }

                            if (!TextUtils.isEmpty(selectedVideoPath)) {
                                processAndUploadVideo(selectedVideoPath);
                            } else {
                                //TODO failed to fetch video
                                ToastUtils.showAlertToast(this, getString(R.string.alert_failure_video_pick), ToastType.FAILURE_ALERT);

                            }
                        } else {
                            //TODO failed to fetch video
                            ToastUtils.showAlertToast(this, getString(R.string.alert_failure_video_pick), ToastType.FAILURE_ALERT);
                        }
                    } else {
                        //TODO failed to fetch video
                        ToastUtils.showAlertToast(this, getString(R.string.alert_failure_video_pick), ToastType.FAILURE_ALERT);
                    }

                }
                break;
            case Constants.ImagePickerChat:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (data != null) {
                    Bundle mBundle = data.getExtras();
                    if (mBundle != null) {
                        mBundle.putSerializable(Constants.B_TYPE, Constants.SELECTION.CHAT_TO_SELECTION);
                        Intent mIntent = new Intent(this, ActivityShareImage.class);
                        mBundle.putBoolean("isCamera", false);
                        mIntent.putExtras(mBundle);
                        startActivityForResult(mIntent, Constants.ChatToSelection);
                    }
                }
                break;

            case Constants.SketchToSelection:
                if (data != null) {
                    Bundle mBundle = data.getExtras();
                    if (mBundle != null && mBundle.containsKey(Constants.B_RESULT)) {
                        createImageArray((ArrayList<DataShareImage>) mBundle.getSerializable(Constants.B_RESULT));
                    }
                }
                break;
            case Constants.ChatToSelection:
                if (data != null) {
                    Bundle mBundle = data.getExtras();
                    if (mBundle != null && mBundle.containsKey(Constants.B_RESULT)) {
                        createImageArray((ArrayList<DataShareImage>) mBundle.getSerializable(Constants.B_RESULT));
                    }
                }
                break;

            case Constants.ACTION_IMAGE_CAPTURE:
                if (resultCode != RESULT_OK) {
                    return;
                }
                if (outputFileUri == null) {
                    if (data != null) {
                        outputFileUri = data.getData();
                    }

                }
                if (outputFileUri != null) {
                    ImageUtils.normalizeImageForUri(this, outputFileUri);
                    ArrayList<DataShareImage> imageArray = new ArrayList<DataShareImage>();
                    DataShareImage mDataShareImage = new DataShareImage();
                    String path = "";
                    try {
                        path = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), outputFileUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(path)) {
                        ToastUtils.showAlertToast(this, "Failed to process the image. Please try again", ToastType.FAILURE_ALERT);
                        return;
                    }
                    mDataShareImage.setImgUrl(path);

                    Bundle mBundle = new Bundle();
                    if (mBundle != null) {
                        mBundle.putSerializable("cameraImage", mDataShareImage);
                        Intent mIntent = new Intent(this, ActivityShareImage.class);
                        mBundle.putBoolean("isCamera", true);
                        mIntent.putExtras(mBundle);
                        startActivityForResult(mIntent, Constants.ChatToSelection);
                    }
//                    imageArray.add(mDataShareImage);
//                    createImageArray(imageArray);
                } else {
                    ToastUtils.showAlertToast(this, "Failed to process the image. Please try again", ToastType.FAILURE_ALERT);
                    return;
                }

                return;
            //return required here, otherwise the super.onActivityResult() will be called,
            // which handles the edit profile image capture with crop functionality,
            // which is not required here

            case Constants.LocationPikerChat:
                if (resultCode == Activity.RESULT_OK) {
                   /* if (data != null) {
                        Bundle mBundle = data.getExtras();
                        LocationGetSet mLocationGetSet = (LocationGetSet) mBundle.getSerializable(Constants.B_RESULT);
                        // ToastUtils.showAlertToast(mActivity, mLocationGetSet.getAddress(), ToastType.SUCESS_ALERT);
                        createLocation(mLocationGetSet);

                    }*/
//                    modified @21032016
                    if (data != null) {
                        // The user has selected a place. Extract the name and address.
                        final Place place = PlacePicker.getPlace(data, this);

                        final CharSequence name = place.getName();
                        final CharSequence address = place.getAddress();
                        String attributions = PlacePicker.getAttributions(data);
                        if (attributions == null) {
                            attributions = "";
                        }
                        LogUtils.i(LOG_TAG, "share loc" + name + "-:" + address);
                        LocationGetSet mLocationGetSet = new LocationGetSet();
                        mLocationGetSet.setAddress(name.toString() + ", " + address.toString());
                        mLocationGetSet.setLat(String.valueOf(place.getLatLng().latitude));
                        mLocationGetSet.setLong(String.valueOf(place.getLatLng().longitude));
                        createLocation(mLocationGetSet);
                    }
                }
                break;
            case Constants.YouTubeVideoPicker:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Bundle mBundle = data.getExtras();
                        YouTubeVideo selectedYouTubeVideo = (YouTubeVideo) mBundle.getSerializable(Constants.B_RESULT);
                        // ToastUtils.showAlertToast(mActivity, mLocationGetSet.getAddress(), ToastType.SUCESS_ALERT);
                        processShareYoutubeVideo(selectedYouTubeVideo);

                    }
                }
                break;
            case Constants.YahooNewsPicker:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Bundle mBundle = data.getExtras();
                        DataYahooNews selectedYahooNews = (DataYahooNews) mBundle.getSerializable(Constants.B_RESULT);
                        // ToastUtils.showAlertToast(mActivity, mLocationGetSet.getAddress(), ToastType.SUCESS_ALERT);
                        processShareYahooVideo(selectedYahooNews);

                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void processShareYahooVideo(DataYahooNews selectedYahooNews) {
        DataTextChat mDataTextChat = new DataTextChat();
        mDataTextChat.setMessageid(new org.jivesoftware.smack.packet.Message().getStanzaId());
        mDataTextChat.setOnline(ConstantChat.ISONLINE);
        mDataTextChat.setMessageType(ConstantChat.YAHOO_TYPE);
        mDataTextChat.setBody("");
        mDataTextChat.setLang(TextUtils.isEmpty(new TableUserInfo(this).getUser().getLanguageIdentifire()) ? "en" : new TableUserInfo(this).getUser().getLanguageIdentifire());
        mDataTextChat.setSenderChatID(myChatId);
        mDataTextChat.setFriendChatID(friendChatId);
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        mDataTextChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        mDataTextChat.setChattype(mChatType);
        mDataTextChat.setStrGroupID(mGroupId);
        mDataTextChat.setGroupJID(mDataGroup.getGroupJId());
        mDataTextChat.setUserID(mDataProfile.getUserId());
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mDataTextChat.setFriendName(friendName);
            mDataTextChat.setFriendId(friendId);
        }
        mDataTextChat.setSenderId(mDataProfile.getUserId());
        mDataTextChat.setSenderName(mDataProfile.getUsername());


        mDataTextChat.setYahooTitle(selectedYahooNews.getTitle());
        mDataTextChat.setYahooDescription(selectedYahooNews.getDescription());
        mDataTextChat.setYahooImageUrl(selectedYahooNews.getUrl());
        mDataTextChat.setYahooPublishTime(selectedYahooNews.getPubDate());
        mDataTextChat.setYahooShareUrl(selectedYahooNews.getLink());


        sendYahooNewsToXMPP(mDataTextChat);
        mDataTextChat.setFriendPhoneNo(friendPhoneNum);
        insertMessageToDB(mDataTextChat);
        updateChatListUI(mDataTextChat);
    }

    private void sendYahooNewsToXMPP(DataTextChat mDataTextChat) {
        String SendMessageJSon = "";
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        SendMessageJSon = OneToOneChatJSonCreator.yahooNewsToJsonString(mDataTextChat);


        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE;

            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
            bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendMessageJSon);

            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    private void processShareYoutubeVideo(YouTubeVideo selectedYouTubeVideo) {
        DataTextChat mDataTextChat = new DataTextChat();
        mDataTextChat.setMessageid(new org.jivesoftware.smack.packet.Message().getStanzaId());
        mDataTextChat.setOnline(ConstantChat.ISONLINE);
        mDataTextChat.setMessageType(ConstantChat.YOUTUBE_TYPE);
        mDataTextChat.setBody("");
        mDataTextChat.setLang(TextUtils.isEmpty(new TableUserInfo(this).getUser().getLanguageIdentifire()) ? "en" : new TableUserInfo(this).getUser().getLanguageIdentifire());
        mDataTextChat.setSenderChatID(myChatId);
        mDataTextChat.setFriendChatID(friendChatId);
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        mDataTextChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        mDataTextChat.setChattype(mChatType);
        mDataTextChat.setStrGroupID(mGroupId);
        mDataTextChat.setGroupJID(mDataGroup.getGroupJId());
        mDataTextChat.setUserID(mDataProfile.getUserId());
        mDataTextChat.setSenderId(mDataProfile.getUserId());
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mDataTextChat.setFriendName(friendName);
            mDataTextChat.setFriendId(friendId);
        }
        mDataTextChat.setSenderName(mDataProfile.getUsername());


        mDataTextChat.setYoutubeTitle(selectedYouTubeVideo.getTitle());
        mDataTextChat.setYoutubeDescription(selectedYouTubeVideo.getDescription());
        mDataTextChat.setYoutubeThumbUrl(selectedYouTubeVideo.getThumbLinkMedium());
        mDataTextChat.setYoutubePublishTime(selectedYouTubeVideo.getPublishTime());
        mDataTextChat.setYoutubeVideoId(selectedYouTubeVideo.getVideoId());


        sendYoutubeVideoToXMPP(mDataTextChat);
        mDataTextChat.setFriendPhoneNo(friendPhoneNum);
        insertMessageToDB(mDataTextChat);
        updateChatListUI(mDataTextChat);
    }

    private void sendYoutubeVideoToXMPP(DataTextChat mDataTextChat) {
        String SendMessageJSon = "";
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        SendMessageJSon = OneToOneChatJSonCreator.youtubeChatToJsonString(mDataTextChat);


        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE;

            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
            bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendMessageJSon);

            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.view:
                view.setVisibility(View.GONE);
                lnr_Share.setVisibility(View.GONE);

                break;

            case R.id.et_chatText:
                showSmiley(false);
                if (emojicons.getVisibility() == View.VISIBLE) {
                    emojicons.setVisibility(View.GONE);

                }

                break;
            case R.id.rl_camera:
                if (!checkIfGroupStillValid()) {
                    showGroupRemovedAlert();
                    return;
                }

                if (blockStatus != 0) {
                    String blockAlert = "";
                    if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                        blockAlert = "You have been blocked in this group";
                    } else {
                        blockAlert = getResources().getString(R.string.toast_block_user);
                    }
                    ToastUtils.showAlertToast(this, blockAlert, ToastType.FAILURE_ALERT);
                    return;
                }

                if (shouldShowFirstTimeChatSendOptionAtTopBar) {
                    if (TextUtils.isEmpty(firstTimeChatOptionSelection)) {
                        CommonMethods.hideSoftKeyboard(this);
                        ToastUtils.showAlertToast(this,
                                "Please select your relation with this person", ToastType.FAILURE_ALERT);
                        openFirstTimeChatOptionWindowAfterAFewSecond(2000);
                        return;
                    } else {
                        if (et_chatText.getText().toString().trim().length() <= 0) {
                            ToastUtils.showAlertToast(this,
                                    "Please write something to this person", ToastType.FAILURE_ALERT);
                        } else {
                            iv_camera.setTag("send");
                            createFirstTimeStickerMessage(et_chatText.getText().toString().trim());
                            et_chatText.setText("");
                        }

                        return;
                    }
                }
                if (et_chatText.getText().toString().trim().length() <= 0) {
                    iv_camera.setTag("camera");
                    startImageIntentCamra();
//                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent, Constants.ACTION_IMAGE_CAPTURE);
                } else {
                    iv_camera.setTag("send");
                    createMessage(et_chatText.getText().toString().trim());
                    et_chatText.setText("");
                }


                break;


            case R.id.rl_smiley:
//                hideSmiley();
//                if (!isSmiley) {
//                    keyboardEvent();
//
//                    //int h = CommonMethods.getScreen(mActivity).heightPixels / 2;
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, smileyHeight);
////        lp.addRule(ViewGroup.ALIGN_PARENT_BOTTOM);
//                    emojicons.setLayoutParams(lp);
//                    showSmiley(true);
//                    hideSoftKeyboard(mActivity, et_chatText);
//                    isSmiley = true;
//
////                    setEmojiconFragment(false);
//                } else {
//                    showSmiley(false);
//                    showSoftKeyboard(main_container);
//                    isSmiley = false;
//                }
                keyboardEvent();
                hideSoftKeyboard(this, v);
                LinearLayout.LayoutParams lp = new LinearLayout.
                        LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, smileyHeight);
//        lp.addRule(ViewGroup.ALIGN_PARENT_BOTTOM);
                emojicons.setLayoutParams(lp);
//                emojicons.requestLayout();
                showSmiley(true);
                break;

            default:
                break;
        }
    }

    private void createFirstTimeStickerMessage(String message) {
        DataTextChat mDataTextChat = new DataTextChat();
        mDataTextChat.setMessageid(new org.jivesoftware.smack.packet.Message().getStanzaId());
        mDataTextChat.setOnline(ConstantChat.ISONLINE);
        mDataTextChat.setMessageType(ConstantChat.FIRST_TIME_STICKER_TYPE);
        mDataTextChat.setBody(CommonMethods.getUTFEncodedString(message));


        mDataTextChat.setLang(getInputLanguages());
        mDataTextChat.setSenderChatID(myChatId);
        mDataTextChat.setFriendChatID(friendChatId);
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        mDataTextChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        mDataTextChat.setChattype(mChatType);
        mDataTextChat.setStrGroupID(mGroupId);
        mDataTextChat.setGroupJID(mDataGroup.getGroupJId());
        mDataTextChat.setUserID(mDataProfile.getUserId());
        mDataTextChat.setSenderId(mDataProfile.getUserId());
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mDataTextChat.setFriendName(friendName);
            mDataTextChat.setFriendId(friendId);
        }
        mDataTextChat.setSenderName(mDataProfile.getUsername());
        mDataTextChat.setFilePath(firstTimeChatOptionSelection);
        sendFirstTimeStickerToXMPP(mDataTextChat);
        mDataTextChat.setFriendPhoneNo(friendPhoneNum);
        insertMessageToDB(mDataTextChat);
        shouldShowFirstTimeChatSendOptionAtTopBar = false;
        showHideFirstTimeMenu();
        updateChatListUI(mDataTextChat);
    }

    private void openFirstTimeChatOptionWindowAfterAFewSecond(final int delay) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (handler != null)
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFirstTimeFriendOptionDialog(findViewById(R.id.item_right_first_time_option));
                        }
                    }, delay);
            }
        }).start();
    }

    private void processAndUploadVideo(String selectedVideoPath) {
//        ToastUtils.showAlertToast(this, selectedVideoPath, ToastType.SUCESS_ALERT);
        Thread videoHandlerThread = new Thread(new RunnableProcessVideoChat(this, mChatType,
                mGroupId, mDataGroup.getGroupJId(), mDataProfile.getUserId(), mDataProfile.getUsername(), myChatId,
                friendChatId, myPhoneNum, friendPhoneNum, selectedVideoPath, videoUploadHandler));
        videoHandlerThread.start();
    }

    private void insertImageMessageToDB(DataTextChat mDataFileChat) {
        mDataFileChat.setSenderId(mDataProfile.getUserId());
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mDataFileChat.setFriendName(friendName);
            mDataFileChat.setFriendId(friendId);
        }
        mDataFileChat.setSenderName(mDataProfile.getUsername());
        mTableChat.insertChat(mDataFileChat);
    }

    /**
     * Creates POJO for each image, adds them to the list, sends for upload
     *
     * @param selectedImageFilePathArray
     */
    private void createImageArray(final ArrayList<DataShareImage> selectedImageFilePathArray) {
        if (selectedImageFilePathArray == null) {
            CommonMethods.MYToast(this, "No image seleted");
            return;
        }

        Thread imageHandlerThread = new Thread(new RunnableProcessImageChat(this, mChatType,
                mGroupId, mDataGroup.getGroupJId(),
                mDataProfile.getUserId(), mDataProfile.getUsername(), myChatId,
                friendChatId, myPhoneNum, friendPhoneNum, selectedImageFilePathArray, imageUploadHandler));
        imageHandlerThread.start();
    }

    private String getInputLanguages() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> ims = imm.getEnabledInputMethodList();
        String currentKeyBoardLocale = "";
        for (InputMethodInfo method : ims) {
            List<InputMethodSubtype> submethods = imm.getEnabledInputMethodSubtypeList(method, true);
            for (InputMethodSubtype submethod : submethods) {
                if (submethod.getMode().equals("keyboard")) {
                    currentKeyBoardLocale = submethod.getLocale();
                    if (currentKeyBoardLocale.contains("_")) {
                        currentKeyBoardLocale = currentKeyBoardLocale.split("_")[0];
                    }
                    LogUtils.i(LOG_TAG, "Available input method locale: " + currentKeyBoardLocale);

                }
            }
        }
        if (TextUtils.isEmpty(currentKeyBoardLocale)) {
            currentKeyBoardLocale = TextUtils.isEmpty(new TableUserInfo(this).getUser().getLanguageIdentifire()) ? "en" : new TableUserInfo(this).getUser().getLanguageIdentifire();
        }
        return currentKeyBoardLocale;


    }

    private void initFirstTimePopUpWindow() {
        popUpWindow = new FirstTimeChatOptionsPopUpWindow(this, new FirstTimeChatOptionsPopUpWindow.FirstTimeChatOptionsSelectionCallBack() {
            @Override
            public void onOptionSelected(String selectedOption) {

                firstTimeChatOptionSelection = selectedOption;

            }
        });
//        popUpWindow.setSelection(R.id.rel_classmate);
    }

    @Override
    protected void showFirstTimeFriendOptionDialog(View v) {

        if (handler != null) {
            if (popUpWindow == null) {
                initFirstTimePopUpWindow();
            }


            popUpWindow.showAsDropDown(v, 0, 0);
        }
    }

    //    for text message
    private void createMessage(String message) {

        DataTextChat mDataTextChat = new DataTextChat();
        mDataTextChat.setMessageid(new org.jivesoftware.smack.packet.Message().getStanzaId());
        mDataTextChat.setOnline(ConstantChat.ISONLINE);
        mDataTextChat.setMessageType(ConstantChat.MESSAGE_TYPE);
        mDataTextChat.setBody(CommonMethods.getUTFEncodedString(message));


        mDataTextChat.setLang(getInputLanguages());
        mDataTextChat.setSenderChatID(myChatId);
        mDataTextChat.setFriendChatID(friendChatId);
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        mDataTextChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        mDataTextChat.setChattype(mChatType);
        mDataTextChat.setStrGroupID(mGroupId);
        mDataTextChat.setGroupJID(mDataGroup.getGroupJId());
        mDataTextChat.setUserID(mDataProfile.getUserId());
        mDataTextChat.setSenderId(mDataProfile.getUserId());
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mDataTextChat.setFriendName(friendName);
            mDataTextChat.setFriendId(friendId);
        }

        mDataTextChat.setSenderName(mDataProfile.getUsername());
        sendMessageToXMPP(mDataTextChat);
        mDataTextChat.setFriendPhoneNo(friendPhoneNum);
        insertMessageToDB(mDataTextChat);
        shouldShowFirstTimeChatSendOptionAtTopBar = false;
        showHideFirstTimeMenu();
        updateChatListUI(mDataTextChat);
    }

    private void sendVideoMessageToXMPP(DataTextChat mDataTextChat) {
        String SendMessageJSon = "";
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        SendMessageJSon = OneToOneChatJSonCreator.videoChatToJsonString(mDataTextChat);


        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE;

            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
            bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendMessageJSon);

            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendImageMessageToXMPP(DataTextChat mDataTextChat) {
        String SendMessageJSon = "";
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        SendMessageJSon = OneToOneChatJSonCreator.imageChatToJsonString(mDataTextChat);


        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE;

            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
            bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendMessageJSon);

            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateChatListUI(DataTextChat mDataTextChat) {
        List<AdapterChatSection.Section> sections =
                new ArrayList<AdapterChatSection.Section>();
        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();

        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mArrDataTextChats = mTableChat.getChatSections(friendChatId);
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            mArrDataTextChats = mTableChat.getGroupChatSections(mGroupId);
        }
        int sectionPosition = 0;
        for (int i = 0; i < mArrDataTextChats.size(); i++) {
            if (i == 0) {
                sectionPosition = i;
            } else {
                sectionPosition = sectionPosition + (Integer.parseInt(mArrDataTextChats.get(i - 1).getSectionCount()));
            }
            long thenTime = 0;
            String dayDiff = "";
            //to test
            try {
                thenTime = Long.parseLong(DateTimeUtil.
                        convertFormattedDateToMillisecond(DateTimeUtil.
                                getFormattedDate(mArrDataTextChats.get(i).getSectionTime(), "MM-dd-yyyy HH:mm:ss"
                                        , "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (DateUtils.isToday(thenTime)) {
                dayDiff = "Today";
            } else {

            }

            String sectionDateString = mArrDataTextChats.get(i).getSectionTime();

            sectionDateString = DateTimeUtil.convertUtcToLocal(sectionDateString, "MM-dd-yyyy HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss",
                    getResources().getConfiguration().locale);

            String headerTimeFormatted = "";
            if (TextUtils.isEmpty(dayDiff)) {
                String date_time = DateTimeUtil.getFormattedDate(sectionDateString, "yyyy-MM-dd HH:mm:ss"
                        , "MMMM d, yyyy hh:mm a");
                String[] dateTimeArray = StringUtils.split(date_time, " ");
                if (dateTimeArray != null && dateTimeArray.length >= 5) {
                    String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                    headerTimeFormatted = date;
                }

            } else {
                //if today show time only
                if (StringUtils.equalsIgnoreCase(dayDiff, "Today")) {
                    headerTimeFormatted = dayDiff;
                } else {
                    String date_time = DateTimeUtil.getFormattedDate(sectionDateString, "yyyy-MM-dd HH:mm:ss"
                            , "MMMM d, yyyy hh:mm a");
                    String[] dateTimeArray = StringUtils.split(date_time, " ");
                    if (dateTimeArray != null && dateTimeArray.length >= 5) {
                        String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                        headerTimeFormatted = date;
                    }
                }
            }
            sections.add(new AdapterChatSection.Section(sectionPosition, headerTimeFormatted));
        }


        AdapterChatSection.Section[] dummy = new AdapterChatSection.Section[sections.size()];
        mSectionedAdapter.setSections(sections.toArray(dummy));
        mAdapter.AddNewMessageInUI(mDataTextChat);
        recycler_view_common.postDelayed(new Runnable() {
            @Override
            public void run() {
                recycler_view_common.scrollToPosition(recycler_view_common.getAdapter().getItemCount() - 1);
            }
        }, 500);
    }

    private void insertMessageToDB(DataTextChat mDataTextChat) {
        mTableChat.insertChat(mDataTextChat);
    }

    //for first time sticker send
    private void sendFirstTimeStickerToXMPP(DataTextChat mDataTextChat) {
        String SendMessageJSon = "";

        SendMessageJSon = OneToOneChatJSonCreator.getFirstTimeStickerToJSON(mDataTextChat);

        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE;

            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
            bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendMessageJSon);

            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendMessageToXMPP(DataTextChat mDataTextChat) {
        String SendMessageJSon = "";

        SendMessageJSon = OneToOneChatJSonCreator.getTextMessageToJSON(mDataTextChat);

        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE;

            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
            bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendMessageJSon);

            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    //for Contact
    private void createContact(ContactSetget mContactSetget) {

        DataTextChat mDataTextChat = new DataTextChat();
        mDataTextChat.setMessageid(new org.jivesoftware.smack.packet.Message().getStanzaId());
        mDataTextChat.setOnline(ConstantChat.ISONLINE);
        mDataTextChat.setMessageType(ConstantChat.CONTACT_TYPE);
        mDataTextChat.setBody("");
        mDataTextChat.setLang(TextUtils.isEmpty(new TableUserInfo(this).getUser().getLanguageIdentifire()) ? "en" : new TableUserInfo(this).getUser().getLanguageIdentifire());
        mDataTextChat.setSenderChatID(myChatId);
        mDataTextChat.setFriendChatID(friendChatId);
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        mDataTextChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        mDataTextChat.setChattype(mChatType);
        mDataTextChat.setStrGroupID(mGroupId);
        mDataTextChat.setGroupJID(mDataGroup.getGroupJId());
        mDataTextChat.setAttachContactName(CommonMethods.getUTFEncodedString(mContactSetget.getContactName()));
        mDataTextChat.setAttachContactNo(mContactSetget.getContactNumber());
        mDataTextChat.setAttachBase64str4Img(mContactSetget.getmBitmap());
        mDataTextChat.setUserID(mDataProfile.getUserId());
        mDataTextChat.setSenderId(mDataProfile.getUserId());
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mDataTextChat.setFriendName(friendName);
            mDataTextChat.setFriendId(friendId);
        }
        mDataTextChat.setSenderName(mDataProfile.getUsername());
        updateChatListUI(mDataTextChat);
        sendContactToXMPP(mDataTextChat);
        mDataTextChat.setFriendPhoneNo(friendPhoneNum);
        insertMessageToDB(mDataTextChat);

    }

    private void sendContactToXMPP(DataTextChat mDataContactChat) {
        String SendContactJSon = "";
        if (!TextUtils.isEmpty(mDataContactChat.getAttachContactNo()))
            SendContactJSon = OneToOneChatJSonCreator.getSharedContactToJSON(mDataContactChat);

        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_ONETOONE_CONTACT_MESSAGE;

            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataContactChat);
            bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendContactJSon);

            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendLocationToXMPP(DataTextChat mDataLocation) {
        String SendLocationJSon = "";
        if (!TextUtils.isEmpty(mDataLocation.getLoc_address()))
            SendLocationJSon = OneToOneChatJSonCreator.getSharedLocationToJSON(mDataLocation);

        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_ONETOONE_LOCATION_MESSAGE;
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataLocation);
            bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendLocationJSon);

            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void createLocation(LocationGetSet mLocationGetSet) {

        DataTextChat mDataTextChat = new DataTextChat();
        mDataTextChat.setMessageid(new org.jivesoftware.smack.packet.Message().getStanzaId());
        mDataTextChat.setOnline(ConstantChat.ISONLINE);
        mDataTextChat.setMessageType(ConstantChat.LOCATION_TYPE);
        mDataTextChat.setBody("");
        mDataTextChat.setLang(TextUtils.isEmpty(new TableUserInfo(this).getUser().getLanguageIdentifire()) ? "en" : new TableUserInfo(this).getUser().getLanguageIdentifire());
        mDataTextChat.setSenderChatID(myChatId);
        mDataTextChat.setFriendChatID(friendChatId);
        mDataTextChat.setFriendPhoneNo(myPhoneNum);
        mDataTextChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        mDataTextChat.setChattype(mChatType);
        mDataTextChat.setStrGroupID(mGroupId);
        mDataTextChat.setGroupJID(mDataGroup.getGroupJId());
        mDataTextChat.setLoc_lat(mLocationGetSet.getLat());
        mDataTextChat.setLoc_long(mLocationGetSet.getLong());
        mDataTextChat.setLoc_address(CommonMethods.getUTFEncodedString(mLocationGetSet.getAddress()));
        mDataTextChat.setUserID(mDataProfile.getUserId());
        mDataTextChat.setSenderId(mDataProfile.getUserId());
        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mDataTextChat.setFriendName(friendName);
            mDataTextChat.setFriendId(friendId);
        }
        mDataTextChat.setSenderName(mDataProfile.getUsername());
        updateChatListUI(mDataTextChat);
        sendLocationToXMPP(mDataTextChat);
        mDataTextChat.setFriendPhoneNo(friendPhoneNum);
        insertMessageToDB(mDataTextChat);

    }

    private void keyboardEvent() {
        main_container = (LinearLayout) findViewById(R.id.main_container);
        view = findViewById(R.id.view);
        main_container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    if (main_container == null) {
                        return;
                    }

                    Rect r = new Rect();
                    main_container.getWindowVisibleDisplayFrame(r);

                    int heightDiff = main_container.getRootView().getHeight() - (r.bottom - r.top);
                    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                    if (resourceId > 0) {
                        heightDiff -= getResources().getDimensionPixelSize(resourceId);
                    }

                    Log.d(LOG_TAG, "onGlobalLayout: heightDiff: " + heightDiff);
                    if (heightDiff > dpToPx(100)) {
                        smileyHeight = heightDiff - (int) CommonMethods.convertDpToPixel(30, ActivityChat.this);
                        if (!keyboardVisible) {
                            keyboardVisible = true;
                            //  setbackground(keyboardVisible);
                            showSmiley(false);
                        }
                    } else {
                        if (keyboardVisible) {
                            keyboardVisible = false;
                            // setbackground(keyboardVisible);
                            hideSoftKeyboard(ActivityChat.this, et_chatText);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void showSmiley(boolean show) {
        findViewById(R.id.emojicons).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (et_chatText.getText().toString().trim().length() > 0) {

            iv_camera.setImageResource(R.drawable.ic_chat_send_icon);


        } else {
            iv_camera.setImageResource(R.drawable.ic_chat_share_chat_camera_icon);
        }
        //mTxtEmojicon.setText(s);

        if (blockStatus != 0) {
            return;
        }
        if (et_chatText.getText().toString().trim().length() > 0) {
            sendChatState(ChatState.composing);
        }
    }

    private void sendChatState(ChatState chatState) {
        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_CHAT_STATE;

            Bundle bundle = new Bundle();
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {

                bundle.putString(Constants.B_ID, friendChatId);
            } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                bundle.putString(Constants.B_ID, mDataGroup.getGroupJId());
            }
            bundle.putString("my_chat_id", myChatId);
            bundle.putString(OneToOneChatJSonCreator.CHATTYPE, mChatType);
            bundle.putSerializable(Constants.B_OBJ, chatState);
            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (et_chatText.getText().toString().trim().length() > 0) {

            iv_camera.setImageResource(R.drawable.ic_chat_send_icon);
        } else {
            iv_camera.setImageResource(R.drawable.ic_chat_share_chat_camera_icon);
        }
    }

    @Override
    public boolean RecieveChatFromXMPP(DataTextChat mdDataTextChat) {
        if (mdDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
                if (!mdDataTextChat.getSenderChatID().equalsIgnoreCase(friendChatId)) {
                    addNotification(mdDataTextChat);
                    return true;
                }
            } else {
                addNotification(mdDataTextChat);
                return true;
            }
        } else if (mdDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                if (!mdDataTextChat.getStrGroupID().equalsIgnoreCase(mGroupId)) {
                    addNotification(mdDataTextChat);
                    return true;
                }
            } else {
                addNotification(mdDataTextChat);
                return true;
            }
        } else {
            return true;
        }

        if (!isVisible) {
            addNotification(mdDataTextChat);
        }

        List<AdapterChatSection.Section> sections =
                new ArrayList<AdapterChatSection.Section>();
        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();

        if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mArrDataTextChats = mTableChat.getChatSections(friendChatId);
        } else if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            mArrDataTextChats = mTableChat.getGroupChatSections(mGroupId);
        }
        int sectionPosition = 0;
        for (int i = 0; i < mArrDataTextChats.size(); i++) {
            if (i == 0) {
                sectionPosition = i;
            } else {
                sectionPosition = sectionPosition + (Integer.parseInt(mArrDataTextChats.get(i - 1).getSectionCount()));
            }
            long thenTime = 0;
            String dayDiff = "";
            try {
                thenTime = Long.parseLong(DateTimeUtil.
                        convertFormattedDateToMillisecond(DateTimeUtil.
                                getFormattedDate(mArrDataTextChats.get(i).getSectionTime(), "MM-dd-yyyy HH:mm:ss"
                                        , "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (DateUtils.isToday(thenTime)) {
                dayDiff = "Today";
            } else {

            }

            String sectionDateString = mArrDataTextChats.get(i).getSectionTime();

            sectionDateString = DateTimeUtil.convertUtcToLocal(sectionDateString, "MM-dd-yyyy HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss",
                    getResources().getConfiguration().locale);

            String headerTimeFormatted = "";
            if (TextUtils.isEmpty(dayDiff)) {
                String date_time = DateTimeUtil.getFormattedDate(sectionDateString, "yyyy-MM-dd HH:mm:ss"
                        , "MMMM d, yyyy hh:mm a");
                String[] dateTimeArray = StringUtils.split(date_time, " ");
                if (dateTimeArray != null && dateTimeArray.length >= 5) {
                    String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                    headerTimeFormatted = date;
                }

            } else {
                //if today show time only
                if (StringUtils.equalsIgnoreCase(dayDiff, "Today")) {
                    headerTimeFormatted = dayDiff;
                } else {
                    String date_time = DateTimeUtil.getFormattedDate(sectionDateString, "yyyy-MM-dd HH:mm:ss"
                            , "MMMM d, yyyy hh:mm a");
                    String[] dateTimeArray = StringUtils.split(date_time, " ");
                    if (dateTimeArray != null && dateTimeArray.length >= 5) {
                        String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                        headerTimeFormatted = date;
                    }
                }
            }
            sections.add(new AdapterChatSection.Section(sectionPosition, headerTimeFormatted));
        }

     /* sections.add(new AdapterChatSection.Section(5, "Section 2"));
        sections.add(new AdapterChatSection.Section(12, "Section 3"));
        sections.add(new AdapterChatSection.Section(14, "Section 4"));
        sections.add(new AdapterChatSection.Section(20, "Section 5"));*/

        AdapterChatSection.Section[] dummy = new AdapterChatSection.Section[sections.size()];
        mSectionedAdapter.setSections(sections.toArray(dummy));


        mAdapter.AddNewMessageInUI(mdDataTextChat);

        recycler_view_common.postDelayed(new Runnable() {
            @Override
            public void run() {
                recycler_view_common.scrollToPosition(recycler_view_common.getAdapter().getItemCount() - 1);
            }
        }, 500);

        //send read status when online in chat screens.
        createReadStatusForDeliveredChats();

        try {
            ShortcutBadger.applyCount(getApplicationContext(), getBadgeCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (findViewById(R.id.emojicons).getVisibility() == View.VISIBLE) {
            showSmiley(false);
            return;
        }

        if (StringUtils.equalsIgnoreCase(isFromNotification, "true")) {
            Intent mIntent = new Intent(this, ActivityDash.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            overridePendingTransition(0, 0);
//            ((AppVhortex) getApplicationContext()).getContactService(this);
            startActivity(mIntent);
        }
        super.onBackPressed();
    }

    @Override
    public void onResponseObject(Object mObject) {

    }

    @Override
    public void onResponseList(ArrayList<?> mList) {

    }

    @Override
    public void onResponseFaliure(String responseText) {

    }

    @Override
    protected void onChatStateReceived(String senderId, String groupJid, String type) {

        if (TextUtils.isEmpty(groupJid)) {
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
                if (TextUtils.isEmpty(senderId)) {
                    LogUtils.i(LOG_TAG, "SenderId empty or null");
                    return;
                }

                if (mDataContact != null)
                    if (TextUtils.isEmpty(this.mDataContact.getChatId())) {
                        if (this.friendChatId.equalsIgnoreCase(senderId)) {
                            if (tv_subTitle != null) {
                                if (!isTyping) {
                                    tv_subTitle.post(new PublishChatStateRunnable("Typing...", type));
                                }
                            }
                        }
                    } else {
                        if (this.mDataContact.getChatId().equalsIgnoreCase(senderId)) {
                            if (tv_subTitle != null) {
                                if (!isTyping) {
                                    tv_subTitle.post(new PublishChatStateRunnable("Typing...", type));
                                }

                            }
                        }
                    }
            }
        } else {
            if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                String nameOrPhone = "";
                if (this.mDataGroup.getGroupJId().equalsIgnoreCase(groupJid)) {
                    for (int i = 0; i < mDataGroup.getMemberdetails().size(); i++) {

                        if (ValidationUtils.containsXmppDomainName(senderId)) {

                            if (senderId.equalsIgnoreCase(mDataGroup.getMemberdetails().get(i).getChatId())) {

                                String name = new TableContact(this).
                                        getFriendDetailsByID(mDataGroup.getMemberdetails().get(i).getUserId()).getName();
                                if (TextUtils.isEmpty(mDataGroup.getMemberdetails().get(i).getUserName())) {
                                    if (TextUtils.isEmpty(name)) {
                                        nameOrPhone = "+" + mDataGroup.getMemberdetails().get(i).getUserCountryId()
                                                + mDataGroup.getMemberdetails().get(i).getUserPhNo();
                                    } else
                                        nameOrPhone = name;
                                } else
                                    nameOrPhone = mDataGroup.getMemberdetails().get(i).getUserName();
                            }
                        } else if ((senderId + "@" + ServiceXmppConnection.SERVICE_NAME).
                                equalsIgnoreCase(mDataGroup.getMemberdetails().get(i).getChatId())) {
                            String name = new TableContact(this).
                                    getFriendDetailsByID(mDataGroup.getMemberdetails().get(i).getUserId()).getName();
                            if (TextUtils.isEmpty(mDataGroup.getMemberdetails().get(i).getUserName())) {
                                if (TextUtils.isEmpty(name)) {
                                    nameOrPhone = "+" + mDataGroup.getMemberdetails().get(i).getUserCountryId()
                                            + mDataGroup.getMemberdetails().get(i).getUserPhNo();
                                } else
                                    nameOrPhone = name;
                            } else
                                nameOrPhone = mDataGroup.getMemberdetails().get(i).getUserName();
                        }
                    }

                    if (tv_subTitle != null) {
                        if (!isTyping) {

                            tv_subTitle.post(new PublishChatStateRunnable(nameOrPhone + " is typing...", type));
                        }

                    }
                }
            }
        }

    }

    private void showHideSubtitle(boolean show) {
        if (show) {

        }
    }

    private void reCheckStatusIn(int nextCheckTime) {
        tv_subTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTyping) {
                    if (mChatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                        tv_subTitle.setText(groupMemberName);

                    } else {
                        tv_subTitle.setText("");
                        tv_subTitle.setVisibility(View.GONE);
                    }
                    isTyping = false;
                }
            }
        }, nextCheckTime);
    }

    public static class ScrollGesture extends OnScrollListener {
        private LinearLayoutManager mLinearLayoutManager;
        private LinearLayout lnr_Section;
        private TextView tvSection;

        /**
         * Used  To get the Scroll Gesture And show the Section Associated with the Recycler
         */
        public ScrollGesture(LinearLayout lnr_Section, TextView tvSection, LinearLayoutManager mLinearLayoutManager) {
            super();
            this.lnr_Section = lnr_Section;
            this.tvSection = tvSection;
            this.mLinearLayoutManager = mLinearLayoutManager;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
//                    if (lnr_Section.getVisibility() == View.VISIBLE) {
//                        lnr_Section.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                lnr_Section.setVisibility(View.INVISIBLE);
//                            }
//                        }, 200);
//                    }
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
//            try {
//                RecyclerView.ViewHolder mViewHolder = recyclerView.findViewHolderForLayoutPosition(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
//                if (mViewHolder instanceof AdapterChatSection.SectionViewHolder) {
//                    lnr_Section.setVisibility(View.VISIBLE);
//                    tvSection.setText(((AdapterChatSection.SectionViewHolder) mViewHolder).title.getText());
//                }
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
        }
    }

    private class PublishChatStateRunnable implements Runnable {
        private String status = "";
        private String type = "";

        public PublishChatStateRunnable(String status, String type) {
            this.status = status;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                tv_subTitle.setVisibility(View.VISIBLE);
                isTyping = true;
                tv_subTitle.setText(status);

                reCheckStatusIn(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Toast.makeText(this, "senderId:" + senderId + " type:" + type, Toast.LENGTH_SHORT).show();
        }
    }


}
