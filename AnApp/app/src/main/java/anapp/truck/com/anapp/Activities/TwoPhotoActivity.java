package anapp.truck.com.anapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSManager;
import anapp.truck.com.anapp.utility.image.DisplayImageUtil;
import anapp.truck.com.anapp.utility.image.ImageRenderer;
import anapp.truck.com.anapp.utility.image.UploadImageUtil;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSUploadDelegate;

/**
 * Created by angli on 4/6/15.
 */
public class TwoPhotoActivity extends DefaultActivity implements AliOSSUploadDelegate, ImageRenderer {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_IMAGE2_ACTIVITY_REQUEST_CODE = 101;
    private static final int UPLOAD_FORM_GALLERY_REQUEST_CODE = 102;

    private Uri fileUri;
    private boolean taken1 = false;
    private boolean taken2 = false;
    private ArrayList<Uri> allPhotoUris = new ArrayList<Uri>();
    private ArrayList<String> successUUIDs = new ArrayList<String>();

    private String title;
    private String fieldName;
    private String[] picIDs;
    private String uuid;
    private int rendered = 0;
    private ImageView p1;
    private ImageView p2;
    private ImageView p1PlusSign;
    private ImageView p2PlusSign;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.license_layout);

        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        fieldName = bundle.getString("fieldName");

        TextView titleView = (TextView) findViewById(R.id.title_text);
        titleView.setText("认证"+title);
        TextView p1Text = (TextView) findViewById(R.id.p1_text);
        p1Text.setText("点击上传"+title+"主页");
        TextView p2Text = (TextView) findViewById(R.id.p2_text);
        p2Text.setText("点击上传"+title+"附页");

        String initialPic = bundle.getString("initValue");
        if (initialPic!=null && !initialPic.equals("")) {
            picIDs = initialPic.replaceAll(" ","").split(",");
            uuid = UUID.randomUUID().toString();
            DisplayImageUtil.fetchImage(TwoPhotoActivity.this,
                    TwoPhotoActivity.this,
                    uuid, picIDs);
        }

        LinearLayout firstButton = (LinearLayout) findViewById(R.id.take_first_photo);
        LinearLayout secondButton = (LinearLayout) findViewById(R.id.take_second_photo);
        RelativeLayout submitButton = (RelativeLayout) findViewById(R.id.confirm_upload);
        RelativeLayout cancelButton = (RelativeLayout) findViewById(R.id.cancel_upload);

        p1 = (ImageView) findViewById(R.id.p1);
        p2 = (ImageView) findViewById(R.id.p2);
        p1PlusSign = (ImageView) findViewById(R.id.p1_plus_sign);
        p2PlusSign = (ImageView) findViewById(R.id.p2_plus_sign);

        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = UploadImageUtil.getOutputMediaFileUri(TwoPhotoActivity.this.getContentResolver());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = UploadImageUtil.getOutputMediaFileUri(TwoPhotoActivity.this.getContentResolver());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE2_ACTIVITY_REQUEST_CODE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!taken1 || !taken2){
                    Toast.makeText(TwoPhotoActivity.this.getApplicationContext(),
                            "请拍照证件正反面（两张图片）", Toast.LENGTH_SHORT).show();
                } else {
                    AliOSSManager.init(getApplicationContext());
                    try {
                        UploadImageUtil.uploadPhotoToAliServer(allPhotoUris,
                                TwoPhotoActivity.this,
                                TwoPhotoActivity.this);
                    } catch (FileNotFoundException e){
                        Log.e("Upload to OSS failed", e.getMessage());
                    }
                    uploadLicensePicIds();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoPhotoActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy(){
        if(p1!=null && p1.getDrawable()!=null) {
//            ((BitmapDrawable) p1.getDrawable()).getBitmap().recycle();
            p1.destroyDrawingCache();
            p1.setImageBitmap(null);
        }
        if(p2!=null && p2.getDrawable()!=null) {
//            ((BitmapDrawable) p2.getDrawable()).getBitmap().recycle();
            p2.destroyDrawingCache();
            p2.setImageBitmap(null);
        }
        if(p1PlusSign!=null && p1PlusSign.getDrawable()!=null) {
//            ((BitmapDrawable) p1.getDrawable()).getBitmap().recycle();
            p1PlusSign.destroyDrawingCache();
            p1PlusSign.setImageBitmap(null);
        }
        if(p2PlusSign!=null && p2PlusSign.getDrawable()!=null) {
//            ((BitmapDrawable) p2.getDrawable()).getBitmap().recycle();
            p2PlusSign.destroyDrawingCache();
            p2PlusSign.setImageBitmap(null);
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
        savedInstanceState.putBoolean("taken1", taken1);
        savedInstanceState.putBoolean("taken2", taken2);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        fileUri = (Uri) savedInstanceState.get("fileUri");
        taken1 = savedInstanceState.getBoolean("taken1");
        taken2 = savedInstanceState.getBoolean("taken2");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bitmap bitmap = null;
                try {
                    bitmap = UploadImageUtil.getBitmapWithUri(this, fileUri);
                    this.allPhotoUris.add(fileUri);
                    taken1 = true;
                } catch (FileNotFoundException e){
                    Toast.makeText(this.getApplicationContext(), "拍照出错", Toast.LENGTH_SHORT).show();
                    return;
                }
                p1.setVisibility(View.VISIBLE);
                p1.setImageBitmap(bitmap);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this.getApplicationContext(), "拍照取消", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getApplicationContext(), "拍照出错", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAPTURE_IMAGE2_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bitmap bitmap = null;
                try {
                    bitmap = UploadImageUtil.getBitmapWithUri(this, fileUri);
                    this.allPhotoUris.add(fileUri);
                    taken2 = true;
                } catch (FileNotFoundException e){
                    Toast.makeText(this.getApplicationContext(), "拍照出错", Toast.LENGTH_SHORT).show();
                    return;
                }
                p2.setVisibility(View.VISIBLE);
                p2.setImageBitmap(bitmap);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this.getApplicationContext(), "拍照取消", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getApplicationContext(), "拍照出错", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            System.out.println("Err... there are some error "+requestCode);
        }
    }

    private void uploadLicensePicIds() {
        // start async task to upload event type, description, and list of photoIDs to our server
        // photo IDs are returned by Ali server
        ArrayList<String> photoUUIDs = this.successUUIDs;
        String picStr = photoUUIDs.toString();
        picStr = picStr.substring(1, picStr.length() - 1).replaceAll(" ", "");

        new UpdateTask().execute(CookieManager.getInstance().getCookie(),
                this.fieldName, picStr);
    }

    public void uploadComplete(String uuid) {
        this.successUUIDs.add(uuid);
    }
    public void uploadAudioComplete(String uuid) {}

    public void renderImage(String filename) {
        ImageView img = null;
        if(rendered==0) {
            img = p1;
            rendered++;
        } else if(rendered==1) {
            img = p2;
            rendered++;
        } else {
            return;
        }
        try {
            img.setVisibility(View.VISIBLE);
            img.setImageURI(Uri.parse(filename));
        } catch (Exception e) {
            Log.e("TwoPhotoPage", "Error rendering image for " + filename);
            return;
        }
    }

    private class UpdateTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... info) {
            final String url = TwoPhotoActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/changeInfoField.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            urlArgs.put("fieldname", info[1]);
            urlArgs.put("value", info[2]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}&fieldname={fieldname}" +
                                "&value={value}",
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
            TwoPhotoActivity.this.finish();
        }

    }
}
