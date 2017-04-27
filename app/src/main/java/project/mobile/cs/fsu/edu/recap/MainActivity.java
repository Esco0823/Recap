package project.mobile.cs.fsu.edu.recap;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static ArrayList<RecapEvent> lastNight = new ArrayList<>();
    public PhoneCallReceiver phoneCalls;
    public IncomingSmsReceiver incomingSMS;
    public OutgoingSmsReceiver outgoingSMS;
    public IntentFilter callFilter;
    public IntentFilter incSmsFilter;
    public IntentFilter outSmsFilter;

    LocationListener locationListener;
    LocationManager locationManager;
    List<Address> currentAddress;
    String address = "";
    String time = "";

    Button Recap;
    Button Start;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //saves address, longitude, latitude, and time of location change as an event
                Geocoder gecoder = new Geocoder(getApplicationContext());
                try {
                    time = "" + TimeFormat();
                    currentAddress = gecoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if(currentAddress.isEmpty())
                        address = "No Nearby Locations";
                    else
                        address = currentAddress.get(0).getAddressLine(0);

                    RecapEvent newEvent = new RecapEvent(PhoneCallReceiver.LOCATION_UPDATE, "", location.getLatitude(), location.getLongitude(), address, time);
                    lastNight.add(newEvent);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Start = (Button) findViewById(R.id.start);
        Recap = (Button) findViewById(R.id.recap);

        //build google api client
        if(googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //register broadcast receiver

    }

    public void StartClicked(View view) {
        //starts keeping tracking of current location, BroadcastReceiver for messages/phone calls
        Recap.setClickable(true);
        Recap.setVisibility(Recap.VISIBLE);
        Start.setClickable(false);
        Start.setVisibility(Recap.INVISIBLE);
        lastNight.clear();
        //start google api client
        googleApiClient.connect();

        //register receivers
        phoneCalls = new PhoneCallReceiver();
        callFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        registerReceiver(phoneCalls, callFilter);

        incomingSMS = new IncomingSmsReceiver();
        incSmsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(incomingSMS, incSmsFilter);

        outgoingSMS = new OutgoingSmsReceiver();
        outSmsFilter = new IntentFilter("android.provider.Telephony.SMS_SENT");
        registerReceiver(outgoingSMS, outSmsFilter);
    }

    public void RecapClicked(View view) {
        //unregister receivers
        unregisterReceiver(phoneCalls);
        unregisterReceiver(incomingSMS);
        unregisterReceiver(outgoingSMS);

        StopLocationUpdates();
        googleApiClient.disconnect();
        Start.setClickable(true);
        Start.setVisibility(Start.VISIBLE);

        //opens recap activity to show an overview of the night
        Intent intent = new Intent(this, RecapView.class);
        startActivity(intent);
    }

    public void onConnected(Bundle bundle) throws SecurityException {
        StartLocationUpdates();
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //starts listening for location updates
    protected void StartLocationUpdates() throws SecurityException{
        LocationRequest LocationRequest = new LocationRequest();
        LocationRequest.setInterval(10000);
        LocationRequest.setFastestInterval(5000);
        LocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,LocationRequest,locationListener);
    }

    //stops listening for location updates
    protected void StopLocationUpdates() throws SecurityException{
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,locationListener);
    }

    //gets the current time as a string
    public static String TimeFormat(){
        Calendar c = Calendar.getInstance();
        int minutes = c.get(Calendar.MINUTE);
        int hours = c.get(Calendar.HOUR);
        int ampm = c.get(Calendar.AM_PM);
        String am_pm = "";
        if(ampm == 0){ am_pm = "AM";}
        else if(ampm == 1){ am_pm = "PM";}

        if(minutes < 10)
            return hours + ":0" + minutes + "" + am_pm;
        else
            return hours + ":" + minutes + " " + am_pm;

    }
}
