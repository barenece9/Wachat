package com.wachat.customViews.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;

/**
 * Created by Gourav Kundu on 24-08-2015.
 */
public class CellGroupNotification extends Cell{
    private Context context;
    public RelativeLayout cell;
    public TextView tv_Msg;

    public CellGroupNotification(Context context) {
        super(context);
        this.context = context;
    }

    public CellGroupNotification(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public CellGroupNotification(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }



    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_group_notification, this, true);
        tv_Msg = (TextView) findViewById(R.id.tv_Msg);
    }

    @Override
    public void setUpView(boolean isMine, DataTextChat mDataTextChat) {
//        this.mDataTextChat = mDataTextChat;
            this.setGravity(Gravity.CENTER);
            tv_Msg.setTextColor(Color.parseColor(color_WHITE));

            tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));

    }

}
