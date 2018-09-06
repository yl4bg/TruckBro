package anapp.truck.com.anapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.CookieManager;

/**
 * Created by LaIMaginE on 4/8/2015.
 */
public class SOSCancelActivity extends DefaultActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.sos_waiting_layout);

        TextView numText = (TextView) findViewById(R.id.numNotifiedText);
        Bundle extras = getIntent().getExtras();
        numText.setText(extras.getString("num"));

        ImageButton cancelSOS = (ImageButton) findViewById(R.id.cancel_sos_button);
        cancelSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SOSCancelTask().execute(CookieManager.getInstance().getCookie());
            }
        });
    }

    private class SOSCancelTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        /**
         * locInfo: roadNum, province, city, district
         */
        protected ErrorMsg doInBackground(String... info) {
            final String url = SOSCancelActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/deleteSOS.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}",
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
            SOSCancelActivity.this.finish();
        }

    }
}
