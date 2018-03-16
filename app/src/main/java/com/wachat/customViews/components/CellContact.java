package com.wachat.customViews.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.activity.ActivityAddToContact;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.ChatRoundedImageView;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.DateTimeUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Gourav Kundu on 28-08-2015.
 */
public class CellContact extends Cell implements View.OnClickListener, View.OnLongClickListener {

    public RelativeLayout cellContact;
    private ChatRoundedImageView iv_Contact;
    private TextView tv_Contact, tv_Date;
    private Context context;
    DataTextChat mDataTextChat;
    private TextView tv_group_sender_name;

    public CellContact(Context context) {
        super(context);
        this.context = context;

    }

    public CellContact(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellContact(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_contact, this, true);
        cellContact = (RelativeLayout) findViewById(R.id.cellContact);
        cellContact.setOnClickListener(this);
        tv_group_sender_name = (TextView) findViewById(R.id.tv_group_sender_name);
        iv_Contact = (ChatRoundedImageView) findViewById(R.id.iv_Contact);
        tv_Contact = (TextView) findViewById(R.id.tv_Contact);
        tv_Date = (TextView) findViewById(R.id.tv_Date);
        cellContact.setOnLongClickListener(this);
    }

    public void setUpView(boolean isMine, DataTextChat mDataTextChat) {

        this.mDataTextChat = mDataTextChat;
        if (isMine) {
            this.setGravity(Gravity.RIGHT);
            tv_Contact.setTextColor(Color.parseColor(color_WHITE));
            tv_Date.setTextColor(Color.parseColor(color_WHITE));
            cellContact.setBackgroundResource((R.drawable.ic_gray_bubble));
            cellContact.setPadding(15, 15, 18, 15);
            tv_Contact.setMaxWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 5);
            tv_Contact.setMinWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 7);
            tv_Contact.setGravity(Gravity.CENTER_VERTICAL);
//            set data to view

//            ImageLoader.getInstance().

            if (!TextUtils.isEmpty(mDataTextChat.getAttachBase64str4Img())) {
                iv_Contact.setImageBitmap(CommonMethods.decodeBase64(mDataTextChat.getAttachBase64str4Img()));
            } else {
                iv_Contact.setImageBitmap(BitmapFactory.
                        decodeResource(context.getResources(), R.drawable.ic_chats_noimage_profile));
            }

            tv_Contact.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getAttachContactName()));
            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            tv_Date.setText(calCulatedLocTime + " " + amOrPm);
            tv_group_sender_name.setTextColor(Color.parseColor(color_WHITE));
            tv_group_sender_name.setText("");
            tv_group_sender_name.setVisibility(View.GONE);

        } else {
            this.setGravity(Gravity.LEFT);
            tv_Contact.setTextColor(Color.parseColor(color_DARK));
            tv_Date.setTextColor(Color.parseColor(color_DARK));
            cellContact.setBackgroundResource((R.drawable.ic_white_bubble));
            cellContact.setPadding(18, 15, 15, 15);
            tv_Contact.setMaxWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 5);
            tv_Contact.setMinWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 7);
            tv_Contact.setGravity(Gravity.CENTER_VERTICAL);

//            set data to view
            if (!TextUtils.isEmpty(mDataTextChat.getAttachBase64str4Img())) {
                iv_Contact.setImageBitmap(CommonMethods.decodeBase64(mDataTextChat.getAttachBase64str4Img()));
            } else {
                iv_Contact.setImageBitmap(BitmapFactory.
                        decodeResource(context.getResources(), R.drawable.ic_chats_noimage_profile));
            }

            tv_Contact.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getAttachContactName()));
            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            tv_Date.setText(calCulatedLocTime + " " + amOrPm);

            tv_group_sender_name.setTextColor(Color.parseColor(color_ORANGE));
            if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                String senderName = "";
                senderName = mDataTextChat.getFriendName();
                if (TextUtils.isEmpty(senderName)) {
                    senderName = mDataTextChat.getSenderName();
                }
                tv_group_sender_name.setText(CommonMethods.getUTFDecodedString(senderName));
                tv_group_sender_name.setVisibility(View.VISIBLE);
            } else {
                tv_group_sender_name.setText("");
                tv_group_sender_name.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cellContact:

                Intent intent = new Intent(context, ActivityAddToContact.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(Constants.B_RESULT, mDataTextChat);
                intent.putExtras(mBundle);
                context.startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (longClickListener != null) {
            longClickListener.onCellItemLongClick(v, mDataTextChat);
        }
        return false;
    }
}
