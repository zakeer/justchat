package me.zakeer.justchat.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import me.zakeer.justchat.utility.Constant;

public class SessionUserImage {
	
	private static final String KEY = Constant.USER_IMAGE;
		
	public static boolean clear(Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
        return editor.commit();
	}
	
	public static boolean isImageSet(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(Constant.STATUS, false);
	}
		
	public static String getImageName(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return preferences.getString(Constant.NAME, "");
	}
	
	public static String getImagePath(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return preferences.getString(Constant.PATH, "");
	}
	
	public static boolean setImage(Context context, String name, String path) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(Constant.NAME, name);
		editor.putString(Constant.PATH, path);
		editor.putBoolean(Constant.STATUS, true);
        return editor.commit();		
	}
}	