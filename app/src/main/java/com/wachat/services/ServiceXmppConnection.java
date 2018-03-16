package com.wachat.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.api.services.translate.model.DetectionsListResponse;
import com.google.api.services.translate.model.DetectionsResourceItems;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import com.wachat.R;
import com.wachat.activity.ActivityChat;
import com.wachat.async.AsyncSendPushNotification;
import com.wachat.async.AsyncSetFriendStatus;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.chatUtils.NotificationUtils;
import com.wachat.chatUtils.OneToOneChatJSonCreator;
import com.wachat.data.BaseResponse;
import com.wachat.data.DataContact;
import com.wachat.data.DataGroup;
import com.wachat.data.DataGroupMembers;
import com.wachat.data.DataProfile;
import com.wachat.data.DataTextChat;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.interfaces.WebServiceCallBack;
import com.wachat.storage.ConstantDB;
import com.wachat.storage.TableChat;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableGroup;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;
import com.wachat.util.ValidationUtils;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.delay.provider.LegacyDelayInformationProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Gourav Kundu on 23-09-2015.
 */
public class ServiceXmppConnection extends Service {

    /*@                                 /\  /\
 * @                                  /  \/  \
 *  @                                /        --
 *   \---\                          /           \
 *    |   \------------------------/       /-\    \
 *    |                                    \-/     \
 *     \                                             ------O
 *      \                                                 /
 *       |    |                    |    |                /
 *       |    |                    |    |-----    -------
 *       |    |\                  /|    |     \  WWWWWW/
 *       |    | \                / |    |      \-------
 *       |    |  \--------------/  |    |
 *      /     |                   /     |
 *      \      \                  \      \
 *       \-----/                   \-----/
 */


    /**Iscoveri*/
//    public static final String SERVER_HOST = "162.250.121.118";
//    public static final int SERVER_PORT = 5222;
//    public static final String SERVICE_NAME = "iscoveri.com";
//    public static final String SERVICE_SECRET = "pzao0Hb9";
//    public static final String SERVICE_URL = "http://162.250.121.118:9090/plugins/userService/userservice";
//    public static final String PRESENCE_REQUEST_URL = "http://162.250.121.118:9090/plugins/presence/status";


//    http://emathready.com:9090/plugins/userService/userservice?type=add&secret=pzao0Hb9&username=919903494848&password=919903494848&groups=VhorText&subscription=1

    /**
     * Live
     */
    public static final String SERVER_HOST = "emathready.com";
    public static final int SERVER_PORT = 5222;
    public static final String SERVICE_NAME = "emathready.com";
    public static final String SERVICE_SECRET = "pzao0Hb9";
    public static final String SERVICE_URL = "http://emathready.com:9090/plugins/userService/userservice";
    public static final String PRESENCE_REQUEST_URL = "http://emathready.com:9090/plugins/presence/status";


    public static final String GROUP_NAME = "VhorText";
    public static final String PASSWORD = "123!@#45$%";
    public static final String RECIEVED_SMACK = "/Smack";
    public static final String ACTION_NETWORK_STATUS_CHANGED = "action_network_status_changed";
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ServiceXmppConnection.class);
    private static volatile boolean isOnlineRequested = false;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private final BroadcastReceiver groupModificationBroadCastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i(LOG_TAG, "leaveGroupReceiver : OnReceive");
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    int action = bundle.getInt("action");
                    if (action > 0) {
                        Message chatMessage = Message.obtain();
                        chatMessage.what = action;
                        // Set the bundle data to the Message
                        chatMessage.setData(bundle);
                        try {
                            mMessenger.send(chatMessage);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };
    public Presence mPresence;
    private String userId = "";
    private String password = "";
    private ExecutorService mExecutorService;
    private RunnableXmpp mRunnableXmpp;
    //    private static volatile XmppManager xmppManager;
    private AbstractXMPPConnection xmppCON;
    private XMPPTCPConnectionConfiguration.Builder xmppConfig;
    private Prefs mPrefs;
    private Messenger mMessengerUI;
    //    private ContactObserver mContactObserver;
    private TableUserInfo mTableUserInfo;
    private DataProfile mDataProfile;
    private Translate translate;
    private int retryCouter = 0;
    private ConnectionListener mConnectionListener;
    private MultiUserChatManager mucManager;
    private ChatListenerImpl chatMessageListener = new ChatListenerImpl();

    private void receivedChatState(String senderId, String groupId, JSONObject mMsgJSONobj) {
        if (mMessengerUI != null) {
            Message recievedmessage = Message.obtain();

            recievedmessage.what = ConstantXMPP.RECEIVE_CHAT_STATE;
            Bundle mBundle = new Bundle();

            String status = mMsgJSONobj.optString(OneToOneChatJSonCreator.TYPE);
            mBundle.putString(OneToOneChatJSonCreator.SENDERID, senderId);
            mBundle.putString(OneToOneChatJSonCreator.TYPE, status);
            mBundle.putString(OneToOneChatJSonCreator.GROUP_JID, groupId);
            // Set the bundle data to the Message
            recievedmessage.setData(mBundle);

            try {

                mMessengerUI.send(recievedmessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(LOG_TAG, "On Create Called");
        mPrefs = Prefs.getInstance(getBaseContext());
//        mContactObserver = new ContactObserver(new Handler());
        mTableUserInfo = new TableUserInfo(getBaseContext());

        initTranslateBuild();

        IntentFilter leaveGroupBroadCastFilter = new IntentFilter();
        leaveGroupBroadCastFilter.addAction("modify_group");

        registerReceiver(groupModificationBroadCastReceiver, leaveGroupBroadCastFilter);

//        getContentResolver()
//                .registerContentObserver(
//                        ContactsContract.Contacts.CONTENT_URI, true,
//                        mContactObserver);


    }

    private void initTranslateBuild() {

        translate = new Translate.Builder(
                AndroidHttp.newCompatibleTransport(), AndroidJsonFactory.getDefaultInstance(),
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest httpRequest) throws IOException {
                        // Log.d("tag", "Http requst: " + httpRequest);
                    }
                })
                .setTranslateRequestInitializer(
                        new TranslateRequestInitializer(
                                getApplicationContext().getString(R.string.google_translate_key)))

                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i(LOG_TAG, "On Bind Called");
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDataProfile = mTableUserInfo.getUser();
        LogUtils.i(LOG_TAG, "ON onStartCommand");
        if (mDataProfile == null || TextUtils.isEmpty(Prefs.getInstance(getApplicationContext()).getUserId())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        boolean groupUpdated = false;
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase(Constants.B_NEED_UPDATED)) {
                groupUpdated = true;
            }
            Bundle mBundle = intent.getExtras();
            if (mBundle != null) {
                LogUtils.i(LOG_TAG, "onNetChanged: " + String.valueOf(mBundle.containsKey("connected")));
            }
        }


        if (isConnected()) {
            if (groupUpdated) {
                if (xmppCON != null && xmppCON.isAuthenticated()) {
                    setUpMuc();
                }
            }
        } else {
            if (mDataProfile != null && CommonMethods.isOnline(getBaseContext())) {
                LogUtils.i(LOG_TAG, "ON onStartCommand and data not null");
                //CountryCode+PhoneNumber,CountryCode+PhoneNumber
                retryCouter = 0;
                cleanUpConnection();
                loginXMPP();
            }
        }

//        NotificationUtils.clearAllNotification(this);

        return START_STICKY;
    }

    private void loginXMPP() {
        if (TextUtils.isEmpty(Prefs.getInstance(getApplicationContext()).getUserId())) {
            stopSelf();
            return;
        }
        userId = password = mDataProfile.getChatId();
        mExecutorService = Executors.newFixedThreadPool(1);
        mRunnableXmpp = new RunnableXmpp(TYPE.LOGIN);
        mExecutorService.execute(mRunnableXmpp);
    }

    public boolean isConnected() {
        if (xmppCON == null) {
            return false;
        }

        return xmppCON.isConnected() && xmppCON.isAuthenticated();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (xmppCON != null) {
            xmppCON.disconnect();
        }

        try {
            unregisterReceiver(groupModificationBroadCastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        getContentResolver().unregisterContentObserver(mContactObserver);
    }

    private void updateSentStatusToDB(DataTextChat mDataTextChat) {
        mDataTextChat.setDeliveryStatus(ConstantChat.STATUS_SENT);
        LogUtils.i(LOG_TAG, "XMPPDeliveryStatusSend: " + ConstantChat.STATUS_SENT);
        new TableChat(getBaseContext()).updateChatDeliveryStatus(mDataTextChat);
    }

    public void setUpPresenceOnline() throws Exception {
        mPresence = new Presence(Presence.Type.available);
        mPresence.setStatus("Online");
// Send the packet (assume we have an XMPPConnection instance called "con").
        if (xmppCON != null) {
            xmppCON.sendStanza(mPresence);
        }
    }

    public void registerXmpp() {
        mExecutorService = Executors.newFixedThreadPool(1);
        mRunnableXmpp = new RunnableXmpp(TYPE.REGISTER);
        mExecutorService.execute(mRunnableXmpp);

    }

   /* public void setUpPresenceOffline() throws SmackException.NotConnectedException {
         mPresence = new Presence(Presence.Type.unavailable);
        mPresence.setStatus("Offline");
// Send the packet (assume we have an XMPPConnection instance called "con").
        if (xmppCON != null) {
            xmppCON.sendStanza(mPresence);
        }
    }*/

    private void retryXMPPCOnnection() {
        if (retryCouter < 20) {
            cleanUpConnection();
            loginXMPP();
            retryCouter++;
        } else {
            //show alert message na not connected
        }
    }

    private void sendUndeliveredMesage() {
        DataTextChat mDataTextChat = new DataTextChat();
        ArrayList<DataTextChat> mArrDataTextChat = new TableChat(getBaseContext())
                .getNotSentChatMessage(ConstantChat.STATUS_UNSENT);
        for (int i = 0; i < mArrDataTextChat.size(); i++) {
            mDataTextChat = mArrDataTextChat.get(i);
            mDataTextChat.setOnline(ConstantChat.ISONLINE);
            mDataTextChat.setLang(TextUtils.isEmpty(new TableUserInfo(this).getUser().getLanguageIdentifire()) ? "en" : new TableUserInfo(this).getUser().getLanguageIdentifire());
//            mDataTextChat.setChattype(ConstantChat.TYPE_SINGLECHAT);
            switch (mDataTextChat.getMessageType()) {
                case ConstantChat.TYPE_SINGLECHAT:
                    if (!TextUtils.isEmpty(mDataTextChat.getBody()))
                        sendUnsentMessageToXMPP(mDataTextChat);
                    break;
                case ConstantChat.LOCATION_TYPE:
                    if (!TextUtils.isEmpty(mDataTextChat.getLoc_address()))
                        sendUnsentLocationToXMPP(mDataTextChat);
                    break;
                case ConstantChat.CONTACT_TYPE:
                    sendUnsentContactToXMPP(mDataTextChat);
                    break;

                default:
                    break;
            }

        }
    }

    private void sendUnsentMessageToXMPP(DataTextChat mDataTextChat) {
        String SendMessageJSon = "";
        SendMessageJSon = OneToOneChatJSonCreator.getTextMessageToJSON(mDataTextChat);

        Message chatMessage = Message.obtain();
        chatMessage.what = ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE;

        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
        bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendMessageJSon);

        // Set the bundle data to the Message
        chatMessage.setData(bundle);
        try {
            mMessenger.send(chatMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void sendUnsentLocationToXMPP(DataTextChat mDataTextChat) {
        String SendMessageJSon = "";

        SendMessageJSon = OneToOneChatJSonCreator.getSharedLocationToJSON(mDataTextChat);

        Message chatMessage = Message.obtain();
        chatMessage.what = ConstantXMPP.SEND_ONETOONE_LOCATION_MESSAGE;

        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
        bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendMessageJSon);

        // Set the bundle data to the Message
        chatMessage.setData(bundle);
        try {
            mMessenger.send(chatMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void sendUnsentContactToXMPP(DataTextChat mDataTextChat) {
        String SendMessageJSon = "";

        SendMessageJSon = OneToOneChatJSonCreator.getSharedContactToJSON(mDataTextChat);

        Message chatMessage = Message.obtain();
        chatMessage.what = ConstantXMPP.SEND_ONETOONE_CONTACT_MESSAGE;

        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
        bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, SendMessageJSon);

        // Set the bundle data to the Message
        chatMessage.setData(bundle);
        try {
            mMessenger.send(chatMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void setUpConnectionListener() {

        mConnectionListener = new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                LogUtils.i(LOG_TAG, "connected");
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                LogUtils.i(LOG_TAG, "authenticated");
            }

            @Override
            public void connectionClosed() {
                retryXMPPCOnnection();
//                loginXMPP();
                LogUtils.i(LOG_TAG, "Connection closed");
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                LogUtils.i(LOG_TAG, "connectionClosedOnError" + e);
                retryXMPPCOnnection();
            }

            @Override
            public void reconnectionSuccessful() {

            }

            @Override
            public void reconnectingIn(int seconds) {

            }

            @Override
            public void reconnectionFailed(Exception e) {

            }
        };
        xmppCON.addConnectionListener(mConnectionListener);
    }

    private void cleanUpConnection() {
        if (xmppCON != null) {
            if (xmppCON.isConnected()) {
                xmppCON.removeConnectionListener(mConnectionListener);
//                xmppCON.removeSyncStanzaListener(stanzaListener);
                xmppCON.removeSyncStanzaListener(chatMessageListener);
                xmppCON.disconnect();
                xmppCON = null;
            } else {
                try {
                    xmppCON.removeConnectionListener(mConnectionListener);
//                    xmppCON.removeSyncStanzaListener(stanzaListener);
                    xmppCON.removeSyncStanzaListener(chatMessageListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                xmppCON = null;
            }
        }
    }

    private void setUpRecieveMessagePacketListener() {

        // Create a packet filter to listen for new messages from a particular
        // user. We use an AndFilter to combine two other filters._
        StanzaFilter filter = new AndFilter(new StanzaTypeFilter(org.jivesoftware.smack.packet.Message.class));
// Assume we've created an XMPPConnection name "connection".

// First, register a packet collector using the filter we created.
        PacketCollector myCollector = xmppCON.createPacketCollector(filter);
// Normally, you'd do something with the collector, like wait for new packets.

// Next, create a packet listener. We use an anonymous inner class for brevity.

// Register the listener._
        xmppCON.addSyncStanzaListener(chatMessageListener, filter);
    }

    private synchronized void receivedYahooMessage(String recievedProcessedMessageJSOn, String recieverId, String groupJId) {
        DataTextChat mDataFileChat = null;
        mDataFileChat = OneToOneChatJSonCreator.chatJsonStringToYahooNewsChat(recievedProcessedMessageJSOn,
                getBaseContext());

        if (xmppCON != null && xmppCON.isAuthenticated()) {
            mDataFileChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        }

        if (recieverId.contains(RECIEVED_SMACK))
            recieverId = recieverId.replace(RECIEVED_SMACK, "");

        mDataFileChat.setGroupJID(groupJId);
        mDataFileChat.setFriendChatID(recieverId);
        mDataFileChat.setSenderChatID(recieverId);
        mDataFileChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);

//        if(TextUtils.isEmpty(groupJId)) {
        String friendChatId = mDataFileChat.getFriendChatID();
        if (!ValidationUtils.containsXmppDomainName(friendChatId)) {
            friendChatId = friendChatId + "@" + SERVICE_NAME;
        }
        DataContact mDataFriendContact = new TableContact(getBaseContext())
                .getFriendDetailsByFrienChatID(friendChatId);
        if (mDataFriendContact != null) {
//            String name = mDataFriendContact.getAppName();
//            if (TextUtils.isEmpty(name)) {
//                name = mDataFriendContact.getName();
//            }
//            if(TextUtils.isEmpty(name)){
//                name = mDataFileChat.getFriendName();
//            }
//            mDataFileChat.setFriendName(name);
//            mDataFileChat.setSenderName(name);
        } else {
            mDataFriendContact = new DataContact();
            mDataFriendContact.setPhoneNumber(mDataFileChat.getFriendPhoneNo());
//            String friendChatId = mDataFileChat.getFriendChatID();
//            if (!friendChatId.contains("@" + SERVICE_NAME)) {
//                friendChatId = friendChatId + "@" + SERVICE_NAME;
//            }
            mDataFriendContact.setAppName(mDataFileChat.getSenderName());
            mDataFriendContact.setName(mDataFileChat.getSenderName());
            mDataFriendContact.setChatId(friendChatId);
            mDataFriendContact.setIsBlocked(0);
            mDataFriendContact.setIsFriend(0);
            mDataFriendContact.setIsRegistered(1);
            mDataFriendContact.setFriendId(mDataFileChat.getFriendId());
            new TableContact(getBaseContext()).insertBulkForUnknownNUmber(mDataFriendContact);
        }
//        }

        //Translated Message if required
        if (new TableUserInfo(this).getUser().getIstransalate().equalsIgnoreCase("1"))
            if (!StringUtils.equalsIgnoreCase(mDataFileChat.getLang(), new TableUserInfo(this).getUser().getLanguageIdentifire()))
                mDataFileChat.setStrTranslatedText(translateMessages(mDataFileChat));
        TableChat tableChat = new TableChat(getBaseContext());
        if (!tableChat.checkIfMessageExists(mDataFileChat.getMessageid())) {
            new TableChat(getBaseContext()).insertChat(mDataFileChat);
        } else {
            return;
        }

        if (mDataFileChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            if (TextUtils.isEmpty(mDataFileChat.getSenderName())) {
                TableGroup tableGroup = new TableGroup(getBaseContext());
                DataGroupMembers groupMember = tableGroup.getMemberInAGroup(
                        mDataFileChat.getStrGroupID(), mDataFileChat.getSenderId());
                if (groupMember != null) {
                    String name = groupMember.getUserName();

                    if (!TextUtils.isEmpty(name)) {
//                                mDataTextChat.setFriendName(name);
                        mDataFileChat.setSenderName(name);
                    }
                }

            }
        }
        if (mMessengerUI != null) {
            Message recievedmessage = Message.obtain();

            recievedmessage.what = ConstantXMPP.RECIEVE_ONETOONE_IMAGE_MESSAGE;
            Bundle mBundle = new Bundle();

            mBundle.putSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE, mDataFileChat);

            // Set the bundle data to the Message
            recievedmessage.setData(mBundle);

            sendDeliveryStatus(mDataFileChat);
            try {

                mMessengerUI.send(recievedmessage);

            } catch (RemoteException e) {
                e.printStackTrace();
                addNotification(mDataFileChat);
                sendDeliveryStatus(mDataFileChat);
            }
        } else {
            addNotification(mDataFileChat);
            sendDeliveryStatus(mDataFileChat);

        }
    }

    private synchronized void receivedYoutubeMessage(String recievedProcessedMessageJSOn,
                                                     String recieverId, String groupJId) {
        DataTextChat mDataFileChat = null;
        mDataFileChat = OneToOneChatJSonCreator.chatJsonStringToYouTubeChat(recievedProcessedMessageJSOn,
                getBaseContext());
        if (xmppCON != null && xmppCON.isAuthenticated()) {
            mDataFileChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        }
        if (recieverId.contains(RECIEVED_SMACK))
            recieverId = recieverId.replace(RECIEVED_SMACK, "");

        mDataFileChat.setGroupJID(groupJId);
        mDataFileChat.setFriendChatID(recieverId);
        mDataFileChat.setSenderChatID(recieverId);
        mDataFileChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);

//        if(TextUtils.isEmpty(groupJId)) {
        String friendChatId = mDataFileChat.getFriendChatID();
        if (!ValidationUtils.containsXmppDomainName(friendChatId)) {
            friendChatId = friendChatId + "@" + SERVICE_NAME;
        }
        DataContact mDataFriendContact = new TableContact(getBaseContext())
                .getFriendDetailsByFrienChatID(friendChatId);
        if (mDataFriendContact != null) {
//            String name = mDataFriendContact.getAppName();
//            if (TextUtils.isEmpty(name)) {
//                name = mDataFriendContact.getName();
//            }
//
//            if(TextUtils.isEmpty(name)){
//                name = mDataFileChat.getFriendName();
//            }
//            mDataFileChat.setFriendName(name);
//            mDataFileChat.setSenderName(name);
        } else {
            mDataFriendContact = new DataContact();
            mDataFriendContact.setPhoneNumber(mDataFileChat.getFriendPhoneNo());
//            String friendChatId = mDataFileChat.getFriendChatID();
//            if (!friendChatId.contains("@" + SERVICE_NAME)) {
//                friendChatId = friendChatId + "@" + SERVICE_NAME;
//            }
            mDataFriendContact.setAppName(mDataFileChat.getSenderName());
            mDataFriendContact.setName(mDataFileChat.getSenderName());
            mDataFriendContact.setChatId(friendChatId);
            mDataFriendContact.setIsBlocked(0);
            mDataFriendContact.setIsFriend(0);
            mDataFriendContact.setIsRegistered(1);
            mDataFriendContact.setFriendId(mDataFileChat.getFriendId());
            new TableContact(getBaseContext()).insertBulkForUnknownNUmber(mDataFriendContact);
        }
        //Translated Message if required
        if (new TableUserInfo(this).getUser().getIstransalate().equalsIgnoreCase("1"))
            if (!StringUtils.equalsIgnoreCase(mDataFileChat.getLang(), new TableUserInfo(this).getUser().getLanguageIdentifire()))
                mDataFileChat.setStrTranslatedText(translateMessages(mDataFileChat));

        TableChat tableChat = new TableChat(getBaseContext());
        if (!tableChat.checkIfMessageExists(mDataFileChat.getMessageid())) {
            new TableChat(getBaseContext()).insertChat(mDataFileChat);
        } else {
            return;
        }
        if (mDataFileChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            if (TextUtils.isEmpty(mDataFileChat.getSenderName())) {
                TableGroup tableGroup = new TableGroup(getBaseContext());
                DataGroupMembers groupMember = tableGroup.getMemberInAGroup(
                        mDataFileChat.getStrGroupID(), mDataFileChat.getSenderId());
                if (groupMember != null) {
                    String name = groupMember.getUserName();

                    if (!TextUtils.isEmpty(name)) {
//                                mDataTextChat.setFriendName(name);
                        mDataFileChat.setSenderName(name);
                    }
                }

            }
        }
        if (mMessengerUI != null) {
            Message recievedmessage = Message.obtain();

            recievedmessage.what = ConstantXMPP.RECIEVE_ONETOONE_IMAGE_MESSAGE;
            Bundle mBundle = new Bundle();

            mBundle.putSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE, mDataFileChat);

            // Set the bundle data to the Message
            recievedmessage.setData(mBundle);

            sendDeliveryStatus(mDataFileChat);
            try {

                mMessengerUI.send(recievedmessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                addNotification(mDataFileChat);
                sendDeliveryStatus(mDataFileChat);
            }
        } else {
            addNotification(mDataFileChat);
            sendDeliveryStatus(mDataFileChat);

        }
    }

    private String translateMessages(DataTextChat mDataTextChat) {
        String result = "";
        if (!TextUtils.isEmpty(mDataTextChat.getBody())) {


//                if(mDataTextChat.getBody())

            String detectedLanguage = "";
            try {
                Translate.Detections.List detectionRequest = translate.detections().
                        list(Arrays.asList(
                                CommonMethods.getUTFDecodedString(mDataTextChat.getBody())));

                DetectionsListResponse detectionResponse = detectionRequest.execute();

                List<List<DetectionsResourceItems>> dri = detectionResponse.getDetections();
                detectedLanguage = dri.get(0).get(0).getLanguage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (!TextUtils.isEmpty(detectedLanguage)
                        && !StringUtils.equalsIgnoreCase(detectedLanguage,
                        new TableUserInfo(this).getUser().getLanguageIdentifire())) {
                    Translate.Translations.List request = translate.translations().
                            list(Arrays.asList(
                                    CommonMethods.getUTFDecodedString(mDataTextChat.getBody())
                            ), new TableUserInfo(this).getUser().getLanguageIdentifire());
                    request.setSource(detectedLanguage);
                    TranslationsListResponse tlr = request.execute();
                    List<TranslationsResource> list = tlr.getTranslations();
                    result = list.get(0).getTranslatedText();
                    LogUtils.i(LOG_TAG, "translateMessages: Lang: " + result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = "";
            }
        }
        return result;
    }

    private void sendDeliveryStatus(DataTextChat mDataTextChat) {
        Message chatDeliveryMessage = Message.obtain();
        chatDeliveryMessage.what = ConstantXMPP.SEND_DELVERY_STATUS;
        String DeliveryStatusMessageJSon = OneToOneChatJSonCreator.
                getDeliveryMessageToJSON(mDataTextChat);

        //make bundle
        Bundle mBundle = new Bundle();
        mBundle.putString(ConstantChat.SEND_DELIVERY_DTATUS_B_MESSAGE, DeliveryStatusMessageJSon);
        mBundle.putSerializable(ConstantChat.SEND_DELIVERY_OBJ_B_MESSAGE, mDataTextChat);
        chatDeliveryMessage.setData(mBundle);
        try {
            mMessenger.send(chatDeliveryMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void recieveDeliveryStatus(String recievedProcessedMessageJSOn) {
        String ChatIds = "";
        String[] commaSeperatedIds;
        DataTextChat mDataTextChat = new DataTextChat();
        try {
            JSONObject mMsgJSONobj = new JSONObject(recievedProcessedMessageJSOn);
            if (mMsgJSONobj != null) {
                if (mMsgJSONobj.optString(OneToOneChatJSonCreator.TYPE).equalsIgnoreCase(ConstantChat.TYPE_DELIVERY_STATUS)) {
                    ChatIds = mMsgJSONobj.optString(OneToOneChatJSonCreator.MESSAGEID);
                    commaSeperatedIds = CommonMethods.getCommaseperateToArray(ChatIds);
                    for (int i = 0; i < commaSeperatedIds.length; i++) {
                        mDataTextChat.setMessageid(commaSeperatedIds[i]);
                        mDataTextChat.setDeliveryStatus(mMsgJSONobj.optString(OneToOneChatJSonCreator.DELIVERYSTATUS));
                        new TableChat(getBaseContext()).updateChatDeliveryStatus(mDataTextChat);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void recievedContactMessage(String recievedProcessedMessageJSOn, String recieverId, String groupJId) {

        DataTextChat mDataContact = null;
        mDataContact = OneToOneChatJSonCreator.getSharedContactFromJSON(recievedProcessedMessageJSOn);
        if (xmppCON != null && xmppCON.isAuthenticated()) {
            mDataContact.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        }
        if (recieverId.contains(RECIEVED_SMACK))
            recieverId = recieverId.replace(RECIEVED_SMACK, "");
        mDataContact.setGroupJID(groupJId);
        mDataContact.setFriendChatID(recieverId);
        mDataContact.setSenderChatID(recieverId);
        mDataContact.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);

//        if(TextUtils.isEmpty(groupJId)) {
        String friendChatId = mDataContact.getFriendChatID();
        if (!ValidationUtils.containsXmppDomainName(friendChatId)) {
            friendChatId = friendChatId + "@" + SERVICE_NAME;
        }
        DataContact mDataFriendContact = new TableContact(getBaseContext())
                .getFriendDetailsByFrienChatID(friendChatId);
        if (mDataFriendContact != null) {
//            String name = mDataFriendContact.getAppName();
//            if (TextUtils.isEmpty(name)) {
//                name = mDataFriendContact.getName();
//            }
//            if(TextUtils.isEmpty(name)){
//                name = mDataContact.getFriendName();
//            }
//            mDataContact.setFriendName(name);
//            mDataContact.setSenderName(name);
        } else {
            mDataFriendContact = new DataContact();
            mDataFriendContact.setPhoneNumber(mDataContact.getFriendPhoneNo());
//            String friendChatId = mDataContact.getFriendChatID();
//            if (!friendChatId.contains("@" + SERVICE_NAME)) {
//                friendChatId = friendChatId + "@" + SERVICE_NAME;
//            }
            mDataFriendContact.setAppName(mDataContact.getSenderName());
            mDataFriendContact.setName(mDataContact.getSenderName());
            mDataFriendContact.setChatId(friendChatId);
            mDataFriendContact.setIsBlocked(0);
            mDataFriendContact.setIsFriend(0);
            mDataFriendContact.setIsRegistered(1);
            mDataFriendContact.setFriendId(mDataContact.getFriendId());
            new TableContact(getBaseContext()).insertBulkForUnknownNUmber(mDataFriendContact);
        }
//        }

        TableChat tableChat = new TableChat(getBaseContext());
        if (!tableChat.checkIfMessageExists(mDataContact.getMessageid())) {
            new TableChat(getBaseContext()).insertChat(mDataContact);
        } else {
            return;
        }
        if (mDataContact.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            if (TextUtils.isEmpty(mDataContact.getSenderName())) {
                TableGroup tableGroup = new TableGroup(getBaseContext());
                DataGroupMembers groupMember = tableGroup.getMemberInAGroup(
                        mDataContact.getStrGroupID(), mDataContact.getSenderId());
                if (groupMember != null) {
                    String name = groupMember.getUserName();

                    if (!TextUtils.isEmpty(name)) {
//                                mDataTextChat.setFriendName(name);
                        mDataContact.setSenderName(name);
                    }
                }

            }
        }

        if (mMessengerUI != null) {
            Message recievedmessage = Message.obtain();

            recievedmessage.what = ConstantXMPP.RECIEVE_ONETOONE_CONTACT_MESSAGE;
            Bundle mBundle = new Bundle();

            mBundle.putSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE, mDataContact);

            // Set the bundle data to the Message
            recievedmessage.setData(mBundle);

            sendDeliveryStatus(mDataContact);
            try {

                mMessengerUI.send(recievedmessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                addNotification(mDataContact);
                sendDeliveryStatus(mDataContact);
            }
        } else {
            addNotification(mDataContact);
            sendDeliveryStatus(mDataContact);

        }
    }

    private void recievedLocationMessage(String recievedProcessedMessageJSOn, String recieverId, String groupJId) {

        DataTextChat mDataLocation = null;
        mDataLocation = OneToOneChatJSonCreator.getSharedLocationFromJSON(recievedProcessedMessageJSOn);
        if (xmppCON != null && xmppCON.isAuthenticated()) {
            mDataLocation.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        }
        if (recieverId.contains(RECIEVED_SMACK))
            recieverId = recieverId.replace(RECIEVED_SMACK, "");
        mDataLocation.setGroupJID(groupJId);
        mDataLocation.setFriendChatID(recieverId);
        mDataLocation.setSenderChatID(recieverId);
        mDataLocation.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);

//        if(TextUtils.isEmpty(groupJId)) {
        String friendChatId = mDataLocation.getFriendChatID();
        if (!ValidationUtils.containsXmppDomainName(friendChatId)) {
            friendChatId = friendChatId + "@" + SERVICE_NAME;
        }
        DataContact mDataContact = new TableContact(getBaseContext())
                .getFriendDetailsByFrienChatID(friendChatId);
        if (mDataContact != null) {
//            String name = mDataContact.getAppName();
//            if (TextUtils.isEmpty(name)) {
//                name = mDataContact.getName();
//            }
//            if(TextUtils.isEmpty(name)){
//                name = mDataLocation.getFriendName();
//            }
//            mDataLocation.setFriendName(name);
//            mDataLocation.setSenderName(name);
        } else {
            mDataContact = new DataContact();
            mDataContact.setPhoneNumber(mDataLocation.getFriendPhoneNo());
//            String friendChatId = mDataLocation.getFriendChatID();
//            if (!friendChatId.contains("@" + SERVICE_NAME)) {
//                friendChatId = friendChatId + "@" + SERVICE_NAME;
//            }
            mDataContact.setAppName(mDataLocation.getSenderName());
            mDataContact.setName(mDataLocation.getSenderName());
            mDataContact.setChatId(friendChatId);
            mDataContact.setIsBlocked(0);
            mDataContact.setIsFriend(0);
            mDataContact.setIsRegistered(1);
            mDataContact.setFriendId(mDataLocation.getFriendId());
            new TableContact(getBaseContext()).insertBulkForUnknownNUmber(mDataContact);
        }
        TableChat tableChat = new TableChat(getBaseContext());
        if (tableChat.checkIfMessageExists(mDataLocation.getMessageid())) {
            return;
        } else {
            new TableChat(getBaseContext()).insertChat(mDataLocation);
        }

        if (mDataLocation.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            if (TextUtils.isEmpty(mDataLocation.getSenderName())) {
                TableGroup tableGroup = new TableGroup(getBaseContext());
                DataGroupMembers groupMember = tableGroup.getMemberInAGroup(
                        mDataLocation.getStrGroupID(), mDataLocation.getSenderId());
                if (groupMember != null) {
                    String name = groupMember.getUserName();

                    if (!TextUtils.isEmpty(name)) {
//                                mDataTextChat.setFriendName(name);
                        mDataLocation.setSenderName(name);
                    }
                }

            }
        }
        if (mMessengerUI != null) {
            Message recievedmessage = Message.obtain();

            recievedmessage.what = ConstantXMPP.RECIEVE_ONETOONE_CONTACT_MESSAGE;
            Bundle mBundle = new Bundle();

            mBundle.putSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE, mDataLocation);

            // Set the bundle data to the Message
            recievedmessage.setData(mBundle);

            sendDeliveryStatus(mDataLocation);
            try {

                mMessengerUI.send(recievedmessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                addNotification(mDataLocation);
                sendDeliveryStatus(mDataLocation);
            }
        } else {
            addNotification(mDataLocation);
            sendDeliveryStatus(mDataLocation);

        }
    }

    private synchronized void receivedVideoMessage(String recievedProcessedMessageJSOn, String recieverId, String groupJId) {
        DataTextChat mDataFileChat = null;
        mDataFileChat = OneToOneChatJSonCreator.chatJsonStringToVideoChat(recievedProcessedMessageJSOn,
                getBaseContext());
        if (xmppCON != null && xmppCON.isAuthenticated()) {
            mDataFileChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        }
        if (recieverId.contains(RECIEVED_SMACK))
            recieverId = recieverId.replace(RECIEVED_SMACK, "");

        mDataFileChat.setGroupJID(groupJId);
        mDataFileChat.setFriendChatID(recieverId);
        mDataFileChat.setSenderChatID(recieverId);
        mDataFileChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);

//        if(TextUtils.isEmpty(groupJId)) {
        String friendChatId = mDataFileChat.getFriendChatID();
        if (!ValidationUtils.containsXmppDomainName(friendChatId)) {
            friendChatId = friendChatId + "@" + SERVICE_NAME;
        }
        DataContact mDataContact = new TableContact(getBaseContext())
                .getFriendDetailsByFrienChatID(friendChatId);
        if (mDataContact != null) {
//            String name = mDataContact.getAppName();
//            if (TextUtils.isEmpty(name)) {
//                name = mDataContact.getName();
//            }
//            if(TextUtils.isEmpty(name)){
//                name = mDataFileChat.getFriendName();
//            }
//
//            mDataFileChat.setFriendName(name);
//            mDataFileChat.setSenderName(name);
        } else {
            mDataContact = new DataContact();
            mDataContact.setPhoneNumber(mDataFileChat.getFriendPhoneNo());
//            String friendChatId = mDataFileChat.getFriendChatID();
//            if (!friendChatId.contains("@" + SERVICE_NAME)) {
//                friendChatId = friendChatId + "@" + SERVICE_NAME;
//            }
            mDataContact.setAppName(mDataFileChat.getSenderName());
            mDataContact.setName(mDataFileChat.getSenderName());
            mDataContact.setChatId(friendChatId);
            mDataContact.setIsBlocked(0);
            mDataContact.setIsFriend(0);
            mDataContact.setIsRegistered(1);
            mDataContact.setFriendId(mDataFileChat.getFriendId());
            new TableContact(getBaseContext()).insertBulkForUnknownNUmber(mDataContact);
        }
        //Translated Message if required
        if (new TableUserInfo(this).getUser().getIstransalate().equalsIgnoreCase("1"))
            if (!StringUtils.equalsIgnoreCase(mDataFileChat.getLang(), new TableUserInfo(this).getUser().getLanguageIdentifire()))
                mDataFileChat.setStrTranslatedText(translateMessages(mDataFileChat));

        TableChat tableChat = new TableChat(getBaseContext());
        if (!tableChat.checkIfMessageExists(mDataFileChat.getMessageid())) {
            new TableChat(getBaseContext()).insertChat(mDataFileChat);
        } else {
            return;
        }

        if (mDataFileChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            if (TextUtils.isEmpty(mDataFileChat.getSenderName())) {
                TableGroup tableGroup = new TableGroup(getBaseContext());
                DataGroupMembers groupMember = tableGroup.getMemberInAGroup(
                        mDataFileChat.getStrGroupID(), mDataFileChat.getSenderId());
                if (groupMember != null) {
                    String name = groupMember.getUserName();

                    if (!TextUtils.isEmpty(name)) {
//                                mDataTextChat.setFriendName(name);
                        mDataFileChat.setSenderName(name);
                    }
                }

            }
        }
        if (mMessengerUI != null) {
            Message recievedmessage = Message.obtain();

            recievedmessage.what = ConstantXMPP.RECIEVE_ONETOONE_IMAGE_MESSAGE;
            Bundle mBundle = new Bundle();

            mBundle.putSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE, mDataFileChat);

            // Set the bundle data to the Message
            recievedmessage.setData(mBundle);

            sendDeliveryStatus(mDataFileChat);
            try {

                mMessengerUI.send(recievedmessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                addNotification(mDataFileChat);
                sendDeliveryStatus(mDataFileChat);
            }
        } else {
            addNotification(mDataFileChat);
            sendDeliveryStatus(mDataFileChat);

        }
    }

    private synchronized void receivedImageMessage(String recievedProcessedMessageJSOn,
                                                   String recieverId, String groupJId) {
        DataTextChat mDataFileChat = null;
        mDataFileChat = OneToOneChatJSonCreator.
                chatJsonStringToDataFileChat(recievedProcessedMessageJSOn, getBaseContext());
        if (xmppCON != null && xmppCON.isAuthenticated()) {
            mDataFileChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        }
        if (recieverId.contains(RECIEVED_SMACK))
            recieverId = recieverId.replace(RECIEVED_SMACK, "");

        mDataFileChat.setGroupJID(groupJId);
        mDataFileChat.setFriendChatID(recieverId);
        mDataFileChat.setSenderChatID(recieverId);
        mDataFileChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);

//        if(TextUtils.isEmpty(groupJId)) {
        String friendChatId = mDataFileChat.getFriendChatID();
        if (!ValidationUtils.containsXmppDomainName(friendChatId)) {
            friendChatId = friendChatId + "@" + SERVICE_NAME;
        }
        DataContact mDataContact = new TableContact(getBaseContext())
                .getFriendDetailsByFrienChatID(friendChatId);
        if (mDataContact != null) {
//            String name = mDataContact.getAppName();
//            if (TextUtils.isEmpty(name)) {
//                name = mDataContact.getName();
//            }
//            if(TextUtils.isEmpty(name)){
//                name = mDataFileChat.getFriendName();
//            }
//            mDataFileChat.setFriendName(name);
//            mDataFileChat.setSenderName(name);
        } else {
            mDataContact = new DataContact();
            mDataContact.setPhoneNumber(mDataFileChat.getFriendPhoneNo());
//            if (!friendChatId.contains("@" + SERVICE_NAME)) {
//                friendChatId = friendChatId + "@" + SERVICE_NAME;
//            }
            mDataContact.setAppName(mDataFileChat.getSenderName());
            mDataContact.setName(mDataFileChat.getSenderName());
            mDataContact.setChatId(friendChatId);
            mDataContact.setIsBlocked(0);
            mDataContact.setIsFriend(0);
            mDataContact.setIsRegistered(1);
            mDataContact.setFriendId(mDataFileChat.getFriendId());
            new TableContact(getBaseContext()).insertBulkForUnknownNUmber(mDataContact);
        }

        //Translated Message if required
        if (new TableUserInfo(this).getUser().getIstransalate().equalsIgnoreCase("1"))
            if (!StringUtils.equalsIgnoreCase(mDataFileChat.getLang(), new TableUserInfo(this).getUser().getLanguageIdentifire()))
                mDataFileChat.setStrTranslatedText(translateMessages(mDataFileChat));

        TableChat tableChat = new TableChat(getBaseContext());
        if (!tableChat.checkIfMessageExists(mDataFileChat.getMessageid())) {
            new TableChat(getBaseContext()).insertChat(mDataFileChat);

        } else {
            return;
        }

        if (mDataFileChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            if (TextUtils.isEmpty(mDataFileChat.getSenderName())) {
                TableGroup tableGroup = new TableGroup(getBaseContext());
                DataGroupMembers groupMember = tableGroup.getMemberInAGroup(
                        mDataFileChat.getStrGroupID(), mDataFileChat.getSenderId());
                if (groupMember != null) {
                    String name = groupMember.getUserName();

                    if (!TextUtils.isEmpty(name)) {
//                                mDataTextChat.setFriendName(name);
                        mDataFileChat.setSenderName(name);
                    }
                }

            }
        }
        LogUtils.i(LOG_TAG, "ImageReceived:Name: " + mDataFileChat.getFileUrl());
        if (mMessengerUI != null) {
            Message recievedmessage = Message.obtain();

            recievedmessage.what = ConstantXMPP.RECIEVE_ONETOONE_IMAGE_MESSAGE;
            Bundle mBundle = new Bundle();

            mBundle.putSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE, mDataFileChat);

            // Set the bundle data to the Message
            recievedmessage.setData(mBundle);

            sendDeliveryStatus(mDataFileChat);
            try {

                mMessengerUI.send(recievedmessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                addNotification(mDataFileChat);
                sendDeliveryStatus(mDataFileChat);
            }
        } else {
            addNotification(mDataFileChat);
            sendDeliveryStatus(mDataFileChat);

        }
    }

    private void recievedFirstTimeStickerMessageAccepted(String recievedProcessedMessageJSOn, String recieverId, String groupJId) {


        try {
            JSONObject mMsgJSONobj = new JSONObject(recievedProcessedMessageJSOn);
            if (mMsgJSONobj != null) {
                String name = mMsgJSONobj.optString(OneToOneChatJSonCreator.SENDERNAME, "");
                String phoneNumberWithCountryCode = mMsgJSONobj.optString(OneToOneChatJSonCreator.SENDERPHONE, "");

                if (!TextUtils.isEmpty(phoneNumberWithCountryCode)) {
                    DataContact mDataContact = new TableContact(getBaseContext()).getFriendDetailsByPhoneNumber(phoneNumberWithCountryCode);
                    CommonMethods.writePhoneContact(CommonMethods.getUTFDecodedString(name), "+" + phoneNumberWithCountryCode, getBaseContext());
                }

                LogUtils.i(LOG_TAG, "1stTimeStickerAccepted: Saved to device contact" + "Name: "
                        + name + "phoneNumberWithCountryCode:" + phoneNumberWithCountryCode);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void recievedFirstTimeStickerMessage(String recievedProcessedMessageJSOn, String recieverId, String groupJId) {

        DataTextChat mDataTextChat = null;
        mDataTextChat = OneToOneChatJSonCreator.getFirstTimeStickerFromJSON(recievedProcessedMessageJSOn);
        if (xmppCON != null && xmppCON.isAuthenticated()) {
            mDataTextChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        }
        if (recieverId.contains(RECIEVED_SMACK))
            recieverId = recieverId.replace(RECIEVED_SMACK, "");

        mDataTextChat.setGroupJID(groupJId);
        mDataTextChat.setFriendChatID(recieverId);
        mDataTextChat.setSenderChatID(recieverId);
        mDataTextChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);

        //Translated Message if required
        if (new TableUserInfo(this).getUser().getIstransalate().equalsIgnoreCase("1"))
//            if (!StringUtils.equalsIgnoreCase(mDataTextChat.getLang(), new TableUserInfo(this).getUser().getLanguageIdentifire()))
            mDataTextChat.setStrTranslatedText(translateMessages(mDataTextChat));

//        if(TextUtils.isEmpty(groupJId)) {
        String friendChatId = mDataTextChat.getFriendChatID();
        if (!ValidationUtils.containsXmppDomainName(friendChatId)) {
            friendChatId = friendChatId + "@" + SERVICE_NAME;
        }
        DataContact mDataContact = new TableContact(getBaseContext()).
                getFriendDetailsByFrienChatID(friendChatId);
        if (mDataContact != null) {
//            String name = mDataContact.getAppName();
//            if (TextUtils.isEmpty(name)) {
//                name = mDataContact.getName();
//            }
//            mDataTextChat.setFriendName(name);
//
//            if (mDataContact.getIsFriend() == 1) {
//                handleFirstTimeStickerMessageForContactsAlreadySaved(mDataTextChat);
//            }
        } else {
            mDataContact = new DataContact();
            mDataContact.setPhoneNumber(mDataTextChat.getFriendPhoneNo());
//            String friendChatId = mDataTextChat.getFriendChatID();
//            if (!friendChatId.contains("@" + SERVICE_NAME)) {
//                friendChatId = friendChatId + "@" + SERVICE_NAME;
//            }
            mDataContact.setChatId(friendChatId);
            mDataContact.setAppName(mDataTextChat.getSenderName());
            mDataContact.setName(mDataTextChat.getSenderName());
            mDataContact.setIsBlocked(0);
            mDataContact.setIsFriend(0);
            mDataContact.setIsRegistered(1);
            mDataContact.setFriendId(mDataTextChat.getFriendId());
            new TableContact(getBaseContext()).insertBulkForUnknownNUmber(mDataContact);
        }
//        }
        new TableChat(getBaseContext()).insertChat(mDataTextChat);


        if (mMessengerUI != null) {
            Message recievedmessage = Message.obtain();

            recievedmessage.what = ConstantXMPP.RECIEVE_ONETOONE_TEXT_MESSAGE;
            Bundle mBundle = new Bundle();

            mBundle.putSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE, mDataTextChat);

            // Set the bundle data to the Message
            recievedmessage.setData(mBundle);

            sendDeliveryStatus(mDataTextChat);
            try {

                mMessengerUI.send(recievedmessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                addNotification(mDataTextChat);
                sendDeliveryStatus(mDataTextChat);
            }
        } else {
            addNotification(mDataTextChat);
            sendDeliveryStatus(mDataTextChat);

        }
    }

    private void handleFirstTimeStickerMessageForContactsAlreadySaved(final DataTextChat mDataTextChat) {
        AsyncSetFriendStatus asyncSetFriendStatus = new AsyncSetFriendStatus(mDataProfile.getUserId(),
                mDataTextChat.getSenderId(), mDataTextChat.getImageUrl(), new WebServiceCallBack<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse result) {
                onSetFriendStatusApiCallSuccess(result, mDataTextChat);

            }

            @Override
            public void onFailure(BaseResponse result) {
                onSetFriendStatusApiCallFailed(result.getResponseDetails(), mDataTextChat);
            }

            @Override
            public void onFailure(Throwable e) {
                onSetFriendStatusApiCallFailed(CommonMethods.getAlertMessageFromException(getBaseContext(), e), mDataTextChat);
            }
        });

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            asyncSetFriendStatus.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncSetFriendStatus.execute();
        }
    }

    private void onSetFriendStatusApiCallSuccess(BaseResponse result, final DataTextChat mDataTextChat) {

        Message chatDeliveryMessage = Message.obtain();
        chatDeliveryMessage.what = ConstantXMPP.SEND_FIRST_TIME_STICKER_ACCEPT_MESSAGE;
        String firstTimeStickerAcceptMessageJSON = OneToOneChatJSonCreator.
                getFirstTimeStickerAcceptMessageJSON(
                        mDataProfile.getCountryCode() + mDataProfile.getPhoneNo(), mDataProfile.getUsername());

        //make bundle
        Bundle mBundle = new Bundle();
        mBundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, firstTimeStickerAcceptMessageJSON);
        mBundle.putSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE, mDataTextChat);
        chatDeliveryMessage.setData(mBundle);
        try {
            mMessenger.send(chatDeliveryMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onSetFriendStatusApiCallFailed(String failureAlert, final DataTextChat mDataTextChat) {

    }

    private void recievedMessage(String recievedProcessedMessageJSOn, String recieverId, String groupJId) {

        DataTextChat mDataTextChat = null;
        mDataTextChat = OneToOneChatJSonCreator.getTextMessageFromJSON(recievedProcessedMessageJSOn);
        if (xmppCON != null && xmppCON.isAuthenticated()) {
            mDataTextChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        }
        if (recieverId.contains(RECIEVED_SMACK))
            recieverId = recieverId.replace(RECIEVED_SMACK, "");

        mDataTextChat.setGroupJID(groupJId);
        mDataTextChat.setFriendChatID(recieverId);
        mDataTextChat.setSenderChatID(recieverId);
        mDataTextChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);

        //Translated Message if required
        if (new TableUserInfo(this).getUser().getIstransalate().equalsIgnoreCase("1"))
//            if (!StringUtils.equalsIgnoreCase(mDataTextChat.getLang(), new TableUserInfo(this).getUser().getLanguageIdentifire()))
            mDataTextChat.setStrTranslatedText(translateMessages(mDataTextChat));

//        if(TextUtils.isEmpty(groupJId)) {
        String friendChatId = mDataTextChat.getFriendChatID();
        if (!ValidationUtils.containsXmppDomainName(friendChatId)) {
            friendChatId = friendChatId + "@" + SERVICE_NAME;
        }
        DataContact mDataContact = new TableContact(getBaseContext()).
                getFriendDetailsByFrienChatID(friendChatId);
        if (mDataContact != null) {
//            String name = mDataContact.getAppName();
//            if (TextUtils.isEmpty(name)) {
//                name = mDataContact.getName();
//            }
//
//            if(TextUtils.isEmpty(name)){
//                name = mDataTextChat.getFriendName();
//            }
//            mDataTextChat.setFriendName(name);
//            mDataTextChat.setSenderName(name);
        } else {
            mDataContact = new DataContact();
            mDataContact.setPhoneNumber(mDataTextChat.getFriendPhoneNo());
//            String friendChatId = mDataTextChat.getFriendChatID();
//            if (!friendChatId.contains("@" + SERVICE_NAME)) {
//                friendChatId = friendChatId + "@" + SERVICE_NAME;
//            }
            mDataContact.setChatId(friendChatId);
            mDataContact.setAppName(mDataTextChat.getSenderName());
            mDataContact.setName(mDataTextChat.getSenderName());
            mDataContact.setIsBlocked(0);
            mDataContact.setIsFriend(0);
            mDataContact.setIsRegistered(1);
            mDataContact.setFriendId(mDataTextChat.getFriendId());
            new TableContact(getBaseContext()).insertBulkForUnknownNUmber(mDataContact);
        }
//        }
        TableChat tableChat = new TableChat(getBaseContext());
        if (!tableChat.checkIfMessageExists(mDataTextChat.getMessageid())) {
            new TableChat(getBaseContext()).insertChat(mDataTextChat);
        } else {
            return;
        }

        if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            if (TextUtils.isEmpty(mDataTextChat.getSenderName())) {
                TableGroup tableGroup = new TableGroup(getBaseContext());
                DataGroupMembers groupMember = tableGroup.getMemberInAGroup(
                        mDataTextChat.getStrGroupID(), mDataTextChat.getSenderId());
                if (groupMember != null) {
                    String name = groupMember.getUserName();

                    if (!TextUtils.isEmpty(name)) {
//                                mDataTextChat.setFriendName(name);
                        mDataTextChat.setSenderName(name);
                    }
                }

            }
        }

        if (mMessengerUI != null) {
            Message recievedmessage = Message.obtain();

            recievedmessage.what = ConstantXMPP.RECIEVE_ONETOONE_TEXT_MESSAGE;
            Bundle mBundle = new Bundle();

            mBundle.putSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE, mDataTextChat);

            // Set the bundle data to the Message
            recievedmessage.setData(mBundle);

            sendDeliveryStatus(mDataTextChat);
            try {

                mMessengerUI.send(recievedmessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                addNotification(mDataTextChat);
                sendDeliveryStatus(mDataTextChat);
            }
        } else {
            addNotification(mDataTextChat);
            sendDeliveryStatus(mDataTextChat);

        }

        try {
            ShortcutBadger.applyCount(getApplicationContext(), getBadgeCount());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addNotification(DataTextChat mDataTextChat) {

        /*add intent to notificaition*/
        Intent notificationIntent = new Intent(getApplicationContext(), ActivityChat.class);

//        get data using friend id
        DataContact mDataContact = new TableContact(getBaseContext()).getFriendDetailsByFrienChatID(mDataTextChat.getFriendChatID());
//        notificationIntent.putExtra(Constants.B_ID, mDataContact.getFriendId());
//        notificationIntent.putExtra(ConstantDB.ChatId, mDataContact.getChatId());
//        notificationIntent.putExtra(ConstantDB.PhoneNo, mDataContact.getFriendId());

        if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            notificationIntent.putExtra(Constants.B_NAME, mDataTextChat.getFriendName());
            notificationIntent.putExtra(Constants.B_ID, mDataContact.getFriendId());
            notificationIntent.putExtra(Constants.B_RESULT, mDataTextChat.getFriendChatID());
            notificationIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_SINGLECHAT);
            notificationIntent.putExtra(ConstantDB.ChatId, mDataTextChat.getFriendChatID());
            notificationIntent.putExtra(ConstantDB.PhoneNo, mDataTextChat.getFriendPhoneNo());
            notificationIntent.putExtra(ConstantDB.FriendImageLink, mDataContact.getAppFriendImageLink());
        } else if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            notificationIntent.putExtra(Constants.B_ID, mDataTextChat.getStrGroupID());
            mDataTextChat.setGroupName(new TableGroup(this).getGroupById(mDataTextChat.getStrGroupID()).getGroupName());
            notificationIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_GROUPCHAT);
        }
        notificationIntent.putExtra("fromNotification", "true");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        String friendName = TextUtils.isEmpty(mDataContact.getAppName()) ? (TextUtils.isEmpty(mDataContact.getName()) ? "" : mDataContact.getName()) : mDataContact.getAppName();

//        if (TextUtils.isEmpty(friendName)) {
//            if(!TextUtils.isEmpty(mDataTextChat.getSenderName())) {
//                friendName = "+" + mDataTextChat.getSenderName();
//            }else{
//                friendName = "+" + mDataTextChat.getFriendPhoneNo();
//            }
//        }
//        mDataTextChat.setFriendName(friendName);
//        mDataTextChat.setSenderName(friendName);


        NotificationUtils.showNotificationMessage(notificationIntent, this, mDataTextChat);
    }

    public int getBadgeCount() {
        TableChat tableChat = new TableChat(this);
        String msgCount = tableChat.getUnreadOnetoOneMessagesCount(ConstantChat.STATUS_DELIVERED, mPrefs.getChatId());
        int oneToOneCount = 0;
        try {
            oneToOneCount = Integer.parseInt(msgCount);
        } catch (NumberFormatException e) {
        }

        String groupMessageCount = tableChat.getUnreadGroupMessagesCount(ConstantChat.STATUS_DELIVERED, mPrefs.getChatId());

        int groupCount = 0;
        try {
            groupCount = Integer.parseInt(groupMessageCount);
        } catch (NumberFormatException e) {
        }


        return oneToOneCount + groupCount;

    }

    private void registerThread() {
        String result = "";
        try {
            String localVarUserId = userId;
            if (ValidationUtils.containsXmppDomainName(localVarUserId)) {
                localVarUserId = localVarUserId.split("@")[0];
            }
            String url = SERVICE_URL + "?type=add&secret="
                    + SERVICE_SECRET + "&username=" + /*userId.replace("@" + SERVICE_NAME, "")*/localVarUserId
                    + "&password=" + /*userId.replace("@" + SERVICE_NAME, "")*/localVarUserId
                    + "&groups=" + GROUP_NAME + "&subscription=1";
//            password = URLEncoder.encode(PASSWORD, "UTF-8");
//            ArrayList<NameValuePair> mListPair = new ArrayList<>();
//            mListPair.add(new BasicNameValuePair("username", userId.replace("@"+SERVICE_NAME, "")));
//            mListPair.add(new BasicNameValuePair("username", userId.replace("@"+SERVICE_NAME, "")));
//            mListPair.add(new BasicNameValuePair("type", "add"));
//            mListPair.add(new BasicNameValuePair("secret", SERVICE_SECRET));
//            mListPair.add(new BasicNameValuePair("groups", GROUP_NAME));
//            mListPair.add(new BasicNameValuePair("subscription", "1"));
//            String url = SERVICE_URL ;
            LogUtils.i(LOG_TAG, "url :" + url);
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(url);
//            result = mHttpConnectionClass.getResponse(mListPair);
            result = mHttpConnectionClass.getResponse(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.i(LOG_TAG, "result: " + result);
        if (result.contains("ok") || result.contains("UserAlreadyExistsException")) {
            mExecutorService = Executors.newFixedThreadPool(1);
            mRunnableXmpp = new RunnableXmpp(TYPE.LOGIN);
            mExecutorService.execute(mRunnableXmpp);
        } else {

        }
    }

    private void setUpMuc() {
        if (xmppCON != null && xmppCON.isAuthenticated()) {
            mucManager = MultiUserChatManager.getInstanceFor(xmppCON);
//            mucManager.addInvitationListener(new InvitationListener() {
//                @Override
//                public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, org.jivesoftware.smack.packet.Message message) {
//                    try {
//                        Log.i("invitationReceived", "inviter: " + inviter);
//                        room.join(xmppCON.getUser());
//                    } catch (SmackException.NoResponseException e) {
//                        e.printStackTrace();
//                    } catch (XMPPException.XMPPErrorException e) {
//                        e.printStackTrace();
//                    } catch (SmackException.NotConnectedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

            try {
                reJoinGroups();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void reJoinGroups() {

        if (xmppCON == null || !xmppCON.isAuthenticated()) {
            retryXMPPCOnnection();
            return;
        }
        ArrayList<DataGroup> mListGroup = new TableGroup(getBaseContext()).getGroupList();
        if (mListGroup != null) {
            for (DataGroup group : mListGroup) {
                String groupXmppId = group.getGroupJId();
                LogUtils.i(LOG_TAG, "Rejoin group:Starting" + groupXmppId + "@conference." + SERVICE_NAME);
                try {
                    if (!TextUtils.isEmpty(groupXmppId)) {
                        MultiUserChat muc = mucManager.getMultiUserChat(groupXmppId
                                + "@conference." + SERVICE_NAME);

//                    RoomInfo roomInfo = mucManager.getRoomInfo(groupXmppId + "@conference." + SERVICE_NAME);

                        if (!muc.isJoined()) {
                            DiscussionHistory history = new DiscussionHistory();
                            history.setMaxStanzas(0);
                            String nickName = XmppStringUtils.parseBareJid(xmppCON.getUser());
                            if (TextUtils.isEmpty(nickName)) {
                                nickName = "";
                            }
                            muc.join(nickName, null, history, SmackConfiguration.getDefaultPacketReplyTimeout());
                        }

                        LogUtils.i(LOG_TAG, "Rejoin group:Success" + muc.getRoom() + "Name: " + group.getGroupName());

                    }
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                    LogUtils.i(LOG_TAG, "Rejoin group:" + groupXmppId + "@conference." + SERVICE_NAME + "Error: " + e.getMessage());
                } catch (SmackException e) {
                    e.printStackTrace();
                    LogUtils.i(LOG_TAG, "Rejoin group:" + groupXmppId + "@conference." + SERVICE_NAME + "Error: " + e.getMessage());
                }
                // Openfire needs some time to collect the owners list
//                    }

            }
        }

    }

    private void createGroup(Bundle mBundle) {
        if (mBundle != null) {
            String groupName = mBundle.getString("group_name", "");
            String groupJId = mBundle.getString("group_jid", "");
            String groupMember = mBundle.getString("group_members", "");


            try {
                MultiUserChat muc = mucManager.getMultiUserChat(groupJId + "@conference." + SERVICE_NAME);
                muc.create(XmppStringUtils.parseBareJid(xmppCON.getUser()));

                // Get the the room's configuration form
                Form form = muc.getConfigurationForm();
// Create a new form to submit based on the original form
                Form submitForm = form.createAnswerForm();
// Add default answers to the form to submit
                List<FormField> fields = form.getFields();
                for (int i = 0; i < fields.size(); i++) {
                    FormField field = fields.get(i);
                    if (!FormField.Type.hidden.equals(field.getType()) && field.getVariable() != null) {
                        // Sets the default value as the answer
                        submitForm.setDefaultAnswer(field.getVariable());
                    }
                }
//                submitForm.setAnswer("muc#roomconfig_publicroom", true);
                submitForm.setAnswer("muc#roomconfig_roomname", groupName);
                List<String> maxUser = new ArrayList<String>();
                maxUser.add("50");
                submitForm.setAnswer("muc#roomconfig_maxusers", maxUser);

                List<String> owners = new ArrayList<String>();
                owners.add(0, XmppStringUtils.parseBareJid(xmppCON.getUser()));
                String[] members = groupMember.split(",");
                if (members != null) {
                    for (String chatId : members)
                        if (!chatId.equalsIgnoreCase(XmppStringUtils.parseBareJid(xmppCON.getUser())))
                            owners.add(chatId);
                }
//                for (DataGroupMember groupMember : dataCreateOrEditGroup.getDataGroupMemberArrayList()) {
//                    if (!xmppCON.getUser().equalsIgnoreCase(groupMember.getChatId())) {
//                        owners.add(groupMember.getChatId());
//                    }
//                }
//                submitForm.setAnswer("muc#roomconfig_roomowners", owners);
                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", false);
                submitForm.setAnswer("x-muc#roomconfig_registration", false);

                submitForm.setAnswer("muc#roomconfig_whois", Arrays.asList("anyone"));
                submitForm.setAnswer("muc#roomconfig_persistentroom", true);
                muc.sendConfigurationForm(submitForm);

                LogUtils.i(LOG_TAG, "createGroup: Success");
                //Create a bookmark for the group inorder to auto join on login
//                    BookmarkManager bookmarkManager = BookmarkManager.getBookmarkManager(xmppCON);
//                    bookmarkManager.addBookmarkedConference(groupName, roomId, true, nick, null);
//                    muc.addMessageListener(mGroupChatListener);


                if (mMessengerUI != null) {
                    Message msg = Message.obtain();

                    msg.what = ConstantXMPP.CREATE_GROUP_SUCCESS;
                    try {
                        mMessengerUI.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (mMessengerUI != null) {
                    Message msg = Message.obtain();

                    msg.what = ConstantXMPP.CREATE_GROUP_FAILURE;
                    try {
                        mMessengerUI.send(msg);
                    } catch (RemoteException re) {
                        re.printStackTrace();
                    }
                }

            }

        }
    }

    private void leaveGroup(Bundle bundle) {
        try {
            String groupJId = bundle.getString("group_jid", "");
            LogUtils.i(LOG_TAG, "leave group: groupId:" + groupJId);
            MultiUserChat muc = mucManager.getMultiUserChat(groupJId + "@conference." + SERVICE_NAME);

            if (muc.isJoined()) {
                muc.leave();
            }
            LogUtils.i(LOG_TAG, "leave group:Success: groupId:" + groupJId);
        } catch (SmackException e) {
            e.printStackTrace();
        }

    }

    private void configure() {

        // Private Data Storage
        ProviderManager.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

        // Time
        try {
            ProviderManager.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            LogUtils.i(LOG_TAG, "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        // Roster Exchange
//        ProviderManager.addExtensionProvider("x", "jabber:x:roster", ((Object)new RosterExchangeProvider()));
//        // Message Events
//        ProviderManager.addExtensionProvider("x", "jabber:x:event",  ((Object)new Messag));

        // Chat State
        ProviderManager.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        ProviderManager.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        ProviderManager.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        ProviderManager.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        ProviderManager.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        // XHTML
        ProviderManager.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        // Group Chat Invitations
        ProviderManager.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

        // Service Discovery # Items
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

        // Service Discovery # Info
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

        // Data Forms
        ProviderManager.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        // MUC User
        ProviderManager.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

        // MUC Admin
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

        // MUC Owner
        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

        // Delayed Delivery
        ProviderManager.addExtensionProvider("x", "jabber:x:delay", new LegacyDelayInformationProvider());

        // Version
        try {
            ProviderManager.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }

        // VCard
        ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        // Offline Message Requests
        ProviderManager.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        ProviderManager.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

        // Last Activity
        ProviderManager.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        // User Search
        ProviderManager.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        // SharedGroupsInfo
        ProviderManager.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        ProviderManager.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

        // FileTransfer
        ProviderManager.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

        ProviderManager.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

//        ProviderManager.addIQProvider("open", "http://jabber.org/protocol/ibb", new OpenIQProvider());
//        ProviderManager.addIQProvider("data", "http://jabber.org/protocol/ibb", new DataPacketProvider());
//        ProviderManager.addIQProvider("close", "http://jabber.org/protocol/ibb", new CloseIQProvider());
//        ProviderManager.addExtensionProvider("data", "http://jabber.org/protocol/ibb", new DataPacketProvider());

        // ProviderManager.addIQProvider("open","http://jabber.org/protocol/ibb", new
        // IBBProviders.Open());
        //
        // ProviderManager.addIQProvider("close","http://jabber.org/protocol/ibb", new
        // IBBProviders.Close());
        //
        // ProviderManager.addExtensionProvider("data","http://jabber.org/protocol/ibb", new
        // IBBProviders.Data());
        //
        // Privacy
        ProviderManager.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());

        ProviderManager.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        ProviderManager.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        ProviderManager.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        ProviderManager.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        ProviderManager.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        ProviderManager.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
    }

    private void sendChatState(Bundle bundle) {
        try {
            org.jivesoftware.smack.packet.Message mMessage = new org.jivesoftware.smack.packet.Message();
            String chatType = bundle.getString(OneToOneChatJSonCreator.CHATTYPE);
            ChatState chatState = (ChatState) bundle.getSerializable(Constants.B_OBJ);
            final String toId = bundle.getString(Constants.B_ID);
            String myChatId = bundle.getString("my_chat_id");
            final String chatStateJson = OneToOneChatJSonCreator.getChateStateJson(chatType, myChatId, chatState);
            mMessage.setBody(chatStateJson);

            if (chatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
                if (ValidationUtils.containsXmppDomainName(toId)) {
//                    if (toId.contains(SERVICE_NAME))
                    mMessage.setTo(toId);
                } else {
                    mMessage.setTo(toId + "@" + SERVICE_NAME);
                }
//                mMessage.addExtension(new ComposingMessagePacketExtension());
                LogUtils.i(LOG_TAG, "XMPPDeliveryStatusSend" + chatStateJson);
                //ToastUtils.showAlertToast(getApplicationContext(), processedDeliveryStatusJSONMessage, ToastType.SUCESS_ALERT);

//                ChatStateManager.getInstance(xmppCON).setCurrentState();
                xmppCON.sendStanza(mMessage);
            } else if (chatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {

                boolean groupSendSuccess = sendGroupMessage(toId, chatStateJson);
                if (!groupSendSuccess) {
                    boolean groupJoinSuccess = joinGroup(toId);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendGroupMessage(toId, chatStateJson);
                        }
                    }, 1000);
                }

            }


        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private boolean sendGroupMessage(String toId, String chatJson) {
        MultiUserChat muc = mucManager.getMultiUserChat(toId + "@conference." + SERVICE_NAME);
        if (muc.isJoined()) {
            try {
                muc.sendMessage(chatJson);
                LogUtils.i(LOG_TAG, "group send message success");
                return true;
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.i(LOG_TAG, "group: " + toId + " not joined");
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);
            try {
                muc.join(XmppStringUtils.parseBareJid(xmppCON.getUser()), null, history, SmackConfiguration.getDefaultPacketReplyTimeout());
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                muc.sendMessage(chatJson);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean joinGroup(String toId) {
        try {
            MultiUserChat muc = mucManager.getMultiUserChat(toId + "@conference." + SERVICE_NAME);
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);

            muc.join(XmppStringUtils.parseBareJid(xmppCON.getUser()), null, history, SmackConfiguration.getDefaultPacketReplyTimeout());

            return true;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void sendFirstTimeStickerAcceptedMessage(Bundle bundle) {
        try {
            org.jivesoftware.smack.packet.Message mMessage = new org.jivesoftware.smack.packet.Message();
            String processedSendJSONchatMessage = (String) bundle.get(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE);
            DataTextChat mDataTextChat = (DataTextChat) bundle.getSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE);
            mMessage.setBody(processedSendJSONchatMessage);

            if (ValidationUtils.containsXmppDomainName(mDataTextChat.getFriendChatID())) {
//                if (mDataTextChat.getFriendChatID().contains(SERVICE_NAME))
                mMessage.setTo(mDataTextChat.getFriendChatID());
            } else {
                mMessage.setTo(mDataTextChat.getFriendChatID() + "@" + SERVICE_NAME);
            }
            xmppCON.sendStanza(mMessage);

            // ToastUtils.showAlertToast(getApplicationContext(), processedSendJSONchatMessage, ToastType.SUCESS_ALERT);
            LogUtils.i(LOG_TAG, "sendMessageXMPP: " + processedSendJSONchatMessage);
//            updateSentStatusToDB(mDataTextChat);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }

    private void addMemberToGroup(Bundle bundle) {
        //TODO
    }

    private void removeMemberFromGroup(Bundle bundle) {
        try {
            String groupJId = bundle.getString("group_jid", "");
            String memberJId = bundle.getString("group_member_jid", "");
            if (TextUtils.isEmpty(memberJId)) {
                LogUtils.i(LOG_TAG, "removeMember: Failed groupId:" + groupJId + " memberJId: empty");
                return;
            }

            LogUtils.i(LOG_TAG, "removeMember: starting: groupId:" + groupJId + " memberJId: " + memberJId);
            MultiUserChat muc = mucManager.getMultiUserChat(groupJId + "@conference." + SERVICE_NAME);

            if (muc.isJoined()) {
                try {
                    muc.kickParticipant(memberJId, "");
                    LogUtils.i(LOG_TAG, "removeMember:Success: groupId:" + groupJId + " memberJId: " + memberJId);
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();

                }
//                muc.getParticipants()
            }

        } catch (SmackException e) {
            e.printStackTrace();
        }
    }

    private void checkFriendOfflineAndPostMessageToApi(DataTextChat mDataTextChat) {


        if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
//
//            if (XmppStatusManager.retrieveStatusMessage(mDataTextChat.getFriendChatID(),
//                    xmppCON).equalsIgnoreCase("Offline")) {
            new AsyncSendPushNotification(mDataProfile.getUserId(),
                    mDataTextChat.getFriendChatID(),
                    "0",
                    NotificationUtils.
                            getNotificationBodyAsPerMessageType(mDataTextChat),
                    mDataTextChat.getMessageType(), "", null).execute();
//            }

        } else if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {

            TableGroup tableGroup = new TableGroup(getBaseContext());
            DataGroup mGroup = tableGroup.getGroupByGroupJId(mDataTextChat.getGroupJID());
            String groupId = "";
            String groupName = "";
            if (mGroup != null) {
                groupId = mGroup.getGroupId();
                groupName = mGroup.getGroupName();
//                if (mGroup.getMemberdetails() != null) {
//                    for (DataGroupMembers member : mGroup.getMemberdetails()) {
//                        String memberChatId = member.chatId;
//
//                        if (!TextUtils.isEmpty(memberChatId)) {
//                            if (!ValidationUtils.containsXmppDomainName(memberChatId)) {
//                                memberChatId = memberChatId + "@" + SERVICE_NAME;
//                            }
//                            if (XmppStatusManager.retrieveStatusMessage(
//                                    memberChatId,
//                                    xmppCON).equalsIgnoreCase("Offline")) {
                new AsyncSendPushNotification(mPrefs.getUserId(),
                        "0",
                        groupId,
                        NotificationUtils.getNotificationBodyAsPerMessageType(mDataTextChat),
                        mDataTextChat.getMessageType(), groupName, null).execute();
//                            }
//
//                        }
//                    }
//                }
            }
        }

    }

    private enum TYPE {
        LOGIN, REGISTER
    }

    public class ComposingMessagePacketExtension implements PacketExtension {

        @Override
        public String getElementName() {
            return "composing";
        }

        @Override
        public String getNamespace() {
            return "http://jabber.org/protocol/chatstates";
        }

        @Override
        public String toXML() {
            return "<composing xmlns='http://jabber.org/protocol/chatstates'/>";
        }

    }

    private final class ChatListenerImpl implements StanzaListener, ChatStateListener {

        @Override
        public void stateChanged(Chat chat, ChatState state) {
            LogUtils.i(LOG_TAG, "ChatStateChanged" + state.toString());
        }


        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

            org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
            String recievedProcessedMessageJSOn = message.getBody();
            String recieverId = message.getFrom();
            LogUtils.i("XMPPStatusRecieve", message.toString());
            if (message.getType().equals(org.jivesoftware.smack.packet.Message.Type.groupchat)) {

                String groupJId = XmppStringUtils.parseLocalpart(XmppStringUtils.parseBareJid(recieverId));
                String senderId = XmppStringUtils.parseBareJid(XmppStringUtils.parseResource(recieverId));
//                    XmppStringUtils.parseBareJid(xmppCON.getUser())
                if (!TextUtils.isEmpty(senderId)) {
                    if (senderId.equalsIgnoreCase(XmppStringUtils.parseBareJid(xmppCON.getUser()))) {
                        return;
                    }

                    DataContact mDataContact = new TableContact(getBaseContext()).getFriendDetailsByFrienChatID(senderId.trim());
                    int isBlocked = 0;
                    if (mDataContact != null) {
                        isBlocked = mDataContact.getIsBlocked();
                    }
                    if (isBlocked == 0) {
                        TableGroup tableGroup = new TableGroup(getBaseContext());
                        DataGroup mGroup = tableGroup.getGroupByGroupJId(groupJId);
                        String groupId = "";
                        if (mGroup != null) {
                            groupId = mGroup.getGroupId();

                            DataGroupMembers member = tableGroup.getMemberInAGroup(groupId, mDataProfile.getUserId());

                            if (member != null) {
                                if (member.getIsGrpblock().equalsIgnoreCase("1")) {
                                    return;
                                }
                            } else {
                                return;
                            }
                        }


                        if (!TextUtils.isEmpty(recievedProcessedMessageJSOn)) {
                            JSONObject mMsgJSONobj = null;
                            try {
                                mMsgJSONobj = new JSONObject(recievedProcessedMessageJSOn);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (mMsgJSONobj != null) {
                                String messageType = "";
                                String type = "";
                                type = OneToOneChatJSonCreator.getType(mMsgJSONobj);
                                messageType = OneToOneChatJSonCreator.getMessageType(mMsgJSONobj);
                                if (type.equalsIgnoreCase("status")) {
                                    receivedChatState(senderId, groupJId, mMsgJSONobj);
                                } else if (type.equalsIgnoreCase(ConstantChat.TYPE_DELIVERY_STATUS)) {
                                    String[] messageIds = CommonMethods.getCommaseperateToArray(mMsgJSONobj.optString(OneToOneChatJSonCreator.MESSAGEID, ""));
                                    DataTextChat mDataTextChat = new DataTextChat();
                                    for (int i = 0; i < messageIds.length; i++) {
                                        mDataTextChat.setMessageid(messageIds[i]);
                                        mDataTextChat.setDeliveryStatus(mMsgJSONobj.optString(OneToOneChatJSonCreator.DELIVERYSTATUS));
                                        new TableChat(getBaseContext()).updateChatDeliveryStatus(mDataTextChat);
                                    }
                                    LogUtils.i("XMPPStatusRecieve", "TYPE_DELIVERY_STATUS");
                                }
                                //recieve contact
                                else if (messageType.equalsIgnoreCase(ConstantChat.CONTACT_TYPE)) {
                                    recievedContactMessage(recievedProcessedMessageJSOn, senderId, groupJId);
                                } else if (messageType.equalsIgnoreCase(ConstantChat.LOCATION_TYPE)) {
                                    recievedLocationMessage(recievedProcessedMessageJSOn, senderId, groupJId);
                                } else if (messageType.equalsIgnoreCase(ConstantChat.IMAGE_TYPE)) {
                                    receivedImageMessage(recievedProcessedMessageJSOn, senderId, groupJId);
                                } else if (messageType.equalsIgnoreCase(ConstantChat.IMAGECAPTION_TYPE)) {
                                    receivedImageMessage(recievedProcessedMessageJSOn, senderId, groupJId);
                                } else if (messageType.equalsIgnoreCase(ConstantChat.SKETCH_TYPE)) {
                                    receivedImageMessage(recievedProcessedMessageJSOn, senderId, groupJId);
                                } else if (messageType.equalsIgnoreCase(ConstantChat.VIDEO_TYPE)) {
                                    receivedVideoMessage(recievedProcessedMessageJSOn, senderId, groupJId);
                                } else if (messageType.equalsIgnoreCase(ConstantChat.YOUTUBE_TYPE)) {
                                    receivedYoutubeMessage(recievedProcessedMessageJSOn, senderId, groupJId);
                                } else if (messageType.equalsIgnoreCase(ConstantChat.YAHOO_TYPE)) {
                                    receivedYahooMessage(recievedProcessedMessageJSOn, senderId, groupJId);
                                } else if (messageType.equalsIgnoreCase(ConstantChat.MESSAGE_TYPE)) {
                                    recievedMessage(recievedProcessedMessageJSOn, senderId, groupJId);
                                }
                            }
                        }
                    }
                }
            } else {
                if (recieverId.contains("/"))
                    recieverId = message.getFrom().split("/")[0];
//checked whether friend is blocked or not
                DataContact mDataContact = new TableContact(getBaseContext()).getFriendDetailsByFrienChatID(recieverId.trim());
                int isBlocked = 0;
                if (mDataContact != null) {
                    isBlocked = mDataContact.getIsBlocked();
                }
                if (isBlocked == 0) {
                    if (!TextUtils.isEmpty(recievedProcessedMessageJSOn)) {
                        JSONObject mMsgJSONobj = null;
                        try {
                            mMsgJSONobj = new JSONObject(recievedProcessedMessageJSOn);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String type = "";
                        if (mMsgJSONobj != null) {

                            type = OneToOneChatJSonCreator.getType(mMsgJSONobj);
                        }
                        if (type.equalsIgnoreCase(ConstantChat.FIRST_TIME_STICKER_ACCEPTED)) {

                            LogUtils.i("XMPPTextRecieve", recievedProcessedMessageJSOn);
                            recievedFirstTimeStickerMessageAccepted(recievedProcessedMessageJSOn, recieverId, "");
                        } else if (type.equalsIgnoreCase("status")) {
                            receivedChatState(recieverId, "", mMsgJSONobj);
                        } else if (OneToOneChatJSonCreator.isDeliveryStatus(recievedProcessedMessageJSOn)) {
                            //recieve status
                            recieveDeliveryStatus(recievedProcessedMessageJSOn);
                            LogUtils.i("XMPPStatusRecieve", recievedProcessedMessageJSOn);
                        }
                        //recieve contact
                        else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.CONTACT_TYPE)) {
                            recievedContactMessage(recievedProcessedMessageJSOn, recieverId, "");
                        }

                        //recieve location
                        else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.LOCATION_TYPE)) {
                            recievedLocationMessage(recievedProcessedMessageJSOn, recieverId, "");
                        } else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.IMAGE_TYPE)) {
                            receivedImageMessage(recievedProcessedMessageJSOn, recieverId, "");
                        } else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.IMAGECAPTION_TYPE)) {
                            receivedImageMessage(recievedProcessedMessageJSOn, recieverId, "");
                        } else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.SKETCH_TYPE)) {
                            receivedImageMessage(recievedProcessedMessageJSOn, recieverId, "");
                        } else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.VIDEO_TYPE)) {
                            receivedVideoMessage(recievedProcessedMessageJSOn, recieverId, "");
                        } else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.YOUTUBE_TYPE)) {
                            receivedYoutubeMessage(recievedProcessedMessageJSOn, recieverId, "");
                        } else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.YAHOO_TYPE)) {
                            receivedYahooMessage(recievedProcessedMessageJSOn, recieverId, "");
                        }
                        //recieve  text chat
                        else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.MESSAGE_TYPE)) {

                            recievedMessage(recievedProcessedMessageJSOn, recieverId, "");
                        } else if (StringUtils.equalsIgnoreCase(OneToOneChatJSonCreator.isMessageType(recievedProcessedMessageJSOn),
                                ConstantChat.FIRST_TIME_STICKER_TYPE)) {

                            recievedFirstTimeStickerMessage(recievedProcessedMessageJSOn, recieverId, "");
                        }
                    }
                }
            }
        }

        @Override
        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {

        }
    }

    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE:
                    //demo
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        try {
                            org.jivesoftware.smack.packet.Message mMessage = new org.jivesoftware.smack.packet.Message();
                            Bundle bundle = msg.getData();
                            final String processedSendJSONchatMessage = (String) bundle.get(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE);
                            final DataTextChat mDataTextChat = (DataTextChat) bundle.getSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE);
                            mMessage.setBody(processedSendJSONchatMessage);

                            if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                                boolean groupSendSuccess = sendGroupMessage(mDataTextChat.getGroupJID(),
                                        processedSendJSONchatMessage);

                                if (groupSendSuccess)
                                    try {
                                        checkFriendOfflineAndPostMessageToApi(mDataTextChat);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                            } else if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
                                if (ValidationUtils.containsXmppDomainName(mDataTextChat.getFriendChatID())) {
//                                    if (mDataTextChat.getFriendChatID().contains(SERVICE_NAME))
                                    mMessage.setTo(mDataTextChat.getFriendChatID());
                                } else {
                                    mMessage.setTo(mDataTextChat.getFriendChatID() + "@" + SERVICE_NAME);
                                }
                                xmppCON.sendStanza(mMessage);
                                try {
                                    checkFriendOfflineAndPostMessageToApi(mDataTextChat);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            // ToastUtils.showAlertToast(getApplicationContext(), processedSendJSONchatMessage, ToastType.SUCESS_ALERT);
                            LogUtils.i(LOG_TAG, "sendMessageXMPP: " + processedSendJSONchatMessage);
                            updateSentStatusToDB(mDataTextChat);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }


                    }
                    break;

                case ConstantXMPP.SEND_ONETOONE_CONTACT_MESSAGE:
                    //demo

                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        try {
                            org.jivesoftware.smack.packet.Message mMessage = new org.jivesoftware.smack.packet.Message();
                            Bundle bundle = msg.getData();
                            String processedSendJSONchatMessage = (String) bundle.get(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE);
                            DataTextChat mDataContactChat = (DataTextChat) bundle.getSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE);
                            mMessage.setBody(processedSendJSONchatMessage);

                            if (mDataContactChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {

                                MultiUserChat muc = mucManager.getMultiUserChat(mDataContactChat.getGroupJID() + "@conference." + SERVICE_NAME);
                                try {
                                    muc.sendMessage(processedSendJSONchatMessage);
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    checkFriendOfflineAndPostMessageToApi(mDataContactChat);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (mDataContactChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
                                if (ValidationUtils.containsXmppDomainName(mDataContactChat.getFriendChatID())) {
//                                    if (mDataContactChat.getFriendChatID().contains(SERVICE_NAME))
                                    mMessage.setTo(mDataContactChat.getFriendChatID());
                                } else {
                                    mMessage.setTo(mDataContactChat.getFriendChatID() + "@" + SERVICE_NAME);
                                }
                                xmppCON.sendStanza(mMessage);
                                try {
                                    checkFriendOfflineAndPostMessageToApi(mDataContactChat);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            // ToastUtils.showAlertToast(getApplicationContext(), processedSendJSONchatMessage, ToastType.SUCESS_ALERT);
                            LogUtils.i(LOG_TAG, "sendContactXMPP: " + processedSendJSONchatMessage);
                            updateSentStatusToDB(mDataContactChat);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }


                    }
                    break;

                case ConstantXMPP.SEND_ONETOONE_LOCATION_MESSAGE:
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        try {
                            org.jivesoftware.smack.packet.Message mMessage = new org.jivesoftware.smack.packet.Message();
                            Bundle bundle = msg.getData();
                            String processedSendJSONchatMessage = (String) bundle.get(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE);
                            DataTextChat mDataLocationChat = (DataTextChat) bundle.getSerializable(ConstantChat.SEND_OBJ_CHAT_B_MESSAGE);
                            mMessage.setBody(processedSendJSONchatMessage);


                            if (mDataLocationChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {

                                MultiUserChat muc = mucManager.getMultiUserChat(mDataLocationChat.getGroupJID() + "@conference." + SERVICE_NAME);
                                try {
                                    muc.sendMessage(processedSendJSONchatMessage);
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    checkFriendOfflineAndPostMessageToApi(mDataLocationChat);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (mDataLocationChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
                                if (ValidationUtils.containsXmppDomainName(mDataLocationChat.getFriendChatID())) {
//                                    if (mDataLocationChat.getFriendChatID().contains(SERVICE_NAME))
                                    mMessage.setTo(mDataLocationChat.getFriendChatID());
                                } else {
                                    mMessage.setTo(mDataLocationChat.getFriendChatID() + "@" + SERVICE_NAME);
                                }
                                xmppCON.sendStanza(mMessage);
                                try {
                                    checkFriendOfflineAndPostMessageToApi(mDataLocationChat);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            // ToastUtils.showAlertToast(getApplicationContext(), processedSendJSONchatMessage, ToastType.SUCESS_ALERT);
                            LogUtils.i(LOG_TAG, "sendLocationXMPP: " + processedSendJSONchatMessage);
                            updateSentStatusToDB(mDataLocationChat);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }


                    }
                    break;

                case ConstantXMPP.SEND_DELVERY_STATUS:
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        try {
                            org.jivesoftware.smack.packet.Message mMessage = new org.jivesoftware.smack.packet.Message();
                            Bundle bundle = msg.getData();
                            String processedDeliveryStatusJSONMessage = (String) bundle.get(ConstantChat.SEND_DELIVERY_DTATUS_B_MESSAGE);
                            DataTextChat mDataTextChat = (DataTextChat) bundle.getSerializable(ConstantChat.SEND_DELIVERY_OBJ_B_MESSAGE);
                            mMessage.setBody(processedDeliveryStatusJSONMessage);
                            if (ValidationUtils.containsXmppDomainName(mDataTextChat.getFriendChatID())) {
//                                if (mDataTextChat.getFriendChatID().contains(SERVICE_NAME))
                                mMessage.setTo(mDataTextChat.getFriendChatID());
                            } else {
                                mMessage.setTo(mDataTextChat.getFriendChatID() + "@" + SERVICE_NAME);
                            }
                            LogUtils.i(LOG_TAG, "XMPPDeliveryStatusSend: " + processedDeliveryStatusJSONMessage);
                            //ToastUtils.showAlertToast(getApplicationContext(), processedDeliveryStatusJSONMessage, ToastType.SUCESS_ALERT);

                            xmppCON.sendStanza(mMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case ConstantXMPP.XMPPAUTHENTICATION:
                    mDataProfile = new TableUserInfo(getBaseContext()).getUser();
                    userId = password = mDataProfile.getCountryCode() + mDataProfile.getPhoneNo();
                    mMessengerUI = msg.replyTo;
                    registerXmpp();
                    break;
                case ConstantXMPP.ONLINE:
                    isOnlineRequested = true;
                    mMessengerUI = msg.replyTo;
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        try {
                            setUpPresenceOnline();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case ConstantXMPP.OFFLINE:
                    isOnlineRequested = false;
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        try {
                            // setUpPresenceOffline();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mMessengerUI = null;

                    break;
                case ConstantXMPP.CREATE_GROUP:
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        Bundle bundle = msg.getData();
//                        DataCreateOrEditGroup dataCreateOrEditGroup = (DataCreateOrEditGroup) bundle.getSerializable(Constants.B_OBJ);
                        createGroup(bundle);
                    } else {
                        if (mMessengerUI != null) {
                            Message nmsg = Message.obtain();

                            nmsg.what = ConstantXMPP.CREATE_GROUP_FAILURE;
                            try {
                                mMessengerUI.send(nmsg);
                            } catch (RemoteException re) {
                                re.printStackTrace();
                            }
                        }
                    }
                    break;

                case ConstantXMPP.SEND_CHAT_STATE:
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        Bundle bundle = msg.getData();

                        sendChatState(bundle);
                    }
                    break;

                case ConstantXMPP.LEAVE_GROUP:
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        Bundle bundle = msg.getData();
                        leaveGroup(bundle);
                    }
                    break;
                case ConstantXMPP.REMOVE_GROUP_MEMBER:
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        Bundle bundle = msg.getData();
                        removeMemberFromGroup(bundle);
                    }
                    break;
                case ConstantXMPP.ADD_GROUP_MEMBER:
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        Bundle bundle = msg.getData();
                        addMemberToGroup(bundle);
                    }
                    break;
                case ConstantXMPP.SEND_FIRST_TIME_STICKER_ACCEPT_MESSAGE:
                    if (xmppCON != null && xmppCON.isAuthenticated()) {
                        Bundle bundle = msg.getData();
                        sendFirstTimeStickerAcceptedMessage(bundle);
                    }
                    break;

            }
        }
    }

    public class RunnableXmpp implements Runnable {

        private TYPE mType;

        public RunnableXmpp(TYPE mType) {
            this.mType = mType;
        }

        @Override
        public void run() {
            switch (mType) {
                case LOGIN:

                    if (CommonMethods.isOnline(getBaseContext())) {
                        cleanUpConnection();
                        LogUtils.i(LOG_TAG, "XMPP LOGIN: call login");
                        String uId = userId.replace("@" + SERVICE_NAME, "");
                        xmppConfig = XMPPTCPConnectionConfiguration.builder()
                                .setUsernameAndPassword(uId, uId)
                                .setServiceName(SERVICE_NAME)
                                .setHost(SERVER_HOST)
                                .setPort(SERVER_PORT)
                                .setResource("Android")
                                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                        xmppConfig.setConnectTimeout(30000);
//                        try {
//                            TLSUtils.acceptAllCertificates(xmppConfig);
//                            xmppConfig.setHostnameVerifier(new HostnameVerifier() {
//                                @Override
//                                public boolean verify(String hostname, SSLSession session) {
//                                    return true;
//                                }
//                            });
//                            e.printStackTrace();
//                        } catch (KeyManagementException e) {
//                            e.printStackTrace();
//                        }

                        configure();
                        xmppCON = new XMPPTCPConnection(xmppConfig.build());
                        try {
                            xmppCON.connect();
//                            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
//                            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                            xmppCON.login(uId, uId);
                            if (isOnlineRequested) {
                                setUpPresenceOnline();
                            } else {
                                //setUpPresenceOffline();
                            }
                            LogUtils.i(LOG_TAG, "Login : successfull : connected" + xmppCON.isConnected() + "Auth :" + xmppCON.isAuthenticated());
                            //add packet listener incoming


                            try {
                                setUpRecieveMessagePacketListener();
                                setUpConnectionListener();
                                sendUndeliveredMesage();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                setUpMuc();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retryXMPPCOnnection();
                        }
                        if (mMessengerUI != null) {
                            Message message = Message.obtain();
                            message.what = 0;
                            try {
                                mMessengerUI.send(message);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case REGISTER:
                    registerThread();
                    break;
            }
        }
    }

    /*private void sendMessage() {
        ChatManager chatmanager = ChatManager.getInstanceFor(xmppCON);
        Chat newChat = chatmanager.createChat("jsmith@jivesoftware.com", new ChatMessageListener() {
            //recieve message
            @Override
            public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                if (mMessengerUI != null) {
                    Bundle mBundle = new Bundle();
                    mBundle.putBoolean(Constants.B_ID, true);
                    Message msg = Message.obtain();
                    msg.setData(mBundle);
                    msg.what = 505;
                    try {
                        mMessengerUI.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        //send message to xmpp
        try {
            newChat.sendMessage("Howdy!");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }*/

//    public class ContactObserver extends ContentObserver {
//        public ContactObserver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            this.onChange(selfChange, null);
//            LogUtils.i(LOG_TAG, "ContactObserver: onChange");
//            ((AppVhortex) getApplication().getApplicationContext()).setSyncStatus("0");
//        }
//
//        @Override
//        public void onChange(boolean selfChange, Uri uri) {
//            LogUtils.i(LOG_TAG, "ContactObserver: onChange" + uri.getPath());
//            ((AppVhortex) getApplication().getApplicationContext()).setSyncStatus("0");
//        }
//    }
}
