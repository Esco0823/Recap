package project.mobile.cs.fsu.edu.recap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingSmsReceiver extends BroadcastReceiver {
    //types of events
    public static final String INCOMING_CALL = "Incoming Call";
    public static final String INCOMING_SMS = "Incoming SMS";
    public static final String OUTGOING_CALL = "Outgoing Call";
    public static final String OUTGOING_SMS = "Outgoing SMS";
    public static final String LOCATION_UPDATE = "Location Update";

    RecapEvent newEvent;
    String time = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        time = "" + MainActivity.TimeFormat();
        Bundle bundle = intent.getExtras();
        //get the sms received, retrieve the phone number, and add the event
        if(bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[0]);
            newEvent = new RecapEvent(INCOMING_SMS, sms.getDisplayOriginatingAddress(), -1.0, -1.0, "", time);
            MainActivity.lastNight.add(newEvent);
        }
    }
}
