package anapp.truck.com.anapp.activities;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.UUID;

import anapp.truck.com.anapp.adapters.ChatBubbleAdapter;
import anapp.truck.com.anapp.chatDataClasses.ChatMessage;
import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSManager;
import anapp.truck.com.anapp.utility.aliOSS.AliOSSUploadDelegate;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.audio.AudioRecordUtil;
import anapp.truck.com.anapp.utility.smack.ChatRefresher;
import anapp.truck.com.anapp.utility.smack.SmackUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 聊天内容页
 */

public class ChatDetailActivity extends ActionBarActivity implements AliOSSUploadDelegate, ChatRefresher{

    private ArrayList<ChatMessage> messageArrayList = new ArrayList<>();

    private AnimationDrawable recordingAnimation;
    private ChatBubbleAdapter adapter;

    private boolean isLocked;
    private boolean isPinned;
    private boolean isMuted;

    private TextView title;
    private ImageView lockView;
    private ImageView pinView;
    private ImageView muteView;

    private ListView mListView;
    private String chatName;
    private String chatId;

    private ImageView toggleVoicePanel;
    private LinearLayout voicePanelLinearLayout;
    private TextView timer;
    private ImageView microphoneImage;
    private TextView pressToSpeak;
    private SlidingUpPanelLayout mSlideLayout;
    private ImageView keyboardSwitch;
    private EditText editText;
    private Button sendButton;

    private String mFileName;
    private int duration;
    private InputMethodManager imm;

    final private String KEYBOARD_CURRENT_TAG = "KEYBOARD";
    final private String VOICE_CURRENT_TAG = "VOICE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        Bundle extras = getIntent().getExtras();
        chatName = extras.getString("chatName");
        chatId = extras.getString("chatId");
        isLocked = extras.getBoolean("locked");
        isPinned = extras.getBoolean("top");
        isMuted = extras.getBoolean("muted");

        // set custom action bar
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View actionBarView = inflater.inflate(R.layout.chat_detail_action_bar, null);
        this.getSupportActionBar().setCustomView(actionBarView);

        // for keyboard
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // listview setup
        mListView = (ListView) findViewById(R.id.chat_detail_listView);
        adapter = new ChatBubbleAdapter(this, messageArrayList, GlobalVar.USED_IN_BASIC_CHAT_PAGE);
        mListView.setAdapter(adapter);

        // 顾名思义 setup一堆其他东西 见下
        setUpActionBar(chatName);
        setUpToggleButton();

        SmackUtil.getInstance().detailedGroupChat(
                ChatDetailActivity.this, messageArrayList,
                (EditText) findViewById(R.id.chat_detail_edittext),
                (Button) findViewById(R.id.chat_detail_send_button),
                chatName, CookieManager.getInstance().getUserID());

    }

    @Override
    protected void onDestroy(){
        SmackUtil.getInstance().removeDetailChatActivityAndUpdateSetting(
                chatName, chatId, isLocked, isPinned, isMuted);
        super.onDestroy();
    }

    public void refreshUI(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(adapter.getCount() - 1);
                mListView.invalidateViews();
            }
        });
    }

    public void uploadComplete(String uuid) {}

    public void uploadAudioComplete(String fileName) {
        SmackUtil.getInstance().sendAudioMessage(
                chatName, fileName, duration);
    }

    /**
     * setup 页面底下那一部分 箭头 弹出 动画之类的
     */
    private void setUpToggleButton() {

        mSlideLayout = (SlidingUpPanelLayout) findViewById(R.id.chat_detail_sliding_layout);
        voicePanelLinearLayout = (LinearLayout) findViewById(R.id.chat_detail_voice_input_panel);
        toggleVoicePanel = (ImageView) findViewById(R.id.chat_detail_up_arrow);
        keyboardSwitch = (ImageView) findViewById(R.id.chat_detail_keyboard_switch);
        keyboardSwitch.setTag(KEYBOARD_CURRENT_TAG);
        mSlideLayout.setTouchEnabled(false);
        editText = (EditText) findViewById(R.id.chat_detail_edittext);
        sendButton = (Button) findViewById(R.id.chat_detail_send_button);

        // 拉开的箭头
        toggleVoicePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSlideLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    toggleVoicePanel.setImageResource(R.drawable.fold_arrow);
                }
                if (mSlideLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    toggleVoicePanel.setImageResource(R.drawable.up_arrow);
                }
            }
        });

        // 换成文字输入
        keyboardSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag check", keyboardSwitch.getTag().toString());
                if (keyboardSwitch.getTag() == KEYBOARD_CURRENT_TAG) {
                    if (mSlideLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        toggleVoicePanel.setImageResource(R.drawable.up_arrow);
                    }
                    keyboardSwitch.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toggleVoicePanel.setVisibility(View.GONE);
                            editText.setVisibility(View.VISIBLE);
                            sendButton.setVisibility(View.VISIBLE);
                            keyboardSwitch.setTag(VOICE_CURRENT_TAG);
                            keyboardSwitch.setImageResource(R.drawable.voice_switch);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        }
                    }, 500);
                }
                else if (keyboardSwitch.getTag() == VOICE_CURRENT_TAG) {
                    toggleVoicePanel.setVisibility(View.VISIBLE);
                    voicePanelLinearLayout.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                    sendButton.setVisibility(View.GONE);
                    keyboardSwitch.setTag(KEYBOARD_CURRENT_TAG);
                    keyboardSwitch.setImageResource(R.drawable.keyboard_switch);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }
        });

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSlideLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    toggleVoicePanel.setImageResource(R.drawable.up_arrow);
                }
                return false;
            }
        });

//        初始化麦克风那一部分
        microphoneImage = (ImageView) findViewById(R.id.chat_detail_microphone_button);
        timer = (TextView) findViewById(R.id.chat_detail_timer_textview);
        pressToSpeak = (TextView) findViewById(R.id.chat_detail_press_to_speak);
//        这个动画我做了drawable 设成了数字那一部分的背景
        recordingAnimation = (AnimationDrawable) timer.getBackground();

        microphoneImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mFileName = AudioRecordUtil.AUDIO_FILE_PREFIX + UUID.randomUUID().toString();
                    AudioRecordUtil.getInstance().onRecord(true, mFileName);
                    AudioRecordUtil.getInstance().startShowTimer(timer);
                    pressToSpeak.setVisibility(View.GONE);
                    timer.setVisibility(View.VISIBLE);
                    // Log.d("check check check check", Boolean.toString(timer.getVisibility() == View.VISIBLE));
                    recordingAnimation.start();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    timer.setVisibility(View.GONE);
                    recordingAnimation.stop();
                    pressToSpeak.setVisibility(View.VISIBLE);

                    AudioRecordUtil.getInstance().onRecord(false, null);
                    AudioRecordUtil.getInstance().stopShowTimer();
                    duration = AudioRecordUtil.getInstance().getRecordingDuraiton();
                    if(duration >= 1) {
                        AliOSSManager.uploadAudioFile(
                                AudioRecordUtil.formatFullFilePath(mFileName),
                                mFileName,
                                ChatDetailActivity.this);
                    } else {
                        ToastUtil.show(ChatDetailActivity.this, "语音时间过短");
                    }

                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 设置右上角那三个按键
     * 注意没有把他们放在菜单里！因为菜单里按键大小都是固定的 然后并排放三个就没有地方放title了
     * 我目前还没有看到在菜单里放三个可见按钮的安卓app
     */
    private void setUpActionBar(String titleText) {
        // set title
        title = (TextView) findViewById(R.id.chat_detail_title);
        title.setText(titleText);

        // set toggle imageviews
        lockView = (ImageView) findViewById(R.id.chat_detail_toggle_lock);
        if(chatName.endsWith(GlobalVar.SUFFIX_LOCATION) || chatName.endsWith(GlobalVar.SUFFIX_SOS)) {
            lockView.setImageResource(isLocked ? R.drawable.locked_true : R.drawable.locked_false);
            lockView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLocked) {
                        lockView.setImageResource(R.drawable.locked_false);
                        isLocked = false;
                    } else {
                        lockView.setImageResource(R.drawable.locked_true);
                        isLocked = true;
                    }
                }
            });
        } else {
            lockView.setVisibility(View.INVISIBLE);
        }

        pinView = (ImageView) findViewById(R.id.chat_detail_toggle_pin);
        pinView.setImageResource(isPinned?R.drawable.pinned_true:R.drawable.pinned_false);
        pinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPinned) {
                    pinView.setImageResource(R.drawable.pinned_false);
                    isPinned = false;
                } else {
                    pinView.setImageResource(R.drawable.pinned_true);
                    isPinned = true;
                }
            }
        });

        muteView = (ImageView) findViewById(R.id.chat_detail_toggle_mute);
        muteView.setImageResource(isMuted?R.drawable.muted_true:R.drawable.muted_false);
        muteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMuted) {
                    muteView.setImageResource(R.drawable.muted_false);
                    isMuted = false;
                } else {
                    muteView.setImageResource(R.drawable.muted_true);
                    isMuted = true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_detail, menu);
        return true;
    }

    /**
     * no use at all
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

//    /**
//     * 这部分也很重要！每个activity都要放 不然字体就不会被改
//     * @param newBase
//     */
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
}
