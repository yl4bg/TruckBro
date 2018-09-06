package anapp.truck.com.anapp.utility.aliOSS;

import android.content.Context;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.aliyun.mbaas.oss.util.OSSToolKit;

/**
 * Created by ZYL on 3/1/2015.
 */


public class AliOSSManager {

    private static AliOSSManager instance = null;
    private static OSSBucket bucket = null;

    private static boolean inited = false;

    // pw in plain text in its best...
    static final String accessKey = "yo5BmXzWsXFbVjPT";
    static final String secretKey = "yuwcYYsM0GyBz1OFjMEIsLi3aHWVdz";

    private static Context context;

    private AliOSSManager(){}

    public static void init(Context appContext){
        if(inited) {
            return;
        }
        context = appContext;
        OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() {
            @Override
            public String generateToken(String httpMethod, String md5, String type, String date, String ossHeaders,
                                        String resource) {
                String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date
                        + "\n" + ossHeaders + resource;

                return OSSToolKit.generateToken(accessKey, secretKey, content);
            }
        });
        OSSClient.setGlobalDefaultACL(AccessControlList.PUBLIC_READ_WRITE);
        OSSClient.setGlobalDefaultHostId("oss-cn-hangzhou.aliyuncs.com");

        OSSClient.setApplicationContext(appContext);
        bucket = new OSSBucket("truckapp");

        inited = true;

    }
    public static AliOSSManager getInstance(){
        if(instance == null) return new AliOSSManager();
        return instance;
    }

    public static void uploadFile(String filePath, String nameInCloud, final AliOSSUploadDelegate delegate) {
        if(!inited) {
            throw new RuntimeException("AliOSS is not inited");
        }
        OSSFile ossFile = new OSSFile(bucket, nameInCloud);
        ossFile.setUploadFilePath(filePath, "jpg");
        ossFile.enableUploadCheckMd5sum();
        ossFile.uploadInBackground(new SaveCallback() {
            public void onSuccess(String objectKey) {
                // delegate.uploadComplete(objectKey);
            }
            public void onProgress(String objectKey, int byteCount, int totalSize) {}
            public void onFailure(String objectKey, OSSException ossException) {
                System.out.println("Uploading failed: "+objectKey);
                ossException.printStackTrace();
            }
        });
    }

    public static void uploadAudioFile(String filePath, String nameInCloud, final AliOSSUploadDelegate delegate) {
        if(!inited) {
            throw new RuntimeException("AliOSS is not inited");
        }
        OSSFile ossFile = new OSSFile(bucket, nameInCloud);
        ossFile.setUploadFilePath(filePath, "3gp");
        ossFile.enableUploadCheckMd5sum();
        ossFile.uploadInBackground(new SaveCallback() {
            public void onSuccess(String objectKey) {
                delegate.uploadAudioComplete(objectKey);
            }
            public void onProgress(String objectKey, int byteCount, int totalSize) {}
            public void onFailure(String objectKey, OSSException ossException) {
                System.out.println("Uploading failed: "+objectKey);
                ossException.printStackTrace();
            }
        });
    }

}
