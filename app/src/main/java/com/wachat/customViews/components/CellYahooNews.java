package com.wachat.customViews.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.wachat.R;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.DateTimeUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Gourav Kundu on 28-08-2015.
 */
public class CellYahooNews extends Cell implements View.OnClickListener, View.OnLongClickListener {


    public RelativeLayout cell_yahoo_news;
    private ImageView cell_yahoo_news_iv_image;
    private TextView cell_yahoo_news_tv_title, cell_yahoo_news_tv_desc, cell_yahoo_news_tv_time;
    private DisplayImageOptions options;
    private ImageLoader mImageLoader;
    private DataTextChat yahooChat;
    private TextView tv_group_sender_name;

    public CellYahooNews(Context context) {
        super(context);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.transluscent_bg)
                .showImageOnFail(R.drawable.transluscent_bg)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(1000))
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .build();
        this.mImageLoader = ImageLoader.getInstance();
        this.mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public CellYahooNews(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellYahooNews(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_yahoo_news, this, true);
        cell_yahoo_news = (RelativeLayout) findViewById(R.id.cell_yahoo_news);
        tv_group_sender_name = (TextView)findViewById(R.id.tv_group_sender_name);
        cell_yahoo_news_iv_image = (ImageView) findViewById(R.id.cell_yahoo_news_iv_image);
        cell_yahoo_news_tv_title = (TextView) findViewById(R.id.cell_yahoo_news_tv_title);
        cell_yahoo_news_tv_desc = (TextView) findViewById(R.id.cell_yahoo_news_tv_desc);
        cell_yahoo_news_tv_time = (TextView) findViewById(R.id.cell_yahoo_news_tv_time);
        cell_yahoo_news.setLayoutParams(new LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        cell_yahoo_news.setOnClickListener(this);
        cell_yahoo_news.setOnLongClickListener(this);
    }

    @Override
    public void setUpView(boolean isMine, DataTextChat yahooChat) {
        this.yahooChat = yahooChat;
        if (isMine) {
            this.setGravity(Gravity.RIGHT);
            cell_yahoo_news.setBackgroundResource((R.drawable.ic_gray_bubble));
            cell_yahoo_news.setPadding(15, 15, 15, 15);
            cell_yahoo_news_tv_desc.setTextColor(Color.parseColor(color_WHITE));
            cell_yahoo_news_tv_time.setTextColor(Color.parseColor(color_WHITE));
            cell_yahoo_news_tv_title.setTextColor(Color.parseColor(color_WHITE));

            tv_group_sender_name.setTextColor(Color.parseColor(color_WHITE));
            tv_group_sender_name.setText("");
            tv_group_sender_name.setVisibility(View.GONE);
        } else {
            this.setGravity(Gravity.LEFT);
            cell_yahoo_news.setBackgroundResource((R.drawable.ic_white_bubble));
            cell_yahoo_news.setPadding(15, 15, 15, 15);
            cell_yahoo_news_tv_desc.setTextColor(Color.parseColor(color_DARK));
            cell_yahoo_news_tv_time.setTextColor(Color.parseColor(color_DARK));
            cell_yahoo_news_tv_title.setTextColor(Color.parseColor(color_DARK));

            tv_group_sender_name.setTextColor(Color.parseColor(color_ORANGE));
            if(yahooChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                String senderName = "";
                senderName = yahooChat.getFriendName();
                if(TextUtils.isEmpty(senderName)){
                    senderName = yahooChat.getSenderName();
                }
                tv_group_sender_name.setText(CommonMethods.getUTFDecodedString(senderName));
                tv_group_sender_name.setVisibility(View.VISIBLE);
            }else{
                tv_group_sender_name.setText("");
                tv_group_sender_name.setVisibility(View.GONE);
            }
        }

        cell_yahoo_news_tv_desc.setText(CommonMethods.getUTFDecodedString(yahooChat.getYahooDescription()));
        String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(yahooChat.getTimestamp(),
                getResources().getConfiguration().locale), " ")[1];
        String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(yahooChat.getTimestamp(),
                getResources().getConfiguration().locale), " ")[2];
        cell_yahoo_news_tv_time.setText(calCulatedLocTime + " " + amOrPm);
        cell_yahoo_news_tv_title.setText(CommonMethods.getUTFDecodedString(yahooChat.getYahooTitle()));
        cell_yahoo_news_tv_title.setSelected(true);
        ImageLoader.getInstance().displayImage(yahooChat.getYahooImageUrl(), cell_yahoo_news_iv_image,options);
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cell_yahoo_news:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), yahooChat);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onLongClick(View v) {
        if(longClickListener!=null) {
            longClickListener.onCellItemLongClick(v, yahooChat);
        }
        return false;
    }
}
