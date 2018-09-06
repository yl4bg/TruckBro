package anapp.truck.com.anapp.activities;

import android.app.DialogFragment;
import android.content.Intent;
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
import anapp.truck.com.anapp.dialogs.SOSMsgDialog;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.rest.SOSDetail;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.audio.AudioRecordUtil;
import anapp.truck.com.anapp.utility.image.DisplayImageUtil;
import anapp.truck.com.anapp.utility.image.ImageRenderer;

/**
 * Created by LaIMaginE on 4/8/2015.
 */
public class SOSDetailActivity extends DefaultActivity implements ImageRenderer {

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
    private String phoneNum = "";
    private String description;
    private String mFileName;

    private int reportCnt;
    private TextView reportText;

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos_detail_layout);

        Bundle extras = getIntent().getExtras();
        final String eventID = extras.getString("eventID");
        final String distance = extras.getString("distance");
        final String direction = extras.getString("direction");
        final String descriptionText = extras.getString("description");
        final String time = extras.getString("time");
        final double srcLongitude = extras.getDouble("srcLongitude");
        final double srcLatitude = extras.getDouble("srcLatitude");
        final String eventLongitude = extras.getString("eventLongitude");
        final String eventLatitude = extras.getString("eventLatitude");
        final String roadNumText = extras.getString("roadNumText");
        final String addressText = extras.getString("addressText");
        final String senderName = extras.getString("senderName");
        reportCnt = Integer.parseInt(extras.getString("reportCnt"));

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

        final TextView distanceField = (TextView) findViewById(R.id.distanceText);
        final TextView descriptionField = (TextView) findViewById(R.id.descriptionText);
        final TextView timeField = (TextView) findViewById(R.id.timeText);
        final TextView directionField = (TextView) findViewById(R.id.directionText);
        final TextView addressView = (TextView) findViewById(R.id.addressText);
        final TextView roadNumField = (TextView) findViewById(R.id.roadNum);
        final TextView senderNameField = (TextView) findViewById(R.id.sos_publisher_name);
        reportText = (TextView) findViewById(R.id.report_text);
        final LinearLayout reportLayout = (LinearLayout) findViewById(R.id.report_layout);

        this.img1 = (ImageView) findViewById(R.id.img1);
        this.img2 = (ImageView) findViewById(R.id.img2);
        this.img3 = (ImageView) findViewById(R.id.img3);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                distanceField.setText(distance);
                directionField.setText(direction);
                timeField.setText(time);
                addressView.setText(addressText);
                descriptionField.setText(
                        description.replace(getString(R.string.click_view_image_hint), ""));
                roadNumField.setText(roadNumText);
                senderNameField.setText(senderName);
                reportText.setText("(" + reportCnt + ")");

                mapView = (MapView) findViewById(R.id.map);
                mapView.onCreate(savedInstanceState);
                if (SOSDetailActivity.this.picIDs != null) {
                    DisplayImageUtil.fetchImage(
                            SOSDetailActivity.this,
                            SOSDetailActivity.this,
                            uuid, picIDs);
                }
                initMap();
            }
        });

        ImageView phone = (ImageView) findViewById(R.id.sos_phone);
        ImageView sms = (ImageView) findViewById(R.id.sos_sms);
        ImageView chat = (ImageView) findViewById(R.id.sos_chat);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNum.length()!=11){
                    Toast.makeText(getApplicationContext(), "电话号码出错，无法拨打", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = "tel:" + phoneNum;
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                startActivity(callIntent);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SOSMsgDialog();
                Bundle args = new Bundle();
                args.putString("phoneNum", phoneNum);
                newFragment.setArguments(args);
                newFragment.show(SOSDetailActivity.this.getFragmentManager(), "sos_sms");
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(SOSDetailActivity.this, ChatMainActivity.class);
                startActivity(toEnterPage);
            }
        });

        reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReportTask().execute(
                        CookieManager.getInstance().getCookie(),
                        eventID);
            }
        });

        new SOSDetailTask().execute(eventID);

        final LinearLayout playVoiceButton = (LinearLayout) findViewById(R.id.voice_play_bg);

        final ImageView thisVoiceUnheard = (ImageView) findViewById(R.id.voice_unheard);
        if(mFileName == null){
            playVoiceButton.setVisibility(View.INVISIBLE);
            thisVoiceUnheard.setVisibility(View.INVISIBLE);
        }
        playVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFileName != null) {
                    // play animation
                    ImageView voicePlay = (ImageView) findViewById(R.id.voice_play);
                    voicePlay.setBackgroundResource(R.drawable.play_voice);
                    final AnimationDrawable voicePlayAnimation = (AnimationDrawable) voicePlay.getBackground();
                    voicePlayAnimation.start();

                    File audioFile = new File(AudioRecordUtil.formatFullFilePath(mFileName));
                    if (audioFile.exists()) {
                        AudioRecordUtil.getInstance().onPlay(true, mFileName);
                    } else {
                        ToastUtil.show(SOSDetailActivity.this,
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
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("当前位置"));
            geoMarkerDest = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("路况位置"));
            geoMarkerSrc.setPosition(src);
            geoMarkerDest.setPosition(dest);
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    LatLngBounds.builder().include(src).include(dest).build(),
                    mapView.getWidth(), mapView.getHeight(), 15));
        }
//        progDialog = new ProgressDialog(this);

    }

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

    private class SOSDetailTask extends AsyncTask<String, Void, SOSDetail> {

        private int position;

        @Override
        /**
         * @param events an array of Event objects
         */
        protected SOSDetail doInBackground(String... info) {
            final String url = getString(R.string.server_prefix)
                    + "/getSOSDetail.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("eventID", info[0]);
            Log.i("sos detail", info[0]);
            try {
                return restTemplate.getForObject(
                        url + "?eventID={eventID}",
                        SOSDetail.class, urlArgs);
            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final SOSDetail data) {
            if(data==null) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.server_err),
                        Toast.LENGTH_SHORT).show();
            } else {

                phoneNum = data.getPhoneNum();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.sos_hometown)).setText(data.getHomeTown());
                        ((TextView) findViewById(R.id.sos_myTruck)).setText("车型: " + data.getMyTruck());
                        ((TextView) findViewById(R.id.sos_user_name)).setText(data.getUserName());
                        ((TextView) findViewById(R.id.sos_license_no)).setText(data.getLicensePlate());
                    }
                });
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
                    reportText.setText("(" + (reportCnt+1) + ")");
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
