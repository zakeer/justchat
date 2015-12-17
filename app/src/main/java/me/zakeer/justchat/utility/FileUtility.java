package me.zakeer.justchat.utility;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import me.zakeer.justchat.R;

public class FileUtility {
	
	private Context context;
	
	File mainFolder;
	
	private static String APP_FOLDER = "FreshIM";
	
	public static final String FOLDER_STICKER = "Sticker";
	public static final String FOLDER_FILES = "Files";
	public static final String FOLDER_IMAGES = "Images";
	
	public static final String FOLDER_TEMP = "temp";
	
	public static final String IMAGE_NAME_TEMPORARY = "temp.jpg";
	
	
	
	public FileUtility(Context context) {
		this.context = context;
		APP_FOLDER = context.getResources().getString(R.string.app_name);		
		File appFolder = new File(Environment.getExternalStorageDirectory(), APP_FOLDER);
		if(!appFolder.exists())
		{
			appFolder.mkdir();
		}
		mainFolder = appFolder;
	}
	
	public File getAppFolder() {
		APP_FOLDER = context.getResources().getString(R.string.app_name);
		File appFolder = new File(Environment.getExternalStorageDirectory(), APP_FOLDER);
		if(!appFolder.exists())
		{
			appFolder.mkdir();
		}
		return appFolder;
	}
	
	public boolean isStickerPresent(String name) {
		
		mainFolder = getAppFolder();
		File stickerFolder = new File(mainFolder, FOLDER_STICKER);
		if(stickerFolder.exists())
		{
			File sticker = new File(stickerFolder, name);
			return sticker.exists();
		}
		return false;
	}
	
	public Bitmap getStickerImage(String name) {
		File stickerFolder = new File(mainFolder, FOLDER_STICKER);
		if(stickerFolder.exists())
		{
			File sticker = new File(stickerFolder, name);
			return decodeImage(sticker);
		}
		return null;
	}
	
	private Bitmap decodeImage(File f) {
    	return BitmapFactory.decodeFile(f.getAbsolutePath());		
    }
	
	public int getLengthOfFile(String path) {
		if(path!=null) {
			File file = new File(path);
			if (file.exists()) {				
				if (file.isFile()) {
					return (int) file.length();
				}
			}
		}
		return 0;
	}
	
	public File getTempJpgImageFile() {
		String name = IMAGE_NAME_TEMPORARY;
		File mainFolder = getAppFolder();
		File tempFolder = new File(mainFolder, FOLDER_TEMP);
		if(!tempFolder.exists())
			tempFolder.mkdir();
		File imageFile = new File(tempFolder, name);
		return imageFile;
	}
	
	public boolean deleteTempFolder() {
		File mainFolder = getAppFolder();
		File tempFolder = new File(mainFolder, FOLDER_TEMP);
		if(tempFolder.exists())
		{
			return tempFolder.delete();
		}
		return true;
	}
	

}
