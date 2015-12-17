package me.zakeer.justchat.interfaces;

public interface OnAsyncTaskListener {
	void onTaskBegin();
	void onTaskComplete(boolean isComplete, String message);
}
