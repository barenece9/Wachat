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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.BlurringView;
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
public class CellPhoto extends Cell implements View.OnClickListener, View.OnLongClickListener {


    public RelativeLayout cellPhoto , trnsparent_layer;
    private ImageView cell_photo_iv ,cell_photo_mask  ,img_translate;
    private TextView cell_photo_tv_Date , cell_tv_Msg;
    private ProgressWheel progressBar;
    private View cell_photo_download;
    private View cell_photo_cross;
    private View cell_photo_retry;
    private DataTextChat imageChat;
    private DisplayImageOptions options;
    private ImageLoader mImageLoader;
    private Context mContext;
    private TextView tv_group_sender_name;
    private BlurringView blurring_view;

    public CellPhoto(Context context) {
        super(context);
        this.mContext = context;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
//                .displayer(new FadeInBitmapDisplayer(100))
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        this.mImageLoader = ImageLoader.getInstance();
        this.mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public CellPhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellPhoto(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        LayoutInflater.from(getContext()).inflate(R.layout.cell_photo, this, true);
        cellPhoto = (RelativeLayout) findViewById(R.id.cellPhoto);
        cell_photo_iv = (ImageView) findViewById(R.id.cell_photo_iv);
        blurring_view = (BlurringView) findViewById(R.id.blurring_view);
//        blurring_view.setBlurredView(cell_photo_iv);
        cell_photo_tv_Date = (TextView) findViewById(R.id.cell_photo_tv_Date);
        tv_group_sender_name = (TextView)findViewById(R.id.tv_group_sender_name);

        cell_tv_Msg = (TextView)findViewById(R.id.tv_Msg);
        cell_photo_mask = (ImageView)findViewById(R.id.cell_photo_mask);
        progressBar = (ProgressWheel) findViewById(R.id.cell_photo_progress_wheel);
        img_translate = (ImageView) findViewById(R.id.img_translate);
        img_translate.setOnClickListener(this);
        trnsparent_layer = (RelativeLayout)findViewById(R.id.trnsparent_layer);
        cellPhoto.setLayoutParams(new LayoutParams(width, width));
//        cellPhoto.setLayoutParams(new LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT));
//         int marginPixel = CommonMethods.dpToPx(getContext(),10);
//        cell_photo_iv.setLayoutParams(new RelativeLayout.LayoutParams(width-marginPixel, width-marginPixel));
//        cellPhoto.setOnClickListener(this);

//        findViewById(R.id.cell_rl_option).setOnClickListener(this);
        cell_photo_download = findViewById(R.id.cell_photo_download);
        cell_photo_download.setOnClickListener(this);
        cell_photo_cross = findViewById(R.id.cell_photo_cross);
        cell_photo_cross.setOnClickListener(this);
        cell_photo_retry = findViewById(R.id.cell_photo_retry);
        cell_photo_retry.setOnClickListener(this);
        cell_photo_mask.setOnClickListener(this);
        cellPhoto.setOnClickListener(this);

        cellPhoto.setOnLongClickListener(this);

    }

    @Override
    public void setUpView(boolean isMine, final DataTextChat imageChat) {
        this.imageChat = imageChat;
        progressBar.setVisibility(GONE);
        blurring_view.setBlurredView(cell_photo_iv);
        blurring_view.invalidate();
        blurring_view.setVisibility(GONE);
        if (isMine) {
            cell_photo_mask.setVisibility(View.GONE);
            this.setGravity(Gravity.RIGHT);
            cell_photo_tv_Date.setTextColor(Color.parseColor(color_WHITE));
            cellPhoto.setBackgroundResource((R.drawable.ic_gray_bubble));

            cellPhoto.setPadding(15, 15, 15, 15);
            cell_tv_Msg.setTextColor(Color.parseColor(color_WHITE));

            cell_photo_download.setVisibility(View.GONE);
            img_translate.setVisibility(GONE);
            ImageLoader.getInstance().displayImage("file://" + imageChat.getFilePath(), cell_photo_iv,options);
            if (TextUtils.isEmpty(imageChat.getFileUrl())) {
                ImageLoader.getInstance().displayImage("file://" + imageChat.getFilePath(), cell_photo_iv,options);
                blurring_view.setVisibility(GONE);
                if (AppVhortex.getInstance().isInUploadQueue(imageChat)) {
                    cell_photo_cross.setVisibility(View.VISIBLE);
                    cell_photo_retry.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    trnsparent_layer.setVisibility(VISIBLE);
                } else {
                    cell_photo_cross.setVisibility(View.GONE);
                    cell_photo_retry.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    trnsparent_layer.setVisibility(VISIBLE);
                }
            } else {
                //upload complete
                progressBar.setVisibility(View.GONE);
                cell_photo_cross.setVisibility(View.GONE);
                cell_photo_retry.setVisibility(View.GONE);

                ImageLoader.getInstance().displayImage("file://" + imageChat.getFilePath(), cell_photo_iv,
                        options, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                trnsparent_layer.setVisibility(GONE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                trnsparent_layer.setVisibility(GONE);
                                if(imageChat.getIsMasked().equalsIgnoreCase("1"))
                                    maskedImage(imageChat);
                                else
                                    nonMaskedImage(imageChat);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                                trnsparent_layer.setVisibility(GONE);
                            }
                        });
                if(imageChat.getIsMasked().equalsIgnoreCase("1"))
                    maskedImage(imageChat);
                else
                    nonMaskedImage(imageChat);

            }
            if(TextUtils.isEmpty(imageChat.getBody()))
                cell_tv_Msg.setVisibility(GONE);
            else
                cell_tv_Msg.setVisibility(VISIBLE);
            cell_tv_Msg.setText(CommonMethods.getUTFDecodedString(imageChat.getBody()));
            //set chat message data
            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(imageChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(imageChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            cell_photo_tv_Date.setText(calCulatedLocTime + " " + amOrPm);
            tv_group_sender_name.setTextColor(Color.parseColor(color_WHITE));
            tv_group_sender_name.setText("");
            tv_group_sender_name.setVisibility(View.GONE);


        } else {
            //recieve message bubble
            this.setGravity(Gravity.LEFT);
            cell_photo_tv_Date.setTextColor(Color.parseColor(color_DARK));
            cellPhoto.setBackgroundResource((R.drawable.ic_white_bubble));
            cellPhoto.setPadding(15, 15, 15, 15);

            cell_tv_Msg.setTextColor(Color.parseColor(color_DARK));


            if(TextUtils.isEmpty(imageChat.getBody()) && TextUtils.isEmpty(imageChat.getStrTranslatedText()))
                cell_tv_Msg.setVisibility(GONE);
            else
                cell_tv_Msg.setVisibility(VISIBLE);


            cell_tv_Msg.setText(CommonMethods.getUTFDecodedString(imageChat.getBody()));
            //if any translated messages then set as bubble
            if (!StringUtils.isEmpty(imageChat.getStrTranslatedText())) {
                cell_tv_Msg.setText(CommonMethods.getUTFDecodedString(imageChat.getStrTranslatedText()));
                //img_translate.setVisibility(VISIBLE);
            } else {
                cell_tv_Msg.setText(CommonMethods.getUTFDecodedString(imageChat.getBody()));
                img_translate.setVisibility(GONE);
            }

            if (TextUtils.isEmpty(imageChat.getFilePath())) {
                if (!TextUtils.isEmpty(imageChat.getFileUrl())) {
                    //TODO show download icon
                    cell_photo_mask.setVisibility(GONE);
                    if (AppVhortex.getInstance().isInDownloadQueue(imageChat)) {
                        cell_photo_download.setVisibility(View.GONE);
                        cell_photo_cross.setVisibility(View.GONE);
                        cell_photo_retry.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        trnsparent_layer.setVisibility(VISIBLE);
                    } else {
                        cell_photo_download.setVisibility(View.VISIBLE);
                        cell_photo_cross.setVisibility(View.GONE);
                        cell_photo_retry.setVisibility(View.GONE);

                        progressBar.setVisibility(View.GONE);
                        trnsparent_layer.setVisibility(VISIBLE);
                    }

                }

//                if (TextUtils.isEmpty(imageChat.getThumbFilePath())) {
//                    if (TextUtils.isEmpty(imageChat.getThumbBase64())) {
//                        //TODO show file is not available
//                    } else {
//                        Bitmap mBitmap = null;
//                        try {
//                            mBitmap = CommonMethods.decodeBase64(imageChat.getThumbBase64());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        File incomingImageFile = MediaUtils.saveImageStringToFile(mBitmap,
//                                getContext());
//                        if (incomingImageFile != null) {
//                            imageChat.setThumbFilePath(incomingImageFile.getPath());
//                            Picasso.with(mContext).load("file://"+imageChat.getThumbFilePath())
//                                    .transform(new BlurTransformation(mContext, CompressImageUtils.BLUR_RADIUS_MASK_IMAGE)).into(cell_photo_iv);
//                        } else {
//                            //TODO show file is not available
//                        }
//                    }
//                } else {
//                    Picasso.with(mContext).load("file://"+imageChat.getThumbFilePath())
//                            .into(cell_photo_iv);
//                }

            } else {
                //Download complete
//                trnsparent_layer.setVisibility(GONE);
                cell_photo_download.setVisibility(View.GONE);
                cell_photo_cross.setVisibility(View.GONE);
                cell_photo_retry.setVisibility(View.GONE);
//                cell_photo_iv.setImageResource(0);
//                ImageLoader.getInstance().displayImage("file://" + imageChat.getFilePath(), cell_photo_iv,
//                        options);
                ImageLoader.getInstance().displayImage("file://" + imageChat.getFilePath(), cell_photo_iv,
                        options, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                trnsparent_layer.setVisibility(GONE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                trnsparent_layer.setVisibility(GONE);
                                if(imageChat.getIsMasked().equalsIgnoreCase("1")) {
                                    maskedImage(imageChat);
                                }else {
                                    nonMaskedImage(imageChat);
                                }
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                                trnsparent_layer.setVisibility(GONE);
                            }
                        });
//                if(imageChat.getIsMasked().equalsIgnoreCase("1")) {
//                    maskedImage(imageChat);
//                }else {
//                    nonMaskedImage(imageChat);
//                }


            }

            String calCulatedLocTime = StringUtils.split(DateTimeUtil.convertUtcToLocal(imageChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[1];
            String amOrPm = StringUtils.split(DateTimeUtil.convertUtcToLocal(imageChat.getTimestamp(),
                    getResources().getConfiguration().locale), " ")[2];
            cell_photo_tv_Date.setText(calCulatedLocTime + " " + amOrPm);

            tv_group_sender_name.setTextColor(Color.parseColor(color_ORANGE));
            if(imageChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                String senderName = "";
                senderName = imageChat.getSenderName();
                if(TextUtils.isEmpty(senderName)){
                    senderName = imageChat.getSenderName();
                }
                tv_group_sender_name.setText(CommonMethods.getUTFDecodedString(senderName));
                tv_group_sender_name.setVisibility(View.VISIBLE);
            }else{
                tv_group_sender_name.setText("");
                tv_group_sender_name.setVisibility(View.GONE);
            }

        }

        if(blurring_view.getVisibility()==VISIBLE) {
            blurring_view.invalidate();
        }
    }

    private void maskedImage(DataTextChat imageChat){
        if(imageChat.getMaskEnabled().equalsIgnoreCase("1"))
            setMasked();
        else
            setUnMasked();
    }

    private void nonMaskedImage(DataTextChat imageChat){
        blurring_view.setVisibility(GONE);

        cell_photo_mask.setVisibility(GONE);
        ImageLoader.getInstance().displayImage("file://" + imageChat.getFilePath(), cell_photo_iv,options);
    }
    private void setMasked(){
//        trnsparent_layer.setVisibility(VISIBLE);
        blurring_view.setVisibility(VISIBLE);
        blurring_view.invalidate();

//        if(TextUtils.isEmpty(imageChat.getBlurredImagePath())) {
//            Picasso.with(mContext).load("file://" + imageChat.getFilePath())
//                    .transform(new BlurTransformation(mContext,
//                            CompressImageUtils.BLUR_RADIUS_MASK_IMAGE))
//                    .into(cell_photo_iv, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            trnsparent_layer.setVisibility(GONE);
//                        }
//
//                        @Override
//                        public void onError() {
//                            trnsparent_layer.setVisibility(GONE);
//                        }
//                    });
//        }else{
//            ImageLoader.getInstance().displayImage("file://" + imageChat.getBlurredImagePath(),
//                    cell_photo_iv,options,new ImageLoadingListener(){
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                    trnsparent_layer.setVisibility(GONE);
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    trnsparent_layer.setVisibility(GONE);
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//                    trnsparent_layer.setVisibility(GONE);
//                }
//            });
//        }
//        trnsparent_layer.setVisibility(GONE);
        cell_photo_mask.setVisibility(VISIBLE);
        cell_photo_mask.setSelected(true);
    }

    private void setUnMasked(){
        blurring_view.setVisibility(GONE);
//        trnsparent_layer.setVisibility(GONE);
        cell_photo_mask.setVisibility(VISIBLE);
        cell_photo_mask.setSelected(false);
        ImageLoader.getInstance().displayImage("file://" + imageChat.getFilePath(), cell_photo_iv,options);
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
    }

    public void loadViaImageLoader(final String path, final DataTextChat dataFileChat, final boolean shouldSaveDownloadedImage) {
        mImageLoader.displayImage(path, cell_photo_iv, options,
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

                        if (shouldSaveDownloadedImage) {
                            if (dataFileChat != null) {

                                if (TextUtils.isEmpty(dataFileChat.getFilePath())) {
                                    File incomingImageFile = MediaUtils
                                            .saveImageStringToFile(loadedImage,
                                                    getContext());
//                                        imageLoaderDownloadListener
//                                                .onImageDownloadComplete(comment,
//                                                        incomingImageFile.getPath());
                                    dataFileChat.setFilePath(incomingImageFile.getPath());
                                }
                            }
                            // comments.get(comments.indexOf(comment))
                            // .setCommentImageUrl(uploadedFileUrl);
                            // this.notifyDataSetChanged();
                        }
                    }
                });
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
        if(longClickListener!=null) {
            longClickListener.onCellItemLongClick(v, imageChat);
        }
        return false;
    }
}
