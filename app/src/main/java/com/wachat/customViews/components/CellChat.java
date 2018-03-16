package com.wachat.customViews.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.DateTimeUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Gourav Kundu on 24-08-2015.
 */
public class CellChat extends Cell implements View.OnClickListener, View.OnLongClickListener {
    public void setTranslationClickCallback(int position,TranslationClickCallback _translationClickCallback) {
        this.translationClickCallback = _translationClickCallback;
        this.position = position;
    }

    public void setShowOriginalTextFirst(boolean _showingOriginalTextFirst) {
        this.showingOriginalTextFirst = _showingOriginalTextFirst;
    }

    //
    public interface TranslationClickCallback{
        void onClick(DataTextChat mDataTextChat);
    }
    private Context context;
    public RelativeLayout cell;
    private RelativeLayout cell_chat_rl_translated_text;
//    private RelativeLayout cell_chat_rl_original_text;

    private TextView tv_group_sender_name;
    public TextView tv_Msg;
    public TextView tv_Date;
    public ImageView img_translate;

    private DataTextChat mDataTextChat;

//    private TextView tv_group_sender_name_original;
//    private TextView tv_Msg_original;
//    public ImageView img_translate_original;
//    private TextView tv_Date_original;
    private boolean showingOriginalTextFirst = false;

    int position;
    private TranslationClickCallback translationClickCallback;
    public CellChat(Context context) {
        super(context);
        this.context = context;
    }

    public CellChat(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public CellChat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();


boolean showingOriginal = false;

    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_chat, this, true);
        cell = (RelativeLayout) findViewById(R.id.cell);
        cell_chat_rl_translated_text = (RelativeLayout) findViewById(R.id.cell_chat_rl_translated_text);
//        cell_chat_rl_original_text = (RelativeLayout) findViewById(R.id.cell_chat_rl_original_text);
//        cell.setOnLongClickListener(this);
//        cell.setOnClickListener(this);
        tv_group_sender_name = (TextView) findViewById(R.id.tv_group_sender_name);
        tv_Msg = (TextView) findViewById(R.id.tv_Msg);
        tv_Date = (TextView) findViewById(R.id.tv_Date);
        img_translate = (ImageView) findViewById(R.id.img_translate);
//        img_translate.setOnClickListener(this);


//        tv_group_sender_name_original = (TextView) findViewById(R.id.tv_group_sender_name_original);
//        tv_Msg_original = (TextView) findViewById(R.id.tv_Msg_original);
//        img_translate_original = (ImageView) findViewById(R.id.img_translate_original);
//        tv_Date_original = (TextView) findViewById(R.id.tv_Date_original);
//        img_translate_original.setOnClickListener(this);
        cell_chat_rl_translated_text.setOnClickListener(this);
//        cell_chat_rl_original_text.setOnClickListener(this);
        cell_chat_rl_translated_text.setOnLongClickListener(this);
//        cell_chat_rl_original_text.setOnLongClickListener(this);

    }

    @Override
    public void setUpView(boolean isMine, DataTextChat mDataTextChat) {
        this.mDataTextChat = mDataTextChat;
        // Recognize all of the default link text patterns


        Linkify.addLinks(tv_Msg, Linkify.ALL);
        if (isMine) {
            showingOriginal = true;
//            cell_chat_rl_original_text.setVisibility(GONE);
            cell_chat_rl_translated_text.setVisibility(VISIBLE);
            this.setGravity(Gravity.RIGHT);
            img_translate.setVisibility(GONE);
            tv_Msg.setTextColor(Color.parseColor(color_WHITE));

            tv_Msg.setLinkTextColor(Color.parseColor(color_WHITE));
            tv_Date.setTextColor(Color.parseColor(color_WHITE));
            cell_chat_rl_translated_text.setBackgroundResource((R.drawable.ic_gray_bubble));
//            cell_chat_rl_original_text.setBackgroundResource((R.drawable.ic_gray_bubble));
//            cell.setPadding(15, 15, 15, 15);
            //cell.setPadding(5, 5, 8, 5);
            tv_Msg.setMaxWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 3);
            tv_Msg.setMinWidth(CommonMethods.getScreenWidth(context).widthPixels / 3);
           /* cell.setPadding((int) context.getResources().getDimension(R.dimen.lr_cell),
                    (int) context.getResources().getDimension(R.dimen.lr_cell),
                    (int) context.getResources().getDimension(R.dimen.tb_cell),
                    (int) context.getResources().getDimension(R.dimen.lr_cell));*/

            //set chat message data
            tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));
            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            tv_Date.setText(calCulatedLocTime + " " + amOrPm);
            tv_group_sender_name.setTextColor(Color.parseColor(color_WHITE));
//            tv_group_sender_name_original.setTextColor(Color.parseColor(color_WHITE));
            tv_group_sender_name.setText("");
            tv_group_sender_name.setVisibility(View.GONE);
//            tv_group_sender_name_original.setVisibility(GONE);

        } else {

//            cell_chat_rl_original_text.setVisibility(VISIBLE);
//            cell_chat_rl_translated_text.setVisibility(GONE);
            this.setGravity(Gravity.LEFT);
//            tv_Msg_original.setTextColor(Color.parseColor(color_DARK));
//            tv_Msg_original.setLinkTextColor(Color.parseColor(color_DARK));

            tv_Msg.setTextColor(Color.parseColor(color_DARK));
            tv_Msg.setLinkTextColor(Color.parseColor(color_DARK));

            tv_Date.setTextColor(Color.parseColor(color_DARK));

            cell_chat_rl_translated_text.setBackgroundResource((R.drawable.ic_white_bubble));
//            cell_chat_rl_original_text.setBackgroundResource((R.drawable.ic_white_bubble));

//            cell.setBackgroundResource((R.drawable.ic_white_bubble));
//            cell.setPadding(15, 15, 15, 15);
            //cell.setPadding(8, 5, 5, 5);

//            tv_Msg_original.setMaxWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 3);
//            tv_Msg_original.setMinWidth(CommonMethods.getScreenWidth(context).widthPixels / 4);


            tv_Msg.setMaxWidth((CommonMethods.getScreenWidth(context).widthPixels * 2) / 3);
            tv_Msg.setMinWidth(CommonMethods.getScreenWidth(context).widthPixels / 4);
           /* cell.setPadding((int) context.getResources().getDimension(R.dimen.tb_cell),
                    (int) context.getResources().getDimension(R.dimen.lr_cell),
                    (int) context.getResources().getDimension(R.dimen.lr_cell),
                    (int) context.getResources().getDimension(R.dimen.lr_cell));*/

            //set data
//if any translated messages then set as bubble
//            tv_Msg_original.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));
            if (!StringUtils.isEmpty(mDataTextChat.getStrTranslatedText())) {

                img_translate.setVisibility(VISIBLE);
//                img_translate_original.setVisibility(VISIBLE);

                if(!showingOriginalTextFirst){
                    showingOriginal = false;
                    img_translate.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_bubble_globe_icon));
                    tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getStrTranslatedText()));
//                    cell_chat_rl_translated_text.setVisibility(GONE);
//                    cell_chat_rl_original_text.setVisibility(VISIBLE);
                }else{
                    showingOriginal = true;
                    img_translate.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_bubble_globe_icon_select));
                    tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));
//                    cell_chat_rl_translated_text.setVisibility(VISIBLE);
//                    cell_chat_rl_original_text.setVisibility(GONE);
                }
            } else {
                tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));
                img_translate.setVisibility(GONE);
//                img_translate_original.setVisibility(GONE);
            }
            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            tv_Date.setText(calCulatedLocTime + " " + amOrPm);
//            tv_Date_original.setText(calCulatedLocTime + " " + amOrPm);

            tv_group_sender_name.setTextColor(Color.parseColor(color_ORANGE));
//            tv_group_sender_name_original.setTextColor(Color.parseColor(color_ORANGE));
            if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                String senderName = "";
                senderName = mDataTextChat.getFriendName();
                if (TextUtils.isEmpty(senderName)) {
                    senderName = mDataTextChat.getSenderName();
                }
                tv_group_sender_name.setText(CommonMethods.getUTFDecodedString(senderName));
//                tv_group_sender_name_original.setText(CommonMethods.getUTFDecodedString(senderName));
                tv_group_sender_name.setVisibility(View.VISIBLE);
//                tv_group_sender_name_original.setVisibility(VISIBLE);
            } else {
//                tv_group_sender_name_original.setVisibility(VISIBLE);
                tv_group_sender_name.setText("");
//                tv_group_sender_name_original.setVisibility(GONE);
                tv_group_sender_name.setVisibility(View.GONE);
            }


        }

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
//            case R.id.img_translate:
            case R.id.cell_chat_rl_translated_text:
//                showOrgMessagePopup();
                if (!StringUtils.isEmpty(mDataTextChat.getStrTranslatedText())) {
                    animateToOriginalMessageView();
                }
                break;
//            case R.id.img_translate_original:
//            case R.id.cell_chat_rl_original_text:
////                showOrgMessagePopup();
//                if (!StringUtils.isEmpty(mDataTextChat.getStrTranslatedText())) {
//                    animateToTranslatedMessageView();
//                }
//                break;
        }


    }

//    private void animateToTranslatedMessageView() {
//
//        flipit(cell_chat_rl_original_text,cell_chat_rl_translated_text);
////        cell_chat_rl_translated_text.setVisibility(VISIBLE);
////        cell_chat_rl_original_text.setVisibility(GONE);
//        translationClickCallback.onClick(position);
//    }

    private void animateToOriginalMessageView() {
//        flipit(cell_chat_rl_translated_text,cell_chat_rl_translated_text);

        if(!showingOriginal){
            showingOriginal = true;
//                if(tv_Msg.getText().toString().equals(CommonMethods.getUTFDecodedString(mDataTextChat.getStrTranslatedText()))){
            tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));
            img_translate.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_bubble_globe_icon_select));
        }else{
            showingOriginal = false;
            tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getStrTranslatedText()));
            img_translate.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_bubble_globe_icon));
        }

        translationClickCallback.onClick(mDataTextChat);
    }


    private void flipit(final View currentVisibleView, final View currentInvisibleView) {
//        final ListView visibleList;
//        final ListView invisibleList;
//        if (mEnglishList.getVisibility() == View.GONE) {
//            visibleList = mFrenchList;
//            invisibleList = mEnglishList;
//        } else {
//            invisibleList = mFrenchList;
//            visibleList = mEnglishList;
//        }
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(currentVisibleView, "rotationX", 0f, 360f);
        visToInvis.setDuration(1000);

        final ObjectAnimator visToInvis2 = ObjectAnimator.ofFloat(currentVisibleView, "rotationX", 180f, 360f);
        visToInvis2.setDuration(1000);
//        visToInvis.setInterpolator(accelerator);
//        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(currentInvisibleView, "rotationX",
//                -90f, 0f);
//        invisToVis.setDuration(500);
//        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator anim) {
//                currentVisibleView.setVisibility(View.GONE);
//                invisToVis.start();
//                currentInvisibleView.setVisibility(View.VISIBLE);
                if(!showingOriginal){
                    showingOriginal = true;
//                if(tv_Msg.getText().toString().equals(CommonMethods.getUTFDecodedString(mDataTextChat.getStrTranslatedText()))){
                    tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));
                    img_translate.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_bubble_globe_icon_select));
                }else{
                    showingOriginal = false;
                    tv_Msg.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getStrTranslatedText()));
                    img_translate.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_bubble_globe_icon));
                }

//                visToInvis2.start();
            }
        });
        visToInvis.start();
    }

    public void showOrgMessagePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setMessage(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));
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
            longClickListener.onCellItemLongClick(v, mDataTextChat);
        }
        return false;
    }
}
