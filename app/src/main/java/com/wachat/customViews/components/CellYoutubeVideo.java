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
public class CellYoutubeVideo extends Cell implements View.OnClickListener, View.OnLongClickListener {


    public RelativeLayout cell_youtube_video;
    private ImageView cell_youtube_video_iv_vid_thumb;
    private TextView cell_youtube_video_tv_vid_title, cell_youtube_video_tv_desc, cell_youtube_video_tv_time;
    private DisplayImageOptions options;
    private ImageLoader mImageLoader;
    private DataTextChat youtubeChat;
    private TextView tv_group_sender_name;

    public CellYoutubeVideo(Context context) {
        super(context);
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(1000))
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .build();
        this.mImageLoader = ImageLoader.getInstance();
        this.mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public CellYoutubeVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellYoutubeVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_youtube_video, this, true);
        cell_youtube_video = (RelativeLayout) findViewById(R.id.cell_youtube_video);
        cell_youtube_video_iv_vid_thumb = (ImageView) findViewById(R.id.cell_youtube_video_iv_vid_thumb);
        tv_group_sender_name = (TextView)findViewById(R.id.tv_group_sender_name);
        cell_youtube_video_tv_vid_title = (TextView) findViewById(R.id.cell_youtube_video_tv_vid_title);
        cell_youtube_video_tv_desc = (TextView) findViewById(R.id.cell_youtube_video_tv_desc);
        cell_youtube_video_tv_time = (TextView) findViewById(R.id.cell_youtube_video_tv_time);
        cell_youtube_video.setLayoutParams(new LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        cell_youtube_video.setOnClickListener(this);
        cell_youtube_video.setOnLongClickListener(this);
    }

    @Override
    public void setUpView(boolean isMine, DataTextChat youtubeChat) {
        this.youtubeChat = youtubeChat;
        if (isMine) {
            this.setGravity(Gravity.RIGHT);
            cell_youtube_video.setBackgroundResource((R.drawable.ic_gray_bubble));
            cell_youtube_video.setPadding(10, 10, 10, 10);
            cell_youtube_video_tv_desc.setTextColor(Color.parseColor(color_WHITE));
            cell_youtube_video_tv_time.setTextColor(Color.parseColor(color_WHITE));
            cell_youtube_video_tv_vid_title.setTextColor(Color.parseColor(color_WHITE));
            tv_group_sender_name.setTextColor(Color.parseColor(color_WHITE));
            tv_group_sender_name.setText("");
            tv_group_sender_name.setVisibility(View.GONE);

        } else {
            this.setGravity(Gravity.LEFT);
            cell_youtube_video.setBackgroundResource((R.drawable.ic_white_bubble));
            cell_youtube_video.setPadding(10, 10, 10, 10);
            cell_youtube_video_tv_desc.setTextColor(Color.parseColor(color_DARK));
            cell_youtube_video_tv_time.setTextColor(Color.parseColor(color_DARK));
            cell_youtube_video_tv_vid_title.setTextColor(Color.parseColor(color_DARK));

            tv_group_sender_name.setTextColor(Color.parseColor(color_ORANGE));
            if(youtubeChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                String senderName = "";
                senderName = youtubeChat.getFriendName();
                if(TextUtils.isEmpty(senderName)){
                    senderName = youtubeChat.getSenderName();
                }
                tv_group_sender_name.setText(CommonMethods.getUTFDecodedString(senderName));
                tv_group_sender_name.setVisibility(View.VISIBLE);
            }else{
                tv_group_sender_name.setText("");
                tv_group_sender_name.setVisibility(View.GONE);
            }
        }

        cell_youtube_video_tv_desc.setText(CommonMethods.getUTFDecodedString(youtubeChat.getYoutubeDescription()));
        String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(youtubeChat.getTimestamp(),
                getResources().getConfiguration().locale), " ")[1];
        String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(youtubeChat.getTimestamp(),
                getResources().getConfiguration().locale), " ")[2];
        cell_youtube_video_tv_time.setText(calCulatedLocTime + " " + amOrPm);
        cell_youtube_video_tv_vid_title.setText(CommonMethods.getUTFDecodedString(youtubeChat.getYoutubeTitle()));
        cell_youtube_video_tv_vid_title.setSelected(true);
        ImageLoader.getInstance().displayImage(youtubeChat.getYoutubeThumbUrl(), cell_youtube_video_iv_vid_thumb);
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cell_youtube_video:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), youtubeChat);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onLongClick(View v) {
        if(longClickListener!=null) {
            longClickListener.onCellItemLongClick(v, youtubeChat);
        }
        return false;
    }
}
