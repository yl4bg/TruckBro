package anapp.truck.com.anapp.activities;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.dialogs.CustomerServiceDialog;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.dialogs.AppUpdateDialog;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.location.GPSAlarmReceiver;
import anapp.truck.com.anapp.utility.location.GPSTracker;
import anapp.truck.com.anapp.utility.smack.SmackUtil;

/**
 * Created by angli on 3/25/15.
 */
public class MainPageActivity extends DefaultActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private static final String SAW_WELCOME = "saw welcome page";

    private PendingIntent alarmIntent;
    private ViewFlipper viewFlipper;
    private ImageView sosButton;

    private SliderLayout mDemoSlider;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.homepage);

        GPSTracker.updateContextAndLocation(this);

        viewFlipper = (ViewFlipper) findViewById(R.id.mainPageFlipper);
        final GestureDetector gestureDetector =
                new GestureDetector(new OnSwipeGestureDetector());
        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return false;
                } else {
                    return true;
                }
            }
        });

        ImageView eventsButton = (ImageView) findViewById(R.id.home_event);
        sosButton = (ImageView) findViewById(R.id.home_sos);
        ImageView chatButton = (ImageView) findViewById(R.id.home_chat);
        ImageView csButton = (ImageView) findViewById(R.id.home_phone);
        ImageView personalButton = (ImageView) findViewById(R.id.home_profile);
        ImageView personalButton2 = (ImageView) findViewById(R.id.home_profile_2);
        ImageView driveMode = (ImageView) findViewById(R.id.drive_mode);

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toNext = new Intent(MainPageActivity.this, EventTabActivity.class);
                startActivity(toNext);
            }
        });

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sosButton.setEnabled(false);
                new SOSNumTask().execute(CookieManager.getInstance().getCookie());
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLevelPage = new Intent(MainPageActivity.this, ChatMainActivity.class);
                startActivity(toLevelPage);
            }
        });

        csButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new CustomerServiceDialog();
                newFragment.show(MainPageActivity.this.getFragmentManager(), "service_call");
            }
        });

        personalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toNext = new Intent(MainPageActivity.this, UserProfileActivity.class);
                startActivity(toNext);
            }
        });

        personalButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toNext = new Intent(MainPageActivity.this, UserProfileActivity.class);
                startActivity(toNext);
            }
        });


        driveMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toNext = new Intent(MainPageActivity.this, DrivingModeActivity.class);
                startActivity(toNext);
            }
        });

        // move alarm and destroyalarm to main page
        startGPSAlarm();

        checkVersionUpdate();

        connectToChatServer();


        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("ad1",R.drawable.ad1);
        file_maps.put("ad2", R.drawable.ad2);
        file_maps.put("ad3", R.drawable.ad3);

        for(String name : file_maps.keySet()){
            DefaultSliderView sliderView = new DefaultSliderView(this);
            // initialize a SliderLayout
            sliderView
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(sliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//        mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(SAW_WELCOME, false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(SAW_WELCOME, Boolean.TRUE);
            edit.commit();
            Intent i = new Intent(MainPageActivity.this, WelcomeActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean validateCookie() {
        try {
            if (!CookieManager.getInstance().validateCookieFromFile(this.getApplicationContext())) {
                Intent toEnterPage = new Intent(MainPageActivity.this, LoginActivity.class);
                startActivity(toEnterPage);
                return false;
            }
        } catch (IOException e){
            Log.e("CookieFile", "IOException! " + e.getMessage());
            Intent toEnterPage = new Intent(MainPageActivity.this, LoginActivity.class);
            startActivity(toEnterPage);
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void checkVersionUpdate(){
        new VersionCheckTask().execute();
    }

    private void connectToChatServer(){
        if(CookieManager.getInstance()!=null)
            new ConnectToChatTask().execute(CookieManager.getInstance().getUserID());
    }

    private void startGPSAlarm(){
        // gps alarm init
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentOnAlarm = new Intent(GPSAlarmReceiver.GPS_ALARM);
        alarmIntent = PendingIntent.getBroadcast(MainPageActivity.this, 0, intentOnAlarm, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 10 * 1000,
                5 * 60 * 1000, // set at 5 min
                alarmIntent);
    }

    private class SOSNumTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        /**
         * locInfo: roadNum, province, city, district
         */
        protected ErrorMsg doInBackground(String... info) {
            final String url = MainPageActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/getHelpNum.json";
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
                sosButton.setEnabled(true);
                return;
            }
            if(msg.getText().equals("服务器未找到您的SOS")){
                sosButton.setImageResource(R.drawable.sos);
                Intent toNext = new Intent(MainPageActivity.this, SOSActivity.class);
                startActivity(toNext);
            } else {
                sosButton.setImageResource(R.drawable.sos_sent);
                Intent toNext = new Intent(MainPageActivity.this, SOSCancelActivity.class);
                toNext.putExtra("num", msg.getText());
                startActivity(toNext);
            }
            sosButton.setEnabled(true);
        }

    }

    private class VersionCheckTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        /**
         * locInfo: roadNum, province, city, district
         */
        protected ErrorMsg doInBackground(String... info) {
            final String url = MainPageActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/getCurrentAppVersion.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            try {
                return restTemplate.getForObject(
                        url,
                        ErrorMsg.class);
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
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                return;
            }
            String version = pInfo.versionName;
            Log.i("app-version", "current version = " + version + " server version = " + msg.getText());
            if(!version.equals(msg.getText())){
                DialogFragment newFragment = new AppUpdateDialog();
                newFragment.show(MainPageActivity.this.getFragmentManager(), "app_update");
            }
        }

    }

    private class OnSwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
//        System.out.println(" in onFling() :: ");
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // swipe left show right
                Intent toNext = new Intent(MainPageActivity.this, DrivingModeActivity.class);
                startActivity(toNext);
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // swipe right show left
                Intent toNext = new Intent(MainPageActivity.this, UserProfileActivity.class);
                startActivity(toNext);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private class ConnectToChatTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... info) {
            SmackUtil.getInstance().connectIfNot(info[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
        }
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {}

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {}

    @Override
    public void onPageScrollStateChanged(int state) {}
}
