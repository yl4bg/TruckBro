package anapp.truck.com.anapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.adapters.FreqPlaceAdapter;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.AddressConst;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.ToastUtil;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created by LaIMaginE on 4/8/2015.
 */
public class FrequentPlaceActivity extends DefaultActivity {

    private boolean showToDriver;
    private boolean showToHer;
    private boolean showToOwner;

    private boolean wheelScrolled = false;
    private ArrayList<String> freqPlaces;
    private ListView placesList;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.frequentplace_layout);

        Bundle b = getIntent().getExtras();

        String[] places = b.getString("freqPlaces").split(",");
        freqPlaces = new ArrayList<>();
        for(String place : places)
            freqPlaces.add(place);


        placesList = (ListView) this.findViewById(R.id.freqPlaces_list);

        updateHintText();


        FreqPlaceAdapter adapter = new FreqPlaceAdapter(FrequentPlaceActivity.this, freqPlaces);
        placesList.setAdapter(adapter);
        setListViewHeightBasedOnItems();
        placesList.invalidateViews();

        initWheel(R.id.province_field, AddressConst.provinces);
        initWheel(R.id.city_field, AddressConst.cities[0]);

        RelativeLayout submit = (RelativeLayout) findViewById(R.id.cancel_add);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddFrequentPlaceTask().execute(CookieManager.getInstance().getCookie(),
                        freqPlaces.toString().substring(1, freqPlaces.toString().length() - 1).replaceAll(" ", ""));
                new UpdateShowInChatTask().execute(CookieManager.getInstance().getCookie(),
                        Boolean.toString(showToDriver),
                        Boolean.toString(showToOwner),
                        Boolean.toString(showToHer));

            }
        });

        RelativeLayout add = (RelativeLayout) findViewById(R.id.confirm_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String place = formatAddressFromWheels();
                if(place.length()==0){
                    ToastUtil.show(FrequentPlaceActivity.this, "请选择常跑地后添加");
                } else if (freqPlaces.size()==5){
                    ToastUtil.show(FrequentPlaceActivity.this, "已达到5个常跑地（最多）");
                }
                freqPlaces.add(place);
                placesList.invalidateViews();
                setListViewHeightBasedOnItems();
                updateHintText();

            }
        });

        final RelativeLayout driver = (RelativeLayout) findViewById(R.id.driver_checkbox_layout);
        final RelativeLayout owner = (RelativeLayout) findViewById(R.id.owner_checkbox_layout);
        final RelativeLayout her = (RelativeLayout) findViewById(R.id.her_checkbox_layout);

        final ImageView driverImg = (ImageView) findViewById(R.id.driver_checkbox);
        final ImageView ownerImg = (ImageView) findViewById(R.id.owner_checkbox);
        final ImageView herImg = (ImageView) findViewById(R.id.her_checkbox);

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showToDriver){
                    driverImg.setImageResource(R.drawable.new_unselected_box);
                } else {
                    driverImg.setImageResource(R.drawable.new_selected_box);
                }
                showToDriver = !showToDriver;
            }
        });

        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showToOwner){
                    ownerImg.setImageResource(R.drawable.new_unselected_box);
                } else {
                    ownerImg.setImageResource(R.drawable.new_selected_box);
                }
                showToOwner = !showToOwner;
            }
        });

        her.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showToHer){
                    herImg.setImageResource(R.drawable.new_unselected_box);
                } else {
                    herImg.setImageResource(R.drawable.new_selected_box);
                }
                showToHer = !showToHer;
            }
        });
    }

    public void updateHintText(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView hint = (TextView) findViewById(R.id.freq_place_number_hint);
                hint.setText("(您还能添加" + (5 - freqPlaces.size()) + "个常跑地)");
            }
        });
    }

    private String formatAddressFromWheels(){
        StringBuilder sb = new StringBuilder();
        sb.append(getProvince());
        sb.append(getCity());
        return sb.toString().replaceAll("不限", "");
    }

    private String getProvince(){
        return AddressConst.provinces[getWheel(R.id.province_field).getCurrentItem()];
    }

    private String getCity(){
        if(getWheel(R.id.province_field).getCurrentItem()>0)
            return AddressConst.cities[getWheel(R.id.province_field).getCurrentItem()-1][getWheel(R.id.city_field).getCurrentItem()];
        else
            return "";
    }

    /**
     * Updates entered PIN status
     */
    private void updateStatus(int wheelId) {
        if(wheelId == R.id.province_field){
            if(getWheel(R.id.province_field).getCurrentItem() == 0)
                return;
            changeMenu(R.id.city_field, AddressConst.cities[getWheel(R.id.province_field).getCurrentItem()-1]);
        }
    }

    /**
     * Initializes wheel
     *
     * @param id
     *          the wheel widget Id
     */
    private void initWheel(int id, String[] wheelMenu1) {
        WheelView wheel = (WheelView) findViewById(id);
        wheel.TEXT_COLOR = "#ffffffff";
        wheel.setViewAdapter(new ArrayWheelAdapter<String>(this, wheelMenu1));
        wheel.setVisibleItems(3);
        wheel.setCurrentItem(0);
        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
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

    private class AddFrequentPlaceTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        /**
         * locInfo: roadNum, province, city, district
         */
        protected ErrorMsg doInBackground(String... info) {
            final String url = FrequentPlaceActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/updateFrequentPlaces.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            urlArgs.put("frequentPlaceList", info[1]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}&frequentPlaceList={frequentPlaceList}",
                        ErrorMsg.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg msg) {
            if (msg == null) {
                Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), msg.getText(), Toast.LENGTH_SHORT).show();
            FrequentPlaceActivity.this.finish();
        }

    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @return true if the listView is successfully resized, false otherwise
     */
    public  boolean setListViewHeightBasedOnItems() {

        ListAdapter listAdapter = placesList.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, placesList);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = placesList.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = placesList.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            placesList.setLayoutParams(params);
            placesList.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    private class UpdateShowInChatTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... info) {
            final String url = FrequentPlaceActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/updateShowFPInChat.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            urlArgs.put("showToDriver", info[1]);
            urlArgs.put("showToOwner", info[2]);
            urlArgs.put("showToHer", info[3]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}&showToDriver={showToDriver}" +
                                "&showToOwner={showToOwner}&showToHer={showToHer}",
                        ErrorMsg.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg msg) {
            if (msg == null) {
                Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), msg.getText(), Toast.LENGTH_SHORT).show();
        }

    }

}
