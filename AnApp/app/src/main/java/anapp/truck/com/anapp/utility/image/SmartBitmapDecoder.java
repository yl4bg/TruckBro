package anapp.truck.com.anapp.utility.image;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.FileNotFoundException;

/**
 * Created by angli on 6/29/15.
 */
public class SmartBitmapDecoder {

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Context context, Uri theUri,
                                                         int reqWidth, int reqHeight) throws FileNotFoundException {

        AssetFileDescriptor fileDescriptor =
                context.getContentResolver()
                        .openAssetFileDescriptor(theUri, "r");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(), R.id.myimage, options);
        BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
//        int imageHeight = options.outHeight;
//        int imageWidth = options.outWidth;
//        String imageType = options.outMimeType;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
    }

}
