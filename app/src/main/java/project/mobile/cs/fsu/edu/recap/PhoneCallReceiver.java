package project.mobile.cs.fsu.edu.recap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
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
    public static final String INCOMING_SMS = "Incoming SMS";
    public static final String OUTGOING_CALL = "Outgoing Call";
    public static final String OUTGOING_SMS = "Outgoing SMS";
    public static final String LOCATION_UPDATE = "Location Update";
    String time = "";

    RecapEvent newEvent;

    @Override
    public void onReceive(Context context, Intent intent) {

        time = "" + System.currentTimeMillis();
        //DateFormat.getDateTimeInstance().format(new Date());
        //make
        try{
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneStateListener phoneListener = new MyPhoneStateListener();

            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e){
            Log.i("Phone Receive Error", "" + e);
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i("stop adding idle ", Integer.toString(state));
                    //if the events are the same (to stop duplicate adding
                    if(MainActivity.lastNight.size() > 2) {
                        if (MainActivity.lastNight.get(MainActivity.lastNight.size() - 1).getPhoneNumber().
                                equals(MainActivity.lastNight.get(MainActivity.lastNight.size() - 2).getPhoneNumber()) &&
                                MainActivity.lastNight.get(MainActivity.lastNight.size() - 1).getType().
                                        equals(MainActivity.lastNight.get(MainActivity.lastNight.size() - 2).getType())) {
                            MainActivity.lastNight.remove(MainActivity.lastNight.size()-1);
                        }
                    }


                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i("stop adding offhook ", Integer.toString(state));
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    newEvent = new RecapEvent(INCOMING_CALL, incomingNumber, -1.0, -1.0, "", time);
                    MainActivity.lastNight.add(newEvent);
                    //if the events are the same (to stop duplicate adding
                    if(MainActivity.lastNight.size() > 2) {
                        if (MainActivity.lastNight.get(MainActivity.lastNight.size() - 1).getPhoneNumber().
                                equals(MainActivity.lastNight.get(MainActivity.lastNight.size() - 2).getPhoneNumber()) &&
                                MainActivity.lastNight.get(MainActivity.lastNight.size() - 1).getType().
                                        equals(MainActivity.lastNight.get(MainActivity.lastNight.size() - 2).getType())) {
                            MainActivity.lastNight.remove(MainActivity.lastNight.size()-1);
                        }
                    }
                    break;
            }
            Log.i("state", Integer.toString(state));
        }
    }

}
