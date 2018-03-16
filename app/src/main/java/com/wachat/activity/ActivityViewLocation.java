package com.wachat.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;

/**
 * Created by Argha on 02-12-2015.
 */
public class ActivityViewLocation extends BaseActivity implements OnMapReadyCallback {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityViewLocation.class);

    public Toolbar appBar;
    private DataTextChat mDataTextChat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        initComponent();
        getValuesFromIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);
    }


    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.viewLocation));

    }

    private void getValuesFromIntent() {

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey(Constants.B_RESULT)) {
            mDataTextChat = (DataTextChat) mBundle.getSerializable(Constants.B_RESULT);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng loc = new LatLng(Double.parseDouble(mDataTextChat.getLoc_lat().trim()),
                Double.parseDouble(mDataTextChat.getLoc_long().trim()));
        LogUtils.i(LOG_TAG,"Location showed: "+mDataTextChat.getLoc_lat() + " ," + mDataTextChat.getLoc_long());
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));

        map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_icon))
                        .title(CommonMethods.getUTFDecodedString(mDataTextChat.getLoc_address()))
                        .position(loc));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
