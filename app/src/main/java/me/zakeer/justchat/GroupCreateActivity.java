package me.zakeer.justchat;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.FileUtility;
import me.zakeer.justchat.utility.ImageCustomize;
import me.zakeer.justchat.utility.Validate;

public class GroupCreateActivity extends SherlockActivity {

protected static final String TAG = "CreateGroupActivity";
	
	private Context context = GroupCreateActivity.this;
    
	EditText groupName;
    ImageView groupImage;
    
    private String name;
    
    //private Bitmap bitmap;
    private String path;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.group_create);
		
		getSupportActionBar().setTitle("Create Group");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(Constant.ACTIONBAR_ICON);
        
        groupName = (EditText) findViewById(R.id.groupName);
        groupImage = (ImageView) findViewById(R.id.groupImage);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.status, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getSupportMenuInflater().inflate(R.menu.status, menu);
		MenuItem item = menu.findItem(R.id.savestatus);
		item.setTitle("Next");
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
				
			switch (item.getItemId()) {
				
			case android.R.id.home:
	        	finish();
	        return true;
	        
			case R.id.savestatus:
	        	onNext();
	        return true;
	        
			default:
				return super.onOptionsItemSelected(item);
			}
	}
	
	private void onNext() {
		
		boolean check = true;
		Validate validate = new Validate();
		
		name = groupName.getText().toString();
		
		if(validate.isNotEmpty(name))
		{
			name = groupName.getText().toString();
		}
		else
		{
			name = "";
			check = false;
			groupName.setError("Enter a group name.");
		}
		
		if(check)
		{
			Intent intent=new Intent();
			intent.setClass(context, GroupChooseFriendListActivity.class);
			if(path==null) 
				path="";
			intent.putExtra(Constant.IMAGEPATH, path);
			intent.putExtra(Constant.NAME, name);
			startActivityForResult(intent, Constant.FINISH_BACK_ACTIVITY_INT_REQUEST_CODE);
		}
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		
		if(!groupName.getText().toString().equals(""))
			name = groupName.getText().toString();
		else
			name = "";
		outState.putString(Constant.NAME, name);
		super.onSaveInstanceState(outState);
	};
	
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);	
		name = savedInstanceState.getString(Constant.NAME);
		setDetails(name);
	};
	
	private void setDetails(String name) {
		groupName.setText(""+name);
	}
	
	/////
	
	private static final int CAMERA_REQUEST = 1;
	private static final int RESULT_LOAD_IMAGE = 2;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode==Constant.FINISH_BACK_ACTIVITY_INT_RESULT_CODE && requestCode==Constant.FINISH_BACK_ACTIVITY_INT_REQUEST_CODE) {
            finish();
        }  
		
		if (resultCode != RESULT_OK)
			return;
		
		switch (requestCode) {
		case CAMERA_REQUEST:
			
			if (resultCode == Activity.RESULT_OK)
			{
				FileUtility fileUtility = new FileUtility(context);    	
		    	File imageFile = fileUtility.getTempJpgImageFile();
				
				String path=imageFile.getAbsolutePath();
				File file = new File(path);
				Bitmap bitmap = ImageCustomize.decodeFile(file, 200);
				groupImage.setImageBitmap(bitmap);
				
				this.path = path;
			}
			break;
			
		case RESULT_LOAD_IMAGE:
			{
				 if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			            Uri selectedImage = data.getData();
			            String[] filePathColumn = { MediaStore.Images.Media.DATA };
			            Cursor cursor = getContentResolver().query(selectedImage,
			                    filePathColumn, null, null, null);
			            cursor.moveToFirst();
			            
			            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			            String picturePath = cursor.getString(columnIndex);
			            cursor.close();
			            
			            File file = new File(picturePath);
						Bitmap bitmap = ImageCustomize.decodeFile(file, 200);
			        	groupImage.setImageBitmap(bitmap);
			        	
						this.path = picturePath;
				 	}
			}
			break;
			
		}
	}
	
	public void onUploadClick(View view) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Choose Image Source");
		builder.setItems(new CharSequence[] {"Gallery", "Camera"}, 
		        new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which) {
		        case 0:
		        	callgalery();
		            break;

		        case 1:
		        	callCamera();
		            break;

		        default:
		            break;
		        }
		    }
		});
		
		builder.show();
	}
    
    public void callCamera() {
		
    	FileUtility fileUtility = new FileUtility(context);    	
    	File imageFile = fileUtility.getTempJpgImageFile();
				
		Uri uriSavedImage = Uri.fromFile(imageFile);
		
		Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		i.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage); 
	    startActivityForResult(i, CAMERA_REQUEST);
	        
	}
	
	protected void callgalery()
	{
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
	/////
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}		
    
    protected void showToastLong(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
	}
}
