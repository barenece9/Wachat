package com.wachat.adapter;//package com.wachat.adapter;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//import com.wachat.R;
//import com.wachat.data.DataYahooNews;
//
//import java.util.ArrayList;
//
//
//public class AdapterYahooNews extends BaseAdapter {
//
//    ImageLoader mImageLoader;
//    private LayoutInflater mInflater;
//    private Context mContext;
//    private ArrayList<DataYahooNews> dataYahooNewses = null;
//
//
//
//    public AdapterYahooNews(Context context) {
//        mContext = context;
//        mInflater = LayoutInflater.from(mContext);
//        mImageLoader = ImageLoader.getInstance();
//        dataYahooNewses = new ArrayList<DataYahooNews>();
//    }
//
//    public void refreshAdapter(ArrayList<DataYahooNews> dataYahooNewsArrayList) {
//        if (dataYahooNewsArrayList != null && dataYahooNewsArrayList.size() > 0)
//            dataYahooNewses.clear();
//        dataYahooNewses.addAll(dataYahooNewsArrayList);
//        notifyDataSetChanged();
//
//    }
//
//    @Override
//    public int getCount() {
//        if (dataYahooNewses != null && dataYahooNewses.size() > 0)
//            return dataYahooNewses.size();
//        else
//            return 0;
//
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @SuppressLint("ViewHolder")
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        ViewHolder mViewHolder;
//        if (convertView == null) {
//
//            convertView = mInflater.inflate(R.layout.inflater_yahoo_news_list, null);
//            mViewHolder = new ViewHolder();
//            mViewHolder.iv_yahoo_thumb = (ImageView) convertView.findViewById(R.id.iv_yahoo_thumb);
//            mViewHolder.tv_yahoo_news_title = (TextView) convertView.findViewById(R.id.tv_yahoo_news_title);
//            mViewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
//            mViewHolder.tv_pubdate = (TextView) convertView.findViewById(R.id.tv_pubdate);
//
//            convertView.setTag(mViewHolder);
//        } else {
//            mViewHolder = (ViewHolder) convertView.getTag();
//        }
//        mImageLoader.displayImage(dataYahooNewses.get(position).getUrl(),
//                mViewHolder.iv_yahoo_thumb, new ImageLoadingListener() {
//
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String imageUri, View view,
//                                                FailReason failReason) {
//
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view,
//                                                  Bitmap loadedImage) {
////                        Animation anim = AnimationUtils.loadAnimation(mContext,
////                                R.anim.fade_in);
////                        view.setAnimation(anim);
////                        anim.start();
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String imageUri, View view) {
//
//                    }
//                });
//        mViewHolder.tv_yahoo_news_title.setText(dataYahooNewses.get(position).getTitle());
//        mViewHolder.tv_desc.setText(dataYahooNewses.get(position).getDescription());
//        mViewHolder.tv_pubdate.setText(dataYahooNewses.get(position).getPubDate());
//        return convertView;
//    }
//
//
//
//
//
//    private class ViewHolder {
//        private ImageView iv_yahoo_thumb;
//        private TextView tv_yahoo_news_title;
//        private TextView tv_desc;
//        private TextView tv_pubdate;
//
//    }
//}
