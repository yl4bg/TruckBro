package anapp.truck.com.anapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSUploadDelegate;
import anapp.truck.com.anapp.utility.image.SmartBitmapDecoder;
import anapp.truck.com.anapp.utility.image.UploadImageUtil;

/**
 * Created by angli on 7/16/15.
 */
public class ProfilePicActivity extends DefaultActivity implements AliOSSUploadDelegate{

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 201;
    private static final int UPLOAD_FORM_GALLERY_REQUEST_CODE = 202;

    private Uri fileUri;
    private ImageView preview1;
    private ImageView preview3;
    private ImageView preview;
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_profile_image);

        final RelativeLayout uploadPhotoButton = (RelativeLayout) findViewById(R.id.take_photo_button);
        final RelativeLayout uploadFromAlbumButton = (RelativeLayout) findViewById(R.id.upload_photo_button);
        final RelativeLayout submitButton = (RelativeLayout) findViewById(R.id.confirm_upload);
        final RelativeLayout cancelButton = (RelativeLayout) findViewById(R.id.cancel_upload);

        preview = (ImageView) findViewById(R.id.preview_pic);
        preview1 = (ImageView) findViewById(R.id.preview1_pic);
        preview3 = (ImageView) findViewById(R.id.preview3_pic);

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = UploadImageUtil.getOutputMediaFileUri(ProfilePicActivity.this.getContentResolver());
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
                if(uuid == null){
                    ToastUtil.show(v.getContext(), "请拍摄或上传头像");
                } else {
                    new ChangeInfoFieldTask().execute(CookieManager.getInstance().getCookie(),
                            "portrait", uuid);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilePicActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy(){
        if(preview != null && preview.getDrawable()!=null) {
//            ((BitmapDrawable) img1.getDrawable()).getBitmap().recycle();
            preview.destroyDrawingCache();
            preview.setImageBitmap(null);
        }
        if(preview1 != null && preview1.getDrawable()!=null) {
//            ((BitmapDrawable) img2.getDrawable()).getBitmap().recycle();
            preview1.destroyDrawingCache();
            preview1.setImageBitmap(null);
        }
        if(preview3!= null && preview3.getDrawable()!=null) {
//            ((BitmapDrawable) img3.getDrawable()).getBitmap().recycle();
            preview3.destroyDrawingCache();
            preview3.setImageBitmap(null);
        }
        super.onDestroy();
    }

    public void uploadComplete(String uuid) {
        this.uuid = uuid;
    }
    public void uploadAudioComplete(String uuid) {}

    private void showBitmap(final Bitmap bitmap){

        List<Uri> allPhotoUris = new ArrayList<>();
        allPhotoUris.add(fileUri);
        try {
            UploadImageUtil.uploadPhotoToAliServer(allPhotoUris,
                    ProfilePicActivity.this,
                    ProfilePicActivity.this);
        } catch (IOException e){
            Log.e("Upload pic failed", e.getLocalizedMessage());
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                preview.setImageBitmap(bitmap);
                preview1.setImageBitmap(bitmap);
                preview3.setImageBitmap(bitmap);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bitmap bitmap = null;
                try {
                    bitmap = UploadImageUtil.getBitmapWithUri(this, fileUri);
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
                    fileUri = selectedImage;
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

    private class ChangeInfoFieldTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... info) {
            final String url = ProfilePicActivity.this.getApplicationContext().getString(R.string.server_prefix)
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
        protected void onPostExecute(ErrorMsg msg) {
            if (msg == null) {
                Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), msg.getText(), Toast.LENGTH_SHORT).show();
            ProfilePicActivity.this.finish();
        }

    }
}
