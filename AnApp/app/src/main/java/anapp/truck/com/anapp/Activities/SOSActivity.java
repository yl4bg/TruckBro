package anapp.truck.com.anapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.audio.AudioRecordUtil;
import anapp.truck.com.anapp.utility.image.UploadImageUtil;
import anapp.truck.com.anapp.utility.image.SmartBitmapDecoder;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSManager;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSUploadDelegate;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.location.GPSTracker;


public class SOSActivity extends DefaultActivity
        implements AliOSSUploadDelegate, GeocodeSearch.OnGeocodeSearchListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int UPLOAD_FORM_GALLERY_REQUEST_CODE = 101;

    private Uri fileUri;
    private int taken = 0;
    private ArrayList<Uri> allPhotoUris = new ArrayList<Uri>();
    private ArrayList<String> successUUIDs = new ArrayList<String>();

    private TextView addressText;

    private String textDescription;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;

    private String province;
    private String city;
    private String district;
    private String roadNum = "";

    private GeocodeSearch geocoderSearch;

    private String mFileName;
    private int duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsos_layout);

        this.img1 = (ImageView) findViewById(R.id.report_img_1);
        this.img2 = (ImageView) findViewById(R.id.report_img_2);
        this.img3 = (ImageView) findViewById(R.id.report_img_3);

        final EditText textField = (EditText) findViewById(R.id.descriptionText);
        addressText = (TextView) findViewById(R.id.addressText);

        final RelativeLayout uploadPhotoButton = (RelativeLayout) findViewById(R.id.take_photo_button);
        final RelativeLayout uploadFromAlbumButton = (RelativeLayout) findViewById(R.id.upload_photo_button);
        final ImageButton submitButton = (ImageButton) findViewById(R.id.send_sos_button);
        final TextView cancelButton = (TextView) findViewById(R.id.sos_cancel_button);

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taken == 3) {
                    Toast.makeText(getApplicationContext(), R.string.max3, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = UploadImageUtil.getOutputMediaFileUri(SOSActivity.this.getContentResolver());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        uploadFromAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        UPLOAD_FORM_GALLERY_REQUEST_CODE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.SOS), Toast.LENGTH_SHORT).show();
                textDescription = textField.getText().toString();
                try {
                    UploadImageUtil.uploadPhotoToAliServer(allPhotoUris,
                            SOSActivity.this,
                            SOSActivity.this);
                } catch (FileNotFoundException e){
                    Log.e("Upload to OSS failed", e.getMessage());
                }
                uploadNewSOS();
                SOSActivity.this.finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SOSActivity.this.finish();
            }
        });

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        getAddress(GPSTracker.getInstance().getLatLng());


        final ImageView voiceAnim = (ImageView) findViewById(R.id.record_voice_ani);
        final ImageView recordVoiceButton = (ImageView) findViewById(R.id.sos_microphone);

        recordVoiceButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // play animation
                voiceAnim.setBackgroundResource(R.drawable.animation_speaking_big);
                AnimationDrawable recordingAnimation = (AnimationDrawable) voiceAnim.getBackground();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mFileName = AudioRecordUtil.AUDIO_FILE_PREFIX + UUID.randomUUID().toString();
                    AudioRecordUtil.getInstance().onRecord(true, mFileName);
                    AudioRecordUtil.getInstance().startShowTimer((TextView) findViewById(R.id.timer));
                    recordingAnimation.start();
                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                    recordingAnimation.stop();
                    recordingAnimation.selectDrawable(4);
                    AudioRecordUtil.getInstance().onRecord(false, null);
                    duration = AudioRecordUtil.getInstance().getRecordingDuraiton();
                    if (duration >= 1) {
                        AliOSSManager.uploadAudioFile(
                                AudioRecordUtil.formatFullFilePath(mFileName),
                                mFileName,
                                SOSActivity.this);
                        AudioRecordUtil.getInstance().stopShowTimer();
                    } else {
                        ToastUtil.show(SOSActivity.this, "语音时间过短");
                    }

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("fileUri", fileUri);
        savedInstanceState.putInt("taken", taken);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        fileUri = (Uri) savedInstanceState.get("fileUri");
        taken = savedInstanceState.getInt("taken");
    }

    @Override
    protected void onDestroy(){
        if(img1 != null && img1.getDrawable()!=null) {
//            ((BitmapDrawable) img1.getDrawable()).getBitmap().recycle();
            img1.destroyDrawingCache();
            img1.setImageBitmap(null);
        }
        if(img2 != null && img2.getDrawable()!=null) {
//            ((BitmapDrawable) img2.getDrawable()).getBitmap().recycle();
            img2.destroyDrawingCache();
            img2.setImageBitmap(null);
        }
        if(img3!= null && img3.getDrawable()!=null) {
//            ((BitmapDrawable) img3.getDrawable()).getBitmap().recycle();
            img3.destroyDrawingCache();
            img3.setImageBitmap(null);
        }
        super.onDestroy();
    }

    private void uploadNewSOS() {
        // start async task to upload event type, description, and list of photoIDs to our server
        // photo IDs are returned by Ali server
        ArrayList<String> photoUUIDs = this.successUUIDs;
        String picStr = photoUUIDs.toString();
        picStr = picStr.substring(1, picStr.length() - 1);

        if(duration >= 1) {
            new SOSTask().execute(CookieManager.getInstance().getCookie(),
                    this.textDescription + GlobalVar.CHAT_AUDIO_INDICATOR + mFileName, picStr,
                    province, city, district, roadNum);
        } else {
            new SOSTask().execute(CookieManager.getInstance().getCookie(),
                    this.textDescription, picStr,
                    province, city, district, roadNum);
        }

    }

    private void showBitmap(Bitmap bitmap){
        if(taken==1) {
            img1.setImageBitmap(bitmap);
        } else if(taken==2) {
            img2.setImageBitmap(bitmap);
        } else if(taken==3) {
            img3.setImageBitmap(bitmap);
        } else {
            throw new RuntimeException("Err... taken is not 1, 2, or 3...");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bitmap bitmap = null;
                try {
                    bitmap = UploadImageUtil.getBitmapWithUri(this, fileUri);
                    this.allPhotoUris.add(fileUri);
                    taken++;
                } catch (FileNotFoundException e){
                    Toast.makeText(this.getApplicationContext(), "拍照出错", Toast.LENGTH_SHORT).show();
                    return;
                }

                showBitmap(bitmap);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this.getApplicationContext(), "拍照取消", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getApplicationContext(), "拍照出错", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == UPLOAD_FORM_GALLERY_REQUEST_CODE){

            if (resultCode == RESULT_OK) {

                Uri selectedImage = data.getData();

                Bitmap bitmap = null;
                try {
                    bitmap = SmartBitmapDecoder.decodeSampledBitmapFromResource(
                            this, selectedImage,
                            GlobalVar.PREVIEW_WIDTH, GlobalVar.PREVIEW_HEIGHT);
                    this.allPhotoUris.add(selectedImage);
                    taken++;
                } catch (Exception e){
                    Toast.makeText(this.getApplicationContext(), "上传照片出错", Toast.LENGTH_SHORT).show();
                    return;
                }

                showBitmap(bitmap);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this.getApplicationContext(), "上传照片取消", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getApplicationContext(), "上传照片出错", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            System.out.println("Err... there are some error "+requestCode);
        }
    }

    public void uploadComplete(String uuid) {
        this.successUUIDs.add(uuid);
    }
    public void uploadAudioComplete(String uuid) {}

    /**
     * SOS button on-click async task: set help request to server
     */
    private class SOSTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... sosInfo) {
            final String url = SOSActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/HelpRequest.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = GPSTracker.getInstance().toLocationInfoMap();
            urlArgs.put("cookie", sosInfo[0]);
            urlArgs.put("text", sosInfo[1]);
            urlArgs.put("picIDs", sosInfo[2]);
            urlArgs.put("province", sosInfo[3]);
            urlArgs.put("city", sosInfo[4]);
            urlArgs.put("district", sosInfo[5]);
            urlArgs.put("roadNum", sosInfo[6]);
            try {
                return restTemplate.getForObject(
                        url + "?longitude={longitude}&latitude={latitude}&cookie={cookie}"
                                + "&text={text}&picIDs={picIDs}" +
                                "&province={province}&city={city}&district={district}&roadNum={roadNum}",
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
            Intent toNext = new Intent(SOSActivity.this, SOSCancelActivity.class);
            toNext.putExtra("num", msg.getText());
            startActivity(toNext);
            SOSActivity.this.finish();
        }
    }

    public void getAddress(final LatLng latLng) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(
                new LatLonPoint(latLng.latitude, latLng.longitude),
                500,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {}

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 0) {
            province = result.getRegeocodeAddress().getProvince();
            city = result.getRegeocodeAddress().getCity();
            district = result.getRegeocodeAddress().getDistrict();

            if(result.getRegeocodeAddress().getRoads().size()>0){
                roadNum = result.getRegeocodeAddress().getRoads().get(0).getName();
            }

            addressText.setText(province + (city.equals("")?"":",") + city +
                    (district.equals("")?"":",") + district);

            if(addressText.getText().equals("")){
                addressText.setText("未知地点");
            }


        } else {
            ToastUtil.show(getApplicationContext(),
                    getString(R.string.server_err) + rCode);
        }
    }

}
