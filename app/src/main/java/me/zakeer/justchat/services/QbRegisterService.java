package me.zakeer.justchat.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import me.zakeer.justchat.R;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.Constant;

public class QbRegisterService extends Service{

	private static final String TAG = "QbRegisterService";	
	
	Timer timer=new Timer();
	TimerTask task;
	
	String userid,qb_id;
	SharedPreferences sharedPreferences;
	private AsyncLoadVolley asyncLoadVolley;
	
	public QbRegisterService() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub		
		
		userid = Sessions.getUserId(getApplicationContext());
		qb_id = Sessions.getQbId(getApplicationContext());
		
		Log.e(TAG, userid + "  "+ qb_id); 
		
		super.onCreate();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		String filename = getResources().getString(R.string.setqb_php);
		asyncLoadVolley = new AsyncLoadVolley(getApplicationContext(), filename);
		
		task=new TimerTask() {
			@Override
			public void run() {
				getlocations();
			}
		};
			
		timer.schedule(task, 0l, 1000*10);		//EVERY 10 seconds		
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		timer.cancel();
		Log.w("mine", "CLICKED");
		super.onDestroy();
	}
	
	 public void getlocations() 
		{
			// TODO Auto-generated method stub
			
	    	Map<String, String> map = new HashMap<String, String>();			
			map.put(Constant.USER_ID, userid);			
			map.put(Constant.QB_ID, qb_id);
			asyncLoadVolley.setBasicNameValuePair(map);
			
			asyncLoadVolley.setOnAsyncTaskListener(new OnAsyncTaskListener() {
				
				@Override
				public void onTaskComplete(boolean isComplete, String message) {
					// TODO Auto-generated method stu					
					try {
						JSONObject object = new JSONObject(message);
						boolean success = object.getBoolean(Constant.SUCCESS);
						
						if(success)
							stopSelf();						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						
					}						
					
				}				
				@Override
				public void onTaskBegin() {
					// TODO Auto-generated method stub
					
				}
			});
			
	    	asyncLoadVolley.beginTask();			

								
		}

	
}
