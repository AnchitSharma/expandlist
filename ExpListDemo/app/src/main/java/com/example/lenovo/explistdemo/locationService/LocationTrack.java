package com.example.lenovo.explistdemo.locationService;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.lenovo.explistdemo.MainActivity;
import com.example.lenovo.explistdemo.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lenovo on 20-08-2018.
 */

public class LocationTrack extends Service implements LocationListener {
    private static final String TAG = "LocationTrack";
    private Context context;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;
    Location loc;
    double latitude, longitude;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    public static final long MIN_TIME_BTW_UPDATES = 1000 * 60 * 2;
    protected LocationManager locationManager;
    private Handler handler;


    public LocationTrack(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //getLocation();
    }


    public Location getLocation(String provider) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: jlloo");
            if (isGpsEnabled()) {

                locationManager.requestLocationUpdates(provider, MIN_TIME_BTW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    loc = locationManager.getLastKnownLocation(provider);
                    return loc;
                }
            }
        }

        return null;
    }


    public boolean isGpsEnabled() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (TextUtils.isEmpty(provider)) {
                return false;
            }
            return provider.contains(LocationManager.GPS_PROVIDER);
        } else {
            final int locationMode;
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            switch (locationMode) {
                case Settings.Secure.LOCATION_MODE_HIGH_ACCURACY:
                case Settings.Secure.LOCATION_MODE_SENSORS_ONLY:
                    return true;
                case Settings.Secure.LOCATION_MODE_BATTERY_SAVING:
                case Settings.Secure.LOCATION_MODE_OFF:
                default:
                    return false;

            }
        }
    }


    private Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!checkGPS && !checkNetwork) {
                Toast.makeText(context, "No Service Provider is Available", Toast.LENGTH_SHORT).show();
            } else {
                canGetLocation = true;

                if (checkGPS) {

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BTW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (loc != null) {
                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();
                            }
                        }
                    }
                }


                if (checkNetwork) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BTW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (loc != null) {
                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "getLocation: " + e.getMessage());
        }

        return loc;
    }


    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }


    public boolean canGetLocation() {
        return canGetLocation;
    }


    public void showAlertSettings() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("GPS is not Enabled!");
        alertBuilder.setMessage("Do you want to turn on GPS?");

        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();
    }


    public void stopListener() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BTW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            locationManager.removeUpdates(LocationTrack.this);
        }
    }

    public static final String BASEURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";//location=26.4734113,80.348275"
    public static final String PARAMETERS = "&sensor=true&rankby=distance&key=AIzaSyA3WyIFpem1UhuFTrrCwuICVGGGB47O6mg&types=food&opennow=true";

    @Override
    public void onLocationChanged(Location location) {
        //adressGeoCoder(location);

        if (location != null) {
            final double lat = location.getLatitude();
            final double lng = location.getLongitude();
            String url = BASEURL + "location=" + lat + "," + lng + PARAMETERS;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response!=null){
                                String add=null;
                                try {
                                    JSONObject root = new JSONObject(response);
                                    JSONArray results = root.getJSONArray("results");
                                    JSONObject obj1 = results.getJSONObject(0);

                                    add = obj1.optString("vicinity");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }finally {
                                    Message message = Message.obtain();
                                    message.setTarget(handler);
                                    if (add!=null){
                                        message.what =1 ;
                                        Bundle bundle = new Bundle();
                                        add = "Latitude: " + lat + " Longitude: " + lng +
                                                "\n\n "+add;
                                        bundle.putString("address",add);
                                        message.setData(bundle);
                                    }else{
                                        message.what = 1;
                                        Bundle bundle = new Bundle();
                                        add = "Latitude: " + lat + " Longitude: " + lng +
                                                "\n\n Unable to get address from this Lat-lon";
                                        bundle.putString("address",add);
                                        message.setData(bundle);
                                    }

                                    message.sendToTarget();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            MyApplication.getInstance().addToRequestQueue(stringRequest);
        }

    }

    private void adressGeoCoder(Location location) {
        if (location != null) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            String res = null;

            try {
                List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }

                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName()).append("\n");
                    res = sb.toString();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Message message = Message.obtain();
                message.setTarget(handler);
                if (res != null) {
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    res = "Latitude: " + lat + " Longitude: " + lon + "\n\n" + res;
                    bundle.putString("address", res);
                    message.setData(bundle);
                } else {
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    res = "Latitude: " + lat + " Longitude: " + lon +
                            "\n\n Unable to get address from this Lat-lon";
                    bundle.putString("address", res);
                    message.setData(bundle);
                }
                message.sendToTarget();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
