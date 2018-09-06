package anapp.truck.com.anapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anapp.truck.com.anapp.adapters.ChatMainAdapter;
import anapp.truck.com.anapp.chatDataClasses.ChatData;
import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ReceivedChatList;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.location.GPSTracker;
import anapp.truck.com.anapp.utility.smack.ChatRelatedActivity;
import anapp.truck.com.anapp.utility.smack.SmackUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ChatMainActivity extends ActionBarActivity
        implements GeocodeSearch.OnGeocodeSearchListener, ChatRelatedActivity{

    private List<ChatData> list;
    private ListView mListView;
    private ProgressDialog progDialog;
    private GeocodeSearch geocoderSearch;
    private ChatMainAdapter adapter;

    @Override
    public Activity getActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main_list_layout);

        // added for driving mode
        SmackUtil.getInstance().updateChatActivityAndClearGroupChatList(this);
        list = SmackUtil.getInstance().getGroupDataList();
        // added for driving mode

        progDialog = new ProgressDialog(this);
        showDialog();
        // set custom action bar
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View actionBarView = inflater.inflate(R.layout.chat_main_action_bar, null);
        this.getSupportActionBar().setCustomView(actionBarView);

        ImageView invite_bro = (ImageView) actionBarView.findViewById(R.id.chat_main_actionbar_invite);
        invite_bro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent invite = new Intent(ChatMainActivity.this, ReferActivity.class);
                startActivity(invite);
            }
        });

        mListView = (ListView) findViewById(R.id.chat_main_listView);
        adapter = new ChatMainAdapter(this, list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatData cellChat = (ChatData) mListView.getAdapter().getItem(position);
                Intent detailView = new Intent(ChatMainActivity.this, ChatDetailActivity.class);
                detailView.putExtra("chatName", cellChat.getName());
                detailView.putExtra("chatId", cellChat.getChatId());
                detailView.putExtra("locked", cellChat.isLocked());
                detailView.putExtra("top", cellChat.isPinned());
                detailView.putExtra("muted", cellChat.isMuted());
                startActivity(detailView);
                return;
            }
        });

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        new ConnectToChatTask().execute(CookieManager.getInstance().getUserID());
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        progDialog.dismiss();
        progDialog.onDetachedFromWindow();
        progDialog = null;
    }

    public void refreshUI(){
        Collections.sort(list);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                mListView.invalidateViews();
            }
        });
    }

    public void updateLastestText(final String timestamp, final String text, final int index){
        list.get(index).setTime(GlobalVar.trimChatDateStr(timestamp));
        list.get(index).setMsg(text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                mListView.invalidateViews();
            }
        });
    }

    public void upUnreadCount(final int index){
        String count = list.get(index).getUnreadCount();
        list.get(index).setUnreadCount(Integer.toString(Integer.parseInt(count) + 1));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                mListView.invalidateViews();
            }
        });
    }

    public void clearUnreadCount(final int index){
        list.get(index).setUnreadCount("0");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                mListView.invalidateViews();
            }
        });
    }

    public void showDialog() {
        if(progDialog != null) {
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setIndeterminate(false);
            progDialog.setCancelable(true);
            progDialog.setMessage("正在登录聊天账号");
            progDialog.show();
        }
    }

    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    public void updateDialogMessage(String msg){
        progDialog.setMessage(msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    /**
//     * 还是换字体用的
//     * @param newBase
//     */
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    private class FetchChatDataTask extends AsyncTask<String, Void, ReceivedChatList> {

        @Override
        protected ReceivedChatList doInBackground(String... info) {
            try {
                final String url = ChatMainActivity.this.getApplicationContext().getString(R.string.server_prefix)
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
                for (ReceivedChatList.ReceivedChat chatInfo : chatList.getChats()) {
                    SmackUtil.getInstance().enterOneChat(
                            ChatMainActivity.this, chatInfo);
                }
                refreshUI();
            } else {
                ToastUtil.show(getApplicationContext(),
                        getString(R.string.server_err));
            }
            dismissDialog();
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
            updateDialogMessage("正在确认当前GPS地点");
            getAddress(GPSTracker.getInstance().getLatLng());
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
}
