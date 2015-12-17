package me.zakeer.justchat.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import me.zakeer.justchat.R;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.sessions.SessionUserImage;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.Base64;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.ImageCustomize;

public class LoadImageService extends Service {
	
	protected static final String TAG = "LoadImageService";
	
	private Handler handler;
	SharedPreferences sharedPreferences;
	InputStream inputStream;
	private String imageName, imagePath;
	private String friendId, message, type, value;
	private int position = 0;
	private String data = "1";
	private String groupId;
	
	private String filename;
	private AsyncLoadVolley asyncLoadVolley;
	
	public static final String VALUE_MESSAGE = "1";
	public static final String VALUE_PROFILE_PIC = "2";
	public static final String VALUE_GROUP_PIC = "3";
	public static final String VALUE_FILE_MESSAGE = "4";
	
	public LoadImageService() {
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		handler = new Handler();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Bundle extras = intent.getExtras();
		if(extras!=null)
		{
		imageName = extras.getString(Constant.NAME);
		imagePath = extras.getString(Constant.PATH);
		Log.e(TAG, "ipath : "+imagePath);
		value = extras.getString(Constant.VALUE);
		
		if(value.equals(VALUE_MESSAGE)) 
		{
			friendId = extras.getString(Constant.FRIEND_ID);
			message = extras.getString(Constant.MESSAGE);
			type = extras.getString(Constant.TYPE);
			position = extras.getInt(Constant.POSITION);
			data = extras.getString(Constant.DATA);
		}
		else if(value.equals(VALUE_PROFILE_PIC))
		{			
			imageName = SessionUserImage.getImageName(this);
			imagePath = SessionUserImage.getImagePath(this);
		}
		else if(value.equals(VALUE_GROUP_PIC))
		{
			groupId = extras.getString(Constant.GROUP_ID);
		}
		else if(value.equals(VALUE_FILE_MESSAGE)) {
			friendId = extras.getString(Constant.FRIEND_ID);
			message = extras.getString(Constant.MESSAGE);
			type = extras.getString(Constant.TYPE);
			position = extras.getInt(Constant.POSITION);
			data = extras.getString(Constant.DATA);
		}
		
		Log.e("IN CHECK", "START");
		//check();
		
		onUploadStart(imageName, imagePath);
		}
		else
		{
			Toast.makeText(getApplicationContext(), "The image path could not be located. Please try again", Toast.LENGTH_SHORT).show();
			stopSelf();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void onUploadStart(String imageName, String imagePath) {
		
		String content = getImageFromPath(imagePath);
		
		if(content!=null) {
			//String filename = "upload";
			String filename = "upload_image";
			asyncLoadVolley = new AsyncLoadVolley(this, filename);
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.CONTENT, content);
			map.put(Constant.NAME, imageName);
			asyncLoadVolley.setBasicNameValuePair(map);
			asyncLoadVolley.setOnAsyncTaskListener(uploadAsyncTaskListener);
			 
			handler.post(new Runnable() {
			    public void run()
			    {
			    	asyncLoadVolley.beginTask();
			    }
			});
		}
		else {
			Log.e(TAG, "Could not decode image..");
		}
	}
	
	OnAsyncTaskListener uploadAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			//Log.e(TAG, "value : "+value+", mess : "+message);
			if(value.equals(VALUE_MESSAGE)) 
			{
				Intent intent = new Intent(Constant.IMAGE);
				intent.putExtra(Constant.STATUS, true);
				intent.putExtra(Constant.POSITION, position);
				intent.putExtra(Constant.IMAGE, imageName);
				// sendBroadcast(intent); // TODO : 
				
				onUploadComplete();
			}
			else if(value.equals(VALUE_PROFILE_PIC))
			{
				onUpdateProfileImageInfo();
			}
			else if(value.equals(VALUE_GROUP_PIC))
			{
				onUpdateGroupImageInfo();
				Log.e(TAG, "onUpdateGroupImageInfo ");
			}
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};
	
	private String getImageFromPath(String imagePath) {
		
		if(imagePath!=null)
		{
			if(imagePath.length()!=0)
			{				
				File file = new File(imagePath);
				Bitmap bitmap = ImageCustomize.decodeFile(file, 200); 
		        ByteArrayOutputStream stream = new ByteArrayOutputStream();
		        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.
		        byte [] byte_arr = stream.toByteArray();
		        String imageStr = Base64.encodeBytes(byte_arr);
		        return imageStr;
			}
		}
        return null;
	}
	
	// // VALUE = 1
		public void onUploadComplete() {
			
			if(data.equals("2")) 
				filename = getResources().getString(R.string.message_group_php);
			else
				filename = getResources().getString(R.string.message_php);
			asyncLoadVolley = new AsyncLoadVolley(this, filename);
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.USER_ID, Sessions.getUserId(this));
			map.put(Constant.FRIEND_ID, friendId);
			map.put(Constant.MESSAGE, message);
			map.put(Constant.TYPE, type);
			asyncLoadVolley.setBasicNameValuePair(map);
			asyncLoadVolley.setOnAsyncTaskListener(messageAsyncTaskListener);
			
			handler.post(new Runnable() {
			    public void run()
			    {
			    	asyncLoadVolley.beginTask();
			    }
			});
		}
	
		OnAsyncTaskListener messageAsyncTaskListener = new OnAsyncTaskListener() {
			
			@Override
			public void onTaskComplete(boolean isComplete, String message) {
				
				AsyncResponse asyncResponse = new AsyncResponse(message);
				if(asyncResponse.ifSuccess()) {
					
					Intent intent = new Intent(Constant.BROADCAST_REFRESH);
					sendBroadcast(intent);
					
					Log.e(TAG, "Upload complete and notified : mess : "+message);
				}
				else {
					Log.e(TAG, "err : "+asyncResponse.getMessage());
				}
				
				stopSelf();			
			}
			
			@Override
			public void onTaskBegin() {
				
			}
		};
	
// VALUE = 2
	
	public void onUpdateProfileImageInfo() {
		
		String filename = getResources().getString(R.string.edit_image_php);
		final AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(this, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.ID, Sessions.getUserId(this));
		map.put(Constant.IMAGE, imageName);
		map.put(Constant.TYPE, "1");
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(profileAsyncTaskListener);
		
		handler.post(new Runnable() {
		    public void run()
		    {
		    	asyncLoadVolley.beginTask();
		    }
		});
	}	
	

	OnAsyncTaskListener profileAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			
			AsyncResponse asyncResponse = new AsyncResponse(message);
			if(asyncResponse.ifSuccess()) {
				Log.e(TAG, "Upload complete and notified : mess : "+message);
				Sessions.setImage(getApplicationContext(), imageName);
				
				Intent intent = new Intent(Constant.BROADCAST_FRIENDLIST_USER_IMAGE);
				sendBroadcast(intent);
			}
			else {
				Log.e(TAG, "err : "+asyncResponse.getMessage());
			}
			
			Log.e(TAG, "Upload complete and notified : mess : "+message);
			stopSelf();			
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};
	
	
	// VALUE = 3
	
		public void onUpdateGroupImageInfo() {
			
			String filename = getResources().getString(R.string.edit_image_php);
			final AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(this, filename);
			Map<String, String> map = new HashMap<String, String>();
			
			Log.e(TAG, "onUpdateGroupImageInfo : group ID : "+groupId);
			map.put(Constant.ID, groupId);
			map.put(Constant.IMAGE, imageName);
			map.put(Constant.TYPE, "2");
			asyncLoadVolley.setBasicNameValuePair(map);
			asyncLoadVolley.setOnAsyncTaskListener(groupAsyncTaskListener);
			
			handler.post(new Runnable() {
			    public void run()
			    {
			    	asyncLoadVolley.beginTask();
			    }
			});
		}	
		
		OnAsyncTaskListener groupAsyncTaskListener = new OnAsyncTaskListener() {
			
			@Override
			public void onTaskComplete(boolean isComplete, String message) {
				
				AsyncResponse asyncResponse = new AsyncResponse(message);
				if(asyncResponse.ifSuccess()) {
					Log.e(TAG, "Upload complete and notified : mess : "+message);
				}
				else {
					Log.e(TAG, "err : "+asyncResponse.getMessage());
				}
				
				Log.e(TAG, "Upload complete and notified : mess : "+message);
				stopSelf();			
			}
			
			@Override
			public void onTaskBegin() {
				
			}
		};
	
	
/////
		
	@Override
	public void onDestroy() {
		Log.w("mine", "CLICKED");
		super.onDestroy();
	}
	
}
