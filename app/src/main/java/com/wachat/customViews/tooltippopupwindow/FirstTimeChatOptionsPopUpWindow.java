package com.wachat.customViews.tooltippopupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.wachat.R;
import com.wachat.application.AppVhortex;

public class FirstTimeChatOptionsPopUpWindow extends PopupWindow implements View.OnClickListener {

    public static final String FIRST_TIME_CHAT_OPTION_CLASSMATE = "Classmate";
    public static final String FIRST_TIME_CHAT_OPTION_FRIEND = "Friend";
    public static final String FIRST_TIME_CHAT_OPTION_ACQUAINTANCE = "Acquaintance";
    public static final String FIRST_TIME_CHAT_OPTION_CO_WORKER = "Co-Worker";
    public static final String FIRST_TIME_CHAT_OPTION_COLLEAGUE = "Colleague";
    public static final String FIRST_TIME_CHAT_OPTION_OTHER = "Other";

    public interface FirstTimeChatOptionsSelectionCallBack{
        void onOptionSelected(String selectedOption);
    }
    private FirstTimeChatOptionsSelectionCallBack itemClickListener;
    private RelativeLayout rel_classmate;
    private ImageView iv_classmate;
    private RelativeLayout rel_friend;
    private ImageView iv_friend;
    private RelativeLayout rel_acquaintance;
    private ImageView iv_acquaintance;
    private RelativeLayout rel_coworker;
    private ImageView iv_coworker;
    private RelativeLayout rel_colleague;
    private ImageView iv_colleague;
    private RelativeLayout rel_other;
    private ImageView iv_other;

    int currentSelection = R.id.rel_classmate;

    public FirstTimeChatOptionsPopUpWindow(Context context,
                                           FirstTimeChatOptionsSelectionCallBack itemClickListener) {
        super(((LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.include_chat_dropdown, null), LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        this.itemClickListener = itemClickListener;


        int width = (int) (AppVhortex.width * 0.7f);
        LinearLayout rlRoot = (LinearLayout) getContentView().findViewById(R.id.lnr_main);
        rlRoot.setLayoutParams(new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));


        initViews();
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setFocusable(true);

        setTouchable(true);


    }

    private void initViews() {

        rel_classmate = (RelativeLayout) getContentView().findViewById(R.id.rel_classmate);
        rel_classmate.setOnClickListener(this);
        iv_classmate = (ImageView) getContentView().findViewById(R.id.iv_classmate);
        rel_friend = (RelativeLayout) getContentView().findViewById(R.id.rel_friend);
        rel_friend.setOnClickListener(this);
        iv_friend = (ImageView) getContentView().findViewById(R.id.iv_friend);
        rel_acquaintance = (RelativeLayout) getContentView().findViewById(R.id.rel_acquaintance);
        rel_acquaintance.setOnClickListener(this);
        iv_acquaintance = (ImageView) getContentView().findViewById(R.id.iv_acquaintance);
        rel_coworker = (RelativeLayout) getContentView().findViewById(R.id.rel_coworker);
        rel_coworker.setOnClickListener(this);
        iv_coworker = (ImageView) getContentView().findViewById(R.id.iv_coworker);
        rel_colleague = (RelativeLayout) getContentView().findViewById(R.id.rel_colleague);
        rel_colleague.setOnClickListener(this);
        iv_colleague = (ImageView) getContentView().findViewById(R.id.iv_colleague);
        rel_other = (RelativeLayout) getContentView().findViewById(R.id.rel_other);
        rel_other.setOnClickListener(this);
        iv_other = (ImageView) getContentView().findViewById(R.id.iv_other);
    }


    public void setSelection(int selectedViewId){
        this.currentSelection = selectedViewId;
        setCheckBoxSelection(currentSelection);
    }



    private void setCheckBoxSelection(int selectedViewId) {
        switch (selectedViewId) {
            case R.id.rel_classmate:
                iv_classmate.setSelected(true);
                iv_friend.setSelected(false);
                iv_acquaintance.setSelected(false);
                iv_coworker.setSelected(false);
                iv_colleague.setSelected(false);
                iv_other.setSelected(false);
                itemClickListener.onOptionSelected(FIRST_TIME_CHAT_OPTION_CLASSMATE);
                break;
            case R.id.rel_friend:
                iv_classmate.setSelected(false);
                iv_friend.setSelected(true);
                iv_acquaintance.setSelected(false);
                iv_coworker.setSelected(false);
                iv_colleague.setSelected(false);
                iv_other.setSelected(false);
                itemClickListener.onOptionSelected(FIRST_TIME_CHAT_OPTION_FRIEND);
                break;
            case R.id.rel_acquaintance:
                iv_classmate.setSelected(false);
                iv_friend.setSelected(false);
                iv_acquaintance.setSelected(true);
                iv_coworker.setSelected(false);
                iv_colleague.setSelected(false);
                iv_other.setSelected(false);
                itemClickListener.onOptionSelected(FIRST_TIME_CHAT_OPTION_ACQUAINTANCE);
                break;
            case R.id.rel_coworker:
                iv_classmate.setSelected(false);
                iv_friend.setSelected(false);
                iv_acquaintance.setSelected(false);
                iv_coworker.setSelected(true);
                iv_colleague.setSelected(false);
                iv_other.setSelected(false);
                itemClickListener.onOptionSelected(FIRST_TIME_CHAT_OPTION_CO_WORKER);
                break;
            case R.id.rel_colleague:
                iv_classmate.setSelected(false);
                iv_friend.setSelected(false);
                iv_acquaintance.setSelected(false);
                iv_coworker.setSelected(false);
                iv_colleague.setSelected(true);
                iv_other.setSelected(false);
                itemClickListener.onOptionSelected(FIRST_TIME_CHAT_OPTION_COLLEAGUE);
                break;
            case R.id.rel_other:
                iv_classmate.setSelected(false);
                iv_friend.setSelected(false);
                iv_acquaintance.setSelected(false);
                iv_coworker.setSelected(false);
                iv_colleague.setSelected(false);
                iv_other.setSelected(true);
                itemClickListener.onOptionSelected(FIRST_TIME_CHAT_OPTION_OTHER);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        setCheckBoxSelection(v.getId());

        this.dismiss();
    }
}
