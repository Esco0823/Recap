
package project.mobile.cs.fsu.edu.recap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecapView extends ListActivity {
    ListView listView;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> displayEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap_view);

        listView = getListView();
        //generates displayEvents with info on the event, depending on the type
        for(int i = 0; i < MainActivity.lastNight.size(); i++){
            if(MainActivity.lastNight.get(i).getType().equals(PhoneCallReceiver.LOCATION_UPDATE)){
                displayEvents.add(i,MainActivity.lastNight.get(i).getTime() + ":\nType - " +
                        MainActivity.lastNight.get(i).getType() + "\nAddr - " +
                        MainActivity.lastNight.get(i).getAddress() + "\n");
            }
            else{
                displayEvents.add(i,MainActivity.lastNight.get(i).getTime() + ":\nType - " +
                        MainActivity.lastNight.get(i).getType() + "\nPhone No - " +
                        PhoneNumberFormat(MainActivity.lastNight.get(i).getPhoneNumber()) + "\n");
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
                    Toast.makeText(RecapView.this, "Pretend you can see the location\non the map", Toast.LENGTH_LONG).show();
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

        String correct = "(" + pn.charAt(0) + pn.charAt(1) + pn.charAt(2) + ") " +
                pn.charAt(3) + pn.charAt(4) + pn.charAt(5) + "-" +
                pn.charAt(6) + pn.charAt(7) + pn.charAt(8) + pn.charAt(9);
        return correct;

    }


}
