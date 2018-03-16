package com.wachat.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wachat.R;
import com.wachat.activity.ActivityChoosePeople;
import com.wachat.activity.ActivityMap;
import com.wachat.adapter.AdapterMarkerInfoWindow;
import com.wachat.dataClasses.LocationGetSet;
import com.wachat.util.GeoCoderUtil;

import org.apache.commons.lang3.StringUtils;


public class FragmentMap extends Fragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener {

    public static FragmentMap getInstance() {
        return new FragmentMap();
    }

    private GoogleApiClient mGoogleApiClient;
    private UiSettings mUiSettings;
    private GoogleMap mMap;
    private ActivityMap mActivityMap;
    LocationManager lm;
    private MarkerOptions marker = null;
    CameraUpdate cameraUpdate;

    // These settings are the same as the settings for the map. They will in
    // fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000) // 5 seconds
            .setFastestInterval(16) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        LoadMapFragment();
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivityMap = (ActivityMap) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void LoadMapFragment() {

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.mapview);
        //  mapFragment.getMapAsync(FragmentMap.this);

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapview)).getMapAsync(this);

        LocationManager locationManager = (LocationManager) mActivityMap
                .getSystemService(Context.LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled && isGPSEnabled)
            mGoogleApiClient = new GoogleApiClient.Builder(mActivityMap)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        else
            mActivityMap.showGPSSettingsAlert();
    }

    /**
     * Button to get current Location. This demonstrates how to get the current
     * Location as required without needing to register a LocationListener.
     */
    public void showMyLocation(View view) {

        if (mGoogleApiClient.isConnected()) {
            String msg = "Location = "
                    + LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            Toast.makeText(mActivityMap.getApplicationContext(), msg, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMapClickListener(this);
//		map.addMarker(new MarkerOptions().position(new LatLng(mLocation.getLatitude(),mLocation.getLongitude())).title(
//				"Marker"));
        mapUISettings(map);
    }

    private void mapUISettings(GoogleMap map) {
        map.setMyLocationEnabled(true);
        mUiSettings = map.getUiSettings();

        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(true);

        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        // mMessageView.setText("Location = " + location);
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
//        mMap.animateCamera(cameraUpdate);
        // lm.removeUpdates(mActivityMap);
        //setMarkerOnMap(location);
    }

    /**
     * Callback called when connected to GCore. Implementation of
     * {@link GoogleApiClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //Location mInitLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            setMarkerOnMap(mLatLng);
        }
    }

    /**
     * Callback called when disconnected from GCore. Implementation of
     * {@link GoogleApiClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Toast.makeText(mActivityMap, "Connection disconnected !Please try again.", Toast.LENGTH_SHORT).show();
        // Do nothing
    }

    /**
     * Implementation of {@link GoogleApiClient.OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }


    private void setMarkerOnMap(LatLng mlatLong) {

        if (mlatLong != null) {
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mlatLong, 17));

            marker = new MarkerOptions();
            mMap.addMarker(marker.position(mlatLong)
                    .title(GeoCoderUtil.getAddress(mlatLong, mActivityMap))
                    .snippet(mlatLong.latitude + " , " + mlatLong.longitude)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_icon)));
        } else {
            Toast.makeText(mActivityMap, "Cannot fetch location", Toast.LENGTH_SHORT).show();
        }


        mMap.setInfoWindowAdapter(new AdapterMarkerInfoWindow(
                mActivityMap));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
//                mMap.animateCamera(CameraUpdateFactory
//                        .newLatLngZoom(marker.getPosition(),
//                                15.0f));
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker mMarker) {
                LocationGetSet mLocationGetSet = new LocationGetSet();
                mLocationGetSet.setLat(StringUtils.split(mMarker.getSnippet().trim(), ",")[0]);
                mLocationGetSet.setLong(StringUtils.split(mMarker.getSnippet().trim(), ",")[1]);
                mLocationGetSet.setAddress(mMarker.getTitle());
                if (!mLocationGetSet.getAddress().equalsIgnoreCase(getString(R.string.address_not_available)))
                    mActivityMap.backToChat(mLocationGetSet);
            }

        });

    }

    //click on map and put marker there
    @Override
    public void onMapClick(LatLng latLng) {

        setMarkerOnMap(latLng);
        //cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        //mMap.animateCamera(cameraUpdate);

    }


}
