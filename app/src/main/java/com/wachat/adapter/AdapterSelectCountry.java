package com.wachat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataCountry;
import com.wachat.util.StringMatcher;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class AdapterSelectCountry extends BaseAdapter implements SectionIndexer,View.OnClickListener {

    private String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private LayoutInflater layoutInflater;
    private ArrayList<DataCountry> mListCountry;
    private AdapterCallback mCallback;

    public AdapterSelectCountry(Context mContext, ArrayList<DataCountry> mListCountry, AdapterCallback mCallback) {
        this.mListCountry = mListCountry;
        this.layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mCallback=mCallback;
    }

    @Override
    public int getCount() {
        return mListCountry.size();
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

        final ViewHolder mHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.inflater_select_country_screen,
                    null);

            mHolder = new ViewHolder(convertView);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.pos = position;
        mHolder.tv_country.setText(mListCountry.get(position).getCountryName());
        mHolder.tv_code.setText("+"+ mListCountry.get(position).getCountryCode());
        convertView.setOnClickListener(this);
        return convertView;

    }

    @Override
    public void onClick(View v) {
        mCallback.OnClickPerformed(getList().get(((ViewHolder)v.getTag()).pos));
    }

    public ArrayList<DataCountry> getList(){
        return mListCountry;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_country, tv_code;
        public int pos;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_country = (TextView) itemView
                    .findViewById(R.id.tv_country);
            tv_code = (TextView) itemView.findViewById(R.id.tv_code);

        }
    }


    //index section override methods
    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
    }


    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public int getPositionForSection(int section) {
        // If there is no item for current section, previous section will be
        // selected
        for (int i = section; i >= 0; i--) {
            for (int j = 0; j < getCount(); j++) {
                if (i == 0) {
                    // For numeric section
                    for (int k = 0; k <= 9; k++) {
                        if (StringMatcher.match(
                                String.valueOf(mListCountry.get(j).getCountryName().charAt(0)),
                                String.valueOf(k)))
                            return j;
                    }
                } else {
                    if (StringMatcher.match(
                            String.valueOf(mListCountry.get(j).getCountryName().charAt(0)),
                            String.valueOf(mSections.charAt(i))))
                        return j;
                }
            }
        }
        return 0;
    }

    public void refreshList(ArrayList<DataCountry> mListCountry){
        this.mListCountry = mListCountry;
        this.notifyDataSetChanged();
    }

}
