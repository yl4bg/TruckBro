package anapp.truck.com.anapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.adapters.EventCellAdapter;
import anapp.truck.com.anapp.rest.ReceivedEventList;
import anapp.truck.com.anapp.utility.AddressConst;
import anapp.truck.com.anapp.utility.EventItemOnClickListener;
import anapp.truck.com.anapp.utility.location.GPSTracker;
import anapp.truck.com.anapp.utility.valueObjects.Event;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created by ZYL on 3/8/2015.
 */
public class SearchActivity extends DefaultActivity {

    private ListView listView;
    private boolean wheelScrolled = false;
    private int wheelsHeight;
    private boolean searched = false;

    // Wheel scrolled listener
    private OnWheelScrollListener scrolledListener = new OnWheelScrollListener()  {
        @Override
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }
        @Override
        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            updateStatus(wheel.getId());
        }
    };

    // Wheel changed listener
    private final OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
//            Log.d(TAG, "onChanged, wheelScrolled = " + wheelScrolled);
            if (!wheelScrolled) {
                updateStatus(wheel.getId());
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.searchevent_layout);

        initWheel(R.id.province_field, AddressConst.provinces, 5);
        initWheel(R.id.city_field, AddressConst.cities[0], 5);
        initWheel(R.id.district_field, AddressConst.districts[0][0], 5);
        initWheel(R.id.roadTypeChoice, AddressConst.roadTypes, 3);
        initWheel(R.id.roadNumChoice, AddressConst.highwayByProvince[0], 3);

        final TextView search = (TextView) this.findViewById(R.id.search_events_button);
        final TextView titleBarText = (TextView) this.findViewById(R.id.titleBarText);
        final LinearLayout allWheels = (LinearLayout) this.findViewById(R.id.allWheelsLayout);
        final ImageView restart = (ImageView) this.findViewById(R.id.restart_search);

        this.listView = (ListView) this.findViewById(R.id.event_list);
        listView.setOnItemClickListener(new EventItemOnClickListener(listView, this));

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restart.setVisibility(View.VISIBLE);
                GPSTracker.updateContextAndLocation(SearchActivity.this);
                final String p = AddressConst.provinces[getWheel(R.id.province_field).getCurrentItem()].replaceAll("不限", "");
                final String c = AddressConst.cities[getWheel(R.id.province_field).getCurrentItem() - (p.equals("") ? 0 : 1)][getWheel(R.id.city_field).getCurrentItem()].replaceAll("不限", "");
                final String t = AddressConst.districts[getWheel(R.id.province_field).getCurrentItem() - (p.equals("") ? 0 : 1)][getWheel(R.id.city_field).getCurrentItem() - (c.equals("") ? 0 : 1)][getWheel(R.id.district_field).getCurrentItem()].replaceAll("不限", "");
                final String r = getRoadNum().replaceAll("不限", "");
//                Log.i("search", p + c + t + r);
                if (p.equals("") && c.equals("") && t.equals("") && r.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.search_all_blank, Toast.LENGTH_SHORT).show();
                    return;
                }

                search(p, c, t, r);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wheelsHeight = allWheels.getHeight();
                        allWheels.setVisibility(View.VISIBLE);
                        allWheels.setAlpha(1.0f);
                        allWheels.animate().setDuration(1500)
                                .alpha(0.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        allWheels.getLayoutParams().height = 0;
                                        allWheels.requestLayout();
                                    }
                                });
                        restart.setVisibility(View.VISIBLE);
                        titleBarText.setText(p + c + t + r);
                    }
                });
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        restart.setVisibility(View.INVISIBLE);
                        titleBarText.setText("路况搜索");
                        allWheels.getLayoutParams().height += wheelsHeight;
                        allWheels.setAlpha(1.0f);
//                            allWheels.setAlpha(0.0f);
//                            allWheels.animate().setDuration(1500)
//                                    .alpha(1.0f);
//                            allWheels.requestLayout();
                        listView.getLayoutParams().height -= wheelsHeight;
                        listView.requestLayout();
                    }
                });
            }
        });
    }

    private void search(String province, String city, String town, String road) {
        Toast.makeText(getApplicationContext(), getString(R.string.searching), Toast.LENGTH_SHORT).show();
        new SearchTask().execute(province, city, town, road);
    }

    private void reload(final List<Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                return rhs.reportTime.compareTo(lhs.reportTime);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventCellAdapter adapter = new EventCellAdapter(SearchActivity.this,
                        events.toArray(new Event[events.size()]), true);
                listView.setAdapter(adapter);
                listView.getLayoutParams().height += wheelsHeight;
                listView.requestLayout();
                listView.invalidateViews();

                if(events.size()>0){
                    Toast.makeText(getApplicationContext(), "搜索完成", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "没有相关路况", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * get events with search criteria
     */
    private class SearchTask extends AsyncTask<String, Void, ReceivedEventList> {

        @Override
        /**
         * locInfo: roadNum, province, city, district
         */
        protected ReceivedEventList doInBackground(String... locInfo) {
            final String url = SearchActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/searchEventsRequest.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("longitude", GPSTracker.getInstance().getLongitude() + "");
            urlArgs.put("latitude", GPSTracker.getInstance().getLatitude() + "");
            urlArgs.put("province", locInfo[0]);
            urlArgs.put("city", locInfo[1]);
            urlArgs.put("town", locInfo[2]);
            urlArgs.put("road", locInfo[3]);
            try {
                return restTemplate.getForObject(
                        url + "?longitude={longitude}&latitude={latitude}" +
                                "&province={province}&city={city}&district={town}&roadNum={road}&",
                        ReceivedEventList.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReceivedEventList eventList) {
            if (eventList == null) {
                Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                reload(new ArrayList<Event>());
                return;
            }
            List<Event> events = eventList.toUiEventList();
            reload(events);
        }

    }

    /**
     * Updates entered PIN status
     */
    private void updateStatus(int wheelId) {
        if(wheelId == R.id.province_field){
            if(getWheel(R.id.roadTypeChoice).getCurrentItem() == 0){
                int index = AddressConst.findIndexForHighwayProvince(AddressConst.provinces[getWheel(R.id.province_field).getCurrentItem()]);
                if(index>=0)
                    changeMenu(R.id.roadNumChoice, AddressConst.highwayByProvince[index]);
            } else {
                int index = AddressConst.findIndexForRoadProvince(AddressConst.provinces[getWheel(R.id.province_field).getCurrentItem()]);
                if(index>=0)
                    changeMenu(R.id.roadNumChoice, AddressConst.roadsByProvince[index]);
            }

            if(getWheel(R.id.province_field).getCurrentItem() == 0)
                return;
            changeMenu(R.id.city_field, AddressConst.cities[getWheel(R.id.province_field).getCurrentItem()-1]);
            changeMenu(R.id.district_field, AddressConst.districts[getWheel(R.id.province_field).getCurrentItem()-1][0]);

        } else if (wheelId == R.id.city_field){
            if(getWheel(R.id.province_field).getCurrentItem() == 0 || getWheel(R.id.city_field).getCurrentItem() == 0)
                return;
            changeMenu(R.id.district_field, AddressConst.districts[getWheel(R.id.province_field).getCurrentItem()-1][getWheel(R.id.city_field).getCurrentItem()-1]);
        } else if(wheelId == R.id.roadTypeChoice){
            if(getWheel(R.id.roadTypeChoice).getCurrentItem() == 0){
                int index = AddressConst.findIndexForHighwayProvince(AddressConst.provinces[getWheel(R.id.province_field).getCurrentItem()]);
                if(index>=0)
                    changeMenu(R.id.roadNumChoice, AddressConst.highwayByProvince[index]);
            } else {
                int index = AddressConst.findIndexForRoadProvince(AddressConst.provinces[getWheel(R.id.province_field).getCurrentItem()]);
                if(index>=0)
                    changeMenu(R.id.roadNumChoice, AddressConst.roadsByProvince[index]);
            }
        }
    }

    /**
     * Initializes wheel
     *
     * @param id
     *          the wheel widget Id
     */
    private void initWheel(int id, String[] wheelMenu1, int itemCount) {
        WheelView wheel = (WheelView) findViewById(id);
        wheel.setViewAdapter(new ArrayWheelAdapter<String>(this, wheelMenu1));
        wheel.setVisibleItems(itemCount);
        wheel.setCurrentItem(0);
        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
        wheel.setDrawShadows(false);
    }

    private void changeMenu(int id, String[] wheelMenu1){
        WheelView wheel = (WheelView) findViewById(id);
        wheel.setViewAdapter(new ArrayWheelAdapter<String>(this, wheelMenu1));
        wheel.setCurrentItem(0);
    }


    /**
     * Returns wheel by Id
     *
     * @param id
     *          the wheel Id
     * @return the wheel with passed Id
     */
    private WheelView getWheel(int id) {
        return (WheelView) findViewById(id);
    }

    private String getRoadNum(){
        if(getWheel(R.id.roadTypeChoice).getCurrentItem() == 0){
            int index = AddressConst.findIndexForHighwayProvince(AddressConst.provinces[getWheel(R.id.province_field).getCurrentItem()]);
            if(index>=0)
                return AddressConst.highwayByProvince[index][getWheel(R.id.roadNumChoice).getCurrentItem()];
            else
                return "未知道路";
        } else {
            int index = AddressConst.findIndexForRoadProvince(AddressConst.provinces[getWheel(R.id.province_field).getCurrentItem()]);
            if(index>=0)
                return AddressConst.roadsByProvince[index][getWheel(R.id.roadNumChoice).getCurrentItem()];
            else
                return "未知道路";
        }
    }
}
