package com.wachat.customViews.components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.wachat.R;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.tooltippopupwindow.FirstTimeChatOptionsPopUpWindow;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.DateTimeUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Gourav Kundu on 28-08-2015.
 */
public class CellSticker extends Cell implements View.OnClickListener, View.OnLongClickListener {


    public RelativeLayout cell_sticker;
    private ImageView cell_sticker_iv, img_translate;
    private TextView cell_sticker_tv_Date, cell_tv_Msg;
    private DataTextChat imageChat;
    private DisplayImageOptions options;
    private ImageLoader mImageLoader;
    private Context mContext;
    private TextView tv_group_sender_name;

    public CellSticker(Context context) {
        super(context);
        this.mContext = context;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100))
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        this.mImageLoader = ImageLoader.getInstance();
        this.mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public CellSticker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellSticker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_sticker, this, true);
        cell_sticker = (RelativeLayout) findViewById(R.id.cell_sticker);
        cell_sticker_iv = (ImageView) findViewById(R.id.cell_sticker_iv);
        cell_sticker_tv_Date = (TextView) findViewById(R.id.cell_sticker_tv_Date);
        tv_group_sender_name = (TextView) findViewById(R.id.tv_group_sender_name);

        cell_tv_Msg = (TextView) findViewById(R.id.tv_Msg);
        img_translate = (ImageView) findViewById(R.id.img_translate);
//        img_translate.setOnClickListener(this);
        cell_sticker.setLayoutParams(new LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT));
        int marginPixel = CommonMethods.dpToPx(getContext(),20);
        cell_sticker_iv.setLayoutParams(new RelativeLayout.LayoutParams(width-marginPixel, width-marginPixel));
        cell_sticker.setOnClickListener(this);

        cell_sticker.setOnLongClickListener(this);

    }

    private int getStickerPath(String stickerName) {
        if(stickerName.equalsIgnoreCase(FirstTimeChatOptionsPopUpWindow.FIRST_TIME_CHAT_OPTION_ACQUAINTANCE)){
            return R.drawable.ic_sticker_acquiantance;
        }else if(stickerName.equalsIgnoreCase(FirstTimeChatOptionsPopUpWindow.FIRST_TIME_CHAT_OPTION_CLASSMATE)){
            return R.drawable.ic_sticker_classmates;
        }else if(stickerName.equalsIgnoreCase(FirstTimeChatOptionsPopUpWindow.FIRST_TIME_CHAT_OPTION_CO_WORKER)
        || stickerName.equalsIgnoreCase("CoWorker")
                ||stickerName.equalsIgnoreCase("Co-Worker")
                || stickerName.equalsIgnoreCase("Co-worker")){
            return R.drawable.ic_sticker_coworkers;
        }else if(stickerName.equalsIgnoreCase(FirstTimeChatOptionsPopUpWindow.FIRST_TIME_CHAT_OPTION_COLLEAGUE)){
            return R.drawable.ic_sticker_colleague;
        }else if(stickerName.equalsIgnoreCase(FirstTimeChatOptionsPopUpWindow.FIRST_TIME_CHAT_OPTION_FRIEND)){
            return R.drawable.ic_sticker_friends;
        }else if(stickerName.equalsIgnoreCase(FirstTimeChatOptionsPopUpWindow.FIRST_TIME_CHAT_OPTION_OTHER)){
            return R.drawable.ic_sticker_others;
        }

        return 0;
    }

    @Override
    public void setUpView(boolean isMine, DataTextChat imageChat) {
        this.imageChat = imageChat;
        if (isMine) {
            this.setGravity(Gravity.RIGHT);
            cell_sticker_tv_Date.setTextColor(Color.parseColor(color_DARK));
            ImageLoader.getInstance().displayImage("drawable://" + getStickerPath(imageChat.getFilePath()), cell_sticker_iv);
            cell_sticker.setPadding(15, 15, 15, 15);
            cell_tv_Msg.setTextColor(Color.parseColor(color_DARK));
            img_translate.setVisibility(GONE);
            if (TextUtils.isEmpty(imageChat.getBody()))
                cell_tv_Msg.setVisibility(GONE);
            else
                cell_tv_Msg.setVisibility(VISIBLE);
            cell_tv_Msg.setText(CommonMethods.getUTFDecodedString(imageChat.getBody()));
            //set chat message data
            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(imageChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(imageChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            cell_sticker_tv_Date.setText(calCulatedLocTime + " " + amOrPm);
            tv_group_sender_name.setTextColor(Color.parseColor(color_WHITE));
            tv_group_sender_name.setText("");
            tv_group_sender_name.setVisibility(View.GONE);


        } else {
            //recieve message bubble
            this.setGravity(Gravity.LEFT);
            cell_sticker_tv_Date.setTextColor(Color.parseColor(color_DARK));
            cell_sticker.setPadding(15, 15, 15, 15);

            cell_tv_Msg.setTextColor(Color.parseColor(color_DARK));


            if (TextUtils.isEmpty(imageChat.getBody()) && TextUtils.isEmpty(imageChat.getStrTranslatedText()))
                cell_tv_Msg.setVisibility(GONE);
            else
                cell_tv_Msg.setVisibility(VISIBLE);


            cell_tv_Msg.setText(CommonMethods.getUTFDecodedString(imageChat.getBody()));
            //if any translated messages then set as bubble
            //Dont show translation for sticker 2016-08-17
//            if (!StringUtils.isEmpty(imageChat.getStrTranslatedText())) {
//                cell_tv_Msg.setText(CommonMethods.getUTFDecodedString(imageChat.getStrTranslatedText()));
//                img_translate.setVisibility(VISIBLE);
//            } else {
//                cell_tv_Msg.setText(CommonMethods.getUTFDecodedString(imageChat.getBody()));
//                img_translate.setVisibility(GONE);
//            }

            ImageLoader.getInstance().displayImage("drawable://" + getStickerPath(imageChat.getFilePath()), cell_sticker_iv);


            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(imageChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(imageChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            cell_sticker_tv_Date.setText(calCulatedLocTime + " " + amOrPm);

            tv_group_sender_name.setTextColor(Color.parseColor(color_ORANGE));
            if (imageChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                String senderName = "";
                senderName = imageChat.getFriendName();
                if (TextUtils.isEmpty(senderName)) {
                    senderName = imageChat.getSenderName();
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
            case R.id.cellPhoto:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), imageChat);
                }
                break;
            case R.id.cell_photo_download:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), imageChat);
                }
                break;
            case R.id.cell_photo_cross:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), imageChat);
                }
                break;
            case R.id.cell_photo_retry:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), imageChat);
                }
                break;

            case R.id.cell_photo_mask:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), imageChat);
                }
                break;
            case R.id.img_translate:
                showOrgMessagePopup();
                break;
            default:
                break;
        }
    }


    public void showOrgMessagePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.app_name));
        builder.setMessage(CommonMethods.getUTFDecodedString(imageChat.getBody()));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.setCancelable(false);

        builder.show();
    }

    @Override
    public boolean onLongClick(View v) {
        if (longClickListener != null) {
            longClickListener.onCellItemLongClick(v, imageChat);
        }
        return false;
    }
}
