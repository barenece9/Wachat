package com.wachat.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncRegisterChat;
import com.wachat.async.AsyncResendOTP;
import com.wachat.async.AsyncValidateUser;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.BaseResponse;
import com.wachat.data.DataResendOTP;
import com.wachat.data.DataTextChat;
import com.wachat.data.DataValidateUser;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.services.ConstantXMPP;
import com.wachat.services.ServiceXmppConnection;
import com.wachat.storage.ConstantDB;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 03-09-2015.
 */
public class ActivityAuthentication extends BaseActivity implements TextWatcher, InterfaceResponseCallback {

    public Toolbar appBar;
    private TextView tv_auth, tv_code, tv_reEnterPh, tv_resendOTP;
    private String phoneNo = "";
    private String userId = "";
    private EditText et1, et2, et3, et4, et5, et6;
    private int verificationCode = 0;
//    private Prefs mPrefs;
    AsyncValidateUser mAsyncValidateUser;
    AsyncRegisterChat mAsyncRegisterChat;
    AsyncResendOTP mAsyncResendOTP;

    private int i = 0;
    android.os.Handler mHandler;
    private ProgressBarCircularIndeterminate mProgressBarCircularIndeterminate;
    private CustomRunnable mCustomRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
//        mPrefs = Prefs.getInstance(mActivity);
        getBundleValue();
        initComponent();
        iniViews();
//        getWindow().getDecorView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                populateView();
//            }
//        }, 1000);
    }

    private void getBundleValue() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            phoneNo = mBundle.getString(Constants.B_RESULT);
            verificationCode = mBundle.getInt(Constants.B_ID);
            userId = mBundle.getString(Constants.B_TYPE);
        }
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);

        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.authentication));

    }


    private void iniViews() {

        tv_auth = (TextView) findViewById(R.id.tv_auth);
        mProgressBarCircularIndeterminate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
        // tv_code = (TextView) findViewById(R.id.tv_code);
        tv_reEnterPh = (TextView) findViewById(R.id.tv_reEnterPh);
        tv_resendOTP = (TextView) findViewById(R.id.tv_resendOTP);
        tv_resendOTP.setOnClickListener(this);

        //code et
        et1 = (EditText) findViewById(R.id.et1);

        et2 = (EditText) findViewById(R.id.et2);
        et3 = (EditText) findViewById(R.id.et3);
        et4 = (EditText) findViewById(R.id.et4);
        et5 = (EditText) findViewById(R.id.et5);
        et6 = (EditText) findViewById(R.id.et6);

        clickListener();
        tv_reEnterPh.setOnClickListener(this);
        setValues();
        //  showSoftKeyboard(et1);
    }


    private void populateView() {

//        mHandler = new android.os.Handler();
//        mCustomRunnable = new CustomRunnable();
//        mHandler.postDelayed(mCustomRunnable, 500);
    }

    private void clickListener() {
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et1.getText().length() == 1)
                    et2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        et1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR
//                        || event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                        || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//                    return false;
//                }
//                if (et1.getText().length() == 1)
//
//                    et2.requestFocus();
//                return false;
//            }
//        });
//        et1.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR
//                        || event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                        || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//                    return false;
//                }
//                if (et1.getText().length() == 1)
//
//                    et2.requestFocus();
//                return false;
//            }
//        });

        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et2.getText().length() == 1)
                    et3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        et2.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR
//                        || event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                        || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//                    return false;
//                }
//                if (et2.getText().length() == 1)
//                    et3.requestFocus();
//                return false;
//            }
//        });

        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et3.getText().length() == 1)
                    et4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        et3.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR
//                        || event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                        || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//                    return false;
//                }
//                if (et3.getText().length() == 1)
//                    et4.requestFocus();
//                return false;
//            }
//
//        });
        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et4.getText().length() == 1)
                    et5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        et4.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR
//                        || event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                        || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//                    return false;
//                }
//                if (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR
//                        || event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                        || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//                    return false;
//                }
//                if (et4.getText().length() == 1)
//                    et5.requestFocus();
//                return false;
//            }
//        });
        et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et5.getText().length() == 1)
                    et6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        et5.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR
//                        || event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                        || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//                    if(et5.getText().toString().length()==0)
//                        et4.requestFocus();
//                    return false;
//                }
//
//                return false;
//            }
//        });
//
//        et6.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR
//                        || event.getKeyCode() == KeyEvent.KEYCODE_BACK
//                        || event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
//                    if(et6.getText().toString().length()==0)
//                        et5.requestFocus();
//                    return false;
//                }
//
//
//                return false;
//            }
//        });
        et6.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftKeyboard(ActivityAuthentication.this, et6);
                    et6.setSelection(et6.getText().length());
                    if (checkvalidCode()) {

                        //   startActivity(new Intent(mActivity, ActivityDash.class));
                        if (CommonMethods.isOnline(ActivityAuthentication.this)) {
                            validateUser();
                        } else {
                            ToastUtils.showAlertToast(ActivityAuthentication.this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                        }
                        //  finish();
                    } else {
                        ToastUtils.showAlertToast(ActivityAuthentication.this, "Invalid Verification Code", ToastType.FAILURE_ALERT);
//                        et1.setText("");
//                        et2.setText("");
//                        et3.setText("");
//                        et4.setText("");
//                        et5.setText("");
//                        et6.setText("");
                        et1.setFocusable(true);
                    }
                    return true;
                }
                return true;
            }

        });


        //  et6.addTextChangedListener(this);
    }

    private void setValues() {
        tv_auth.setText(getResources().getString(R.string.authentication_text) + "\n" + "+" + phoneNo);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_reEnterPh:


                resetVerificationCodeToUserInfoTable("");
                Intent mIntent = new Intent(this, ActivityRegistration.class);
                startActivity(mIntent);
                finish();
                break;

            case R.id.tv_resendOTP:
                if (mCustomRunnable != null) {
                    mHandler.removeCallbacks(mCustomRunnable);
                }
                CommonMethods.hideSoftKeyboard(this);
                if (CommonMethods.isOnline(this)) {
                    resendOTP();
                } else {
                    ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                }
                break;
        }
    }

    private void resetVerificationCodeToUserInfoTable(String verificationCode) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.VerificationCode, verificationCode);
//        TableUserInfo mTableUserInfo = new TableUserInfo(mActivity);
        mTableUserInfo.editProfile(mContentValues, userId);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (et6.getText().toString().trim().length() == 1) {
            if (checkvalidCode()) {

                //   startActivity(new Intent(mActivity, ActivityDash.class));
                if (CommonMethods.isOnline(this)) {
                    validateUser();
                } else {
                    ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                }
                //  finish();
            } else {
                ToastUtils.showAlertToast(this, "Invalid Verification Code", ToastType.FAILURE_ALERT);
//                et1.setText("");
//                et2.setText("");
//                et3.setText("");
//                et4.setText("");
//                et5.setText("");
//                et6.setText("");
                et1.setFocusable(true);
            }

        }
    }

    private void validateUser() {

        mAsyncValidateUser = new AsyncValidateUser(mPrefs.getGcmId(),CommonMethods.getDeviceUniqueId(this), userId,
                String.valueOf(verificationCode), mPrefs.getAccountParing(), this);
        mProgressBarCircularIndeterminate.setVisibility(View.VISIBLE);
        if (CommonMethods.checkBuildVersionAsyncTask()) {

            mAsyncValidateUser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncValidateUser.execute();
        }
    }

    private void resendOTP() {
        mAsyncResendOTP = new AsyncResendOTP(userId, this);
        mProgressBarCircularIndeterminate.setVisibility(View.VISIBLE);
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncResendOTP.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncResendOTP.execute();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (et6.getText().toString().trim().length() == 1) {
            if (checkvalidCode()) {
                // startActivity(new Intent(mActivity, ActivityDash.class));

                // finish();
            } else {
                ToastUtils.showAlertToast(this, "Invalid Verification Code", ToastType.FAILURE_ALERT);
            }

        }
    }

    private boolean checkvalidCode() {

        return (et1.getText().toString() + et2.getText().toString() + et3.getText().toString() +
                et4.getText().toString() + et5.getText().toString() + et6.getText().toString()).equals(String.valueOf(verificationCode));
    }


    private void setResndOTP() {
        et1.setText("");
        et2.setText("");
        et3.setText("");
        et4.setText("");
        et5.setText("");
        et6.setText("");
        i = 0;
        et1.requestFocus();
        populateView();
        resetVerificationCodeToUserInfoTable(String.valueOf(verificationCode));
    }

    @Override
    public void onResponseObject(Object mObject) {

        if (mObject instanceof DataValidateUser) {
            DataValidateUser mDataValidateUser = (DataValidateUser) mObject;
            mPrefs.setRegister("Yes");
            mPrefs.setUserId(userId);
            mPrefs.setChatId(mDataValidateUser.getCountryCode() + mDataValidateUser.getPhoneNo() + "@" + ServiceXmppConnection.SERVICE_NAME);
            updateUserInfoTable(mDataValidateUser);


//            ((AppVhortex) getApplicationContext()).checkUserSession();
            if (mBound) {
                Message message = Message.obtain();
                message.what = ConstantXMPP.XMPPAUTHENTICATION;
                message.replyTo = uiMessenger;
                try {
                    messengerConnection.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        } else if (mObject instanceof DataResendOTP) {
            //set new verification code
            mProgressBarCircularIndeterminate.setVisibility(View.GONE);
            verificationCode = ((DataResendOTP) mObject).getVerificationCode();
            ToastUtils.showAlertToast(this, "New verification code has been sent to your mobile number", ToastType.FAILURE_ALERT);
            setResndOTP();
        } else if (mObject instanceof BaseResponse) {
            ((AppVhortex) getApplicationContext()).getContactService(this);
            mProgressBarCircularIndeterminate.setVisibility(View.GONE);
            startActivity(new Intent(this, ActivityContactSync.class));
//            startActivity(new Intent(this, ActivityDash.class));
            finish();
        }
    }

    private void updateUserInfoTable(DataValidateUser mDataValidateUser) {
        ContentValues mContentValues = new ContentValues();


        mContentValues.put(ConstantDB.UserName, mDataValidateUser.getUsername());
        mContentValues.put(ConstantDB.ProfileImage, mDataValidateUser.getProfileImage());
        mContentValues.put(ConstantDB.PhoneNo, mDataValidateUser.getPhoneNo());
        mContentValues.put(ConstantDB.CountryCode, mDataValidateUser.getCountryCode());
        mContentValues.put(ConstantDB.ChatId, mPrefs.getChatId());
        mContentValues.put(ConstantDB.Gender, mDataValidateUser.getGender());
        if (TextUtils.isEmpty(mDataValidateUser.getLanguage()))
            mContentValues.put(ConstantDB.Language, "English");
        else
            mContentValues.put(ConstantDB.Language, mDataValidateUser.getLanguage());
        if (TextUtils.isEmpty(mDataValidateUser.getLanguageIdentifire()))
            mContentValues.put(ConstantDB.LanguageIdentifire, "en");
        else
            mContentValues.put(ConstantDB.LanguageIdentifire, mDataValidateUser.getLanguageIdentifire());
        mContentValues.put(ConstantDB.UserStatus, mDataValidateUser.getUserStatus());
        mContentValues.put(ConstantDB.IsVerified, 1);
        mContentValues.put(ConstantDB.IsFindByPhoneno, mDataValidateUser.getIsfindbyphoneno());
        mContentValues.put(ConstantDB.IsTranslated, mDataValidateUser.getIstransalate());
        mContentValues.put(ConstantDB.IsFindByLocation, mDataValidateUser.getIsfindbylocation());
        mContentValues.put(ConstantDB.isNotificationOn, mDataValidateUser.getIsNotificationOn());

//        TableUserInfo mTableUserInfo = new TableUserInfo(mActivity);
        mTableUserInfo.editProfile(mContentValues, mDataValidateUser.getUserId());
    }

    @Override
    protected void registerChat() {

        mAsyncRegisterChat = new AsyncRegisterChat(mPrefs.getUserId(), mPrefs.getChatId(), this);
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncRegisterChat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncRegisterChat.execute();
        }
    }

    @Override
    public boolean RecieveChatFromXMPP(DataTextChat mdDataTextChat) {
        return true;
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        mProgressBarCircularIndeterminate.setVisibility(View.GONE);
    }

    @Override
    public void onResponseFaliure(String responseText) {
        try {
            mProgressBarCircularIndeterminate.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(responseText))
                ToastUtils.showAlertToast(this, responseText, ToastType.FAILURE_ALERT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class CustomRunnable implements Runnable {
        @Override
        public void run() {
            String code = String.valueOf(verificationCode);
            if (code.length() == 6) {
                if (i == 0) {
                    et1.setText(String.valueOf(code.charAt(0)));
                    et2.requestFocus();
                    ++i;
                    populateView();
                } else if (i == 1) {
                    et2.setText(String.valueOf(code.charAt(1)));
                    et3.requestFocus();
                    i++;
                    populateView();
                } else if (i == 2) {
                    et3.setText(String.valueOf(code.charAt(2)));
                    et4.requestFocus();
                    i++;
                    populateView();
                } else if (i == 3) {
                    et4.setText(String.valueOf(code.charAt(3)));
                    et5.requestFocus();
                    i++;
                    populateView();
                } else if (i == 4) {
                    et5.setText(String.valueOf(code.charAt(4)));
                    et6.requestFocus();
                    i++;
                    populateView();
                } else if (i == 5) {
                    et6.setText(String.valueOf(code.charAt(5)));
                    i++;
                }


            }
        }
    }
}
