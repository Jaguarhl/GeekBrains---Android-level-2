package ru.dmitry.kartsev.mapgoogleapiapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

import ru.dmitry.kartsev.mapgoogleapiapp.MainActivity;
import ru.dmitry.kartsev.mapgoogleapiapp.R;
import ru.dmitry.kartsev.mapgoogleapiapp.data.MarkerItem;
import ru.dmitry.kartsev.mapgoogleapiapp.helpers.DBWork;
import ru.dmitry.kartsev.mapgoogleapiapp.location.LocationActivity;

/**
 * Created by Jag on 23.01.2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String LOG_TAG = "MapActivity";
    private TextView currentPositionText;
    private Toolbar toolbar;
    private LocationListener locationListener = null;
    private LocationActivity locationActivity = null;
    private Location searchLocation = null; // using only to find location by address

    private LocationManager locationManager;
    private GoogleMap googleMap = null;
    private boolean setCurLocation = true;
    private DBWork dbWork;

    private static List<MarkerItem> markersList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initLocationManager();
        initViews();
        initDrawer();
        dbWork = new DBWork(MapActivity.this);
        markersList = dbWork.initDatabase();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

        Intent intent = getIntent();
        Bundle b = getIntent().getExtras();
        if (b.getString(MainActivity.SEARCH_ADDRESS_ACTION).equalsIgnoreCase(MainActivity.SEARCH_ADDRESS_ACTION)) {
            searchLocation = new Location(LocationManager.GPS_PROVIDER);
            setCurLocation = false;
            double res = b.getDouble(MainActivity.SEARCH_ADDRESS_LATITUDE);
            searchLocation.setLatitude(res);
            res = b.getDouble(MainActivity.SEARCH_ADDRESS_LONGITUDE);
            searchLocation.setLongitude(res);
            Log.d(LOG_TAG, "Setting location to " + searchLocation.toString());
        }
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initLocationManager() {
        locationActivity = new LocationActivity(getBaseContext());
        locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
    }

    private void initDrawer() {
        new DrawerBuilder().withActivity(this).build();

        PrimaryDrawerItem primaryDrawerItem = new PrimaryDrawerItem();
        primaryDrawerItem.withName(R.string.side_menu_profile_1).withIcon(R.drawable.markers)
                .withSubItems(
                        new SecondaryDrawerItem().withName(getBaseContext().getResources().getString(R.string.mnu_markers_view_on_map)).withIdentifier(11),
                        new SecondaryDrawerItem().withName(getBaseContext().getResources().getString(R.string.mnu_markers_turn_off_on_map)).withIdentifier(12),
                        new SecondaryDrawerItem().withName(getBaseContext().getResources().getString(R.string.mnu_markers_view_list)).withIdentifier(13),
                        new SecondaryDrawerItem().withName(getBaseContext().getResources().getString(R.string.mnu_markers_delete_all)).withIdentifier(14));

        SecondaryDrawerItem secondaryDrawerItem = new SecondaryDrawerItem();
        secondaryDrawerItem.withName(getResources().getString(R.string.side_menu_profile_2)).withIcon(R.drawable.ic_launcher)
                .withSubItems(
                        new SecondaryDrawerItem().withName(getBaseContext().getResources().getString(R.string.mnu_mapmode_normal)).withIdentifier(21),
                        new SecondaryDrawerItem().withName(getBaseContext().getResources().getString(R.string.mnu_mapmode_satellite)).withIdentifier(22),
                        new SecondaryDrawerItem().withName(getBaseContext().getResources().getString(R.string.mnu_mapmode_tarrain)).withIdentifier(23),
                        new SecondaryDrawerItem().withName(getBaseContext().getResources().getString(R.string.mnu_mapmode_hybrid)).withIdentifier(24));

        SecondaryDrawerItem thirdDrawerItem = new SecondaryDrawerItem();
        thirdDrawerItem.withName(R.string.side_menu_profile_3).withIcon(R.drawable.search).withIdentifier(3);

        DividerDrawerItem dividerDrawerItem = new DividerDrawerItem();

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(primaryDrawerItem, secondaryDrawerItem, thirdDrawerItem, dividerDrawerItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        int id = (int) drawerItem.getIdentifier();
                        switch (id) {
                            case 11: {
                                if (markersList.size() > 0) {
                                    restoreMarkersOnMap();
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.error_marker_no_markers_to_display),
                                            Toast.LENGTH_LONG).show();
                                }
                                break;
                            }
                            case 12: {
                                googleMap.clear();
                                break;
                            }
                            case 13: {
                                MarkerListActivity.openView(MapActivity.this, markersList);
                                break;
                            }
                            case 21: {
                                if ((googleMap != null) & (googleMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL)) {
                                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                }
                                break;
                            }
                            case 22: {
                                if ((googleMap != null) & (googleMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE)) {
                                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                }
                                break;
                            }
                            case 23: {
                                if ((googleMap != null) & (googleMap.getMapType() != GoogleMap.MAP_TYPE_TERRAIN)) {
                                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                }
                                break;
                            }
                            case 24: {
                                if ((googleMap != null) & (googleMap.getMapType() != GoogleMap.MAP_TYPE_HYBRID)) {
                                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                }
                                break;
                            }
                            case 3: {
                                searchAddress();
                                break;
                            }
                        }
                        return false;
                    }
                })
                .build();
    }

    private void restoreMarkersOnMap() {
        googleMap.clear();
        for (MarkerItem item : markersList) {
            LatLng target = new LatLng(item.getMarkerLatitude(), item.getMarkerLongitude());
            try {
                googleMap.addMarker(new MarkerOptions()
                        .position(target)
                        .title(item.getMarkerName())
                        .snippet(item.getMarkerDescription())
                        .visible(true));
                Log.d(LOG_TAG, "Marker added on " + target.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void searchAddress() {
        LayoutInflater inflater = LayoutInflater.from(MapActivity.this);
        View addressDlg = inflater.inflate(R.layout.enter_address_dialog, null);

        AlertDialog.Builder mDialog = new AlertDialog.Builder(MapActivity.this);
        mDialog.setView(addressDlg);

        final EditText addressToSearch = (EditText) addressDlg.findViewById(R.id.editAddressToSearch);
        mDialog
                .setCancelable(false)
                .setPositiveButton(MapActivity.this.getResources().getString(R.string.btn_show),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setCurrentLocation(locationActivity.findLocationByAddress(
                                        addressToSearch.getText().toString()), true);
                            }
                        })
                .setNegativeButton(MapActivity.this.getResources().getString(R.string.btn_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = mDialog.create();
        alertDialog.show();
    }

    private void setCurrentLocation(Location location, boolean search) {
        if (googleMap != null) {
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(target, 15F);
            googleMap.animateCamera(camUpdate);
            if (search) {
                googleMap.addMarker(new MarkerOptions()
                        .position(target)
                        .title(locationActivity.getAddressByLoc(location)));
            }
            toolbar.setTitle(locationActivity.getCityByLoc(location));
            Log.d(LOG_TAG, "Displaying map with location " + location.toString());
        } else {
            Log.d(LOG_TAG, "Can't set location. GoogleMap is null.");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(LOG_TAG, "Map is ready.");
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap = map;
            googleMap.setMyLocationEnabled(true);
            // Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            // Enable zoom controls
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            restoreMarkersOnMap();

            //Show my location at map
            if (setCurLocation) {
                final Location location = locationActivity.getLocation();
                // If location available
                if (location != null) {
                    setCurrentLocation(location, false);
                }
            } else if (searchLocation != null) {
                setCurrentLocation(searchLocation, true);
                Log.d(LOG_TAG, "Displaying map with location " + searchLocation.toString());
            }

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {
                    Log.d(LOG_TAG, "onMapClick: " + latLng.latitude + "," + latLng.longitude);
                }
            });

            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng latLng) {
                    Log.d(LOG_TAG, "onMapLongClick: " + latLng.latitude + "," + latLng.longitude);
                    setMarker(latLng);
                }
            });

        } else {
            Toast.makeText(getBaseContext(), getString(R.string.error_not_all_permissions_granted),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setMarker(final LatLng latLng) {
        LayoutInflater inflater = LayoutInflater.from(MapActivity.this);
        View addressDlg = inflater.inflate(R.layout.add_marker_dialog, null);

        AlertDialog.Builder mDialog = new AlertDialog.Builder(MapActivity.this);
        mDialog.setView(addressDlg);

        final EditText markerName = (EditText) addressDlg.findViewById(R.id.editMarkerName);
        final EditText markerDescription = (EditText) addressDlg.findViewById(R.id.editMarkerDescription);
        mDialog
                .setCancelable(false)
                .setPositiveButton(MapActivity.this.getResources().getString(R.string.btn_add_marker),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (markerName.getText().length() > 3) {
                                    markersList.add(dbWork.addMarker(new MarkerItem(markerName.getText().toString(),
                                            markerDescription.getText().toString(), latLng.latitude,
                                            latLng.longitude, true)));
                                    Log.d(LOG_TAG, "Marker added on " + latLng.toString());
                                    restoreMarkersOnMap();
                                } else {
                                    Toast.makeText(MapActivity.this, getResources().getString(R.string.error_marker_name_is_empty),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton(MapActivity.this.getResources().getString(R.string.btn_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = mDialog.create();
        alertDialog.show();
    }
}
