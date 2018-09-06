package com.truckapp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

public class SmackTrialUtil {

	protected static final String userName = "15871783106";
	protected static final String password = "chuangkenbtxd1";
	protected static final String ofDomain = "colab-sbx-170.oit.duke.edu";
	
	public static AbstractXMPPConnection connectToOF() throws SmackException, IOException,
			XMPPException {


		XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
				.builder().setUsernameAndPassword(userName, password)
				.setServiceName(ofDomain)
				.setHost(ofDomain).setPort(5222)
				.setSecurityMode(SecurityMode.disabled).build();

		AbstractXMPPConnection conn2 = new XMPPTCPConnection(config);

		conn2.connect();
		System.out.println("Connectd: " + conn2.isConnected());
		conn2.login();
		System.out.println("Logged in as " + userName);

		return conn2;
//		try {
//			createGroupChatRoom(conn2);
//			testSendMessageToGroupChat(conn2);
//		} finally {
//			conn2.disconnect();
//		}
	}

	public static void testCreateAcct(AbstractXMPPConnection conn2) throws NoResponseException, XMPPErrorException, NotConnectedException{
		
		AccountManager.sensitiveOperationOverInsecureConnectionDefault(true);
		AccountManager.getInstance(conn2).createAccount("18196628250", Constants.CHAT_PW);
		System.out.println("Account created.");
		
	}
	
	public static void testSendMessageToPerson(AbstractXMPPConnection conn2)
			throws NotConnectedException {

		Chat chat = ChatManager.getInstanceFor(conn2).createChat(
				"wangnima@"+ofDomain,
				new ChatMessageListener() {
					@Override
					public void processMessage(Chat arg0, Message arg1) {
						System.out.println("Received msg: " + arg1);
					}
				});
		
		Scanner in = new Scanner(System.in);
		try {
			while (true) {
				System.out.println("enter next msg:");
				String msg = in.nextLine();
				chat.sendMessage(msg);
				System.out.println(msg + " sent");
			}
		} finally {
			in.close();
		}
	}
	
	public static void createGroupChatRoom(AbstractXMPPConnection conn2, String roomId) throws XMPPErrorException, SmackException{

		// Get the MultiUserChatManager
		MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn2);

		// Create a MultiUserChat using an XMPPConnection for a room
		MultiUserChat muc2 = manager.getMultiUserChat(roomId + "@conference."+ofDomain);

		muc2.createOrJoin(userName);
		muc2.sendConfigurationForm(new Form(DataForm.Type.submit));

	}
	
	public static void testSendMessageToGroupChat(AbstractXMPPConnection conn2) throws XMPPErrorException, SmackException{
		
		// Get the MultiUserChatManager
		MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn2);

		// Create a MultiUserChat using an XMPPConnection for a room
		MultiUserChat muc2 = manager.getMultiUserChat("0000abc@conference."+ofDomain);

		Scanner in = new Scanner(System.in);
		try {
			muc2.createOrJoin(userName);
			muc2.sendConfigurationForm(new Form(DataForm.Type.submit));
//			muc2.changeSubject("heihei");
			
//			muc2.grantMembership("18196628250@"+ofDomain+"/Ressource");
//			muc2.nextMessage();
			muc2.addMessageListener(new MessageListener(){
				@Override
				public void processMessage(Message arg0) {
					if(arg0.getFrom().contains("/"))
						System.out.println("Received msg: (from " + arg0.getFrom().split("/")[1] + ")" + arg0.getBody());
				}
			});
			System.out.println("entered chat room...");
			while (true) {
				System.out.println("enter next msg:");
				String msg = in.nextLine();
				muc2.sendMessage(msg);
				System.out.println(msg + " sent");
			}
		} finally {
			in.close();
		}
	}

}
