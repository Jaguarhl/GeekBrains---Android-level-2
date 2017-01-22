package kartsev.dmitry.ru.mapgoogleapiapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import kartsev.dmitry.ru.mapgoogleapiapp.location.LocationActivity;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MA";
    public static final String SEARCH_STRING_BY_ADDRESS = "geo:0,0?q=";
    public static final String GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps";
    public static final String SEARCH_STRING_CAFE = "кафе, ресторан, бистро";
    LocationActivity locationActivity;
    private final Context mContext = this;

    Button btnViewOnMap, btnFindAddress, btnFindObjectsNear;
    ImageButton btnGetLocation;
    TextView currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationActivity = new LocationActivity(mContext);

        initViews();
        setButtonsBehavior();
    }

    private void initViews() {
        btnViewOnMap = (Button) findViewById(R.id.btnShowOnMap);
        btnGetLocation = (ImageButton) findViewById(R.id.ibtnSetCurrentLocation);
        btnFindAddress = (Button) findViewById(R.id.btnFindAddress);
        btnFindObjectsNear = (Button) findViewById(R.id.btnFindCafeNear);
        currentLocation = (TextView) findViewById(R.id.textLocation);
    }

    private void setButtonsBehavior() {
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Trying to get location");
                String s = null;
                try {
                    s = locationActivity.getLocationAddress();
                } catch (IOException e) {
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
                if(currentLocation.getText().length() > 0) {
                    showOnMapCurrent();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error_no_location_set),
                            Toast.LENGTH_LONG).show();
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
                                        SearchAndShowAddress(addressToSearch.getText().toString());
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

    private void SearchAndShowAddress(String address) {
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
}
