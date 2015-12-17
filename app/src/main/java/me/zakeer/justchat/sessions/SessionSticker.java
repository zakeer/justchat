package me.zakeer.justchat.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import me.zakeer.justchat.utility.Constant;

public class SessionSticker {
	
	private static final String KEY = Constant.STICKER;
		
	public static boolean clear(Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
        return editor.commit();
	}
	
	public static boolean isAllStickersSet(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(Constant.NAME, false);
	}
	
	public static boolean setAllStickers(Context context, boolean state) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(Constant.NAME, state);
        return editor.commit();		
	}
	
	public static boolean isSomeStickersSet(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return preferences.getBoolean(Constant.VALUE, false);
	}
	
	public static boolean setSomeStickers(Context context, boolean state) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(Constant.VALUE, state);
        return editor.commit();		
	}
}	