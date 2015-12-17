package me.zakeer.justchat.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import me.zakeer.justchat.R;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.sessions.SessionLastSeen;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.ConnectionDetector;
import me.zakeer.justchat.utility.Constant;

public class LastSeenGetService extends Service {

	private static final String TAG = "LastSeenGetService";	
		
	private ConnectionDetector cd;
	private AsyncLoadVolley asyncLoadVolley;
	
	private Timer timer=new Timer();
	private TimerTask task;
	
	private Handler handler;
	
	String friendId = "";
	
	public static final int getTime = 1000 * 8; // 8sec
	
	public LastSeenGetService() {
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		//Log.e(TAG, "LastSeenGetService : Created");
		handler = new Handler();
		cd=new ConnectionDetector(getApplicationContext());
		friendId = SessionLastSeen.getFriendId(this);
		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.e(TAG, "LastSeenGetService : Started");
		String filename = getResources().getString(R.string.last_seen_get_php);
		asyncLoadVolley = new AsyncLoadVolley(getApplicationContext(), filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.ID, friendId);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(uploadAsyncTaskListener);
		
		task=new TimerTask() {
			@Override
			public void run() {
		    	asyncLoadVolley.beginTask();
			}
		};
		
		timer.schedule(task, 0l, getTime);	
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	OnAsyncTaskListener uploadAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			//Log.e(TAG, ", mess : "+message);
			
			if(message!=null)
			{
				Intent intent = new Intent(Constant.LAST_SEEN);			
				intent.putExtra(Constant.RESPONSE, message);
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			}
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};
	
	@Override
	public void onDestroy() {
		timer.cancel();
		Log.w("mine", "CLICKED");
		super.onDestroy();
	}
	
	
}
