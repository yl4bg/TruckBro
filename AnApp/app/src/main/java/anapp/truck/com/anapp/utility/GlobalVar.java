package anapp.truck.com.anapp.utility;;import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Anna on 6/18/15.
 *
 * This class is used to store global variables that might be used in other classes
 */
public class GlobalVar {

    // misc
    public static final String NOT_SET = "未设置";

    // image related constants
    public static final String DOWNLOAD_ERROR = "AngSaysThisIsDownloadError";
    public static final String IMAGE_FILE_PREFIX = "image-";
    public static final int PROFILE_PIC_WIDTH = 100;
    public static final int PROFILE_PIC_HEIGHT = 100;
    public static final int PREVIEW_WIDTH = 300;
    public static final int PREVIEW_HEIGHT = 533;
    public static final int ALI_UPLOAD_WIDTH = 450;
    public static final int ALI_UPLOAD_HEIGHT = 800;

    // chat related constants
    public static final int USED_IN_BASIC_CHAT_PAGE = 0;
    public static final int USED_IN_DRIVING_CHAT_PAGE = 1;

    public static final int USER_LEVEL_SUPER_HIGH = 5;
    public static final int USER_LEVEL_BARE_FOOT = 0;

    public static final int USER_IDENTITY_DRIVER = 0;
    public static final int USER_IDENTITY_OWNER = 1;
    public static final int USER_IDENTITY_OTHER = 2;

    public static final String SUFFIX_HOMETOWN = "老乡群";
    public static final String SUFFIX_GOODTYPE = "属性群";
    public static final String SUFFIX_LOCATION = "当地群";
    public static final String SUFFIX_SOS = "SOS群";

    public static final String NO_NAME = "匿名";
    public static final String CHAT_SEPARATOR = "--:--";
    public static final String CHAT_AUDIO_INDICATOR = "AngSaysThisIsAnAudioClip";
    public static final String CHAT_DURATION_INDICATOR = "AngSaysThisAudioHasDuration";
    public static final String THIS_IS_AUDIO = "[语音]";

    public static final String CHAT_DATE_FORMAT = "yyyy-MM-dd hh:mm a";
    public static final String CHAT_DATE_TIMEONLY_FORMAT = "hh:mm a";
    public static final String CHAT_DATE_DATEONLY_FORMAT = "yyyy-MM-dd";

    public static String trimChatDateStr(String date){
        try {
            Date toFormat = new SimpleDateFormat(CHAT_DATE_FORMAT, Locale.ENGLISH).parse(date);
            Date curr = Calendar.getInstance().getTime();
            if(toFormat.getDate()==curr.getDate()){
                return new SimpleDateFormat(CHAT_DATE_TIMEONLY_FORMAT, Locale.ENGLISH).format(toFormat);
            } else {
                return new SimpleDateFormat(CHAT_DATE_DATEONLY_FORMAT, Locale.ENGLISH).format(toFormat);
            }
        } catch (ParseException e) {
            Log.e("Global Date Processing", e.getMessage());
            return "";
        }
    }
}
