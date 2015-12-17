package me.zakeer.justchat.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import me.zakeer.justchat.R;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.ConnectionDetector;
import me.zakeer.justchat.utility.Constant;

public class LastSeenSetService extends Service {
	
	private static final String TAG = "LastSeenSetService";	
	
	private Timer timer=new Timer();
	private TimerTask task;
	
	private ConnectionDetector cd;
	private AsyncLoadVolley asyncLoadVolley;
	
	private Handler handler;
	
	// set time interval is 3 secs before gettime interval
	public static final int setTime = LastSeenGetService.getTime - 3; 
	
	public LastSeenSetService() {
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		//Log.v(TAG, "LastSeenSetService : Created");
		handler = new Handler();
		cd=new ConnectionDetector(getApplicationContext());
		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		//Log.v(TAG, "LastSeenSetService : Started");
		String filename = getResources().getString(R.string.last_seen_set_php);
		asyncLoadVolley = new AsyncLoadVolley(getApplicationContext(), filename);		
		asyncLoadVolley.setOnAsyncTaskListener(uploadAsyncTaskListener);
		
		task=new TimerTask() {
			@Override
			public void run() {
				
				Map<String, String> map = new HashMap<String, String>();
				map.put(Constant.ID, Sessions.getUserId(getApplicationContext()));
				map.put(Constant.TIME, ""+new Date().getTime());
				asyncLoadVolley.setBasicNameValuePair(map);
		    	asyncLoadVolley.beginTask();
			}
		};
		
		timer.schedule(task, 0l, setTime);		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	OnAsyncTaskListener uploadAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			//Log.e(TAG, ", mess : "+message);
        	AsyncResponse asyncResponse = new AsyncResponse(message);
			if(asyncResponse.ifSuccess())
			{	
				Log.e(TAG, "Time update");
			}
			else
			{
				Log.e(TAG, "Time update ERROR.");
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
