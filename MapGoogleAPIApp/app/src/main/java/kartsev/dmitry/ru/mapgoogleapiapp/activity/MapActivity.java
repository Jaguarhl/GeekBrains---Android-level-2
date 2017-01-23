package kartsev.dmitry.ru.mapgoogleapiapp.activity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import kartsev.dmitry.ru.mapgoogleapiapp.MainActivity;
import kartsev.dmitry.ru.mapgoogleapiapp.R;

/**
 * Created by Jag on 23.01.2017.
 */

public class MapActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MapActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        if(intent.getStringExtra(MainActivity.SEARCH_ADDRESS_ACTION).equalsIgnoreCase(MainActivity.SEARCH_ADDRESS_ACTION)) {
            Location loc = new Location(LocationManager.GPS_PROVIDER);
            double res = 0;
            intent.getDoubleExtra(MainActivity.SEARCH_ADDRESS_LATITUDE, res);
            loc.setLatitude(res);
            intent.getDoubleExtra(MainActivity.SEARCH_ADDRESS_LONGITUDE, res);
            loc.setLongitude(res);
            moveToLocation(loc);
        }

        initViews();        
        openFragment(new MapFragment());
    }

    private void initViews() {
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public void moveToLocation(Location location) {
        setContentView(R.layout.activity_map);
        MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.d(LOG_TAG, "Trying to move map to location");
        try {
            fragment.showLocationOnMap(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
