package com.wachat.customViews.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.wachat.application.AppVhortex;
import com.wachat.data.DataTextChat;

/**
 * Created by Gourav Kundu on 28-08-2015.
 */
public abstract class Cell extends LinearLayout {
    public interface OnCellItemClickListener{
        void onCellItemClick(int viewId, DataTextChat dataItem);
    }
    public interface OnCellItemLongClickListener{
        void onCellItemLongClick(View v, DataTextChat dataItem);
    }
    protected String color_DARK = "#806C61", color_WHITE = "#FFFFFF", color_ORANGE = "#e27408";
    protected int width;
    protected OnCellItemClickListener clickListener;
    protected OnCellItemLongClickListener longClickListener;
    public Cell(Context context) {
        super(context);
        initComponent();
    }

    public Cell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponent();
    }

    public Cell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponent();
    }

    protected void initComponent() {
        width = (int) (AppVhortex.width * 0.7f);

    }

    public void setUpClickListener(OnCellItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public void setUpLongPressListener(OnCellItemLongClickListener longClickListener){
        this.longClickListener = longClickListener;

    }
    public abstract void setUpView(boolean isMine , DataTextChat mDataTextChat);

}
