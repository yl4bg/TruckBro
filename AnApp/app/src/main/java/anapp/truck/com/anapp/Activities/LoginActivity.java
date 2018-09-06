package anapp.truck.com.anapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.User;
import anapp.truck.com.anapp.utility.getui.GetuiUtil;
import anapp.truck.com.anapp.utility.location.GPSTracker;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Login and authentication
 */
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);

        GPSTracker.updateContextAndLocation(LoginActivity.this);

        final EditText usernameField = (EditText) this.findViewById(R.id.login_phonenumber_edittext);
        final EditText passwordField = (EditText) this.findViewById(R.id.login_password_edittext);

        passwordField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                            String username = usernameField.getText().toString();
                            String password = passwordField.getText().toString();

                            authenticate_password(username, password, GetuiUtil.getInstance().getClientID());
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        Button loginButton = (Button) this.findViewById(R.id.login_button);
        TextView signupButton = (TextView) this.findViewById(R.id.login_register_button);
        TextView forgetPwButton = (TextView) this.findViewById(R.id.login_forget_pw_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((
                        null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                authenticate_password(username, password, GetuiUtil.getInstance().getClientID());

                return;
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                Intent toSignupView = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(toSignupView);
                return;
            }
        });

        forgetPwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                Intent toForgetPwView = new Intent(LoginActivity.this, ForgetPwActivity.class);
                startActivity(toForgetPwView);
                return;
            }
        });

        checkCookie();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCookie();
    }

    /**
     * At login page, check whether the user is already logged in. If so, redirect to event list.
     */
    private void checkCookie() {
        String filename = this.getString(R.string.login_cookie_storage_file_name);
        File cookieFile = new File(this.getApplicationContext().getFilesDir(), filename);
        if (!cookieFile.exists()) return;
        try {
            BufferedReader in = new BufferedReader(new FileReader(cookieFile));
            String username = in.readLine();
            String cookie = in.readLine();
            if (username == null || cookie == null) {
                Log.e("Cookie", "File Reading Error!");
                throw new Exception();
            }
            authenticate_cookie(cookie);
        } catch (Exception e) {
            cookieFile.delete();
            Log.e("AnApp", "Error reading cookie file. " + e.getMessage(), e);
            return;
        }
    }

    private void deleteCookie() {
        String filename = this.getString(R.string.login_cookie_storage_file_name);
        File cookieFile = new File(this.getApplicationContext().getFilesDir(), filename);
        if (!cookieFile.exists()) return;
        cookieFile.delete();
    }

    /**
     * Authenticate cookie validity from server
     *
     * @param cookie
     */
    private void authenticate_cookie(String cookie) {
        // Async task
        new RegisterTask().execute(cookie);
    }

    /**
     * Authenticate username and password from server
     *
     * @param userID
     * @param password
     * @return a String representing the cookie that the server returns or null if authentication failed
     */
    private void authenticate_password(String userID, String password, String pushClientID) {
        final int TIMEOUT = 10000; // timeout in milliseconds
        final AsyncTask<String, Void, User> task = new RegisterTask();
        task.execute(userID, password, pushClientID);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run() {
                if ( task.getStatus() == AsyncTask.Status.RUNNING ) {
                    task.cancel(true);
                    Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        }, TIMEOUT );
    }

    private void storeLoginStatus(String username, String cookie, String nickName) {
        String filename = this.getString(R.string.login_cookie_storage_file_name);
        String string = username + "\n" + cookie + '\n' + nickName;
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
           Log.e("Cookie Error", "Error writing to cookie file. ");
        }
    }

    /**
     * Authentication request consisting of username and hashed password
     */
    private class RegisterTask extends AsyncTask<String, Void, User> {

        @Override
        /**
         * @param userinfo an array of 3 Strings representing userID, password and pushClientID
         */
        protected User doInBackground(String... userInfo) {
            try {
                final String url = LoginActivity.this.getApplicationContext().getString(R.string.server_prefix)
                        + "/loginRequest.json";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Map<String, String> urlArgs = new HashMap<>();
                String argStr = "";
                if (userInfo.length == 1) {
                    urlArgs.put("cookie", userInfo[0]);
                    argStr = "?cookie={cookie}";
                }
                else {
                    // cookie verification only
                    urlArgs.put("userID", userInfo[0]);
                    // this is actually the plain password
                    urlArgs.put("pwdhash", userInfo[1]);
                    urlArgs.put("pushClientID", userInfo[2]);
                    argStr = "?userID={userID}&pwdhash={pwdhash}&pushClientID={pushClientID}";
                }
                return restTemplate.getForObject(
                        url + argStr,
                        User.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            if (user == null) {
                Toast.makeText(getApplicationContext(),
                        LoginActivity.this.getString(R.string.no_connection),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // cookie in user object is null iff authentication fails
            if (user.getCookie() == null) {
                Toast.makeText(getApplicationContext(), R.string.incorrect_password, Toast.LENGTH_SHORT).show();
            }
            else if (user.getCookie().equals("invalidCookie")){
                Toast.makeText(getApplicationContext(), R.string.invalid_cookie, Toast.LENGTH_SHORT).show();
                deleteCookie();
            }
            else {
                storeLoginStatus(user.getUserID(), user.getCookie(), user.getUsername());

                Toast.makeText(getApplicationContext(), getString(R.string.logged_in), Toast.LENGTH_SHORT).show();
                LoginActivity.this.finish();

            }
        }
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
