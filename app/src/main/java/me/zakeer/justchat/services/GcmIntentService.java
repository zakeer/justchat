package me.zakeer.justchat.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import me.zakeer.justchat.FriendsAllListActivity;
import me.zakeer.justchat.FriendsListActivity;
import me.zakeer.justchat.GcmBroadcastReceiver;
import me.zakeer.justchat.GroupListActivity;
import me.zakeer.justchat.R;
import me.zakeer.justchat.adapters.FriendDetailAdapter;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.items.FriendDetailItem;
import me.zakeer.justchat.items.GcmDetailItem;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.CommonUtilities;
import me.zakeer.justchat.utility.Constant;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
	private static final String TAG = "GcmIntentService";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
    public static final String MESSAGETYPE_SINGLE = "1";
    public static final String MESSAGETYPE_GROUP = "2";
    public static final String MESSAGETYPE_REQUEST = "3";
    public static final String MESSAGETYPE_DELIVERY = "4";
    
    private static final int TYPE_NEW_REQUEST = 0;
    private static final int TYPE_ACCEPT_REQUEST = 1;
    private static final int TYPE_CANCEL_REQUEST = 2;
    private static final int TYPE_REJECT_REQUEST = 3;
    private static final int TYPE_UNFRIEND = 4;
    
    public static final String ACTION_ACCEPT = "ACCEPT";
    public static final String ACTION_REJECT = "REJECT";
    
    private int notificationIcon = R.drawable.top_logo_fresh;
	    
    public GcmIntentService() {
        super("GcmIntentService");
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String gcmMessageType = gcm.getMessageType(intent);
        
        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
        	
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
        	
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(gcmMessageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(gcmMessageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString(), "1");
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(gcmMessageType)) {
                // This loop represents the service doing some work.
            	
            	String newMessage = intent.getExtras().getString("message"); //extras.toString();
            	Log.e(TAG, "newMessage : " + newMessage);
            	
            	List<GcmDetailItem> list = new ArrayList<GcmDetailItem>();
            	AsyncResponse asyncResponse = new AsyncResponse(newMessage);
    			if(asyncResponse.ifSuccess())
    			{
    				Log.e(TAG, "Message is delivered to the user.");
    				
    				list = asyncResponse.getGcmDetail();
    				GcmDetailItem gcmDetailItem = list.get(0);
    				Log.e(TAG, "WHO : " + gcmDetailItem.getWho());
    				
    				String messageType = gcmDetailItem.getWho();
    				
    				// whoType = Type of message sent
    				
    				if(Sessions.isLoggedIn(this))
    				{
    					Log.i(TAG, "User is Logged in");
    					
    					if(messageType.equals(MESSAGETYPE_DELIVERY))
        				{        					
        					Log.e(TAG, "Refresh list...");
        					
        					Log.e(TAG, "delivery : "+newMessage);
        					intent = new Intent(CommonUtilities.DELIVERY_MESSAGE_ACTION);
    					    getApplicationContext().sendBroadcast(intent);
    					    
    					    // Donot proceed further.
    					    return;
        				}
        				else
        				{
        					Log.d(TAG, "Send Delivery Report");
        					String messageId = gcmDetailItem.getId();
        					sendDeliveryReport(messageId);
        				}
    					    					
    					if(gcmDetailItem.getWho().equals(MESSAGETYPE_REQUEST))	// FRIEND REQUEST STUFF
    					{
    						Log.e(TAG, "FRIEND REQUEST STUFF");
    						int type = Integer.parseInt(gcmDetailItem.getType()); // To detect what exactly happens
    						
    						intent = new Intent(CommonUtilities.REQUEST_MESSAGE_ACTION);
    					    intent.putExtra(CommonUtilities.EXTRA_MESSAGE, newMessage);
    					    getApplicationContext().sendBroadcast(intent);
    						
    						String name = gcmDetailItem.getName();
    						
    						switch (type) {
							case TYPE_NEW_REQUEST:
								sendRequestNotification("FreshIM", name+" wants to be your friend on FreshIM. "
										, list.get(0).getUserId(), list.get(0).getFriendId());
								break;
								
							case TYPE_ACCEPT_REQUEST:
								sendNotification(name+" accepted your friends request.", MESSAGETYPE_REQUEST);
								
								break;
								
							case TYPE_CANCEL_REQUEST:
								sendNotification(name+" cancelled his friend request. ", MESSAGETYPE_REQUEST);
								break;
								
							case TYPE_REJECT_REQUEST:
								sendNotification(name+" rejected your friend request. ", MESSAGETYPE_REQUEST);
								break;
								
							case TYPE_UNFRIEND:
								sendNotification(name+" unfriended you. ", MESSAGETYPE_REQUEST);
								break;
								
							default:
								break;
							}
    					}
    					else if(gcmDetailItem.getWho().equals(MESSAGETYPE_GROUP))  	// MESSAGE TO A GROUP
    					{
    						Log.e(TAG, "GROUP_MESSAGE");
    						sendGroupMessage(gcmDetailItem.getId());
    					}
    					else if(gcmDetailItem.getWho().equals(MESSAGETYPE_SINGLE))   // MESSAGE TO INDIVIDUAL FRIEND
    					{
    						Log.e(TAG, "SINGLE_MESSAGE");
    						sendSingleMessage(gcmDetailItem.getId());
    					}   
    				}
    				else
    				{
    					Log.e(TAG, "User is Logged OUT. So nothing happens");
    				}
    			}
    			else
    			{
    				Log.e(TAG, "err : "+asyncResponse.getMessage());
    			}
            	
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    
    private void sendDeliveryReport(String messageId) {
    	String filename = getResources().getString(R.string.message_delivery_php);
    	AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(this, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.MESSAGE_ID, messageId);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(messageDeliveryTaskListener);
		asyncLoadVolley.beginTask();		
	}
    
    OnAsyncTaskListener messageDeliveryTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "groupTaskListener mess : "+message);
			
			AsyncResponse asyncResponse = new AsyncResponse(message);
			List<FriendDetailItem> list = new ArrayList<FriendDetailItem>();
			if(asyncResponse.ifSuccess())
			{	
				list = asyncResponse.getFriendDetail();
				String msg = list.get(0).getMessage();
				
				if(Integer.parseInt(list.get(0).getType())==FriendDetailAdapter.TYPE_IMAGE)
					msg = "Image";	
				
				Log.e(TAG, "msg : "+message);
				
			    Intent intent = new Intent(CommonUtilities.GROUP_MESSAGE_ACTION);
			    intent.putExtra(CommonUtilities.EXTRA_MESSAGE, message);
			    getApplicationContext().sendBroadcast(intent);
				
				sendNotification(msg, MESSAGETYPE_GROUP);
			}
			else
			{
				Log.e(TAG, "err : "+asyncResponse.getMessage());
			}
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};

	// Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, String who) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, FriendsListActivity.class), 0);
        
        if(who.equals(MESSAGETYPE_SINGLE))
        {
        	contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, FriendsListActivity.class), 0);
        }
        else if(who.equals(MESSAGETYPE_GROUP))
        {
        	contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, GroupListActivity.class), 0);
        }
        else if(who.equals(MESSAGETYPE_REQUEST))
        {
        	contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, FriendsAllListActivity.class), 0);
        }
        
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(notificationIcon)
        .setContentTitle(getResources().getString(R.string.app_name))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setTicker(msg)
        .setContentText(msg)
        .setAutoCancel(true);
        
        // Play default notification sound
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL);
        
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
        
    private void sendSingleMessage(String id) {
		
    	String filename = getResources().getString(R.string.frienddetailgcm_php);
    	AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(this, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.ID, id);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(singleTaskListener);
		asyncLoadVolley.beginTask();
	}
    
    OnAsyncTaskListener singleTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "singleTaskListener mess : "+message);
			
			AsyncResponse asyncResponse = new AsyncResponse(message);
			List<FriendDetailItem> list = new ArrayList<FriendDetailItem>();
			if(asyncResponse.ifSuccess())
			{	
				list = asyncResponse.getFriendDetail();
				String notificationMessage = list.get(0).getMessage();
				String name = list.get(0).getUserName();
				
				switch (Integer.parseInt(list.get(0).getType())) {
				
				case FriendDetailAdapter.TYPE_TEXT:
					notificationMessage = name+" : "+notificationMessage;
					break;
				
				case FriendDetailAdapter.TYPE_IMAGE:
					notificationMessage = name+" has sent you an Image.";
					break;
					
				case FriendDetailAdapter.TYPE_FILE:
					notificationMessage = name+" has sent you a file.";
					break;
					
				case FriendDetailAdapter.TYPE_MAP:
					notificationMessage = name+" has sent you his Location.";
					break;
				
				case FriendDetailAdapter.TYPE_STICKER:
					notificationMessage = name+" has sent you a Sticker.";
					break;
					
				default:
					break;
				}
				
				Log.e(TAG, "msg : "+message);
				
			    Intent intent = new Intent(CommonUtilities.SINGLE_MESSAGE_ACTION);
			    intent.putExtra(CommonUtilities.EXTRA_MESSAGE, message);
			    getApplicationContext().sendBroadcast(intent);
				
				sendNotification(notificationMessage, MESSAGETYPE_SINGLE);				
			}
			else
			{
				Log.e(TAG, "err : "+asyncResponse.getMessage());
			}
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};
	
	private void sendGroupMessage(String id) {
		
    	String filename = getResources().getString(R.string.frienddetailgcm_php);
    	AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(this, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.ID, id);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(groupTaskListener);
		asyncLoadVolley.beginTask();
	}
	
	OnAsyncTaskListener groupTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "groupTaskListener mess : "+message);
			
			AsyncResponse asyncResponse = new AsyncResponse(message);
			List<FriendDetailItem> list = new ArrayList<FriendDetailItem>();
			if(asyncResponse.ifSuccess())
			{	
				list = asyncResponse.getFriendDetail();
				String msg = list.get(0).getMessage();
				
				if(Integer.parseInt(list.get(0).getType())==FriendDetailAdapter.TYPE_IMAGE)
					msg = "Image";	
				
				Log.e(TAG, "msg : "+message);
				
			    Intent intent = new Intent(CommonUtilities.GROUP_MESSAGE_ACTION);
			    intent.putExtra(CommonUtilities.EXTRA_MESSAGE, message);
			    getApplicationContext().sendBroadcast(intent);
				
				sendNotification(msg, MESSAGETYPE_GROUP);
			}
			else
			{
				Log.e(TAG, "err : "+asyncResponse.getMessage());
			}
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};
    
	
	private void sendRequestNotification(String title, String message, String userId, String friendId) {
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(false);
        //builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(message);
        
        builder.setSmallIcon(notificationIcon);
        builder.setTicker(message);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        
        Intent intent = new Intent(this, FriendsAllListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        
        Intent acceptIntent = new Intent(this, FriendRequestService.class);
        acceptIntent.setAction(ACTION_ACCEPT);
        acceptIntent.putExtra(Constant.USER_ID, userId);
        acceptIntent.putExtra(Constant.FRIEND_ID, friendId);
        
        PendingIntent acceptPendingIntent = PendingIntent.getService(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);        
        builder.addAction(android.R.drawable.ic_media_play, "Accept", acceptPendingIntent);
        
        Intent rejectIntent = new Intent(this, FriendRequestService.class);
        rejectIntent.setAction(ACTION_REJECT);
        rejectIntent.putExtra(Constant.USER_ID, userId);
        rejectIntent.putExtra(Constant.FRIEND_ID, friendId);
        
        PendingIntent rejectPendingIntent = PendingIntent.getService(this, 0, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_action_cancel, "Reject", rejectPendingIntent);
        
        // if(artwork != null) {
//             builder.setLargeIcon(artwork);
        // }
        // builder.setContentText(artist);
        // builder.setSubText(album);
        
        // startForeground(R.id.notification_id, builder.build());
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
	
	public static void cancelNotification(Context ctx, int notifyId) {
	    String ns = Context.NOTIFICATION_SERVICE;
	    NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
	    nMgr.cancel(notifyId);
	}    
	
	protected void showToast(String message) {
		Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
	}		
    
    protected void showToastLong(String message) {
		Toast.makeText(this, ""+message, Toast.LENGTH_LONG).show();
	}
}
