package anapp.truck.com.anapp.utility.audio;

import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by angli on 6/27/15.
 */
public class AudioRecordUtil {

    private static AudioRecordUtil instance = new AudioRecordUtil();

    private static final String LOG_TAG = "AudioRecordUtil";

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;

    private long startTime;
    private long endTime;
    private CountDownTimer timer;
    private static final long LONGEST_DURATION = 60 * 60 * 1000;

    public static final String AUDIO_FILE_PREFIX = "audio-";
    public static final String FILE_NAME_PREFIX =
            Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/audio-";
    public static final String FILE_NAME_SUFFIX = ".3gp";

    public static String formatFullFilePath(String uuid){
        return FILE_NAME_PREFIX + uuid + FILE_NAME_SUFFIX;
    }

    public int getRecordingDuraiton(){
        return Math.round((endTime - startTime)/(1000*1000*1000));
    }

    public void onRecord(boolean start, String fileName) {
        if (start) {
            startTime = System.nanoTime();
            startRecording(formatFullFilePath(fileName));
        } else {
            endTime = System.nanoTime();
            stopRecording();
        }
    }

    public void onPlay(boolean start, String fileName) {
        if (start) {
            startPlaying(formatFullFilePath(fileName));
        } else {
            stopPlaying();
        }
    }

    private void startPlaying(String mFileName) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mPlayer = null;
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording(String mFileName) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private AudioRecordUtil() {
    }

    public static AudioRecordUtil getInstance(){
        return instance;
    }

    public void startShowTimer(final TextView timerText){
        timer = new CountDownTimer(LONGEST_DURATION, 1000) {

            public void onTick(long millisUntilFinished) {
                timerText.setText(Long.toString((LONGEST_DURATION - millisUntilFinished)/(60*1000)) + ":"
                                + Long.toString(((LONGEST_DURATION - millisUntilFinished)%(60*1000))/1000));
            }

            public void onFinish() {
                timerText.setText("已录" + timerText.getText());
            }
        };
        timer.start();
    }

    public void stopShowTimer(){
        timer.onFinish();
        timer.cancel();
    }
}