package com.wachat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wachat.R;
import com.wachat.activity.ActivityGallery;
import com.wachat.callBack.InterfaceGallerySelect;
import com.wachat.data.DataShareImage;
import com.wachat.dataClasses.GallerySetGet;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;


public class AdapterGallery extends BaseAdapter {

    ImageLoader mImageLoader;
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<GallerySetGet> arrGallerySetGets;
    private InterfaceGallerySelect mInterfaceGallerySelect;
    private int width;
    private boolean isChat;
    private Constants.SELECTION mSelection;
    private OnGridItemClick mCallback;
    private ArrayList<DataShareImage> mListStrings = null;
    DisplayImageOptions options;
    public AdapterGallery(Context context, boolean isChat, Constants.SELECTION mSelection, OnGridItemClick mCallback,
                          InterfaceGallerySelect mInterfaceGallerySelect) {
        mContext = context;
        this.isChat = isChat;
        this.mSelection = mSelection;
        this.mCallback = mCallback;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoader.getInstance();
        this.mInterfaceGallerySelect = mInterfaceGallerySelect;
        arrGallerySetGets = new ArrayList<GallerySetGet>();
        width = (CommonMethods.getScreenWidth(mContext).widthPixels) / 3;
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true).cacheOnDisk(true).build();
    }

    public AdapterGallery(Context context, boolean isChat, Constants.SELECTION mSelection, ArrayList<DataShareImage> mListStrings, OnGridItemClick mCallback, InterfaceGallerySelect mInterfaceGallerySelect) {
        mContext = context;
        this.isChat = isChat;
        this.mSelection = mSelection;
        this.mCallback = mCallback;
        this.mListStrings = mListStrings;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoader.getInstance();
        this.mInterfaceGallerySelect = mInterfaceGallerySelect;
        arrGallerySetGets = new ArrayList<GallerySetGet>();
        width = (CommonMethods.getScreenWidth(mContext).widthPixels) / 3;
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).cacheOnDisk(true).build();
    }

    public void refreshAdapter(ArrayList<GallerySetGet> _arrGallerySetGets) {
        if (arrGallerySetGets != null && arrGallerySetGets.size() > 0)
            arrGallerySetGets.clear();
        arrGallerySetGets.addAll(_arrGallerySetGets);
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        if (arrGallerySetGets != null && arrGallerySetGets.size() > 0)
            return arrGallerySetGets.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.row_multiphoto_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.checkBox1 = (CheckBox) convertView
                    .findViewById(R.id.checkBox1);
            mViewHolder.imageView1 = (ImageView) convertView
                    .findViewById(R.id.imageView1);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.pos = position;

        mImageLoader.displayImage("file://"
                        + arrGallerySetGets.get(position).getImage_URL(),
                mViewHolder.imageView1, options,new ImageLoadingListener() {

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
//                        Animation anim = AnimationUtils.loadAnimation(mContext,
//                                R.anim.fade_in);
//                        view.setAnimation(anim);
//                        anim.start();
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
        setGridViewColumn(mViewHolder);
        mViewHolder.checkBox1
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            if (((ActivityGallery) mContext).getTotalSelected() == 10) {
                                ToastUtils.showAlertToast(mContext, mContext.getResources().getString(R.string.share_img_limit),
                                        ToastType.FAILURE_ALERT);
                                buttonView.setChecked(false);
                                return;
                            }
                        }
                        arrGallerySetGets.get(position).setSelected(isChecked);
                        mInterfaceGallerySelect.selectChecked(isChecked,
                                position);
                    }
                });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChat) {

//                    arrGallerySetGets.get(position).setSelected(true);
//                    mInterfaceGallerySelect.selectChecked(true,
//                            position);
                    if(!((CheckBox) v
                            .findViewById(R.id.checkBox1)).isChecked()) {
                        ActivityGallery.arrGallerySetGets_AllImages.get(position).setSelected(true);
                    }

                    ViewHolder mHolder = (ViewHolder) v.getTag();
                    mCallback.onGridItemClick(arrGallerySetGets.get(mHolder.pos).getImage_URL());
                }else{

                    if(((CheckBox) v
                            .findViewById(R.id.checkBox1)).isChecked()){
                        ((CheckBox) v
                                .findViewById(R.id.checkBox1)).setChecked(false);

                        arrGallerySetGets.get(position).setSelected(false);
                        mInterfaceGallerySelect.selectChecked(false,
                                position);
                    }else{
                        if (((ActivityGallery) mContext).getTotalSelected() == 10) {
                            ToastUtils.showAlertToast(mContext,
                                    mContext.getResources().getString(R.string.share_img_limit),
                                    ToastType.FAILURE_ALERT);
                            ((CheckBox) v
                                    .findViewById(R.id.checkBox1)).setChecked(false);
                            return;
                        }
                        ((CheckBox) v
                                .findViewById(R.id.checkBox1)).setChecked(true);
                        arrGallerySetGets.get(position).setSelected(true);
                        mInterfaceGallerySelect.selectChecked(true,
                                position);
                    }
                }

            }
        });
        if (isChat) {
            mViewHolder.checkBox1.setVisibility(View.GONE);

        } else {
            mViewHolder.checkBox1.setVisibility(View.VISIBLE);
            if (mListStrings != null) {
                condition:
                for (DataShareImage result : mListStrings) {
                    if (result.getImgUrl().equals(arrGallerySetGets.get(position).getImage_URL())) {
                        mViewHolder.checkBox1.setChecked(true);
                       if(result.isMasked())
                           arrGallerySetGets.get(position).setIsMasked(true);
                        break condition;
                    }
                }
            }
            if (arrGallerySetGets.get(position).isSelected()) {
                mViewHolder.checkBox1.setChecked(true);
            } else {
                mViewHolder.checkBox1.setChecked(false);
            }

        }

        return convertView;
    }

    private void setGridViewColumn(ViewHolder mViewHolder) {

        mViewHolder.imageView1.getLayoutParams().width = (CommonMethods
                .getScreen(mContext).widthPixels) / 3;
        mViewHolder.imageView1.getLayoutParams().height = (CommonMethods
                .getScreen(mContext).widthPixels) / 3;

    }

    public interface OnGridItemClick {
        void onGridItemClick(String fileName);
    }

    private class ViewHolder {
        private int pos;
        private ImageView imageView1;
        private CheckBox checkBox1;

    }
}
