package me.zakeer.justchat.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import me.zakeer.justchat.R;
import me.zakeer.justchat.database.DbSticker;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.items.StickerItem;
import me.zakeer.justchat.sessions.SessionSticker;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.Base64;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.FileUtility;

public class LoadFileService extends Service {
	
	protected static final String TAG = "LoadFileService";
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
	
	public static final int MAX_FILE_LENGTH = 1000 * 1000 * 1; // 1MB
	
	public LoadFileService() {
		
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
		imageName = extras.getString(Constant.NAME);
		imagePath = extras.getString(Constant.PATH);
		value = extras.getString(Constant.VALUE);
		
		if(value.equals(VALUE_MESSAGE)) {
			friendId = extras.getString(Constant.FRIEND_ID);
			message = extras.getString(Constant.MESSAGE);
			type = extras.getString(Constant.TYPE);
			position = extras.getInt(Constant.POSITION);
			data = extras.getString(Constant.DATA);
		}
		
		Log.e("IN CHECK", "START");
		//check();
		onUploadStart(imageName, imagePath);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void onUploadStart(String name, String imagePath) {
		
		if(imagePath!=null)
		{
			FileUtility fileUtility = new FileUtility(getApplicationContext());
			int fileLength = fileUtility.getLengthOfFile(imagePath);
			Log.e(TAG, "length : "+fileLength);
			if(fileLength<=MAX_FILE_LENGTH)
			{
				if(fileLength!=0)
				{
					String content = getStringFromFile(imagePath);
					
					if(content!=null) {
						//String filename = "upload_file";
						String filename = "upload";
						asyncLoadVolley = new AsyncLoadVolley(this, filename);
						Map<String, String> map = new HashMap<String, String>();
						map.put(Constant.CONTENT, content);
						map.put(Constant.NAME, name);
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
				else
				{
					Toast.makeText(getApplicationContext(), "Choose a valid file.", Toast.LENGTH_SHORT).show();
					Log.e(TAG, "Length of file is 0.");
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Maximum 1Mb of File can be uploaded..!!", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "Length of file is greater than 1mb. Filesize = "+fileLength);
			}
		}
	}
	
	OnAsyncTaskListener uploadAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "value : "+value+", mess : "+message);
			if(value.equals("1"))
			{
				Intent intent = new Intent(Constant.IMAGE);
				intent.putExtra(Constant.STATUS, true);
				intent.putExtra(Constant.POSITION, position);
				intent.putExtra(Constant.IMAGE, imageName);
				//sendBroadcast(intent);
				onUploadComplete();
			}
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};
	
	public String getStringFromFile(String path) {
		
		File file = new File(path);
		String text = "";
		byte[] byte_arr = new byte[(int) file.length()];
		
		try {
			 FileInputStream fileInputStream = new FileInputStream(file);
			 fileInputStream.read(byte_arr);
            
            for (int i = 0; i < byte_arr.length; i++) {
                System.out.print((char)byte_arr[i]);
            }
            
            text = Base64.encodeBytes(byte_arr);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		 return text;
	}
	
	/*
	private String getImageFromPath(String imagePath) {
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
        return null;
	}	
	*/
	
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
	
	
		/**
		 * Background Async Task to upload file
		 * */
		class DownloadStickersFromURL extends AsyncTask<String, String, String> {
			
		    /**
		     * Before starting background thread
		     * Show Progress Bar Dialog
		     * */
		    @Override
		    protected void onPreExecute() {
		        super.onPreExecute();
		    }
		    
		    /**
		     * Uploading file in background thread
		     * */
		    @Override
		    protected String doInBackground(String... response) {
		        int count;
		        try {
		        	AsyncResponse asyncResponse = new AsyncResponse(response[0]);
					if(asyncResponse.ifSuccess())
					{
						List<StickerItem> list = asyncResponse.getStickerList();
						
						File mainFolder = new File(Environment.getExternalStorageDirectory(),  getResources().getString(R.string.app_name));
			            
						if (!mainFolder.exists()) {
			            	mainFolder.mkdir();
						}
						
			            File stickerFolder = new File(mainFolder, FileUtility.FOLDER_STICKER);
			            
			            if (!stickerFolder.exists()) {
			            	stickerFolder.mkdir();
						}
						
						for (int i = 0; i < list.size(); i++) {
								
							StickerItem details = list.get(i);
							Log.i(TAG, details.getId() + ", "+details.getImage()+", "+details.getExtension() + ", "+details.getCategory());
							
				            String imageName = details.getImage() + details.getExtension();
				            Log.e(TAG, "imageName : "+imageName);
				            
				            File imageFile = new File(stickerFolder, imageName);
				            
				            DbSticker dbSticker = new DbSticker(getApplicationContext());
				            
				            boolean downloadImage = true;
				            if (dbSticker.isImagePresent(Integer.parseInt(details.getId()))) {
				            	Log.v(TAG, "Image has been already downloaded. No need to download again.");
				            	if(imageFile.exists()) // check if image is present in the folder.
				            	{
				            		downloadImage = false; // donot download the image
				            	}
							}
							
				            if(downloadImage)
				            {
				            
								String imageUrl = Constant.URL + Constant.FOLDER + Constant.FOLDER_STICKER + imageName;
								
					        	Log.e("IN CHECK", "imageUrl : "+imageUrl);
					            URL url = new URL(imageUrl);
					            URLConnection conection = url.openConnection();
					            conection.connect();
					            // getting file length
					            int lenghtOfFile = conection.getContentLength();
					            
					            // input stream to read file - with 8k buffer
					            InputStream input = new BufferedInputStream(url.openStream(), 8192);
					            
					            
					            // Output stream to write file
					            OutputStream output = new FileOutputStream(imageFile);
					            
					            byte data[] = new byte[1024];
					            
					            long total = 0;
					            
					            while ((count = input.read(data)) != -1) {
					                total += count;
					                // publishing the progress....
					                // After this onProgressUpdate will be called
					                publishProgress(""+(int)((total*100)/lenghtOfFile));
					                
					                // writing data to file
					                output.write(data, 0, count);
					            }
		
					            details.setTime(""+new Date().getTime());
					            if (dbSticker.isImagePresent(Integer.parseInt(details.getId()))) {			            	
					            	dbSticker.updateTable(details);
								}
					            else
					            {
						            dbSticker.insertInTable(details);
					            }
					            
					            List<StickerItem> locallist = dbSticker.getAllDetails();
					            if(locallist!=null)
					            {
					            for (int x = 0; x < locallist.size(); x++) {
									
									StickerItem localdetails = list.get(x);
									Log.v(TAG, localdetails.getId() + ", "+localdetails.getImage()+", "+localdetails.getExtension() + ", "+localdetails.getCategory()+", "+localdetails.getTime());
					            }
					            
					            }
					            Log.i(TAG, imageName + " download complete.");
					            
					            // flushing output
					            output.flush();
					            
					            // closing streams
					            output.close();
					            input.close();		 
				            }
						}
						
						DbSticker dbSticker = new DbSticker(getApplicationContext());
						
						Log.e(TAG, "db count : "+dbSticker.getCount());
						Log.e(TAG, "list.size(): "+list.size());
						if (list.size()==dbSticker.getCount()) {
							SessionSticker.setAllStickers(getApplicationContext(), true);
						}
						
					}	
					else
					{
						Log.e(TAG, "Error : "+asyncResponse.getMessage());
					}
		            
		        } catch (Exception e) {
		            Log.e("Error: ", e.getMessage());
		        }	 
		        return null;
		    }
		    	    
		    /**
		     * Updating progress bar
		     * */
		    protected void onProgressUpdate(String... progress) {
		    		    	
		        // setting progress percentage
		        //pDialog.setProgress(Integer.parseInt(progress[0]));
		    	//Toast.makeText(getApplicationContext(), "Progress : " + progress[0], Toast.LENGTH_SHORT).show();
		   }
		    
		    /**
		     * After completing background task
		     * Dismiss the progress dialog
		     * **/
		    @Override
		    protected void onPostExecute(String file_url) {
		    	
		    	if (SessionSticker.isAllStickersSet(getApplicationContext())) {
			    	stopSelf();				
				}	        
		    }	 
		}
		
	
	
/////
		
	@Override
	public void onDestroy() {
		Log.w("mine", "CLICKED");
		super.onDestroy();
	}
	
}
