package me.zakeer.justchat.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import me.zakeer.justchat.utility.Constant;

public class SessionLastSeen {
	
	private static final String KEY = Constant.LAST_SEEN;
	
	public static final String TYPE_IN 	= "in";
	public static final String TYPE_OUT = "out";
		
	public static boolean clear(Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
        return editor.commit();
	}
	
	public static String getType(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return preferences.getString(Constant.TYPE, "");
	}
	
	public static boolean setType(Context context, String type) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(Constant.TYPE, type);
        return editor.commit();		
	}
	
	
	public static String getFriendId(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return preferences.getString(Constant.FRIEND_ID, "");
	}
	
	public static boolean setFriendId(Context context, String friendId) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(Constant.FRIEND_ID, friendId);
        return editor.commit();		
	}
	
}	