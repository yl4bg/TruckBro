package anapp.truck.com.anapp.activities;

/**
 * Created by angli on 7/5/15.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.adapters.ChatBubbleAdapter;
import anapp.truck.com.anapp.adapters.EventCellAdapter;
import anapp.truck.com.anapp.chatDataClasses.ChatMessage;
import anapp.truck.com.anapp.rest.ReceivedChatList;
import anapp.truck.com.anapp.rest.ReceivedEventList;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.EventItemOnClickListener;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSManager;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSUploadDelegate;
import anapp.truck.com.anapp.utility.audio.AudioRecordUtil;
import anapp.truck.com.anapp.utility.location.GPSTracker;
import anapp.truck.com.anapp.utility.smack.ChatRefresher;
import anapp.truck.com.anapp.utility.smack.ChatRelatedActivity;
import anapp.truck.com.anapp.utility.smack.SmackUtil;
import anapp.truck.com.anapp.utility.valueObjects.Event;


public class DrivingModeActivity extends DefaultActivity implements
        GeocodeSearch.OnGeocodeSearchListener, ChatRelatedActivity, ChatRefresher, AliOSSUploadDelegate{

    private ProgressDialog progDialog;
    private GeocodeSearch geocoderSearch;

    private ListView eventsList;
    private ListView chatList;
    private List<ChatMessage> chatMsgList = new ArrayList<>();
    private String mFileName;
    private int duration;
    private String chatName;
    private List<ReceivedChatList.ReceivedChat> receivedChats;
    private ReceivedChatList.ReceivedChat currentChat;

    private ChatBubbleAdapter adapter;
    private ImageView VoiceButton;
    private TextView chatGroupName;
    private TextView pressToSpeak;
    private TextView voiceLength;
    private ImageView keyboardButton;
    private AnimationDrawable recordingAnimation;
    private LinearLayout voicePanel;
    private InputMethodManager imm;
    private RelativeLayout keyboardBar;
    private ImageView voicePanelButton;
    private EditText editText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driving_mode);

        SmackUtil.getInstance().updateChatActivityAndClearGroupChatList(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);

        eventsList = (ListView) findViewById(R.id.road_cond_list);
        eventsList.setOnItemClickListener(new EventItemOnClickListener(eventsList, this));

        chatList = (ListView) findViewById(R.id.driving_chat_listView);
        adapter = new ChatBubbleAdapter(this, chatMsgList, GlobalVar.USED_IN_DRIVING_CHAT_PAGE);
        chatList.setAdapter(adapter);

        chatGroupName = (TextView) findViewById(R.id.driving_chat_group_name);

        // for keyboard
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboardBar = (RelativeLayout) findViewById(R.id.driving_mode_toolbar);
        voicePanel = (LinearLayout) findViewById(R.id.driving_mode_voice_panel);
        voicePanelButton = (ImageView) findViewById(R.id.driving_mode_voice_switch);

        // for input
        editText = (EditText) findViewById(R.id.driving_mode_edittext);
        sendButton = (Button) findViewById(R.id.driving_mode_send_button);

        voicePanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardBar.setVisibility(View.GONE);
                voicePanel.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });

        keyboardButton = (ImageView) findViewById(R.id.driving_chat_keyboard_button);
        keyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardBar.setVisibility(View.VISIBLE);
                voicePanel.setVisibility(View.GONE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

//        LinearLayout driving_chat_box = (LinearLayout) findViewById(R.id.driving_chat_box);
//        final GestureDetector gestureDetector =
//                new GestureDetector(new OnSwipeGestureDetector());
//        driving_chat_box.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                if (gestureDetector.onTouchEvent(event)) {
//                    return false;
//                } else {
//                    return true;
//                }
//            }
//        });

        final ImageView prevChat = (ImageView) findViewById(R.id.driving_chat_prev_arrow);
        final ImageView nextChat = (ImageView) findViewById(R.id.driving_chat_next_arrow);
        prevChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNextChat(getPrevChat());
            }
        });
        nextChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNextChat(getNextChat());
            }
        });

        setUpBottomBox();

        // refresh on create
        refresh();
    }

    @Override
    protected void onDestroy(){
        SmackUtil.getInstance().removeDrivingModeDetailActivity(
                chatName);
        super.onDestroy();
    }

    public void uploadComplete(String uuid) {}

    public void uploadAudioComplete(String fileName) {
        SmackUtil.getInstance().sendAudioMessage(
                chatName, fileName, duration);
    }
    public void refreshUI(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                chatList.smoothScrollToPosition(adapter.getCount() - 1);
                chatList.invalidateViews();
            }
        });
    }
    public void clearUnreadCount(int index){}
    public void upUnreadCount(int index){}
    public Activity getActivity(){
        return this;
    }
    public void updateLastestText(String date, String msg, int index){}

    private ReceivedChatList.ReceivedChat getNextChat(){

        if(currentChat == null){
            currentChat = receivedChats.get(0);
        } else {
            int index = receivedChats.indexOf(currentChat);
            if(index == receivedChats.size()-1){
                currentChat = receivedChats.get(0);
            } else {
                currentChat = receivedChats.get(index+1);
            }
        }
        return currentChat;

    }

    private ReceivedChatList.ReceivedChat getPrevChat(){

        if(currentChat == null){
            currentChat = receivedChats.get(0);
        } else {
            int index = receivedChats.indexOf(currentChat);
            if(index == 0){
                currentChat = receivedChats.get(receivedChats.size()-1);
            } else {
                currentChat = receivedChats.get(index-1);
            }
        }
        return currentChat;
    }

    private void enterNextChat(ReceivedChatList.ReceivedChat chatInfo){

        SmackUtil.getInstance().removeDrivingModeDetailActivity(chatName);
        chatMsgList = new ArrayList<>();
        adapter = new ChatBubbleAdapter(this, chatMsgList, GlobalVar.USED_IN_DRIVING_CHAT_PAGE);
        chatList.setAdapter(adapter);

        if(SmackUtil.getInstance().enterOneChat(
                DrivingModeActivity.this, chatInfo)){
            SmackUtil.getInstance().detailedGroupChat(
                    DrivingModeActivity.this, chatMsgList,
                    editText,
                    sendButton,
                    chatInfo.getChatName(), CookieManager.getInstance().getUserID());
        }

        chatName = chatInfo.getChatName();
        chatGroupName.setText(chatName);
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
                EventCellAdapter adapter = new EventCellAdapter(DrivingModeActivity.this,
                        events.toArray(new Event[events.size()]), true);
                DrivingModeActivity.this.eventsList.setAdapter(adapter);
                DrivingModeActivity.this.eventsList.invalidateViews();
            }
        });
    }

    private void refresh() {
        showDialog();
        GPSTracker.updateContextAndLocation(this);

        new RefreshTask().execute(GPSTracker.getInstance().getLatitude(), GPSTracker.getInstance().getLatitude());
        new ConnectToChatTask().execute(CookieManager.getInstance().getUserID());
    }

    public void showDialog() {
        if(progDialog != null) {
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setIndeterminate(false);
            progDialog.setCancelable(true);
            progDialog.setMessage("正在获取路况数据");
            progDialog.show();
        }
    }

    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void setUpBottomBox() {
        VoiceButton = (ImageView) findViewById(R.id.driving_chat_voice_button);
        pressToSpeak = (TextView) findViewById(R.id.driving_chat_press_to_speak);
        voiceLength = (TextView) findViewById(R.id.driving_chat_timer_textview);
        recordingAnimation = (AnimationDrawable) voiceLength.getBackground();

        VoiceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mFileName = AudioRecordUtil.AUDIO_FILE_PREFIX + UUID.randomUUID().toString();
                    AudioRecordUtil.getInstance().onRecord(true, mFileName);
                    AudioRecordUtil.getInstance().startShowTimer(voiceLength);
                    pressToSpeak.setVisibility(View.GONE);
                    voiceLength.setVisibility(View.VISIBLE);
                    // Log.d("check check check check", Boolean.toString(timer.getVisibility() == View.VISIBLE));
                    recordingAnimation.start();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    voiceLength.setVisibility(View.GONE);
                    recordingAnimation.stop();
                    pressToSpeak.setVisibility(View.VISIBLE);

                    AudioRecordUtil.getInstance().onRecord(false, null);
                    AudioRecordUtil.getInstance().stopShowTimer();
                    duration = AudioRecordUtil.getInstance().getRecordingDuraiton();
                    if(duration >= 1) {
                        AliOSSManager.uploadAudioFile(
                                AudioRecordUtil.formatFullFilePath(mFileName),
                                mFileName,
                                DrivingModeActivity.this);
                    } else {
                        ToastUtil.show(DrivingModeActivity.this, "语音时间过短");
                    }

                    return true;
                }
                return false;
            }
        });
    }

    private class RefreshTask extends AsyncTask<Double, Void, ReceivedEventList> {

        @Override
        /**
         * @param events an array of Event objects
         */
        protected ReceivedEventList doInBackground(Double... coords) {
            try {
                final String url = DrivingModeActivity.this.getApplicationContext().getString(R.string.server_prefix)
                        + "/getEventsRequest.json";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Map<String, String> urlArgs = GPSTracker.getInstance().toLocationInfoMap();
                return restTemplate.getForObject(
                        url + "?longitude={longitude}&latitude={latitude}",
                        ReceivedEventList.class, urlArgs);
            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReceivedEventList eventList) {
            if(eventList!=null) {
                List<Event> events = eventList.toUiEventList();
                reload(events);
            } else {
                Toast.makeText(getApplicationContext(),
                        DrivingModeActivity.this.getString(R.string.no_connection),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void updateDialogMessage(String msg){
        progDialog.setMessage(msg);
    }

    private class FetchChatDataTask extends AsyncTask<String, Void, ReceivedChatList> {

        @Override
        protected ReceivedChatList doInBackground(String... info) {
            try {
                final String url = DrivingModeActivity.this.getApplicationContext().getString(R.string.server_prefix)
                        + "/fetchChatList.json";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Map<String, String> urlArgs = new HashMap<>();
                urlArgs.put("province", info[0]);
                urlArgs.put("city", info[1]);
                urlArgs.put("district", info[2]);
                urlArgs.put("cookie", info[3]);

                return restTemplate.getForObject(
                        url + "?province={province}&city={city}"
                                + "&district={district}&cookie={cookie}",
                        ReceivedChatList.class, urlArgs);

            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReceivedChatList chatList) {
            if(chatList != null) {
                receivedChats = chatList.getChats();
            } else {
                ToastUtil.show(getApplicationContext(),
                        getString(R.string.server_err));
            }
            dismissDialog();
            enterNextChat(getNextChat());
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {}

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 0) {

            String province = result.getRegeocodeAddress().getProvince();
            String city = result.getRegeocodeAddress().getCity();
            String district = result.getRegeocodeAddress().getDistrict();

            updateDialogMessage("正在获取聊天列表及离线信息");
            new FetchChatDataTask().execute(province, city, district,
                    CookieManager.getInstance().getCookie());

        } else {
            dismissDialog();
            ToastUtil.show(getApplicationContext(),
                    getString(R.string.server_err) + rCode);
        }
    }

    private void getAddress(final LatLng latLng) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(
                new LatLonPoint(latLng.latitude, latLng.longitude),
                500,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    private class ConnectToChatTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... info) {
            SmackUtil.getInstance().connectIfNot(info[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
            updateDialogMessage("正在确认当前GPS地点");
            getAddress(GPSTracker.getInstance().getLatLng());
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
                enterNextChat(getNextChat());

            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // swipe right show left
                enterNextChat(getPrevChat());
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
