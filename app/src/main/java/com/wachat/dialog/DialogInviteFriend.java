package com.wachat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.callBack.DialogCallback;


public class DialogInviteFriend extends Dialog implements
        View.OnClickListener {
    int pos;
    TextView tv_dialog_cancel, tv_dialog_ok;
    private String friendName = "";
    private Context context;
    private DialogCallback mDialogCallback;
    private String txt;
    private TextView dialog_common_header_txt;

    public DialogInviteFriend(Context context, DialogCallback mDialogCallback, int pos, String friendName) {
        super(context);
        this.context = context;
        this.mDialogCallback = mDialogCallback;
        this.pos = pos;
        this.friendName = friendName;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_invite_friend);
        //  this.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;

        initView();
        initClickListener();
        String friendFormattedName = String.format(context.getResources().getString(R.string.invite_text),
                friendName);
        dialog_common_header_txt.setText(TextUtils.isEmpty(friendFormattedName) ? getContext().getResources().getString(R.string.invite_text_generic) : friendFormattedName);
    }

    private void initView() {
        tv_dialog_cancel = (TextView) findViewById(R.id.tv_dialog_cancel);
        tv_dialog_ok = (TextView) findViewById(R.id.tv_dialog_ok);
        dialog_common_header_txt = (TextView) findViewById(R.id.dialog_common_header_txt);
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
                mDialogCallback.onYes(pos);
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
