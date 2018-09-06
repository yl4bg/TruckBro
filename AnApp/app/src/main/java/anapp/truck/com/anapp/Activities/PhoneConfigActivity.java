package anapp.truck.com.anapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.CookieManager;

/**
 * Created by angli on 7/6/15.
 */
public class PhoneConfigActivity extends DefaultActivity {

    private boolean showToDriver;
    private boolean showToHer;
    private boolean showToOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_config);

        RelativeLayout confirm = (RelativeLayout) findViewById(R.id.confirm_edit);
        RelativeLayout cancel = (RelativeLayout) findViewById(R.id.cancel_edit);

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

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateShowInChatTask().execute(CookieManager.getInstance().getCookie(),
                        Boolean.toString(showToDriver),
                        Boolean.toString(showToOwner),
                        Boolean.toString(showToHer));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneConfigActivity.this.finish();
            }
        });
    }

    private class UpdateShowInChatTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... info) {
            final String url = PhoneConfigActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/updateShowPhoneInChat.json";
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
