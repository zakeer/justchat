package me.zakeer.justchat.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

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

import me.zakeer.justchat.R;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.FileUtility;

public class DownLoadFileService extends Service {
	
	protected static final String TAG = "DownLoadFileService";
	private Handler handler;
	SharedPreferences sharedPreferences;
	InputStream inputStream;
	private String imageName, imageUrl, imagePath;
	private String value;
	
	public static final String VALUE_MESSAGE = "1";
	
	public DownLoadFileService() {
		
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
		imageUrl = extras.getString(Constant.URL);
		imagePath = extras.getString(Constant.PATH);
		value = extras.getString(Constant.VALUE);
		
		Log.e("IN CHECK", "START");
		
		onDownloadStart(imageUrl, imagePath);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void onDownloadStart(final String imageUrl, String imagePath) {
		
			handler.post(new Runnable() {
			    public void run()
			    {
			    	new DownloadFileFromURL().execute(imageUrl);
			    }
			});		
	}
	
	/**
	 * Background Async Task to download file
	 * */
	class DownloadFileFromURL extends AsyncTask<String, String, String> {
		
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
	    protected String doInBackground(String... f_url) {
	        int count;
	        try {
	        	
	        	Log.e("IN CHECK", "url : "+f_url[0]);
	            URL url = new URL(f_url[0]);
	            URLConnection conection = url.openConnection();
	            conection.connect();
	            // getting file length
	            int lenghtOfFile = conection.getContentLength();
	            
	            // input stream to read file - with 8k buffer
	            InputStream input = new BufferedInputStream(url.openStream(), 8192);
	            
	            File mainFolder = new File(Environment.getExternalStorageDirectory(),  getResources().getString(R.string.app_name));
	            
				if (!mainFolder.exists()) {
	            	mainFolder.mkdir();
				}
				
	            File filesFolder = new File(mainFolder, FileUtility.FOLDER_FILES);
	            
	            if (!filesFolder.exists()) {
	            	filesFolder.mkdir();
				}
	            	            
	            File file = new File(filesFolder, imageName);
	            // Output stream to write file
	            OutputStream output = new FileOutputStream(file);
	            
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
	            
	            // flushing output
	            output.flush();
	            
	            // closing streams
	            output.close();
	            input.close();
	            
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
	        // dismiss the dialog after the file was downloaded
	    	
	    	Intent intent = new Intent(Constant.BROADCAST_REFRESH);
			sendBroadcast(intent);
	    	
	    	
	    	stopSelf();
	    	
	    	
	    	/*
	        // Displaying downloaded image into image view
	        // Reading image path from sdcard
	        String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
	        // setting downloaded into image view
	        my_image.setImageDrawable(Drawable.createFromPath(imagePath));
	        */
	        
	    }	 
	}
		
	@Override
	public void onDestroy() {
		Log.w("mine", "CLICKED");
		super.onDestroy();
	}
	
}
