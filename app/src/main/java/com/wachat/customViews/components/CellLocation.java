package com.wachat.customViews.components;

import android.content.Context;
import android.content.Intent;
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
import com.wachat.activity.ActivityViewLocation;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.DateTimeUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Gourav Kundu on 28-08-2015.
 */
public class CellLocation extends Cell implements View.OnClickListener, View.OnLongClickListener {

    public RelativeLayout cellContact;
    private TextView tv_address, tv_Date, loc_view_location_btn;
    private Context context;
    private DataTextChat mDataTextChat;
    private TextView tv_group_sender_name;

    public CellLocation(Context context) {
        super(context);
        this.context = context;
    }

    public CellLocation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellLocation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_location, this, true);
        cellContact = (RelativeLayout) findViewById(R.id.cellLocation);
        tv_address = (TextView) findViewById(R.id.tv_loc);
        tv_group_sender_name = (TextView)findViewById(R.id.tv_group_sender_name);
        tv_Date = (TextView) findViewById(R.id.tv_Date);
        loc_view_location_btn = (TextView) findViewById(R.id.loc_view_location_btn);
        loc_view_location_btn.setOnClickListener(this);
        cellContact.setOnLongClickListener(this);
    }

    public void setUpView(boolean isMine, DataTextChat mDataTextChat) {
        this.mDataTextChat=mDataTextChat;
        if (isMine) {
            this.setGravity(Gravity.RIGHT);
//            tv_address.setTextColor(Color.parseColor(color_WHITE));
//            tv_Date.setTextColor(Color.parseColor(color_WHITE));
            cellContact.setBackgroundResource((R.drawable.ic_gray_bubble));
            cellContact.setPadding(15, 15, 15, 15);
            tv_address.setMaxWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 3);
            tv_address.setMinWidth(CommonMethods.getScreenWidth(context).widthPixels / 3);
//            set data to view


            tv_address.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getLoc_address()));
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
//            tv_address.setTextColor(Color.parseColor(color_DARK));
//            tv_Date.setTextColor(Color.parseColor(color_DARK));
            cellContact.setBackgroundResource((R.drawable.ic_white_bubble));
            cellContact.setPadding(15, 15, 15, 15);
            tv_address.setMaxWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 3);
            tv_address.setMinWidth(CommonMethods.getScreenWidth(context).widthPixels / 3);

//            set data to view
            tv_address.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getLoc_address()));
            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            tv_Date.setText(calCulatedLocTime + " " + amOrPm);


            tv_group_sender_name.setTextColor(Color.parseColor(color_ORANGE));
            if(mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                String senderName = "";
                senderName = mDataTextChat.getFriendName();
                if(TextUtils.isEmpty(senderName)){
                    senderName = mDataTextChat.getSenderName();
                }
                tv_group_sender_name.setText(CommonMethods.getUTFDecodedString(senderName));
                tv_group_sender_name.setVisibility(View.VISIBLE);
            }else{
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
            case R.id.loc_view_location_btn:
                Intent mIntent = new Intent(context , ActivityViewLocation.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(Constants.B_RESULT , mDataTextChat);
                mIntent.putExtras(mBundle);
                context.startActivity(mIntent);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(longClickListener!=null) {
            longClickListener.onCellItemLongClick(v, mDataTextChat);
        }
        return false;
    }
}
