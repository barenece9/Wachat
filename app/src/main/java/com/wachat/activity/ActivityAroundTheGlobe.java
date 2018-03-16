package com.wachat.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncFindPeopleByLocation;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.DataContact;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.ConstantDB;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by Argha on 02-12-2015.
 */
public class ActivityAroundTheGlobe extends BaseActivity implements OnMapReadyCallback, PlaceSelectionListener,
        InterfaceResponseCallback, View.OnClickListener {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityAroundTheGlobe.class);
    public Toolbar appBar;
    private String searchText = "";
    private ImageView img_map_to_grid;
    private String gender = "";
    private GoogleMap map = null;
    private float mapZoomLvl = 8;
//    private TableContact mTableContact;
    private ArrayList<DataContact> mArrayList = new ArrayList<>();
    private LatLng currLatLng = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_globe);
        //getBundleData();
        initComponent();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);
//        mTableContact = new TableContact(mActivity);

    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);

        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                this.getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Country, Place, Location");
        autocompleteFragment.setOnPlaceSelectedListener(this);
        initSuperViews();
        tv_act_name.setText(R.string.around_the_globe);
        img_map_to_grid = (ImageView) findViewById(R.id.img_map_to_grid);
        img_map_to_grid.setOnClickListener(this);


        currLatLng = new LatLng(Double.valueOf(AppVhortex.Lat),
                Double.valueOf(AppVhortex.Long));
        getDataFromAPI(currLatLng);


    }

    private void getBundleData() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey(Constants.B_RESULT)) {
            searchText = mBundle.getString(Constants.B_RESULT);
            LogUtils.i(LOG_TAG, searchText);
        }
    }


    private void getDataFromAPI(LatLng mLatLng) {
        if (mLatLng.latitude != 0.0 && mLatLng.longitude != 0.0 && CommonMethods.isOnline(this)) {

            AsyncFindPeopleByLocation mAsyncFindPeopleByLocation = new AsyncFindPeopleByLocation(mDataProfile.getUserId(),
                    gender, searchText, String.valueOf(mLatLng.latitude),
                    String.valueOf(mLatLng.longitude),"50",this);

            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mAsyncFindPeopleByLocation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mAsyncFindPeopleByLocation.execute();
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        map.getUiSettings().setMapToolbarEnabled(false);
        currLatLng = new LatLng(Double.valueOf(AppVhortex.Lat),
                Double.valueOf(AppVhortex.Long));
        getDataFromAPI(currLatLng);
        replaceMarkerWithMoveMap(currLatLng);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                currLatLng = latLng;
                replaceMarkerWithMoveMap(latLng);
                getDataFromAPI(latLng);
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!TextUtils.isEmpty(marker.getSnippet())) {
                    if (!mArrayList.isEmpty()) {
                        int pos = Integer.parseInt(marker.getSnippet());
                        DataContact mDataContact = mArrayList.get(pos);

                        Intent mIntent = new Intent(ActivityAroundTheGlobe.this, ActivityChat.class);
                        mIntent.putExtra(Constants.B_ID, mDataContact.getFriendId());
                        mIntent.putExtra(Constants.B_RESULT, mDataContact.getChatId());
                        mIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_SINGLECHAT);
                        mIntent.putExtra(ConstantDB.ChatId, mDataContact.getChatId());
                        mIntent.putExtra(ConstantDB.PhoneNo, mDataContact.getCountryCode() + mDataContact.getPhoneNumber());
                        mIntent.putExtra(ConstantDB.FriendImageLink, mDataContact.getAppFriendImageLink());
                        startActivity(mIntent);
                    }

                }
            }
        });
    }

    private void replaceMarkerWithMoveMap(LatLng latLng) {

        map.clear();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mapZoomLvl);
        map.animateCamera(cameraUpdate);
        addMyMarkerToMap(latLng);
    }

    private void addMyMarkerToMap(LatLng latLng) {

        Marker marker = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_icon))
                .title("You")
                .snippet("")
                .position(latLng));
        marker.showInfoWindow();
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

    @Override
    public void onPlaceSelected(Place place) {
        if(place!=null) {
            currLatLng = place.getLatLng();
            getDataFromAPI(place.getLatLng());
            if (map != null)
                replaceMarkerWithMoveMap(place.getLatLng());
        }
    }

    @Override
    public void onError(Status status) {
        LogUtils.i(LOG_TAG,"Place : An error occurred: " + status);
    }

    @Override
    public void onResponseObject(Object mObject) {

    }

    @Override
    public void onResponseList(ArrayList<?> mList) {

        if (mList != null) {
            mArrayList = (ArrayList<DataContact>) mList;
            if (!mArrayList.isEmpty()) {
                // map.clear();
                for (int i = 0; i < mArrayList.size(); i++) {
                    DataContact mDataContact = mArrayList.get(i);

                    addMultipleMarkerToMap(new LatLng(Double.parseDouble(mDataContact.getLat()), Double.parseDouble(mDataContact.getLng())), mDataContact, i);
                }
            }
        }
    }


    private void addMultipleMarkerToMap(LatLng latLng, DataContact mDataContact, int position) {
        String nameOrNum = "";
        if (!TextUtils.isEmpty(mDataContact.getName()))
            nameOrNum = CommonMethods.getUTFDecodedString(mDataContact.getName());
        else
            nameOrNum = "+" + mDataContact.getCountryCode() + mDataContact.getPhoneNumber();
        Marker marker = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_icon))
                .title(nameOrNum)
                .snippet(String.valueOf(position))
                .position(latLng));
        marker.showInfoWindow();

    }

    @Override
    public void onResponseFaliure(String responseText) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_map_to_grid:

                    Intent int1 = new Intent(ActivityAroundTheGlobe.this, ActivityChoosePeople.class);

                    Bundle mBundle = new Bundle();
                    mBundle.putString(Constants.B_RESULT, searchText);
                    mBundle.putString(ConstantDB.lat, String.valueOf(currLatLng.latitude));
                    mBundle.putString(ConstantDB.lng, String.valueOf(currLatLng.longitude));
                    mBundle.putString(Constants.B_TYPE, "FindNearByLoc");
                    int1.putExtras(mBundle);

                    startActivity(int1);
                break;
        }
    }
}
