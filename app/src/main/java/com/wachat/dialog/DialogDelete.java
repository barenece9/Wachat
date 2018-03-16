package com.wachat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.callBack.CallbackFromDialog;
import com.wachat.callBack.DialogCallback;
import com.wachat.util.CommonMethods;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;


public class DialogDelete extends Dialog implements
        View.OnClickListener {
    private Context context;
    private CallbackFromDialog mDialogCallback;
    private String txt;

    TextView tv_dialog_cancel, tv_dialog_ok;

    public DialogDelete(Context context, CallbackFromDialog mDialogCallback) {
        super(context);
        this.context = context;
        this.mDialogCallback = mDialogCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_delete);
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
                mDialogCallback.OnClickDelete();
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
