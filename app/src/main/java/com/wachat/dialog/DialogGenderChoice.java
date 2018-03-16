package com.wachat.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.util.Constants;


public class DialogGenderChoice extends DialogFragment {
    private Context context;

    private String txt;
    String gender;

    TextView tv_male, tv_female, tv_both;


    private BaseActivity mActivityEditProfile;
    private boolean showAllOption = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseActivity) {
            mActivityEditProfile = (BaseActivity) activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBundleData();
    }

    private void getBundleData() {
        Bundle mBundle = getArguments();
        if (mBundle != null && mBundle.containsKey(Constants.B_RESULT)) {
            txt = mBundle.getString(Constants.B_RESULT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().setCanceledOnTouchOutside(true);
        return inflater.inflate(R.layout.dialog_gender_choose, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_male = (TextView) view.findViewById(R.id.tv_male);
        tv_female = (TextView) view.findViewById(R.id.tv_female);
        tv_both = (TextView) view.findViewById(R.id.tv_both);

        tv_both.setVisibility(showAllOption ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_male.setOnClickListener(mActivityEditProfile);
        tv_female.setOnClickListener(mActivityEditProfile);
        tv_both.setOnClickListener(mActivityEditProfile);

    }

    public void showAll(boolean showAllOption) {
        this.showAllOption = showAllOption;
    }
}
