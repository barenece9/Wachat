package com.wachat.customViews.tooltippopupwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.wachat.R;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.DataTextChat;

public class TooltipPopUpWindow extends PopupWindow {

    private TooltipItemClickListener itemClickListener;
    private OnDismissListener onDismissListener;
    private DataTextChat dataItem;
    public TooltipPopUpWindow(Context context,
                              TooltipItemClickListener itemClickListener,
                              OnDismissListener onDismissListener, DataTextChat dataItem) {
        super(((LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.menu_tooltip, null), LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        this.itemClickListener = itemClickListener;
        this.onDismissListener = onDismissListener;

        this.dataItem = dataItem;

        RelativeLayout rlRoot = (RelativeLayout) getContentView().findViewById(R.id.menu_tooltip_root);
        rlRoot.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        rlRool.setGravity(Gravity.RIGHT);
//        LinearLayout menu_tooltip_ll_wrap = (LinearLayout) getContentView().findViewById(R.id.menu_tooltip_ll_wrap);
//        menu_tooltip_ll_wrap.setGravity(Gravity.RIGHT);
        if(dataItem.getMessageType().equalsIgnoreCase(ConstantChat.MESSAGE_TYPE)){
            getContentView().findViewById(R.id.menu_tooltip_copy).setVisibility(View.VISIBLE);
        }else{
            getContentView().findViewById(R.id.menu_tooltip_copy).setVisibility(View.GONE);
        }
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setFocusable(true);

        setTouchable(true);

        setListeners();

    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void setItemBackGroundDrawable(Drawable bgDrawable) {
//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//			getContentView().findViewById(R.id.filter_all)
//					.setBackgroundDrawable(bgDrawable);
//			getContentView().findViewById(R.id.filter_open)
//					.setBackgroundDrawable(bgDrawable);
//			getContentView().findViewById(R.id.filter_close)
//					.setBackgroundDrawable(bgDrawable);
//		} else {
//			getContentView().findViewById(R.id.filter_all).setBackground(
//					bgDrawable);
//			getContentView().findViewById(R.id.filter_open).setBackground(
//					bgDrawable);
//			getContentView().findViewById(R.id.filter_close).setBackground(
//					bgDrawable);
//		}

    }

    private void setListeners() {
        setOnDismissListener(onDismissListener);

		getContentView().findViewById(R.id.menu_tooltip_delete).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						onFilterItemClick(v);
					}
				});
		getContentView().findViewById(R.id.menu_tooltip_copy).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						onFilterItemClick(v);
					}
				});
		getContentView().findViewById(R.id.menu_tooltip_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						onFilterItemClick(v);
					}
				});

    }

    private void onFilterItemClick(View v) {
        itemClickListener.onTooltipItemClick(v.getId());
        this.dismiss();
    }


}
