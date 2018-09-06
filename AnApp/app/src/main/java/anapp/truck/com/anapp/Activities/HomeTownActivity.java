package anapp.truck.com.anapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created by angli on 7/5/15.
 */
public class HomeTownActivity extends DefaultActivity {

    private boolean wheelScrolled = false;

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
        this.setContentView(R.layout.hometown_layout);

        initWheel(R.id.province_field, AddressConst.provinces);
        initWheel(R.id.city_field, AddressConst.cities[0]);
        initWheel(R.id.district_field, AddressConst.districts[0][0]);

        RelativeLayout cancel = (RelativeLayout) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeTownActivity.this.finish();
            }
        });

        RelativeLayout submit = (RelativeLayout) findViewById(R.id.confirm);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = formatAddressFromWheels();
                new ChangeInfoFieldTask().execute(CookieManager.getInstance().getCookie(), "homeTown", place);
                new DetailedHometownTask().execute(CookieManager.getInstance().getCookie(), getProvince(),
                        getCity(), getDistrict());
            }
        });
    }

    private String formatAddressFromWheels(){
        StringBuilder sb = new StringBuilder();
        sb.append(getProvince());
        sb.append(getCity());
        sb.append(getDistrict());
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

    private String getDistrict(){
        if(getWheel(R.id.province_field).getCurrentItem()>0 && getWheel(R.id.city_field).getCurrentItem()>0)
            return AddressConst.districts[getWheel(R.id.province_field).getCurrentItem()-1][getWheel(R.id.city_field).getCurrentItem()-1][getWheel(R.id.district_field).getCurrentItem()];
        else if(getWheel(R.id.province_field).getCurrentItem()>0 && isCityProvince(getWheel(R.id.province_field).getCurrentItem()))
            return AddressConst.districts[getWheel(R.id.province_field).getCurrentItem()-1][0][getWheel(R.id.district_field).getCurrentItem()];
        else
            return "";

    }

    private boolean isCityProvince(int provinceIndex){
        return AddressConst.provinces[provinceIndex].equals("北京市")
                || AddressConst.provinces[provinceIndex].equals("天津市")
                || AddressConst.provinces[provinceIndex].equals("上海市")
                || AddressConst.provinces[provinceIndex].equals("重庆市");
    }
    /**
     * Updates entered PIN status
     */
    private void updateStatus(int wheelId) {
        if(wheelId == R.id.province_field){
            if(getWheel(R.id.province_field).getCurrentItem() == 0)
                return;
            changeMenu(R.id.city_field, AddressConst.cities[getWheel(R.id.province_field).getCurrentItem()-1]);
            changeMenu(R.id.district_field, AddressConst.districts[getWheel(R.id.province_field).getCurrentItem()-1][0]);

        }
        else if (wheelId == R.id.city_field){
            if(getWheel(R.id.province_field).getCurrentItem() == 0 || getWheel(R.id.city_field).getCurrentItem() == 0)
                return;
            changeMenu(R.id.district_field, AddressConst.districts[getWheel(R.id.province_field).getCurrentItem()-1][getWheel(R.id.city_field).getCurrentItem()-1]);
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

    private class ChangeInfoFieldTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... info) {
            final String url = HomeTownActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/changeInfoField.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            urlArgs.put("fieldname", info[1]);
            urlArgs.put("value", info[2]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}&fieldname={fieldname}" +
                                "&value={value}",
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
            HomeTownActivity.this.finish();
        }

    }

    private class DetailedHometownTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... info) {
            final String url = HomeTownActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/detailedHometown.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            urlArgs.put("province", info[1]);
            urlArgs.put("city", info[2].equals("")?null:info[2]);
            urlArgs.put("district", info[3].equals("")?null:info[3]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}&province={province}&city={city}&district={district}",
                        ErrorMsg.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg data) {

        }

    }
}
