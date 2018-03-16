package com.wachat.application;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.provider.ContactsContract;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.crittercism.app.Crittercism;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;
import com.wachat.R;
import com.wachat.activity.ActivityChat;
import com.wachat.activity.ActivityDash;
import com.wachat.activity.ActivitySelectCountry;
import com.wachat.activity.ActivitySplash;
import com.wachat.chatUtils.CompressImageUtils;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.chatUtils.MultipartRequest;
import com.wachat.data.BaseResponse;
import com.wachat.data.DataCountry;
import com.wachat.data.DataTextChat;
import com.wachat.runnables.ProcessDownloadedImageRunnable;
import com.wachat.services.ServiceContact;
import com.wachat.services.ServiceCountry;
import com.wachat.services.ServiceXmppConnection;
import com.wachat.storage.DBHelper;
import com.wachat.storage.TableAttchmentDetails;
import com.wachat.storage.TableChat;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableCountry;
import com.wachat.storage.TableGroup;
import com.wachat.storage.TableSync;
import com.wachat.storage.TableTimeStamp;
import com.wachat.storage.TableUserInfo;
import com.wachat.storage.TableUserStatus;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;
import com.wachat.util.MediaUtils;
import com.wachat.util.Prefs;
import com.wachat.util.WebContstants;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Gourav Kundu on 17-08-2015.
 */
public class AppVhortex extends MultiDexApplication {

    public static final String TAG = AppVhortex.class
            .getSimpleName();
    public static final boolean DEBUG = true;
    public static volatile AppVhortex applicationContext;
    private static Prefs mPrefs;
    public static int width;
    public static String Lat = "0.0", Long = "0.0";
    public static Point displaySize = new Point();
    //    public Messenger messengerBound;
    public ResponseReceiver mResponseReceiver;
    public boolean isSyncedFirst = false;
    public DBHelper mDbHelper;
    public WeakReference<AppCompatActivity> mBaseActivity;
    private ExecutorService sessionExecutorService;
    public String countryCode = "";
    public boolean serviceContactRunning = false;
    /**
     * mobile.crash.report
     */
    private String CRITISIM_APPID = "5602a49fd224ac0a00ed3de1";

    /**
     * argha.d
     */
//    private String CRITISIM_APPID = "561dfc658d4d8c0a00d07d9d";
    private SessionHandler mSessionHandler;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private HashMap<String, DataTextChat> uploadQueueMap;
    private HashMap<String, DataTextChat> downloadQueueMap;

    private ThinDownloadManager thinDownloadManager;
    private Bitmap groupCreateImageBitmap;

    public boolean isContactChangeSyncing = false;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;

    /**
     * Method checks if the app is in background or not
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }


        return isInBackground;
    }

    public static synchronized AppVhortex getInstance() {
        return applicationContext;
    }

    public void setGroupCreateImageBitmap(Bitmap groupCreateImageBitmap) {
        this.groupCreateImageBitmap = groupCreateImageBitmap;
    }

    public Bitmap getGroupCreateImageBitmap() {
        return this.groupCreateImageBitmap;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

 /*   private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(AppVhortex.this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(AppVhortex.this)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(5) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(AppVhortex.this)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();
        ImageLoader.getInstance().init(config);
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        initComponent();
        registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());

        initDB();
        initImageLoader();
        Crittercism.initialize(getApplicationContext(), CRITISIM_APPID);
        if (!Prefs.getInstance(applicationContext).getUserId().equals("") ||
                Prefs.getInstance(applicationContext).getUserId().trim().length() > 0) {
            setSyncStatus("0");
//            new TableTimeStamp(getApplicationContext()).setStatus("0");
            Prefs.getInstance(this).setGroupListTimeStamp("0");
            checkXmppConnection();
        }

//        initThinDownloadManager();
        ContactObserver mContactObserver = new ContactObserver(new Handler());
        getContentResolver()
                .registerContentObserver(
                        ContactsContract.Contacts.CONTENT_URI, true,
                        mContactObserver);
        getRenderScript(this);
        getScriptIntrinsicBlur(this);
//        getGCMRegistered();

    }


    public RenderScript getRenderScript(Context context) {
        if (mRenderScript == null) {
            mRenderScript = RenderScript.create(context.getApplicationContext());
        }

        return mRenderScript;
    }

    public ScriptIntrinsicBlur getScriptIntrinsicBlur(Context context) {
        if (mBlurScript == null) {
            mBlurScript = ScriptIntrinsicBlur.create(getRenderScript(context), Element.U8_4(getRenderScript(context)));
        }
        return mBlurScript;
    }

    public static Prefs sharedPrefs() {
        return mPrefs;
    }

    public class ContactObserver extends ContentObserver {
        public ContactObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
           /* this.onChange(selfChange, null);*/
            LogUtils.i("AppVhortext", "ContactObserver: onChange");
            setSyncStatus("0");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            LogUtils.i("AppVhortext", "ContactObserver: onChange" + uri.getPath());
            setSyncStatus("0");
        }
    }


    private void initThinDownloadManager() {


        thinDownloadManager = new ThinDownloadManager();


    }

    public int getDownloadStatus(int downloadId) {
        return thinDownloadManager != null ? thinDownloadManager.query(downloadId) : 0;
    }

    public int cancelDownloadRequest(DataTextChat mDataFileChat) {
        if (thinDownloadManager != null) {
            return thinDownloadManager.cancel(mDataFileChat.getDownloadId());

        }
        return -1;
    }

    public int addDownloadRequest(final DataTextChat mDataFileChat) {
        Uri downloadUri = Uri.parse(mDataFileChat.getFileUrl());
        if (TextUtils.isEmpty(mDataFileChat.getFileUrl())) {
            return -1;
        }

        String fileName = mDataFileChat.getFileUrl().substring(mDataFileChat.getFileUrl().lastIndexOf('/') + 1, mDataFileChat.getFileUrl().length());


        File file = null;
        try {
            file = MediaUtils.getOutputMediaFileInPublicDirectory(mDataFileChat.getMessageType(),
                    getApplicationContext(), fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file == null) {
            return -1;
        }
        final Uri destinationUri = Uri.parse(file.getPath());
//        mDataFileChat.setDownloadStatus(ThinDownloadManager.STATUS_CONNECTING);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new com.thin.downloadmanager.DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int id) {
                        removeFromDownloadQueueMap(mDataFileChat);
                        mDataFileChat.setFilePath(destinationUri.getPath());
                        try {
                            int index = destinationUri.getPath().lastIndexOf('.') + 1;
                            String ext = destinationUri.getPath().substring(index).toLowerCase();
                            MediaUtils.refreshGalleryAppToShowTheFile(getApplicationContext(), destinationUri.getPath(), ext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new TableChat(getApplicationContext()).updateFileChatAfterDownloadComplete(mDataFileChat);
                        if (mBaseActivity != null) {
                            AppCompatActivity activity = mBaseActivity.get();
                            if (activity != null && activity instanceof ActivityChat) {
                                ((ActivityChat) activity).notifyChatListUI(mDataFileChat);
                            }
                        }


                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
//                        mDataFileChat.setFilePath("");
                        mDataFileChat.setDownloadStatus(DataTextChat.DownloadStatus.NOT_DOWNLOADED);
                        removeFromDownloadQueueMap(mDataFileChat);
                        if (mBaseActivity != null) {
                            Toast.makeText(mBaseActivity.get(), errorMessage, Toast.LENGTH_SHORT).show();
                            AppCompatActivity activity = mBaseActivity.get();
                            if (activity != null && activity instanceof ActivityChat) {
                                ((ActivityChat) activity).notifyChatListUI(mDataFileChat);
                            }
                        }


                    }

                    @Override
                    public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress) {
//                        mDataFileChat.setProgress(progress);
//                        if (mBaseActivity != null) {
//                            AppCompatActivity activity = mBaseActivity.get();
//                            if (activity != null && activity instanceof ActivityChat) {
//                                ((ActivityChat) activity).notifyChatListUI(mDataFileChat);
//                            }
//                        }

                    }
                });

        if (thinDownloadManager == null) {
            initThinDownloadManager();
        }
        int downloadId = thinDownloadManager.add(downloadRequest);
        mDataFileChat.setDownloadId(downloadId);
        addToDownloadQueueMap(mDataFileChat);
//        if (mBaseActivity instanceof ActivityChat) {
//            ((ActivityChat) mBaseActivity).notifyChatListUI(mDataFileChat);
//        }
        return downloadId;
    }


    private void initComponent() {
        applicationContext = AppVhortex.this;
        mPrefs = Prefs.getInstance(this);
        mResponseReceiver = new ResponseReceiver(null);
        mSessionHandler = new SessionHandler();
//        messengerBound = new Messenger(new HandlerBound());

    }

    private void initDB() {
        if (mDbHelper != null) {
            mDbHelper.getDB().close();
            mDbHelper = null;
        }
        mDbHelper = new DBHelper(AppVhortex.this);

        try {
            mDbHelper.copyFromDataPackgeToSdCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
//            mDbHelper.getWritableDatabase();
            mDbHelper.openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initImageLoader() {
        try {
            File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
                    "Vhortext");
            @SuppressWarnings("deprecation")
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showStubImage(0).showImageForEmptyUri(0)
                    .showImageOnFail(0).bitmapConfig(Bitmap.Config.RGB_565)
                    .cacheInMemory(false).cacheOnDisc(true).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    getApplicationContext())
                    .discCache(new UnlimitedDiskCache(cacheDir))
                    .defaultDisplayImageOptions(options).build();
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerActivity(AppCompatActivity mBaseActivity) {
        this.mBaseActivity = new WeakReference<AppCompatActivity>(mBaseActivity);
    }

    public void unregisterActivity() {
//        this.mBaseActivity = null;
    }


//    private String getCountryCode() {
//        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        String countryCode = tm.getNetworkCountryIso();
//        return countryCode;
//    }

    private void getCountryService() {
        Intent mIntent = new Intent(this, ServiceCountry.class);
        mIntent.putExtra(Constants.B_TYPE, mResponseReceiver);
        startService(mIntent);
    }

    public synchronized void getContactService(final Context mActivity) {
        Intent mIntent = new Intent(mActivity, ServiceContact.class);
        mIntent.putExtra(Constants.B_TYPE, mResponseReceiver);
        startService(mIntent);

    }

    public synchronized void sendContactBroadcast() {
        Intent mIntent = new Intent();
        mIntent.setAction(getResources().getString(R.string.ContactBroadcast));
        sendBroadcast(mIntent);
    }

//    public void checkUserSession() {
//        if(TextUtils.isEmpty(Prefs.getInstance(this).getUserId())){
//            return;
//        }
//        if (CommonMethods.isOnline(getApplicationContext())) {
//            sessionExecutorService = Executors.newFixedThreadPool(1);
//            sessionExecutorService.execute(new RunnableUserSession(getApplicationContext(),
//                    Prefs.getInstance(applicationContext).getUserId(), mSessionHandler));
//        }
//    }

    public void checkXmppConnection() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean isAvailable = false;
        isAvailable:
        {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (ServiceXmppConnection.class.getName().equals(service.service.getClassName())) {
                    isAvailable = true;
                    break isAvailable;
                } else {
                    isAvailable = false;
                }
            }
        }
        if (!isAvailable) {
            startService(new Intent(getApplicationContext(), ServiceXmppConnection.class));
        }
    }

    public void setSyncStatus(String value) {
        if (new TableSync(getApplicationContext()).setStatus(value)) {
            new TableTimeStamp(getApplicationContext()).setStatus(value);
//            getContactService(this);
        }
    }

    public void setUpCountryService(String countryCode) {
        this.countryCode = countryCode;
//        if (mBaseActivity != null) {
//            AppCompatActivity activity = mBaseActivity.get();
//            if (activity != null && activity instanceof ActivitySplash) {
//                getCountryService();
//            }
//        }
    }

    public void checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {

                    if (Build.VERSION.SDK_INT < 13) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        isSyncedFirst = false;
    }

    public String checkActivityDashAvailable() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean isAvailable = false;
        String topActivity = "";
        try {
            List<ActivityManager.RunningTaskInfo> runningTaskInfoList = manager.getRunningTasks(10);
            Iterator<ActivityManager.RunningTaskInfo> itr = runningTaskInfoList.iterator();
            while (itr.hasNext()) {
                ActivityManager.RunningTaskInfo runningTaskInfo = itr.next();
                int id = runningTaskInfo.id;
                CharSequence desc = runningTaskInfo.description;
                topActivity = runningTaskInfo.baseActivity.getShortClassName();
                int numOfActivities = runningTaskInfo.numActivities;
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            isAvailable = false;
        }


        return topActivity;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /* local upload queue for status*/
    public boolean isInUploadQueue(DataTextChat dataTextChat) {
        if (uploadQueueMap != null) {
            return uploadQueueMap.containsKey(dataTextChat.getMessageid());
        }
        return false;
    }

    public void addToUploadQueueMap(DataTextChat mDataImageChat) {
        if (uploadQueueMap == null) {
            uploadQueueMap = new HashMap<String, DataTextChat>();
        }
        uploadQueueMap.put(mDataImageChat.getMessageid(), mDataImageChat);
    }
    /* local download queue for status*/

    private void removeFromUploadQueueMap(DataTextChat mDataImageChat) {
        if (uploadQueueMap != null && uploadQueueMap.containsKey(mDataImageChat.getMessageid())) {
            uploadQueueMap.remove(mDataImageChat.getMessageid());
        }
    }

    public boolean isInDownloadQueue(DataTextChat dataTextChat) {
        if (downloadQueueMap != null) {
            return downloadQueueMap.containsKey(dataTextChat.getMessageid());
        }
        return false;
    }

    public void addToDownloadQueueMap(DataTextChat mDataImageChat) {
        if (downloadQueueMap == null) {
            downloadQueueMap = new HashMap<String, DataTextChat>();
        }
        downloadQueueMap.put(mDataImageChat.getMessageid(), mDataImageChat);
    }

    public void removeFromDownloadQueueMap(DataTextChat mDataImageChat) {
        if (downloadQueueMap != null && downloadQueueMap.containsKey(mDataImageChat.getMessageid())) {
            downloadQueueMap.remove(mDataImageChat.getMessageid());
        }
    }

    /***********
     * End
     ************/

    public void cancelFileUploadRequest(final DataTextChat mDataImageChat) {
        cancelPendingRequests(mDataImageChat.getMessageid());
        removeFromUploadQueueMap(mDataImageChat);

    }

    public void addFileUploadRequestToQueue(final DataTextChat mDataImageChat) {
        addToUploadQueueMap(mDataImageChat);

        LogUtils.i(TAG, "addFileUpload:" + mDataImageChat.getFilePath());
        File file = new File(mDataImageChat.getFilePath());
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        int index = file.getName().lastIndexOf('.') + 1;
        String ext = file.getName().substring(index).toLowerCase();
        String mimeType = "." + ext;
//        mimeType = mime.getMimeTypeFromExtension(ext);
        String fileType = "";
        if (mDataImageChat.getMessageType().equals(ConstantChat.IMAGE_TYPE)) {
            fileType = "1";
        } else if (mDataImageChat.getMessageType().equals(ConstantChat.VIDEO_TYPE)) {
            fileType = "2";
        }
        MultipartRequest multipartRequest = new MultipartRequest(WebContstants.UrlFileUpload,
                mDataImageChat.getFilePath(), mimeType, fileType,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject s) {
                        onFileUploadComplete(mDataImageChat, s);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        onFileUploadError(mDataImageChat, volleyError);

                    }
                });
//        multipartRequest.setProgressListener(new MultipartRequest.ProgressPercentListener() {
//            @Override
//            public void progressPercentage(int progressPercentage) {
//                onFileUploadProgress(mDataImageChat,progressPercentage);
//            }
//        });
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(multipartRequest, mDataImageChat.getMessageid());
    }

    private void onFileUploadError(final DataTextChat mDataImageChat, final VolleyError volleyError) {
        try {
            removeFromUploadQueueMap(mDataImageChat);
            Bundle nBundle = new Bundle();
            mDataImageChat.setUploadStatus(DataTextChat.UploadStatus.NOT_UPLOADED);

            new TableChat(getApplicationContext()).updateUploadStatus(mDataImageChat);

            nBundle.putSerializable(Constants.B_OBJ, mDataImageChat);
            nBundle.putSerializable(Constants.B_ERROR_OBJ, volleyError);
            nBundle.putString(Constants.KEY_FILE_UPLOAD_STATUS, Constants.UPLOAD_STATUS_FAILED_NETWORK_ERROR);

            Intent intent = new Intent(Constants.ACTION_FILE_UPLOAD_COMPLETE);
            intent.putExtras(nBundle);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onFileUploadProgress(final DataTextChat mDataImageChat, int progressPercentage) {
        removeFromUploadQueueMap(mDataImageChat);
        Bundle nBundle = new Bundle();
        mDataImageChat.setUploadStatus(DataTextChat.UploadStatus.UPLOADING);
        mDataImageChat.setProgress(progressPercentage);
        nBundle.putSerializable(Constants.B_OBJ, mDataImageChat);
        nBundle.putString(Constants.KEY_FILE_UPLOAD_STATUS, Constants.UPLOAD_STATUS_UPLOADING);
        nBundle.putInt(Constants.KEY_FILE_UPLOAD_PROGRESS, progressPercentage);

        Intent intent = new Intent(Constants.ACTION_FILE_UPLOAD_PROGRESS);
        intent.putExtras(nBundle);
        sendBroadcast(intent);
    }

    private void onFileUploadComplete(final DataTextChat mDataImageChat, final JSONObject s) {
        removeFromUploadQueueMap(mDataImageChat);
        String fileUrl = s.optString("url", "");
        String responseCode = s.optString("responseCode", "");
        String responseDetails = s.optString("responseDetails", "");
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setResponseCode(Integer.parseInt(responseCode));
        baseResponse.setResponseDetails(responseDetails);
        mDataImageChat.setFileUrl(fileUrl);
        mDataImageChat.setUploadStatus(DataTextChat.UploadStatus.UPLOADED);

        new TableChat(getApplicationContext()).updateFileChatAfterUploadComplete(mDataImageChat);


        Bundle nBundle = new Bundle();
        LogUtils.i(TAG, "fileUpload: " + s.toString());
        nBundle.putSerializable(Constants.B_OBJ, mDataImageChat);
        Intent intent = new Intent(Constants.ACTION_FILE_UPLOAD_COMPLETE);

        if ("200".equals(responseCode)) {
            nBundle.putString(Constants.KEY_FILE_UPLOAD_STATUS, Constants.UPLOAD_STATUS_SUCCESS);
        } else {
            nBundle.putString(Constants.KEY_FILE_UPLOAD_STATUS, Constants.UPLOAD_STATUS_FAILED_SERVER_ERROR);
            nBundle.putSerializable(Constants.B_RESPONSE_OBJ, baseResponse);
        }

        intent.putExtras(nBundle);
        sendBroadcast(intent);
    }


    public void onFileDownloadForChat(final DataTextChat mDataTextChat) {

        mImageLoader.loadImage(mDataTextChat.getFileUrl(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

                LogUtils.d("AppVhortext", "DownloadImage:" + imageUri + "onLoadingStarted");
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                LogUtils.d("AppVhortext", "DownloadImage:" + imageUri + "onLoadingFailed: " + failReason.toString());
                removeFromDownloadQueueMap(mDataTextChat);
                if (mBaseActivity != null) {
                    AppCompatActivity activity = mBaseActivity.get();
                    if (activity != null && activity instanceof ActivityChat) {
                        ((ActivityChat) activity).notifyChatListUI(mDataTextChat);
                    }
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                LogUtils.d("AppVhortext", "DownloadImage:" + imageUri + "onLoadingComplete");
                removeFromDownloadQueueMap(mDataTextChat);

                File file = com.nostra13.universalimageloader.utils.DiskCacheUtils.findInCache(imageUri,
                        mImageLoader.getDiscCache());
                if (file != null && file.exists()) {

                    File targetFile = null;
                    try {
                        targetFile = MediaUtils.getOutputMediaFileInPublicDirectory(mDataTextChat.getMessageType(), getApplicationContext(), file.getName() + ".jpg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (targetFile != null) {
                        file.renameTo(targetFile);
                        if (targetFile.exists()) {
                            mDataTextChat.setFilePath(targetFile.getAbsolutePath());
                            file.delete();
                        } else {
                            mDataTextChat.setFilePath(file.getAbsolutePath());
                        }
                    } else {
                        mDataTextChat.setFilePath(file.getAbsolutePath());
                    }


//                    if (mDataTextChat.getIsMasked().equalsIgnoreCase("1")) {
                    try {
                        Thread imageHandlerThread = new Thread(
                                new ProcessDownloadedImageRunnable(getApplicationContext(), mDataTextChat, imageBlurHandler));
                        imageHandlerThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    }else{
//                        try {
//                            int index = mDataTextChat.getFilePath().lastIndexOf('.') + 1;
//                            String ext = mDataTextChat.getFilePath().substring(index).toLowerCase();
//                            MediaUtils.refreshGalleryAppToShowTheFile(getApplicationContext(), mDataTextChat.getFilePath(), ext);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        new TableChat(getApplicationContext()).updateFileChatAfterDownloadComplete(mDataTextChat);
//                        if (mBaseActivity != null) {
//                            AppCompatActivity activity = mBaseActivity.get();
//                            if (activity != null && activity instanceof ActivityChat) {
//                                ((ActivityChat) activity).notifyChatListUI(mDataTextChat);
//                            }
//                        }
//                    }

//                    try {
//                        if (mDataTextChat.getIsMasked().equalsIgnoreCase("1")) {
//                            //save masked image
//                            Bitmap blurredBitmap = maskImageAndSaveMaskedImageBitmap(mDataTextChat.getFilePath());
//                            Bitmap fixRotationBitmap = MediaUtils.fixImageRotation(blurredBitmap,mDataTextChat.getFilePath());
//                            if (fixRotationBitmap != null) {
//                                String[] filePathSplit = mDataTextChat.getFilePath().split("/");
//                                String blurredFileName = "";
//                                if (filePathSplit != null && filePathSplit.length > 0) {
//                                    blurredFileName = filePathSplit[filePathSplit.length - 1];
//                                    File blurredBitmapFile = MediaUtils.
//                                            saveImageStringToPublicFile(fixRotationBitmap, ".blurred_" + blurredFileName, getApplicationContext());
//                                    if (blurredBitmapFile != null)
//                                        mDataTextChat.setBlurredImagePath(blurredBitmapFile.getPath());
//                                }
//
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }catch (OutOfMemoryError e) {
//                        e.printStackTrace();
//                    }
                } else {
                    if (mBaseActivity != null) {
                        AppCompatActivity activity = mBaseActivity.get();
                        if (activity != null && activity instanceof ActivityChat) {
                            ((ActivityChat) activity).notifyChatListUI(mDataTextChat);
                        }
                    }
                }

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                LogUtils.d("AppVhortext", "DownloadImage:" + imageUri + "onLoadingComplete");
                removeFromDownloadQueueMap(mDataTextChat);
                if (mBaseActivity != null) {
                    AppCompatActivity activity = mBaseActivity.get();
                    if (activity != null && activity instanceof ActivityChat) {
                        ((ActivityChat) activity).notifyChatListUI(mDataTextChat);
                    }
                }
            }
        });
    }

    private final Handler imageBlurHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            DataTextChat mDataTextChat = (DataTextChat) bundle.getSerializable(Constants.B_RESULT);

            try {
                int index = mDataTextChat.getFilePath().lastIndexOf('.') + 1;
                String ext = mDataTextChat.getFilePath().substring(index).toLowerCase();
                MediaUtils.refreshGalleryAppToShowTheFile(getApplicationContext(), mDataTextChat.getFilePath(), ext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TableChat(getApplicationContext()).updateFileChatAfterDownloadComplete(mDataTextChat);
            if (mBaseActivity != null) {
                AppCompatActivity activity = mBaseActivity.get();
                if (activity != null && activity instanceof ActivityChat) {
                    ((ActivityChat) activity).notifyChatListUI(mDataTextChat);
                }
            }
        }
    };

    private Bitmap maskImageAndSaveMaskedImageBitmap(String actualPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap data = BitmapFactory.decodeFile(actualPath, options);
        return transformBlur(data);
    }

    private Bitmap transformBlur(Bitmap bitmap) {
        bitmap = cropBitmapWidthToMultipleOfFour(bitmap);
        Bitmap argbBitmap = convertBitmap(bitmap, Bitmap.Config.ARGB_8888);

        Bitmap blurredBitmap = Bitmap.createBitmap(argbBitmap.getWidth(), argbBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Initialize RenderScript and the script to be used
        RenderScript renderScript = RenderScript.create(this);
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur
                .create(renderScript, Element.U8_4(renderScript));
        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(renderScript, argbBitmap);
        Allocation output = Allocation.createFromBitmap(renderScript, blurredBitmap);

        script.setInput(input);
        script.setRadius(CompressImageUtils.BLUR_RADIUS);
        script.forEach(output);
        output.copyTo(blurredBitmap);

        renderScript.destroy();
        argbBitmap.recycle();
        return blurredBitmap;
    }

    private static Bitmap cropBitmapWidthToMultipleOfFour(Bitmap bitmap) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (bitmapWidth % 4 != 0) {
            // This is the place to actually crop the bitmap,
            // but I don't have the necessary method in this demo project

            // bitmap = BitmapUtils.resize(bitmap, bitmapWidth - (bitmapWidth % 4), bitmap.getHeight(),
            //         ScaleType.CENTER);

            try {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth - (bitmapWidth % 4), bitmapHeight - (bitmapHeight % 4), true);
            } catch (Exception e) {
                return bitmap;
            } catch (OutOfMemoryError e) {
                return bitmap;
            }
        }
        return bitmap;
    }

    private static Bitmap convertBitmap(Bitmap bitmap, Bitmap.Config config) {
        if (bitmap.getConfig() == config) {
            return bitmap;
        } else {
            Bitmap argbBitmap;
            argbBitmap = bitmap.copy(config, false);
            bitmap.recycle();
            if (argbBitmap == null) {
                throw new UnsupportedOperationException(
                        "Couldn't convert bitmap from config " + bitmap.getConfig() + " to "
                                + config);
            }
            return argbBitmap;
        }
    }

    public void deleteUserData() {

        new TableUserStatus(this).delete();
        new TableUserInfo(this).delete();
        new TableTimeStamp(this).delete();
        new TableSync(this).delete();
        new TableGroup(this).deleteGroup();
        new TableGroup(this).deleteMember();
        new TableContact(this).delete();
        new TableChat(this).deleteAll();
        new TableAttchmentDetails(this).delete();

        Prefs.getInstance(this).clearPreferences();
        Prefs.getInstance(this).setFirstTimeTutorialShown();
    }


    @SuppressLint("ParcelCreator")
    public class ResponseReceiver extends ResultReceiver {

        public ResponseReceiver(Handler handler) {
            super(null);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData.containsKey("session_expired")) {
                boolean expired = resultData.getBoolean("session_expired");
                LogUtils.i("AppVhortext", "ResponseReceiver: onReceiveResult:sessionExpired:" + expired);
                if (expired) {
                    //check if already deleted account
                    if (!TextUtils.isEmpty(Prefs.getInstance(getApplicationContext()).getUserId())) {
                        clearSession();
                    }

                }
            }

            if (TextUtils.isEmpty(Prefs.getInstance(getApplicationContext()).getUserId())) {
                //user not logged in
                return;
            }
//            if (resultCode == Constants.ContactSynced) {
//                getContactService(getApplicationContext());
//            }

            if (resultData.getBoolean(Constants.B_NEED_UPDATED)) {

                Intent intent = new Intent(getApplicationContext(), ServiceXmppConnection.class);
                intent.setAction(Constants.B_NEED_UPDATED);
                startService(intent);
            }

            if (ApplicationLifecycleManager.isAppVisible()) {
                if (resultCode == Constants.ContactSynced) {
                    LogUtils.d("AppVhortext", "Call getContactService");
                    getContactService(getApplicationContext());
                }
            }

            if (mBaseActivity != null && mBaseActivity.get() != null) {

                AppCompatActivity activity = mBaseActivity.get();
                if (resultCode == Constants.ContactSynced) {
//                    getContactService(getApplicationContext());
                    if (activity instanceof ActivityDash && resultData.containsKey(Constants.B_RESULT)) {
                        //if anything any data in contact sync and group contact sync then update adapetr
                        if (resultData.getBoolean(Constants.B_NEED_UPDATED)) {
                            ((ActivityDash) activity).notifyAdapter();

                        }

                        isSyncedFirst = true;

//                        sendContactBroadcast();
                    } else {
//                        sendContactBroadcast();
                        if (activity instanceof BaseActivity) {
                            ((BaseActivity) activity).notifySyncComplete();
                        }
                    }

                } else if (activity instanceof ActivitySelectCountry && resultData != null
                        && resultData.containsKey(Constants.B_RESULT)) {
                    ArrayList<DataCountry> mListCountry = (ArrayList<DataCountry>) resultData.getSerializable(Constants.B_RESULT);
                    ((ActivitySelectCountry) activity).onCountryListFetchSuccess(mListCountry);
                } else if (activity instanceof ActivitySplash) {
                    DataCountry mDataCountry = new TableCountry(activity).getCountryCode(countryCode);
                    ((ActivitySplash) activity).gotoRegistration(mDataCountry);
                }

            }
        }
    }

    public class SessionHandler extends Handler {

        public SessionHandler() {

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogUtils.d("AppVhortext: SessionHandler");
            if (TextUtils.isEmpty(Prefs.getInstance(getApplicationContext()).getUserId())) {
                LogUtils.d("AppVhortext: UserId blank: return");
                return;
            }
            int what = msg.what;
            switch (what) {
                case 1234:
                    Bundle mBundle = msg.getData();
                    boolean status = mBundle.getBoolean(Constants.B_ID);
                    LogUtils.d("AppVhortext: SessionHandler: status:" + status);
                    if (status) {
//                        if (mBaseActivity != null)
//                            checkUserSession();
                    } else {
                        //Clear Db And preference
                        if (mBaseActivity != null) {

                            clearSession();
                        }
                    }
                    break;
                case 1:

                    break;
            }
        }
    }

    public void clearSession() {
        stopService(new Intent(this, ServiceXmppConnection.class));
        stopService(new Intent(this, ServiceContact.class));

        Intent mIntent = new Intent(this, ActivitySplash.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mIntent);
        new TableUserInfo(this).delete();
        new TableUserStatus(this).delete();
        new TableSync(this).delete();
        new TableTimeStamp(this).delete();
        new TableGroup(this).deleteGroup();
        new TableGroup(this).deleteMember();
        Prefs.getInstance(this).clearPreferences();

        deleteUserData();
    }

    public String getAppVersionName() {
        String myVersionName = "";
        try {
            myVersionName = getPackageManager().getPackageInfo(getPackageName(),
                    0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return myVersionName;
    }
}