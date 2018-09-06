package anapp.truck.com.anapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.AddressConst;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.audio.AudioRecordUtil;
import anapp.truck.com.anapp.utility.image.UploadImageUtil;
import anapp.truck.com.anapp.utility.image.SmartBitmapDecoder;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSManager;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSUploadDelegate;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.location.GPSTracker;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created by ZYL on 3/1/2015.
 */
public class ReportUploadActivity extends DefaultActivity
        implements AliOSSUploadDelegate, GeocodeSearch.OnGeocodeSearchListener{

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int UPLOAD_FORM_GALLERY_REQUEST_CODE = 101;

    private Uri fileUri;
    private int taken = 0;
    private ArrayList<Uri> allPhotoUris = new ArrayList<Uri>();
    private ArrayList<String> successUUIDs = new ArrayList<String>();

    private GeocodeSearch geocoderSearch;

    private boolean wheelScrolled = false;

    private String type;
    private String textDescription;
    private String roadNum = "";
    private String province = "";
    private String city = "";
    private String district = "";
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private TextView addressText;

    private String mFileName;
    private int duration = 1;

    // for counting time elapsed in seconds
    private int timeElapsed = 0;
    private Timer timer;
    private LinkedList<RelativeLayout> viewList = new LinkedList<RelativeLayout>();

    // Wheel scrolled listener
    private OnWheelScrollListener scrolledListener = new OnWheelScrollListener()  {
        @Override
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }
        @Override
        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            updateStatus(wheel.getId());
        }
    };

    // Wheel changed listener
    private final OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
//            Log.d(TAG, "onChanged, wheelScrolled = " + wheelScrolled);
            if (!wheelScrolled) {
                updateStatus(wheel.getId());
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newevent_layout);

        initWheel(R.id.roadTypeChoice, AddressConst.roadTypes);
        initWheel(R.id.roadNumChoice, AddressConst.highwayByProvince[0]);
        this.img1 = (ImageView) findViewById(R.id.report_img_1);
        this.img2 = (ImageView) findViewById(R.id.report_img_2);
        this.img3 = (ImageView) findViewById(R.id.report_img_3);

        ImageView recordVoiceBtn = (ImageView) findViewById(R.id.record_voice_btn);
        timer = new Timer();

        final EditText textField = (EditText) findViewById(R.id.descriptionText);
        addressText = (TextView) findViewById(R.id.addressText);

        RelativeLayout uploadPhotoButton = (RelativeLayout) findViewById(R.id.take_photo_button);
        RelativeLayout uploadFromAlbumButton = (RelativeLayout) findViewById(R.id.upload_photo_button);
        Button submitButton = (Button) findViewById(R.id.submit_button);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taken == 3) {
                    Toast.makeText(getApplicationContext(), R.string.max3, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = UploadImageUtil.getOutputMediaFileUri(ReportUploadActivity.this.getContentResolver());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        uploadFromAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "上传功能将在下次更新时推出", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        UPLOAD_FORM_GALLERY_REQUEST_CODE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportUploadActivity.this.finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == null) {
                    Toast.makeText(ReportUploadActivity.this.getApplicationContext(),
                            "请选择一种路况类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                textDescription = textField.getText().toString();
                roadNum = getRoadNum().replaceAll("不限", "");
                try {
                    UploadImageUtil.uploadPhotoToAliServer(allPhotoUris,
                            ReportUploadActivity.this,
                            ReportUploadActivity.this);
                } catch (FileNotFoundException e) {
                    Log.e("Upload to OSS failed", e.getMessage());
                }
                uploadNewEvent();
                ReportUploadActivity.this.finish();
            }
        });

        final ImageView voiceAnim = (ImageView) findViewById(R.id.record_voice_ani);

        // record voice and play record animation
        recordVoiceBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // play animation
                voiceAnim.setBackgroundResource(R.drawable.animation_list_speaking);
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
                    if(duration >= 1) {
                        AliOSSManager.uploadAudioFile(
                                AudioRecordUtil.formatFullFilePath(mFileName),
                                mFileName,
                                ReportUploadActivity.this);
                        AudioRecordUtil.getInstance().stopShowTimer();
                    } else {
                        ToastUtil.show(ReportUploadActivity.this, "语音时间过短");
                    }

                    return true;
                }
                return false;
            }
        });

        viewList.add((RelativeLayout) findViewById(R.id.check_icon_frame));
        viewList.add((RelativeLayout) findViewById(R.id.construction_icon_frame));
        viewList.add((RelativeLayout) findViewById(R.id.crowded_icon_frame));
        viewList.add((RelativeLayout) findViewById(R.id.limitedflow_icon_frame));
        viewList.add((RelativeLayout) findViewById(R.id.accident_icon_frame));
        viewList.add((RelativeLayout) findViewById(R.id.other_icon_frame));

        for(RelativeLayout view : viewList) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = ReportUploadActivity.this.getString(R.string.event_type_regulation);
                    clearSelection();
                    ((RelativeLayout) v).getChildAt(0).setBackgroundResource(R.drawable.selection_box);

                    switch (v.getId()) {
                        case R.id.check_icon_frame:
                            type = ReportUploadActivity.this.getString(R.string.event_type_regulation);
                            break;
                        case R.id.construction_icon_frame:
                            type = ReportUploadActivity.this.getString(R.string.event_type_construction);
                            break;
                        case R.id.crowded_icon_frame:
                            type = ReportUploadActivity.this.getString(R.string.event_type_trafficjam);
                            break;
                        case R.id.limitedflow_icon_frame:
                            type = ReportUploadActivity.this.getString(R.string.event_type_limitedflow);
                            break;
                        case R.id.accident_icon_frame:
                            type = ReportUploadActivity.this.getString(R.string.event_type_accident);
                            break;
                        case R.id.other_icon_frame:
                            type = ReportUploadActivity.this.getString(R.string.event_type_other);
                            break;
                    }
                }
            });
        }


        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        getAddress(GPSTracker.getInstance().getLatLng());
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

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

    private void uploadNewEvent() {
        // start async task to upload event type, description, and list of photoIDs to our server
        // photo IDs are returned by Ali server
        ArrayList<String> photoUUIDs = this.successUUIDs;
        String picStr = photoUUIDs.toString();
        picStr = picStr.substring(1, picStr.length() - 1).replaceAll(" ", "");

        if(duration >= 1) {
            new ReportTask().execute(CookieManager.getInstance().getCookie(),
                    this.type, this.textDescription + GlobalVar.CHAT_AUDIO_INDICATOR + mFileName,
                    picStr, this.roadNum,
                    this.province, this.city, this.district);
        } else {
            new ReportTask().execute(CookieManager.getInstance().getCookie(),
                    this.type, this.textDescription,
                    picStr, this.roadNum,
                    this.province, this.city, this.district);
        }
    }

    /**
     * send report
     */
    private class ReportTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... reportInfo) {
            final String url = ReportUploadActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/newEventRequest.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("longitude", GPSTracker.getInstance().getLongitude() + "");
            urlArgs.put("latitude", GPSTracker.getInstance().getLatitude() + "");
            urlArgs.put("cookie", reportInfo[0]);
            urlArgs.put("type", reportInfo[1]);
            urlArgs.put("description", reportInfo[2]);
            urlArgs.put("picIDs", reportInfo[3]);
            urlArgs.put("roadNum", reportInfo[4]);
            urlArgs.put("province", reportInfo[5]);
            urlArgs.put("city", reportInfo[6]);
            urlArgs.put("district", reportInfo[7]);

            try {
                return restTemplate.getForObject(
                        url + "?longitude={longitude}&latitude={latitude}&roadNum={roadNum}" +
                                "&eventType={type}&eventInfo={description}&cookie={cookie}&picIDs={picIDs}" +
                                "&province={province}&city={city}&district={district}",
                        ErrorMsg.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg err) {
            if (err == null) {
                Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), err.getText(), Toast.LENGTH_SHORT).show();
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

    private void clearSelection() {
        for(RelativeLayout view:viewList) {
            RelativeLayout innerFrame = (RelativeLayout) view.getChildAt(0);
            innerFrame.setBackgroundColor(Color.TRANSPARENT);
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

            addressText.setText(province + (city.equals("")?"":",") + city +
                    (district.equals("")?"":",") + district);

            if(addressText.getText().equals("")){
                addressText.setText("未知地点");
            }

            updateStatus(R.id.roadTypeChoice);

        } else {
            ToastUtil.show(getApplicationContext(),
                    getString(R.string.server_err) + rCode);
        }
    }

    /**
     * Updates entered PIN status
     */
    private void updateStatus(int wheelId) {
        if(wheelId == R.id.roadTypeChoice){
            if(getWheel(R.id.roadTypeChoice).getCurrentItem() == 0){
                int index = AddressConst.findIndexForHighwayProvince(province);
                if(index>0)
                    changeMenu(R.id.roadNumChoice, AddressConst.highwayByProvince[index]);
            } else {
                int index = AddressConst.findIndexForRoadProvince(province);
                if(index>0)
                    changeMenu(R.id.roadNumChoice, AddressConst.roadsByProvince[index]);
            }
        }
    }

    /**
     * Initializes wheel
     *
     * @param id
     *          the wheel widget Id
     */
    private void initWheel(int id, String[] wheelMenu1) {
        WheelView wheel = (WheelView) findViewById(id);
        wheel.setViewAdapter(new ArrayWheelAdapter<String>(this, wheelMenu1));
        wheel.setVisibleItems(2);
        wheel.setCurrentItem(0);
        wheel.setDrawShadows(false);
        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
    }

    private void changeMenu(int id, String[] wheelMenu1){
        WheelView wheel = (WheelView) findViewById(id);
        wheel.setViewAdapter(new ArrayWheelAdapter<String>(this, wheelMenu1));
        wheel.setCurrentItem(0);
    }


    /**
     * Returns wheel by Id
     *
     * @param id
     *          the wheel Id
     * @return the wheel with passed Id
     */
    private WheelView getWheel(int id) {
        return (WheelView) findViewById(id);
    }

    private String getRoadNum(){
        if(getWheel(R.id.roadTypeChoice).getCurrentItem() == 0){
            int index = AddressConst.findIndexForHighwayProvince(province);
            if(index>-1)
                return AddressConst.highwayByProvince[index][getWheel(R.id.roadNumChoice).getCurrentItem()];
            else
                return "未知道路";
        } else {
            int index = AddressConst.findIndexForRoadProvince(province);
            if(index>-1)
                return AddressConst.roadsByProvince[index][getWheel(R.id.roadNumChoice).getCurrentItem()];
            else
                return "未知道路";

        }
    }
}
