package project.mobile.cs.fsu.edu.recap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class OutgoingSmsReceiver extends BroadcastReceiver {
    //types of events
    public static final String INCOMING_CALL = "IC";
    public static final String INCOMING_SMS = "IS";
    public static final String OUTGOING_CALL = "OC";
    public static final String OUTGOING_SMS = "OS";
    public static final String LOCATION_UPDATE = "LU";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        Log.i("event heard", "event heard");
        //Calendar rightNow = Calendar.getInstance();
        double time =  (double) System.currentTimeMillis(); //rightNow.get(Calendar.HOUR_OF_DAY) + (rightNow.get(Calendar.MINUTE) / 100.0);
        RecapEvent newEvent;
        String type = "";
        String phoneNumber = "";

        Bundle bundle = intent.getExtras();

        //if a phone call is being received...
        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
            Log.i("call received", "call received");
            type = INCOMING_CALL;
            phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        }
        //if a phone call was made...
        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            Log.i("call made", "call made");
            type = OUTGOING_CALL;
            phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        }
        //if a text was sent...
        else if(intent.getAction().equals("android.provider.Telephony.SMS_SENT")){
            Log.i("text sent", "text sent");
            type = OUTGOING_SMS;
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[0]);
            phoneNumber = sms.getOriginatingAddress();
        }
        //if a text was received...
        else if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){

            Log.i("text received", "text sent");
            type = INCOMING_SMS;
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[0]);
            phoneNumber = sms.getOriginatingAddress();
        }

        newEvent = new RecapEvent(type, phoneNumber, null, time);
        Log.i("Event Type: ", type);
        Log.i("Event PhNo: ", phoneNumber);
        Log.i("Event Time: ", Double.toString(time));

        Log.i("adding event...", "adding event...");
        MainActivity.lastNight.add(newEvent);

        for(int i = 0; i < MainActivity.lastNight.size(); i++){
            Log.i("Event " + i + " :", MainActivity.lastNight.get(i).getType());
        }
        */
    }

}
