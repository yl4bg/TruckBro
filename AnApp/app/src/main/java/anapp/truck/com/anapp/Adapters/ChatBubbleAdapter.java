package anapp.truck.com.anapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import anapp.truck.com.anapp.activities.ChatUserProfileActivity;
import anapp.truck.com.anapp.chatDataClasses.ChatMessage;
import anapp.truck.com.anapp.chatDataClasses.ChatUser;
import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.audio.AudioRecordUtil;


/**
 * Created by Anna on 6/18/15.
 * populate chat bubble 用的
 */
public class ChatBubbleAdapter extends BaseAdapter {

    private static final int ONE_MINUTE_IN_MILLIS = 60000;//
    private static final float LONGEST_AUDIO_LENGTH = 300.0f;
    private static final float LONGEST_DURATION = 10.0f;
    private Context mContext;
    private List<ChatMessage> list;

    private View bubbleListView;
    private ImageView userPic;
    private TextView messageView;
    private TextView userName;
    private ImageView voiceMsgIcon;
    private TextView voiceLengthTextView;
    private ImageView voiceUnheard;
    private ImageView userLevelIcon;
    private TextView curTimeTextView;

    private ChatUser curUser;
    private ChatMessage curMessage;
    private int usedIn;

    // use different layouts for messages from different sides
//     TODO 这一部分我写的时候以为api一定要是15 不能设置背景图 所以赤脚和宇宙飞船那两个bubble建了不同的layout
//    如果有朝一日换成api 16了 可以考虑把它们合并
    private static final int VIEW_TYPE_COUNT = 3;
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_HIGH_LEVEL = 2;

    public ChatBubbleAdapter(Context context, List<ChatMessage> list, int usedIn){
        this.mContext = context;
        this.list = list;
        this.usedIn = usedIn;
    }

//    这里要看一下！
    @Override
    public int getViewTypeCount() {
        // we have separate views for each different bubble
        // because the setBackground method is only supported
        // from apk level 16, but our min is 15
        return VIEW_TYPE_COUNT;
    }

    /**
     * 根据user的等级 还有消息是不是我发的 判断该用哪个view
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int viewType = VIEW_TYPE_NORMAL;
        int level = list.get(position).getChatUser().getLevel();
        boolean fromMe = list.get(position).isFromMe();
        if (fromMe)
            viewType = VIEW_TYPE_ME;
        else {
            if (level == GlobalVar.USER_LEVEL_SUPER_HIGH)
                viewType = VIEW_TYPE_HIGH_LEVEL;
            else viewType = VIEW_TYPE_NORMAL;
        }
        return viewType;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private boolean showTimeText(String curTime, String lastTime){
        try {
            Date cur = new SimpleDateFormat(GlobalVar.CHAT_DATE_FORMAT, Locale.ENGLISH).parse(curTime);
            Date last = new SimpleDateFormat(GlobalVar.CHAT_DATE_FORMAT, Locale.ENGLISH).parse(lastTime);
            Date lastPlusTenMin=new Date(last.getTime() + (5 * ONE_MINUTE_IN_MILLIS));
            return lastPlusTenMin.before(cur);
        } catch (ParseException e) {
            Log.e("Parse Chat Date", e.getMessage());
            return false;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        int layoutId = R.layout.chat_bubble_other;
        int viewType = getItemViewType(position);
//       根据get的结果设待会要pop的layout
        switch (viewType) {
            case VIEW_TYPE_ME:
                layoutId = R.layout.chat_bubble_me;
                break;
            case VIEW_TYPE_HIGH_LEVEL:
                layoutId = R.layout.chat_bubble_high_level;
                break;
            case VIEW_TYPE_NORMAL:
                layoutId = R.layout.chat_bubble_other;
                break;
        }

//      pop chat那一块
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bubbleListView = inflater.inflate(layoutId, null);

        userPic = (ImageView) bubbleListView.findViewById(R.id.chat_bubble_user_pic);
        userName = (TextView) bubbleListView.findViewById(R.id.chat_bubble_other_name);
        messageView = (TextView) bubbleListView.findViewById(R.id.chat_bubble_message);
        userLevelIcon = (ImageView) bubbleListView.findViewById(R.id.chat_bubble_level_icon);

        curMessage = list.get(position);
        curUser = curMessage.getChatUser();

        if (usedIn == GlobalVar.USED_IN_DRIVING_CHAT_PAGE) {
            messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        }

        userPic.setImageResource(curUser.getProfilePic());
        final String curUserId = curUser.getId();
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile = new Intent(context, ChatUserProfileActivity.class);
                toProfile.putExtra("userId", curUserId);
                context.startActivity(toProfile);
            }
        });

        userName.setText(curUser.getName());
        messageView.setText(curMessage.getMessage());

        // take care of audio messages
        if(curMessage.isVoice()) {

            // common stuff
            voiceMsgIcon = (ImageView) bubbleListView.findViewById(R.id.chat_detail_voice_indicator_imageview);
            voiceMsgIcon.setVisibility(View.VISIBLE);
            messageView.setText("");
            voiceLengthTextView = (TextView) bubbleListView.findViewById(R.id.chat_bubble_voicemsg_length_textview);
            voiceLengthTextView.setVisibility(View.VISIBLE);

            final AnimationDrawable voiceAnimation = (AnimationDrawable) voiceMsgIcon.getBackground();

            voiceUnheard = (ImageView) bubbleListView.findViewById(R.id.chat_bubble_voicemsg_unplayed_reddot);
            voiceUnheard.setVisibility(View.VISIBLE);

            // set up final fields for listener class
            final ImageView thisVoiceUnheard = voiceUnheard;
            final String fileName = curMessage.getMessage().split(GlobalVar.CHAT_AUDIO_INDICATOR)[1];

            // for backward compatibility only, all msgs sent from new versions should have duration
            if(!fileName.contains(GlobalVar.CHAT_DURATION_INDICATOR)) {

                voiceLengthTextView.setText("--\"");
                messageView.setWidth((int)LONGEST_AUDIO_LENGTH);
                messageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File audioFile = new File(AudioRecordUtil.formatFullFilePath(fileName));
                        if (audioFile.exists()) {
                            AudioRecordUtil.getInstance().onPlay(true, fileName);
                        } else {
                            ToastUtil.show(context,
                                    "语音文件还未被下载，请稍后再试。");
                        }
                        thisVoiceUnheard.setVisibility(View.GONE);
                    }
                });
            }
            // for msgs with duration in msg.getBody()
            else {
                final String newFileName = fileName.split(GlobalVar.CHAT_DURATION_INDICATOR)[0];
                final int duration = Integer.parseInt(fileName.split(GlobalVar.CHAT_DURATION_INDICATOR)[1]);
                final String durationText = Integer.toString(duration);
                final float lengthFraction = duration >= LONGEST_DURATION ? 1.0f : ((float) duration)/LONGEST_DURATION;
                messageView.setWidth((int)(lengthFraction * LONGEST_AUDIO_LENGTH));
                voiceLengthTextView.setText(durationText + "\"");
                messageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File audioFile = new File(AudioRecordUtil.formatFullFilePath(newFileName));
                        if (audioFile.exists()) {
                            AudioRecordUtil.getInstance().onPlay(true, newFileName);
                        } else {
                            ToastUtil.show(context,
                                    "语音文件还未被下载，请稍后再试。");
                        }
                        thisVoiceUnheard.setVisibility(View.GONE);
                        voiceAnimation.start();
                        messageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                voiceAnimation.stop();
                                voiceAnimation.selectDrawable(0); // so that each time it stops at the same frame
                            }
                        }, duration * 1000);
                    }
                });
            }
        }

        if (position == 0 ||
                showTimeText(
                        list.get(position).getTimestamp(),
                        list.get(position-1).getTimestamp())) {
            curTimeTextView = (TextView) bubbleListView.findViewById(R.id.chat_bubble_time);
            curTimeTextView.setText(GlobalVar.trimChatDateStr(curMessage.getTimestamp()));
            curTimeTextView.setVisibility(View.VISIBLE);
        }

        // set username color
        switch (curUser.getLevel()) {
            case GlobalVar.USER_LEVEL_SUPER_HIGH:
                userName.setTextColor(mContext.getResources().getColor(R.color.super_high_level_color));
                userLevelIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.spaceship));
                break;
            case GlobalVar.USER_LEVEL_BARE_FOOT:
                userName.setTextColor(mContext.getResources().getColor(R.color.normal_user_name_color));
                userLevelIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.barefoot));
                break;
            default: //TODO : not sure if this is the default other user color
                    // if you change this remember to set the name color in the switch below
                userName.setTextColor(mContext.getResources().getColor(R.color.other_user_name_color));
                break;
        }

//        这部分本来是想画边框
//        先留在这备份。。

        // draw border (failed)
//        int borderColorId;
//        switch (curUser.getIdentity()) {
//            case GlobalVar.USER_IDENTITY_DRIVER:
//                borderColorId = R.color.black;
//                break;
//            case GlobalVar.USER_IDENTITY_OWNER:
//                borderColorId = R.color.owner_border_color; // then overwrite the name color
//                userName.setTextColor(mContext.getResources().getColor(R.color.owner_user_name_color));
//                break;
//            default:
//                borderColorId = R.color.other_user_border_color;
//                break;
//        }
//
//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), curUser.getProfilePic());
//
//        final int BORDER_WIDTH = 3;
//        final int BORDER_COLOR = mContext.getResources().getColor(borderColorId);
//
//        Bitmap res = Bitmap.createBitmap(bitmap.getWidth() + 2 * BORDER_WIDTH,
//                bitmap.getHeight() + 2 * BORDER_WIDTH,
//                bitmap.getConfig());
//        Canvas c = new Canvas(res);
//        Paint p = new Paint();
//        p.setColor(BORDER_COLOR);
//        c.drawRect(0, 0, res.getWidth(), res.getHeight(), p);
//        p = new Paint(Paint.FILTER_BITMAP_FLAG);
//        c.drawBitmap(bitmap, BORDER_WIDTH, BORDER_WIDTH, p);



        // set the name to be invisible instead of GONE so that the chat bubble can stay
        // at the right position
        if (curMessage.isFromMe()) userName.setVisibility(View.INVISIBLE);

        return bubbleListView;
    }
}
