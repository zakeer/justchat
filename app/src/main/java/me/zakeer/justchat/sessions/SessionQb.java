package me.zakeer.justchat.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import me.zakeer.justchat.utility.Constant;

public class SessionQb {
	
	private static final String KEY = Constant.QB_SESSION;
	
	public static boolean save(Context context, String id) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(Constant.USER_ID, id);
		editor.putBoolean(Constant.STATUS, true);
        return editor.commit();
	}
	
	public static boolean clear(Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
        return editor.commit();
	}
	
	public static boolean isLoggedIn(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(Constant.STATUS, false);
		
	}
		
	public static String getUserId(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return preferences.getString(Constant.USER_ID, null);
	}
		
	public static boolean setUserId(Context context, String userId) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(Constant.USER_ID, userId);
        return editor.commit();		
	}	
}	