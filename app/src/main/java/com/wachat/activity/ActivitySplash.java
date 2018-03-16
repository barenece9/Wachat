package com.wachat.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.wachat.GcmOnCompleteListener;
import com.wachat.GcmUtilities;
import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncVersionCheckApi;
import com.wachat.data.DataCountry;
import com.wachat.data.DataProfile;
import com.wachat.data.DataVersionCheck;
import com.wachat.interfaces.WebServiceCallBack;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

public class ActivitySplash extends BaseActivity {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivitySplash.class);
    private Handler mNavigate;
    private AsyncVersionCheckApi versionCheckApi;
    private View progressBarCircularIndetermininate;
//    private ArrayList<DataLanguages> mArrayList = new ArrayList<DataLanguages>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//        if (!TextUtils.isEmpty(Prefs.getInstance(this).getChatId())) {
////            Prefs.getInstance(this).versionCheckingSkip
//            Intent mIntent = new Intent(ActivitySplash.this, ActivityDash.class);
//            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            overridePendingTransition(0, 0);
//            startActivity(mIntent);
//            finish();
//            return;
//        }
        setContentView(R.layout.activity_splash);
        progressBarCircularIndetermininate = findViewById(R.id.progressBarCircularIndetermininate);
//        prefs = Prefs.getInstance(mContext);
        if (Prefs.getInstance(this).getChatId().equals("")
                || Prefs.getInstance(this).getChatId().length() < 0) {
            setUpForCountryCodeFetch();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mNavigate = new Handler();
//        goToNextStep();
        callVersionCheckApi();
//        if (Prefs.getInstance(this).getChatId().equals("") || Prefs.getInstance(this).getChatId().length() < 0) {
//            if (CommonMethods.isOnline(this)) {
//                setUpForCountryCodeFetch();
//                getLanguageFromAPI();
//            } else {
//                ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet),
//                        ToastType.FAILURE_ALERT);
//            }
//        } else {
//            gotoDash();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if (versionCheckApi != null) {
                versionCheckApi.cancel(true);
                versionCheckApi = null;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mNavigate.removeCallbacks(goToDashRunnable);
            mNavigate = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Runnable goToDashRunnable = new Runnable() {
        @Override
        public void run() {
            if (mNavigate != null) {

//                    if(prefs.getChatId().equals("") || prefs.getChatId().trim().length()<=0) {
//                        Intent mIntent = new Intent(ActivitySplash.this, ActivityRegistration.class);
//                        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        overridePendingTransition(0, 0);
//                        startActivity(mIntent);
//                        ActivitySplash.this.finish();
//                    }else{
                // Registered user
                Intent mIntent = new Intent(ActivitySplash.this, ActivityDash.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0, 0);
//                    ((AppVhortex) getApplication().getApplicationContext()).setSyncStatus("0");
                ((AppVhortex) getApplicationContext()).getContactService(ActivitySplash.this);
                startActivity(mIntent);
                ActivitySplash.this.finish();
//                    }
            }
        }
    };

    private void gotoDash() {
        progressBarCircularIndetermininate.setVisibility(View.GONE);
        mNavigate.postDelayed(goToDashRunnable, Constants.DELAY_SPLASH);
    }


    private void setUpForCountryCodeFetch() {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
//            String locale = getResources().getConfiguration().locale.getCountry();
//            TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
            String countryCodeValue = "";
            boolean isEmulator = Build.HARDWARE.contains("vbox86");
            if (isEmulator) {
                countryCodeValue = "IN";
            } else {
                countryCodeValue = getUserCountry(this);
            }


            int countryCode = phoneUtil.getCountryCodeForRegion(countryCodeValue.toUpperCase());
            LogUtils.i(LOG_TAG, "countrycode: " + String.valueOf(countryCode));

            ((AppVhortex) getApplicationContext()).setUpCountryService(String.valueOf(countryCode));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     *
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    public static String getUserCountry(Context context) {
        String countryCode = "";
        try {

            final TelephonyManager tm = (TelephonyManager) context.
                    getSystemService(Context.TELEPHONY_SERVICE);
            countryCode = tm.getSimCountryIso();
            if (countryCode != null && countryCode.length() == 2) { // SIM country code is available
                return countryCode.toUpperCase();
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
                // device is not 3G (would be unreliable)
                countryCode = tm.getNetworkCountryIso();
                if (countryCode != null && countryCode.length() == 2) {
                    // network country code is available
                    return countryCode.toUpperCase();
                }
            }

            if (TextUtils.isEmpty(countryCode)) {
                countryCode = context.getResources().getConfiguration().locale.getCountry();
            }
        } catch (Exception e) {
        }
        return countryCode;
    }

    private void callVersionCheckApi() {
        if (!CommonMethods.isOnline(this)) {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet),
                    ToastType.FAILURE_ALERT);
            progressBarCircularIndetermininate.setVisibility(View.GONE);
            return;
        }

        progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
        versionCheckApi = new AsyncVersionCheckApi(new WebServiceCallBack() {
            @Override
            public void onSuccess(Object result) {
                progressBarCircularIndetermininate.setVisibility(View.GONE);
                DataVersionCheck dataVersionCheck = (DataVersionCheck) result;
                LogUtils.d(LOG_TAG, "AsyncVersionCheckApi:onSuccess " + dataVersionCheck.toString());
                checkAndValidateCurrentVersion(dataVersionCheck);
                versionCheckApi = null;
            }

            @Override
            public void onFailure(Object result) {
                DataVersionCheck dataVersionCheck = (DataVersionCheck) result;
                progressBarCircularIndetermininate.setVisibility(View.GONE);
                LogUtils.d(LOG_TAG, "AsyncVersionCheckApi:onFailure " + dataVersionCheck.toString());
                String alertText = TextUtils.isEmpty(dataVersionCheck.responseDetails) ?
                        getResources().getString(
                                R.string.alert_failure_server_connection_failed_) :
                        dataVersionCheck.responseDetails;
//                ToastUtils.showAlertToast(ActivitySplash.this,
//                        alertText,
//                        ToastType.FAILURE_ALERT);
                showRetryAlert(alertText);
                versionCheckApi = null;
            }

            @Override
            public void onFailure(Throwable e) {
                progressBarCircularIndetermininate.setVisibility(View.GONE);
                String alertText = CommonMethods.
                        getAlertMessageFromException(ActivitySplash.this, e);
                alertText = TextUtils.isEmpty(alertText) ?
                        getResources().getString(
                                R.string.alert_failure_server_connection_failed_) :
                        alertText;
//                ToastUtils.showAlertToast(ActivitySplash.this, alertText
//                        ,
//                        ToastType.FAILURE_ALERT);

                showRetryAlert(alertText);
                versionCheckApi = null;

            }
        });

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            versionCheckApi.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            versionCheckApi.execute();
        }
    }

    private void checkAndValidateCurrentVersion(DataVersionCheck dataVersionCheck) {
        String myVersionName = AppVhortex.getInstance().getAppVersionName();

        if (!TextUtils.isEmpty(myVersionName)) {
            if (TextUtils.isEmpty(dataVersionCheck.version)) {
                goToNextStep();
            } else {
                if (myVersionName.equals(dataVersionCheck.version)) {
                    //go to next step
                    goToNextStep();
                } else {
                    //Assuming we will always use version names in valid double
                    // (i.e 1.0 format not 1.0.0 format).
                    double myVersion = 0;
                    try {
                        myVersion = Double.parseDouble(myVersionName);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    double latestPlayStoreVersion = 0;
                    try {
                        latestPlayStoreVersion = Double.parseDouble(dataVersionCheck.version);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    LogUtils.d(LOG_TAG, "myVersion:" + myVersion + " latestPlayStoreVersion: " + latestPlayStoreVersion);
                    if (latestPlayStoreVersion > myVersion) {

                        showVersionUpdateAvailableDialog(dataVersionCheck);
                    } else {
                        //go to next step
                        goToNextStep();
                    }

                }
            }
        } else {
            //Go to next step
            goToNextStep();
        }
    }

    private void showVersionUpdateAvailableDialog(DataVersionCheck dataVersionCheck) {

        AlertDialog.Builder alertBuilder;

        alertBuilder = new AlertDialog.Builder(this);

        if (dataVersionCheck.mandetory.equalsIgnoreCase("yes")) {
            // must update
            alertBuilder
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openPlayStore();
                        }
                    });
        } else {
            // can skip
            alertBuilder
                    .setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openPlayStore();
                        }
                    });
            alertBuilder.setNegativeButton("Remind me later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goToNextStep();
                }
            });
        }

        // TextView titleTextView = new TextView(context);
        // // You Can Customise your Title here
        // titleTextView.setText(context.getResources().getString(
        // R.string.app_name));
        // // title.setBackgroundColor(Color.DKGRAY);
        // titleTextView.setPadding(20, 20, 20, 20);
        // titleTextView.setGravity(Gravity.CENTER);
        // titleTextView.setTextColor(context.getResources().getColor(
        // R.color.app_blue));
        // titleTextView.setTextSize(20);
        // alertBuilder.setCustomTitle(titleTextView);
        alertBuilder.setTitle(getResources().getString(
                R.string.app_name));

        // TextView contentTextView = new TextView(context);
        // // You Can Customise your Title here
        // contentTextView.setText(context.getResources().getString(
        // R.string.alert_dialog_new_version_available));
        // // title.setBackgroundColor(Color.DKGRAY);
        // contentTextView.setPadding(15, 20, 15, 20);
        // contentTextView.setGravity(Gravity.LEFT);
        // contentTextView.setTextColor(Color.BLACK);
        // contentTextView.setTextSize(18);
        // alertBuilder.setView(contentTextView);
        alertBuilder.setMessage("A new version of Vhortext is available");
        // alertBuilder.
        AlertDialog dialog = alertBuilder.create();
        if (dataVersionCheck.mandetory.equalsIgnoreCase("yes")) {
            dialog.setCancelable(false);
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                goToNextStep();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void openPlayStore() {
        try {
            final String my_package_name = "com.wachat";
            String url = "";
            // https://play.google.com/store/apps/details?id=in.mygov.mobile
            try {
                // Check whether Google Play store is installed or not:
                getPackageManager().getPackageInfo("com.android.vending",
                        0);

                url = "market://details?id=" + my_package_name;
            } catch (final Exception e) {
                url = "https://play.google.com/store/apps/details?id="
                        + my_package_name;
            }

            // Open the app page in Google Play store:
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void goToNextStep() {
        getGCMRegistered();

    }

    private void getGCMRegistered() {
        progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
        GcmUtilities gcmUtilities = new GcmUtilities(this, new GcmOnCompleteListener() {
            @Override
            public void onComplete() {
                Log.i("ActivitySplash", "GcmUtilities:onComplete");
                if (Prefs.getInstance(ActivitySplash.this).getChatId().equals("")
                        || Prefs.getInstance(ActivitySplash.this).getChatId().length() < 0) {
                    gotoRegistration(null);
                } else {
                    gotoDash();
                }
            }

            @Override
            public void onError(String message) {
                Log.i("ActivitySplash", "GcmUtilities:onError");
                showGcmRetryAlert(getString(
                        R.string.alert_failure_server_connection_failed_));
            }

            @Override
            public void onCancel() {
                finish();
                Log.i("ActivitySplash", "GcmUtilities:onCancel");
            }
        });
    }

    public void showGcmRetryAlert(String alertText) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
            builder.setMessage(alertText);
            builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (TextUtils.isEmpty(Prefs.getInstance(ActivitySplash.this).getGcmId())) {
                        getGCMRegistered();
                    } else {
                        if (Prefs.getInstance(ActivitySplash.this).getChatId().equals("")
                                || Prefs.getInstance(ActivitySplash.this).getChatId().length() < 0) {
                            gotoRegistration(null);
                        } else {
                            gotoDash();
                        }
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setCancelable(false);

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String result) {

    }

    @Override
    protected void CommonMenuClcik() {

    }

//    @Override
//    public void onResponseObject(Object mObject) {
//        if (mObject instanceof String) {
//            //((AppVhortex) getApplication().getApplicationContext()).setUpCountryService(((String) mObject));
//
//        }
//    }
//
//    @Override
//    public void onResponseList(ArrayList<?> mList) {
//
//        mArrayList = (ArrayList<DataLanguages>) mList;
//        new TableLanguage(this).insertAllLang(mArrayList);
//        // mAdapterSelectLanguage.refreshList(mArrayList);
//        gotoRegistration(null);
//
//    }
//
//    @Override
//    public void onResponseFaliure(String responseText) {
//        showRetryAlert();
//    }

    public void gotoRegistration(DataCountry mDataCountry) {
        progressBarCircularIndetermininate.setVisibility(View.GONE);

        if (TextUtils.isEmpty(Prefs.getInstance(this).getChatId())) {
            TableUserInfo mTableUserInfo = new TableUserInfo(this);
            DataProfile mDataProfile = mTableUserInfo.getUser();
            if (mDataProfile!=null&&!TextUtils.isEmpty(mDataProfile.getUserId())
                    && !TextUtils.isEmpty(mDataProfile.getVerificationCode())) {

                LogUtils.d(LOG_TAG,"Previous OTP forund.. going to Authentication");
                Bundle mBundle = new Bundle();

                mBundle.putInt(Constants.B_ID, Integer.parseInt(mDataProfile.getVerificationCode()));
                mBundle.putString(Constants.B_TYPE, mDataProfile.getUserId());
                mBundle.putString(Constants.B_RESULT, mDataProfile.getPhoneNo());
                mBundle.putString(Constants.B_CODE, mDataProfile.getCountryCode());
                Intent mIntent = new Intent(this, ActivityAuthentication.class);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
                finish();
                return;
            }

        }

        if (mDataCountry != null) {
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(Constants.B_RESULT, (mDataCountry));
            Intent mIntent = new Intent(ActivitySplash.this, ActivityRegistration.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mIntent.putExtras(mBundle);
            overridePendingTransition(0, 0);
            startActivity(mIntent);
            ActivitySplash.this.finish();
        } else {
            Intent mIntent = new Intent(ActivitySplash.this, ActivityRegistration.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            overridePendingTransition(0, 0);
            startActivity(mIntent);
            ActivitySplash.this.finish();
        }
    }

    public void showRetryAlert(String alertText) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
            builder.setMessage(alertText);
            builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Prefs.getInstance(ActivitySplash.this).getChatId().equals("")
                            || Prefs.getInstance(ActivitySplash.this).getChatId().length() < 0) {
                        callVersionCheckApi();
                    } else {
                        gotoDash();
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setCancelable(false);

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}



