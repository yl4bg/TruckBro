package anapp.truck.com.anapp.utility.image;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.ToastUtil;

/**
 * Created by angli on 6/29/15.
 */
public class DisplayImageUtil {

    public static void fetchImage(Activity activity, ImageRenderer renderer, String uuid, String[] picIDs) {

        String thisEventDir = activity.getFilesDir() + "/pictures/" + uuid + "/";

        ArrayList<String> urls = new ArrayList<String>();
        for(String imgUuid : picIDs) {
            urls.add(activity.getString(R.string.alibucket)+imgUuid);
        }
        downloadAllImages(urls, picIDs, thisEventDir, activity, renderer);
    }

    public static void downloadFileFromUrl(String urlString, String filename)
            throws IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, String> {

        private Activity activity;
        private ImageRenderer renderer;

        public DownloadImageTask(Activity activity, ImageRenderer renderer){
            this.renderer = renderer;
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String fileName = params[1];
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            final File file = new File(fileName);
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                downloadFileFromUrl(url, fileName);
            } catch (Exception e) {
                Log.e("EventDetailPage", e.getMessage());
                return GlobalVar.DOWNLOAD_ERROR + fileName;
            }
            return fileName;
        }
        @Override
        protected void onPostExecute(String fileName) {
            if(fileName.contains(GlobalVar.DOWNLOAD_ERROR)){
                ToastUtil.show(activity, "下载图片失败，请稍后再试");
                File file = new File(fileName.split(GlobalVar.DOWNLOAD_ERROR)[1]);
                if(file.exists()){
                    file.delete();
                }
                return;
            }
            renderer.renderImage(fileName);
        }
    }

    private static void downloadAllImages(List<String> urls, String[] picIDs,
                                          String thisEventDir, Activity activity,
                                          ImageRenderer renderer) {

        for(int i=0; i<urls.size(); i++){
            String fileName = thisEventDir + picIDs[i] + ".png";
            File f = new File(fileName);
            if(f.exists()){
                Log.i("info", "file" + fileName + " exists");
                renderer.renderImage(f.getAbsolutePath());
            } else {
                Log.i("info", "file" + fileName + " needs to be downloaded");
                new DownloadImageTask(activity, renderer).execute(urls.get(i), fileName);
            }
        }
    }
}
