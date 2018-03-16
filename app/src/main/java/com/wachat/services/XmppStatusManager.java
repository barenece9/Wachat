package com.wachat.services;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.roster.Roster;

public class XmppStatusManager {
    public static final int ONLINE = 0;
    public static final int AWAY = 1;
    public static final int EXAWAY = 2;
    public static final int BUSY = 3;
    public static final int FFC = 4;
    public static final int OFFLINE = 5;

    public static String retrieveStatusMessage(String userJid, AbstractXMPPConnection xmppCON) {
        String userStatus; // default return value
        int userState = 0;
        try {
            userState = retrieveState(userJid, xmppCON);
        } catch (Exception e) {
            Log.e("", "Invalid connection or user in retrieveStatus() - NPE" + e.getLocalizedMessage());
            userStatus = "Unknown";
        }

        userStatus = stateToString(userState);

        return userStatus;
    }

    public static String stateToString(int state) {
        switch (state) {
            case ONLINE:
                return "Online";
            case AWAY:
                return "Away";
            case EXAWAY:
                return "Extended Away";
            case BUSY:
                return "Busy";
            case FFC:
                return "Free for chat";
            case OFFLINE:
                return "Offline";
            default:
                return "Unknown";
        }
    }

    public static int retrieveState(String userID, AbstractXMPPConnection xmppCON) {
        int userState = OFFLINE; // default return value
        Presence userFromServer;

        try {
            Roster roster = Roster.getInstanceFor(xmppCON);
            userFromServer = roster.getPresence(userID);
            userState = retrieveState(userFromServer.getMode(),
                    userFromServer.isAvailable());
        } catch (Exception e) {
            Log.e("retrieveState()",": Invalid connection or user - NPE"+e.getLocalizedMessage());
        }

        return userState;
    }

    /**
     * Maps the smack internal userMode enums into our int status mode flags
     *
     * @param userMode
     * @param isOnline
     * @return
     */
    // TODO do we need the isOnline boolean?
    // Mode.available should be an equivalent
    public static int retrieveState(Mode userMode, boolean isOnline) {
        int userState = OFFLINE; // default return value

        if (userMode == Mode.dnd) {
            userState = BUSY;
        } else if (userMode == Mode.away || userMode == Mode.xa) {
            userState = AWAY;
        } else if (isOnline) {
            userState = ONLINE;
        }

        return userState;
    }

    // public static int getStateRes(int state) {
    // switch (state) {
    // case XmppFriend.AWAY:
    // case XmppFriend.EXAWAY:
    // return R.drawable.ic_chat_user_status_offline;
    // case XmppFriend.BUSY:
    // return R.drawable.ic_chat_user_status_offline;
    // case XmppFriend.FFC:
    // case XmppFriend.ONLINE:
    // return R.drawable.ic_chat_user_status_online;
    // }
    // return R.drawable.ic_chat_user_status_offline;
    // }

}
