package anapp.truck.com.anapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.PersonalInfo;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.image.DisplayImageUtil;
import anapp.truck.com.anapp.utility.image.ImageRenderer;

/**
 * Created by ZYL on 2/20/2015.
 */
public class UserProfileActivity extends DefaultActivity implements ImageRenderer{

    private ProgressDialog progDialog;
    private String driverLicensePic;
    private String registrationPic;
    private String freqPlaces;
    private int points;
    private ImageView profilePic;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        progDialog = new ProgressDialog(this);

        profilePic = (ImageView) this.findViewById(R.id.profile_pic);
        RelativeLayout userIdButton = (RelativeLayout) this.findViewById(R.id.userIdButton);
        ImageView logoutButton = (ImageView) this.findViewById(R.id.log_out_button);
        RelativeLayout inviteButton = (RelativeLayout) this.findViewById(R.id.inviteBroButton);
        RelativeLayout userNameButton = (RelativeLayout) this.findViewById(R.id.userNameButton);
        RelativeLayout nickNameButton = (RelativeLayout) this.findViewById(R.id.nickNameButton);
        RelativeLayout signatureButton = (RelativeLayout) this.findViewById(R.id.signatureButton);
        RelativeLayout homeTownButton = (RelativeLayout) this.findViewById(R.id.homeTownButton);
        RelativeLayout frequentPlacesButton = (RelativeLayout) this.findViewById(R.id.freqPlacesButton);
        RelativeLayout goodTypesButton = (RelativeLayout) this.findViewById(R.id.goodTypesButton);
        RelativeLayout myTruckButton = (RelativeLayout) this.findViewById(R.id.myTruckButton);
        RelativeLayout boughtTimeButton = (RelativeLayout) this.findViewById(R.id.boughtTimeButton);
        TextView levelText = (TextView) this.findViewById(R.id.level_name);
        ImageView levelIcon = (ImageView) this.findViewById(R.id.level_star);
        RelativeLayout pointsButton = (RelativeLayout) this.findViewById(R.id.myPointsButton);
        RelativeLayout driverLicenseButton = (RelativeLayout) this.findViewById(R.id.driverLicenseButton);
        RelativeLayout registrationButton = (RelativeLayout) this.findViewById(R.id.registrationButton);
        RelativeLayout licensePlateButton = (RelativeLayout) this.findViewById(R.id.licensePlateButton);
        ImageView homeButton = (ImageView) this.findViewById(R.id.home_btn);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toNext = new Intent(UserProfileActivity.this, ProfilePicActivity.class);
                startActivity(toNext);
            }
        });

        licensePlateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, InformationUpdaterActivity.class);
                toEnterPage.putExtra("title", "车牌号");
                toEnterPage.putExtra("fieldName", "licensePlate");
                startActivity(toEnterPage);
            }
        });

        userIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toNext = new Intent(UserProfileActivity.this, PhoneConfigActivity.class);
                startActivity(toNext);
            }
        });

        goodTypesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView goodTypes = (TextView) UserProfileActivity.this.findViewById(R.id.goodstype_after_edit);
                Intent toEnterPage = new Intent(UserProfileActivity.this, GoodTypeActivity.class);
                toEnterPage.putExtra("initValue", goodTypes.getText().toString());
                startActivity(toEnterPage);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.this.finish();
            }
        });

        frequentPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, FrequentPlaceActivity.class);
                toEnterPage.putExtra("freqPlaces", freqPlaces);
                startActivity(toEnterPage);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = UserProfileActivity.this.getString(R.string.login_cookie_storage_file_name);
                File cookieFile = new File(UserProfileActivity.this.getApplicationContext().getFilesDir(), filename);
                cookieFile.delete();
                finish();
                System.exit(0);
            }
        });

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toNext = new Intent(UserProfileActivity.this, ReferActivity.class);
                startActivity(toNext);
            }
        });


        userNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, InformationUpdaterActivity.class);
                toEnterPage.putExtra("title", "真实姓名");
                toEnterPage.putExtra("fieldName", "userName");
                startActivity(toEnterPage);
            }
        });
        nickNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, InformationUpdaterActivity.class);
                toEnterPage.putExtra("title", "昵称");
                toEnterPage.putExtra("fieldName", "nickName");
                startActivity(toEnterPage);
            }
        });

        homeTownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, HomeTownActivity.class);
                startActivity(toEnterPage);
            }
        });

        signatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, InformationUpdaterActivity.class);
                toEnterPage.putExtra("title", "个性签名");
                toEnterPage.putExtra("fieldName", "signature");
                startActivity(toEnterPage);
            }
        });

        myTruckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, MyTruckActivity.class);
                startActivity(toEnterPage);
            }
        });

        boughtTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, PurchaseTimeActivity.class);
                startActivity(toEnterPage);
            }
        });

        driverLicenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, TwoPhotoActivity.class);
                toEnterPage.putExtra("title", "驾驶证");
                toEnterPage.putExtra("fieldName", "driverLicensePic");
                toEnterPage.putExtra("initValue", driverLicensePic);
                startActivity(toEnterPage);
            }
        });
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnterPage = new Intent(UserProfileActivity.this, TwoPhotoActivity.class);
                toEnterPage.putExtra("title", "行驶证");
                toEnterPage.putExtra("fieldName", "registrationPic");
                toEnterPage.putExtra("initValue", registrationPic);
                startActivity(toEnterPage);
            }
        });


        levelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLevelPage = new Intent(UserProfileActivity.this, LevelDetailActivity.class);
                startActivity(toLevelPage);
            }
        });

        levelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLevelPage = new Intent(UserProfileActivity.this, LevelDetailActivity.class);
                startActivity(toLevelPage);
            }
        });

        pointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPointsPage = new Intent(UserProfileActivity.this, PointsDetailActivity.class);
                toPointsPage.putExtra("points", points);
                startActivity(toPointsPage);
            }
        });

        refresh();
    }

    private void refresh(){
        showDialog();
        new PersonalInfoTask().execute(CookieManager.getInstance().getCookie());
    }

    @Override
    protected void onStart(){
        super.onStart();
        this.overridePendingTransition(R.anim.slide_left_to_right,
                R.anim.slide_right_to_left);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        progDialog.dismiss();
        progDialog.onDetachedFromWindow();
        progDialog = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
    }

    public void showDialog() {
        if(progDialog != null) {
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setIndeterminate(false);
            progDialog.setCancelable(true);
            progDialog.setMessage("正在获取个人信息");
            progDialog.show();
        }
    }

    public void renderImage(String filename) {
        try {
            profilePic.setImageURI(Uri.parse(filename));
        } catch (Exception e) {
            Log.e("UserProfilePage", "Error rendering image for " + filename);
            return;
        }
    }

    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void reload(final PersonalInfo data){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView userBefore = (TextView) UserProfileActivity.this.findViewById(R.id.phone_before_edit);
                TextView userAfter = (TextView) UserProfileActivity.this.findViewById(R.id.phone_after_edit);
                userBefore.setText("");
                userAfter.setText(CookieManager.getInstance().getUserID());

                TextView nickNameBefore = (TextView) UserProfileActivity.this.findViewById(R.id.nickname_before_edit);
                TextView nickNameAfter = (TextView) UserProfileActivity.this.findViewById(R.id.nickname_after_edit);
                if(!data.getNickName().equals(GlobalVar.NOT_SET)){
                    nickNameAfter.setText(data.getNickName());
                    nickNameBefore.setText("");
                }

                TextView userNameBefore = (TextView) UserProfileActivity.this.findViewById(R.id.name_before_edit);
                TextView userNameAfter = (TextView) UserProfileActivity.this.findViewById(R.id.name_after_edit);
                if(!data.getUserName().equals(GlobalVar.NOT_SET)){
                    userNameAfter.setText(data.getUserName());
                    userNameBefore.setText("");
                }

                TextView signatureBefore = (TextView) UserProfileActivity.this.findViewById(R.id.signaure_before_edit);
                TextView signatureAfter = (TextView) UserProfileActivity.this.findViewById(R.id.signaure_after_edit);
                if(!data.getSignature().equals(GlobalVar.NOT_SET)){
                    signatureAfter.setText(data.getSignature());
                    signatureBefore.setText("");
                }

                if(!data.getPortrait().equals(GlobalVar.NOT_SET)){
                    DisplayImageUtil.fetchImage(UserProfileActivity.this,
                            UserProfileActivity.this,
                            CookieManager.getInstance().getUserID(),
                            new String[]{data.getPortrait()});
                }

//                ImageView level = (ImageView) UserProfileActivity.this.findViewById(R.id.level_icon);
//                if(data.getPrivilege().equals("basic")){
//                    level.setImageResource(R.drawable.level_rookie);
//                } else if(data.getPrivilege().equals("60pc")){
//                    level.setImageResource(R.drawable.level_middle);
//                } else {
//                    level.setImageResource(R.drawable.level_high);
//                }

                TextView homeTownBefore = (TextView) UserProfileActivity.this.findViewById(R.id.hometown_before_edit);
                TextView homeTownAfter = (TextView) UserProfileActivity.this.findViewById(R.id.hometown_after_edit);
                if(!data.getHomeTown().equals(GlobalVar.NOT_SET)){
                    homeTownAfter.setText(data.getHomeTown());
                    homeTownBefore.setText("");
                }

                TextView frequentPlacesAfterEdit = (TextView) UserProfileActivity.this.findViewById(R.id.frequentplace_after_edit);
                TextView frequentPlacesBeforeEdit = (TextView) UserProfileActivity.this.findViewById(R.id.frequentplace_before_edit);
                String places = data.getFrequentPlaces().toString();
                freqPlaces = places.substring(1, places.length() - 1).replaceAll(" ", "");
                if(data.getFrequentPlaces().size()>0) {
                    frequentPlacesAfterEdit.setText(places.substring(1, places.length() - 1).replaceAll(" ", "").replaceAll(",", "\n"));
                    frequentPlacesBeforeEdit.setText("");
                }

                TextView goodTypesAfterEdit = (TextView) UserProfileActivity.this.findViewById(R.id.goodstype_after_edit);
                TextView goodTypesBeforeEdit = (TextView) UserProfileActivity.this.findViewById(R.id.goodstype_before_edit);
                String types = data.getGoodTypes().toString();
                if(data.getGoodTypes().size()>0) {
                    goodTypesAfterEdit.setText(types.substring(1, types.length() - 1).replaceAll(" ", ""));
                    goodTypesBeforeEdit.setText("");
                }

                TextView driverLicenseBefore = (TextView) UserProfileActivity.this.findViewById(R.id.drivinglicense_before_edit);
                RelativeLayout driverLicenseAfter = (RelativeLayout) UserProfileActivity.this.findViewById(R.id.drivinglicense_uploaded_layout);
                if(data.getDriverLicensePic()!=null) {
                    driverLicenseBefore.setVisibility(View.INVISIBLE);
                    driverLicenseAfter.setVisibility(View.VISIBLE);
                }

                TextView registrationBefore = (TextView) UserProfileActivity.this.findViewById(R.id.registration_before_edit);
                RelativeLayout registrationAfter = (RelativeLayout) UserProfileActivity.this.findViewById(R.id.registration_uploaded_layout);
                if(data.getRegistrationPic()!=null) {
                    registrationBefore.setVisibility(View.INVISIBLE);
                    registrationAfter.setVisibility(View.VISIBLE);
                }

                TextView myTruckAfter = (TextView) UserProfileActivity.this.findViewById(R.id.mytruck_after_edit);
                TextView myTruckBefore = (TextView) UserProfileActivity.this.findViewById(R.id.mytruck_before_edit);
                if(!data.getMyTruck().equals(GlobalVar.NOT_SET)){
                    myTruckAfter.setText(data.getMyTruck());
                    myTruckBefore.setText("");
                }

                TextView boughtTimeAfter = (TextView) UserProfileActivity.this.findViewById(R.id.boughttime_after_edit);
                TextView boughtTimeBefore = (TextView) UserProfileActivity.this.findViewById(R.id.boughttime_before_edit);
                if(!data.getBoughtTime().equals(GlobalVar.NOT_SET)){
                    boughtTimeAfter.setText(data.getBoughtTime());
                    boughtTimeBefore.setText("");
                }

                TextView pointsField = (TextView) UserProfileActivity.this.findViewById(R.id.point_after_edit);
                pointsField.setText("总积分：" + data.getPoints());
                points = Integer.parseInt(data.getPoints());

                driverLicensePic = data.getDriverLicensePic();
                registrationPic = data.getRegistrationPic();

                TextView licensePlateBefore = (TextView) UserProfileActivity.this.findViewById(R.id.license_plate_before_edit);
                TextView licensePlateAfter = (TextView) UserProfileActivity.this.findViewById(R.id.license_plate_after_edit);
                if(!data.getLicensePlate().equals(GlobalVar.NOT_SET)){
                    licensePlateAfter.setText(data.getLicensePlate());
                    licensePlateBefore.setText("");
                }

                dismissDialog();
            }
        });
    }

    /**
     * Refresh personal information
     */
    private class PersonalInfoTask extends AsyncTask<String, Void, PersonalInfo> {

        @Override
        protected PersonalInfo doInBackground(String... info) {
            final String url = UserProfileActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/getPersonalInfoRequest.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}",
                        PersonalInfo.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(PersonalInfo data) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.server_err), Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                reload(data);
            }
        }
    }
}
