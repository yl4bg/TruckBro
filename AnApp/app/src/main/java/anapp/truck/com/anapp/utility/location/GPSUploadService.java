package anapp.truck.com.anapp.utility.location;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;

/**
 * Created by LaIMaginE on 3/12/2015.
 */
public class GPSUploadService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.i("GPSUpload", "Service created!");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i("GPSUpload", "Service started!");
        Bundle bundle = intent.getExtras();
        new LocationUpdateTask().execute(bundle.getString("longitude"),
                bundle.getString("latitude"),
                bundle.getString("cookie"),
                bundle.getString("pushID"));
        this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    private class LocationUpdateTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        /**
         * locInfo: roadNum, province, city, district
         */
        protected ErrorMsg doInBackground(String... info) {
            final String url = GPSUploadService.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/locationUpdate.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("longitude", info[0]);
            urlArgs.put("latitude", info[1]);
            urlArgs.put("cookie", info[2]);
            urlArgs.put("pushID", info[3]);
            try {
                return restTemplate.getForObject(
                        url + "?longitude={longitude}&latitude={latitude}" +
                                "&cookie={cookie}&pushID={pushID}",
                        ErrorMsg.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg msg) {
            if (msg!= null && msg.getText() != null) {
                Log.e("GPSUpload", msg.getText());
            }
        }

    }
}
