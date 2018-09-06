package anapp.truck.com.anapp.chatDataClasses;

/**
 * Created by Anna on 6/17/15.
 */
public class ChatData implements Comparable<ChatData>{

    public int compareTo(ChatData other){
        if(this.pinned && !other.isPinned()) return -1;
        else if (other.isPinned() && !this.pinned) return 1;
        else{
            StringBuilder selfSb = new StringBuilder(name);
            StringBuilder otherSb = new StringBuilder(other.getName());
            return selfSb.reverse().toString().compareTo(otherSb.reverse().toString());
        }
    }

    private String unreadCount;
    private String name;
    private String time;
    private String msg;
    private String chatId;
    private int picId;
    private boolean locked;
    private boolean muted;
    private boolean read;
    private boolean pinned;

    public void setChatId(String chatId){
        this.chatId = chatId;
    }

    public String getChatId(){
        return chatId;
    }

    public void setUnreadCount(String unreadCount) { this.unreadCount = unreadCount;}

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public ChatData(String name, String time, String msg, int picId,
                    boolean locked, boolean muted, boolean read, boolean pinned,
                    String unreadCount, String chatId) {
        this.name = name;
        this.time = time;
        this.msg = msg;
        this.picId = picId;
        this.locked = locked;
        this.muted = muted;
        this.read = read;
        this.pinned = pinned;
        this.unreadCount = unreadCount;
        this.chatId = chatId;
    }

    public int getPicId() { return this.picId; }

    public String getMsg() {
        return msg;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isMuted() {
        return muted;
    }

    public boolean isRead() {
        return read;
    }

    public boolean isPinned() {
        return pinned;
    }

    public String getUnreadCount() { return unreadCount; }
}
