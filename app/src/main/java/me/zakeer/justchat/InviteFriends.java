package me.zakeer.justchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import me.zakeer.justchat.adapters.InviteListAdapter2;
import me.zakeer.justchat.database.FriendData;
import me.zakeer.justchat.database.SqliteHandle;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.Constant;

public class InviteFriends extends SherlockActivity {
	
	Context context = InviteFriends.this;
	private static final String TAG = "ListFriends";
	
	List<String> contactlist=new ArrayList<String>();
	List<String> namelist=new ArrayList<String>();
	
	ListView listView;
	
	List<FriendData> list=new ArrayList<FriendData>();
	List<FriendData> checklist=new ArrayList<FriendData>();
	
	InviteListAdapter2 adapter;	
	SqliteHandle sqhandle;
		
	//FOR SEARCH
	
	EditText search;
	String user_id;
	
	//
	TextView showhide;
	String Sender_Name="";
	String Promo_Id = "",Promo_code = "";
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.invite);
		
		user_id=Sessions.getUserId(context);
		
		showhide = (TextView) findViewById(R.id.showhide);
		showhide.setVisibility(View.INVISIBLE);
		search = (EditText) findViewById(R.id.find);
		
		getSupportActionBar().setTitle("Invite");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(Constant.ACTIONBAR_ICON);
		
		search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				setnewadapter();
			}
		});
		
		listView=(ListView)findViewById(R.id.list_friend);
		adapter=new InviteListAdapter2(context, list);
		listView.setAdapter(adapter);				
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub		
		checklist.clear();		
		String contact_list = getcontactno();		
		Log.e(TAG, contact_list);
		
		sqhandle=new SqliteHandle(context);
		checklist=sqhandle.getAllFriendDetails();
		sqhandle.close();
		
		if(checklist.size()==0)
			getresponse(contact_list);
		else
		{
			setnewadapter();
		}
		
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		sqhandle=new SqliteHandle(context);
		sqhandle.deleteAllFriendDetails();
		sqhandle.close();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.submit2, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			
		case android.R.id.home:
        	finish();
        return true;
        
		case R.id.add_contact:
						
			sqhandle=new SqliteHandle(context);
			sqhandle.deleteAllFriendDetails();
			sqhandle.close();
			
			  Intent intent = new Intent(Intent.ACTION_INSERT);
			    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		        startActivity(intent);
					
			return true;
		
		case R.id.submit:
			
			List<FriendData> total;
			sqhandle=new SqliteHandle(context);
			total=sqhandle.getCheckedFriendDetails();
			sqhandle.close();
			String names="";
			for(int i=0;i<total.size();i++)
			{	
				Log.e(TAG, "Checked "+total.get(i).getFname());
				names+=total.get(i).getFname()+",";
				
				String message = Sender_Name+" asked you to join promo code "+Promo_code;
				String number = total.get(i).getPhone();
				sendsms(message,number);			
				
				
			}		
			//	sendnotifications(total,names);
			
			sqhandle=new SqliteHandle(context);
			sqhandle.deleteAllFriendDetails();
			sqhandle.close();
		
			
			//finish();
			
			return true;
		}
		
		
		return super.onOptionsItemSelected(item);
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

	private void sendsms(String message, String number) {
		// TODO Auto-generated method stub
		try {
			Log.e(TAG, number+"  "+message);
			 SmsManager sms = SmsManager.getDefault();
		        sms.sendTextMessage(number, null, message, null, null);
		} 
		catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	private void setadapters() 
	{
		list.clear();
		adapter.refresh(list);
		// TODO Auto-generated method stub
		sqhandle=new SqliteHandle(context);
		list=sqhandle.getAllFriendDetails();	
		adapter.refresh(list);
		sqhandle.close();
	}
	
	private void setnewadapter() 
	{
		list.clear();
		checklist.clear();
		showhide.setVisibility(View.INVISIBLE);
		//GET ALL CONTACTS LIST
		sqhandle=new SqliteHandle(context);
		checklist=sqhandle.getAllFriendDetails();
		sqhandle.close();
		
		//get edittext text
		
		String text = search.getText().toString();
		text = text.toLowerCase(Locale.getDefault());
		for(int i=0;i<checklist.size();i++)
		{
			String name = checklist.get(i).getFname();
			name = name.toLowerCase(Locale.getDefault());
			if(name.contains(text))
			{
				list.add(checklist.get(i));
			}
			
		}
		
		if(list.size() == 0)
			showhide.setVisibility(View.VISIBLE);
		
		adapter.refresh(list);
		
	}
	
	private void getresponse(String contact_list) 
	{
		
		String filename = context.getResources().getString(R.string.ContactNotRegitered_php);
		AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(context, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.USER_ID, user_id);
		map.put(Constant.DATA, contact_list);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onTaskComplete(boolean isComplete, String message) 
			{
				boolean success;
				
				JSONObject response;
				try {
					
					response = new JSONObject(message);
				
					success = response.getBoolean("success");
				
					 if(success)
					 {
						 sqhandle=new SqliteHandle(context);
						 JSONArray friend=response.getJSONArray("friend");
						 						 
						 for(int i=0;i<friend.length();i++)
						 {
							
							 boolean promo_avilable = response.getBoolean("promo_available");
							 
							 if(promo_avilable)
							 {
								 Promo_Id = response.getString("promo_id");
								 Promo_code = response.getString("promocode");
								 Sender_Name = response.getString("sender_name");
								 String left = response.getString("left");
								 adapter.setCapacity(Integer.parseInt(left)); 
							 }
							 else
							 {
								adapter.setCapacity(0);
							 }
							 
								
							 
							 
							 JSONObject c=friend.getJSONObject(i);
							 
							 String user_id	= c.getString("user_id");
							 String fname 	= c.getString("fname");
							 String lname 	= c.getString("lname");
							 String phone	= c.getString("phone");
							 String pic		= c.getString("pic");
							 
							 sqhandle.insertInFriend(new FriendData(user_id, fname, lname, pic, phone, "0"));
						 //(user_id, fname, lname,pic, false));}
						 }
						 sqhandle.close();
						 setnewadapter();
						 
						
					 }
					 else
					 {		
						 
					 }
					 
					 setProgressBarIndeterminateVisibility(false);
				 
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					 setProgressBarIndeterminateVisibility(false);
				}
				
			}
			
			@Override
			public void onTaskBegin() {
				 setProgressBarIndeterminateVisibility(true);
			}
		});	
		asyncLoadVolley.beginTask();
	}
	
	private String getcontactno() 
	{
		// TODO Auto-generated method stub
		contactlist.clear();
		namelist.clear();
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
				namelist.add(name);
			}
			cursor.close();
		
			JSONArray myarray=new JSONArray();
			for(int i=0;i<contactlist.size();i++)
			{			
				JSONObject myo=new JSONObject();
				try {
					myo.put(Constant.PHONE, contactlist.get(i));
					myo.put(Constant.NAME, namelist.get(i));				
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
	
	
}
