package com.truckapp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.truckapp.database.User;
import com.truckapp.database.UserDevice;

public class GeTuiUtil {
	
	private static String appId = "1whUcaecNu7Gcbii3yi3R8";
	private static String appkey = "5LxWBXQ3VL9AIPwMEOhkt9";
	private static String master = "jvbbULI8qn9yWT6YtPwq71";
	
	private static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	private static Logger logger = Logger.getLogger(GeTuiUtil.class.getName());
	
	public static void pushNewSOSToClients(List<User> users) throws Exception{
		pushNewStuffHelper(newSOSTransmissionTemplate(), users);
	}
	
	public static void pushNewEventToClients(List<User> users) throws Exception {
		pushNewStuffHelper(newEventTransmissionTemplate(), users);
	}
	
	private static void pushNewStuffHelper(ITemplate data, List<User> users) throws Exception{
		
		System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");

		final IGtPush push = new IGtPush(host, appkey, master);
		push.connect();
		
		ListMessage message = new ListMessage();

		message.setData(data);

		message.setOffline(true);
		message.setOfflineExpireTime(24 * 1000 * 3600);
		message.setPushNetWorkType(0);

		List<Target> targets = new ArrayList<Target>();
		for(User user : users){
			Target target = new Target();
			target.setAppId(appId);
			if(user.getDevice()==null) continue;
			target.setClientId(user.getDevice().getDeviceID());
			targets.add(target);
		}

		String taskId = push.getContentId(message, "toList_Alias_Push");
		IPushResult ret = push.pushMessageToList(taskId, targets);
		
		logger.info("\nGETUI response: " + ret.getResponse().toString());
		
	}
	
	public static boolean pushMsgToUser(String senderID, String msg, List<String> receiverIDs){
		
		System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");

		final IGtPush push = new IGtPush(host, appkey, master);
		try {
			push.connect();
		} catch (IOException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1);
			return false;
		}

		ListMessage message = new ListMessage();

		try {
			message.setData(newOfficialMsgNotificationTemplate(senderID, msg));
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}

		message.setOffline(true);
		message.setOfflineExpireTime(24 * 1000 * 3600);
		message.setPushNetWorkType(0);

		List<Target> targets = new ArrayList<Target>();
		for(String deviceID : receiverIDs){
			Target target = new Target();
			target.setAppId(appId);
			if(deviceID==null || deviceID.equals("")) continue;
			target.setClientId(deviceID);
			targets.add(target);
		}

		String taskId = push.getContentId(message, "toList_Alias_Push");
		IPushResult ret = push.pushMessageToList(taskId, targets);
		
		logger.info("\nGETUI response: " + ret.getResponse().toString());
		
		return true;
		
	}


	public static NotificationTemplate newOfficialMsgNotificationTemplate(String senderID, String msg)
			throws Exception {
		NotificationTemplate template = new NotificationTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTitle("卡车兄弟--消息");
		template.setText("亲，来自" + senderID + "的消息：" + msg);
		template.setLogo("push.png");
		template.setLogoUrl("");
		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		template.setTransmissionType(2);
		template.setTransmissionContent("penetrating message?");
		return template;
	}

	public static TransmissionTemplate newEventTransmissionTemplate()
			throws Exception {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTransmissionType(2);
		template.setTransmissionContent("newEvent");
		return template;
	}
	
	public static TransmissionTemplate newSOSTransmissionTemplate()
			throws Exception {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTransmissionType(2);
		template.setTransmissionContent("newSOS");
		return template;
	}
//
//	public static LinkTemplate linkTemplateDemo() throws Exception {
//		LinkTemplate template = new LinkTemplate();
//		template.setAppId(appId);
//		template.setAppkey(appkey);
//		template.setTitle("标题");
//		template.setText("内容");
//		template.setLogo("icon.png");
//		template.setLogoUrl("");
//		template.setIsRing(true);
//		template.setIsVibrate(true);
//		template.setIsClearable(true);
//		template.setUrl("http://www.baidu.com");
//		template.setPushInfo("actionLocKey", 1, "message", "sound", "payload",
//				"locKey", "locArgs", "launchImage");
//		return template;
//	}
//	
//	public static NotyPopLoadTemplate NotyPopLoadTemplateDemo() {
//		NotyPopLoadTemplate template = new NotyPopLoadTemplate();
//		template.setAppId(appId);
//		template.setAppkey(appkey);
//		template.setNotyTitle("title");
//		template.setNotyContent("text");
//		template.setNotyIcon("icon.png");
//		template.setBelled(true);
//		template.setVibrationed(true);
//		template.setCleared(true);
//
//		template.setPopTitle("pop");
//		template.setPopContent("popcontent");
//		template.setPopImage("http://www-igexin.qiniudn.com/wp-content/uploads/2013/08/logo_getui1.png");
//		template.setPopButton1("Button1");
//		template.setPopButton2("Button2");
//
//		template.setLoadTitle("poptitle");
//		template.setLoadIcon("file://icon.png");
//		template.setLoadUrl("http://wap.igexin.com/android_download/Gexin_android_2.0.apk");
//		return template;
//	}
}
