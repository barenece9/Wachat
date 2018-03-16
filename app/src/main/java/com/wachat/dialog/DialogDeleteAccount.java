package com.wachat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.callBack.CallbackFromDialog;


public class DialogDeleteAccount extends Dialog implements
        View.OnClickListener {
    private Context context;

    private String txt;
    private CallbackFromDialog mCallbackFromDialog;
    TextView tv_dialog_cancel, tv_dialog_ok;

    public DialogDeleteAccount(Context context,CallbackFromDialog mCallbackFromDialog) {
        super(context);
        this.context = context;
        this.mCallbackFromDialog = mCallbackFromDialog;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_delete_account);
      //  this.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;

        initView();
        initClickListener();
    }

    private void initView() {
        tv_dialog_cancel = (TextView) findViewById(R.id.tv_dialog_cancel);
        tv_dialog_ok = (TextView) findViewById(R.id.tv_dialog_ok);

    }

    private void initClickListener() {
        tv_dialog_cancel.setOnClickListener(this);
        tv_dialog_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_ok:
                // send callback
                mCallbackFromDialog.OnClickDelete();
                dismiss();
                break;
            case R.id.tv_dialog_cancel:
                dismiss();
                break;

            default:
                break;
        }
    }

}
