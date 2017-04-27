
package project.mobile.cs.fsu.edu.recap;

import android.app.ListActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecapView extends ListActivity {
    ListView listView;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> displayEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap_view);

        listView = getListView();

        for(int i = 0; i < MainActivity.lastNight.size(); i++){
            Log.i("event", MainActivity.lastNight.get(i).getType());

            if(MainActivity.lastNight.get(i).getType().equals(PhoneCallReceiver.LOCATION_UPDATE)){
                displayEvents.add(i, MainActivity.lastNight.get(i).getType() + ": " + MainActivity.lastNight.get(i).getAddress());
            }
            else{
                displayEvents.add(i, MainActivity.lastNight.get(i).getType() + ": " + MainActivity.lastNight.get(i).getPhoneNumber());

            }
        }

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, displayEvents);
        setListAdapter(listAdapter);

        listView.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String event = (String)parent.getItemAtPosition(position);
                if(event.contains(PhoneCallReceiver.LOCATION_UPDATE)){
                    Toast.makeText(RecapView.this, MainActivity.lastNight.get(position).getLatitude() + " " + MainActivity.lastNight.get(position).getLongitude(), Toast.LENGTH_LONG).show();
                }
            }
        }));

        for(int i = 0; i < displayEvents.size(); i++){
            Log.i("Display Event " + i, displayEvents.get(i));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




}
