package ru.dmitry.kartsev.mapgoogleapiapp.data;

/**
 * Created by Jag on 25.01.2017.
 */

public class MarkerItem {
    private double markerLatitude;
    private double markerLongitude;
    private String markerName;
    private String markerDescription;
    private boolean markerActive;
    private int markerIdInDB;

    public MarkerItem(int idInDB, String name, String desc, double lat, double lng, boolean active) {
        this.markerIdInDB = idInDB;
        this.markerName = name;
        this.markerDescription = desc;
        this.markerLatitude = lat;
        this.markerLongitude = lng;
        this.markerActive = active;
    }

    public MarkerItem(String name, String desc, double lat, double lng, boolean active) {
        this.markerName = name;
        this.markerDescription = desc;
        this.markerLatitude = lat;
        this.markerLongitude = lng;
        this.markerActive = active;
    }

    public double getMarkerLatitude() {
        return markerLatitude;
    }

    public void setMarkerLatitude(double markerLatitude) {
        this.markerLatitude = markerLatitude;
    }

    public double getMarkerLongitude() {
        return markerLongitude;
    }

    public void setMarkerLongitude(double markerLongitude) {
        this.markerLongitude = markerLongitude;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getMarkerDescription() {
        return markerDescription;
    }

    public void setMarkerDescription(String markerDescription) {
        this.markerDescription = markerDescription;
    }

    public boolean isMarkerActive() {
        return markerActive;
    }

    public void setMarkerActive(boolean markerActive) {
        this.markerActive = markerActive;
    }

    public int getMarkerIdInDB() {
        return markerIdInDB;
    }

    public void setMarkerIdInDB(int markerIdInDB) {
        this.markerIdInDB = markerIdInDB;
    }
}
