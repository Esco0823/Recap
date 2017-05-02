package project.mobile.cs.fsu.edu.recap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static ArrayList<RecapEvent> lastNight = new ArrayList<>();
    public PhoneCallReceiver phoneCalls;
    public IncomingSmsReceiver incomingSMS;
    public IntentFilter callFilter;
    public IntentFilter incSmsFilter;

    ContentResolver contentResolver;
    SmsOutObserver smsOutObserver;

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

        //register content observer
        contentResolver = getApplicationContext().getContentResolver();
        smsOutObserver = new SmsOutObserver(new Handler());
        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, smsOutObserver);
    }

    public void RecapClicked(View view) {
        //unregister receivers
        unregisterReceiver(phoneCalls);
        unregisterReceiver(incomingSMS);

        //unregister observer
        contentResolver.unregisterContentObserver(smsOutObserver);

        //stop google api client
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
            return hours + ":0" + minutes + " " + am_pm;
        else
            return hours + ":" + minutes + " " + am_pm;

    }

    //outgoing SMS content observer (broken, incoming sms also trigger it)
    class SmsOutObserver extends ContentObserver{
        public SmsOutObserver(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange){
            super.onChange(selfChange);
            Uri uriSms = Uri.parse("content://sms");
            Cursor cur = getApplicationContext().getContentResolver().query(uriSms, null, null, null, null);
            // last SMS sent
            cur.moveToNext();
            String type = cur.getString(cur.getColumnIndex("type"));
            //if outgoing
            if(type.equals("2")) {
                String content = cur.getString(cur.getColumnIndex("address"));
                time = "" + MainActivity.TimeFormat();
                RecapEvent newEvent = new RecapEvent(IncomingSmsReceiver.OUTGOING_SMS, content, -1.0, -1.0, "", time);
                if(!MainActivity.lastNight.isEmpty()) {
                    //ignores duplicate objects being made from the same call (weird error where 2 or 3 newEvent objects are inserted and not just 1)
                    if (MainActivity.lastNight.get(MainActivity.lastNight.size() - 1).getPhoneNumber().equals(newEvent.getPhoneNumber()) &&
                            MainActivity.lastNight.get(MainActivity.lastNight.size() - 1).getType().equals(newEvent.getType())) {
                    }
                    else{
                        if(!newEvent.getPhoneNumber().equals("")) {
                            MainActivity.lastNight.add(newEvent);
                        }
                    }
                }
                else{
                    if(!newEvent.getPhoneNumber().equals("")) {
                        MainActivity.lastNight.add(newEvent);
                    }
                }
            }
        }
    }

}
