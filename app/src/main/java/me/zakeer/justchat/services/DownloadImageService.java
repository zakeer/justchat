package me.zakeer.justchat.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import me.zakeer.justchat.R;
import me.zakeer.justchat.database.DbSticker;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.items.StickerItem;
import me.zakeer.justchat.sessions.SessionSticker;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.FileUtility;

public class DownloadImageService extends Service {
	
	protected static final String TAG = "DownloadImageService";
	private Handler handler;
	
	public DownloadImageService() {
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		handler = new Handler();
		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.e(TAG, "Service START");
		
		getStickerList();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void getStickerList() {
		String filename = getResources().getString(R.string.getstickerlist_php);
		AsyncLoadVolley asyncLoadVolley = new AsyncLoadVolley(this, filename);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.ID, "1");
		asyncLoadVolley.setBasicNameValuePair(map);
		asyncLoadVolley.setOnAsyncTaskListener(asyncTaskListener);
		asyncLoadVolley.beginTask();
	}
	
	OnAsyncTaskListener asyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "mess : "+message);
			AsyncResponse asyncResponse = new AsyncResponse(message);
			if(asyncResponse.ifSuccess())
			{
				List<StickerItem> list = asyncResponse.getStickerList();
				for (int i = 0; i < list.size(); i++) {
					Log.e(TAG, list.get(i).getImage()+", "+list.get(i).getExtension() + ", "+list.get(i).getCategory());
				}
				onDownloadStart(message);
			}	
			else
			{
				Log.e(TAG, "Error : "+asyncResponse.getMessage());
			}
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};
	
	public void onDownloadStart(final String response) {
		
			handler.post(new Runnable() {
			    public void run()
			    {
			    	new DownloadStickersFromURL().execute(response);
			    }
			});	
	}
	
	/**
	 * Background Async Task to download file
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
	     * Downloading file in background thread
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
		Log.w(TAG, "Service has STOPPED");
		super.onDestroy();
	}
	
}
