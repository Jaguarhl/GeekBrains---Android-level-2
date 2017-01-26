package ru.dmitry.kartsev.mapgoogleapiapp.location;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.dmitry.kartsev.mapgoogleapiapp.R;

import static ru.dmitry.kartsev.mapgoogleapiapp.R.id.map;

public class LocationActivity extends Service implements LocationListener {

    public static final String LOG_TAG = "LocationActivity";
    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    String place; // address

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public LocationActivity(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public String getLocationAddress() {
        if(location != null) {
            try {
                Geocoder gCoder = new Geocoder(mContext, Locale.getDefault());
                List<Address> addresses = gCoder.getFromLocation(this.latitude, this.longitude, 1);
                if ((addresses != null) && (addresses.size() > 0)) {
                    place = addresses.get(0).getCountryName() + " " +addresses.get(0).getAdminArea() +
                            " " + addresses.get(0).getThoroughfare() + " " + addresses.get(0).getSubThoroughfare();
                    Log.d(LOG_TAG, "Place detected as " + place);
                } else {
                    Log.d(LOG_TAG,"NO-RESULT");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return place;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public String getAddressByLoc(Location loc) {

        if (location != null) {

            // Create geocoder
            final Geocoder geo = new Geocoder(mContext);

            // Try to get addresses list
            List<Address> list;
            try {
                list = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 5);
            } catch (IOException e) {
                e.printStackTrace();
                return e.getLocalizedMessage();
            }

            // If list is empty, return "No data" string
            if (list.isEmpty()) return getString(R.string.no_data);

            // Get first element from List
            Address a = list.get(0);

            // Get a Postal Code
            final int index = a.getMaxAddressLineIndex();
            String postal = null;
            if (index >= 0) {
                postal = a.getAddressLine(index);
            }

            // Make address string
            StringBuilder builder = new StringBuilder();
            final String sep = ", ";
            builder.append(a.getCountryName()).append(sep)
                    .append(a.getAdminArea()).append(sep)
                    .append(a.getThoroughfare()).append(sep)
                    .append(a.getSubThoroughfare());

            return builder.toString();
        }
        return null;
    }

    public String getGeoCoordinates(Location location) {
        String address = mContext.getString(R.string.address) + ": " + getAddressByLoc(location);
        String coordinates = mContext.getString(R.string.latitude) + ": " + location.getLatitude() + ", "
                + mContext.getString(R.string.longitude) + ": " + location.getLongitude();

        return address + "\n" + coordinates;
    }

    public Location findLocationByAddress(String address) {
        Location returnLocation = getLocation();
        if(address != null || !address.equalsIgnoreCase("")) {
            Log.d(LOG_TAG, "Trying to extract address to location object");
            Geocoder geo = new Geocoder(mContext);
            List<Address> addressList = null;
            try {
                addressList = geo.getFromLocationName(address, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addressList != null) {
                Address searchAddress = addressList.get(0);
                returnLocation = new Location(LocationManager.GPS_PROVIDER);
                returnLocation.setLongitude(searchAddress.getLongitude());
                returnLocation.setLatitude(searchAddress.getLatitude());
            }
        }
        return returnLocation;
    }

    public String getCityByLoc(Location loc) {
        if (location != null) {

            // Create geocoder
            final Geocoder geo = new Geocoder(mContext);

            // Try to get addresses list
            List<Address> list;
            try {
                list = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 5);
            } catch (IOException e) {
                e.printStackTrace();
                return e.getLocalizedMessage();
            }

            // If list is empty, return "No data" string
            if (list.isEmpty()) return getString(R.string.no_data);

            // Get first element from List
            Address a = list.get(0);

            // Get a Postal Code
            final int index = a.getMaxAddressLineIndex();
            String postal = null;
            if (index >= 0) {
                postal = a.getAddressLine(index);
            }

            // Make address string
            StringBuilder builder = new StringBuilder();
            builder.append(a.getAdminArea());

            return builder.toString();
        }
        return null;
    }
}