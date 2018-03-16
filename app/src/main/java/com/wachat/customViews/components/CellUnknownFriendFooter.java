package com.wachat.customViews.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.data.DataTextChat;

public class CellUnknownFriendFooter extends Cell implements View.OnClickListener{
    //
    public TextView tv_cell_footer_reject;
    public RelativeLayout cell;
    private Context context;
    private TextView tv_cell_footer_accept;

    public CellUnknownFriendFooter(Context context) {
        super(context);
        this.context = context;
    }

    public CellUnknownFriendFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public CellUnknownFriendFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_footer, this, true);
        cell = (RelativeLayout) findViewById(R.id.cell);
        tv_cell_footer_accept = (TextView)findViewById(R.id.tv_cell_footer_accept);
        tv_cell_footer_reject = (TextView) findViewById(R.id.tv_cell_footer_reject);
//        cell.setLayoutParams(new LayoutParams(width, width));
        tv_cell_footer_accept.setOnClickListener(this);
        tv_cell_footer_reject.setOnClickListener(this);
    }

    @Override
    public void setUpView(boolean isMine, DataTextChat mDataTextChat) {
        // Recognize all of the default link text patterns



            this.setGravity(Gravity.CENTER);
            //cell.setPadding(5, 5, 8, 5);
//            tv_Msg.setMaxWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 3);
//            tv_Msg.setMinWidth(CommonMethods.getScreenWidth(context).widthPixels / 3);
//           /* cell.setPadding((int) context.getResources().getDimension(R.dimen.lr_cell),
//                    (int) context.getResources().getDimension(R.dimen.lr_cell),
//                    (int) context.getResources().getDimension(R.dimen.tb_cell),
//                    (int) context.getResources().getDimension(R.dimen.lr_cell));*/
//
//            //set chat message data
//            tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));
//            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
//                    getResources().getConfiguration().locale), " ")[1];
//            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
//                    getResources().getConfiguration().locale), " ")[2];
//            tv_Date.setText(calCulatedLocTime + " " + amOrPm);
//            tv_group_sender_name.setTextColor(Color.parseColor(color_WHITE));
//            tv_group_sender_name.setText("");
//            tv_group_sender_name.setVisibility(View.GONE);


    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
//        if(gravity == Gravity.LEFT){
//            tv_Msg.setGravity(Gravity.LEFT);
//        }else{
//            tv_Msg.setGravity(Gravity.RIGHT);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cell_footer_accept:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), null);
                }
                break;
            case R.id.tv_cell_footer_reject:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), null);
                }
                break;
        }
    }


}
