package me.zakeer.justchat.interfaces;

public interface OnUploadCompleteListener {
	void onUploadBegin();
	void onUploadComplete(boolean isComplete, String message);
}
