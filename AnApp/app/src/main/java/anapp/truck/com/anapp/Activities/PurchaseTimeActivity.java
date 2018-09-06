package anapp.truck.com.anapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.TruckConst;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created by LaIMaginE on 4/8/2015.
 */
public class PurchaseTimeActivity extends DefaultActivity {

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
        this.setContentView(R.layout.mytruck_layout);

        ((TextView) findViewById(R.id.title_text)).setText("设置购买时间");

        initWheel(R.id.factorytype_field, TruckConst.purchaseYears);
        initWheel(R.id.trucktype_field, TruckConst.purchaseMonths);

        RelativeLayout cancel = (RelativeLayout) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseTimeActivity.this.finish();
            }
        });

        RelativeLayout submit = (RelativeLayout) findViewById(R.id.confirm);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String purchaseTime = formatPurchaseTimeFromWheels();
                new ChangeInfoFieldTask().execute(CookieManager.getInstance().getCookie(),
                        "boughtTime", purchaseTime);
            }
        });
    }

    private String formatPurchaseTimeFromWheels(){
        StringBuilder sb = new StringBuilder();
        sb.append(TruckConst.purchaseYears[getWheel(R.id.factorytype_field).getCurrentItem()]);
        sb.append("年");
        sb.append(TruckConst.purchaseMonths[getWheel(R.id.trucktype_field).getCurrentItem()]);
        return sb.toString();
    }

    /**
     * Updates entered PIN status
     */
    private void updateStatus(int wheelId) {
        // intentionally empty
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
        wheel.setVisibleItems(5);
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
            final String url = PurchaseTimeActivity.this.getApplicationContext().getString(R.string.server_prefix)
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
            PurchaseTimeActivity.this.finish();
        }

    }
}
