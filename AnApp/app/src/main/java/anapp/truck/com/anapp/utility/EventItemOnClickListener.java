package anapp.truck.com.anapp.utility;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.activities.EventDetailActivity;
import anapp.truck.com.anapp.activities.EventTableActivity;
import anapp.truck.com.anapp.activities.SOSDetailActivity;
import anapp.truck.com.anapp.utility.location.GPSTracker;
import anapp.truck.com.anapp.utility.valueObjects.Event;

/**
 * Created by angli on 7/6/15.
 */
public class EventItemOnClickListener implements AdapterView.OnItemClickListener{

    private ListView listView;
    private Activity activity;

    public EventItemOnClickListener(ListView listView, Activity activity){
        this.listView = listView;
        this.activity = activity;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        GPSTracker.updateContextAndLocation(activity.getApplicationContext());
        Event cellEvent = (Event) listView.getAdapter().getItem(position);
        Intent detailView = new Intent(activity, EventDetailActivity.class);
        if(cellEvent.type.equals(activity.getString(R.string.event_type_help))){
            detailView = new Intent(activity, SOSDetailActivity.class);
        }
        detailView.putExtra("eventID", cellEvent.eventID);
        detailView.putExtra("type", cellEvent.type);
        detailView.putExtra("distance", String.format("%.1f", cellEvent.distance) + activity.getString(R.string.kilometer));
        detailView.putExtra("direction", cellEvent.direction);
        detailView.putExtra("description", cellEvent.description);
        detailView.putExtra("uuid", cellEvent.uuid);
        detailView.putExtra("time", cellEvent.time);
        detailView.putExtra("eventLongitude", cellEvent.longitude);
        detailView.putExtra("eventLatitude", cellEvent.latitude);
        detailView.putExtra("roadNumText", cellEvent.roadNumText);
        detailView.putExtra("addressText", cellEvent.addressText);
        detailView.putExtra("srcLongitude", GPSTracker.getInstance().getLongitude());
        detailView.putExtra("srcLatitude", GPSTracker.getInstance().getLatitude());
        detailView.putExtra("picIDs", cellEvent.getPicIDStr());
        detailView.putExtra("official", cellEvent.official);
        detailView.putExtra("upCnt", cellEvent.upCnt+"");
        detailView.putExtra("reportCnt", cellEvent.reportCnt+"");
        detailView.putExtra("senderName", cellEvent.senderName);
        activity.startActivity(detailView);
        return;
    }
}
