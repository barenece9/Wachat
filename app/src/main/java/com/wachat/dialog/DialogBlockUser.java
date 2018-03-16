package com.wachat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.callBack.DialogCallback;
import com.wachat.util.CommonMethods;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;


public class DialogBlockUser extends Dialog implements
        View.OnClickListener {
    private Context context;


    TextView tv_no, tv_yes;
    private DialogCallback mDialogCallback;
    int pos;

    public DialogBlockUser(Context context, DialogCallback mDialogCallback,int pos) {
        super(context);
        this.context = context;
        this.mDialogCallback = mDialogCallback;
        this.pos=pos;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        ViewGroup.LayoutParams mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
//                .MATCH_PARENT, CommonMethods.getScreenWidth(context).widthPixels / 2);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_block_user_popup);
        // this.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;

        initView();
        initClickListener();
    }

    private void initView() {
        tv_no = (TextView) findViewById(R.id.tv_no);
        tv_yes = (TextView) findViewById(R.id.tv_yes);


    }

    private void initClickListener() {
        tv_no.setOnClickListener(this);
        tv_yes.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_yes:
                // ToastUtils.showAlertToast(context,"view profile", ToastType.SUCESS_ALERT);
                mDialogCallback.onYes(pos);
                dismiss();
                break;


            case R.id.tv_no:
                // ToastUtils.showAlertToast(context, "view profile", ToastType.SUCESS_ALERT);
                dismiss();
                break;

            default:
                break;
        }
    }


}
