package anapp.truck.com.anapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.adapters.PointsHistoryAdapter;
import anapp.truck.com.anapp.rest.PointsHistoryList;
import anapp.truck.com.anapp.utility.CookieManager;

/**
 * Created by angli on 3/26/15.
 */
public class PointsDetailActivity extends DefaultActivity {

    private List<PointsHistoryList.PointsHistory> list;
    private ListView mList;
    private PointsHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pointshistory_layout);

        Bundle b = getIntent().getExtras();
        int points = b.getInt("points");

        TextView pointsField = (TextView) findViewById(R.id.points_text);
        pointsField.setText(points + pointsField.getText().toString());

        mList = (ListView) findViewById(R.id.points_history_list);

        list = new ArrayList<>();

        new getPointsHistoryTask().execute(CookieManager.getInstance().getCookie());

        adapter = new PointsHistoryAdapter(PointsDetailActivity.this, list);
        mList.setAdapter(adapter);
        mList.invalidateViews();

        TextView pointsRule = (TextView) findViewById(R.id.points_rule_button);
        pointsRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRule = new Intent(PointsDetailActivity.this, PointsExplainationActivity.class);
                startActivity(toRule);
            }
        });
    }

    private class getPointsHistoryTask extends AsyncTask<String, Void, PointsHistoryList> {

        @Override
        protected PointsHistoryList doInBackground(String... info) {
            final String url = PointsDetailActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/getPointsHistory.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}",
                        PointsHistoryList.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final PointsHistoryList msg) {
            if (msg == null) {
                Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            } else if (msg.getPoinstHistories() == null || msg.getPoinstHistories().size() == 0){
                // intentionally empty
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        list.addAll(msg.getPoinstHistories());
                        adapter.notifyDataSetChanged();
                        mList.invalidateViews();
                    }
                });
            }
        }

    }
}
