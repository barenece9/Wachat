package com.wachat.util;

import com.wachat.R;
import com.wachat.application.AppVhortex;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public interface WebContstants {

    String YOUTUBE_DATA_VIDEO_SEARCH_BASE_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&key=" + AppVhortex.applicationContext.getResources().getString(R.string.youtube_api_key_client)+"&maxResults=10";
    String UrlselectCountry = "http://ip-api.com/json";
    String UrlGapiForLang = "https://www.googleapis.com/language/translate/v2/languages?key=" + AppVhortex.applicationContext.getResources().getString(R.string.google_translate_key) + "&prettyprint=false&target=en";
    String UrlYahooNewsList = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20rss%20where%20url%3D%22http%3A%2F%2Frss.news.yahoo.com%2Frss%2Ftopstories%22&format=json";



    /**Dev*/
//    String base_root = "http://52.6.251.159/~mobapp2/Wachat/";

    /**Live*/
    String base_root = "https://emathready.com/~wachat/";


//    String base = base_root+"v1/";
    String base = base_root+"v2/";

    String about_us = base_root+"about.php";
    String help_faq = base_root+"help.php";

    String copyright = base_root+"intellectual.php";
    String terms = base_root+"terms.php";
    String privacy = base_root+"privacy.php";

    String UrlCountrylist = base + "CountryList";
    String UrlRegisterUser = base + "RegisterUser";
    String UrlValidateUser = base + "ValidateUser";
    String UrlValidateUserSession = base + "ValidateUserSession";
    String UrlRegisterChat = base + "RegisterChat";
    String UrlContactList = base + "ContactListNew";
    String UrlUpdateStatus = base + "UpdateStatus";
    String UrlResendOTP = base + "ResendOTP";
    String UrlEditProfile = base + "EditProfile";
    String UrlCreateOrEditGroup = base + "CreateAndEditGroup";
    String UrlBlockUser = base + "BlockUser";
    String UrlFavoriteUser = base + "FavoriteUser";
    String UrlFriendProfile = base + "FriendProfile";
    String UrlUpdateUserLocation = base + "UpdateUserLocation";
    String UrlDeleteAccount = base + "DeleteAccount";
    String UrlInviteFriend = base + "InviteFriend";
    String UrlFileUpload = base + "FileUpload";
    String UrlFindMeByMyLocation = base + "FindMeByMyLocation";
    String UrlFindMeByMyPhoneNumber = base + "FindMeByMyPhoneNumber";
    String UrluserTranslate = base + "userTranslate";
    String UrlFindPeopleByLocation = base + "searchuser";
    String Urlsendmail = base+"sendmail";
    String LeaveGroup = base+"LeaveGroup";
    String DeleteGroup = base+"DeleteGroup";
    String SetFriendStatus = base+"setFriendStatus";
    String GROUP_ADMIN_ACTION = base+"groupAdminAction";
    String SendNotification = base+"SendNotification";
    String EnableNotification = base+"EnableNotification";

    String GroupList = base+"GroupList";
    String VERSION_CHECK_API = base+"vcntrl";

    int ResponseCode_200 = 200;
    int ResponseCode_201 = 201;
    int ResponseCode_210 = 210;
    int ResponseCode_299 = 299;
    int ResponseCode_300 = 300;

    String apikey = "apikey";
    String apikeyValue = "hgfyhfyi87hgc67";

    String countries = "countries";
    String countryCode = "countryCode";
    String phoneNo = "phoneNo";
    String deviceUniqueId = "deviceUniqueId";
    String deviceGcmToken= "deviceToken";
    String deviceType = "deviceType";
    String Android = "Android";

    String userId = "userId";
    String verificationCode = "verificationCode";
    String changeAccountParing = "changeAccountParing";

    String chatId = "chatId";
    String serverTimeStamp = "serverTimeStamp";
    String status = "status";

    String name = "name";
    String image = "image";
    String image_send = "attachment";
    String language = "language";
    String gender = "gender";
    String langCode = "langCode";

    String video = "video";
    String groupName = "groupName";
    String groupMemberUserId = "groupMemberUserId";
    String creationDateTime = "creationDateTime";
    String groupId = "groupId";
    String groupJId = "groupJId";
    String groupImage = "groupImage";
    String friendChatId = "friendChatId";
    String blockStatus = "blockStatus";
    String favoriteStatus = "favoriteStatus";

    String friendId = "friendId";
    String friendNo = "friendNo";
    String lat = "lat";
    String lng = "lng";
    String radius = "radius";
    String userid = "userid";
    String languages = "languages";

    String searchText = "searchtext";
    String mlong = "long";
    String istransalate = "istransalate";
    String countryName = "countryName";
    String userName = "userName";
    String subject="subject";
    String userno="userno";
    String message ="message";
}
