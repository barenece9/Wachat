package com.wachat.chatUtils;

import android.content.Context;
import android.graphics.Bitmap;

import com.wachat.application.AppVhortex;
import com.wachat.data.DataGroup;
import com.wachat.data.DataGroupMembers;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.MediaUtils;

import org.jivesoftware.smackx.chatstates.ChatState;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Argha on 30-10-2015.
 */
public class OneToOneChatJSonCreator {
    public static final String APP_VERSION = "app_version";

    public static final String GROUP_JID = "group_jid";
    public static String MESSAGEID = "messageid";
    public static String CHATTYPE = "chattype";
    public static String ONLINE = "online";
    public static String BODY = "messagebody";
    public static String TIMESTAMP = "timestamp";
    public static String LANG = "lang";
    public static String SENDERPHONE = "senderphone";
    public static String SENDERNAME = "sendername";
    public static String TYPE = "type";
    public static String DELIVERYSTATUS = "deliverystatus";
    public static String SENDERID = "senderid";
    public static String SENDER_NAME = "sendername";
    public static String GROUP_ID = "groupid";
    // public static  String CHATIDS = "chatids";

    //    parameters for contact share
    public static String Contact_Details = "Contact_Details";
    public static String CONTACTNAME = "Name";
    public static String CONTACTPHONE_NUMBER = "Phone_Number";
    public static String CONTACTIMAGESTR = "ImageStr";

    //parameters for Location
    public static String Location_Details = "Location_Details";
    public static String ADDRESS = "address";
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";

    //parameters for Image
    public static String PHOTO_DETAILS = "Photo_Details";
    public static String IMAGE_URL = "Image_Url";
    public static String THUMB_BASE64 = "Thumb_Base64";

    public static String ISMASKED = "ismasked";

    //parameters for Video
    public static String VIDEO_DETAILS = "Video_Details";
    public static String VIDEO_URL = "Video_Url";
    public static String VIDEO_THUMB_BASE64 = "Thumb_Base64";

    //parameters for YouTube
    public static String YOUTUBE_DETAILS = "YouTube_Details";
    public static String YOUTUBE_VIDEO_URL = "VideoUrlStr";
    public static String YOUTUBE_VIDEO_THUMB_URL = "ThumbnailUrlStr";
    public static String YOUTUBE_VIDEO_TITLE = "Title";
    public static String YOUTUBE_VIDEO_DESCRIPTION = "Description";
    public static String YOUTUBE_VIDEO_DATE_OF_PUBLICATION = "Date_of_publication";

    //parameters for Yahoo
    public static String YAHOO_DETAILS = "Yahoo_Details";
    public static String YAHOO_TITLE = "Title";
    public static String YAHOO_THUMB_URL = "ThumbnailUrlStr";
    public static String YAHOO_NEWS_URL = "NewsUrlStr";
    public static String YAHOO_DESCRIPTION = "Description";
    public static String YAHOO_DATE_OF_PUBLICATION = "Date_of_publication";


    //parameters for First timew chat sticker
    public static String FIRST_TIME_STICKER_DETAILS = "first_time_sticker_detail";
    public static String STICKER_TYPE = "sticker_type";


    public static String getTextMessageToJSON(DataTextChat mDataTextChat) {

        if (mDataTextChat == null)
            return null;

        JSONObject mMsgBodyJSON = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(APP_VERSION,
                    AppVhortex.getInstance().getAppVersionName());
            mMsgBodyJSON.put(MESSAGEID, mDataTextChat.getMessageid());
            mMsgBodyJSON.put(GROUP_ID, mDataTextChat.getStrGroupID());
            mMsgBodyJSON.put(CHATTYPE, mDataTextChat.getMessageType());
            mMsgBodyJSON.put(ONLINE, mDataTextChat.getOnline());
            mMsgBodyJSON.put(BODY, mDataTextChat.getBody());
            mMsgBodyJSON.put(TIMESTAMP, mDataTextChat.getTimestamp());
            mMsgBodyJSON.put(LANG, mDataTextChat.getLang());
            mMsgBodyJSON.put(SENDERPHONE, mDataTextChat.getFriendPhoneNo());
            mMsgBodyJSON.put(TYPE, mDataTextChat.getChattype());
            mMsgBodyJSON.put(SENDERID, mDataTextChat.getUserID());
            mMsgBodyJSON.put(SENDER_NAME, mDataTextChat.getSenderName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }

    public static String getFirstTimeStickerToJSON(DataTextChat mDataTextChat) {

        if (mDataTextChat == null)
            return null;

        JSONObject mMsgBodyJSON = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(APP_VERSION,
                    AppVhortex.getInstance().getAppVersionName());
            mMsgBodyJSON.put(MESSAGEID, mDataTextChat.getMessageid());
            mMsgBodyJSON.put(GROUP_ID, mDataTextChat.getStrGroupID());
            mMsgBodyJSON.put(CHATTYPE, mDataTextChat.getMessageType());
            mMsgBodyJSON.put(ONLINE, mDataTextChat.getOnline());
            mMsgBodyJSON.put(BODY, mDataTextChat.getBody());
            mMsgBodyJSON.put(TIMESTAMP, mDataTextChat.getTimestamp());
            mMsgBodyJSON.put(LANG, mDataTextChat.getLang());
            mMsgBodyJSON.put(SENDERPHONE, mDataTextChat.getFriendPhoneNo());
            mMsgBodyJSON.put(TYPE, mDataTextChat.getChattype());
            mMsgBodyJSON.put(SENDERID, mDataTextChat.getUserID());
            mMsgBodyJSON.put(SENDER_NAME, mDataTextChat.getSenderName());

            JSONObject firstTimeStickerJson = new JSONObject();
            //For First time sticker
            firstTimeStickerJson.put(STICKER_TYPE, mDataTextChat.getFilePath());


            mMsgBodyJSON.put(FIRST_TIME_STICKER_DETAILS, firstTimeStickerJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }


    public static DataTextChat getFirstTimeStickerFromJSON(String firstTimeStickerJson) {

        DataTextChat mDataTextChat = new DataTextChat();
        try {
            JSONObject mMsgJSONobj = new JSONObject(firstTimeStickerJson);
            if (mMsgJSONobj != null) {
                mDataTextChat.setMessageid(mMsgJSONobj.optString(MESSAGEID));
                mDataTextChat.setStrGroupID(mMsgJSONobj.optString(GROUP_ID));
                mDataTextChat.setMessageType(mMsgJSONobj.optString(CHATTYPE));
                mDataTextChat.setOnline(mMsgJSONobj.optString(ONLINE));
                mDataTextChat.setBody(mMsgJSONobj.optString(BODY));
                mDataTextChat.setTimestamp(mMsgJSONobj.optString(TIMESTAMP));
                mDataTextChat.setLang(mMsgJSONobj.optString(LANG));
                mDataTextChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);
                mDataTextChat.setFriendPhoneNo(mMsgJSONobj.optString(SENDERPHONE));
                mDataTextChat.setFriendId(mMsgJSONobj.optString(SENDERID));
                mDataTextChat.setSenderId(mMsgJSONobj.optString(SENDERID));
                mDataTextChat.setSenderName(mMsgJSONobj.optString(SENDER_NAME));
                mDataTextChat.setFriendName(mMsgJSONobj.optString(SENDER_NAME));
                mDataTextChat.setChattype(mMsgJSONobj.optString(TYPE));

                //For Contact
                JSONObject mContactJSON = mMsgJSONobj.getJSONObject(FIRST_TIME_STICKER_DETAILS);
                mDataTextChat.setFilePath(mContactJSON.optString(STICKER_TYPE));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mDataTextChat;
    }


    public static DataTextChat getTextMessageFromJSON(String messageJson) {

        DataTextChat mDataTextChat = new DataTextChat();
        try {
            JSONObject mMsgJSONobj = new JSONObject(messageJson);
            if (mMsgJSONobj != null) {
                mDataTextChat.setMessageid(mMsgJSONobj.optString(MESSAGEID));
                mDataTextChat.setStrGroupID(mMsgJSONobj.optString(GROUP_ID));
                mDataTextChat.setMessageType(mMsgJSONobj.optString(CHATTYPE));
                mDataTextChat.setOnline(mMsgJSONobj.optString(ONLINE));
                mDataTextChat.setBody(mMsgJSONobj.optString(BODY));
                mDataTextChat.setTimestamp(mMsgJSONobj.optString(TIMESTAMP));
                mDataTextChat.setLang(mMsgJSONobj.optString(LANG));
                mDataTextChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);
                mDataTextChat.setFriendPhoneNo(mMsgJSONobj.optString(SENDERPHONE));
                mDataTextChat.setFriendId(mMsgJSONobj.optString(SENDERID));
                mDataTextChat.setChattype(mMsgJSONobj.optString(TYPE));
                mDataTextChat.setSenderId(mMsgJSONobj.optString(SENDERID));
                mDataTextChat.setSenderName(mMsgJSONobj.optString(SENDER_NAME));
                mDataTextChat.setFriendName(mMsgJSONobj.optString(SENDER_NAME));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mDataTextChat;
    }

    public static DataTextChat getGroupNotificationMessage(DataGroup mDataGroup,
                                                           DataGroupMembers member,
                                                           String notificationType, String messageBody) {

        DataTextChat mDataTextChat = new DataTextChat();
        String time = "";
        String id = "";
        try {
            if (notificationType.equals(ConstantChat.NOTIFICATION_TYPE_ADDED)){
                time = member.addedTime;
                id = member.userId+"_"+member.addedTime;
            }else if(notificationType.equals(ConstantChat.NOTIFICATION_TYPE_CREATED)){
                time = member.addedTime;
                id = member.userId+"_"+member.addedTime;
            }else if(notificationType.equals(ConstantChat.NOTIFICATION_TYPE_LEFT)){
                time = member.delTime;
                id = member.userId+"_"+member.delTime;
            } else if (notificationType.equals(ConstantChat.NOTIFICATION_TYPE_REMOVED)) {
                time = member.delTime;
                id = member.userId+"_"+member.delTime;
            } else {
                return null;
            }
            mDataTextChat.setMessageid(id);

            mDataTextChat.setStrGroupID(mDataGroup.groupId);
            mDataTextChat.setGroupName(mDataGroup.getGroupName());
            mDataTextChat.setMessageType(notificationType);
//                mDataTextChat.setOnline(mMsgJSONobj.optString(ONLINE));
            mDataTextChat.setBody(messageBody);
            mDataTextChat.setTimestamp(time);
//            mDataTextChat.setFriendPhoneNo(member.userPhNo);
//            mDataTextChat.setFriendId(member.userId);
//            mDataTextChat.setFriendChatID(member.chatId);
            mDataTextChat.setChattype(ConstantChat.TYPE_GROUPCHAT);
//            mDataTextChat.setSenderId(member.userId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mDataTextChat;
    }

    public static String getSharedContactToJSON(DataTextChat mDataTextChat) {

        if (mDataTextChat == null)
            return null;

        JSONObject mMsgBodyJSON = new JSONObject();
        JSONObject mContactJSON = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(APP_VERSION,
                    AppVhortex.getInstance().getAppVersionName());
            mMsgBodyJSON.put(MESSAGEID, mDataTextChat.getMessageid());
            mMsgBodyJSON.put(GROUP_ID, mDataTextChat.getStrGroupID());
            mMsgBodyJSON.put(CHATTYPE, mDataTextChat.getMessageType());
            mMsgBodyJSON.put(ONLINE, mDataTextChat.getOnline());
            mMsgBodyJSON.put(BODY, mDataTextChat.getBody());
            mMsgBodyJSON.put(TIMESTAMP, mDataTextChat.getTimestamp());
            mMsgBodyJSON.put(LANG, mDataTextChat.getLang());
            mMsgBodyJSON.put(SENDERPHONE, mDataTextChat.getFriendPhoneNo());
            mMsgBodyJSON.put(TYPE, mDataTextChat.getChattype());
            mMsgBodyJSON.put(SENDERID, mDataTextChat.getUserID());
            mMsgBodyJSON.put(SENDER_NAME, mDataTextChat.getSenderName());

            //For Contact
            mContactJSON.put(CONTACTNAME, mDataTextChat.getAttachContactName());
            mContactJSON.put(CONTACTPHONE_NUMBER, mDataTextChat.getAttachContactNo());
            mContactJSON.put(CONTACTIMAGESTR, mDataTextChat.getAttachBase64str4Img());


            mMsgBodyJSON.put(Contact_Details, mContactJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }


    public static DataTextChat getSharedContactFromJSON(String contactJson) {

        DataTextChat mDataTextChat = new DataTextChat();
        try {
            JSONObject mMsgJSONobj = new JSONObject(contactJson);
            if (mMsgJSONobj != null) {
                mDataTextChat.setMessageid(mMsgJSONobj.optString(MESSAGEID));
                mDataTextChat.setStrGroupID(mMsgJSONobj.optString(GROUP_ID));
                mDataTextChat.setMessageType(mMsgJSONobj.optString(CHATTYPE));
                mDataTextChat.setOnline(mMsgJSONobj.optString(ONLINE));
                mDataTextChat.setBody(mMsgJSONobj.optString(BODY));
                mDataTextChat.setTimestamp(mMsgJSONobj.optString(TIMESTAMP));
                mDataTextChat.setLang(mMsgJSONobj.optString(LANG));
                mDataTextChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);
                mDataTextChat.setFriendPhoneNo(mMsgJSONobj.optString(SENDERPHONE));
                mDataTextChat.setFriendId(mMsgJSONobj.optString(SENDERID));
                mDataTextChat.setSenderId(mMsgJSONobj.optString(SENDERID));
                mDataTextChat.setSenderName(mMsgJSONobj.optString(SENDER_NAME));
                mDataTextChat.setFriendName(mMsgJSONobj.optString(SENDER_NAME));

                mDataTextChat.setChattype(mMsgJSONobj.optString(TYPE));

                //For Contact
                JSONObject mContactJSON = mMsgJSONobj.getJSONObject(Contact_Details);
                mDataTextChat.setAttachContactName(mContactJSON.optString(CONTACTNAME));
                mDataTextChat.setAttachContactNo(mContactJSON.optString(CONTACTPHONE_NUMBER));
                mDataTextChat.setAttachBase64str4Img(mContactJSON.optString(CONTACTIMAGESTR));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mDataTextChat;
    }


    public static String getSharedLocationToJSON(DataTextChat mDataTextChat) {

        if (mDataTextChat == null)
            return null;

        JSONObject mMsgBodyJSON = new JSONObject();
        JSONObject mLocationJSON = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(APP_VERSION,
                    AppVhortex.getInstance().getAppVersionName());
            mMsgBodyJSON.put(MESSAGEID, mDataTextChat.getMessageid());
            mMsgBodyJSON.put(GROUP_ID, mDataTextChat.getStrGroupID());
            mMsgBodyJSON.put(CHATTYPE, mDataTextChat.getMessageType());
            mMsgBodyJSON.put(ONLINE, mDataTextChat.getOnline());
            mMsgBodyJSON.put(BODY, mDataTextChat.getBody());
            mMsgBodyJSON.put(TIMESTAMP, mDataTextChat.getTimestamp());
            mMsgBodyJSON.put(LANG, mDataTextChat.getLang());
            mMsgBodyJSON.put(SENDERPHONE, mDataTextChat.getFriendPhoneNo());
            mMsgBodyJSON.put(TYPE, mDataTextChat.getChattype());
            mMsgBodyJSON.put(SENDERID, mDataTextChat.getUserID());
            mMsgBodyJSON.put(SENDER_NAME, mDataTextChat.getSenderName());
            //For Location

            mLocationJSON.put(LATITUDE, Float.parseFloat(mDataTextChat.getLoc_lat().trim()));
            mLocationJSON.put(LONGITUDE, Float.parseFloat(mDataTextChat.getLoc_long().trim()));
            mLocationJSON.put(ADDRESS, mDataTextChat.getLoc_address());
            mMsgBodyJSON.put(Location_Details, mLocationJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }


    public static DataTextChat getSharedLocationFromJSON(String locationJson) {

        DataTextChat mDataTextChat = new DataTextChat();
        try {
            JSONObject mMsgJSONobj = new JSONObject(locationJson);
            if (mMsgJSONobj != null) {
                mDataTextChat.setMessageid(mMsgJSONobj.optString(MESSAGEID));
                mDataTextChat.setStrGroupID(mMsgJSONobj.optString(GROUP_ID));
                mDataTextChat.setMessageType(mMsgJSONobj.optString(CHATTYPE));
                mDataTextChat.setOnline(mMsgJSONobj.optString(ONLINE));
                mDataTextChat.setBody(mMsgJSONobj.optString(BODY));
                mDataTextChat.setTimestamp(mMsgJSONobj.optString(TIMESTAMP));
                mDataTextChat.setLang(mMsgJSONobj.optString(LANG));
                mDataTextChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);
                mDataTextChat.setFriendPhoneNo(mMsgJSONobj.optString(SENDERPHONE));
                mDataTextChat.setFriendId(mMsgJSONobj.optString(SENDERID));
                mDataTextChat.setSenderId(mMsgJSONobj.optString(SENDERID));
                mDataTextChat.setSenderName(mMsgJSONobj.optString(SENDER_NAME));
                mDataTextChat.setFriendName(mMsgJSONobj.optString(SENDER_NAME));

                mDataTextChat.setChattype(mMsgJSONobj.optString(TYPE));

                //For Contact
                JSONObject mLocationJSON = mMsgJSONobj.getJSONObject(Location_Details);
                mDataTextChat.setLoc_address(mLocationJSON.optString(ADDRESS));
                mDataTextChat.setLoc_long(String.valueOf(mLocationJSON.optDouble(LONGITUDE)));
                mDataTextChat.setLoc_lat(String.valueOf(mLocationJSON.optDouble(LATITUDE)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mDataTextChat;
    }

    public static String getDeliveryMessageToJSON(DataTextChat mDataTextChat) {

        if (mDataTextChat == null)
            return null;

        JSONObject mMsgBodyJSON = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(MESSAGEID, mDataTextChat.getMessageid());
            mMsgBodyJSON.put(GROUP_ID, mDataTextChat.getStrGroupID());
            mMsgBodyJSON.put(TYPE, ConstantChat.TYPE_DELIVERY_STATUS);
            mMsgBodyJSON.put(DELIVERYSTATUS, mDataTextChat.getDeliveryStatus());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }

    public static String getFirstTimeStickerAcceptMessageJSON(String phoneNo, String name) {

        JSONObject mMsgBodyJSON = new JSONObject();
        try {
            mMsgBodyJSON.put(TYPE, ConstantChat.FIRST_TIME_STICKER_ACCEPTED);
            mMsgBodyJSON.put(SENDERPHONE, phoneNo);
            mMsgBodyJSON.put(SENDERNAME, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }

    public static boolean isDeliveryStatus(String messageJson) {
        boolean status = false;
        try {
            JSONObject mMsgJSONobj = new JSONObject(messageJson);
            if (mMsgJSONobj != null) {
                if (mMsgJSONobj.optString(TYPE).equalsIgnoreCase(ConstantChat.TYPE_DELIVERY_STATUS)) {
                    status = true;
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
            status = false;
        }


        return status;
    }

    public static String isMessageType(String messageJson) {
        String returntype = "";
        try {
            JSONObject mMsgJSONobj = new JSONObject(messageJson);
            if (mMsgJSONobj != null) {
                if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.CONTACT_TYPE)) {
                    returntype = ConstantChat.CONTACT_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.LOCATION_TYPE)) {
                    returntype = ConstantChat.LOCATION_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.IMAGE_TYPE)) {
                    returntype = ConstantChat.IMAGE_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.IMAGECAPTION_TYPE)) {
                    returntype = ConstantChat.IMAGECAPTION_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.SKETCH_TYPE)) {
                    returntype = ConstantChat.SKETCH_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.MESSAGE_TYPE)) {
                    returntype = ConstantChat.MESSAGE_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.VIDEO_TYPE)) {
                    returntype = ConstantChat.VIDEO_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.YOUTUBE_TYPE)) {
                    returntype = ConstantChat.YOUTUBE_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.YAHOO_TYPE)) {
                    returntype = ConstantChat.YAHOO_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.FIRST_TIME_STICKER_TYPE)) {
                    returntype = ConstantChat.FIRST_TIME_STICKER_TYPE;
                } else if (mMsgJSONobj.optString(CHATTYPE).equalsIgnoreCase(ConstantChat.FIRST_TIME_STICKER_ACCEPTED)) {
                    returntype = ConstantChat.FIRST_TIME_STICKER_ACCEPTED;
                }
            }

        } catch (
                JSONException e
                )

        {
            e.printStackTrace();
        }


        return returntype;
    }

    public static String imageChatToJsonString(DataTextChat mDataFileChat) {

        if (mDataFileChat == null)
            return null;

        JSONObject mMsgBodyJSON = new JSONObject();
        JSONObject mPhotoDetailsJson = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(APP_VERSION,
                    AppVhortex.getInstance().getAppVersionName());
            mMsgBodyJSON.put(MESSAGEID, mDataFileChat.getMessageid());
            mMsgBodyJSON.put(GROUP_ID, mDataFileChat.getStrGroupID());
            mMsgBodyJSON.put(CHATTYPE, mDataFileChat.getMessageType());
            mMsgBodyJSON.put(ONLINE, mDataFileChat.getOnline());
            mMsgBodyJSON.put(BODY, mDataFileChat.getBody());
            mMsgBodyJSON.put(TIMESTAMP, mDataFileChat.getTimestamp());
            mMsgBodyJSON.put(LANG, mDataFileChat.getLang());
            mMsgBodyJSON.put(SENDERPHONE, mDataFileChat.getFriendPhoneNo());
            mMsgBodyJSON.put(TYPE, mDataFileChat.getChattype());
            mMsgBodyJSON.put(SENDERID, mDataFileChat.getUserID());
            mMsgBodyJSON.put(SENDER_NAME, mDataFileChat.getSenderName());
            //For Contact
            mPhotoDetailsJson.put(IMAGE_URL, mDataFileChat.getFileUrl());
            mPhotoDetailsJson.put(THUMB_BASE64, mDataFileChat.getThumbBase64());
            mPhotoDetailsJson.put(ISMASKED, mDataFileChat.getIsMasked());

            mMsgBodyJSON.put(PHOTO_DETAILS, mPhotoDetailsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }

    public static String videoChatToJsonString(DataTextChat mDataFileChat) {

        if (mDataFileChat == null)
            return null;

        JSONObject mMsgBodyJSON = new JSONObject();
        JSONObject mVideoDetailsJson = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(APP_VERSION,
                    AppVhortex.getInstance().getAppVersionName());
            mMsgBodyJSON.put(MESSAGEID, mDataFileChat.getMessageid());

            mMsgBodyJSON.put(GROUP_ID, mDataFileChat.getStrGroupID());
            mMsgBodyJSON.put(CHATTYPE, mDataFileChat.getMessageType());
            mMsgBodyJSON.put(ONLINE, mDataFileChat.getOnline());
            mMsgBodyJSON.put(BODY, mDataFileChat.getBody());
            mMsgBodyJSON.put(TIMESTAMP, mDataFileChat.getTimestamp());
            mMsgBodyJSON.put(LANG, mDataFileChat.getLang());
            mMsgBodyJSON.put(SENDERPHONE, mDataFileChat.getFriendPhoneNo());
            mMsgBodyJSON.put(TYPE, mDataFileChat.getChattype());
            mMsgBodyJSON.put(SENDERID, mDataFileChat.getUserID());
            mMsgBodyJSON.put(SENDER_NAME, mDataFileChat.getSenderName());
            //For Contact
            mVideoDetailsJson.put(VIDEO_URL, mDataFileChat.getFileUrl());
            mVideoDetailsJson.put(VIDEO_THUMB_BASE64, mDataFileChat.getThumbBase64());

            mMsgBodyJSON.put(VIDEO_DETAILS, mVideoDetailsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }

    public static DataTextChat chatJsonStringToVideoChat(String chatJsonString, Context context) {

        DataTextChat mDataFileChat = new DataTextChat();
        try {
            JSONObject mMsgJSONobj = new JSONObject(chatJsonString);
            if (mMsgJSONobj != null) {
                mDataFileChat.setMessageid(mMsgJSONobj.optString(MESSAGEID));
                mDataFileChat.setStrGroupID(mMsgJSONobj.optString(GROUP_ID));
                mDataFileChat.setMessageType(mMsgJSONobj.optString(CHATTYPE));
                mDataFileChat.setOnline(mMsgJSONobj.optString(ONLINE));
                mDataFileChat.setBody(mMsgJSONobj.optString(BODY));
                mDataFileChat.setTimestamp(mMsgJSONobj.optString(TIMESTAMP));
                mDataFileChat.setLang(mMsgJSONobj.optString(LANG));
                mDataFileChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);
                mDataFileChat.setFriendPhoneNo(mMsgJSONobj.optString(SENDERPHONE));
                mDataFileChat.setFriendId(mMsgJSONobj.optString(SENDERID));
                mDataFileChat.setSenderId(mMsgJSONobj.optString(SENDERID));
                mDataFileChat.setSenderName(mMsgJSONobj.optString(SENDER_NAME));
                mDataFileChat.setFriendName(mMsgJSONobj.optString(SENDER_NAME));

                mDataFileChat.setChattype(mMsgJSONobj.optString(TYPE));

                //For Contact
                JSONObject mVideoDetailsJson = mMsgJSONobj.getJSONObject(VIDEO_DETAILS);
                mDataFileChat.setFileUrl(mVideoDetailsJson.optString(VIDEO_URL, ""));
                mDataFileChat.setThumbBase64(mVideoDetailsJson.optString(VIDEO_THUMB_BASE64, ""));
                Bitmap mBitmap = null;
                try {
                    mBitmap = CommonMethods.decodeBase64(mDataFileChat.getThumbBase64());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                File incomingImageFile = MediaUtils.saveImageStringToFile(mBitmap,
                        context);
                if (incomingImageFile != null) {
                    mDataFileChat.setThumbFilePath(incomingImageFile.getPath());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mDataFileChat;
    }

    public static String yahooNewsToJsonString(DataTextChat mDataFileChat) {

        if (mDataFileChat == null)
            return null;

        JSONObject mMsgBodyJSON = new JSONObject();
        JSONObject mYahooNewsDetailsJson = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(APP_VERSION,
                    AppVhortex.getInstance().getAppVersionName());
            mMsgBodyJSON.put(MESSAGEID, mDataFileChat.getMessageid());
            mMsgBodyJSON.put(GROUP_ID, mDataFileChat.getStrGroupID());
            mMsgBodyJSON.put(CHATTYPE, mDataFileChat.getMessageType());
            mMsgBodyJSON.put(ONLINE, mDataFileChat.getOnline());
            mMsgBodyJSON.put(BODY, mDataFileChat.getBody());
            mMsgBodyJSON.put(TIMESTAMP, mDataFileChat.getTimestamp());
            mMsgBodyJSON.put(LANG, mDataFileChat.getLang());
            mMsgBodyJSON.put(SENDERPHONE, mDataFileChat.getFriendPhoneNo());
            mMsgBodyJSON.put(TYPE, mDataFileChat.getChattype());
            mMsgBodyJSON.put(SENDERID, mDataFileChat.getUserID());
            mMsgBodyJSON.put(SENDER_NAME, mDataFileChat.getSenderName());

            mYahooNewsDetailsJson.put(YAHOO_TITLE, CommonMethods.getUTFEncodedString(mDataFileChat.getYahooTitle()));
            mYahooNewsDetailsJson.put(YAHOO_DATE_OF_PUBLICATION, mDataFileChat.getYahooPublishTime());
            mYahooNewsDetailsJson.put(YAHOO_DESCRIPTION, CommonMethods.getUTFEncodedString(mDataFileChat.getYahooDescription()));
            mYahooNewsDetailsJson.put(YAHOO_THUMB_URL, mDataFileChat.getYahooImageUrl());
            mYahooNewsDetailsJson.put(YAHOO_NEWS_URL, mDataFileChat.getYahooShareUrl());

            mMsgBodyJSON.put(YAHOO_DETAILS, mYahooNewsDetailsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }


    public static DataTextChat chatJsonStringToYahooNewsChat(String chatJsonString, Context context) {

        DataTextChat mDataFileChat = new DataTextChat();
        try {
            JSONObject mMsgJSONobj = new JSONObject(chatJsonString);
            if (mMsgJSONobj != null) {
                mDataFileChat.setMessageid(mMsgJSONobj.optString(MESSAGEID));
                mDataFileChat.setStrGroupID(mMsgJSONobj.optString(GROUP_ID));
                mDataFileChat.setMessageType(mMsgJSONobj.optString(CHATTYPE));
                mDataFileChat.setOnline(mMsgJSONobj.optString(ONLINE));
                mDataFileChat.setBody(mMsgJSONobj.optString(BODY));
                mDataFileChat.setTimestamp(mMsgJSONobj.optString(TIMESTAMP));
                mDataFileChat.setLang(mMsgJSONobj.optString(LANG));
                mDataFileChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);
                mDataFileChat.setFriendPhoneNo(mMsgJSONobj.optString(SENDERPHONE));
                mDataFileChat.setFriendId(mMsgJSONobj.optString(SENDERID));
                mDataFileChat.setSenderId(mMsgJSONobj.optString(SENDERID));
                mDataFileChat.setSenderName(mMsgJSONobj.optString(SENDER_NAME));
                mDataFileChat.setFriendName(mMsgJSONobj.optString(SENDER_NAME));

                mDataFileChat.setChattype(mMsgJSONobj.optString(TYPE));

                //For Contact
                JSONObject mYahooDetailsJson = mMsgJSONobj.getJSONObject(YAHOO_DETAILS);
                mDataFileChat.setYahooShareUrl(mYahooDetailsJson.optString(YAHOO_NEWS_URL, ""));
                mDataFileChat.setYahooTitle(mYahooDetailsJson.optString(YAHOO_TITLE, ""));
                mDataFileChat.setYahooPublishTime(mYahooDetailsJson.optString(YAHOO_DATE_OF_PUBLICATION, ""));
                mDataFileChat.setYahooDescription(mYahooDetailsJson.optString(YAHOO_DESCRIPTION, ""));
                mDataFileChat.setYahooImageUrl(mYahooDetailsJson.optString(YAHOO_THUMB_URL, ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mDataFileChat;
    }

    public static String youtubeChatToJsonString(DataTextChat mDataFileChat) {

        if (mDataFileChat == null)
            return null;

        JSONObject mMsgBodyJSON = new JSONObject();
        JSONObject mVideoDetailsJson = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(APP_VERSION,
                    AppVhortex.getInstance().getAppVersionName());
            mMsgBodyJSON.put(MESSAGEID, mDataFileChat.getMessageid());
            mMsgBodyJSON.put(GROUP_ID, mDataFileChat.getStrGroupID());
            mMsgBodyJSON.put(CHATTYPE, mDataFileChat.getMessageType());
            mMsgBodyJSON.put(ONLINE, mDataFileChat.getOnline());
            mMsgBodyJSON.put(BODY, mDataFileChat.getBody());
            mMsgBodyJSON.put(TIMESTAMP, mDataFileChat.getTimestamp());
            mMsgBodyJSON.put(LANG, mDataFileChat.getLang());
            mMsgBodyJSON.put(SENDERPHONE, mDataFileChat.getFriendPhoneNo());
            mMsgBodyJSON.put(TYPE, mDataFileChat.getChattype());
            mMsgBodyJSON.put(SENDERID, mDataFileChat.getUserID());
            mMsgBodyJSON.put(SENDER_NAME, mDataFileChat.getSenderName());

            mVideoDetailsJson.put(YOUTUBE_VIDEO_TITLE, CommonMethods.getUTFEncodedString(mDataFileChat.getYoutubeTitle()));

            mVideoDetailsJson.put(YOUTUBE_VIDEO_DATE_OF_PUBLICATION, mDataFileChat.getYoutubePublishTime());

            mVideoDetailsJson.put(YOUTUBE_VIDEO_DESCRIPTION, CommonMethods.getUTFEncodedString(mDataFileChat.getYoutubeDescription()));
            mVideoDetailsJson.put(YOUTUBE_VIDEO_THUMB_URL, mDataFileChat.getYoutubeThumbUrl());
            mVideoDetailsJson.put(YOUTUBE_VIDEO_URL, "http://www.youtube.com/watch?v=" + mDataFileChat.getYoutubeVideoId());

            mMsgBodyJSON.put(YOUTUBE_DETAILS, mVideoDetailsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }

    public static DataTextChat chatJsonStringToYouTubeChat(String chatJsonString, Context context) {

        DataTextChat mDataFileChat = new DataTextChat();
        try {
            JSONObject mMsgJSONobj = new JSONObject(chatJsonString);
            if (mMsgJSONobj != null) {
                mDataFileChat.setMessageid(mMsgJSONobj.optString(MESSAGEID));
                mDataFileChat.setStrGroupID(mMsgJSONobj.optString(GROUP_ID));
                mDataFileChat.setMessageType(mMsgJSONobj.optString(CHATTYPE));
                mDataFileChat.setOnline(mMsgJSONobj.optString(ONLINE));
                mDataFileChat.setBody(mMsgJSONobj.optString(BODY));
                mDataFileChat.setTimestamp(mMsgJSONobj.optString(TIMESTAMP));
                mDataFileChat.setLang(mMsgJSONobj.optString(LANG));
                mDataFileChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);
                mDataFileChat.setFriendPhoneNo(mMsgJSONobj.optString(SENDERPHONE));
                mDataFileChat.setFriendId(mMsgJSONobj.optString(SENDERID));
                mDataFileChat.setSenderId(mMsgJSONobj.optString(SENDERID));
                mDataFileChat.setSenderName(mMsgJSONobj.optString(SENDER_NAME));
                mDataFileChat.setFriendName(mMsgJSONobj.optString(SENDER_NAME));

                mDataFileChat.setChattype(mMsgJSONobj.optString(TYPE));

                //For Contact
                JSONObject mYoutubeDetailsJson = mMsgJSONobj.getJSONObject(YOUTUBE_DETAILS);
                mDataFileChat.setYoutubeVideoId(MediaUtils.getVideoIdFromYoutubeURL(mYoutubeDetailsJson.optString(YOUTUBE_VIDEO_URL, "")));

                mDataFileChat.setYoutubeTitle(mYoutubeDetailsJson.optString(YOUTUBE_VIDEO_TITLE, ""));
                mDataFileChat.setYoutubePublishTime(mYoutubeDetailsJson.optString(YOUTUBE_VIDEO_DATE_OF_PUBLICATION, ""));
                mDataFileChat.setYoutubeDescription(mYoutubeDetailsJson.optString(YOUTUBE_VIDEO_DESCRIPTION, ""));
                mDataFileChat.setYoutubeThumbUrl(mYoutubeDetailsJson.optString(YOUTUBE_VIDEO_THUMB_URL, ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mDataFileChat;
    }


    public static DataTextChat chatJsonStringToDataFileChat(String chatJsonString, Context context) {

        DataTextChat mDataFileChat = new DataTextChat();
        try {
            JSONObject mMsgJSONobj = new JSONObject(chatJsonString);
            if (mMsgJSONobj != null) {
                mDataFileChat.setMessageid(mMsgJSONobj.optString(MESSAGEID));
                mDataFileChat.setStrGroupID(mMsgJSONobj.optString(GROUP_ID));
                mDataFileChat.setMessageType(mMsgJSONobj.optString(CHATTYPE));
                mDataFileChat.setOnline(mMsgJSONobj.optString(ONLINE));
                mDataFileChat.setBody(mMsgJSONobj.optString(BODY));
                mDataFileChat.setTimestamp(mMsgJSONobj.optString(TIMESTAMP));
                mDataFileChat.setLang(mMsgJSONobj.optString(LANG));
                mDataFileChat.setDeliveryStatus(ConstantChat.STATUS_DELIVERED);
                mDataFileChat.setFriendPhoneNo(mMsgJSONobj.optString(SENDERPHONE));
                mDataFileChat.setFriendId(mMsgJSONobj.optString(SENDERID));
                mDataFileChat.setSenderId(mMsgJSONobj.optString(SENDERID));
                mDataFileChat.setSenderName(mMsgJSONobj.optString(SENDER_NAME));
                mDataFileChat.setFriendName(mMsgJSONobj.optString(SENDER_NAME));

                mDataFileChat.setChattype(mMsgJSONobj.optString(TYPE));

                //For Image
                JSONObject mPhotoDetailsJson = mMsgJSONobj.getJSONObject(PHOTO_DETAILS);
                mDataFileChat.setFileUrl(mPhotoDetailsJson.optString(IMAGE_URL, ""));
                mDataFileChat.setIsMasked(mPhotoDetailsJson.optString(ISMASKED, "0"));
                mDataFileChat.setMaskEnabled(mPhotoDetailsJson.optString(ISMASKED, "0"));
                mDataFileChat.setThumbBase64(mPhotoDetailsJson.optString(THUMB_BASE64, ""));

                Bitmap mBitmap = null;
                try {
                    mBitmap = CommonMethods.decodeBase64(mDataFileChat.getThumbBase64());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String fileName = mDataFileChat.getFileUrl().substring(mDataFileChat.getFileUrl().lastIndexOf('/') + 1, mDataFileChat.getFileUrl().length());

                File incomingImageFile = MediaUtils.saveImageStringToFile(mBitmap,
                        context, fileName);
                if (incomingImageFile != null) {
                    mDataFileChat.setThumbFilePath(incomingImageFile.getPath());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mDataFileChat;
    }


    public static String getType(JSONObject mMsgJSONobj) {
        return mMsgJSONobj.optString(TYPE, "");
    }

    public static String getMessageType(JSONObject mMsgJSONobj) {
        return mMsgJSONobj.optString(CHATTYPE, "");
    }

    public static String getChateStateJson(String chatType, String friendId, ChatState chatState) {

        JSONObject mMsgBodyJSON = new JSONObject();

        //Main chat Body
        try {
            mMsgBodyJSON.put(CHATTYPE, chatType);
            mMsgBodyJSON.put(SENDERID, friendId);
            mMsgBodyJSON.put(TYPE, "status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMsgBodyJSON.toString();
    }
}
