package anapp.truck.com.anapp.utility;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by angli on 7/1/15.
 */
public class ForegroundChecker {

    public static boolean isInForeground(Context appContext){

        ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(appContext.getPackageName().toString())) {
            isActivityFound = true;
        }

        return isActivityFound;

    }
}
