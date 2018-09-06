package anapp.truck.com.anapp.chatDataClasses;

/**
 * Created by Anna on 6/18/15.
 */
public class ChatUser {
    private String name;
    private int profilePic;
    private int identity;
    private int level;
    private String id;

    public ChatUser(String name, int profilePic, int identity, int level, String id) {
        this.name = name;
        this.profilePic = profilePic;
        this.identity = identity;
        this.level = level;
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public int getIdentity() {
        return identity;
    }

    public int getLevel() {
        return level;
    }

    public int getProfilePic() {
        return profilePic;
    }

    public String getName() {
        return name;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePic(int profilePic) {
        this.profilePic = profilePic;
    }

    public void setId(String id){
        this.id = id;
    }
}
