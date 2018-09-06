package anapp.truck.com.anapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * Created by ZYL on 2/1/2015.
 */
public class SignupActivity extends Activity {

    private String phoneNum;
    private String password;
    private String referPerson;
    private boolean selected = true;
    private String userType;
    private String driverType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_register);

        registerVericodeHandler();

        final TextView countDown = (TextView) findViewById(R.id.register_get_code_textview);
        final TextView resend = (TextView) findViewById(R.id.register_resend_code_textview);

        final EditText phoneNumField = (EditText) this.findViewById(R.id.register_phonenumber_edittext);
        final EditText confirmationCodeField = (EditText) this.findViewById(R.id.register_put_code);
        final EditText passwordField = (EditText) this.findViewById(R.id.register_newpw_edittext);
        final EditText confirmPasswordField = (EditText) this.findViewById(R.id.register_confirm_newpw_edittext);
        final EditText referralIDField = (EditText) this.findViewById(R.id.register_refer_edittext);

        final CheckBox selectBox = (CheckBox) this.findViewById(R.id.register_consent_checkBox);

        final RelativeLayout requestConfirmationCodeButton = (RelativeLayout) this.findViewById(R.id.register_get_code);

        Button submitButton = (Button) this.findViewById(R.id.register_button);

        TextView enterPrivacyPolicy = (TextView) this.findViewById(R.id.enter_privacy);

        enterPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(SignupActivity.this, PrivacyPolicyActivity.class);
                startActivity(toEnterPage);
            }
        });

        final RadioGroup driverOptions = (RadioGroup) findViewById(R.id.register_driver_options);

        final RadioButton isDriver = (RadioButton) findViewById(R.id.iam_driver);
        final RadioButton isGoodsOwner = (RadioButton) findViewById(R.id.iam_owner);
        final RadioButton isHer = (RadioButton) findViewById(R.id.iam_other);

        final RadioButton profDriver = (RadioButton) findViewById(R.id.iam_driver_professional);
        final RadioButton profOwner = (RadioButton) findViewById(R.id.iam_driver_car_owner);
        final RadioButton profDriverAndOwner = (RadioButton) findViewById(R.id.iam_driver_and_car_owner);

        isDriver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isDriver.isChecked()) {
                    driverOptions.setVisibility(View.VISIBLE);
                }
                else {
                    driverOptions.setVisibility(View.GONE);
                }
            }
        });

        requestConfirmationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String phoneNum = phoneNumField.getText().toString();
                if (!validatePhoneNumFormat(phoneNum)) {
                    Toast.makeText(SignupActivity.this.getApplicationContext(), R.string.phone_num_wrong_format, Toast.LENGTH_LONG).show();
                } else {
                    SignupActivity.this.requestConfirmationCode(phoneNum);
                }

                requestConfirmationCodeButton.setClickable(false);
                countDown.setVisibility(View.INVISIBLE);
                resend.setVisibility(View.VISIBLE);

                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        resend.setText(millisUntilFinished / 1000 + "秒后点击重发");
                    }

                    public void onFinish() {
                        resend.setVisibility(View.INVISIBLE);
                        countDown.setVisibility(View.VISIBLE);
                        requestConfirmationCodeButton.setClickable(true);
                    }
                }.start();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = selectBox.isChecked();
                if(!selected){
                    Toast.makeText(SignupActivity.this.getApplicationContext(),
                            "您必须同意《隐私政策》才能注册", Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNum = phoneNumField.getText().toString();
                password = passwordField.getText().toString();
                referPerson = referralIDField.getText().toString();
                String confirmationCode = confirmationCodeField.getText().toString();
                String passwordConfirm = confirmPasswordField.getText().toString();
                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(SignupActivity.this.getApplicationContext(), R.string.signup_password_not_match, Toast.LENGTH_SHORT).show();
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                    return;
                }

                if(isDriver.isChecked()){
                    userType = "司机";
                } else if (isGoodsOwner.isChecked()){
                    userType = "货主";
                } else if (isHer.isChecked()){
                    userType = "其她";
                }

                if(profDriver.isChecked()){
                    driverType = "司机";
                } else if (profOwner.isChecked()){
                    driverType = "车主";
                } else if (profDriverAndOwner.isChecked()){
                    driverType = "车主兼司机";
                }

                SMSSDK.submitVerificationCode("86", phoneNum, confirmationCode);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    private void authenticateSignup() {

        final HashMap<String, String> info = new HashMap<String, String>();
        info.put("userID", phoneNum);
        info.put("password", password);
        info.put("username", "未设置用户名");
        info.put("referPerson", referPerson);
        info.put("userType", userType);
        info.put("driverType", driverType);

        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              new RegisterTask(info).execute();
                          }
                      }
        );
    }

    private boolean validatePhoneNumFormat(String phoneNum) {
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

    private void requestConfirmationCode(String phoneNum) {
        SMSSDK.getVerificationCode("86", phoneNum);
    }

    /**
     * Authentication request consisting of username and hashed password
     */
    private class RegisterTask extends AsyncTask<String, Void, ErrorMsg> {

        private Map<String, String> args;

        RegisterTask(Map<String, String> args) {
            this.args = args;
        }

        @Override
        /**
         * @param userinfo an array of 2 Strings representing userID, password.
         */
        protected ErrorMsg doInBackground(String... userinfo) {
            try {
                final String url = SignupActivity.this.getApplicationContext().getString(R.string.server_prefix)
                        + "/signupRequest.json";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                String argStr = "?userID={userID}&username={username}&pwdhash={password}&referPerson={referPerson}"
                        + "&userType={userType}&driverType={driverType}";
                return restTemplate.getForObject(url + argStr, ErrorMsg.class, this.args);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg msg) {
            if(msg == null) {
                Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            if (msg.getText() == null) {
                Toast.makeText(getApplicationContext(), R.string.signup_successful, Toast.LENGTH_SHORT).show();
                SignupActivity.this.finish();
            } else {
                Toast.makeText(getApplicationContext(), msg.getText(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void registerVericodeHandler() {

        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                        Log.i("sms", "Verification completed successfully!");
                        SignupActivity.this.authenticateSignup();

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {

                        Log.i("sms", "Verification sent!");
                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              Toast.makeText(SignupActivity.this.getApplicationContext(),
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
                                          Toast.makeText(SignupActivity.this.getApplicationContext(),
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

}
