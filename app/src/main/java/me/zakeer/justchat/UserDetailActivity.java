package me.zakeer.justchat;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import me.zakeer.justchat.imagecache.ImageLoaderNew;
import me.zakeer.justchat.utility.Constant;

public class UserDetailActivity extends SherlockFragmentActivity {
	
	private static final String TAG = "UserDetailActivity";
	
	private Context context = UserDetailActivity.this;
	
	private ImageViewTouch userImageView;
	
	private ImageLoaderNew imageLoader;
	
	private String name = "User", image = "";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdetail);
        
        Log.d(TAG, "On Create");
        
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(Constant.ACTIONBAR_ICON);
		
		Bundle bundle = getIntent().getExtras();
		name = "Image"; 
		image = bundle.getString(Constant.IMAGE);
		
		getSupportActionBar().setTitle(name);
		
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		userImageView = new ImageViewTouch(context);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		userImageView.setLayoutParams(layoutParams);
		viewGroup.addView(userImageView);
		userImageView.setDisplayType(DisplayType.FIT_TO_SCREEN);
		
		imageLoader = new ImageLoaderNew(context);
		String url = Constant.URL + Constant.FOLDER + Constant.FOLDER_IMAGES + image;
		imageLoader.displayImage(url, userImageView, false, 300);		
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