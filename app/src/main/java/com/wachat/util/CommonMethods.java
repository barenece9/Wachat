package com.wachat.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.dataClasses.ContactSetget;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * Created by Gourav Kundu on 20-08-2015.
 */
public class CommonMethods {

    //private static final float BITMAP_SCALE = 1f;
    private static final float BLUR_RADIUS = 10.0f;
    private static final float BITMAP_SCALE = 0.4f;
    @SuppressLint("InlinedApi")
    public static NetworkInfo networkInfo;

    public static DisplayMetrics getScreenWidth(Context mContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        return displaymetrics;
    }

    public static boolean checkBuildVersionAsyncTask() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) ? true : false;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static int dpToPx(Context mContext, int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static void MYToast(Context context, String txt) {
        Toast toast = Toast.makeText(context, txt, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity
                    .getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //normal font
    public static void setCustomFontLight(Context mContext, TextView editText) {
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),
                "font/Roboto-Light_1.ttf");
        editText.setTypeface(custom_font);
    }


/*
    public static String getDateTime(String time) {
        long time1 = Long.parseLong(time);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time1);
        String date = DateFormat.format("dd-M-yyyy hh:mm a", cal).toString();
        //
        String formattedDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm a");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        try {
            Date myDate = simpleDateFormat.parse(date);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            formattedDate = simpleDateFormat.format(myDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
*/

    //bold font
    public static void setCustomFontRegular(Context mContext, TextView editText) {
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),
                "font/Roboto-Regular_1.ttf");
        editText.setTypeface(custom_font);
    }

    // get display width
    public static DisplayMetrics getScreen(Context mContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        return displaymetrics;
    }

//    public static String getEncodedString(String mString) {
//        String mEncodedString = mString;
//        try {
//            mEncodedString = new String(mString.getBytes("ISO-8859-1"), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//
//            e.printStackTrace();
//        }
//
//        String decodedGroupName = Html.fromHtml(mEncodedString).toString();
//        return decodedGroupName;
//    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static String getLocalTime(String str_date) {
        Date utcDate = new Date();
        String localeDate = "";
        Calendar cal = Calendar.getInstance();

        if (!TextUtils.isEmpty(str_date)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy  hh:mm a");
            dateFormat.setTimeZone(TimeZone.getDefault());

            try {
                utcDate = dateFormat.parse(str_date);
                if (utcDate != null) {
                    SimpleDateFormat datelocaleFormat = new SimpleDateFormat("dd-M-yyyy  hh:mm a");
                    localeDate = datelocaleFormat.format(utcDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cal = null;
        utcDate = null;
        return localeDate;
    }

    public static String getDateTime(String timestamp) {
        long milliSeconds = 0;
        try {
            milliSeconds = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
// Create a DateFormatter object for displaying date in specified
// format.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));


// Create a calendar object that will convert the date and time value in
// milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());

    }


    public static String getDateTime(String timestamp,String outputFormat) {
        long milliSeconds = 0;
        try {
            milliSeconds = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
// Create a DateFormatter object for displaying date in specified
// format.
        SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));


// Create a calendar object that will convert the date and time value in
// milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());

    }

    public static String getUTFEncodedString(String mStr) {
        mStr = iosCompatibleSpacetoChar(mStr);
        try {
            mStr = URLEncoder.encode(mStr, "UTF-8");
            // mStr= StringEscapeUtils.escapeJava(mStr);
        } catch (Exception e) {
            return mStr;
        }
        return mStr;
    }

    private static String iosCompatibleSpacetoChar(String mStr) {
        if (mStr.contains(" "))
            mStr = mStr.replace(" ", "%20");
        mStr = mStr.replace(",", "¶¶2");
        mStr = mStr.replace("'", "¶¶1");
        return mStr;
    }

    public static String getUTFDecodedString(String mStr) {

        try {
            mStr = URLDecoder.decode(mStr, "UTF-8");
            // mStr= StringEscapeUtils.unescapeJava(mStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mStr = iosCompatibleChartoSpace(mStr);
        return mStr;
    }

    private static String iosCompatibleChartoSpace(String mStr) {
        if (mStr.contains("%20"))
            mStr = mStr.replace("%20", " ");
        mStr = mStr.replace("¶¶2", ",");
        mStr = mStr.replace("¶¶1", "'");
        return mStr;
    }

    public static ArrayList<ContactSetget> getContact(AppCompatActivity mBaseActivity, Uri uriContact) {
        ArrayList<ContactSetget> mArrList = new ArrayList<ContactSetget>();
        ContactSetget mDataContact;
        String TAG = mBaseActivity.getClass().getSimpleName();
        String contactID = "";
        String contactNumber = "", Type = "";
        String contactName = "";
        Bitmap photo = null;

        // getting contacts ID
        Cursor cursorID = mBaseActivity.getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Cursor cursor = mBaseActivity.getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(mBaseActivity.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                assert inputStream != null;
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = mBaseActivity.getContentResolver().query(Phone.CONTENT_URI,
                null,
                Phone.CONTACT_ID + " = ? ",
                new String[]{contactID},
                null);
        if (cursorPhone != null && cursorPhone.getCount() > 0) {
            cursorPhone.moveToFirst();
            do {
                mDataContact = new ContactSetget();
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(Phone.NUMBER));
                Type = cursorPhone.getString(cursorPhone.getColumnIndex(Phone.TYPE));

                mDataContact.setContactName(contactName);
                mDataContact.setmBitmap(CommonMethods.encodeTobase64(photo, 100));
                mDataContact.setContactNumber(contactNumber);


                switch (Integer.parseInt(Type)) {
                    case Phone.TYPE_HOME:
                        mDataContact.setContactType("Home");
                        break;
                    case Phone.TYPE_MOBILE:
                        mDataContact.setContactType("Mobile");
                        break;
                    case Phone.TYPE_WORK:
                        mDataContact.setContactType("Work");
                        break;
                }

                mArrList.add(mDataContact);
            }
            while (cursorPhone.moveToNext());
        }
        cursorPhone.close();




        return mArrList;
    }

    public static void setListViewHeightBasedOnChildren(Activity mActivity,
                                                        ListView list) {

        ListAdapter listAdapter = list.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(list.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, list);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

            DisplayMetrics displayMetrics = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay()
                    .getMetrics(displayMetrics);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    displayMetrics.widthPixels, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    displayMetrics.heightPixels, View.MeasureSpec.AT_MOST);
            view.measure(widthMeasureSpec, heightMeasureSpec);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = totalHeight
                + (list.getDividerHeight() * (listAdapter.getCount() - 1));
        list.setLayoutParams(params);
        list.requestLayout();
    }

    public static Bitmap fastblur(Bitmap sentBitmap, Context mContext) {

        int width = Math.round(sentBitmap.getWidth() * BITMAP_SCALE);
        int height = Math.round(sentBitmap.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(mContext);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static Bitmap correctBlur(Bitmap image, Activity mActivity) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(mActivity);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    //here we scale bitmap and apply blur
    public static Bitmap blur(Bitmap bkg) {

        float scaleFactor = 8;
        int radius = 2;
        int inputWidth = bkg.getWidth();
        int inputHeight = bkg.getHeight();

        Bitmap overlay = Bitmap.createBitmap((int) (inputWidth / scaleFactor), (int) (inputHeight / scaleFactor), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);

        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);

        canvas.drawBitmap(bkg, 0, 0, paint);

        //  bkg.recycle();
        overlay = FastBlur.doBlur(overlay, radius, true);
        return getResizedBitmap(overlay, inputWidth, inputHeight, true);

    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth, boolean willDelete) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;

        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION

        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP

        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

        //          if(willDelete)
        //              bm.recycle();

        return resizedBitmap;
    }

    @SuppressWarnings("deprecation")
    public static long FreeInternalMemory() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long Free = ((long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize()) / 1048576;
        return Free;
    }

    @SuppressWarnings("deprecation")
    public static long FreeExternalMemory() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long Free = ((long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize()) / 1048576;
        return Free;
    }

    public static int getDensityPixel(Context mContext) {

        int value = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                value = 8;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                value = 60;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                value = 80;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                value = 110;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                value = 160;
                break;
        }
        return value;
    }

    public static ArrayList<NameValuePair> getCommonRequestParams() {
        ArrayList<NameValuePair> mListPair = new ArrayList<NameValuePair>();
        mListPair.add(new BasicNameValuePair(WebContstants.deviceType, WebContstants.Android));
        return mListPair;
    }

    public static ArrayList<NameValuePair> getHeaderValue() {
        ArrayList<NameValuePair> mListPair = new ArrayList<NameValuePair>();
        mListPair.add(new BasicNameValuePair(WebContstants.apikey, WebContstants.apikeyValue));
        return mListPair;
    }

    public synchronized static String getDeviceUniqueId(Context mContext) {

        String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // test for connection for WIFI
        if (networkInfo != null && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            return true;
        }

        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // test for connection for Mobile
        if (networkInfo != null && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            return true;
        }

        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
        // test for connection for Mobile
        return networkInfo != null && networkInfo.isAvailable()
                && networkInfo.isConnected();

    }

    /**
     * @param arrStrings
     * @return comma separated string
     */
    public static String commaSeperatedsStringsFromArray(ArrayList<String> arrStrings) {
        String mStr = "";
        if (arrStrings.size() > 0) {

            for (int i = 0; i < arrStrings.size(); i++) {

                if (mStr.equalsIgnoreCase("")) {
                    mStr = arrStrings.get(i).toString();

                } else {
                    mStr = mStr + ","
                            + arrStrings.get(i).toString();

                }

            }
        }
        return mStr;
    }


    public static JSONObject getChatJson(String timeStamp, String body, String chatId, String chatType, String isOnline, String lang,
                                         String grpId, String grpName, String grpImage, String senderId, String senderPhone, String fileUrl,
                                         String thumb, String _url, String urlThumb, String lat, String lng) {
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("body", body);
            mJsonObject.put("timestamp", timeStamp);
            mJsonObject.put("chatid", chatId);
            mJsonObject.put("chattype", chatType);
            mJsonObject.put("online", isOnline);
            mJsonObject.put("lan", lang);

            JSONObject groupdetails = new JSONObject();
            groupdetails.put("groupid", grpId);
            groupdetails.put("groupname", grpName);
            groupdetails.put("groupimage", grpImage);
            mJsonObject.put("groupdetails", groupdetails);

            JSONObject senderdetails = new JSONObject();
            senderdetails.put("senderid", senderId);
            senderdetails.put("senderphone", senderPhone);
            mJsonObject.put("senderdetails", senderdetails);

            JSONObject file = new JSONObject();
            file.put("fileurl", fileUrl);
            file.put("thumb", thumb);
            mJsonObject.put("file", file);

            JSONObject url = new JSONObject();
            url.put("url", _url);
            url.put("thumb", urlThumb);
            mJsonObject.put("url", url);

            JSONObject location = new JSONObject();
            location.put("lat", lat);
            location.put("lng", lng);
            mJsonObject.put("location", location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mJsonObject;
    }


    public static boolean checkURLFormat(String url_) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(url_);
            URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }


    public static String[] getCommaseperateToArray(String mString) {

        String[] commaSeperatedArr = new String[0];
        if (!TextUtils.isEmpty(mString))
            commaSeperatedArr = StringUtils.split(mString, ",");
        return commaSeperatedArr;
    }

    public static String getTodayorYesterDay(String date, Context mContext) {
        try {
            date = DateTimeUtil.getFormattedDate(date);
            String chatDate = DateTimeUtil.convertUtcToLocal(date, mContext.getResources().getConfiguration().locale);
            String nowDate = DateTimeUtil.getLocalDateFromMilliSecond(DateTimeUtil.getCurrentTimeStampInMilliSecond(), "M-dd-yyyy hh:mm a");
            // nowDate = DateTimeUtil.convertUtcToLocal(nowDate, mContext.getResources().getConfiguration().locale);
            long daysCount = DateTimeUtil.calculateDayDifference(nowDate, chatDate);
            if (daysCount == 0)
                return "Today";
            if (daysCount == 1)
                return "Yesterday";
            if (daysCount > 2)
                return "Next";
            else
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTodayorYesterDayForLocalDate(String date, Context mContext) {
        try {
            date = DateTimeUtil.getFormattedDate(date);
            String chatDate = date;
            String nowDate = DateTimeUtil.getLocalDateFromMilliSecond(DateTimeUtil.getCurrentTimeStampInMilliSecond(), "M-dd-yyyy hh:mm a");
            // nowDate = DateTimeUtil.convertUtcToLocal(nowDate, mContext.getResources().getConfiguration().locale);
            long daysCount = DateTimeUtil.calculateDayDifference(nowDate, chatDate);


            Date date1 = null,date2 = null;
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("dd-M-yyyy hh:mm a");
            try {
                date1 = simpleDateFormat.parse(nowDate);
                date2 = simpleDateFormat.parse(chatDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(date1);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(date2);
            int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
            int day2 = calendar2.get(Calendar.DAY_OF_MONTH);

            int month1 = calendar1.get(Calendar.DAY_OF_MONTH);
            int month2 = calendar2.get(Calendar.DAY_OF_MONTH);

            int year1 = calendar1.get(Calendar.DAY_OF_MONTH);
            int yera2 = calendar2.get(Calendar.DAY_OF_MONTH);

//            if()
            if (daysCount == 0)
                return "Today";
            if (daysCount == 1)
                return "Yesterday";
            if (daysCount > 2)
                return "Next";
            else
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encodeTobase64(Bitmap image) {
        if (image == null)
            return "";
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.NO_WRAP);
        return imageEncoded;
    }

    public static String encodeTobase64(Bitmap image, int quality) {
        if (image == null)
            return "";
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.NO_WRAP);
        return imageEncoded;
    }

  /*  public static String encodeTobase64(Bitmap image) {
        if (image == null)
            return "";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String imageEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return imageEncoded;
    }*/

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        //  byte[] data = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static Bitmap getImageThumbnail(String path) {
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), AppVhortex.width / 2, AppVhortex.width / 2);
    }

    public static String getThumbBase64(Bitmap ThumbImage) {
        return CommonMethods.encodeTobase64(ThumbImage);

    }

    public static String saveThumbBitmap(Bitmap thumbBitmap, String originalImagePathm, Context context) {
        File thumbFile = null;
        try {
            thumbFile = MediaUtils.getOutputMediaFile(MediaUtils.MEDIA_TYPE_IMAGE, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(thumbFile);
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 25, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbFile != null ? thumbFile.getPath() : "";
    }


    public static Integer[] getImageWidthFromDrawable(Context mContext, int icon) {
        Integer[] mInt = new Integer[2];
        BitmapDrawable bd = (BitmapDrawable) mContext.getResources().getDrawable(icon);
        int height = bd.getBitmap().getHeight();
        int width = bd.getBitmap().getWidth();
        mInt[0] = width;
        mInt[1] = height;
        return mInt;
    }

    public static String getRealImagePathFromURI(Uri contentUri, Context context) {
        Cursor cursor = null;
        String str = null;
        try {
            int column_index = 0;
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj,
                    null, null, null);
            column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();
            str = cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return str;

    }


    public static String getAlertMessageFromException(Context context,
                                                      Throwable exception) {
        if (exception == null) {
            return context.getResources().getString(R.string.alert_failure_unknown_error);
        }
        if (exception instanceof SocketTimeoutException
                || exception instanceof ConnectTimeoutException) {

            return context.getResources().getString(R.string.alert_failure_network_time_out);
        } else if (exception instanceof HttpHostConnectException) {
            return context.getResources().getString(
                    R.string.alert_failure_network_connection_failed);
        } else if (exception instanceof UnknownHostException) {
            return context.getResources().getString(
                    R.string.alert_failure_network_connection_failed);
        } else if (exception instanceof SSLPeerUnverifiedException
                || exception instanceof SSLHandshakeException
                || exception instanceof NoResponseFromServerException) {
            return context.getResources().getString(
                    R.string.alert_failure_server_connection_failed_);
        } else {
            return context.getResources().getString(R.string.alert_failure_unknown_error);
        }

    }


    public static void writePhoneContact(String displayName, String number, Context cntx ) {
        Context contetx = cntx; //Application's context or Activity's context
        String strDisplayName = displayName; // Name of the Person to add
        String strNumber = number; //number of the person to add with the Contact

        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        //Newly Inserted contact
        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)//Step1
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)//Step2
                .withValueBackReference(Data.RAW_CONTACT_ID, contactIndex)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                .build());
        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)//Step 3
                .withValueBackReference(Data.RAW_CONTACT_ID, contactIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, strNumber) // Number to be added
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc
        try {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = contetx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
        } catch (RemoteException exp) {
            exp.printStackTrace();
        } catch (OperationApplicationException exp) {
            exp.printStackTrace();
        }
    }

    public static String getVideoIdFromYoutubeURL(String url) {
        String videoId = "";
        if (url.contains("http://www.youtube.com/embed/")) {
            videoId = url.replace("http://www.youtube.com/embed/", "");
        } else if (url.contains("http://www.youtube.com/watch?v=")) {
            videoId = url.replace("http://www.youtube.com/watch?v=", "");
        } else if (url.contains("www.youtube.com/embed/")) {
            videoId = url.replace("www.youtube.com/embed/", "");
        } else if (url.contains("https://www.youtube.com/embed/")) {
            videoId = url.replace("https://www.youtube.com/embed/", "");
        }else if (url.contains("http://youtu.be/")) {
            videoId = url.replace("http://youtu.be/", "");
        }else if (url.contains("https://youtu.be/")) {
            videoId = url.replace("https://youtu.be/", "");
        }
        int index = videoId.indexOf("?");
        if (index != -1)
            videoId = videoId.substring(0, index);
        LogUtils.i("Youtube", videoId);
        return videoId;
    }

    public static boolean isYoutubeVideo(String url){
        return url.contains("youtube.com")||url.contains("youtu.be")?true:false;
    }

    public static String getThumbnailUrlFromYoutubeURL(String url) {
        String thumbnailUrl = "";
        thumbnailUrl = "http://img.youtube.com/vi/"
                + getVideoIdFromYoutubeURL(url) + "/hqdefault.jpg";
        return thumbnailUrl;
    }

}
