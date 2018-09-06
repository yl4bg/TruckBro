package anapp.truck.com.anapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ChatUserProfile;
import anapp.truck.com.anapp.utility.GlobalVar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ChatUserProfileActivity extends ActionBarActivity {

    private ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        progDialog = new ProgressDialog(this);
        showDialog();

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle extras = getIntent().getExtras();
        String userId = extras.getString("userId");
        new FetchChatProfileTask().execute(userId);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        progDialog.dismiss();
        progDialog.onDetachedFromWindow();
        progDialog = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    public void showDialog() {
        if(progDialog != null) {
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setIndeterminate(false);
            progDialog.setCancelable(true);
            progDialog.setMessage("正在读取用户数据");
            progDialog.show();
        }
    }

    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void refreshDisplay(final ChatUserProfile profile){

        this.getSupportActionBar().setTitle(profile.getNickName());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.user_profile_user_name)).setText(profile.getNickName());
                ((TextView)findViewById(R.id.user_profile_user_from)).setText(profile.getHometown());
                ((TextView)findViewById(R.id.user_profile_user_truck)).setText(profile.getMyTruck());
                ((TextView)findViewById(R.id.user_profile_user_contact)).setText(profile.getUserId());
                String list = profile.getFrequentPlaceList().toString();
                ((TextView)findViewById(R.id.user_profile_user_places)).setText(
                        list.substring(1, list.length()-1).replaceAll(",","\n")
                );
            }
        });
    }


    private class FetchChatProfileTask extends AsyncTask<String, Void, ChatUserProfile> {

        @Override
        protected ChatUserProfile doInBackground(String... info) {
            try {
                final String url = ChatUserProfileActivity.this.getApplicationContext().getString(R.string.server_prefix)
                        + "/fetchChatUserProfile.json";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Map<String, String> urlArgs = new HashMap<>();
                urlArgs.put("userId", info[0]);

                return restTemplate.getForObject(
                        url + "?userId={userId}",
                        ChatUserProfile.class, urlArgs);

            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ChatUserProfile chatUserProfile) {
            if(chatUserProfile.getNickName()==null){
                chatUserProfile.setNickName(GlobalVar.NO_NAME);
            }
            refreshDisplay(chatUserProfile);
            dismissDialog();
        }
    }
}
