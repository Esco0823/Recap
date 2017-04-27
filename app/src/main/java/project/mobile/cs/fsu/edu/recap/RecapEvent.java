package project.mobile.cs.fsu.edu.recap;

import android.location.Location;

/**
 * Created by Brandon on 4/25/17.
 * Object which contains information about different events that may happen when going out
 */


public class RecapEvent {
    private String type;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    private String address;
    private String time;

    RecapEvent(){}

    RecapEvent(String ty, String pn, double la, double lo, String addr, String ti){
        type = ty;
        phoneNumber = pn;
        latitude = la;
        longitude = lo;
        address = addr;
        time = ti;
    }

    public String getType(){
        return type;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public double getLatitude(){
        return latitude;

    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress(){
        return address;
    }

    public String getTime(){
        return time;
    }

}
