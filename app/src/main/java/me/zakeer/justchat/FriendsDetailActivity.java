package me.zakeer.justchat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import me.zakeer.justchat.adapters.FriendDetailAdapter;
import me.zakeer.justchat.imagecache.ImageLoader;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.items.FriendDetailItem;
import me.zakeer.justchat.items.FriendItem;
import me.zakeer.justchat.qb.ActivityVideoChat;
import me.zakeer.justchat.qb.DataHolder;
import me.zakeer.justchat.qb.DialogHelper;
import me.zakeer.justchat.qb.OnCallDialogListener;
import me.zakeer.justchat.qb.QBSessions;
import me.zakeer.justchat.qb.QbConnect;
import me.zakeer.justchat.services.LastSeenGetService;
import me.zakeer.justchat.services.LastSeenSetService;
import me.zakeer.justchat.services.LoadFileService;
import me.zakeer.justchat.services.LoadImageService;
import me.zakeer.justchat.sessions.SessionLastSeen;
import me.zakeer.justchat.sessions.SessionSticker;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.Base64;
import me.zakeer.justchat.utility.CommonUtilities;
import me.zakeer.justchat.utility.ConnectionDetector;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.DateTime;
import me.zakeer.justchat.utility.FileUtility;
import me.zakeer.justchat.utility.GpsTracker;
import me.zakeer.justchat.utility.ImageCustomize;
import me.zakeer.justchat.utility.Validate;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat.core.service.QBVideoChatService;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.CallType;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;
import com.quickblox.module.videochat.model.utils.Debugger;

public class FriendsDetailActivity extends SherlockActivity {
	
	private static final String TAG = "FriendsDetailActivity";
	
	private Context context = FriendsDetailActivity.this;
	
	private ListView listView;
	private EditText edittext;
	
	private AsyncLoadVolley asyncLoadVolley;
	private String filename;
	
	private List<FriendDetailItem> list;
	private FriendDetailAdapter adapter;
	
	private TextView nameTextView, descTextView;
	private ImageView profileImageView;
	private ImageLoader imageLoader;
	
	private String friendId, name, isOnline, phone, phoneCode;
	private int pos = 0;
	private String data = "";
	private String qbId = "";
	
	public static final String TYPE_TEXT = "1";
	public static final String TYPE_IMAGE = "2";
	public static final String TYPE_FILE = "3";
	
	////
	
	private static final int CAMERA_REQUEST 		= 1;
	private static final int RESULT_LOAD_IMAGE 		= 2;
	private static final int PICKFILE_RESULT_CODE 	= 3;
	
	private String path;
	
	private ConnectionDetector connectionDetector;
	
	////
	 //QB
	private QBUser qbUser;
	private ProgressDialog progressDialog;
    private boolean isCanceledVideoCall;
	private VideoChatConfig videoChatConfig;
    
	
	private boolean isActionModeSet = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.frienddetail);
		
		QBSessions q = new QBSessions(context);
		q.CheckUserAlive();
		
		Bundle bundle = getIntent().getExtras();
		friendId = bundle.getString(Constant.FRIEND_ID);
		name = bundle.getString(Constant.NAME);
		isOnline = bundle.getString(Constant.ONLINE);
		phone = bundle.getString(Constant.PHONE);
		phoneCode = bundle.getString(Constant.PHONE_CODE);
		pos = bundle.getInt(Constant.POSITION);
		data = bundle.getString(Constant.DATA);
		qbId = bundle.getString(Constant.QB_ID);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(Constant.ACTIONBAR_ICON);  
		
        getSupportActionBar().setTitle(name);
		if(Integer.parseInt(isOnline)==1)
			getSupportActionBar().setSubtitle("");
		else
			getSupportActionBar().setSubtitle("");
		
		Intent service = new Intent();
		service.setClass(context, LastSeenGetService.class);
		SessionLastSeen.setFriendId(context, friendId);
		startService(service);
		
		listView = (ListView) findViewById(R.id.frienddetail_listview);
		edittext = (EditText) findViewById(R.id.frienddetail_edittext);
		
		imageLoader = new ImageLoader(context);
		
		connectionDetector = new ConnectionDetector(context);
		
		list = new ArrayList<FriendDetailItem>();
		adapter = new FriendDetailAdapter(context, list);
		listView.setAdapter(adapter);
		
		filename = getResources().getString(R.string.frienddetail_php);
		asyncLoadVolley = new AsyncLoadVolley(context, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.USER_ID, Sessions.getUserId(context));
		map.put(Constant.FRIEND_ID, friendId);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(asyncTaskListener);
		
		if(savedInstanceState==null)
			asyncLoadVolley.beginTask();
		
		listView.setOnItemClickListener(listItemClickListener);
		listView.setOnItemLongClickListener(listItemLongClickListener);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.SINGLE_MESSAGE_ACTION));
		registerReceiver(mHandleMessageDelivery, new IntentFilter(CommonUtilities.DELIVERY_MESSAGE_ACTION));
		
		registerReceiver(mUploadProgressReceiver, new IntentFilter(Constant.BROADCAST_REFRESH));
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mHandleLastSeen,
			      new IntentFilter(Constant.LAST_SEEN));
		
		//QB
		
		initViews();
		
	}
	
	private void initViews() {
		
		isCanceledVideoCall = true;

		QBSessions q = new QBSessions(context);
		q.CheckUserAlive();
		
		// Setup UI
		//
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.please_wait));
		
		progressDialog
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialogInterface) {
						if (isCanceledVideoCall) {
							QBVideoChatService.getService().stopCalling(
									videoChatConfig);
						}
					}
				});
			
		// String userName = getIntent().getStringExtra("userName");
		// audioCallBtn.setText(audioCallBtn.getText().toString() + " " +
		// userName);

		// Set VideoCHat listener
		//
		QBUser currentQbUser = DataHolder.getInstance().getCurrentQbUser();
		Debugger.logConnection("setQBVideoChatListener: "
				+ (currentQbUser == null) +(qbVideoChatListener==null) + (currentQbUser != null ? currentQbUser.getId()+"":"error") );
		try {
						
			QBVideoChatService.getService().setQBVideoChatListener(currentQbUser, qbVideoChatListener);;
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * listener to get the response of deleted messages
	 * 
	 */	
	OnAsyncTaskListener deleteAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "mess : "+message);
			AsyncResponse asyncResponse = new AsyncResponse(message);
			if(asyncResponse.ifSuccess())
			{
				showToast(""+asyncResponse.getMessage());
				filename = getResources().getString(R.string.frienddetail_php);
				asyncLoadVolley = new AsyncLoadVolley(context, filename);
				Map<String, String> map = new HashMap<String, String>();
				map.put(Constant.USER_ID, Sessions.getUserId(context));
				map.put(Constant.FRIEND_ID, friendId);
				asyncLoadVolley.setBasicNameValuePair(map);
				asyncLoadVolley.setOnAsyncTaskListener(asyncTaskListener);
				asyncLoadVolley.beginTask();
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
	
	/**
	 * 
	 */
	OnItemClickListener listItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			if(!isActionModeSet)
			{				
				Log.e(TAG, "type : "+list.get(position).getType());
				FriendDetailItem item = list.get(position);
				if(item.getType().equals(String.valueOf(FriendDetailAdapter.TYPE_IMAGE))) {
					if(item.getImageType()==0) {
						Intent intent = new Intent();
						intent.setClass(context, UserDetailActivity.class);
						intent.putExtra(Constant.IMAGE, list.get(position).getMessage());
						intent.putExtra(Constant.NAME, list.get(position).getUserName());
			        	startActivity(intent);
					}
				}
				else if(item.getType().equals(String.valueOf(FriendDetailAdapter.TYPE_MAP))) {
					String message = list.get(position).getMessage();
					if(message.contains(","))
					{
						String[] msg = message.split(",");
						String latitude = msg[0];
						String longitude = msg[1];
						openMap("Location", latitude, longitude);
					}
				}	
			}
			else
			{					
				if(list.get(position).getUserId().equals(Sessions.getUserId(context)))
				{
					if(list.get(position).isChecked())
					{
						list.get(position).setChecked(false);
					}
					else
					{
						list.get(position).setChecked(true);
					}
					adapter.refresh(list);
					
					checkList = new ArrayList<String>();
			        for (FriendDetailItem item : list) {
						if(item.isChecked())
						{
							String checkId = item.getId();
							checkList.add(checkId);
						}
					}
				}
			}
		}
	};
	
	private List<String> checkList = new ArrayList<String>();
	
	/**
	 * On Long click listener, to get the selected list items
	 * 
	 */	
	ActionMode mActionMode;
	OnItemLongClickListener listItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			
			FriendDetailItem listItem = list.get(position);
			
			if (isActionModeSet) {
				if(!listItem.getUserId().equals(Sessions.getUserId(context)))
					return false;
	        }
			
			listItem.setChecked(true);
			adapter.refresh(list);
			
	        // Start the CAB using the ActionMode.Callback defined above
			mActionMode = startActionMode(mActionModeCallback);
			isActionModeSet = true;
	        
	        checkList = new ArrayList<String>();
	        for (FriendDetailItem item : list) {
				if(item.isChecked())
				{
					String checkId = item.getId();
					checkList.add(checkId);
				}
			}	        
	        return true;			
		}
	};
	
	private void reset(List<FriendDetailItem> list) {
		for (FriendDetailItem item : list) {
			item.setChecked(false);
		}
		if(adapter!=null)
			adapter.refresh(list);
	}
	
	private String getJsonResponse(List<String> checkList) {
		JSONArray myarray=new JSONArray();
	    for(int i=0;i<checkList.size();i++)
	    {
			{		    	
			    JSONObject myo=new JSONObject();
			    try {
				    myo.put(Constant.MESSAGE_ID, checkList.get(i).toString());
				    myarray.put(myo);
				    
			    } catch (JSONException e)
			    {
			    	e.printStackTrace();
			    }
			}
	    }
	    return myarray.toString();
	}
	
	//////////////////	
	private ActionMode.Callback mActionModeCallback = 
			new ActionMode.Callback() {
		
	    @Override 
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	          MenuInflater inflater = mode.getMenuInflater();
	          inflater.inflate(R.menu.context_menu, menu);
	          return true;
	    }
	    
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    	reset(list);	 
	    	isActionModeSet = false;
	    }
	    
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.delete:
	            	confirmAlertDialog(context, checkList.size(), mode);
	            	
	                return true;
	            default:
	                mode.finish();
	                return false;
	        }
	    }
	    
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
	};
	
	///////////////
	
	private void deleteSelectedItems() {
		
    	String checkedIds = getJsonResponse(checkList);
    	Log.e(TAG, checkedIds);
    	    	
    	if(checkList.size()>0)
	    {
		    String message_id = checkedIds;
			
			String filename = getResources().getString(R.string.message_delete_php);
			asyncLoadVolley = new AsyncLoadVolley(context, filename);
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.MESSAGE_ID, message_id);
			asyncLoadVolley.setBasicNameValuePair(map);
			asyncLoadVolley.setOnAsyncTaskListener(deleteAsyncTaskListener);
			asyncLoadVolley.beginTask();
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.status, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		getSupportMenuInflater().inflate(R.menu.status, menu);
		MenuItem item = menu.findItem(R.id.savestatus);
		item.setTitle("Call "+name);
		item.setIcon(R.drawable.ic_action_end_call);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
			switch (item.getItemId()) {
				
			case android.R.id.home:
				sendResponseToBackActivity();
	        	finish();
	        return true;
	        
			case R.id.savestatus:
				
			/*	String phoneNumber = phoneCode + phone;
				
				Validate validate = new Validate();
				if(validate.isNotEmpty(phoneNumber))
				{
					callThePerson(phoneNumber);
					//videoCall(phoneNumber);
				}
				else
					showToast(name + " doesn't have any number.");*/
				
				if(DataHolder.getInstance().getCurrentQbUser() == null)
				{
					QBSettings.getInstance().fastConfigInit(QbConnect.QB_APP_ID, QbConnect.QB_AUTH_KEY, QbConnect.QB_AUTH_SECRET);
					
					String email = Sessions.getEmail(context);
					String newEmail = Validate.convertEmail(email);
										
					createSession(email, newEmail);
				}
								
			/*	 Intent intent = new Intent(this, ActivityCallUser.class);
			     intent.putExtra("userId", Integer.parseInt(qbId));
			     intent.putExtra("myName", Sessions.getName(context));
			     startActivity(intent);
			     
			*/
				
				onCallClick();
				
	        return true;
			default:
				return super.onOptionsItemSelected(item);
			}
	}
	
	 private void createSession(String login, final String password) {

	        // Create QuickBlox session with user
	        //
	        QBAuth.createSessionByEmail(login, password, new QBCallbackImpl() {
	            @Override
	            public void onComplete(Result result) {
	                if (result.isSuccess()) {
	                    // save current user
	                    DataHolder.getInstance().setCurrentQbUser(((QBSessionResult) result).getSession().getUserId(), password);
	                }
	            }
	        });
	    }
	
	// Call the person
	private void callThePerson(String phone)
	{
		try {
		        Intent intent = new Intent(Intent.ACTION_DIAL);
		        intent.setData(Uri.parse("tel:"+phone));
		        startActivity(intent);
		} catch (Exception e) {
		         Log.e(TAG, "Call failed", e);
		}
	}
	
	private void videoCall(String phone) {
		
		/*
		Intent callIntent = new Intent("com.android.phone.videocall");
		callIntent.putExtra("videocall", true);
		callIntent.setData(Uri.parse("tel:" + phone));
		*/
		
		Intent callIntent = new Intent(Intent.ACTION_NEW_OUTGOING_CALL);
		callIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, ""+phone);
		//callIntent.putExtra("videocall", true);
		//callIntent.setData(Uri.parse("tel:" + phone));
		
		if (getPackageManager().resolveActivity(callIntent, 0) != null)
			startActivity(callIntent);
		else
			showToast("There is no video call facility available.");
	}
	
	private String response = "";
	OnAsyncTaskListener asyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			 setProgressBarIndeterminateVisibility(false);
			Log.e(TAG, "mess : "+message);
			response = message;
			setFriendList(message);
		}
		
		@Override
		public void onTaskBegin() {
			 setProgressBarIndeterminateVisibility(true);
		}
	};
		
	private void openMap(String label, String latitude, String longitude) {
		
		String uriBegin = "geo:" + latitude + "," + longitude;
		String query = latitude + "," + longitude + "(" + label + ")";
		String encodedQuery = Uri.encode(query);
		String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
		Uri uri = Uri.parse(uriString);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	private void setFriendList(String response) {
		
		AsyncResponse asyncResponse = new AsyncResponse(response);
		if(asyncResponse.ifSuccess())
		{
			list = asyncResponse.getFriendDetail();
			Log.i(TAG, "resp : "+response);
			adapter.refresh(list);
			scrollMyListViewToBottom();
		}
		else
		{
			Log.e(TAG, "err : "+asyncResponse.getMessage());
		}
	}
	
	public void onSendClick(View view) {
		
		if(!edittext.getText().toString().equals("")) {
			
			String text = edittext.getText().toString();
			
			if (connectionDetector.isConnectedToInternet()) {
				send(text, "1");
				edittext.setText("");
			}
			else
			{
				showToast("Not connected to the internet.");
			}
		}	
	}
	
	private void scrollMyListViewToBottom() {
		listView.post(new Runnable() {
	        @Override
	        public void run() {
	            listView.setSelection(adapter.getCount() - 1);
	        }
	    }); 
	}
	
	private void send(String text, String type) {
		
		String filename = getResources().getString(R.string.message_php);
		AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(context, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.USER_ID, Sessions.getUserId(context));
		map.put(Constant.FRIEND_ID, friendId);
		map.put(Constant.MESSAGE, text);
		map.put(Constant.TYPE, type);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(sendAsyncTaskListener);
		asyncLoadVolley.beginTask();
		
		FriendDetailItem item = new FriendDetailItem();
		item.setId(Sessions.getUserId(context));
		item.setUserId(Sessions.getUserId(context));
        item.setType(type);
        item.setTime("");
        item.setMessage(text);
        
        item.setChecked(false);
        item.setStatus("0");
        String deliveryTime = "0000-00-00 00:00:00";
        item.setDeliveryTime(deliveryTime);
        
		list.add(item);
		adapter.refresh(list);
		scrollMyListViewToBottom();
		
		data = text;
	}
	
	OnAsyncTaskListener sendAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "Message Sent : "+message);
			
			AsyncResponse asyncResponse = new AsyncResponse(message);
			if(asyncResponse.ifSuccess())
			{
				showToast(""+asyncResponse.getMessage());
								
				refresh();
			}
			else
			{
				showToast(""+asyncResponse.getMessage());
			}			
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};
	
	private void refresh() {
		// refresh
		filename = getResources().getString(R.string.frienddetail_php);
		asyncLoadVolley = new AsyncLoadVolley(context, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.USER_ID, Sessions.getUserId(context));
		map.put(Constant.FRIEND_ID, friendId);
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(asyncTaskListener);
		asyncLoadVolley.beginTask();
		
	}
	
	 /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageDelivery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String newMessage = intent.getExtras().getString("message");
            
            Log.i(TAG, "Delivery ");
        	
        	refresh();
            /*
            AsyncResponse asyncResponse = new AsyncResponse(newMessage);
    		if(asyncResponse.ifSuccess())
    		{
    			List<FriendDetailItem> listNewMessage = asyncResponse.getFriendDetail();
    			
    			Log.i(TAG, "listNewMessage.get(0).getUserId() : "+listNewMessage.get(0).getUserId());
    			Log.i(TAG, "friendId : "+friendId);
    			
    			if(listNewMessage.get(0).getUserId().equals(friendId))
    			{
    				list.addAll(listNewMessage);
	    			Log.i(TAG, "resp : "+newMessage);
	    			adapter.refresh(list);
	    			scrollMyListViewToBottom();
    			}
    		}
    		else
    		{
    			Log.e(TAG, "err : "+asyncResponse.getMessage());
    		}*/
        }
    };	
	
    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString("message");
            
            Log.i(TAG, "newMessage : "+newMessage);
            
            AsyncResponse asyncResponse = new AsyncResponse(newMessage);
    		if(asyncResponse.ifSuccess())
    		{
    			List<FriendDetailItem> listNewMessage = asyncResponse.getFriendDetail();
    			
    			Log.i(TAG, "listNewMessage.get(0).getUserId() : "+listNewMessage.get(0).getUserId());
    			Log.i(TAG, "friendId : "+friendId);
    			
    			if(listNewMessage.get(0).getUserId().equals(friendId))
    			{
    				list.addAll(listNewMessage);
	    			Log.i(TAG, "resp : "+newMessage);
	    			adapter.refresh(list);
	    			scrollMyListViewToBottom();
    			}
    		}
    		else
    		{
    			Log.e(TAG, "err : "+asyncResponse.getMessage());
    		}
        }
    };	
    
    /**
     * Receiving last seen
     * */
    private final BroadcastReceiver mHandleLastSeen = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	String response = intent.getExtras().getString(Constant.RESPONSE);
        	//Log.v(TAG, "resp  : "+response);
        	AsyncResponse asyncResponse = new AsyncResponse(response);
			if(asyncResponse.ifSuccess())
			{
				List<FriendItem> list = asyncResponse.getLastSeen();
				String time = list.get(0).getLastSeen();
				setLastSeen(time);
			}
        }
        
		private void setLastSeen(String time) {
			
			long lastSeen = DateTime.getTimeStampFromCurrentTimeStampFormat(time);
			long dateNow = new Date().getTime();
			
			Log.e(TAG, "lastSeen : "+lastSeen); // 1394140873000
			Log.e(TAG, "dateNow : "+dateNow); 	// 1394097680973
			
			long diff = dateNow - lastSeen;
			Log.e(TAG, "diff : "+diff);
			
			int maxPossibleLagTime = LastSeenGetService.getTime + LastSeenSetService.setTime;
			
			if(lastSeen>=dateNow-maxPossibleLagTime && lastSeen<dateNow+LastSeenGetService.getTime)
			{
				getSupportActionBar().setSubtitle("Online");
			}
			else
			{
				String date = DateTime.formateDateFromstring(DateTime.TIMESTAMP_FORMAT, 
						DateTime.LAST_SEEN_FORMAT, time);		
				Log.e(TAG, date);
				String lastseen = date.replace("AM", "am").replace("PM","pm");
				getSupportActionBar().setSubtitle("last seen "+lastseen);	
			}	
		}
    };
    
    private void stopLastSeenService(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, LastSeenGetService.class);
		context.stopService(intent);		
	}
    
    private final BroadcastReceiver mUploadProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	refresh();
        	
        	/*
        	boolean status = intent.getExtras().getBoolean(Constant.STATUS);
        	String message = intent.getExtras().getString(Constant.IMAGE);
        	int position = intent.getExtras().getInt(Constant.POSITION);
        	if(status)
        	{
        		showToast("Upload Complete");
        		
        		list.get(position).setMessage(message);
        		list.get(position).setImageType(0);
                
        		adapter.refresh(list);
        		scrollMyListViewToBottom();
         	}
        	*/
        }
    };	
    
    //////// upload
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != RESULT_OK)
			return;
		
		switch (requestCode) {
		case CAMERA_REQUEST:
			
			if (resultCode == Activity.RESULT_OK)
			{
				{
					FileUtility fileUtility = new FileUtility(context);    	
			    	File imageFile = fileUtility.getTempJpgImageFile();
					
					path = imageFile.getAbsolutePath();
							
					File file = new File(path);
					Bitmap bitmap = ImageCustomize.decodeFile(file, 200);
							
							SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
							editor.putString(Constant.USER_ID, "pic"+Sessions.getUserId(context) + friendId + ".jpg");
				        	editor.putString(Constant.PATH, path);
				        	editor.putString(Constant.NAME, Sessions.getUserId(context));
					        editor.commit();
							
					onSelectPhoto(bitmap);
				}			
			}
			break;
			
		case RESULT_LOAD_IMAGE:
			{
				 if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			            Uri selectedImage = data.getData();
			            String[] filePathColumn = { MediaStore.Images.Media.DATA };
			            Cursor cursor = getContentResolver().query(selectedImage,
			                    filePathColumn, null, null, null);
			            cursor.moveToFirst();
			            
			            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			            String picturePath = cursor.getString(columnIndex);
			            cursor.close();
			            
			            File file = new File(picturePath);
						Bitmap bitmap = ImageCustomize.decodeFile(file, 200);
						
						path=picturePath;
						
						onSelectPhoto(bitmap);
				 	}
			}
			
			case PICKFILE_RESULT_CODE:
			{
				if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && null != data) {
					Uri selectedFile = data.getData();
					path = selectedFile.getPath();
					
					onSelectFile(path);
					
				}
			}
		}
	}
	
	public String getStringFromFile(File file) {
			
		 String text = "";
		 byte[] byte_arr = new byte[(int) file.length()];
		 try {
			 FileInputStream fileInputStream = new FileInputStream(file);
             fileInputStream.read(byte_arr);
             
             for (int i = 0; i < byte_arr.length; i++) {
                 System.out.print((char)byte_arr[i]);
             }
             
             String imageStr = Base64.encodeBytes(byte_arr);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		 return text;
	}
	
	/*
	public String getStringFromFile(File file){
		
		 String text = "";
		 try {
		        FileInputStream is = new FileInputStream(file);
		        int size = is.available();
		        byte[] buffer = new byte[size];
		        is.read(buffer);
		        is.close();
		        text = new String(buffer);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		 return text;
	}
	*/
	private void onSelectPhoto(Bitmap bitmap) {
		
		FriendDetailItem item = new FriendDetailItem();
        item.setId("1");
        item.setUserId(Sessions.getUserId(context));
        item.setMessage(path);
        item.setType("2");
        item.setTime(""+new Date().getTime());
        item.setImageType(1);
        item.setBitmap(bitmap);
        
        item.setChecked(false);
        item.setStatus("0");
        String deliveryTime = "0000-00-00 00:00:00";
        item.setDeliveryTime(deliveryTime);
        
        list.add(item);
		adapter.refresh(list);
		scrollMyListViewToBottom();
		
		String name = "imapp"+Sessions.getUserId(context) + friendId + new Date().getTime() +".jpg";
		
		Intent intent=new Intent(getApplicationContext(), LoadImageService.class);
		intent.putExtra(Constant.NAME, name);
		intent.putExtra(Constant.PATH, path);
		intent.putExtra(Constant.MESSAGE, name);
		intent.putExtra(Constant.TYPE, "2");
		intent.putExtra(Constant.FRIEND_ID, friendId);
		intent.putExtra(Constant.VALUE, "1");
		intent.putExtra(Constant.DATA, "1");
		intent.putExtra(Constant.POSITION, list.size()-1);
		startService(intent);
	}
	
	/**
	 * 
	 * 
	 * @param view
	 */	
	private void onSelectFile(String path) {
		
		File file = new File(path);
		//String string = getStringFromFile(file);
		
		name = "testfile.txt";
		String filename = file.getName();
		Log.e(TAG, "filename : "+filename);
		if(filename.contains("."))
		{
			String array[] = filename.split("\\.");
			String extension = array[1];
			String tname = array[0];
			
			name = tname + Constant.TAG_SPLIT_TEXT + new Date().getTime() + "." + extension;
		}
		else
		{
			name = filename;
		}
		
		FileUtility fileUtility = new FileUtility(getApplicationContext());
		int fileLength = fileUtility.getLengthOfFile(path);
		Log.e(TAG, "length : "+fileLength); 
		if(fileLength<=LoadFileService.MAX_FILE_LENGTH)
		{	
			if(fileLength!=0)
			{	
				FriendDetailItem item = new FriendDetailItem(); // TODO
		        item.setId("1");
		        item.setUserId(Sessions.getUserId(context));
		        item.setMessage(""+filename);
		        item.setType("3");
		        item.setTime(""+new Date().getTime());
		        
		        item.setChecked(false);
		        item.setStatus("0");
		        String deliveryTime = "0000-00-00 00:00:00";
		        item.setDeliveryTime(deliveryTime);
		        
		        list.add(item);
				adapter.refresh(list);
				scrollMyListViewToBottom();		
			}
		}
		
		Intent intent=new Intent(getApplicationContext(), LoadFileService.class);
		intent.putExtra(Constant.NAME, name);
		intent.putExtra(Constant.PATH, path);
		intent.putExtra(Constant.VALUE, "1");
		
		intent.putExtra(Constant.MESSAGE, name);
		intent.putExtra(Constant.TYPE, "3");
		intent.putExtra(Constant.FRIEND_ID, friendId);
		intent.putExtra(Constant.DATA, "1");
		intent.putExtra(Constant.POSITION, list.size()-1);
		
		startService(intent);			
	}
	
	private void onCallClick() {
		
		final int AUDIO_CALL = 0;
		final int VIDEO_CALL = 1;
		
		qbUser = new QBUser(Integer.parseInt(qbId));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setItems(new CharSequence[] {"Make a Audio Call", "Make Video Call"}, 
		        new DialogInterface.OnClickListener() {
			
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which) {
		        case AUDIO_CALL: // 

		        	if (progressDialog != null && !progressDialog.isShowing()) {
						progressDialog.show();
					}
					videoChatConfig = QBVideoChatService.getService().callUser(
							qbUser, CallType.AUDIO, null);
		        	
		            break;
		            
		        case VIDEO_CALL:
		        	
		        	if (progressDialog != null && !progressDialog.isShowing()) {
						progressDialog.show();
					}
					videoChatConfig = QBVideoChatService.getService().callUser(
							qbUser, CallType.VIDEO_AUDIO, null);
		            break;
		            
		            
		        default:
		            break;
		        }
		    }	
		});
		
		builder.show();
	}
    
    public void onUploadClick(View view) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Choose an Option");
		builder.setItems(new CharSequence[] {"Image from Gallery", "Take Camera Picture", "Choose File", "Send My Location", "Choose Sticker"}, 
		        new DialogInterface.OnClickListener() {
			
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which) {
		        case 0:
		        	callgalery();
		            break;
		            
		        case 1:
		        	callCamera();
		            break;
		            
		        case 2: 
		        	//callFile();
		        	//openFile("file/*");
		        	openFile("*/*");
		            break;
		            
		        case 3: // "Send My Location"
		        	getMyLocation();
		        	break;
		        	
		        case 4:
		        	chooseSticker();
		            
		        default:
		            break;
		        }
		    }	
		});
		
		builder.show();
	}
    
    public void callCamera() {		

    	FileUtility fileUtility = new FileUtility(context);    	
    	File imageFile = fileUtility.getTempJpgImageFile();
		Uri uriSavedImage = Uri.fromFile(imageFile);
		
		 Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		 i.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage); 
	     startActivityForResult(i, CAMERA_REQUEST);
	}
	
	protected void callgalery()
	{
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
	public void openFile(String minmeType) {
		
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(minmeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
         // if you want any file type, you can skip next line 
        sIntent.putExtra("CONTENT_TYPE", minmeType); 
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);
        
        Intent chooserIntent;
        if (getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with samsung file manager
        	
        	Log.e(TAG, "Samsung ");
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intent});
        }
        else {
        	Log.e(TAG, "Not Samsung ");
            chooserIntent = Intent.createChooser(intent, "Open file");
        }
        
        try {
            startActivityForResult(chooserIntent, PICKFILE_RESULT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }
	
	private void getMyLocation() {
		
		GpsTracker gpsTracker = new GpsTracker(context);
		if(gpsTracker.canGetLocation())
		{	
			double lat = gpsTracker.getLatitude();
        	double lon = gpsTracker.getLongitude();
        	
        	if(connectionDetector.isConnectedToInternet())
        	{
	        	if(lat==0 || lon==0)
	        	{
	        		showToast("Could not get your location.");
	        	}
	        	else
	        	{
		        	String latitude = String.valueOf(lat);
		        	String longitude = String.valueOf(lon);
		        	
		        	send(latitude + ","+longitude, ""+FriendDetailAdapter.TYPE_MAP);        		
	        	}
        	}
        	else
        	{
        		showToast(""+getResources().getString(R.string.internet_lost));
        	}   
		}
		else
		{
			gpsTracker.showSettingsAlert();
		}
	}
	
	private void chooseSticker() {
		Log.d(TAG, "Execution Sticker");
		if(SessionSticker.isAllStickersSet(context)) {
			SelectDialog dialog = new SelectDialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setTitle("Select a Sticker");
			dialog.setOnItemClickListener(new SelectDialog.OnSelectDialogResult() {
				@Override
				public void finish(String result, int resultCode) {
					Log.e(TAG, "activity : image id : "+result);
					//showToast("image id : "+result);
					if(resultCode==SelectDialog.RESULT_OK)
					{
						String imageName = result;
						if(imageName!=null)					
							send(""+imageName, ""+FriendDetailAdapter.TYPE_STICKER);
					}
				}
			});
			dialog.show();
		}
		else
		{
			if(SessionSticker.isSomeStickersSet(context))
			{
				
			}
			showToast("Please wait..");
		}	
	}
    
    @Override
    public void onBackPressed() {
    	stopLastSeenService(context);
    	sendResponseToBackActivity();
    	
    	super.onBackPressed();
    		
    }
    
    private void sendResponseToBackActivity() {
    	
    	boolean textEmpty = true, imageEmpty = true;
    	
		Intent output = new Intent();
		
    	if(list==null)
    	{
			output.putExtra(Constant.DATA, "");
			output.putExtra(Constant.POSITION, pos);
			setResult(RESULT_OK, output);
    		return;
    	}
    	
    	if(list.size()==0)
    	{
    		output.putExtra(Constant.DATA, "");
			output.putExtra(Constant.POSITION, pos);
			setResult(RESULT_OK, output);
    		return;
    	}
    	
    	for (int i = list.size()-1; i >= 0; --i) {
    		
    		Log.v(TAG, "type : "+list.get(i).getType());
    		
    		if(list.get(i).getType().equals(String.valueOf(FriendDetailAdapter.TYPE_TEXT))) {
    			data = list.get(i).getMessage();
    			textEmpty = false;
    			break;
    		}   
    		if(list.get(i).getType().equals(String.valueOf(FriendDetailAdapter.TYPE_IMAGE)) && imageEmpty) {
    			data = "Image";
    			imageEmpty = false;
    		}
		}
    	
    	Log.i(TAG, "data : "+data);
    	
    	if(textEmpty && imageEmpty) {
    		data = "";
    	}
    	Log.v(TAG, "data : "+data);
    	
		output.putExtra(Constant.DATA, data);
		output.putExtra(Constant.POSITION, pos);
		setResult(RESULT_OK, output);		
	}
    
    public void confirmAlertDialog(final Context context, int count, final ActionMode actionMode) {    	
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		
		if(count==0) {
			showToast("Select some messages to delete");
			return;
		}
		
		else if(count==1)
			alertDialogBuilder.setMessage("Do you delete this message?");
		else
			alertDialogBuilder.setMessage("Do you delete "+count+" messages?");
		
		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	actionMode.finish();
			    	isActionModeSet = false;
			    	deleteSelectedItems();
			    }
			  })
			  
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	dialog.cancel();
			    }
			  });
		
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		
		// show it
		alertDialog.show();
    }    
   

    private OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {		
		
		@Override
		public void onVideoChatStateChange(CallState state,
				VideoChatConfig receivedVideoChatConfig) {
			
			videoChatConfig = receivedVideoChatConfig;
			isCanceledVideoCall = false;
			switch (state) {
			case ON_CALLING:
				showCallDialog();
				break;
			case ON_ACCEPT_BY_USER:
				progressDialog.dismiss();
				startVideoChatActivity();
				break;
			case ON_REJECTED_BY_USER:
				progressDialog.dismiss();
				break;
			case ON_DID_NOT_ANSWERED:
				progressDialog.dismiss();
				break;
			case ON_CANCELED_CALL:
				isCanceledVideoCall = true;
				videoChatConfig = null;
				break;
			case ON_START_CONNECTING:
				progressDialog.dismiss();
				startVideoChatActivity();
				break;
			default:
				break;
			}
		}
	};
    
	private void showCallDialog() {
		DialogHelper.showCallDialog(this, new OnCallDialogListener() {
			@Override
			public void onAcceptCallClick() {
				if (videoChatConfig == null) {
					Toast.makeText(getBaseContext(),
							getString(R.string.call_canceled_txt),
							Toast.LENGTH_SHORT).show();
					return;
				}
				QBVideoChatService.getService().acceptCall(videoChatConfig);
			}
			
			@Override
			public void onRejectCallClick() {
				if (videoChatConfig == null) {
					Toast.makeText(getBaseContext(),
							getString(R.string.call_canceled_txt),
							Toast.LENGTH_SHORT).show();
					return;
				}
				QBVideoChatService.getService().rejectCall(videoChatConfig);
			}
		});
	}
    
	private void startVideoChatActivity() {
		Intent intent = new Intent(getBaseContext(), ActivityVideoChat.class);
		intent.putExtra(VideoChatConfig.class.getCanonicalName(),
				videoChatConfig);
		
		startActivity(intent);
	}
    
    protected void onDestroy() {
    	super.onDestroy();
    	try {
            unregisterReceiver(mHandleMessageReceiver);
            unregisterReceiver(mUploadProgressReceiver);
            unregisterReceiver(mHandleMessageDelivery);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandleLastSeen);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
    };
        
    protected void onResume() {
    	    	
    	 try {
 			QBVideoChatService.getService().setQbVideoChatListener(
 					qbVideoChatListener);
 		} catch (NullPointerException ex) {
 			ex.printStackTrace();
 		}    	
    	super.onResume();
    };
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}		
    
    protected void showToastLong(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
	}
}
