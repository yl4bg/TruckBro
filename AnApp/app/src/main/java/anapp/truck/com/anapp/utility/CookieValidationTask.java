package anapp.truck.com.anapp.utility;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.activities.DefaultActivity;
import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.User;

/**
 * Created by LaIMaginE on 3/14/2015.
 */
public class CookieValidationTask extends AsyncTask<String, Void, User> {

    private String fileName;
    private File fileDir;
    private DefaultActivity appActivity;

    public CookieValidationTask(){}

    public CookieValidationTask(DefaultActivity appActivity){
        this.fileDir = appActivity.getApplicationContext().getFilesDir();
        this.fileName = appActivity.getApplicationContext().getString(R.string.login_cookie_storage_file_name);
        this.appActivity = appActivity;
//        Log.i("task", "created...");
    }

    @Override
    protected User doInBackground(String... userInfo) {
        try {
            final String url = appActivity.getString(R.string.server_prefix)
                    + "/loginRequest.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", userInfo[0]);
            String argStr = "?cookie={cookie}";
            return restTemplate.getForObject(
                    url + argStr,
                    User.class, urlArgs);

        } catch (Exception e) {
            Log.e("AnApp", e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(User user) {
        if (user == null || user.getCookie() == null) {
            Toast.makeText(appActivity.getApplicationContext(),
                    appActivity.getString(R.string.no_connection),
                    Toast.LENGTH_SHORT);
            return;
        }
        if (user.getCookie().equals("invalidCookie")){
            deleteCookie();
            appActivity.onResume();
        }
    }

    private void deleteCookie() {
        File cookieFile = new File(fileDir, fileName);
        if (!cookieFile.exists()) return;
        cookieFile.delete();
    }
}