package anapp.truck.com.anapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
 * Created by angli on 4/5/15.
 */
public class InformationUpdaterActivity extends DefaultActivity {

    private String title;
    private String fieldName;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.enterfield_layout);

        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        fieldName = bundle.getString("fieldName");

        final TextView titleView = (TextView) findViewById(R.id.title_text);
        titleView.setText(titleView.getText().toString() + title);

        final EditText field = (EditText) findViewById(R.id.enter_field);
        field.setHint("请输入" + title);

        RelativeLayout cancel = (RelativeLayout) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InformationUpdaterActivity.this.finish();
            }
        });

        RelativeLayout submit = (RelativeLayout) findViewById(R.id.confirm);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeInfoFieldTask().execute(CookieManager.getInstance().getCookie(),
                        fieldName, field.getText().toString());
            }
        });
    }

    private class ChangeInfoFieldTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... info) {
            final String url = InformationUpdaterActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/changeInfoField.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            urlArgs.put("fieldname", info[1]);
            urlArgs.put("value", info[2]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}&fieldname={fieldname}" +
                                "&value={value}",
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
            InformationUpdaterActivity.this.finish();
        }

    }
}
