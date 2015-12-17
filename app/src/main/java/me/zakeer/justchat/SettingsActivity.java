package me.zakeer.justchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import me.zakeer.justchat.utility.Constant;

public class SettingsActivity extends SherlockActivity {

protected static final String TAG = "SettingsActivity";
	
	private Context context = SettingsActivity.this;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		getSupportActionBar().setTitle("Account Settings");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(Constant.ACTIONBAR_ICON);
	}
	
	public void onChangeStatusClick(View view) {
		Intent intent = new Intent();
		intent.setClass(context, StatusActivity.class);
    	startActivity(intent);
	}
	
	public void onChangePasswordClick(View view) {
		Intent intent = new Intent();
		intent.setClass(context, EditPasswordActivity.class);
    	startActivity(intent);
	}
	
	public void onEditProfileClick(View view) {
		Intent intent = new Intent();
		intent.setClass(context, EditProfileActivity.class);
    	startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
			switch (item.getItemId()) {
				
			case android.R.id.home:
	        	finish();
	        return true;
	        
			default:
				return super.onOptionsItemSelected(item);
			}
	}
}
