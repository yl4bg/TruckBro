package anapp.truck.com.anapp.utility.smsVerify;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by LaIMaginE on 3/20/2015.
 */
public class SMSVerifyUtil {

    private static final String APPKEY = "64899bcd3a50";
    private static final String APPSECRET = "48d37fcbf56a950967d0428ebe1c2896";

    public static void init(final Context appActivity){
        SMSSDK.initSDK(appActivity.getApplicationContext(), APPKEY, APPSECRET);
    }

}
