package com.wachat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wachat.R;


public class DialogBlockFavouriteAlert extends Dialog implements
        View.OnClickListener {
    private boolean showCancel;
    private String alertMessage;
    private Context context;
    private OnClickListener clickListener;
    private String txt;

    TextView tv_dialog_ok;
    private TextView tv_dialog_cancel;

    public DialogBlockFavouriteAlert(Context context,String alertMessage,boolean showCancel, OnClickListener clickListener) {
        super(context);
        this.context = context;
        this.clickListener = clickListener;
        this.alertMessage = alertMessage;
        this.showCancel = showCancel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_block_favorite_alert);
        //  this.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;

        initView();
        initClickListener();
    }

    private void initView() {
        tv_dialog_ok = (TextView) findViewById(R.id.tv_dialog_ok);
        tv_dialog_cancel = (TextView) findViewById(R.id.tv_dialog_cancel);
        tv_dialog_cancel.setVisibility(showCancel?View.VISIBLE:View.GONE);
        ((TextView) findViewById(R.id.dialog_common_header_txt)).setText(alertMessage);
    }

    private void initClickListener() {
        tv_dialog_ok.setOnClickListener(this);
        tv_dialog_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_ok:
                // send callback
                if(clickListener!=null) {
                    clickListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
                }
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
