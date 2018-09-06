package com.truckapp.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

public class ChatUserThread implements Runnable {

	private static final String ofDomain = "colab-sbx-170.oit.duke.edu";
	private static Logger myLogger = Logger.getLogger(ChatUserThread.class
			.getName());

	private String userName;

	public ChatUserThread(String userName) {
		this.userName = userName;
	}

	@Override
	public void run() {

		XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
				.builder().setUsernameAndPassword(userName, "")
				.setServiceName(ofDomain).setHost(ofDomain).setPort(5222)
				.setConnectTimeout(30 * 1000)
				.setSecurityMode(SecurityMode.disabled).build();

		AbstractXMPPConnection conn2 = new XMPPTCPConnection(config);
		conn2.setPacketReplyTimeout(30 * 1000);

		try {
			conn2.connect();
		} catch (Exception e) {
			myLogger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		// System.out.println("Connectd: " + conn2.isConnected());

		try {
			createAcct(conn2);
		} catch (Exception e) {
			myLogger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			conn2.disconnect();
		}
	}

	private void createAcct(AbstractXMPPConnection conn2)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {

		AccountManager.sensitiveOperationOverInsecureConnectionDefault(true);
		AccountManager.getInstance(conn2).createAccount(userName,
				Constants.CHAT_PW);
		// System.out.println("Account created.");
	}

}
