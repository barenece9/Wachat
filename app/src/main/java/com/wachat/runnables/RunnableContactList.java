package com.wachat.runnables;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.DataContact;
import com.wachat.data.DataContactSync;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableSync;
import com.wachat.storage.TableTimeStamp;
import com.wachat.util.CommonMethods;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;
import com.wachat.util.WebContstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public class RunnableContactList implements Runnable {

    private static final String LOG_TAG = LogUtils
            .makeLogTag(RunnableContactList.class);
    private TableContact mTableContact;

    private InterfaceResponseCallback mInterface;
    private DataContactSync mDataContactSync;
    private DataContactSync mDataGroupSync;
    private Context mContext;
    private String numbers = "";
    private String userId = "";
    private String ownNumber = "";
    private String countryCode = "";
    private TableTimeStamp mTableTimeStamp;
    private TreeSet<String> uniqueContactSet;

    public RunnableContactList(Context mContext, ArrayList<DataContact> mListContact,
                               String ownNumber,
                               String numbers, String userId,
                               TableTimeStamp mTableTimeStamp,
                               String countryCode, InterfaceResponseCallback mInterface) {
        this.mContext = mContext;
        this.ownNumber = ownNumber;
        this.numbers = numbers;
        this.userId = userId;
        this.mTableTimeStamp = mTableTimeStamp;
        this.countryCode = countryCode;
        this.mInterface = mInterface;
        mTableContact = new TableContact(mContext);

    }

    @Override
    public void run() {
        LogUtils.i(LOG_TAG, "RunnableContactList:run()");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mTableTimeStamp.getTimeStamp().equals("all")) {
            AppVhortex.getInstance().isContactChangeSyncing = true;
        }

        ArrayList<DataContact> deviceContacts = null;
        if (!new TableSync(mContext).isSynced()) {
            deviceContacts = fetchContactPhone(mContext);
            mTableTimeStamp.setStatus("0");
        }


        LogUtils.i(LOG_TAG, "RunnableContactList:fetchContactPhone : mListContactPhone="
                + (deviceContacts != null ? deviceContacts.size() : "0"));

        if (deviceContacts != null) {
            //i.e. again device contacts fetched
            if (uniqueContactSet != null && uniqueContactSet.size() > 0) {
                this.numbers = StringUtils.join(
                        uniqueContactSet.toArray(new String[uniqueContactSet.size()]),
                        ",");
            } else {
                this.numbers = mTableContact.getNumbers();
            }
        } else {
            this.numbers = mTableContact.getNumbers();
        }

        if (AppVhortex.DEBUG) {

            ArrayList<DataContact> allList = mTableContact.getAllList();
            ArrayList<DataContact> registeredUserList = mTableContact.getRegisteredUser();
            LogUtils.i(LOG_TAG, "RunnableContactList:mTableContact.getAllList():"
                    + (allList != null ? allList.size() : "0"));

            LogUtils.i(LOG_TAG, "RunnableContactList:mTableContact.getRegisteredUser():"
                    + (registeredUserList != null ? registeredUserList.size() : "0"));
        }
//        else {
        try {
            callContactListWebService();

            if (mDataContactSync != null
                    && mDataContactSync.getResponseCode() == WebContstants.ResponseCode_200) {

                callGroupListWebService();


                if (mDataGroupSync != null && mDataGroupSync.getResponseCode()
                        == WebContstants.ResponseCode_200) {

                    if (deviceContacts != null && deviceContacts.size() > 0) {


                        mTableContact.updateDeletedStatus(deviceContacts);
                        boolean isSuccess = mTableContact.insertBulk(deviceContacts);
                        if (isSuccess) {
                            new TableSync(mContext).setStatus("1");
                        }
//            mInterface.onResponseList(mListContactPhone);
                    } else {
//            mInterface.onResponseFaliure("");
                    }

                    mDataContactSync.setmListGroup(mDataGroupSync.getmListGroup());
                    mDataContactSync.currentGroupIds = mDataGroupSync.currentGroupIds;
                    mInterface.onResponseObject(mDataContactSync);
                } else {
                    mInterface.onResponseFaliure("");
                }
            } else if (mDataContactSync != null
                    && mDataContactSync.getResponseCode() == WebContstants.ResponseCode_300) {
                mInterface.onResponseFaliure(String.valueOf(WebContstants.ResponseCode_300));
            } else {
                mInterface.onResponseFaliure("");
            }
        } catch (Exception e) {
            mInterface.onResponseFaliure("");
            e.printStackTrace();
        }
//        }
        mContext = null;
        mTableContact = null;
        mDataContactSync = null;
        mDataGroupSync = null;
    }

    private void callContactListWebService() {

        ArrayList<NameValuePair> mListPairForContact = CommonMethods.getHeaderValue();
        //ws returns empty grouplist if 'phoneNo' = blank. hence provide some random value
        mListPairForContact.add(new BasicNameValuePair(WebContstants.phoneNo,
                TextUtils.isEmpty(numbers) ? "1262" : numbers));
        mListPairForContact.add(new BasicNameValuePair(WebContstants.serverTimeStamp,
                mTableTimeStamp.getTimeStamp()));
        mListPairForContact.add(new BasicNameValuePair(WebContstants.userId, userId));
        String deviceUniqueId = CommonMethods.getDeviceUniqueId(mContext);
        mListPairForContact.add(new BasicNameValuePair(WebContstants.deviceUniqueId, deviceUniqueId));
        mListPairForContact.add(new BasicNameValuePair(WebContstants.deviceGcmToken, Prefs.getInstance(mContext).getGcmId()));
        LogUtils.i(LOG_TAG, "Api:ContactList: userId: " + userId + " Timestamp: "
                + mTableTimeStamp.getTimeStamp() + " num: " + numbers);
        HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlContactList);
        String result = mHttpConnectionClass.getResponse(mListPairForContact);
        LogUtils.i(LOG_TAG, result);
        if (result != null && result.length() > 0) {
            startParsingContactList(result);
        }
    }

    private void callGroupListWebService() {

        String groupListTimeStamp = Prefs.getInstance(mContext).getGroupListTimeStamp();
        ArrayList<NameValuePair> mListPairForContact = CommonMethods.getHeaderValue();
        mListPairForContact.add(new BasicNameValuePair(WebContstants.serverTimeStamp,
                groupListTimeStamp));
        mListPairForContact.add(new BasicNameValuePair(WebContstants.userId, userId));

        LogUtils.i(LOG_TAG, "Api:GroupList: userId: " + userId + " Timestamp: "
                + groupListTimeStamp);
        HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.GroupList);
        String result = mHttpConnectionClass.getResponse(mListPairForContact);
        LogUtils.i(LOG_TAG, result);
        if (result != null && result.length() > 0) {
            startParsingGroupList(result);
        }
    }

    private void startParsingGroupList(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mDataGroupSync = mapper.readValue(result, DataContactSync.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mDataGroupSync != null) {
            if (mDataGroupSync.getResponseCode() == WebContstants.ResponseCode_200) {
                Prefs.getInstance(mContext).setGroupListTimeStamp(String.valueOf(mDataGroupSync.getTimeStamp()));

//                TableGroup mTableGroup = new TableGroup(mContext);
//                if (!TextUtils.isEmpty(mDataGroupSync.currentGroupIds)) {
//                    mTableGroup.deleteGroupApartFromCurrentGroupIds(mDataGroupSync.currentGroupIds);
//                }
//                if (mDataGroupSync.getmListGroup() != null && mDataGroupSync.getmListGroup().size() > 0) {
//
//                    mTableGroup.bulkInsertGroup(mDataGroupSync.getmListGroup());
//                }
            }
        }
    }


    private void startParsingContactList(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mDataContactSync = mapper.readValue(result, DataContactSync.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mDataContactSync != null) {
            if (mDataContactSync.getResponseCode() == WebContstants.ResponseCode_200) {
                mTableTimeStamp.setStatus(String.valueOf(mDataContactSync.getTimeStamp()));
                condition:
                {
                    for (DataContact mDataContact : mDataContactSync.getmListContact()) {
                        if (mDataContact.getPhoneNumber().contains(ownNumber)) {
                            mDataContactSync.getmListContact().remove(mDataContact);
                            break condition;
                        }
                    }
                }

//                if (mDataContactSync.getmListContact() != null && mDataContactSync.getmListContact().size() > 0) {
//                    TableContact mTableContact = new TableContact(mContext);
//
//                    mTableContact.updateUser(mDataContactSync.getmListContact());
//                }

//
//                mInterface.onResponseObject(mDataContactSync);
////                JsonNode mJsonNode = mapper.readTree(result);
////                mListCountry = mapper.readValue(mJsonNode.get(WebContstants.countries).toString(), new TypeReference<ArrayList<DataCountry>>() {
////                });
            }
        }
    }

    private ArrayList<DataContact> fetchContactPhone(Context mContext) {
        LogUtils.i(LOG_TAG, "RunnableContactList:fetchContactPhone");
        ArrayList<DataContact> mListContactPhone = new ArrayList<DataContact>();
        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = null;
        try {
            cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, null);
            if (cur != null && cur.getCount() > 0 && cur.moveToFirst()) {
                do {
                    if (Integer.parseInt(cur.getString(cur
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        String id = cur.getString(cur
                                .getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Cursor pCur = null;
                        try {
                            pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = ?", new String[]{id}, null);
                            if (pCur != null && pCur.getCount() > 0 && pCur.moveToFirst()) {
                                DataContact mDataContact = null;
                                do {
                                    String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    phoneNo = phoneNo.replaceAll("[^0123456789+]", "");
                                    phoneNo = replaceAndConcatNumber(phoneNo);
                                    if (!TextUtils.isEmpty(phoneNo)) {
                                        String image_uri = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                                        mDataContact = new DataContact();
                                        mDataContact.setPhoneNumber(phoneNo);
                                        mDataContact.setName(name);
                                        mDataContact.setImageUrl((image_uri != null) ? image_uri : "");
                                        mDataContact.setIsSynced(0);
                                        if (phoneNo.contains(ownNumber)) {

                                        } else {
                                            if (uniqueContactSet == null) {
                                                uniqueContactSet = new TreeSet<String>();
                                            }
                                            if (!uniqueContactSet.contains(phoneNo)) {
                                                uniqueContactSet.add(phoneNo);
                                            }
                                            mListContactPhone.add(mDataContact);
                                        }
                                    }

                                } while (pCur.moveToNext());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (pCur != null) {
                                pCur.close();
                            }
                        }
                    }
                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null) {
                cur.close();
            }
        }


        return mListContactPhone;
    }


    private String replaceAndConcatNumber(String phoneNo) {
        if (phoneNo.startsWith("+")) {
            return phoneNo.replace("+", "");
        } else if (phoneNo.startsWith("00")) {
            phoneNo = phoneNo.substring(2, phoneNo.length());
        } else if (phoneNo.startsWith("0")) {
            phoneNo = /*countryCode
                    + */phoneNo.substring(1, phoneNo.length());
        }/* else {
            phoneNo = countryCode + phoneNo;
        }*/
        return phoneNo;
    }
}
