package anapp.truck.com.anapp.utility.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.getui.GetuiUtil;

/**
 * Created by LaIMaginE on 3/12/2015.
 */
public class GPSAlarmReceiver extends BroadcastReceiver {

    public static final String GPS_ALARM = "AnApp_GPS_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {

        GPSTracker.updateContextAndLocation(context);
        Intent serviceIntent = new Intent(context, GPSUploadService.class);
        serviceIntent.putExtra("longitude", Double.toString(GPSTracker.getInstance().getLongitude()));
        serviceIntent.putExtra("latitude", Double.toString(GPSTracker.getInstance().getLatitude()));
        serviceIntent.putExtra("cookie", CookieManager.getInstance().getCookie());
        serviceIntent.putExtra("pushID", GetuiUtil.getInstance().getClientID());
        context.startService(serviceIntent);

    }
}
