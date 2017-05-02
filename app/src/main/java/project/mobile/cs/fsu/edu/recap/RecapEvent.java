package project.mobile.cs.fsu.edu.recap;

import android.location.Location;

/**
 * Created by Brandon on 4/25/17.
 * Object which contains information about different events that may happen when going out
 */


public class RecapEvent {
    //type of event
    private String type;
    //phone number (if sms or call)
    private String phoneNumber;
    //latitude (if location update)
    private double latitude;
    //longitude (if location update)
    private double longitude;
    //address (if location update)
    private String address;
    //time of event
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

    //getters for member data
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
