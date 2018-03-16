package com.wachat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.dataClasses.ContactSetget;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class AdapterAddToContact extends BaseAdapter/* implements View.OnClickListener*/ {

    private LayoutInflater layoutInflater;
    ArrayList<String> mChckedList = new ArrayList<String>();
    private ArrayList<ContactSetget> mList;
    private Context mContext;
//    public int pos;
    public AdapterAddToContact(Context mContext, ArrayList<ContactSetget> mList) {
        this.mList = mList;
        this.mContext = mContext;
        this.layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder mHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.inflate_add_to_contact_item,
                    null);

            mHolder = new ViewHolder();
            mHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            mHolder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            mHolder.img_call = (ImageView) convertView.findViewById(R.id.img_call);


            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.img_call.setTag(position);
        mHolder.tv_name.setText("Call " + mList.get(position).getContactType());
        mHolder.tv_number.setText(mList.get(position).getContactNumber());
        mHolder.img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mList.get(position).getContactNumber()));
                mContext.startActivity(callIntent);
            }
        });


        return convertView;

    }


    public class ViewHolder {

        public TextView tv_name, tv_number;
        public ImageView img_call;



    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.img_call:
//
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + mList.get(pos).getContactNumber()));
//                mContext.startActivity(callIntent);
//                break;
//        }
//    }


    public void refreshList(ArrayList<ContactSetget> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

//    public ContactSetget getCheckedValue() {
//        ContactSetget mContactSetget = mList.get(pos);
//        mContactSetget.setContactNumber(CommonMethods.commaSeperatedsStringsFromArray(mChckedList));
//        return mContactSetget;
//    }
}
