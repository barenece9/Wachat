package com.wachat.util;

/**
 * Created by Gourav Kundu on 17-08-2015.
 */
public interface Constants {

    String SET_0 = "0";
    String SET_1 = "1";

    int ContactSynced = 1321;

    int ContactSyncTiming = 30000;

    int DELAY_SPLASH = 1500;
    int ACTION_IMAGE_CAPTURE = 151;
    int ACTION_VIDEO_CAPTURE = 2;
    int PICK_IMAGE_FROM_GALLERY = 3;

    int ImagePickerChat = 101;
    int ImagePickerSelection = 201;
    int ChatToSelection = 105;
    int ContactPickerChat = 201;
    int EditToLang = 520;
    int ChooseCountry = 106;
    int VideoPickerChat = 111;
    int YouTubeVideoPicker = 112;
    int SketchToSelection = 401;
    int LocationPikerChat = 301;
    int editGrp = 601;
    int YahooNewsPicker = 113;

    String file = "file://";
    String IMAGE_BLUR = "wachat.image.blur";
    String IMAGE = "wachat.image";
    String ENCODE = "wachat.image.base64";


    int ImageResultChat = 102;
    int ImageResultSelection = 205;

    /**********
     * CONSTANTS FOR VISIBILITY
     ********/
    int SHOW = 0;
    int HIDE = 1;


    enum DASH_TYPE {
        CHAT, FAVORITE, GROUP, CONTACTS, MORE
    }

    String B_TYPE = "b_type";
    String B_ID = "b_id";
    String B_NAME = "b_name";
    String B_RESULT = "B_RESULT";
    String B_CODE = "B_CODE";
    String B_NEED_UPDATED = "B_NEED";
    String B_OBJ = "b_obj";
    String B_ERROR_OBJ = "b_error_obj";
    String B_RESPONSE_OBJ = "b_response_obj";
    String B_CAMERA_INMAGE = "isCamera";

    enum SELECTION {
        CHAT_TO_IMAGE, SELECTION_TO_IMAGE, CHAT_TO_SELECTION
    }

    String[] status = {
//            "Urgent calls only",
            "In a meeting",
            "At the movies",
            "At school",
            "Busy",
            "At work",
            "Be the best you",
    };
    String KEY_FILE_UPLOAD_STATUS = "file_upload_status";
    String KEY_FILE_UPLOAD_PROGRESS = "file_upload_progress";
    String ACTION_FILE_UPLOAD_COMPLETE = "com.wachat.ACTION_FILE_UPLOAD_COMPLETE";
    String ACTION_FILE_UPLOAD_PROGRESS = "com.wachat.ACTION_FILE_UPLOAD_PROGRESS";

    String UPLOAD_STATUS_SUCCESS = "com.wachat.UPLOAD_STATUS_SUCCESS";
    String UPLOAD_STATUS_FAILED_NETWORK_ERROR = "com.wachat.UPLOAD_STATUS_FAILED_NETWORK_ERROR";
    String UPLOAD_STATUS_FAILED_SERVER_ERROR = "com.wachat.UPLOAD_STATUS_FAILED_SERVER_ERROR";
    String UPLOAD_STATUS_UPLOADING = "com.wachat.UPLOAD_STATUS_UPLOADING";
}
