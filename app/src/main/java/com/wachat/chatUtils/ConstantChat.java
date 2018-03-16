package com.wachat.chatUtils;

/**
 * Created by Argha on 30-10-2015.
 */
public interface ConstantChat {

    String SEND_TEXT_CHAT_B_MESSAGE = "SEND_TEXT_CHAT_B_MESSAGE";
    String SEND_OBJ_CHAT_B_MESSAGE = "SEND_OBJ_CHAT_B_MESSAGE";
    String RECIEVE_TEXT_CHAT_B_MESSAGE = "RECIEVE_TEXT_CHAT_B_MESSAGE";
    String RECIEVE_CHAT_STATE_MESSAGE = "RECIEVE_CHAT_STATE_MESSAGE";
    String RECIEVE_DELIVERY_DTATUS_B_MESSAGE = "RECIEVE_DELIVERY_DTATUS_B_MESSAGE";
    String SEND_DELIVERY_DTATUS_B_MESSAGE = "SEND_DELIVERY_DTATUS_B_MESSAGE";
    String SEND_DELIVERY_OBJ_B_MESSAGE = "SEND_DELIVERY_OBJ_B_MESSAGE";

    String ISONLINE = "1";
    String ISOFFLINE = "0";
    String TYPE_SINGLECHAT = "singlechat";
    String TYPE_GROUPCHAT = "groupchat";
    String TYPE_DELIVERY_STATUS = "deliverystatus";
    String TYPE_CLEAR_CONVERSATION = "clearconversation";

    String STATUS_UNSENT = "0";
    String STATUS_SENT = "1";
    String STATUS_DELIVERED  = "2";
    String STATUS_READ = "3";

    String MESSAGE_TYPE = "Message";
    String AUDIO_TYPE = "Audio";
    String IMAGE_TYPE = "Image";
    String VIDEO_TYPE = "Video";
    String LOCATION_TYPE = "Location";
    String CONTACT_TYPE = "Contact";
    String IMAGECAPTION_TYPE = "ImageCaption";
    String SKETCH_TYPE = "Sketch";
    String YOUTUBE_TYPE = "YouTube";
    String YAHOO_TYPE = "Yahoo";
    String FIRST_TIME_STICKER_TYPE = "FirstTimeChat";
    String FIRST_TIME_STICKER_ACCEPTED = "FirstTimeChatAccepted";

    String FIRST_TIME_STICKER_LBL = "Sticker";
    String NOTIFICATION_TYPE_CREATED = "notification_msg_created";
    String NOTIFICATION_TYPE_ADDED = "notification_msg_added";
    String NOTIFICATION_TYPE_REMOVED = "notification_msg_removed";
    String NOTIFICATION_TYPE_LEFT = "notification_msg_left";

}
