package com.wachat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.async.AsyncBlockUserFromChat;
import com.wachat.async.AsyncUnFavoriteUserFromChat;
import com.wachat.callBack.CallbackFromDialog;
import com.wachat.data.DataTextChat;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.TableContact;
import com.wachat.util.CommonMethods;
import com.wachat.util.Prefs;

import java.util.ArrayList;


public class DialogMore extends Dialog implements
        View.OnClickListener {
    DataTextChat mDataTextChat;
    TextView tv_clear_conversation, tv_email_conversation, tv_block;
    Prefs mPrefs;
    private Context context;
    private String txt;
    private AsyncUnFavoriteUserFromChat asyncUnFavoriteUserFromChat;
    private AsyncBlockUserFromChat blockUserFromChat;
    private CallbackFromDialog mCallbackFromDialog;

    public DialogMore(Context context, DataTextChat mDataTextChat, CallbackFromDialog mCallbackFromDialog) {
        super(context);
        this.context = context;
        this.mDataTextChat = mDataTextChat;
        this.mCallbackFromDialog = mCallbackFromDialog;
        mPrefs = Prefs.getInstance(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        ViewGroup.LayoutParams mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
//                .MATCH_PARENT, CommonMethods.getScreenWidth(context).widthPixels / 2);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_more_dashboard);
        // this.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;

        initView();
        initClickListener();
    }

    private void initView() {
        tv_clear_conversation = (TextView) findViewById(R.id.tv_clear_conversation);
        tv_email_conversation = (TextView) findViewById(R.id.tv_email_conversation);
        tv_block = (TextView) findViewById(R.id.tv_block);

        if (mDataTextChat.getIsBlocked()>0){
            tv_block.setText("Unblock");
        }else{
            tv_block.setText("Block");
        }

    }

    private void initClickListener() {
        tv_clear_conversation.setOnClickListener(this);
        tv_email_conversation.setOnClickListener(this);
        tv_block.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_clear_conversation:
                //CommonMethods.MYToast(context, "conversation cleared");
                mCallbackFromDialog.OnClickClearConversation();
                dismiss();
                break;

            case R.id.tv_email_conversation:
                //  CommonMethods.MYToast(context, "conversation emailed");
                mCallbackFromDialog.OnClickEmail();
                dismiss();
                break;
            case R.id.tv_block:


                if (mDataTextChat.getIsFavorite() == 1) {
                    DialogBlockFavouriteAlert mDialogBlockUser = new DialogBlockFavouriteAlert(getContext(),
                            getContext().getResources().getString(R.string.alert_warn_friend_will_be_unfavourite_once_block),
                            true, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    unFavourite();
                                    break;
                            }
                        }
                    });
                    mDialogBlockUser.show();

                } else {
                    blockUser();
                }
                //   call webservice block..check isFav first
//                if (mDataTextChat.getIsFavorite() > 0) {
//                    unFavourite();
//
//                } else {
//                    blockUser();
//                }

                dismiss();
                break;
            default:
                break;
        }
    }


    private void unFavourite() {
        asyncUnFavoriteUserFromChat = new AsyncUnFavoriteUserFromChat(mPrefs.getUserId(), mDataTextChat.getFriendChatID(), String.valueOf(mDataTextChat.getIsFavorite()), new InterfaceResponseCallback() {
            @Override
            public void onResponseObject(Object mObject) {
                mDataTextChat.setIsFavorite(0);
                new TableContact(context).updateBlockFromChat(mDataTextChat);
                blockUser();
                mCallbackFromDialog.OnClickBlock();
            }

            @Override
            public void onResponseList(ArrayList<?> mList) {

            }

            @Override
            public void onResponseFaliure(String responseText) {

            }
        });
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            asyncUnFavoriteUserFromChat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncUnFavoriteUserFromChat.execute();
        }
    }


    private void blockUser() {

        blockUserFromChat = new AsyncBlockUserFromChat(context, mPrefs.getUserId(), mDataTextChat.getFriendChatID(), String.valueOf(mDataTextChat.getIsBlocked()), new InterfaceResponseCallback() {
            @Override
            public void onResponseObject(Object mObject) {
                if(mDataTextChat.getIsBlocked()>0){
                mDataTextChat.setIsBlocked(0);
                } else{
                    mDataTextChat.setIsBlocked(1);
                }
                new TableContact(context).updateBlockFromChat(mDataTextChat);

            }

            @Override
            public void onResponseList(ArrayList<?> mList) {

            }

            @Override
            public void onResponseFaliure(String responseText) {

            }
        });
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            blockUserFromChat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            blockUserFromChat.execute();
        }
    }
}
