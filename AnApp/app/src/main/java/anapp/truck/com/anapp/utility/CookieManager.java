package anapp.truck.com.anapp.utility;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import anapp.truck.com.anapp.R;

/**
 * Created by LaIMaginE on 3/10/2015.
 */
public class CookieManager {

    private static final CookieManager instance = new CookieManager();
    private String cookie;
    private String userID;
    private String nickName;

    private String filename;
    private File fileDir;

    private CookieManager(){}

    public static CookieManager getInstance(){
        return instance;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getNickName(){
        return nickName;
    }
    public void updateCookie(String cookie){
        this.cookie = cookie;
    }
    public String getCookie(){
        return this.cookie;
    }
    public void updateUserID(String userID){
        this.userID = userID;
    }
    public String getUserID(){
        return this.userID;
    }

    public boolean validateCookieFromFile(Context appContext) throws IOException {

        filename = appContext.getString(R.string.login_cookie_storage_file_name);
        fileDir = appContext.getApplicationContext().getFilesDir();
        File cookieFile = new File(fileDir, filename);
        if (!cookieFile.exists()) {
            return false;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(cookieFile));
            String userID = in.readLine();
            String cookie = in.readLine();
            String nickName = in.readLine();
            if (userID == null || cookie == null || nickName == null) {
                throw new Exception();
            }
            this.userID = userID;
            this.cookie = cookie;
            this.nickName = nickName;
//            Log.i("Read from cookie file", userID + cookie + nickName);
            in.close();
        } catch (Exception e) {
            Log.e("AnApp-Cookie", "Failed to retrieve cookie file.");
            in.close();
            return false;
        }
        return true;
    }

}
