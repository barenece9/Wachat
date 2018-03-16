package com.wachat.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wachat.GcmOnCompleteListener;
import com.wachat.GcmUtilities;
import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncRegisterUser;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataCountry;
import com.wachat.data.DataRegistration;
import com.wachat.data.DataValidateUser;
import com.wachat.dialog.CopyRightDialog;
import com.wachat.dialog.DialogRegistrationConfirm;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;
import com.wachat.util.ValidationUtils;
import com.wachat.util.WebContstants;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 17-08-2015.
 */
public class ActivityRegistration extends BaseActivity implements View.OnClickListener,
        Animator.AnimatorListener, ViewTreeObserver.OnGlobalLayoutListener, TextWatcher, InterfaceResponseCallback {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityRegistration.class);
    public EditText et_Number;
    public String countryCode = "";
    float logoX_Final, logoY_Final, textX_Final, textY_Final;
    float logoX_Scale, logoY_Scale, textX_Scale, textY_Scale;
    int hContainer;
    int logoHeight;
    int txtHeight;
    DialogRegistrationConfirm mDialogRegistrationConfirm;
    FrameLayout main_container;
    View view;
//    Prefs mPrefs;
    int[] logoLOC = new int[2];
    int[] textLOC = new int[2];
    private View mView;
    //    private FrameLayout fl;
    private ImageView iv_Logo, iv_Text;
    private TextView tv_Start;
    //    private TextView tv_SelectCountry;
    private EditText et_country_code;
    //    private TextView tv_code;
    private LinearLayout lnr_Bottom;
    private AnimatorSet mAnimatorSet;
    private ObjectAnimator animatorYText, animatorScale;
    private float x, y, xText, yText;
    private boolean isAnimated = false;
    private float m_logoHeightActual, m_logoWidthActual;
    private float m_txtHeightActual, m_txtWidthActual;
    private boolean keyboardVisible = false;
//    private AsyncCountryCodeSelection mRunnableCountryCodeSelection;
    private AsyncRegisterUser mAsyncRegisterUser;
//    private DataCountry mDataCountry = new DataCountry();
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;
    //    private ImageView iv_terms_policy_check;
    private TextView tv_terms_policy;
    //    private LinearLayout ll_terms_policy_check;
    private EditText registration_et_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        mPrefs = Prefs.getInstance(mActivity);
        mPrefs.setIsRegClick("true");
        getWindow().setBackgroundDrawable(null);
        getDataFromByndle();
        countryCode = AppVhortex.getInstance().countryCode;




        if(TextUtils.isEmpty(countryCode)){
            Toast.makeText(this,getString(R.string.alert_select_country_error_new),Toast.LENGTH_LONG).show();
//            ToastUtils.showAlertToast(ActivityRegistration.this,
//                    getString(R.string.alert_select_country_error_new),
//                    ToastType.FAILURE_ALERT);
        }
//        if (CommonMethods.isOnline(mActivity)) {
//            getCountryCode();
//        } else {
//            ToastUtils.showAlertToast(mActivity, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
//        }

        initView();

        if(TextUtils.isEmpty(mPrefs.getGcmId())){
            getGCMRegistered();
        }
    }

    private void getGCMRegistered() {
        GcmUtilities gcmUtilities = new GcmUtilities(this, new GcmOnCompleteListener() {
            @Override
            public void onComplete() {
                Log.i("ActivityRegistration", "GcmUtilities:onComplete");
            }

            @Override
            public void onError(String message) {
                Log.i("ActivityRegistration", "GcmUtilities:onError");
                showGcmRetryAlert(getString(
                        R.string.alert_failure_server_connection_failed_));
            }

            @Override
            public void onCancel() {
                Log.i("ActivityRegistration", "GcmUtilities:onCancel");
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
                    if (TextUtils.isEmpty(Prefs.getInstance(ActivityRegistration.this).getGcmId())) {
                        getGCMRegistered();
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
    private void getDataFromByndle() {
        Bundle mBundle = getIntent().getExtras();
//        if (mBundle != null) {
//            mDataCountry = (DataCountry) mBundle.getSerializable(Constants.B_RESULT);
//            if (mDataCountry != null) {
//                TableCountry mTableCountry = new TableCountry(mActivity);
//                mDataCountry = mTableCountry.getCountryCode(mDataCountry.getCountryCode());
//                countryCode = mDataCountry.getCountryCode();
//
//                mPrefs.setCountryCode(countryCode);
//                mPrefs.setCountryName(mDataCountry.getCountryName());
//            } else {
//                // getCountryCode();
//            }
//        } else {
//            //getCountryCode();
//        }

        mPrefs.setCountryCode(countryCode);
//        mPrefs.setCountryName(mDataCountry.getCountryName());
    }

//    private void getCountryCode() {
//        if (CommonMethods.isOnline(ActivityRegistration.this)) {
//
//            mRunnableCountryCodeSelection = new AsyncCountryCodeSelection(mActivity,
//                    new InterfaceResponseCallback() {
//                        @Override
//                        public void onResponseObject(Object mObject) {
//                            if (mObject instanceof DataCountry) {
////                                tv_SelectCountry.setText(((DataCountry) mObject).getCountryName());
//                                et_country_code.setText("+" + ((DataCountry) mObject).getCountryCode());
//                                countryCode = ((DataCountry) mObject).getCountryCode();
//                            }
//                        }
//
//                        @Override
//                        public void onResponseList(ArrayList<?> mList) {
//
//                        }
//
//                        @Override
//                        public void onResponseFaliure(String responseText) {
//                            ToastUtils.showAlertToast(ActivityRegistration.this,
//                                    getString(R.string.alert_select_country_error_new),
//                                    ToastType.FAILURE_ALERT);
//                        }
//                    });
//            if (CommonMethods.checkBuildVersionAsyncTask()) {
//                mRunnableCountryCodeSelection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            } else {
//                mRunnableCountryCodeSelection.execute();
//            }
//
//        } else {
//            ToastUtils.showAlertToast(ActivityRegistration.this,
//                    getResources().getString(R.string.no_internet),
//                    ToastType.FAILURE_ALERT);
//        }
//    }

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

    private void initView() {

        mView = getWindow().getDecorView();
//        fl = (FrameLayout) findViewById(R.id.fl);
        lnr_Bottom = (LinearLayout) findViewById(R.id.lnr_Bottom);
        iv_Logo = (ImageView) findViewById(R.id.iv_Logo);
        iv_Text = (ImageView) findViewById(R.id.iv_Text);
//        tv_SelectCountry = (TextView) findViewById(R.id.tv_SelectCountry);
//        tv_code = (TextView) findViewById(R.id.tv_code);
        et_country_code = (EditText) findViewById(R.id.et_country_code);
        tv_Start = (TextView) findViewById(R.id.tv_Start);
        tv_Start.setOnClickListener(this);
        et_Number = (EditText) findViewById(R.id.et_Number);
        registration_et_name = (EditText) findViewById(R.id.registration_et_name);
//        tv_Start.setBackgroundColor(getResources().getColor(R.color.app_Brown));
//        tv_Start.setAlpha(0.6f);
//        tv_Start.setClickable(false);
//        tv_SelectCountry.setOnClickListener(this);

//        iv_terms_policy_check = (ImageView) findViewById(R.id.iv_terms_policy_check);
//        iv_terms_policy_check.setSelected(false);
//        iv_terms_policy_check.setOnClickListener(this);
//        ll_terms_policy_check = (LinearLayout) findViewById(R.id.ll_terms_policy_check);
//        ll_terms_policy_check.setOnClickListener(this);
        tv_terms_policy = (TextView) findViewById(R.id.tv_terms_policy);


        String info = tv_terms_policy.getText().toString();
        tv_terms_policy.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableString spanString = new SpannableString(info);
//        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 11);
//        spanString.setSpan(new StyleSpan(Typeface.BOLD), 9, spanString.length(), 0);


        Spannable termsSpannable = (Spannable) tv_terms_policy.getText();
        ClickableSpan termsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

//                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(WebContstants.terms));
//                    startActivityForResult(intent, 1234);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    ToastUtils.showAlertToast(ActivityRegistration.this, getString(R.string.alert_failure_yahoo_not_found), ToastType.FAILURE_ALERT);
//                }

                Intent mIntent1 = new Intent(ActivityRegistration.this, ActivityAbout.class);
                Bundle bundle = new Bundle();
                bundle.putString(ActivityAbout.TARGET_URL, WebContstants.terms);
                bundle.putString(ActivityAbout.TITLE, getString(R.string.terms_of_use));
                mIntent1.putExtras(bundle);
                startActivity(mIntent1);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.underline_green));
                ds.setTypeface(Typeface.create(ds.getTypeface(), Typeface.BOLD));

                ds.setUnderlineText(true);
            }
        };
        termsSpannable
                .setSpan(termsClickableSpan, 0,
                        12,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Spannable privacySpannable = (Spannable) tv_terms_policy.getText();
        ClickableSpan privacyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(WebContstants.privacy));
//                    startActivityForResult(intent, 1234);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    ToastUtils.showAlertToast(ActivityRegistration.this, getString(R.string.alert_failure_yahoo_not_found), ToastType.FAILURE_ALERT);
//                }

                Intent mIntent1 = new Intent(ActivityRegistration.this, ActivityAbout.class);
                Bundle bundle = new Bundle();
                bundle.putString(ActivityAbout.TARGET_URL, WebContstants.privacy);
                bundle.putString(ActivityAbout.TITLE, getString(R.string.privacy_policy));
                mIntent1.putExtras(bundle);
                startActivity(mIntent1);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.underline_green));
                ds.setTypeface(Typeface.create(ds.getTypeface(), Typeface.BOLD));
                ds.setUnderlineText(true);
            }
        };
        privacySpannable
                .setSpan(privacyClickableSpan, 15,
                        info.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tv_terms_policy.setText(spanString);
//        tv_terms_policy.setOnClickListener(this);

//if(!TextUtils.isEmpty(mPrefs.getCountryName()))
//        tv_SelectCountry.setText(mPrefs.getCountryName());
//        tv_code.setText("+" + mPrefs.getCountryCode());


        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                initiateAnimationLogo();
            }
        }, 800);

        positioningView();

//        et_Number.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (et_Number.hasFocus()) {
//                    if (!isAnimated)
//                        animateToSmallPosition();
//                }
//            }
//        });

        et_Number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.KEYCODE_BACK) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_Number.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    returnAnimation();
                    return true;
                }
                return false;
            }

        });
//        et_Number.addTextChangedListener(this);
        if (!TextUtils.isEmpty(countryCode)) {
            et_country_code.setText("+" + countryCode);
//            et_country_code.setEnabled(false);
//            et_Number.requestFocus();
        }else{
            et_country_code.post(new Runnable() {
                @Override
                public void run() {
                    et_country_code.setEnabled(true);
                    et_country_code.setText("+");
                    et_country_code.requestFocus();
                    et_country_code.setSelection(et_country_code.getText().toString().length());
                }
            });

        }
//        registration_et_name.addTextChangedListener(this);
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
    }


    private void positioningView() {
        ViewTreeObserver vto = mView.getViewTreeObserver();
        if (vto != null) {
            vto.addOnGlobalLayoutListener(this);
        }
    }

    void showDialog() {
        CopyRightDialog copyRightDialog = new CopyRightDialog();
        copyRightDialog = new CopyRightDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.B_TYPE, "");
        copyRightDialog.setArguments(bundle);
        copyRightDialog.show(getSupportFragmentManager(), CopyRightDialog.class.getSimpleName());
    }

    @Override
    public void onGlobalLayout() {
//        hContainer = mView.getHeight() - fl.getHeight() - 10;
//        logoHeight = iv_Logo.getHeight();
//        txtHeight = iv_Text.getHeight();
//
//        x = iv_Logo.getTranslationX();
//        y = iv_Logo.getTranslationY();
//        if (y != (hContainer - (logoHeight + txtHeight)) / 2 || x != (mView.getWidth() - iv_Logo.getWidth()) / 2) {
//            y = (hContainer - (logoHeight + txtHeight)) / 2;
//            x = (mView.getWidth() - iv_Logo.getWidth()) / 2;
//            m_logoHeightActual = iv_Logo.getHeight();
//            m_logoWidthActual = iv_Logo.getWidth();
//            iv_Logo.setTranslationY(y);
//            iv_Logo.setTranslationX(x);
//            logoX_Final = iv_Logo.getX();
//            logoY_Final = iv_Logo.getY();
//            logoX_Scale = iv_Logo.getScaleX();
//            logoY_Scale = iv_Logo.getScaleY();
//            LogUtils.i(LOG_TAG, "LOGO " + logoX_Final + "- X FINAL " + logoY_Final + "- Y FINAL");
//        }
//
//        xText = iv_Text.getTranslationX();
//        yText = iv_Text.getTranslationY();
//        if (yText != (y + iv_Logo.getHeight()) || xText != (fl.getWidth() - iv_Text.getWidth()) / 2) {
//            yText = (y + iv_Logo.getHeight());
//            xText = (mView.getWidth() - iv_Text.getWidth()) / 2;
//            m_txtWidthActual = iv_Text.getWidth();
//            m_txtHeightActual = iv_Text.getHeight();
//            iv_Text.setTranslationY(yText);
//            iv_Text.setTranslationX(xText);
//            textX_Final = iv_Text.getX();
//            textY_Final = iv_Text.getY();
//            textX_Scale = iv_Text.getScaleX();
//            textY_Scale = iv_Text.getScaleY();
//            LogUtils.i(LOG_TAG, "TEXT " + textX_Final + "- X TEXT FINAL " + textY_Final + "- Y TEXT FINAL");
//        }
//
//        LogUtils.i(LOG_TAG, "Height " + hContainer);
    }

    private void initiateAnimationLogo() {
//        ObjectAnimator animatorXLogo = ObjectAnimator.ofFloat(iv_Logo, "scaleX", 0.0f, 0.8f, 1.1f, 1.0f);
//        ObjectAnimator animatorYLogo = ObjectAnimator.ofFloat(iv_Logo, "scaleY", 0.0f, 0.8f, 1.1f, 1.0f);
//        ObjectAnimator animatorRotate = ObjectAnimator.ofFloat(iv_Logo, "rotation", 0f, 360f);
//        animatorXLogo.setDuration(600);
//        animatorYLogo.setDuration(600);
//        animatorRotate.setDuration(600);
//
//        mAnimatorSet = new AnimatorSet();
//        mAnimatorSet.play(animatorXLogo).with(animatorYLogo).with(animatorRotate);
//        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
//        mAnimatorSet.addListener(this);
//        mAnimatorSet.start();
    }

    private void initiateAnimatorText() {
//        iv_Text.setVisibility(View.VISIBLE);
//        animatorYText = ObjectAnimator.ofFloat(iv_Text, "translationX", -100.0f, xText, xText + 20, xText);
//        animatorYText.setDuration(600);
//        animatorYText.addListener(this);
//        animatorYText.setInterpolator(new AccelerateInterpolator());
//        animatorYText.start();
    }

    private void initiatAnimatoreBottomBar() {
//        animatorScale = ObjectAnimator.ofFloat(lnr_Bottom, "alpha", 0.0f, 1.0f);
//        animatorScale.setDuration(600);
//        animatorScale.addListener(this);
//        animatorScale.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {
//        if (animation instanceof AnimatorSet) {
//            iv_Logo.setVisibility(View.VISIBLE);
//        } else if (animation == animatorYText) {
//            iv_Text.setVisibility(View.VISIBLE);
//        } else {
//            lnr_Bottom.setVisibility(View.VISIBLE);
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            } else {
//                mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            }
//        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
//        if (animation instanceof AnimatorSet) {
//            initiateAnimatorText();
//        } else if (animation == animatorYText) {
//            initiatAnimatoreBottomBar();
//        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onBackPressed() {
        if (isAnimated)
            returnAnimation();
        else
            super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
//        et_Number.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                et_Number.setFocusable(true);
////                et_Number.setFocusableInTouchMode(true);
////                et_Number.requestFocus();
//                keyboardEvent();
//
//            }
//        }, 600);
//        et_Number.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (et_Number.hasFocus()) {
////                    if (!isAnimated)
////                        animateToSmallPosition();
////                }
//            }
//        });
//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(et_Number.getWindowToken(), InputMethodManager.RESULT_SHOWN);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        et_Number.setFocusable(false);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(et_Number.getWindowToken(), 0);
    }

    private void animateToSmallPosition() {

//        isAnimated = true;
//        iv_Logo.getLocationOnScreen(logoLOC);
//        iv_Text.getLocationOnScreen(textLOC);
//
//        AnimationSet animSet = new AnimationSet(true);
//        animSet.setFillAfter(true);
//        animSet.setDuration(250);
//        animSet.setInterpolator(new AccelerateInterpolator());
//        TranslateAnimation translate = new TranslateAnimation(logoLOC[0], 0, logoLOC[1], 0);
//        animSet.addAnimation(translate);
//        ScaleAnimation scale = new ScaleAnimation(1f, 0.4f, 1f, 0.4f, ScaleAnimation.RELATIVE_TO_SELF, 0f, ScaleAnimation.RELATIVE_TO_SELF, 0f);
//        animSet.addAnimation(scale);
//        iv_Logo.startAnimation(animSet);
//
//        AnimationSet animationSet = new AnimationSet(true);
//        animationSet.setFillAfter(true);
//        animationSet.setDuration(250);
//        animationSet.setInterpolator(new AccelerateInterpolator());
//        TranslateAnimation translateText = new TranslateAnimation(textLOC[0], m_logoWidthActual * 0.3f, textLOC[1], ((m_logoHeightActual * 0.3f) / 2) - yText);
//        animationSet.addAnimation(translateText);
//        ScaleAnimation scaleText = new ScaleAnimation(1f, 0.9f, 1f, 0.9f, ScaleAnimation.RELATIVE_TO_SELF, 0f, ScaleAnimation.RELATIVE_TO_SELF, 0f);
//        animationSet.addAnimation(scaleText);
//        iv_Text.startAnimation(animationSet);
//
//        LogUtils.i(LOG_TAG, "Height " + hContainer);
    }

    private void returnAnimation() {
//        LogUtils.i(LOG_TAG, "LOGO " + logoX_Final + "- X FINAL " + logoY_Final + "- Y FINAL");
//        LogUtils.i(LOG_TAG, "TEXT " + textX_Final + "- X FINAL " + textY_Final + "- Y FINAL");
//        AnimationSet animSet = new AnimationSet(true);
//        animSet.setFillAfter(true);
//        animSet.setDuration(250);
//        animSet.setInterpolator(new DecelerateInterpolator());
//        TranslateAnimation translate = new TranslateAnimation(0 - logoX_Final, 0,
//                -logoY_Final, 0);
//        animSet.addAnimation(translate);
//        ScaleAnimation scale = new ScaleAnimation(iv_Logo.getScaleX(), logoX_Scale,
//                iv_Logo.getScaleY(), logoY_Scale, ScaleAnimation.ABSOLUTE, 0.5f, ScaleAnimation.ABSOLUTE, 0.5f);
//        animSet.addAnimation(scale);
//        iv_Logo.startAnimation(animSet);
//
//        AnimationSet animationSet = new AnimationSet(true);
//        animationSet.setFillAfter(true);
//        animationSet.setDuration(250);
//        animationSet.setInterpolator(new AccelerateInterpolator());
//        TranslateAnimation translateText = new TranslateAnimation(-textX_Final, 0,
//                -textY_Final, 0);
//        animationSet.addAnimation(translateText);
//        ScaleAnimation scaleText = new ScaleAnimation(iv_Text.getScaleX(), textX_Scale, iv_Text.getScaleY(), textY_Scale
//                , ScaleAnimation.RELATIVE_TO_SELF, 0f, ScaleAnimation.RELATIVE_TO_SELF, 0f);
//        animationSet.addAnimation(scaleText);
//        iv_Text.startAnimation(animationSet);
//
//        isAnimated = false;
//        LogUtils.i(LOG_TAG, "Height " + hContainer);
    }

    //keyboard state checking


    private void keyboardEvent() {
//        main_container = (FrameLayout) findViewById(R.id.main_container);
//        view = findViewById(R.id.view);
//        main_container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                try {
//                    if (main_container == null) {
//                        return;
//                    }
//
//                    Rect r = new Rect();
//                    main_container.getWindowVisibleDisplayFrame(r);
//
//                    int heightDiff = main_container.getRootView().getHeight() - (r.bottom - r.top);
//                    if (heightDiff > dpToPx(100)) {
//                        if (!keyboardVisible) {
//                            keyboardVisible = true;
//                            //  setbackground(keyboardVisible);
//
//                        }
//                    } else {
//                        if (keyboardVisible) {
//                            keyboardVisible = false;
//                            // setbackground(keyboardVisible);
//
//                            returnAnimation();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Start:

                String name = "";
                name = registration_et_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {

                    ToastUtils.showAlertToast(ActivityRegistration.this, getResources().getString(R.string.alert_enter_name),
                            ToastType.FAILURE_ALERT);
                    return;
                }

                String countryCode = "";
                countryCode = et_country_code.getText().toString();
                if (TextUtils.isEmpty(countryCode)) {

                    ToastUtils.showAlertToast(ActivityRegistration.this, getResources().getString(R.string.alert_no_country),
                            ToastType.FAILURE_ALERT);
                    return;
                }


                String mobileNo = et_Number.getText().toString().trim();
                if (TextUtils.isEmpty(mobileNo)) {

                    ToastUtils.showAlertToast(ActivityRegistration.this, getResources().getString(R.string.alert_enter_mobile_number),
                            ToastType.FAILURE_ALERT);
                    return;
                }

                if (!ValidationUtils.isValidPhoneNumber(mobileNo)) {
                    et_Number.setError(getString(R.string.alert_error_invalid_phone_number));
                    return;
                }

                if (ValidationUtils.containOnlyZero(mobileNo)) {
                    et_Number.setError(getString(R.string.alert_error_invalid_phone_number));
                    return;
                }

                if (mPrefs.getIsRegClick().equals("true")) {
                    mDialogRegistrationConfirm = DialogRegistrationConfirm.
                            getInstance(countryCode, mobileNo);
                    mDialogRegistrationConfirm.show(mFragmentManager, DialogRegistrationConfirm.class.getSimpleName());
                }
                //  }

                break;

//            case R.id.tv_SelectCountry:
//
//                Intent intent = new Intent(ActivityRegistration.this, ActivitySelectCountry.class);
//                startActivityForResult(intent, Constants.ChooseCountry);
//                break;

            case R.id.tv_dialog_cancel:
                mDialogRegistrationConfirm.dismiss();
                mPrefs.setIsRegClick("true");
                break;

            case R.id.tv_dialog_ok:
                if (CommonMethods.isOnline(ActivityRegistration.this)) {
                    registerUser();
                    mDialogRegistrationConfirm.dismiss();
                    mPrefs.setIsRegClick("false");
//                    finish();
                } else {
                    ToastUtils.showAlertToast(ActivityRegistration.this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                }

                break;

            case R.id.tv_terms_policy:

                Intent intent = new Intent(ActivityRegistration.this, ActivitySelectCountry.class);
                startActivityForResult(intent, Constants.ChooseCountry);
                break;

//            case R.id.ll_terms_policy_check:
//
//                iv_terms_policy_check.setSelected(!iv_terms_policy_check.isSelected());
//
//                checkStartButtonStatus();
//                break;

            default:
                super.onClick(v);
                break;

        }
    }

    private void checkStartButtonStatus() {
//        if (et_Number.getText().toString().trim().replaceAll("  ", "").length() >= 10
//                && et_country_code.getText().toString().trim().replaceAll("  ", "").length() >= 0
//                && registration_et_name.getText().toString().trim().replaceAll("  ", "").length() >= 0) {
////            if (iv_terms_policy_check.isSelected()) {
//                tv_Start.setAlpha(1.0f);
//                tv_Start.setClickable(true);
//                tv_Start.setOnClickListener(this);
////            } else {
////                tv_Start.setAlpha(0.6f);
////                tv_Start.setClickable(false);
////            }
//
//        } else {
//            tv_Start.setAlpha(0.6f);
//            tv_Start.setClickable(false);
//        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {
//        if (et_Number.getText().toString().trim().length() >= 10) {
//
//            tv_Start.setBackgroundColor(getResources().getColor(R.color.app_Brown));
//            tv_Start.setClickable(true);
//            tv_Start.setOnClickListener(this);
//
//        } else {
//            tv_Start.setAlpha(0.6f);
//            tv_Start.setClickable(false);
//        }

        checkStartButtonStatus();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterTextChanged(Editable s) {

//        String result = s.toString().replaceAll("  ", "");
//
//        if (!s.toString().equals(result)) {
//            et_Number.setText(result);
//            et_Number.setSelection(result.length());
//        }

        checkStartButtonStatus();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ChooseCountry) {
            if (data != null) {
                try {
                    DataCountry mDataCountry = (DataCountry) (data.getExtras()).getSerializable(Constants.B_RESULT);
//                    this.mDataCountry = mDataCountry;
                    if (!TextUtils.isEmpty(mDataCountry.getCountryName()))
//                        tv_SelectCountry.setText(mDataCountry.getCountryName());
//                    tv_code.setText("+" + mDataCountry.getCountryCode());
                        countryCode = mDataCountry.getCountryCode();
                    mPrefs.setCountryCode(countryCode);
                    mPrefs.setCountryName(mDataCountry.getCountryName());

                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void registerUser() {
        progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
        mAsyncRegisterUser = new AsyncRegisterUser(this,CommonMethods.getDeviceUniqueId(this),
                CommonMethods.getUTFEncodedString(registration_et_name.getText().toString().trim()),
                et_country_code.getText().toString().trim().replace("+", ""),
                ""/*tv_SelectCountry.getText().toString()*/, et_Number.getText().toString().trim(), this);
        if (mAsyncRegisterUser != null && mAsyncRegisterUser.getStatus() != AsyncTask.Status.RUNNING) {
            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mAsyncRegisterUser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mAsyncRegisterUser.execute();
            }
        }
    }

    @Override
    public void onResponseObject(Object mObject) {
        progressBarCircularIndetermininate.setVisibility(View.GONE);
        if (mObject instanceof DataCountry) {
//            if (!TextUtils.isEmpty(((DataCountry) mObject).getCountryName()))
//                tv_SelectCountry.setText(((DataCountry) mObject).getCountryName());
//
//            tv_code.setText("+" + ((DataCountry) mObject).getCountryCode());
            //countryCode = ((DataCountry) mObject).getCountryCode();
        } else if (mObject instanceof DataRegistration) {
            mPrefs.setIsRegClick("true");
            Bundle mBundle = new Bundle();
            DataRegistration mDataRegistration = (DataRegistration) mObject;
            insertRegisteredDataToUserInfo(mDataRegistration);

            mBundle.putInt(Constants.B_ID, mDataRegistration.getVerificationCode());
            mBundle.putString(Constants.B_TYPE, mDataRegistration.getUserId());
            mBundle.putString(Constants.B_RESULT, et_country_code.getText().toString().trim().replace("+", "") + et_Number.getText().toString().trim());
            mBundle.putString(Constants.B_CODE, et_country_code.getText().toString().trim().replace("+", ""));
            Intent mIntent = new Intent(ActivityRegistration.this, ActivityAuthentication.class);
            mIntent.putExtras(mBundle);
            startActivity(mIntent);
            finish();
        }
//        try{
//            tv_SelectCountry.setText(((DataCountry)mObject).getCountryName());
//        }catch(ClassCastException e){
//            e.printStackTrace();
//        }
    }

    private void insertRegisteredDataToUserInfo(DataRegistration mDataRegistration) {

        DataValidateUser mDataValidateUser = new DataValidateUser();
        mDataValidateUser.setUserId(mDataRegistration.getUserId());
        mDataValidateUser.setVerificationCode(String.valueOf(mDataRegistration.getVerificationCode()));
        mDataValidateUser.setCountryCode(et_country_code.getText().toString().trim().replace("+", ""));
        mDataValidateUser.setPhoneNo(et_country_code.getText().toString().trim().replace("+", "") + et_Number.getText().toString().trim());
        mDataValidateUser.setIsRegistered(1);
        mDataValidateUser.setUsername(CommonMethods.getUTFEncodedString(registration_et_name.getText().toString()));
//        mDataValidateUser.setCountryName(mDataCountry.getCountryName());



        TableUserInfo mTableUserInfo = new TableUserInfo(ActivityRegistration.this);
        //clear previous data if any
        mTableUserInfo.delete();
        mTableUserInfo.insertUser(mDataValidateUser);
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        progressBarCircularIndetermininate.setVisibility(View.GONE);
    }

    @Override
    public void onResponseFaliure(String responseText) {
        isConfirmClicked = true;
        progressBarCircularIndetermininate.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(responseText)) {
            showRetryAlert();
        }
    }


    public void showRetryAlert() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegistration.this);
            builder.setTitle("Vhortext");
            builder.setMessage(getResources().getString(R.string.network_try));
            builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (CommonMethods.isOnline(ActivityRegistration.this)) {
                        registerUser();

                    } else {
                        ToastUtils.showAlertToast(ActivityRegistration.this,
                                getResources().getString(R.string.no_internet),
                                ToastType.FAILURE_ALERT);
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    public void setCountryCode() {
//        TableCountry mTableCountry = new TableCountry(ActivityRegistration.this);
//        mDataCountry = mTableCountry.getCountryCode(mDataCountry.getCountryName());
//
//    }
}
