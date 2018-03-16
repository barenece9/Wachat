package com.wachat.adapter;//package com.wachat.adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.wachat.R;
//
///**
// * Created by Priti Chatterjee on 04-09-2015.
// */
//public class AdapterGroupDetailsImages extends RecyclerView.Adapter<AdapterGroupDetailsImages.VH> {
//
//    private int width;
//    private ImageLoader mImageLoader;
//
//    public AdapterGroupDetailsImages(int width, ImageLoader mImageLoader) {
//        this.width = (int) (width / 4.5f);
//        this.mImageLoader = mImageLoader;
//    }
//
//    @Override
//    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
//        ImageView mImageView = new ImageView(parent.getContext());
//        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        mImageView.setPadding(5, 5, 5, 5);
//        mImageView.setLayoutParams(new RecyclerView.LayoutParams(width, width));
//        return new VH(mImageView);
//    }
//
//    @Override
//    public void onBindViewHolder(VH holder, int position) {
//        if(position %2 == 0)
//            holder.mImageView.setImageResource(R.drawable.ic_more_bg);
//        else
//            holder.mImageView.setImageResource(R.drawable.ic_notificaton_red_circle);
//    }
//
//    @Override
//    public int getItemCount() {
//        return 50;
//    }
//
//    public static class VH extends RecyclerView.ViewHolder {
//        private ImageView mImageView;
//
//        public VH(View itemView) {
//            super(itemView);
//            mImageView = (ImageView) itemView;
//        }
//    }
//}
