package com.wachat.util;

/**
 * Created by Argha on 30-11-2015.
 */
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.wachat.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class GeoCoderUtil {

    public static String getAddress(LatLng latLng, Context context) {
        Geocoder geocoder = new Geocoder(context,Locale.getDefault());
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        String address = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                int addressLineIndex = returnedAddress.getMaxAddressLineIndex();

                int addressLinesToShow = 2;
                //              To get address in limited lines
                if (addressLineIndex < 2) {
                    addressLinesToShow = addressLineIndex;
                }
                for (int p = 0; p < addressLineIndex; p++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(p));
                    if(p>0)
                        strReturnedAddress.append("\n");
                }
                address = strReturnedAddress.toString();
            } else {
                address = context.getString(R.string.address_not_available);

            }
        } catch (IOException e) {
            e.printStackTrace();
            address = context.getString(R.string.address_not_available);
        }
        return address;
    }

    public static String getDistanceByUnit(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] distance = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude,
                endLongitude, distance);
        String distanceByUnit = "Not Available";

        DecimalFormat d = new DecimalFormat("0.00");
        if (distance[0] > 999.99) {
            distance[0] = distance[0] / 1000;
            distanceByUnit = String.valueOf(d.format(distance[0])) + " Km";
        } else {
            distanceByUnit = String.valueOf(d.format(distance[0])) + " m";
        }
        return distanceByUnit;
    }
}

