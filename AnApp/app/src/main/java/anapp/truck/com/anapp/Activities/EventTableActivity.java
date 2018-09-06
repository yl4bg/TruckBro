package anapp.truck.com.anapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.adapters.EventCellAdapter;
import anapp.truck.com.anapp.rest.ReceivedEventList;
import anapp.truck.com.anapp.utility.EventItemOnClickListener;
import anapp.truck.com.anapp.utility.location.GPSTracker;
import anapp.truck.com.anapp.utility.valueObjects.Event;

/**
 * Created by ZYL on 1/24/2015.
 */
public class EventTableActivity extends DefaultActivity {

    private ProgressDialog progDialog;
    private ListView listView;
    private TextView travelCnt;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.eventpage_layout);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                refresh();
            }
        });

        this.listView = (ListView) this.findViewById(R.id.event_list);
        listView.setOnItemClickListener(new EventItemOnClickListener(listView, this));

        ImageView reportButton = (ImageView) this.findViewById(R.id.newEventButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toReportPage = new Intent(EventTableActivity.this, ReportUploadActivity.class);
                startActivity(toReportPage);
            }
        });

        LinearLayout travelTogeter = (LinearLayout) findViewById(R.id.travel_together_layout);
        travelCnt = (TextView) findViewById(R.id.travel_together_count);
        TextView safe = (TextView) findViewById(R.id.safety_text);
        if(new Random().nextInt() > 0.5){
            safe.setVisibility(View.GONE);
            travelTogeter.setVisibility(View.VISIBLE);
        }

        progDialog = new ProgressDialog(this);

        // refresh on create
        refresh();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        progDialog.dismiss();
        progDialog.onDetachedFromWindow();
        progDialog = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
    }

    private void reload(final List<Event> events, final String travelTogetherCnt) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                return rhs.reportTime.compareTo(lhs.reportTime);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventCellAdapter adapter = new EventCellAdapter(EventTableActivity.this,
                        events.toArray(new Event[events.size()]), false);
                EventTableActivity.this.listView.setAdapter(adapter);
                EventTableActivity.this.listView.invalidateViews();
                travelCnt.setText(travelTogetherCnt);
                dismissDialog();
            }
        });
    }

    private void refresh() {
        showDialog();
        GPSTracker.updateContextAndLocation(this);
        new RefreshTask().execute(GPSTracker.getInstance().getLatitude(), GPSTracker.getInstance().getLatitude());
    }

    /**
     * Refresh accident display in background
     */
    private class RefreshTask extends AsyncTask<Double, Void, ReceivedEventList> {

        @Override
        /**
         * @param events an array of Event objects
         */
        protected ReceivedEventList doInBackground(Double... coords) {
            try {
                final String url = EventTableActivity.this.getApplicationContext().getString(R.string.server_prefix)
                        + "/getEventsRequest.json";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Map<String, String> urlArgs = GPSTracker.getInstance().toLocationInfoMap();
                return restTemplate.getForObject(
                        url + "?longitude={longitude}&latitude={latitude}",
                        ReceivedEventList.class, urlArgs);
            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReceivedEventList eventList) {
            if(eventList!=null) {
                List<Event> events = eventList.toUiEventList();
                reload(events, eventList.getTravelTogetherCnt());
            } else {
                Toast.makeText(getApplicationContext(),
                        EventTableActivity.this.getString(R.string.no_connection),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void showDialog() {
        if(progDialog != null) {
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setIndeterminate(false);
            progDialog.setCancelable(true);
            progDialog.setMessage("正在获取路况数据");
            progDialog.show();
        }
    }

    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

}
