package me.zakeer.justchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import me.zakeer.justchat.adapters.AllContactAdapter;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.items.FriendItem;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.ConnectionDetector;
import me.zakeer.justchat.utility.Constant;

public class AddContactFriend extends SherlockActivity{
	
	Context context = AddContactFriend.this;
	private static final String TAG = "ListFriends";
	
	List<String> contactlist=new ArrayList<String>();
	
	ListView listView;
	private List<FriendItem> list;
	private AllContactAdapter adapter;
	TextView showhide;
	private ConnectionDetector connectionDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simplelistview);
		
		showhide = (TextView) findViewById(R.id.showhide);
		listView=(ListView)findViewById(R.id.list);
		
		list = new ArrayList<FriendItem>();
		adapter = new AllContactAdapter(context, list);
		
		listView.setAdapter(adapter);		
			
		getSupportActionBar().setTitle("Contact List");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(Constant.ACTIONBAR_ICON); 
		
        connectionDetector = new ConnectionDetector(context);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(connectionDetector.isConnectedToInternet())
		{
			String contact_list = getcontactno();
			getresponse(contact_list);
		}			
		else
			showToast("Not Connected to Internet");
	}
	
	
	/*private void sendnotifications(List<FriendData> total, String names) 
	{
		// TODO Auto-generated method stub

		JSONArray myarray=new JSONArray();
		for(int i=0;i<total.size();i++)
		{			
			JSONObject myo=new JSONObject();
			try {
				myo.put(Constant.ID, total.get(i).getFriend_id());									
				myarray.put(myo);
				
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Log.e(TAG, myarray.toString());
		
		
		String filename = "sendreq";
		AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(context, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.DATA, myarray.toString());
		map.put(Constant.NAME, names);
		map.put(Constant.USERID, userid);
		map.put(Constant.TRIP_ID, trip_driver_id);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onTaskComplete(boolean isComplete, String message) 
			{
				
			}
			
			@Override
			public void onTaskBegin() {
				
			}
		});	
		asyncLoadVolley.beginTask();
		
		
	}*/

	 @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
				switch (item.getItemId()) {
				
				case android.R.id.home:
		        	finish();
		        return true;
				default:
					return super.onOptionsItemSelected(item);
				}
		}
	
	private void setadapters() 
	{
		list.clear();
		adapter.refresh(list);
		// TODO Auto-generated method stub
		
	}
		
	
	public void getresponse(String contact_list) 
	{
		
		String filename = context.getResources().getString(R.string.ContactFriendList_php);
		AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(context, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.USER_ID, Sessions.getUserId(context));
		map.put(Constant.DATA, contact_list);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onTaskComplete(boolean isComplete, String message) 
			{
				 setProgressBarIndeterminateVisibility(false);
				 
				Log.e(TAG, "mess : "+message);
				setFriendList(message);
			}
			
			@Override
			public void onTaskBegin() {
				 setProgressBarIndeterminateVisibility(true);
			}
		});	
		asyncLoadVolley.beginTask();
	}
	
	private void setFriendList(String response) {
		
		AsyncResponse asyncResponse = new AsyncResponse(response);
		if(asyncResponse.ifSuccess())
		{	
			list = asyncResponse.getFriendAllList();
			Log.i(TAG, "resp : "+response);
			if(list.size() == 0)
			{
				showhide.setVisibility(View.VISIBLE);
			}
			else
			{
				showhide.setVisibility(View.INVISIBLE);
			}
			adapter.refresh(list);
		}
		else
		{
			Log.e(TAG, "err : "+asyncResponse.getMessage());
			showToast(asyncResponse.getMessage());
		}	
	}	
	
	public String getcontactno() 
	{
		// TODO Auto-generated method stub
		contactlist.clear();
		Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
			
		int length=cursor.getCount();
		Log.e("CONTACT COUNT", length+"");
		
			while (cursor.moveToNext()) 
			{
				String name =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								
				phoneNumber=phoneNumber.replace(" ", "");
				
				Log.e("CONTACTS", name+" "+phoneNumber);
				contactlist.add(phoneNumber);
			}
			cursor.close();
		
			JSONArray myarray=new JSONArray();
			for(int i=0;i<contactlist.size();i++)
			{			
				JSONObject myo=new JSONObject();
				try {
					myo.put(Constant.PHONE, contactlist.get(i));									
					myarray.put(myo);
					
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Log.e(TAG, myarray.toString());
		
			return myarray.toString();
			
	}
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}
	
}
