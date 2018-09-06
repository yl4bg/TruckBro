package anapp.truck.com.anapp.activities;

import android.app.Activity;
import android.content.Context;
import android.nfc.FormatException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by angli on 3/26/15.
 */
public class ForgetPwActivity extends Activity {

    private String userID;
    private String password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_forget_password);

        registerVericodeHandler();

        final TextView countDown = (TextView) findViewById(R.id.resend_code_textview);
        final TextView getCode = (TextView) findViewById(R.id.get_code_textview);

        final EditText phoneNumField = (EditText) findViewById(R.id.forget_password_phonenumber_edittext);
        final RelativeLayout requestConfirmationCodeButton = (RelativeLayout) findViewById(R.id.forget_password_get_code);
        final EditText vericodeField = (EditText) findViewById(R.id.forget_password_put_code);

        final Button submitButton = (Button) findViewById(R.id.reset_password_button);
        final EditText pw1 = (EditText) findViewById(R.id.forget_password_newpw_edittext);
        final EditText pw2 = (EditText) findViewById(R.id.forget_password_confirm_newpw_edittext);

        requestConfirmationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String phoneNum = phoneNumField.getText().toString();
                if (!validatePhoneNumFormat(phoneNum)) {
                    Toast.makeText(ForgetPwActivity.this.getApplicationContext(), R.string.phone_num_wrong_format, Toast.LENGTH_LONG).show();
                } else {
                    ForgetPwActivity.this.requestConfirmationCode(phoneNum);
                }

                requestConfirmationCodeButton.setClickable(false);
                getCode.setVisibility(View.INVISIBLE);
                countDown.setVisibility(View.VISIBLE);
                new CountDownTimer(30000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        countDown.setText(millisUntilFinished / 1000 + "秒后点击重发");
                    }
                    public void onFinish() {
                        getCode.setVisibility(View.VISIBLE);
                        countDown.setVisibility(View.INVISIBLE);
                        requestConfirmationCodeButton.setClickable(true);
                    }
                }.start();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String password1 = pw1.getText().toString();
                String password2 = pw2.getText().toString();
                if (!password1.equals(password2)) {
                    Toast.makeText(ForgetPwActivity.this.getApplicationContext(),
                            R.string.signup_password_not_match,
                            Toast.LENGTH_LONG).show();
                    pw1.setText("");
                    pw2.setText("");
                    return;
                }

                String phoneNum = phoneNumField.getText().toString();
                String vericode = vericodeField.getText().toString();
                ForgetPwActivity.this.userID = phoneNum;
                ForgetPwActivity.this.password = password1;
                SMSSDK.submitVerificationCode("86", phoneNum, vericode);

            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    private void requestConfirmationCode(String phoneNum) {
        SMSSDK.getVerificationCode("86", phoneNum);
    }

    public boolean validatePhoneNumFormat(String phoneNum) {
        try {
            if (phoneNum.length() < 11) {
                throw new FormatException();
            }
            Long.parseLong(phoneNum);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void registerVericodeHandler() {

        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                        Log.i("sms", "Verification completed successfully!");

                        new PwResetTask().execute(userID, password);

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {

                        Log.i("sms", "Verification sent!");
                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              Toast.makeText(ForgetPwActivity.this.getApplicationContext(),
                                                      "验证码已发送", Toast.LENGTH_SHORT).show();
                                          }
                                      }
                        );

                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {

                        ArrayList<HashMap<String, Object>> countries = (ArrayList<HashMap<String, Object>>) data;
                        for (HashMap<String, Object> map : countries) {
                            Log.i("sms", "Map\n");
                            for (String key : map.keySet()) {
                                Log.i("sms", "Key: " + key + " Value: " + map.get(key));
                            }
                        }

                    }
                } else {
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          Toast.makeText(ForgetPwActivity.this.getApplicationContext(),
                                                  "验证码输入错误", Toast.LENGTH_SHORT).show();
                                      }
                                  }
                    );
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

//    /**
//     * 这部分也很重要！每个activity都要放 不然字体就不会被改
//     * @param newBase
//     */
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    private class PwResetTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... info) {
            final String url = ForgetPwActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/pwResetRequest.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<String, String>();
            urlArgs.put("userID", info[0]);
            urlArgs.put("pwdhash", info[1]);

            try {
                return restTemplate.getForObject(
                        url + "?userID={userID}&pwdhash={pwdhash}",
                        ErrorMsg.class, urlArgs);
            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(ErrorMsg msg) {
            if(msg == null){
                Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), msg.getText(), Toast.LENGTH_SHORT).show();
            ForgetPwActivity.this.finish();
        }
    }

}
