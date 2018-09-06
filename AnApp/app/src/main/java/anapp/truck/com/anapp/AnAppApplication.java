package anapp.truck.com.anapp;

import android.app.Application;

import anapp.truck.com.anapp.utility.TextField;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSManager;
import anapp.truck.com.anapp.utility.audio.AudioRecordUtil;
import anapp.truck.com.anapp.utility.getui.GetuiUtil;
import anapp.truck.com.anapp.utility.smack.ChatNotifier;
import anapp.truck.com.anapp.utility.smsVerify.SMSVerifyUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Anna on 6/17/15.
 *
 * Override the Application's onCreate method to change font and init static classes
 */
public class AnAppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/FZYTK.TTF")
                        .setFontAttrId(R.attr.fontPath)
                        .addCustomStyle(TextField.class, R.attr.textFieldStyle)
                        .build()
        );

        // init chat notifier & other static classes
        ChatNotifier.getInstance().init(this.getApplicationContext());
        AudioRecordUtil.getInstance();

        // ge tui init to get clientID ready
        GetuiUtil.getInstance().init(this.getApplicationContext());

        // init ali OSS
        AliOSSManager.init(getApplicationContext());

        // init sms verify
        SMSVerifyUtil.init(this.getApplicationContext());
    }


}
