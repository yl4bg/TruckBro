package anapp.truck.com.anapp.chatDataClasses;

/**
 * Created by Anna on 6/18/15.
 */
public class ChatMessage {
    private String timestamp;
    private String message;
    private ChatUser chatUser;
    private boolean fromMe;
    private boolean voice;

    public ChatMessage(String timestamp, String message, ChatUser chatUser, boolean fromMe, boolean voice) {
        this.timestamp = timestamp;
        this.message = message;
        this.chatUser = chatUser;
        this.fromMe = fromMe;
        this.voice = voice;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public ChatUser getChatUser() {
        return chatUser;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public boolean isVoice() {
        return voice;
    }
}
