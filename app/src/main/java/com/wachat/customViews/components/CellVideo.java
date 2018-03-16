package com.wachat.customViews.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.ProgressWheel;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.DateTimeUtil;
import com.wachat.util.MediaUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by Gourav Kundu on 28-08-2015.
 */
public class CellVideo extends Cell implements View.OnClickListener, View.OnLongClickListener {


    public RelativeLayout cellVideo;
    private ImageView cell_video_iv;
    private TextView cell_video_tv_Date;
    private ProgressWheel cell_video_progress_wheel;
    private View cell_video_download;
    private View cell_video_cross;
    private View cell_video_retry;
    private View cell_video_play;
    private DataTextChat videoChat;
    private DisplayImageOptions options;
    private ImageLoader mImageLoader;
    private Context mContext;
    private ImageView cell_video_iv_blur;
    private TextView tv_group_sender_name;

    public CellVideo(Context context) {
        super(context);
        this.mContext = context;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .build();
        this.mImageLoader = ImageLoader.getInstance();
        this.mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public CellVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_video, this, true);
        cellVideo = (RelativeLayout) findViewById(R.id.cellVideo);
        cell_video_iv = (ImageView) findViewById(R.id.cell_video_iv);
        tv_group_sender_name = (TextView)findViewById(R.id.tv_group_sender_name);
        cell_video_iv_blur = (ImageView)findViewById(R.id.cell_video_iv_blur);
        cell_video_tv_Date = (TextView) findViewById(R.id.cell_video_tv_Date);
        cell_video_progress_wheel = (ProgressWheel) findViewById(R.id.cell_video_progress_wheel);
        cellVideo.setLayoutParams(new LayoutParams(width, width));
//        cellPhoto.setOnClickListener(this);

//        findViewById(R.id.cell_rl_option).setOnClickListener(this);
        cell_video_download = findViewById(R.id.cell_video_download);
        cell_video_download.setOnClickListener(this);
        cell_video_cross = findViewById(R.id.cell_video_cross);
        cell_video_cross.setOnClickListener(this);
        cell_video_retry = findViewById(R.id.cell_video_retry);
        cell_video_retry.setOnClickListener(this);
        cell_video_play = findViewById(R.id.cell_video_play);
        cell_video_play.setOnClickListener(this);
        cellVideo.setOnLongClickListener(this);

    }

    @Override
    public void setUpView(boolean isMine, DataTextChat videoChat) {
        this.videoChat = videoChat;
        cell_video_progress_wheel.setVisibility(GONE);
        if (isMine) {
            cell_video_iv_blur.setVisibility(View.GONE);
            this.setGravity(Gravity.RIGHT);
            cell_video_tv_Date.setTextColor(Color.parseColor(color_WHITE));
            cellVideo.setBackgroundResource((R.drawable.ic_gray_bubble));
            ImageLoader.getInstance().displayImage("file://" + videoChat.getFilePath(), cell_video_iv);
            cellVideo.setPadding(15, 15, 15, 15);

            cell_video_download.setVisibility(View.GONE);
            if (TextUtils.isEmpty(videoChat.getFileUrl())) {
                if (AppVhortex.getInstance().isInUploadQueue(videoChat)) {
                    cell_video_cross.setVisibility(View.VISIBLE);
                    cell_video_retry.setVisibility(View.GONE);
                    cell_video_progress_wheel.setVisibility(View.VISIBLE);

                } else {
                    cell_video_cross.setVisibility(View.GONE);
                    cell_video_retry.setVisibility(View.VISIBLE);
                    cell_video_progress_wheel.setVisibility(View.GONE);
                }

                cell_video_play.setVisibility(View.GONE);
            } else {
                cell_video_play.setVisibility(View.VISIBLE);
                cell_video_progress_wheel.setVisibility(View.GONE);
                cell_video_cross.setVisibility(View.GONE);
                cell_video_retry.setVisibility(View.GONE);
            }


            //set chat message data
            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(videoChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(videoChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            cell_video_tv_Date.setText(calCulatedLocTime + " " + amOrPm);

            tv_group_sender_name.setTextColor(Color.parseColor(color_WHITE));
            tv_group_sender_name.setText("");
            tv_group_sender_name.setVisibility(View.GONE);

        } else {
            this.setGravity(Gravity.LEFT);
            cell_video_tv_Date.setTextColor(Color.parseColor(color_DARK));
            cellVideo.setBackgroundResource((R.drawable.ic_white_bubble));
            cellVideo.setPadding(15, 15, 15, 15);

            if (TextUtils.isEmpty(videoChat.getFilePath())) {
                cell_video_play.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(videoChat.getFileUrl())) {
                    //TODO show download icon

                    if (AppVhortex.getInstance().isInDownloadQueue(videoChat)) {
                        cell_video_download.setVisibility(View.GONE);
                        cell_video_cross.setVisibility(View.VISIBLE);
                        cell_video_retry.setVisibility(View.GONE);
                        cell_video_progress_wheel.setVisibility(View.VISIBLE);
//                        cell_video_progress_wheel.setProgress(videoChat.getProgress());
                    } else {
                        cell_video_download.setVisibility(View.VISIBLE);
                        cell_video_cross.setVisibility(View.GONE);
                        cell_video_retry.setVisibility(View.GONE);
                        cell_video_progress_wheel.setVisibility(View.GONE);
                    }

                }

                if (TextUtils.isEmpty(videoChat.getThumbFilePath())) {
                    if (TextUtils.isEmpty(videoChat.getThumbBase64())) {
                        //TODO show file is not available
                    } else {
                        Bitmap mBitmap = null;
                        try {
                            mBitmap = CommonMethods.decodeBase64(videoChat.getThumbBase64());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        File incomingImageFile = MediaUtils.saveImageStringToFile(mBitmap,
                                getContext());
                        if (incomingImageFile != null) {
                            videoChat.setThumbFilePath(incomingImageFile.getPath());
                            loadViaImageLoader("file://" + videoChat.getThumbFilePath(), videoChat, false);
                        } else {
                            //TODO show file is not available
                        }
                    }
                } else {
                    loadViaImageLoader("file://" + videoChat.getThumbFilePath(), videoChat, false);
                }

            } else {
                loadViaImageLoader("file://" + videoChat.getThumbFilePath(), videoChat, false);
                cell_video_play.setVisibility(View.VISIBLE);
                cell_video_download.setVisibility(View.GONE);
                cell_video_cross.setVisibility(View.GONE);
                cell_video_retry.setVisibility(View.GONE);
                cell_video_iv_blur.setVisibility(View.GONE);
            }

            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(videoChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(videoChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            cell_video_tv_Date.setText(calCulatedLocTime + " " + amOrPm);

            tv_group_sender_name.setTextColor(Color.parseColor(color_ORANGE));
            if(videoChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                String senderName = "";
                senderName = videoChat.getFriendName();
                if(TextUtils.isEmpty(senderName)){
                    senderName = videoChat.getSenderName();
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

    public void loadViaImageLoader(final String path, final DataTextChat dataFileChat, final boolean shouldSaveDownloadedImage) {
        mImageLoader.displayImage(path, cell_video_iv, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {

//                        if (shouldSaveDownloadedImage) {
//                            if (dataFileChat != null) {
//
//                                if (TextUtils.isEmpty(dataFileChat.getFilePath())) {
//                                    File incomingImageFile = MediaUtils
//                                            .saveImageStringToFile(loadedImage,
//                                                    getContext());
////                                        imageLoaderDownloadListener
////                                                .onImageDownloadComplete(comment,
////                                                        incomingImageFile.getPath());
//                                    dataFileChat.setFilePath(incomingImageFile.getPath());
//                                }
//                            }
//                            // comments.get(comments.indexOf(comment))
//                            // .setCommentImageUrl(uploadedFileUrl);
//                            // this.notifyDataSetChanged();
//                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cell_video_download:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), videoChat);
                }
                break;
            case R.id.cell_video_play:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), videoChat);
                }
                break;
            case R.id.cell_video_cross:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), videoChat);
                }
                break;
            case R.id.cell_video_retry:
                if (clickListener != null) {
                    clickListener.onCellItemClick(v.getId(), videoChat);
                }
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onLongClick(View v) {
        if(longClickListener!=null) {
            longClickListener.onCellItemLongClick(v, videoChat);
        }
        return false;
    }

}
