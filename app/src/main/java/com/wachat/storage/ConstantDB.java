package com.wachat.storage;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public interface ConstantDB {

    String DB_NAME = "db_Vhortext";

    String TableCountry = "tbl_country";
    String countryCode = "countryCode", countryName = "countryName";

    String TableChat = "Chat";
    String messageID = "messageID",
            messageBody = "messageBody",
            senderChatID = "senderChatID",
            chatType = "chatType",
            messageType = "messageType",
            strGroupID = "strGroupID",
            messageDateTime = "messageDateTime",
            deliveryStatus = "deliveryStatus",
            strAttachmentID = "strAttachmentID",
            strTranslatedText = "strTranslatedText",
            friendPhoneNo = "friendPhoneNo",
            friendChatID = "friendChatID",
            userID = "userID",
            attachContactName = "attachContactName",
            attachContactNo = "attachContactNo",
            attachBase64str4Img = "attachBase64str4Img",
            loc_lat = "loc_lat",
            loc_long = "loc_long",
            loc_address = "loc_address" ,
            fileLocalPath = "fileLocalPath",
            maskImagePath = "maskImagePath",
            fileUrl = "fileUrl",
            thumbBase64 = "thumbBase64",
            thumbFilePath = "thumbFilePath",
            downloadStatus = "downloadStatus",
            uploadStatus = "uploadStatus",
            youtubeTitle = "youtubeTitle",
            youtubeDesc = "youtubeDesc",
            youtubePublishTime = "youtubePublishTime",
            youtubeThumbUrl = "youtubeThumbUrl",
            youtubeVidId = "youtubeVidId",
            isMasked = "isMasked",
            maskEnabled = "maskEnabled",
            yahooTitle = "yahooTitle",
            yahooDesc = "yahooDesc",
            yahooPublishTime = "yahooPublishTime",
            yahooImageUrl = "yahooImageUrl",
            yahooShareLink = "yahooShareLink",
            sender_name = "sender_name",
            sender_id = "sender_id",
            friend_name = "friend_name",
            friend_id = "friend_id";

    String TableAttachmentDetails = "AttachmentDetails";
    String attachmentID = "attachmentID", httpURL = "httpURL", filePath = "filePath", fileType = "fileType",
            thumbnailName = "thumbnailName", friendOrGrpID = "friendOrGrpID" ,assetURL= "assetURL";

    String TableChatLocation = "ChatLocation";
    String strID = "strID", lat = "lat", lng = "lng";

    String TableContactList = "ContactList";
    String PhoneNumber = "PhoneNumber",
            Name = "Name",
            FriendId = "FriendId",
            IsRegistered = "IsRegistered",
            FriendImageLink = "FriendImageLink",
            ChatId = "ChatId",
            Status = "Status",
            IsDeleted = "IsDeleted",
            AppName = "AppName",
            AppFriendImageLink = "AppFriendImageLink",
            isfindbyphoneno = "isfindbyphoneno",
            gender = "gender",
            relation = "relation",
            IsBlocked = "IsBlocked",
            IsFavorite = "IsFavorite",
            isSynced = "isSynced",
            isFriend = "isFriend",
            imageUrl = "imageUrl",
            isSelectedForGroup = "isSelectedForGroup";

    String TableFriendAvailableStatus = "FriendAvailableStatus";
    String FriendChatId = "FriendChatId", AvailableStatus = "AvailableStatus", InvisibleStatus = "InvisibleStatus";

    String TableGroupDetails = "GroupDetails";
    String GroupId = "GroupId", GroupName = "GroupName", GroupJId = "GroupJId",GroupImage = "GroupImage", OwnerId = "OwnerId", CreationDateTime = "CreationDateTime";

    String TableGroupMember = "GroupMember";
    String MemberName = "MemberName", MemberId = "MemberId", MemberProfilePic =
            "MemberProfilePic", MemberPhoneNo = "MemberPhoneNo", MemberCountryId = "MemberCountryId",
            IsOwner = "IsOwner" ,
            IsGrpadmin = "IsGrpadmin" ,
            IsGrpblock = "IsGrpblock" ,
            MemberStatus = "MemberStatus";

    String TableUserInfo = "UserInfo";
    String UserId = "UserId", UserName = "UserName", ProfileImage = "ProfileImage", PhoneNo = "PhoneNo", CountryCode = "CountryCode", UserStatus = "UserStatus",
            IsVerified = "IsVerified", PairingValue = "PairingValue", VerificationCode = "VerificationCode", Language = "Language",
            LanguageIdentifire = "LanguageIdentifire", CountryName = "CountryName", Gender = "Gender" , IsTranslated="IsTranslated" ,
            IsFindByLocation = "IsFindByLocation",  IsFindByPhoneno = "IsFindByPhoneno",isNotificationOn = "isNotificationOn"  ;

    String TableUserStatus = "UserStatus";
    String statusId = "statusId", status = "status", selected = "selected";

    String TableContactSync = "ContactSync";
    String TableTimeStamp = "TimeStamp";
    String strTimeStamp = "strTimeStamp";

    String TableLanguage = "Language";
    String langId = "langid", LanguageName = "languageName", languageCode = "languageCode";
}
