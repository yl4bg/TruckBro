package anapp.truck.com.anapp.utility.smack;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import anapp.truck.com.anapp.activities.ChatDetailActivity;
import anapp.truck.com.anapp.chatDataClasses.ChatMessage;
import anapp.truck.com.anapp.chatDataClasses.ChatData;
import anapp.truck.com.anapp.chatDataClasses.ChatUser;
import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.rest.ReceivedChatList;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.ForegroundChecker;
import anapp.truck.com.anapp.utility.GlobalVar;
import anapp.truck.com.anapp.utility.ToastUtil;
import anapp.truck.com.anapp.utility.audio.AudioRecordUtil;
import anapp.truck.com.anapp.utility.image.DisplayImageUtil;

/**
 * Created by angli on 6/19/15.
 */
public class SmackUtil {

    private static final String PASSWORD = "chuangkenbtxd1";
    private static final String CHAT_PREFIX = "@conference.";
    private static final String OF_DOMAIN = "colab-sbx-170.oit.duke.edu";
    private static final int OF_PORT = 5222;

    private static final String NO_NAME = "匿名";
    private static final String NO_NEW_MSG = "没有新消息";
    private static final String DEFAULT_UNREAD_COUNT = "0";
    private static final String COLON_WITH_SPACES = " : ";

    // detailed chat page
//    private Button sendButton;
//    private EditText inputText;

    // chat main page
    private AbstractXMPPConnection conn;
    private ChatRelatedActivity mainActivity;

    private List<ChatData> groupDataList = new ArrayList<>();
    private Map<String, ChatData> chatNameToDataMap;
    private String userId;

    // each element for one group chat
    private Map<String, MultiUserChat> mucs = new HashMap<>();
    private Map<String, ArrayList<Message>> unreadMsgLists = new HashMap<>();

    // each element for one detailed chat page
    private Map<String, ChatRefresher> detailActivities = new HashMap<>();
    private Map<String, List<ChatMessage>> chatMsgLists = new HashMap<>();

    private static final SmackUtil instance = new SmackUtil();
    private SmackUtil(){}
    public static SmackUtil getInstance(){ return instance; }

    public void removeDetailChatActivityAndUpdateSetting(String chatName,
                                                         String chatId,
                                                         boolean locked,
                                                         boolean top,
                                                         boolean muted){
        ChatData data = chatNameToDataMap.get(chatName);
        if(data != null) {
            data.setMuted(muted);
            data.setPinned(top);
            data.setLocked(locked);
            mainActivity.refreshUI();
        }
        detailActivities.remove(chatName);
        chatMsgLists.remove(chatName);
        new UpdateChatSettingTask().execute(CookieManager.getInstance().getCookie(),
                chatId, Boolean.toString(locked),
                Boolean.toString(top), Boolean.toString(muted));
    }

    public void removeDrivingModeDetailActivity(String chatName){
        detailActivities.remove(chatName);
        chatMsgLists.remove(chatName);
    }

    public List<ChatData> getGroupDataList(){
        return groupDataList;
    }

    public void connectIfNot(String userId) {

        chatNameToDataMap = new HashMap<>();

        if(conn != null && conn.isConnected() && conn.isAuthenticated())
            return;

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
                .builder().setUsernameAndPassword(userId, PASSWORD)
                .setServiceName(OF_DOMAIN)
                .setHost(OF_DOMAIN).setPort(OF_PORT)
                .setConnectTimeout(30 * 1000)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled).build();

        conn = new XMPPTCPConnection(config);

        try {
            conn.connect();
            conn.setPacketReplyTimeout(30 * 1000);
            Log.i("Smack_Util", "connected");
            if(!conn.isAuthenticated()) {
                conn.login();
                Log.i("Smack_Util", "logged in");
            }
        } catch (Exception e){
            Log.e("Smack_connect", e.getMessage());
        }

    }

    public boolean enterOneChat(ChatRelatedActivity mainActivity,
                                       ReceivedChatList.ReceivedChat chatInfo){

        this.mainActivity = mainActivity;

        if(!conn.isConnected() || !conn.isAuthenticated()){
            ToastUtil.show(mainActivity.getActivity(), "连接聊天服务器出错");
            return false;
        }
        try {
            enterOneGroupChat(chatInfo);
            return true;
        } catch (Exception e){
//            e.printStackTrace();
            Log.e("Smack_chat", "Could not enter group chat with name : " + chatInfo.getChatName()
                    + e.getLocalizedMessage());
            return false;
        }
    }

    public void updateChatActivityAndClearGroupChatList(ChatRelatedActivity mainActivity){
        this.mainActivity = mainActivity;
        this.groupDataList = new ArrayList<>();
    }

    private void enterOneGroupChat(ReceivedChatList.ReceivedChat chatInfo) throws
            SmackException, XMPPException.XMPPErrorException {

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn);
        MultiUserChat currentChat = manager.getMultiUserChat(chatInfo.getChatRoomId() + CHAT_PREFIX + OF_DOMAIN);
        mucs.put(chatInfo.getChatName(), currentChat);

        Message lastMsg = null;
        List<Message> msgList;
        if(unreadMsgLists.containsKey(chatInfo.getChatName()))
            msgList = unreadMsgLists.get(chatInfo.getChatName());
        else {
            unreadMsgLists.put(chatInfo.getChatName(), new ArrayList<Message>());
            msgList = unreadMsgLists.get(chatInfo.getChatName());
        }
        if(msgList.size()>0){
            lastMsg = msgList.get(msgList.size()-1);
            Log.i("last msg", lastMsg.getFrom());
        }

        // TODO: integrate profilePic into ChatData
        ChatData newChatData = new ChatData(
                chatInfo.getChatName(),
                GlobalVar.trimChatDateStr(getDateFromMsg(lastMsg)),
                formatLastTextFromMsg(lastMsg),
                R.drawable.prof1,
                chatInfo.getLocked(),
                chatInfo.getMuted(),
                true,
                chatInfo.getTop(),
                DEFAULT_UNREAD_COUNT,
                chatInfo.getChatRoomId());

        groupDataList.add(newChatData);
        chatNameToDataMap.put(chatInfo.getChatName(), newChatData);

        if(!currentChat.isJoined()) {

            currentChat.createOrJoin(CookieManager.getInstance().getUserID()
                 + GlobalVar.CHAT_SEPARATOR + CookieManager.getInstance().getNickName());
            currentChat.sendConfigurationForm(new Form(DataForm.Type.submit));

            currentChat.addMessageListener(
                    generateListenerForChat(
                            chatInfo.getChatName(),
                            chatInfo.getMuted(),
                            CookieManager.getInstance().getUserID()));
        }

    }

    public void detailedGroupChat(final ChatRefresher detailActivity,
                                  final List<ChatMessage> chatMsgList,
                                  final EditText inputText,
                                  final Button sendButton,
                                  final String chatName,
                                  final String userId){

        this.userId = userId;
        detailActivities.put(chatName, detailActivity);
        chatMsgLists.put(chatName, chatMsgList);

        for(Message m : unreadMsgLists.get(chatName)){
            addItemToListView(m, chatName);
        }

        detailActivity.refreshUI();
        mainActivity.clearUnreadCount(
                groupDataList.indexOf(chatNameToDataMap.get(chatName)));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(inputText.getText().length()==0){
                        ToastUtil.show(v.getContext(), "消息不能为空");
                        return;
                    }
                    Message toSend = mucs.get(chatName).createMessage();
                    toSend.setBody(inputText.getText().toString());
                    toSend.setSubject(
                            formatDateToStr(Calendar.getInstance().getTime())
                    );
                    mucs.get(chatName).sendMessage(toSend);
                    inputText.setText("");
                } catch (SmackException.NotConnectedException e) {
                    Log.e("Chat", "Error sending message: not connected to OpenFire.");
                    return;
                }
            }
        });
    }

    private MessageListener generateListenerForChat(final String chatName, final boolean muted, final String myID){
        final List<Message> listForThisChat = unreadMsgLists.get(chatName);

        return new MessageListener() {
            @Override
            public void processMessage(Message arg0) {
                listForThisChat.add(arg0);

                mainActivity.updateLastestText(
                        getDateFromMsg(arg0),
                        formatLastTextFromMsg(arg0),
                        groupDataList.indexOf(chatNameToDataMap.get(chatName))
                );
                if(detailActivities.get(chatName)!=null){
                    addItemToListView(arg0, chatName);
                    detailActivities.get(chatName).refreshUI();
                } else {
                    mainActivity.upUnreadCount(groupDataList.indexOf(chatNameToDataMap.get(chatName)));
                }

                boolean fromMe = myID.equals(getUserIdFromMsg(arg0));
                if(!muted && !fromMe) {
                    if (ForegroundChecker.isInForeground(mainActivity.getActivity())) {
                        ChatNotifier.getInstance().genSoundAndVibrate();
                    } else {
                        ChatNotifier.getInstance().genNotification(formatLastTextFromMsg(arg0));
                    }
                }
            }
        };
    }

    private void addItemToListView(Message msg, String chatName){

        // TODO: integrate userLevel, userIdentity, profilePic from database
        ChatUser user = new ChatUser(getNameFromMsg(msg)+"（赤脚）", R.drawable.prof1,
                GlobalVar.USER_IDENTITY_DRIVER, GlobalVar.USER_LEVEL_BARE_FOOT,
                getUserIdFromMsg(msg));

        boolean fromMe = userId.equals(getUserIdFromMsg(msg));

        chatMsgLists.get(chatName).add(new ChatMessage(
                getDateFromMsg(msg),
                msg.getBody(), user, fromMe, isAudioMsg(msg)));

        if(isAudioMsg(msg)) {
            String fileName = msg.getBody().split(GlobalVar.CHAT_AUDIO_INDICATOR)[1];
            if(fileName.contains(GlobalVar.CHAT_DURATION_INDICATOR)){
                fileName = fileName.split(GlobalVar.CHAT_DURATION_INDICATOR)[0];
            }
            new DownloadFileTask().execute(
                    mainActivity.getActivity().getString(R.string.alibucket) + fileName,
                    AudioRecordUtil.formatFullFilePath(fileName)
            );
        }

    }

    public void sendAudioMessage(String chatName, String fileName, int duration){
        Message toSend = mucs.get(chatName).createMessage();
        toSend.setBody(GlobalVar.CHAT_AUDIO_INDICATOR + fileName
            + GlobalVar.CHAT_DURATION_INDICATOR + Integer.toString(duration));
        toSend.setSubject(
                formatDateToStr(Calendar.getInstance().getTime())
        );
        try {
            mucs.get(chatName).sendMessage(toSend);
        } catch (SmackException.NotConnectedException e) {
            Log.e("Chat Audio", e.getLocalizedMessage());
        }
    }

    private boolean isAudioMsg(Message msg){
        return msg.getBody().contains(GlobalVar.CHAT_AUDIO_INDICATOR);
    }

    private String formatLastTextFromMsg(Message msg){
        if(msg != null){
            if(isAudioMsg(msg)){
                return getNameFromMsg(msg) + COLON_WITH_SPACES + GlobalVar.THIS_IS_AUDIO;
            } else {
                return getNameFromMsg(msg) + COLON_WITH_SPACES + msg.getBody();
            }
        }
        else
            return NO_NEW_MSG;
    }

    private String getUserIdFromMsg(Message msg){
        String name;
        if (msg.getFrom().contains("/") && msg.getFrom().contains(GlobalVar.CHAT_SEPARATOR)) {
            name = msg.getFrom().split("/")[1].split(GlobalVar.CHAT_SEPARATOR)[0];
        } else {
            name = NO_NAME;
        }
        return name;
    }

    private String getNameFromMsg(Message msg){
        String name;
        if (msg.getFrom().contains("/") && msg.getFrom().contains(GlobalVar.CHAT_SEPARATOR)) {
            name = msg.getFrom().split("/")[1].split(GlobalVar.CHAT_SEPARATOR)[1];
        } else {
            name = NO_NAME;
        }
        return name;
    }

    private String getDateFromMsg(Message msg){
        if(msg != null && msg.getSubject() != null) {
            return msg.getSubject();
        } else {
            return "";
        }
    }

    private String formatDateToStr(Date date){
        return new SimpleDateFormat(GlobalVar.CHAT_DATE_FORMAT, Locale.ENGLISH).format(date);
    }

    private class UpdateChatSettingTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        protected ErrorMsg doInBackground(String... info) {
            try {
                final String url = mainActivity.getActivity().getString(R.string.server_prefix)
                        + "/updateChatSetting.json";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Map<String, String> urlArgs = new HashMap<>();
                urlArgs.put("cookie", info[0]);
                urlArgs.put("chatId", info[1]);
                urlArgs.put("locked", info[2]);
                urlArgs.put("top", info[3]);
                urlArgs.put("muted", info[4]);

                return restTemplate.getForObject(
                        url + "?cookie={cookie}&chatId={chatId}"
                        + "&muted={muted}&top={top}&locked={locked}",
                        ErrorMsg.class, urlArgs);

            } catch (Exception e){
                Log.e("AsyncTask", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg msg) {
            if(msg!=null) {}
            else {
                Toast.makeText(mainActivity.getActivity(),
                        mainActivity.getActivity().getString(R.string.no_connection),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadFileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String fullFilePath = params[1];
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            final File file = new File(fullFilePath);
            if(file.exists()) return fullFilePath;
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                DisplayImageUtil.downloadFileFromUrl(url, fullFilePath);
//                Log.i("Ali download", "Url = " + url + ", filepath = " + fullFilePath);
            } catch (Exception e) {
                Log.e("Audio File Download", e.getMessage());
                return null;
            }
            return fullFilePath;
        }
        @Override
        protected void onPostExecute(String fullFilePath) {
            if(fullFilePath == null) {
                Log.e("Chat Audio", "file download failed for " + fullFilePath);
                return;
            }
//            else {
//                Log.i("Ali download", "Success! filepath = " + fullFilePath);
//            }
        }
    }

}
