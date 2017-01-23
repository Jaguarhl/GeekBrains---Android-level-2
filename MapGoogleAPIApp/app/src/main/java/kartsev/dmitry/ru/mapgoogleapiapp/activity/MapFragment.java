package kartsev.dmitry.ru.mapgoogleapiapp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import kartsev.dmitry.ru.mapgoogleapiapp.R;
import kartsev.dmitry.ru.mapgoogleapiapp.location.LocationActivity;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Jag on 23.01.2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {
    TextView currentPositionText;
    LocationListener locationListener = null;
    LocationActivity locationActivity = null;

    LocationManager locationManager;
    GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        initViews(view);
        initLocationManager();
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        return view;
    }

    private void initViews(View view) {
        currentPositionText = (TextView) view.findViewById(R.id.current_position_text);
    }

    private void initLocationManager() {
        locationActivity = new LocationActivity(getContext());
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
    }

    private void setCurrentLocation(Location location) {
        currentPositionText.setText(locationActivity.getGeoCoordinates(location));
        LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(target, 15F);
        googleMap.animateCamera(camUpdate);
    }

    @Override
    public void onResume() {

        // Invoke a parent method, at first
        super.onResume();

        // Create Location Listener object (if needed)
        if (locationListener == null) locationListener = new LocListener();

        if (locationActivity == null) locationActivity = new LocationActivity(getContext());

        // Setting up Location Listener
        // min time - 3 seconds
        // min distance - 1 meter
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000L, 1.0F, locationListener);
        } else {
            Toast.makeText(getContext(), getString(R.string.error_not_all_permissions_granted), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {

        // Remove Location Listener
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && locationListener != null) {

            locationManager.removeUpdates(locationListener);
        }

        super.onPause();

    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap = map;
            googleMap.setMyLocationEnabled(true);
            // Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            // Enable zoom controls
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //Show my location at map
            final Location location = locationActivity.getLocation();

            // If location available
            if (location != null) {
                setCurrentLocation(location);
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.error_not_all_permissions_granted), Toast.LENGTH_LONG).show();
        }
    }

    public void showLocationOnMap(Location location) {
        if (location != null) {
            setCurrentLocation(location);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    /**
     * Class that implements Location Listener interface
     * */
    private final class LocListener implements LocationListener {

        /**
         *  Called when the location has changed.
         * */
        @Override
        public void onLocationChanged(Location location) {
            setCurrentLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    }
}
