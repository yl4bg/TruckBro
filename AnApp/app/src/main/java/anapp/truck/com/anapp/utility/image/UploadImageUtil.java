package anapp.truck.com.anapp.utility.image;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSManager;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSUploadDelegate;

/**
 * Created by angli on 6/29/15.
 */
public class UploadImageUtil {

    public static boolean uploadPhotoToAliServer(List<Uri> allPhotoUris,
                                                 AliOSSUploadDelegate delegate,
                                                 Context context) throws FileNotFoundException {
        // start async task to upload photo to Ali server
        // will get photoID or URL for photos uploaded
        // pass the received list of photoID or URL into uploadToOurServer
        for(Uri photoUri : allPhotoUris) {

            Bitmap bitmap = getBitmapWithUri(context, photoUri);

            String fileName = GlobalVar.IMAGE_FILE_PREFIX + UUID.randomUUID().toString();
            String filePathName = getFilePathFromUri(context.getContentResolver(), photoUri);
            boolean storeSuccess = storeImage(bitmap, filePathName);
            if(!storeSuccess) {
                System.out.println("Storing downsized image failed");
                return false;
            }
            AliOSSManager.uploadFile(filePathName, fileName, delegate);
            delegate.uploadComplete(fileName);
        }
        return true;

    }

    public static boolean storeImage(Bitmap image, String filePathName) {
        try {
            FileOutputStream fos = new FileOutputStream(filePathName);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Bitmap getBitmapWithUri(Context context, Uri picUri) throws FileNotFoundException {
        return SmartBitmapDecoder.decodeSampledBitmapFromResource(
                context, picUri,
                GlobalVar.ALI_UPLOAD_WIDTH, GlobalVar.ALI_UPLOAD_HEIGHT);
    }

    public static Uri getOutputMediaFileUri(ContentResolver resolver){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, generateMediaFileName());
        Uri mCapturedImageURI = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return mCapturedImageURI;
    }

    public static String generateMediaFileName(){
        return GlobalVar.IMAGE_FILE_PREFIX + (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
    }

    public static String getFilePathFromUri(ContentResolver resolver, Uri photoFileUri){
        String[] projection = { MediaStore.Images.Media.DATA};
        Cursor cursor = resolver.query(photoFileUri, projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index_data);
    }
}
