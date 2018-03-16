package com.wachat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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
import com.wachat.activity.ActivityRegistration;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;


public class DialogRegistrationConfirm extends DialogFragment {
    private Context context;

    private String txt,code;
    String phNo;

    TextView tv_dialog_cancel, tv_dialog_ok,dialog_common_header_txt;

    public static DialogRegistrationConfirm getInstance(String code,String phNo){
        DialogRegistrationConfirm mDialogRegistrationConfirm = new DialogRegistrationConfirm();
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.B_RESULT, phNo);
        mBundle.putString(Constants.B_ID,code);
        mDialogRegistrationConfirm.setArguments(mBundle);
        return mDialogRegistrationConfirm;
    }

    private ActivityRegistration mActivityRegistration;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ActivityRegistration){
            mActivityRegistration = (ActivityRegistration)activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBundleData();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        setContentView(R.layout.dialog_reg_confirmation);
//        this.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
//
//        initView();
//        initClickListener();
    }

    private void getBundleData() {
        Bundle mBundle = getArguments();
        if(mBundle != null && mBundle.containsKey(Constants.B_RESULT)){
            txt = mBundle.getString(Constants.B_RESULT);
            code=mBundle.getString(Constants.B_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().setCanceledOnTouchOutside(true);
        return inflater.inflate(R.layout.dialog_reg_confirmation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog_common_header_txt=(TextView)view.findViewById(R.id.dialog_common_header_txt);
        tv_dialog_cancel = (TextView)view.findViewById(R.id.tv_dialog_cancel);
        tv_dialog_ok = (TextView)view.findViewById(R.id.tv_dialog_ok);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dialog_common_header_txt.setText(mActivityRegistration.getResources().getString(R.string.reg_confirmation_text)+"\n"  + code +" "+ txt);
        tv_dialog_ok.setOnClickListener(mActivityRegistration);
        tv_dialog_cancel.setOnClickListener(mActivityRegistration);
    }

}
