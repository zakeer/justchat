
package me.zakeer.justchat.imagecache;

import java.io.File;

import android.content.Context;
import android.util.Log;

import me.zakeer.justchat.utility.FileUtility;

public class FileCache {
    
    private static final String TAG = "FileCache";
	private File cacheDir;
    
    public FileCache(Context context) {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
        	
        	FileUtility fileUtility = new FileUtility(context);
        	File appFolder = fileUtility.getAppFolder();
            cacheDir=new File(appFolder, FileUtility.FOLDER_IMAGES);
        }
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }
    
    public File getFile(String url) {
    	
    	Log.e(TAG, "url : "+url);
    	String finalUrl = url;
    	
    	if(url.contains("/"))
    	{
    		finalUrl = url.substring(url.lastIndexOf("/") +1);
    	}
    	Log.e(TAG, "finalUrl : "+finalUrl);
    	
        String filename= finalUrl;//String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;        
    }
    
    public void clear() {
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
}