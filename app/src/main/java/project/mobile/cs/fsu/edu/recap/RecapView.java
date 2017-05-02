
package project.mobile.cs.fsu.edu.recap;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class RecapView extends ListActivity {
    ListView listView;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> displayEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap_view);

        listView = getListView();
        //sort events by time
        Collections.sort(MainActivity.lastNight, new Comparator<RecapEvent>() {
                    @Override
                    public int compare(RecapEvent o1, RecapEvent o2) {
                        if(TimeStringToInt(o1.getTime()) >= TimeStringToInt(o2.getTime())){
                            return -1;
                        }
                        else{
                            return 1;
                        }
                    }
                });
        //generates displayEvents with info on the event, depending on the type
        for(int i = 0; i < MainActivity.lastNight.size(); i++){
            if(MainActivity.lastNight.get(i).getType().equals(PhoneCallReceiver.LOCATION_UPDATE)){
                displayEvents.add(i,MainActivity.lastNight.get(i).getTime() + ":\nType - " +
                        MainActivity.lastNight.get(i).getType() + "\nAddr - " +
                        MainActivity.lastNight.get(i).getAddress() + "\n");
            }
            else{
                displayEvents.add(i,MainActivity.lastNight.get(i).getTime() + ":\nType - " +
                        MainActivity.lastNight.get(i).getType() + "\nContact Name - " +
                        getContactName(getApplicationContext(), MainActivity.lastNight.get(i).getPhoneNumber()) + "\n");
                        //PhoneNumberFormat(MainActivity.lastNight.get(i).getPhoneNumber()) + "\n");

            }
        }

        listAdapter = new ArrayAdapter<String>(this, R.layout.custom_textview, displayEvents);
        setListAdapter(listAdapter);

        //go to MapView so user can see the location selected on the map
        listView.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String event = (String)parent.getItemAtPosition(position);
                if(event.contains(PhoneCallReceiver.LOCATION_UPDATE)){
                    //open Google Maps and display location
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f", MainActivity.lastNight.get(position).getLatitude(), MainActivity.lastNight.get(position).getLongitude(), MainActivity.lastNight.get(position).getLatitude(), MainActivity.lastNight.get(position).getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            }
        }));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //converts 10 digits into phone number format (XXX) XXX-XXXX
    public String PhoneNumberFormat(String pn){
        if(pn.isEmpty()){
            return "No Number";
        }
        String correct = "(" + pn.charAt(0) + pn.charAt(1) + pn.charAt(2) + ") " +
                pn.charAt(3) + pn.charAt(4) + pn.charAt(5) + "-" +
                pn.charAt(6) + pn.charAt(7) + pn.charAt(8) + pn.charAt(9);
        return correct;

    }

    //find contact name using phone number
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        if(phoneNumber.isEmpty()){
            return "No Contact Available";
        }
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public int TimeStringToInt(String time){
        int newTime, hr, min;
        Log.i("time", time);
        hr = Integer.parseInt(time.substring(0, time.indexOf(":")));
        Log.i("hr", "" + hr);
        min = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.indexOf(" ")));
        Log.i("min", "" + min);
        if(time.substring(time.indexOf(" ")).equals("PM")){
            hr = hr + 12;
        }
        newTime = (hr * 100) + min;
        return newTime;
    }
}
