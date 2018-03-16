package com.wachat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wachat.R;
import com.wachat.data.DataContact;
import com.wachat.util.CommonMethods;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 29-08-2015.
 */
public class AdapterChoosePeople extends BaseAdapter {

    private int width = 0;
    private ArrayList<DataContact> mListContact = new ArrayList<DataContact>();
    private Context mContext;
    private DisplayImageOptions options;
    private ImageLoader mImageLoader;

    public AdapterChoosePeople(Context mContext, ArrayList<DataContact> mListContact) {
        this.mContext = mContext;
        this.mListContact = mListContact;
        width = (CommonMethods.getScreenWidth(mContext).widthPixels) / 3;

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_chats_noimage_profile)
                .showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
        this.mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        if (mListContact != null)
            return mListContact.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataContact mDataContact = mListContact.get(position);
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(width, width);
            convertView = (LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_choose_people, null);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.iv_image.setLayoutParams(mLayoutParams);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (!TextUtils.isEmpty(mDataContact.getAppName())) {
            holder.tv_count.setText(CommonMethods.getUTFDecodedString(mDataContact.getAppName()));
        } else {
            if (!TextUtils.isEmpty(mDataContact.getName())) {
                holder.tv_count.setText(CommonMethods.getUTFDecodedString(mDataContact.getName()));
            }else {
                holder.tv_count.setText("+" + mDataContact.getCountryCode() + mDataContact.getPhoneNumber());
            }
        }

        mImageLoader.displayImage(mDataContact.getAppFriendImageLink(), holder.iv_image, options);

        return convertView;
    }

    public void refreshList(ArrayList<DataContact> mListContact) {
        this.mListContact = mListContact;
        notifyDataSetChanged();
    }

    public class Holder {
        TextView tv_count;
        ImageView iv_image;
    }
}
