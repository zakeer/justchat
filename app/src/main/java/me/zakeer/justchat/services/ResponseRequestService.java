package me.zakeer.justchat.services;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.Constant;

public class ResponseRequestService extends Service {
	
	protected static final String TAG = "ResponseRequestService";
	private Handler handler;
	SharedPreferences sharedPreferences;
	InputStream inputStream;
	private String friendRequestId;
	private String friendId, userId, adminId, type;
	
	AsyncLoadVolley asyncLoadVolley;
	
	public ResponseRequestService() {
		
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
		friendRequestId = extras.getString(Constant.FRIEND_REQUEST_ID);
		friendId = extras.getString(Constant.FRIEND_ID);
		type = extras.getString(Constant.TYPE);
		userId = extras.getString(Constant.USER_ID);
		//adminId = extras.getString(Constant.ADMIN_ID);
		
		Log.e("IN CHECK", "START");
		//check();
		onUploadStart(friendRequestId, type, friendId, userId);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void onUploadStart(String friendRequestId, String type, String friendId, String userId) {
		
			asyncLoadVolley = new AsyncLoadVolley(this, "friendrequestgcm");
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.ID, friendRequestId);
			map.put(Constant.TYPE, type);
			map.put(Constant.FRIEND_ID, friendId);
			map.put(Constant.USER_ID, userId);
			asyncLoadVolley.setBasicNameValuePair(map);
			asyncLoadVolley.setOnAsyncTaskListener(uploadAsyncTaskListener);
			
			handler.post(new Runnable() {
			    public void run()
			    {
			    	asyncLoadVolley.beginTask();
			    }
			});	
	}
	
	OnAsyncTaskListener uploadAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			
			Log.e(TAG, " mess : "+message);
			/*
			Intent intent = new Intent(Constant.FRIEND);
			intent.putExtra(Constant.STATUS, true);
			intent.putExtra(Constant.POSITION, position);
			intent.putExtra(Constant.IMAGE, imageName);
			//sendBroadcast(intent);
			*/
			stopSelf();
			
				//onUploadComplete();
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
