package com.wachat.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

/**
 * Created by Argha on 12-01-2016.
 */
public class ActivityPinchToZoom  extends BaseActivity{

    private ImageView touchImg;
    private String imageFilePath = "";
    private DisplayImageOptions options;
    private ImageLoader mImageLoader;
    private boolean url_image = false;
    private View progressBarCircularIndetermininate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinch_to_zoom);
        getBundleData();

        touchImg = (ImageView) findViewById(R.id.img);

        if(TextUtils.isEmpty(imageFilePath)){
            ToastUtils.showAlertToast(this,"Invalid image.", ToastType.FAILURE_ALERT);
            finish();
            return;
        }

        progressBarCircularIndetermininate = findViewById(R.id.progressBarCircularIndetermininate);
        progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
        if(imageFilePath.contains("http")){
            loadViaImageLoader(imageFilePath);
//            Glide.with(this).load(imageFilePath).into(touchImg);
        }else {
            loadViaImageLoader("file://" + imageFilePath);
//            Glide.with(this).load("file://" +imageFilePath).into(touchImg);
        }


    }

    public class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            super( context );

            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public String getId() {
            return "rotate" + rotateRotationAngle;
        }
    }


    private void getBundleData() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey(Constants.B_RESULT))
            imageFilePath = mBundle.getString(Constants.B_RESULT);
        url_image = mBundle.getBoolean("url_image",false);
    }


    public void loadViaImageLoader(final String path) {

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .build();
        this.mImageLoader = ImageLoader.getInstance();
//        this.mImageLoader.init(ImageLoaderConfiguration.createDefault(mActivity));

        mImageLoader.displayImage(path, touchImg, options,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        LogUtils.d("ActivityPinchToZoom","Image Loading Started");
                        touchImg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        LogUtils.d("ActivityPinchToZoom","Image Loading onLoadingFailed");

                        touchImg.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        LogUtils.d("ActivityPinchToZoom","Image Loading onLoadingComplete");
                        progressBarCircularIndetermininate.setVisibility(View.GONE);
                        touchImg.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        LogUtils.d("ActivityPinchToZoom","Image Loading onLoadingCancelled");
                        progressBarCircularIndetermininate.setVisibility(View.GONE);
                        touchImg.setVisibility(View.VISIBLE);
                    }
                });
    }
    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String result) {

    }

    @Override
    protected void CommonMenuClcik() {

    }
}
