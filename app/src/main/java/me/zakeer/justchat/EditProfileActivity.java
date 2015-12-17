package me.zakeer.justchat;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import me.zakeer.justchat.imagecache.ImageLoader;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.services.LoadImageService;
import me.zakeer.justchat.sessions.SessionUserImage;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.Countries;
import me.zakeer.justchat.utility.FileUtility;
import me.zakeer.justchat.utility.ImageCustomize;
import me.zakeer.justchat.utility.Validate;

public class EditProfileActivity extends SherlockActivity {

protected static final String TAG = "EditProfileActivity";
	
	private Context context = EditProfileActivity.this;
	
	private AsyncLoadVolley asyncLoadVolley;
    
	private EditText txtName, txtLastName, txtPhone, txtEmail, txtStatus;
	private ImageView userImageView;
    private Button btnPhoneCode;
    
    private String fname, lname, phone, phoneCode, email, status, imageName;
    
    private Bitmap bitmap;
    private String path;
    
    private ImageLoader imageLoader;
    
    private boolean newImageSet = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.edit_profile);
		
		getSupportActionBar().setTitle("Edit Profile");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(Constant.ACTIONBAR_ICON);
        
        txtName 		= (EditText) findViewById(R.id.txtNameEdit);
        txtLastName 	= (EditText) findViewById(R.id.txtLastNameEdit);
        txtPhone 		= (EditText) findViewById(R.id.txtPhoneEdit);
        btnPhoneCode	= (Button)	 findViewById(R.id.btnPhoneCodeEdit);
        txtEmail 		= (EditText) findViewById(R.id.txtEmailEdit);
        txtStatus 		= (EditText) findViewById(R.id.txtStatusEdit);
        userImageView 	= (ImageView)findViewById(R.id.userImageEdit);
        
        txtEmail.setEnabled(false);
        txtEmail.setClickable(false);
        
        email = Sessions.getEmail(context);
        txtEmail.setText(""+email);
        
        imageLoader = new ImageLoader(context);
        String url = Constant.URL + Constant.FOLDER_IMAGES + Sessions.getImage(context);
        
        if(bitmap==null || path == null)
        	imageLoader.displayImage(url, userImageView);
        else
        	userImageView.setImageBitmap(bitmap);
		
        if(savedInstanceState==null) 
        {
        	fname = Sessions.getName(context);
        	lname = Sessions.getLname(context);        	
        	status = Sessions.getStatus(context);
        	phone = Sessions.getPhone(context);
        	phoneCode = Sessions.getPhoneCode(context);
        	setDetails(fname, lname, status, phone, phoneCode);
        }   
        
        btnPhoneCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, CountryCodesActivity.class);
				startActivityForResult(intent, Constant.PHONE_CODE_INT);
				Log.e(TAG, Countries.getCurrentCountry(context));				
			}
		});	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.status, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
			
			switch (item.getItemId()) {
				
			case android.R.id.home:
	        	finish();
	        return true;
	        
			case R.id.savestatus:
	        	onSave();
	        return true;
	        
			default:
				return super.onOptionsItemSelected(item);
			}
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		
		if(!txtName.getText().toString().equals(""))			
			fname = txtName.getText().toString();
		else
			fname = "";
		
		if(!txtLastName.getText().toString().equals(""))			
			lname = txtLastName.getText().toString();
		else
			lname = "";
		
		if(!txtStatus.getText().toString().equals(""))
			status = txtStatus.getText().toString();
		else
			status = "";
		
		if(!txtPhone.getText().toString().equals(""))
			phone = txtPhone.getText().toString();
		else
			phone = "";
		
		phoneCode = btnPhoneCode.getText().toString();
		
		outState.putString(Constant.NAME, fname);
		outState.putString(Constant.LNAME, lname);
		outState.putString(Constant.STATUS, status);
		outState.putString(Constant.PHONE, phone);
		outState.putString(Constant.PHONE_CODE, phoneCode);
		super.onSaveInstanceState(outState);
	};
	
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);	
		fname = savedInstanceState.getString(Constant.NAME);
		lname = savedInstanceState.getString(Constant.LNAME);
		status = savedInstanceState.getString(Constant.STATUS);
		phone = savedInstanceState.getString(Constant.PHONE);
		phoneCode = savedInstanceState.getString(Constant.PHONE_CODE);
		setDetails(fname, lname, status, phone, phoneCode);
	};
	
	private void setDetails(String fname, String lname, String status, String phone, String phoneCode) {
		txtName.setText(""+fname);
		txtLastName.setText(""+lname);
		txtStatus.setText(""+status);
		txtPhone.setText(""+phone);
		btnPhoneCode.setText(""+phoneCode);
	}
	
	private void onSave() {
		
		boolean check = true;
    	
    	Validate validate = new Validate();
    	
		status = txtStatus.getText().toString();    	
		fname = txtName.getText().toString();			
		lname = txtLastName.getText().toString();		
		phone = txtPhone.getText().toString();    	
		phoneCode = btnPhoneCode.getText().toString();
		
    	if(validate.isNotEmpty(fname))
		{
			fname = txtName.getText().toString();
		}
		else
		{
			fname = "";
			check = false;
			txtName.setError("Enter First Name");
		}
		if(validate.isNotEmpty(lname))
			lname = txtLastName.getText().toString();
		else
		{
			lname = "";
			check = false;
			txtLastName.setError("Enter Last Name");
		}
		if(validate.isValidPhone(phone))
			phone = txtPhone.getText().toString();
		else
		{
			phone = "";
			check = false;
			txtPhone.setError("Enter correct phone number");
		}
		if(validate.isNotEmpty(status))
			status = txtStatus.getText().toString();
		else
		{
			status = "Hey !!";
		}
		
		if(check)
		{		
			String filename = getResources().getString(R.string.edit_profile_php);
			asyncLoadVolley = new AsyncLoadVolley(context, filename);
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.ID, Sessions.getUserId(context));
			map.put(Constant.NAME, fname);
        	map.put(Constant.LNAME, lname);
        	map.put(Constant.PHONE_CODE, phoneCode);
        	map.put(Constant.PHONE, phone);
    		map.put(Constant.STATUS, status);
    		//map.put(Constant.IMAGE, imageName);
			asyncLoadVolley.setBasicNameValuePair(map);
			asyncLoadVolley.setOnAsyncTaskListener(changeAsyncTaskListener);
			asyncLoadVolley.beginTask();
		}
	}
	
	private String response = "";
	OnAsyncTaskListener changeAsyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "mess : "+message);
			response = message;
			AsyncResponse asyncResponse = new AsyncResponse(response);
			if(asyncResponse.ifSuccess())
			{
				showToast("Successfully Changed");
				
				Sessions.setStatus(context, status);
				Sessions.setName(context, fname);
				Sessions.setLname(context, lname);
				Sessions.setPhone(context, phone);
				Sessions.setPhoneCode(context, phoneCode);
			}
			else
			{
				if(bitmap==null || path==null) {
					showToast("Some problem occured while updating. Please try again.");
				}
				else {
					showToast("Your Pic is changed successfully.");
				}
			}	
			
			imageName = fname+"_"+lname + new Date().getTime() +".jpg";	
			
			if(bitmap==null || path==null) {}
			else
				onSelectPhoto(fname, lname, bitmap, path, imageName);
			setProgressBarIndeterminateVisibility(false);
		}
		
		@Override
		public void onTaskBegin() {
			setProgressBarIndeterminateVisibility(true);
		}
	};
	
	///// 
	
	private static final int CAMERA_REQUEST = 1;
	private static final int RESULT_LOAD_IMAGE = 2;
	
	private void onSelectPhoto(String fname, String lname, Bitmap bitmap, String path, String imageName) {
				
		SessionUserImage.setImage(context, imageName, path);
		
		Intent intent=new Intent(context, LoadImageService.class);
		intent.putExtra(Constant.NAME, imageName);
		intent.putExtra(Constant.PATH, path);		
		intent.putExtra(Constant.VALUE, LoadImageService.VALUE_PROFILE_PIC);
		startService(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
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
				
				userImageView.setImageBitmap(bitmap);
				
				this.bitmap = bitmap;
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
			            
			        	Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
			        	userImageView.setImageBitmap(bitmap);
			        	
			        	this.bitmap = bitmap;
						this.path = picturePath;
				 	}
			}
			break;
			
		case Constant.PHONE_CODE_INT:
        	phoneCode = data.getExtras().getString(Constant.CODES);
			btnPhoneCode.setText(phoneCode);
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
