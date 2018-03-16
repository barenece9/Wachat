package com.wachat.services;

/**
 * Created by Gourav Kundu on 30-09-2015.
 */
public interface ConstantXMPP {

    //xmpp status

    int XMPPAUTHENTICATION = 101;
    int ONLINE = 200;
    int OFFLINE = 201;
    int SEND_ONETOONE_TEXT_MESSAGE = 700;
    int RECIEVE_ONETOONE_TEXT_MESSAGE = 701;
    int SEND_ONETOONE_CONTACT_MESSAGE = 720;
    int RECIEVE_ONETOONE_CONTACT_MESSAGE = 721;
    int SEND_ONETOONE_LOCATION_MESSAGE = 740;
    int RECIEVE_ONETOONE_LOCATION_MESSAGE = 741;
//    final String SERVICE_NAME = "@iscoveri.com";
    int SEND_DELVERY_STATUS = 702;
    int SEND_FIRST_TIME_STICKER_ACCEPT_MESSAGE = 704;
    int SEND_ONETOONE_IMAGE_MESSAGE = 742;
    int RECIEVE_ONETOONE_IMAGE_MESSAGE = 743;
    int CREATE_GROUP = 744;
    int CREATE_GROUP_SUCCESS = 745;
    int CREATE_GROUP_FAILURE = 746;

    int SEND_CHAT_STATE = 747;
    int RECEIVE_CHAT_STATE = 748;
    int LEAVE_GROUP = 749;
    int REMOVE_GROUP_MEMBER = 750;
    int ADD_GROUP_MEMBER = 751;
}
