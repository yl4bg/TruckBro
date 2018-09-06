package anapp.truck.com.anapp.activities;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.audio.AudioRecordUtil;
import anapp.truck.com.anapp.utility.image.DisplayImageUtil;
import anapp.truck.com.anapp.utility.image.ImageRenderer;
import anapp.truck.com.anapp.utility.valueObjects.Event;

/**
 * Created by ZYL on 2/7/2015.
 */
public class EventDetailActivity extends DefaultActivity implements ImageRenderer{

    private MapView mapView;
    private AMap aMap;
    private Marker geoMarkerSrc;
    private Marker geoMarkerDest;
    private LatLng src;
    private LatLng dest;

    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private String uuid;
    private String[] picIDs;

    private TextView upvoteText;
    private TextView reportText;
    private String upCnt;
    private String reportCnt;

    private String mFileName;
    private String description;

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventdetail_layout);

        Bundle extras = getIntent().getExtras();
        final String eventID = extras.getString("eventID");
        String type = extras.getString("type");
        String distance = extras.getString("distance");
        String direction = extras.getString("direction");
        String descriptionText = extras.getString("description");
        String time = extras.getString("time");
        double srcLongitude = extras.getDouble("srcLongitude");
        double srcLatitude = extras.getDouble("srcLatitude");
        String eventLongitude = extras.getString("eventLongitude");
        String eventLatitude = extras.getString("eventLatitude");
        String roadNumText = extras.getString("roadNumText");
        String addressText = extras.getString("addressText");
        upCnt = extras.getString("upCnt");
        reportCnt = extras.getString("reportCnt");
        String senderName = extras.getString("senderName");

        // audio related
        if(descriptionText.contains(GlobalVar.CHAT_AUDIO_INDICATOR)){
            description = descriptionText.split(GlobalVar.CHAT_AUDIO_INDICATOR)[0];
            mFileName = descriptionText.split(GlobalVar.CHAT_AUDIO_INDICATOR)[1];
        } else {
            description = descriptionText;
        }
        if(mFileName!=null){
            new DownloadFileTask().execute(
                    this.getString(R.string.alibucket) + mFileName,
                    AudioRecordUtil.formatFullFilePath(mFileName)
            );
        }

        src = new LatLng(srcLatitude, srcLongitude);
        dest = new LatLng(Double.parseDouble(eventLatitude), Double.parseDouble(eventLongitude));
        uuid = extras.getString("uuid");

        if(extras.getString("picIDs").equals("")) {
            this.picIDs = null;
        } else {
            this.picIDs = extras.getString("picIDs").split(",");
        }

        // start setting UI fields

        TextView typeField = (TextView) findViewById(R.id.typeText);
        TextView distanceField = (TextView) findViewById(R.id.distanceText);
        TextView descriptionField = (TextView) findViewById(R.id.descriptionText);
        TextView timeField = (TextView) findViewById(R.id.timeText);
        TextView directionField = (TextView) findViewById(R.id.directionText);
        TextView roadNumView = (TextView) findViewById(R.id.roadNum);
        TextView addressView = (TextView) findViewById(R.id.addressText);
        TextView publisher = (TextView) findViewById(R.id.publisher_name);
        final LinearLayout playVoiceButton = (LinearLayout) findViewById(R.id.voice_play_bg);

        this.img1 = (ImageView) findViewById(R.id.img1);
        this.img2 = (ImageView) findViewById(R.id.img2);
        this.img3 = (ImageView) findViewById(R.id.img3);

        typeField.setText(type);
        distanceField.setText(distance);
        directionField.setText(direction);
        timeField.setText(time);
        roadNumView.setText(roadNumText);
        addressView.setText(addressText);
        descriptionField.setText(
                description.replace(getString(R.string.click_view_image_hint), ""));
        publisher.setText(senderName);

        ImageView typeIcon = (ImageView) findViewById(R.id.event_icon);
        if(type.equals(getString(R.string.event_type_accident))){
            typeIcon.setImageResource(R.drawable.accident_new_symbol);
        } else if(type.equals(getString(R.string.event_type_construction))){
            typeIcon.setImageResource(R.drawable.construction_new_symbol);
        } else if(type.equals(getString(R.string.event_type_limitedflow))){
            typeIcon.setImageResource(R.drawable.limited_new_symbol);
        } else if(type.equals(getString(R.string.event_type_regulation))){
            typeIcon.setImageResource(R.drawable.regulation_new_symbol);
        } else if(type.equals(getString(R.string.event_type_trafficjam))){
            typeIcon.setImageResource(R.drawable.crowded_new_symbol);
        } else {
            typeIcon.setImageResource(R.drawable.other_new_symbol);
        }

        LinearLayout upvoteButton = (LinearLayout) findViewById(R.id.upvote_layout);
        LinearLayout reportButton = (LinearLayout) findViewById(R.id.report_layout);
        upvoteText = (TextView) findViewById(R.id.upvote_text);
        reportText = (TextView) findViewById(R.id.report_text);
        upvoteText.setText(upvoteText.getText() + "(" + upCnt + ")");
        reportText.setText(reportText.getText() + "(" + reportCnt + ")");

        upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VoteUpTask().execute(CookieManager.getInstance().getCookie(),
                        eventID);
            }
        });
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReportTask().execute(CookieManager.getInstance().getCookie(),
                        eventID);
            }
        });

        final ImageView thisVoiceUnheard = (ImageView) findViewById(R.id.voice_unheard);
        if(mFileName == null){
            playVoiceButton.setVisibility(View.INVISIBLE);
            thisVoiceUnheard.setVisibility(View.INVISIBLE);
        }

        playVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mFileName!=null) {
                    // play animation
                    ImageView voicePlay = (ImageView) findViewById(R.id.voice_play);
                    voicePlay.setBackgroundResource(R.drawable.play_voice);
                    final AnimationDrawable voicePlayAnimation = (AnimationDrawable) voicePlay.getBackground();
                    voicePlayAnimation.start();

                    File audioFile = new File(AudioRecordUtil.formatFullFilePath(mFileName));
                    if (audioFile.exists()) {
                        AudioRecordUtil.getInstance().onPlay(true, mFileName);
                    } else {
                        ToastUtil.show(EventDetailActivity.this,
                                "语音文件还未被下载，请稍后再试。");
                    }
                    thisVoiceUnheard.setVisibility(View.INVISIBLE);
                    playVoiceButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            voicePlayAnimation.stop();
                            voicePlayAnimation.selectDrawable(2); // so that each time it stops at the same frame
                        }
                    }, 1000);
                }
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapView = (MapView) findViewById(R.id.map);
                mapView.onCreate(savedInstanceState);
                if (EventDetailActivity.this.picIDs != null) {
                    DisplayImageUtil.fetchImage(
                            EventDetailActivity.this,
                            EventDetailActivity.this,
                            uuid, picIDs);
                }
                initMap();
            }
        });

    }

    @Override
    protected void onDestroy(){
//        Log.i("eventDetail", "Destroyed...");
        mapView.onDestroy();
        if(img1 != null && img1.getDrawable()!=null) {
//            ((BitmapDrawable) img1.getDrawable()).getBitmap().recycle();
            img1.destroyDrawingCache();
            img1.setImageBitmap(null);
        }
        if(img2 != null && img2.getDrawable()!=null) {
//            ((BitmapDrawable) img2.getDrawable()).getBitmap().recycle();
            img2.destroyDrawingCache();
            img2.setImageBitmap(null);
        }
        if(img3!= null && img3.getDrawable()!=null) {
//            ((BitmapDrawable) img3.getDrawable()).getBitmap().recycle();
            img3.destroyDrawingCache();
            img3.setImageBitmap(null);
        }
        super.onDestroy();
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            geoMarkerSrc = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_self_pin)).title("当前位置"));
            geoMarkerDest = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_event_pin)).title("路况位置"));
            geoMarkerSrc.setPosition(src);
            geoMarkerDest.setPosition(dest);
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds.builder().include(src).include(dest).build(),
                    mapView.getWidth(), mapView.getHeight(), 15));
        }
//        progDialog = new ProgressDialog(this);

    }

    // render an image. ultimately it will dynamically create an ImageView. now it just overwrites the only ImageView
    // if rendering is successful and url is not null, add url to records
    public void renderImage(String filename) {
        ImageView img = null;
        if(img1.getDrawable()==null) {
            img = img1;
        } else if(img2.getDrawable()==null) {
            img = img2;
        } else if(img3.getDrawable()==null) {
            img = img3;
        } else {
            return;
        }
        try {
            img.setVisibility(View.VISIBLE);
            img.setImageURI(Uri.parse(filename));
        } catch (Exception e) {
            Log.e("EventDetailPage", "Error rendering image for " + filename);
            return;
        }
    }

    private class VoteUpTask extends AsyncTask<String, Void, ErrorMsg> {

        private int position;

        @Override
        /**
         * @param events an array of Event objects
         */
        protected ErrorMsg doInBackground(String... info) {
            final String url = getString(R.string.server_prefix)
                    + "/voteUpEvent.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            urlArgs.put("eventId", info[1]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}&eventId={eventId}",
                        ErrorMsg.class, urlArgs);
            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg msg) {
            if(msg!=null) {
                Toast.makeText(getApplicationContext(),
                        msg.getText(),
                        Toast.LENGTH_SHORT).show();
                if(msg.getText().contains("成功")){
                    upvoteText.setText("(" + (Integer.parseInt(upCnt)+1) + ")");
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.server_err),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class ReportTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        /**
         * @param events an array of Event objects
         */
        protected ErrorMsg doInBackground(String... info) {
            final String url = getString(R.string.server_prefix)
                    + "/voteDownEvent.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            urlArgs.put("eventId", info[1]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}&eventId={eventId}",
                        ErrorMsg.class, urlArgs);
            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg msg) {
            if(msg!=null) {
                Toast.makeText(getApplicationContext(),
                        msg.getText(),
                        Toast.LENGTH_SHORT).show();
                if(msg.getText().contains("收到")){
                    reportText.setText("(" + (Integer.parseInt(reportCnt)+1) + ")");
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.server_err),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class DownloadFileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String fullFilePath = params[1];
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            final File file = new File(fullFilePath);
            if(file.exists()) return fullFilePath;
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                DisplayImageUtil.downloadFileFromUrl(url, fullFilePath);
//                Log.i("Ali download", "Url = " + url + ", filepath = " + fullFilePath);
            } catch (Exception e) {
                Log.e("Audio File Download", e.getMessage());
                return null;
            }
            return fullFilePath;
        }
        @Override
        protected void onPostExecute(String fullFilePath) {
            if(fullFilePath == null) {
                Log.e("Chat Audio", "file download failed for " + fullFilePath);
                return;
            }
//            else {
//                Log.i("Ali download", "Success! filepath = " + fullFilePath);
//            }
        }
    }

}
