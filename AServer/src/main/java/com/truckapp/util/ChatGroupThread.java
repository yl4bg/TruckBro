package com.truckapp.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

public class ChatGroupThread implements Runnable {

	private static final String ofDomain = "colab-sbx-170.oit.duke.edu";
	private static Logger myLogger = Logger.getLogger(ChatUserThread.class
			.getName());

	private String chatId;

	protected static final String userName = "123456";
	protected static final String password = "chuangkenbtxd1";

	public ChatGroupThread(String chatId) {
		this.chatId = chatId;
	}

	@Override
	public void run() {

		XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
				.builder().setUsernameAndPassword(userName, password)
				.setServiceName(ofDomain).setHost(ofDomain).setPort(5222)
				.setConnectTimeout(30 * 1000)
				.setSecurityMode(SecurityMode.disabled).build();

		AbstractXMPPConnection conn2 = new XMPPTCPConnection(config);
		conn2.setPacketReplyTimeout(30 * 1000);

		try {
			conn2.connect();
			conn2.login();
		} catch (Exception e) {
			myLogger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		// System.out.println("Connectd: " + conn2.isConnected());

		try {
			createGroupChatRoom(conn2);
		} catch (Exception e) {
			myLogger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			conn2.disconnect();
		}
	}
	
	private void createGroupChatRoom(AbstractXMPPConnection conn2) throws XMPPErrorException, SmackException{

		// Get the MultiUserChatManager
		MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn2);

		// Create a MultiUserChat using an XMPPConnection for a room
		MultiUserChat muc2 = manager.getMultiUserChat(chatId + "@conference." + ofDomain);

		muc2.create(userName);
		muc2.sendConfigurationForm(new Form(DataForm.Type.submit));

	}

}
