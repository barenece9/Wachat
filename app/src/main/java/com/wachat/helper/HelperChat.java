package com.wachat.helper;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wachat.R;

/**
 * Created by Gourav Kundu on 27-08-2015.
 */
public class HelperChat implements View.OnClickListener {

    private AppCompatActivity mBaseActivity;
    private HelperCallback mCallback;

    private LinearLayout lnr_Share, lnr_DropDown;
    private LinearLayout lnr_video, lnr_camera, lnr_gallery, lnr_location, lnr_contact, lnr_youtube,
            lnr_news, lnr_sketch;
    private ImageView iv_video, iv_camera, iv_gallery, iv_location, iv_contact, iv_youtube, iv_news, iv_sketch;
    private TextView tv_video, tv_camera, tv_gallery, tv_location, tv_contact, tv_youtube, tv_news, tv_sketch;

    public HelperChat(AppCompatActivity mActivityChat, HelperCallback mCallback) {
        this.mBaseActivity = mActivityChat;
        this.mCallback = mCallback;
        initComponents();
        populateView();
    }

    private void initComponents() {

        lnr_Share = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_Share);
        lnr_DropDown = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_DropDown);

        //share items
        lnr_video = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_video);
        lnr_camera = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_camera);
        lnr_gallery = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_gallery);
        lnr_location = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_location);
        lnr_contact = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_contact);
        lnr_youtube = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_youtube);
        lnr_news = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_news);
        lnr_sketch = (LinearLayout) mBaseActivity.findViewById(R.id.lnr_sketch);

        lnr_video.setOnClickListener(this);
        lnr_camera.setOnClickListener(this);
        lnr_gallery.setOnClickListener(this);
        lnr_location.setOnClickListener(this);
        lnr_contact.setOnClickListener(this);
        lnr_youtube.setOnClickListener(this);
        lnr_news.setOnClickListener(this);
        lnr_sketch.setOnClickListener(this);

        iv_video = (ImageView) lnr_video.findViewById(R.id.iv_item);
        iv_camera = (ImageView) lnr_camera.findViewById(R.id.iv_item);
        iv_gallery = (ImageView) lnr_gallery.findViewById(R.id.iv_item);
        iv_location = (ImageView) lnr_location.findViewById(R.id.iv_item);
        iv_contact = (ImageView) lnr_contact.findViewById(R.id.iv_item);
        iv_youtube = (ImageView) lnr_youtube.findViewById(R.id.iv_item);
        iv_news = (ImageView) lnr_news.findViewById(R.id.iv_item);
        iv_sketch = (ImageView) lnr_sketch.findViewById(R.id.iv_item);


        tv_video = (TextView) lnr_video.findViewById(R.id.tv_item_name);
        tv_camera = (TextView) lnr_camera.findViewById(R.id.tv_item_name);
        tv_gallery = (TextView) lnr_gallery.findViewById(R.id.tv_item_name);
        tv_location = (TextView) lnr_location.findViewById(R.id.tv_item_name);
        tv_contact = (TextView) lnr_contact.findViewById(R.id.tv_item_name);
        tv_youtube = (TextView) lnr_youtube.findViewById(R.id.tv_item_name);
        tv_news = (TextView) lnr_news.findViewById(R.id.tv_item_name);
        tv_sketch = (TextView) lnr_sketch.findViewById(R.id.tv_item_name);

        lnr_Share.setVisibility(View.INVISIBLE);
        lnr_DropDown.setVisibility(View.INVISIBLE);
    }

    private void populateView() {
        iv_video.setImageResource(R.drawable.ic_chat_share_video_icon);
        iv_camera.setImageResource(R.drawable.ic_chat_share_camera_icon);
        iv_gallery.setImageResource(R.drawable.ic_chat_share_gallery_icon);
        iv_location.setImageResource(R.drawable.ic_chat_share_location_icon);
        iv_contact.setImageResource(R.drawable.ic_chat_share_contact_icon);
        iv_youtube.setImageResource(R.drawable.ic_chat_share_you_tube_icon);
        iv_news.setImageResource(R.drawable.ic_chat_share_news_icon);
        iv_sketch.setImageResource(R.drawable.ic_chat_share_sketch_icon);

        tv_video.setText(mBaseActivity.getResources().getString(R.string.video));
        tv_camera.setText(mBaseActivity.getResources().getString(R.string.camera));
        tv_gallery.setText(mBaseActivity.getResources().getString(R.string.gallery));
        tv_location.setText(mBaseActivity.getResources().getString(R.string.loaction));
        tv_contact.setText(mBaseActivity.getResources().getString(R.string.contact));
        tv_youtube.setText(mBaseActivity.getResources().getString(R.string.youtube));
        tv_news.setText(mBaseActivity.getResources().getString(R.string.news));
        tv_sketch.setText(mBaseActivity.getResources().getString(R.string.sketch));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.lnr_video:
                mCallback.onVideoSelect();
                break;
            case R.id.lnr_camera:
                mCallback.onCameraSelect();
                break;
            case R.id.lnr_gallery:
                mCallback.onGallerySelect();
                break;
            case R.id.lnr_location:
                mCallback.onLocationSelect();
                break;
            case R.id.lnr_contact:
                mCallback.onContactSelect();
                break;
            case R.id.lnr_youtube:
                mCallback.onYouTubeSelect();
                break;
            case R.id.lnr_news:
                mCallback.onNewsSelect();
                break;
            case R.id.lnr_sketch:
                mCallback.onSketchSelect();
                break;
        }
    }

    public interface HelperCallback {
        void onContactSelect();

        void onSketchSelect();

        void onNewsSelect();

        void onLocationSelect();

        void onVideoSelect();

        void onGallerySelect();

        void onYouTubeSelect();

        void onCameraSelect();
    }
}
