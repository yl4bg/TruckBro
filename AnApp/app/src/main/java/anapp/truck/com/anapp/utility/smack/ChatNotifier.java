package anapp.truck.com.anapp.utility.smack;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import java.util.UUID;
import anapp.truck.com.anapp.activities.MainPageActivity;
import anapp.truck.com.anapp.R;

/**
 * Created by angli on 7/1/15.
 */
public class ChatNotifier {

    private static ChatNotifier instance = new ChatNotifier();

    private Context mainActivity;

    private ChatNotifier(){}

    public void init(Context mainActivity){
        this.mainActivity = mainActivity;
    }

    public static ChatNotifier getInstance(){
        return instance;
    }

    public void genSoundAndVibrate(){

        Vibrator vib = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        MediaPlayer mp = MediaPlayer.create(mainActivity, R.raw.chatnoti);
        vib.vibrate(500);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

    }

    public void genNotification(String msg){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mainActivity)
                        .setSmallIcon(R.drawable.push);

        mBuilder = mBuilder.setContentTitle("卡车兄弟--聊天")
                    .setContentText(msg);

        Intent notificationIntent = new Intent(mainActivity, MainPageActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(
                mainActivity, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setLights(Color.RED, 500, 500);
        long[] pattern = {500};
        mBuilder.setVibrate(pattern);
        mBuilder.setStyle(new NotificationCompat.InboxStyle());

        mBuilder.setSound(Uri.parse("android.resource://"
                    + mainActivity.getPackageName() + "/" + R.raw.chatnoti));

        NotificationManager mNotificationManager =
                (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(UUID.randomUUID().hashCode(), mBuilder.build());

    }

}
