package com.wachat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.wachat.R;

public class AdapterMarkerInfoWindow implements InfoWindowAdapter {

	private Context mContext;
	private String markerSnippet = "";

	public AdapterMarkerInfoWindow(Context _mContext) {
		this.mContext = _mContext;
	}

	@Override
	public View getInfoContents(Marker marker) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.inflater_map_infowindow, null);
		View v = inflater.inflate(R.layout.inflater_map_infowindow, null);
		TextView tv_title = (TextView) v.findViewById(R.id.title);
		TextView tv_subtitle = (TextView) v.findViewById(R.id.subTitle);

		tv_title.setText(marker.getTitle().toString());
		tv_subtitle.setText(marker.getSnippet().toString());
		//tv_title.setText(marker.getTitle().toString());
//		markerSnippet = marker.getSnippet().toString();
	//	String[] arr = markerSnippet.split("regexratingmappper");

//		if (arr != null && arr[0] != null) {
//			tv_map_snippet.setText(arr[0]);
//		}
//
//
//		if (arr != null && arr[1] != null) {
//			tv_clinic_rating.setText(arr[1]);
//		}

		return v;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
