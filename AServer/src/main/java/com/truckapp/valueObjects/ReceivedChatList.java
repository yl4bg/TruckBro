package com.truckapp.valueObjects;

import java.util.List;

import com.truckapp.database.ChatSetting;

public class ReceivedChatList {

    private List<ReceivedChat> chats;

    public List<ReceivedChat> getChats() {
        return chats;
    }
    
    public void setChats(List<ReceivedChat> chats){
    	this.chats = chats;
    }

    public static class ReceivedChat {

        private String chatName;
        private String chatPicId;
        private String chatRoomId;
        private boolean needToCreate;
        private boolean muted;
		private boolean top;
        private boolean locked;
        
        public boolean getMuted() {
			return muted;
		}

		public void setMuted(boolean muted) {
			this.muted = muted;
		}

		public boolean getTop() {
			return top;
		}

		public void setTop(boolean top) {
			this.top = top;
		}

		public boolean getLocked() {
			return locked;
		}

		public void setLocked(boolean locked) {
			this.locked = locked;
		}

        public boolean getNeedToCreate(){
        	return needToCreate;
        }
        
        public void setNeedToCreate(boolean needToCreate){
        	this.needToCreate = needToCreate;
        }
        
        public String getChatName() {
            return chatName;
        }

        public void setChatName(String chatName) {
            this.chatName = chatName;
        }

        public String getChatPicId() {
            return chatPicId;
        }

        public void setChatPicId(String chatPicId) {
            this.chatPicId = chatPicId;
        }

        public String getChatRoomId() {
            return chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

		public void updateSettings(ChatSetting chatSetting) {
			this.muted = chatSetting.getMuted();
			this.top = chatSetting.getTop();
			this.locked = chatSetting.getLocked();
		}
		
		public void defaultSettings(){
			this.muted = false;
			this.top = false;
			this.locked = false;
		}

    }
}