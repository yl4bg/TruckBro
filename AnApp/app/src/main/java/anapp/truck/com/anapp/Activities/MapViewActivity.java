package anapp.truck.com.anapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ReceivedEventList;
import anapp.truck.com.anapp.utility.location.GPSTracker;

/**
 * Created by LaIMaginE on 3/10/2015.
 */
public class MapViewActivity extends DefaultActivity {

    private MapView mapView;
    private AMap aMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mapview_layout);


        ImageView reportButton = (ImageView) findViewById(R.id.newEventButton);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toReportPage = new Intent(MapViewActivity.this, ReportUploadActivity.class);
                startActivity(toReportPage);
            }
        });

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initMap();
                new RefreshTask().execute();
            }
        });
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
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
            final String url = MapViewActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/getEventsRequest.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = GPSTracker.getInstance().toLocationInfoMap();
            try {
                return restTemplate.getForObject(
                        url + "?longitude={longitude}&latitude={latitude}",
                        ReceivedEventList.class, urlArgs);
            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReceivedEventList eventList) {
            if(eventList!=null) {
                List<LatLng> allLatLngs = eventList.getAllLatLngs();
                drawMarkersAndZoom(allLatLngs);
            } else {
                Toast.makeText(getApplicationContext(),
                        MapViewActivity.this.getString(R.string.no_connection),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void drawMarkersAndZoom(List<LatLng> allLatLngs){
        aMap.addMarker(new MarkerOptions().anchor(0.5f, 1.0f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_self_pin))
//                        .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(getString(R.string.current_position))
                .position(GPSTracker.getInstance().getLatLng()));
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        bounds = bounds.include(GPSTracker.getInstance().getLatLng());
        for(LatLng pt : allLatLngs){
            aMap.addMarker(new MarkerOptions().anchor(0.5f, 1.0f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_event_pin))
//                    .icon(BitmapDescriptorFactory
//                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(getString(R.string.event_position))
                    .position(pt));
            bounds = bounds.include(pt);
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.getWidth(), mapView.getHeight(), 15));
    }
}
