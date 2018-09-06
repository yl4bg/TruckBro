package anapp.truck.com.anapp.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.CookieValidationTask;
import anapp.truck.com.anapp.utility.location.GPSTracker;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by LaIMaginE on 3/14/2015.
 */
public class DefaultActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        validateCookie();
        new CookieValidationTask(this).execute(CookieManager.getInstance().getCookie());
        GPSTracker.updateContextAndLocation(this);
    }


    @Override
    public void onResume(){
        super.onResume();
        if(validateCookie()) {
            new CookieValidationTask(this).execute(CookieManager.getInstance().getCookie());
            GPSTracker.updateContextAndLocation(this);
        }
    }

    public boolean validateCookie() {
        try {
            if (!CookieManager.getInstance().validateCookieFromFile(this.getApplicationContext())) {
                finish();
            }
        } catch (IOException e){
            Log.e("CookieFile", "IOException! " + e.getMessage());
            finish();
        }
        return true;
    }

//    /**
//     * 这部分也很重要！每个activity都要放 不然字体就不会被改
//     * @param newBase
//     */
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

}
