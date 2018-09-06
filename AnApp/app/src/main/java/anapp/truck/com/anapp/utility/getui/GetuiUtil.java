package anapp.truck.com.anapp.utility.getui;

import android.app.Application;
import android.content.Context;

import com.igexin.sdk.PushManager;

/**
 * Created by LaIMaginE on 2/17/2015.
 */
public class GetuiUtil{

    private static PushManager pm;
    private static Context appContext;

    private static GetuiUtil instance = new GetuiUtil();
    private GetuiUtil(){}

    public static GetuiUtil getInstance(){
        return instance;
    }

    public void init(Context appContext){
        GetuiUtil.appContext = appContext;
        pm = PushManager.getInstance();
        pm.initialize(appContext);
    }

    public String getClientID(){
        if(pm == null) return "pushIDNotReady";
        return pm.getClientid(appContext);
    }

}
