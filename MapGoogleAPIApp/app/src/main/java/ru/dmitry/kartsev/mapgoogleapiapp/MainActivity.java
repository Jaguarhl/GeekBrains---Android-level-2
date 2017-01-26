package ru.dmitry.kartsev.mapgoogleapiapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import ru.dmitry.kartsev.mapgoogleapiapp.activity.MapActivity;
import ru.dmitry.kartsev.mapgoogleapiapp.location.LocationActivity;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MA";
    public static final String SEARCH_STRING_BY_ADDRESS = "geo:0,0?q=";
    public static final String GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps";
    public static final String SEARCH_STRING_CAFE = "кафе, ресторан, бистро";
    public static final String SEARCH_ADDRESS_LATITUDE = "SEARCH_ADDRESS_LATITUDE";
    public static final String SEARCH_ADDRESS_LONGITUDE = "SEARCH_ADDRESS_LONGITUDE";
    public static final String SEARCH_ADDRESS_ACTION = "SEARCH_ADDRESS_ACTION";
    public static final String SEARCH_ADDRESS_REQUEST = "SEARCH_ADDRESS_REQUEST";
    LocationActivity locationActivity;
    private final Context mContext = this;

    Button btnViewOnMap, btnFindAddress, btnFindObjectsNear;
    ImageButton btnGetLocation;
    TextView currentLocation;
    CheckBox checkViewMapInside;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationActivity = new LocationActivity(mContext);

        checkPermissions();
        initViews();
        setButtonsBehavior();
    }

    private void initViews() {
        btnViewOnMap = (Button) findViewById(R.id.btnShowOnMap);
        btnGetLocation = (ImageButton) findViewById(R.id.ibtnSetCurrentLocation);
        btnFindAddress = (Button) findViewById(R.id.btnFindAddress);
        btnFindObjectsNear = (Button) findViewById(R.id.btnFindCafeNear);
        currentLocation = (TextView) findViewById(R.id.textLocation);
        try {
            currentLocation.setText(locationActivity.getLocationAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkViewMapInside = (CheckBox) findViewById(R.id.checkViewMapInside);
        checkViewMapInside.setChecked(true);
    }

    private void setButtonsBehavior() {
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Trying to get location");
                String s = null;
                try {
                    s = locationActivity.getLocationAddress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(s != null) {
                    currentLocation.setText(s);
                } else {
                    Log.d(LOG_TAG, "Location is empty");
                }
            }
        });

        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkViewMapInside.isChecked()) {
                    Intent intent = new Intent(mContext, MapActivity.class);
                    intent.putExtra(SEARCH_ADDRESS_ACTION, "");
                    mContext.startActivity(intent);
                } else {
                    if (currentLocation.getText().length() > 0) {
                        showOnMapCurrent();
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.error_no_location_set),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnFindAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View addressDlg = inflater.inflate(R.layout.enter_address_dialog, null);

                AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
                mDialog.setView(addressDlg);

                final EditText addressToSearch = (EditText) addressDlg.findViewById(R.id.editAddressToSearch);
                mDialog
                        .setCancelable(false)
                        .setPositiveButton(mContext.getResources().getString(R.string.btn_show),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        searchAndShowAddress(addressToSearch.getText().toString());
                                    }
                                })
                        .setNegativeButton(mContext.getResources().getString(R.string.btn_cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = mDialog.create();
                alertDialog.show();
            }
        });

        btnFindObjectsNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLocation.getText().length() > 0) {
                    showOnMapCafeNear();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error_no_location_set),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showOnMapCafeNear() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SEARCH_STRING_BY_ADDRESS +
                    currentLocation.getText().toString() + SEARCH_STRING_CAFE));
            intent.setPackage(GOOGLE_MAPS_PACKAGE);
            this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.error_no_service_found),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void searchAndShowAddress(String address) {
        if (checkViewMapInside.isChecked()) {
            Location loc = locationActivity.findLocationByAddress(address);
            Log.d(LOG_TAG, loc.toString());
            Intent intent = new Intent(mContext, MapActivity.class);
            Bundle b = new Bundle();
            b.putString(SEARCH_ADDRESS_ACTION, SEARCH_ADDRESS_ACTION);
            b.putDouble(SEARCH_ADDRESS_LATITUDE, loc.getLatitude());
            b.putDouble(SEARCH_ADDRESS_LONGITUDE, loc.getLongitude());
            intent.putExtras(b);
            mContext.startActivity(intent);
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SEARCH_STRING_BY_ADDRESS +
                        address));
                intent.setPackage(GOOGLE_MAPS_PACKAGE);
                this.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_no_service_found),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showOnMapCurrent() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SEARCH_STRING_BY_ADDRESS +
                    currentLocation.getText().toString()));
            intent.setPackage(GOOGLE_MAPS_PACKAGE);
            this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.error_no_service_found),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, getString(R.string.error_not_all_permissions_granted), Toast.LENGTH_LONG).show();
        }
    }
}
