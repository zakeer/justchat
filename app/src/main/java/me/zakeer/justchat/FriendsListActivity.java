package me.zakeer.justchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.zakeer.justchat.adapters.FriendAdapter;
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
import me.zakeer.justchat.services.DownloadImageService;
import me.zakeer.justchat.services.LastSeenSetService;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.CommonUtilities;
import me.zakeer.justchat.utility.ConnectionDetector;
import me.zakeer.justchat.utility.Constant;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat.core.service.QBVideoChatService;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;
import com.quickblox.module.videochat.model.utils.Debugger;

public class FriendsListActivity extends Activity {

protected static final String TAG = "UserActivity";
	
	private Context context = FriendsListActivity.this;
	
	private ListView listView;
	
	private List<FriendItem> list;
	private FriendAdapter adapter;
	
	private TextView nameTextView, statusTextView;
	private ImageView profileImageView;
	private ImageLoader imageLoader;
	
	private AsyncLoadVolley asyncLoadVolley;
	
	private ListView mDrawerList;
    
    private RelativeLayout actionBarLayout;
    
    private String[] listItems;
    
    private static final int HOME = 0;
    private static final int STATUS = 1;
    private static final int DIRECTORY = 2;
    private static final int INVITE = 3;
    private static final int SETTINGS = 4;
    private static final int LOGOUT = 5;
    
    private SlidingPaneLayout pane;
	private ImageView toggleImageView;
	private LinearLayout nofriendsLayout;
	
	private ConnectionDetector connectionDetector;
	
	private Animation animation;
		
	 //////////////
	
	private final String FIRST_USER_PASSWORD = "videoChatUser1";
	private final String FIRST_USER_LOGIN = "videoChatUser1";
	private final String SECOND_USER_PASSWORD = "videoChatUser2";
	private final String SECOND_USER_LOGIN = "videoChatUser2";
	
	String login = "hello";
	String password = "hello";
	
	/////////////////
	
	 private boolean isCanceledVideoCall;
	private VideoChatConfig videoChatConfig;
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.homeslidingpane);
		
		startService(new Intent(this, QBVideoChatService.class));
		
		QBSessions q = new QBSessions(context);
		q.CheckUserAlive();
		
		actionBarLayout = (RelativeLayout) findViewById(R.id.actionbar);
		animation = AnimationUtils.loadAnimation(context, R.anim.fade_actionbar);
		actionBarLayout.setAnimation(animation);
		actionBarLayout.setVisibility(View.VISIBLE);
		
		listItems = getResources().getStringArray(R.array.homelistitem);
        
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		pane = (SlidingPaneLayout) findViewById(R.id.drawer_layout);
		pane.setVerticalFadingEdgeEnabled(false);
		pane.setPanelSlideListener(slideListener);
		
		View homelist = (View) findViewById(R.id.homelist);
		profileImageView = (ImageView) homelist.findViewById(R.id.user_image);
		nameTextView = (TextView) homelist.findViewById(R.id.usernameTextview);
		statusTextView = (TextView) homelist.findViewById(R.id.statusTextview);
		
		View friendlist = (View) findViewById(R.id.friendslist);
		toggleImageView = (ImageView) friendlist.findViewById(R.id.toggle);
		nofriendsLayout = (LinearLayout) friendlist.findViewById(R.id.oopsLayout);
		nofriendsLayout.setAnimation(animation);
		nofriendsLayout.setVisibility(View.GONE);
		
		nameTextView.setText(""+Sessions.getName(context));
		statusTextView.setText("\""+Sessions.getStatus(context)+"\"");
		
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.simplelistitem, listItems));
		mDrawerList.setOnItemClickListener(listener);
		
		listView = (ListView) findViewById(R.id.listview);
		listView.setFocusable(true);
		listView.setOnItemClickListener(listItemClickListener);
		
		imageLoader = new ImageLoader(context);
		String url = Constant.URL + Constant.FOLDER_IMAGES + Sessions.getImage(context);
		imageLoader.displayImage(url, profileImageView);
		
		list = new ArrayList<FriendItem>();
		adapter = new FriendAdapter(context, list);
		
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(listItemClickListener);
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.SINGLE_MESSAGE_ACTION));		
		registerReceiver(mHandleFriendsRequestReceiver, new IntentFilter(CommonUtilities.REQUEST_MESSAGE_ACTION));
		registerReceiver(mHandleUserImage, new IntentFilter(Constant.BROADCAST_FRIENDLIST_USER_IMAGE));
		
		Intent service = new Intent(getApplicationContext(), DownloadImageService.class);
		startService(service);	
		
		Intent setLastSeenService = new Intent();
		setLastSeenService.setClass(context, LastSeenSetService.class);
		startService(setLastSeenService);
				
		//QB
		
		QBUser currentQbUser = DataHolder.getInstance().getCurrentQbUser();
		Debugger.logConnection("setQBVideoChatListener: "
						+ (currentQbUser == null) +(qbVideoChatListener==null) + (currentQbUser != null ? currentQbUser.getId()+"":"error") );
		try {
								
			QBVideoChatService.getService().setQBVideoChatListener(currentQbUser, qbVideoChatListener);			
					
					
		} catch (Exception e) {
					e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent setLastSeenService = new Intent();
		setLastSeenService.setClass(context, LastSeenSetService.class);
		stopService(setLastSeenService);
			super.onBackPressed();
	}
	
	@Override
	protected void onPause() {
			super.onPause();
			startLastSeenService(context); // TODO
	}
	
	@Override
	protected void onResume() {
		super.onResume();
			
			startLastSeenService(context); // TODO
			
			Log.e(TAG, "onResume");
			nameTextView.setText(""+Sessions.getName(context));
			statusTextView.setText("\""+Sessions.getStatus(context)+"\"");
			nofriendsLayout.setVisibility(View.GONE);
			
			String filename = getResources().getString(R.string.friendlistonlyfriends_php);
			asyncLoadVolley = new AsyncLoadVolley(context, filename);
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.ID, Sessions.getUserId(context));
			asyncLoadVolley.setBasicNameValuePair(map);
			asyncLoadVolley.setOnAsyncTaskListener(asyncTaskListener);
			
			connectionDetector = new ConnectionDetector(context);
			
			if(connectionDetector.isConnectedToInternet())
				asyncLoadVolley.beginTask();
			else
				showToast("Not Connected to Internet");		
			
			try {
	 			QBVideoChatService.getService().setQbVideoChatListener(
	 					qbVideoChatListener);
	 		} catch (NullPointerException ex) {
	 			ex.printStackTrace();
	 		}    	
			
	}
	
	private void refresh() {
		String filename = getResources().getString(R.string.friendlistonlyfriends_php);
		asyncLoadVolley = new AsyncLoadVolley(context, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.ID, Sessions.getUserId(context));
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(asyncTaskListener);
		
		connectionDetector = new ConnectionDetector(context);
		
		if(connectionDetector.isConnectedToInternet())
			asyncLoadVolley.beginTask();
	}
	
	private void startLastSeenService(Context context) {
		Intent service = new Intent();
		//service.setClass(context, LastSeenUpdateService1.class);
		//startService(service);
		
	}
	
	private void logoutUser() {
		
		String filename = getResources().getString(R.string.logout_php);
		AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(context, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.ID, Sessions.getUserId(context));
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(logoutAsyncTaskListener);
		asyncLoadVolley.beginTask();
	}
	
	OnAsyncTaskListener asyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "mess : "+message);
			setFriendList(message);
		}
		
		@Override
		public void onTaskBegin() {
			Log.e(TAG, "onTaskBegin : get friends list");
			nofriendsLayout.setVisibility(View.GONE);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != RESULT_OK)
			return;
		else 
		{
			switch (requestCode) {
			case 0:
				/*
				if(data!=null)
				{
					if(data.getStringExtra(Constant.DATA)!=null)
					{
						String text = data.getStringExtra(Constant.DATA);
						int pos = data.getIntExtra(Constant.POSITION, 0);
						list.get(pos).setStatus(text);
						adapter.refresh(list);
					}
				}*/
				break;
				
			default:
				break;
			}
		}
	}
	
	OnAsyncTaskListener logoutAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "mess : "+message);
			
			AsyncResponse asyncResponse = new AsyncResponse(message);
			if(asyncResponse.ifSuccess())
			{
				Sessions.clear(context);
				
				Intent intent = new Intent();				
				intent.setClass(context, LoginActivity.class);
				startActivity(intent);
				finish();				
			}
			else
			{
				showToast("Error Logging Out.");
			}	
		}
		
		@Override
		public void onTaskBegin() {
		}
	};
	
	OnItemClickListener listItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			if (list.get(position).isNew()) {
				list.get(position).setNew(false);
				adapter.refresh(list);
			}
			
			Intent intent = new Intent();			
			intent.setClass(context, FriendsDetailActivity.class);
			intent.putExtra(Constant.FRIEND_ID, list.get(position).getId());
			intent.putExtra(Constant.NAME, list.get(position).getName());
			intent.putExtra(Constant.IMAGE, list.get(position).getImage());
			intent.putExtra(Constant.ONLINE, list.get(position).getIsOnline());
			intent.putExtra(Constant.PHONE_CODE, list.get(position).getPhoneCode());
			intent.putExtra(Constant.PHONE, list.get(position).getPhone());
			intent.putExtra(Constant.POSITION, position);
			intent.putExtra(Constant.DATA, list.get(position).getStatus());
			intent.putExtra(Constant.QB_ID, list.get(position).getQbId());
        	startActivityForResult(intent, 0);
		}
	};
	
	OnItemClickListener listener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			Intent intent = new Intent();
			
			switch (position) {
			
			case HOME:	      
				if (pane.isOpen()) {
					pane.closePane();
				}
				break;
				
			case STATUS:			
				if (pane.isOpen()) {
					intent.setClass(context, StatusActivity.class);
					intent.putExtra(Constant.STATUS, Sessions.getStatus(context));
					startActivity(intent);
				}
				break;
				
			case INVITE:
				if (pane.isOpen()) {
					intent.setClass(context, InviteFriends.class);
					startActivity(intent);
				}
				break;
				
			case SETTINGS:
				if (pane.isOpen()) {
					intent.setClass(context, SettingsActivity.class);
					intent.putExtra(Constant.STATUS, Sessions.getStatus(context));
					startActivity(intent);
				}
				break;
				
			case LOGOUT:
				if (pane.isOpen()) {
					if (connectionDetector.isConnectedToInternet()) {
						logoutUser();						
					}
					else
					{
						showToast(""+getResources().getString(R.string.internet_lost));
					}
				}
				break;
				
			case DIRECTORY:		
				if (pane.isOpen()) {
					intent.setClass(context, GroupListActivity.class);
					startActivity(intent);
				}
				break;
			
			default:
				break;
			}
		}
	};
	
	private void setFriendList(String response) {
		
		AsyncResponse asyncResponse = new AsyncResponse(response);
		if(asyncResponse.ifSuccess())
		{	
			list = asyncResponse.getFriendList();
			adapter.refresh(list);
			if(list.size()<=0)
			{
				nofriendsLayout.setVisibility(View.VISIBLE);
			}
			else
			{
				nofriendsLayout.setVisibility(View.GONE);
			}
		}
		else
		{
			Log.e(TAG, "err : "+asyncResponse.getMessage());
			
			nofriendsLayout.setVisibility(View.GONE);
			list = new ArrayList<FriendItem>();
			adapter.refresh(list);
		}
	}
	
	public void onToggleClick(View view) {
		if(pane.isOpen()) 
			pane.closePane();
		else
			pane.openPane();
	}
	
	public void onAddClick(View view) {
		
		Intent intent = new Intent();
		intent.setClass(context, FriendsAllListActivity.class);
		startActivity(intent);		
	}
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}
    
    protected void showToastLong(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
	}
    
    PanelSlideListener slideListener = new PanelSlideListener() {
		
		@Override
		public void onPanelSlide(View arg0, float arg1) {
			
		}
		
		@Override
		public void onPanelOpened(View arg0) {
			toggleImageView.setImageResource(R.drawable.ic_action_previous_item);
		}
		
		@Override
		public void onPanelClosed(View arg0) {
			toggleImageView.setImageResource(R.drawable.ic_action_next_item);
		}
	};
	
	/**
     * Receiving image refresh
     * */
    private final BroadcastReceiver mHandleUserImage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	imageLoader = new ImageLoader(context);
    		String url = Constant.URL + Constant.FOLDER_IMAGES + Sessions.getImage(context);
    		imageLoader.displayImage(url, profileImageView);
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
    			List<FriendDetailItem> listDetail = asyncResponse.getFriendDetail();
    			
    			String userid = listDetail.get(0).getUserId();
    			
    			List<FriendItem> listFriend = new ArrayList<FriendItem>();
    			listFriend = list;
    			
    			for (int i = 0; i < list.size(); i++) {
    				
					if(list.get(i).getId().equals(userid)) {
						
						 String msg = listDetail.get(0).getMessage();
						 
						 switch (Integer.parseInt(listDetail.get(0).getType())) {
							
							case FriendDetailAdapter.TYPE_TEXT:
								// do nothing
								break;
								
							case FriendDetailAdapter.TYPE_IMAGE:
								msg = "Image";
								break;
								
							case FriendDetailAdapter.TYPE_FILE:
								msg = "File";
								break;
								
							case FriendDetailAdapter.TYPE_MAP:
								msg = "Location";
								break;
								
							case FriendDetailAdapter.TYPE_STICKER:
								msg = "Sticker";
								break;
								
							default:
								break;
							}	
						 	
						 	list.get(i).setStatus(msg);
						 	list.get(i).setNew(true);
						break;
					}
				}
    			adapter.refresh(list);
    		}
    		else
    		{
    			Log.e(TAG, "err : "+asyncResponse.getMessage());
    		}
        }
    };	
    
    /**
     * Refreshing friends details
     * */
    private final BroadcastReceiver mHandleFriendsRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString("message");
            
            Log.i(TAG, "newMessage : "+newMessage);
            
    		if(connectionDetector.isConnectedToInternet())
    		{
    			//TODO
    			refresh();
    		}
    		
        }
    };
    
    //QB
   
	private OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
				
		@Override
		public void onVideoChatStateChange(CallState state,
				VideoChatConfig receivedVideoChatConfig) {
			
			Log.e(TAG, receivedVideoChatConfig.toString());
			
			videoChatConfig = receivedVideoChatConfig;
			isCanceledVideoCall = false;
			switch (state) {
			case ON_CALLING:
				showCallDialog();
				break;
			case ON_ACCEPT_BY_USER:
				break;
			case ON_REJECTED_BY_USER:
				break;
			case ON_DID_NOT_ANSWERED:
				break;
			case ON_CANCELED_CALL:
				isCanceledVideoCall = true;
				videoChatConfig = null;
				break;
			case ON_START_CONNECTING:
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
            unregisterReceiver(mHandleFriendsRequestReceiver);
            unregisterReceiver(mHandleUserImage);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
    };
}
