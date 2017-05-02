package project.mobile.cs.fsu.edu.recap;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class PhoneCallReceiver extends BroadcastReceiver {
    //types of events
    public static final String INCOMING_CALL = "Incoming Call";
    public static final String OUTGOING_CALL = "Outgoing Call";
    public static final String LOCATION_UPDATE = "Location Update";
    String time = "";

    RecapEvent newEvent;

    @Override
    public void onReceive(Context context, Intent intent) {
        time = "" + MainActivity.TimeFormat();
        //make the receiver listen for changes in call state (ringing, idle, etc)
        try{
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneStateListener phoneListener = new MyPhoneStateListener();

            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e){
        }

    }

    //class to handle changes in phone state
    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            //there is no phone activity
            if(state == TelephonyManager.CALL_STATE_IDLE){

            }
            //checking for outgoing call
            else if(state == TelephonyManager.CALL_STATE_OFFHOOK){
                newEvent = new RecapEvent(OUTGOING_CALL, incomingNumber, -1.0, -1.0, "", time);

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
            //checking for incoming call
            else if(state == TelephonyManager.CALL_STATE_RINGING){
                newEvent = new RecapEvent(INCOMING_CALL, incomingNumber, -1.0, -1.0, "", time);

                if(!MainActivity.lastNight.isEmpty()) {
                    //ignores duplicate objects being made from the same call (weird error where 2 or 3 newEvent objects are inserted and not just 1)
                    if (MainActivity.lastNight.get(MainActivity.lastNight.size() - 1).getPhoneNumber().equals(newEvent.getPhoneNumber()) &&
                            MainActivity.lastNight.get(MainActivity.lastNight.size() - 1).getType().equals(newEvent.getType())) {
                    }
                    else{
                        MainActivity.lastNight.add(newEvent);
                    }
                }
                else{
                    MainActivity.lastNight.add(newEvent);
                }
            }
        }
    }
}
