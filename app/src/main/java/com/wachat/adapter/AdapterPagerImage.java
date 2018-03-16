package com.wachat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wachat.R;
import com.wachat.customViews.BlurringView;
import com.wachat.data.DataShareImage;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 02-09-2015.
 */
public class AdapterPagerImage extends PagerAdapter {

    private Context mContext;
    private ArrayList<DataShareImage> mListString;
    private ImageLoader mImageLoader;
    private LayoutInflater mLayoutInflater;
    private final DisplayImageOptions options;

    public AdapterPagerImage(Context mContext, ArrayList<DataShareImage> mListString, ImageLoader mImageLoader) {
        this.mContext = mContext;
        this.mListString = mListString;
        this.mImageLoader = mImageLoader;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true).cacheOnDisk(true).build();
    }

    public void updateAdapter(ArrayList<DataShareImage> mListString) {
        this.mListString=mListString;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListString.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View mView = mLayoutInflater.inflate(R.layout.inflate_pager_image, container, false);
        ImageView mImageView = (ImageView) mView.findViewById(R.id.iv);
        BlurringView blurring_view = (BlurringView) mView.findViewById(R.id.blurring_view);
        blurring_view.setBlurredView(mImageView);
        final View pager_image_progressbar  = mView.findViewById(R.id.pager_image_progressbar);
        mImageLoader.displayImage("file://" + mListString.get(position).getImgUrl(), mImageView, options);
        if (mListString.get(position).isMasked) {
            blurring_view.setVisibility(View.VISIBLE);
//            pager_image_progressbar.setVisibility(View.VISIBLE);
//            Picasso.with(mContext).load(Constants.file + mListString.get(position).getImgUrl())
//                    .transform(new BlurTransformation(mContext,
//                            CompressImageUtils.BLUR_RADIUS)).into(mImageView, new Callback() {
//                @Override
//                public void onSuccess() {
//                    pager_image_progressbar.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onError() {
//                    ToastUtils.showAlertToast(mContext,"Unable to mask the image. Please try again or select another one",
//                            ToastType.FAILURE_ALERT);
//                    pager_image_progressbar.setVisibility(View.GONE);
//                }
//            });

        }else {
            blurring_view.setVisibility(View.GONE);
//            Picasso.with(mContext).load(Constants.file +
//                    mListString.get(position).getImgUrl()).into(mImageView);

//            mImageLoader.displayImage("file://" + mListString.get(position).getImgUrl(), mImageView, options);
        }
        ((ViewPager) container).addView(mView);
        return mView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
