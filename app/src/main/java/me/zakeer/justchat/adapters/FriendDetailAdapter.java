package me.zakeer.justchat.adapters;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.zakeer.justchat.R;
import me.zakeer.justchat.imagecache.ImageLoader;
import me.zakeer.justchat.items.FriendDetailItem;
import me.zakeer.justchat.services.DownLoadFileService;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.ConnectionDetector;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.FileUtility;

public class FriendDetailAdapter extends BaseAdapter {
	
	private static final String TAG = "FriendDetailAdapter";
	private LayoutInflater inflater;
	private ViewHolder holder;
	private List<FriendDetailItem> list;
	private Context context;
	private ImageLoader imageLoader;
	
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_IMAGE = 2; 
	public static final int TYPE_FILE = 3; 
	public static final int TYPE_MAP = 4; 
	public static final int TYPE_STICKER = 5; 
	
	public static final int STATUS_DELIVERY_SENDING = 0; 
	public static final int STATUS_DELIVERY_SENT = 1; 
	public static final int STATUS_DELIVERY_DELIVERED = 2; 
		
	private String response = "";
	
	private String friendImage;
	
	private ConnectionDetector connectionDetector;
	
	public FriendDetailAdapter(Context context, List<FriendDetailItem> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.list = list;
		imageLoader = new ImageLoader(context);
		connectionDetector = new ConnectionDetector(context);
	}   
	
	@Override
	public int getCount() {
		return list.size();
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void refresh(List<FriendDetailItem> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	public List<FriendDetailItem> getList() {
		return list;
	}
	
	public void setFriendImage(String image) {
		this.friendImage = image;
	}
	
	View hView;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        
    	hView = convertView;
    	
    	final FriendDetailItem item = list.get(position);
    	
    	final String userId = item.getUserId();
    	
     	if (convertView == null) {
     			
     			holder = new ViewHolder();
     			
     			hView = inflater.inflate(R.layout.frienddetailtextitemnew, null);
     			
     			// ME
	            holder.userBubble			= (RelativeLayout) hView.findViewById(R.id.userBubble);  
	            //user
	            holder.usernameTextView		= (TextView) hView.findViewById(R.id.frienddetail1_name_text);
	            //holder.userImageView		= (ImageView) hView.findViewById(R.id.frienddetail1_user_image);
	            //text
	            holder.textLayout			= (RelativeLayout) hView.findViewById(R.id.frienddetail1_text_layout);
 				holder.messageTextView		= (TextView) hView.findViewById(R.id.frienddetail1_message_text);
	            //image
	            holder.imageLayout			= (RelativeLayout) hView.findViewById(R.id.frienddetail1_image_layout);  
	            holder.dataImageView		= (ImageView) hView.findViewById(R.id.frienddetail1_message_image);
	            holder.progressBar			= (ProgressBar) hView.findViewById(R.id.progressLoader);
	            //file
	            holder.fileLayout			= (RelativeLayout) hView.findViewById(R.id.message_file_layout);
	            holder.filenameTextView		= (TextView) hView.findViewById(R.id.message_file_textview);
	            //map
	            holder.mapLayout			= (RelativeLayout) hView.findViewById(R.id.frienddetail1_map_layout);
	            holder.mapTextView			= (TextView) hView.findViewById(R.id.frienddetail1_map_text);
	            holder.mapImageView			= (ImageView) hView.findViewById(R.id.frienddetail1_map_image);
	            //sticker
	            holder.stickerLayout		= (RelativeLayout) hView.findViewById(R.id.frienddetail1_sticker_layout);
	            holder.stickerImageView		= (ImageView) hView.findViewById(R.id.frienddetail1_sticker_image);
	            //time
	            holder.timeTextView			= (TextView) hView.findViewById(R.id.frienddetail1_time_text);
	            //delivery
	            holder.deliveryLayout		= (RelativeLayout) hView.findViewById(R.id.frienddetail1_delivery_layout);  
	            holder.deliveryImageView	= (ImageView) hView.findViewById(R.id.frienddetail1_delivery_image);
	           
	            // FRIEND
	            holder.friendBubble			= (RelativeLayout) hView.findViewById(R.id.friendBubble);
	            //user
	            holder.usernameTextViewf	= (TextView) hView.findViewById(R.id.frienddetail_name_text_f);
	            //holder.userImageViewf		= (ImageView) hView.findViewById(R.id.frienddetail_user_image_f);
	            //text
	            holder.textLayoutf			= (RelativeLayout) hView.findViewById(R.id.frienddetail_text_layout_f);
 				holder.messageTextViewf		= (TextView) hView.findViewById(R.id.frienddetail_message_text_f);
	            //image
	            holder.imageLayoutf			= (RelativeLayout) hView.findViewById(R.id.frienddetail_image_layout_f);  
	            holder.dataImageViewf		= (ImageView) hView.findViewById(R.id.frienddetail_message_image_f);
	            holder.progressBarf			= (ProgressBar) hView.findViewById(R.id.progressLoader);
	            //file
	            holder.fileLayoutf			= (RelativeLayout) hView.findViewById(R.id.message_file_layout_f);
	            holder.filenameTextViewf	= (TextView) hView.findViewById(R.id.frienddetail_file_textview_f);
	            holder.fileDownloadButtonf	= (ImageButton) hView.findViewById(R.id.message_file_download_button_f);
	            //map
	            holder.mapLayoutf			= (RelativeLayout) hView.findViewById(R.id.frienddetail_map_layout_f);
	            holder.mapTextViewf			= (TextView) hView.findViewById(R.id.frienddetail_map_text_f);
	            holder.mapImageViewf			= (ImageView) hView.findViewById(R.id.frienddetail_map_image_f);
	            //sticker
	            holder.stickerLayoutf		= (RelativeLayout) hView.findViewById(R.id.frienddetail_sticker_layout_f);
	            holder.stickerImageViewf	= (ImageView) hView.findViewById(R.id.frienddetail_sticker_image_f);
	            //time
	            holder.timeTextViewf		= (TextView) hView.findViewById(R.id.frienddetail_time_text_f);
	            
     			hView.setTag(holder);            	
        }
     	else {
     		holder = (ViewHolder) hView.getTag();
     	}
     	
     	// Show User Bubble
     	if(Sessions.getUserId(context).equals(userId)) 
     	{   
     		holder.friendBubble.setVisibility(View.GONE);
     		holder.userBubble.setVisibility(View.VISIBLE);
     		
     		if(item.isChecked())
     		{
     			hView.setSelected(true);
     			hView.setBackgroundColor(context.getResources().getColor(R.color.holo_blue_dark));
     		}
     		else
     		{
     			hView.setSelected(false);
     			hView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
     		}
     		
     		holder.deliveryLayout.setVisibility(View.VISIBLE);
     		
     		int deliveryStatus = Integer.parseInt(item.getStatus());
     		int resId = R.drawable.status_sending;
     		switch (deliveryStatus) {
			case STATUS_DELIVERY_SENDING:
				resId = R.drawable.status_sending;
				break;
			case STATUS_DELIVERY_SENT:
				resId = R.drawable.status_sent;
				break;
			case STATUS_DELIVERY_DELIVERED:
				resId = R.drawable.status_deleivered;
				break;
				
			default:
				break;
			}
     		
     		holder.deliveryImageView.setImageResource(resId);
     		
     		try {
    	    	
    	    	String url = "";
        		url = Constant.URL + Constant.FOLDER_IMAGES + item.getUserImage();
    	    	
    	    	if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_TEXT) {
    	    		
    	    		holder.textLayout.setVisibility(View.VISIBLE);
    	    		holder.imageLayout.setVisibility(View.GONE);
    	    		holder.fileLayout.setVisibility(View.GONE);
    	    		holder.mapLayout.setVisibility(View.GONE);
    	    		holder.stickerLayout.setVisibility(View.GONE);
    	    		
    	    		holder.messageTextView.setText(""+item.getMessage());
    	    		holder.timeTextView.setText("");
    	    	}
    	    	else if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_IMAGE) {
    	    		
    	    		holder.textLayout.setVisibility(View.GONE);
    	    		holder.imageLayout.setVisibility(View.VISIBLE);
    	    		holder.fileLayout.setVisibility(View.GONE);
    	    		holder.mapLayout.setVisibility(View.GONE);
    	    		holder.stickerLayout.setVisibility(View.GONE);
    	    		
    	    		holder.timeTextView.setText("");
    	    		url = Constant.URL + Constant.FOLDER_IMAGES + item.getMessage();
    	    		if(item.getImageType()==1) {
    	    			 holder.dataImageView.setImageBitmap(item.getBitmap());
    	    		}
    	    		else
    	    		{
    	    			if(item.getBitmap()!=null)
    	    				imageLoader.displayImage(url, holder.dataImageView, false, 200, item.getBitmap());
    	    			else
    	    				imageLoader.displayImage(url, holder.dataImageView, false, 200);
    	    		}
    	    	}
    	    	else if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_FILE) {
    	    		
    	    		holder.textLayout.setVisibility(View.GONE);
    	    		holder.imageLayout.setVisibility(View.GONE);
    	    		holder.fileLayout.setVisibility(View.VISIBLE);
    	    		holder.mapLayout.setVisibility(View.GONE);
    	    		holder.stickerLayout.setVisibility(View.GONE);
    	    		
    	    		holder.filenameTextView.setText(""+item.getMessage());
    	    	}
    	    	else if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_MAP) {
    	    		
    	    		holder.textLayout.setVisibility(View.GONE);
    	    		holder.imageLayout.setVisibility(View.GONE);
    	    		holder.fileLayout.setVisibility(View.GONE);
    	    		holder.mapLayout.setVisibility(View.VISIBLE);
    	    		holder.stickerLayout.setVisibility(View.GONE);
    	    		
    	    		//holder.mapTextView.setText(""+item.getMessage());
    	    		holder.mapTextView.setText("");
    	    		
    	    		String message = list.get(position).getMessage();
    				if(message.contains(","))
    				{
    					String[] msg = message.split(",");
    					String latitude = msg[0];
    					String longitude = msg[1];
    					String imageUrl = getImageUrlFromLatLon(latitude, longitude);
    					imageLoader.displayImage(imageUrl, holder.mapImageView, false, 200);    					
    				}	
    	    	}
    	    	else if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_STICKER) {
    	    		
    	    		holder.textLayout.setVisibility(View.GONE);
    	    		holder.imageLayout.setVisibility(View.GONE);
    	    		holder.fileLayout.setVisibility(View.GONE);
    	    		holder.mapLayout.setVisibility(View.GONE);
    	    		holder.stickerLayout.setVisibility(View.VISIBLE);
    	    		
    	    		String imageName = item.getMessage();
    	    		/*
    	    		DbSticker dbSticker = new DbSticker(context);
    	     		if (dbSticker.isImagePresent(Integer.parseInt(item.getId()))) {
    	            	Log.i(TAG, "Image has been already downloaded. No need to download again.");
    	            	String imageName = item.getImage()+item.getExtension();
    	            	*/
    	            	Log.i(TAG, "imageName : "+imageName);
    	            	FileUtility fileUtility = new FileUtility(context);
    	            	if(imageName!=null) {
    		            	if(fileUtility.isStickerPresent(imageName)) { // check if image is present in the folder.
    		            		Log.i(TAG, "sticker present : "+imageName);
    		            		Bitmap sticker = fileUtility.getStickerImage(imageName);
    		            		if(sticker!=null)
    		            			holder.stickerImageView.setImageBitmap(sticker);
    		            		else
    		            			Log.e(TAG, "sticker null ");
    		            	}
    		            	else
    		            	{
    		            		Log.e(TAG, "sticker NOT present : "+imageName);
    		            	}
    	            	}
    	     		//}
    	    		
    	    		//holder.stickerImageView.setImageResource(R.drawable.camera);
    	    	}
    	    	
        	} catch (Exception e) {
              	e.printStackTrace();
        	}
     	}
     	
     	else  // Show Friend Bubble
     	{     		
     		holder.friendBubble.setVisibility(View.VISIBLE);
     		holder.userBubble.setVisibility(View.GONE);
     		
		    try {		    	
		    	String url = "";
	    		url = Constant.URL + Constant.FOLDER_IMAGES + item.getUserImage();
		    	
		    	if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_TEXT) {
		    		
		    		holder.textLayoutf.setVisibility(View.VISIBLE);
    	    		holder.imageLayoutf.setVisibility(View.GONE);
    	    		holder.fileLayoutf.setVisibility(View.GONE);
    	    		holder.mapLayoutf.setVisibility(View.GONE);
    	    		holder.stickerLayoutf.setVisibility(View.GONE);
		    		
		    		holder.messageTextViewf.setText(""+item.getMessage());
		    		holder.timeTextViewf.setText("");
		    	}
		    	else if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_IMAGE) {
		    		
		    		holder.textLayoutf.setVisibility(View.GONE);
    	    		holder.imageLayoutf.setVisibility(View.VISIBLE);
    	    		holder.fileLayoutf.setVisibility(View.GONE);
    	    		holder.mapLayoutf.setVisibility(View.GONE);
    	    		holder.stickerLayoutf.setVisibility(View.GONE);
    	    		
		    		holder.timeTextViewf.setText("");
		    		url = Constant.URL + Constant.FOLDER_IMAGES + item.getMessage();
		    		if(item.getImageType()==1) {
		    			 holder.dataImageViewf.setImageBitmap(item.getBitmap());
		    		}
		    		else
		    		{
		    			imageLoader.displayImage(url, holder.dataImageViewf, false, null);
		    		}
		    	}
		    	else if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_FILE) {
    	    		
		    		holder.textLayoutf.setVisibility(View.GONE);
    	    		holder.imageLayoutf.setVisibility(View.GONE);
    	    		holder.fileLayoutf.setVisibility(View.VISIBLE);
    	    		holder.mapLayoutf.setVisibility(View.GONE);
    	    		holder.stickerLayoutf.setVisibility(View.GONE);
    	    		
    	    		holder.filenameTextViewf.setText(""+item.getMessage());
    	    		
    	    		File mainFolder = new File(Environment.getExternalStorageDirectory(),  context.getResources().getString(R.string.app_name));
    	            
    				if (!mainFolder.exists()) {
    	            	mainFolder.mkdir();
    				}
    				
    	            File filesFolder = new File(mainFolder, FileUtility.FOLDER_FILES);
    	            
    	            if (!filesFolder.exists()) {
    	            	filesFolder.mkdir();
    				}
	    	        
	    	        File file = new File(filesFolder, item.getMessage());
		    	    if(file.exists()) {
		    	    	holder.fileDownloadButtonf.setVisibility(View.GONE);
		    	    }
		    	    else
		    	    {
		    	    	holder.fileDownloadButtonf.setVisibility(View.VISIBLE);
		    	    }
		    	    
    	    		holder.fileDownloadButtonf.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							
							Log.e(TAG, "Clicked");
							
							if(connectionDetector.isConnectedToInternet())
							{								
								Intent intent = new Intent(context.getApplicationContext(), DownLoadFileService.class);
								String url = Constant.URL + Constant.FOLDER_IMAGES + item.getMessage();
								intent.putExtra(Constant.NAME, item.getMessage());
								intent.putExtra(Constant.URL, url);
								intent.putExtra(Constant.PATH, "files");
								intent.putExtra(Constant.VALUE, "1");
								context.startService(intent);
							}
							else
							{
								showToast(""+context.getResources().getString(R.string.internet_lost));
							}
							
							
						}
					});
    	    	}
		    	else if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_MAP) {
    	    		
    	    		holder.textLayoutf.setVisibility(View.GONE);
    	    		holder.imageLayoutf.setVisibility(View.GONE);
    	    		holder.fileLayoutf.setVisibility(View.GONE);
    	    		holder.mapLayoutf.setVisibility(View.VISIBLE);
    	    		holder.stickerLayoutf.setVisibility(View.GONE);
    	    		
    	    		//holder.mapTextViewf.setText(""+item.getMessage());
    	    		holder.mapTextViewf.setText("");
    	    		
    	    		String message = list.get(position).getMessage();
    				if(message.contains(","))
    				{
    					String[] msg = message.split(",");
    					String latitude = msg[0];
    					String longitude = msg[1];
    					String imageUrl = getImageUrlFromLatLon(latitude, longitude);
    					imageLoader.displayImage(imageUrl, holder.mapImageViewf, false, 200);    					
    				}
    	    	}
		    	else if(Integer.parseInt(item.getType())==FriendDetailAdapter.TYPE_STICKER) {
    	    		
    	    		holder.textLayoutf.setVisibility(View.GONE);
    	    		holder.imageLayoutf.setVisibility(View.GONE);
    	    		holder.fileLayoutf.setVisibility(View.GONE);
    	    		holder.mapLayoutf.setVisibility(View.GONE);
    	    		holder.stickerLayoutf.setVisibility(View.VISIBLE);
    	    		
    	    		String imageName = item.getMessage();
    	    		Log.i(TAG, "imageName : "+imageName);
	            	FileUtility fileUtility = new FileUtility(context);
	            	if(imageName!=null) {
		            	if(fileUtility.isStickerPresent(imageName)) { // check if image is present in the folder.
		            		Log.i(TAG, "sticker present : "+imageName);
		            		Bitmap sticker = fileUtility.getStickerImage(imageName);
		            		if(sticker!=null)
		            			holder.stickerImageViewf.setImageBitmap(sticker);
		            		else
		            			Log.e(TAG, "sticker null ");
		            	}
		            	else
		            	{
		            		Log.e(TAG, "sticker NOT present : "+imageName);
		            	}
	            	}   
    	    	}
		    	
	    	} catch (Exception e) {
	          	e.printStackTrace();
	    	}
     	}
      	return hView;
	}	
	
	class ViewHolder
	{	
		// ME
			RelativeLayout userBubble;
			//user
			TextView usernameTextView;
			ImageView userImageView;
			//text
			RelativeLayout textLayout;
			TextView messageTextView;
			//image
			RelativeLayout imageLayout;
		    ImageView dataImageView;		    
		    ProgressBar progressBar;
		    //file
		    RelativeLayout fileLayout;
		    TextView filenameTextView;
		    //map
		    RelativeLayout mapLayout;
		    TextView mapTextView;
		    ImageView mapImageView;
		    //sticker
		    RelativeLayout stickerLayout;
		    ImageView stickerImageView;
		    //time
		    RelativeLayout timeLayout;
		    TextView timeTextView;
		    //delivery
			RelativeLayout deliveryLayout;
		    ImageView deliveryImageView;	
		    
		//FRIEND    
		    RelativeLayout friendBubble;		
		    //user
			TextView usernameTextViewf;
			ImageView userImageViewf;
			//text
			RelativeLayout textLayoutf;
			TextView messageTextViewf;
			//image
			RelativeLayout imageLayoutf;
		    ImageView dataImageViewf;		    
		    ProgressBar progressBarf;
		    //file
		    RelativeLayout fileLayoutf;
		    TextView filenameTextViewf;  
		    ImageButton fileDownloadButtonf;
		    //map
		    RelativeLayout mapLayoutf;
		    TextView mapTextViewf;
		    ImageView mapImageViewf;
		    //sticker
		    RelativeLayout stickerLayoutf;
		    ImageView stickerImageViewf;
		    //time
		    RelativeLayout timeLayoutf;
		    TextView timeTextViewf;
		}
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}		
    
    protected void showToastLong(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
	}
	
	private String getImageUrlFromLatLon(String latitude, String longitude)
	{
		String getMapURL = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=560x240&markers=size:large|color:red|"  
				+ latitude
				+ "," 
				+ longitude
				+ "&sensor=false";
		return getMapURL;
	}
	
}   