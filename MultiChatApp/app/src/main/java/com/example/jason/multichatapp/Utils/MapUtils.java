package com.example.jason.multichatapp.Utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

/**
 * MapUtils - for map
 */

public class MapUtils {

    public static BitmapDescriptor createBubble(Context context, int style, String title) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setStyle(style);
        Bitmap bitmap = iconGenerator.makeIcon(title);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Marker addMarker(GoogleMap map, LatLng point, BitmapDescriptor icon) {
        MarkerOptions options = new MarkerOptions()
                .position(point)
                .icon(icon);
        return map.addMarker(options);
    }

}
