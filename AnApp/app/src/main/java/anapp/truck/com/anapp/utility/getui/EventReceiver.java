package anapp.truck.com.anapp.utility.getui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.igexin.sdk.PushConsts;

import java.util.Random;
import java.util.UUID;

import anapp.truck.com.anapp.activities.EventTableActivity;
import anapp.truck.com.anapp.R;

/**
 * Created by LaIMaginE on 3/12/2015.
 */
public class EventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Bundle bundle = intent.getExtras();
//        Log.d("EventReceiver", "onReceive() action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传（payload）数据
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null)
                {
                    String data = new String(payload);
//                    Log.d("GetuiSdkDemo", "Got Payload:" + data);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.push);

                    if(data.equals("newSOS")){
                        mBuilder = mBuilder.setContentTitle("卡车兄弟--SOS")
                                           .setContentText("亲，您收到了一条兄弟的求助！");
                    } else {
                        mBuilder = mBuilder.setContentTitle("卡车兄弟--路况")
                                           .setContentText("亲，您有一条新路况！");
                    }

                    Intent notificationIntent = new Intent(context, EventTableActivity.class);

                    PendingIntent contentIntent = PendingIntent.getActivity(
                            context, 0, notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(contentIntent);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setLights(Color.RED, 500, 500);
                    long[] pattern = {500,500,500,500,500,500,500,500,500};
                    mBuilder.setVibrate(pattern);
                    mBuilder.setStyle(new NotificationCompat.InboxStyle());

                    Random r = new Random();
                    if(data.equals("newSOS")){
                        mBuilder.setSound(Uri.parse("android.resource://"
                                + context.getPackageName() + "/" + R.raw.sos));
                    } else {
                        mBuilder.setSound(Uri.parse("android.resource://"
                                + context.getPackageName() + "/" + ((r.nextInt() > 0.5) ? R.raw.event : R.raw.event2)));
                    }

                    NotificationManager mNotificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(UUID.randomUUID().hashCode(), mBuilder.build());
                }
                break;
            //添加其他case
            //.........
            default:
                break;
        }
    }
}
