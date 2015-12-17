package me.zakeer.justchat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.items.UserDetailItem;
import me.zakeer.justchat.services.LoadImageService;
import me.zakeer.justchat.services.QbRegisterService;
import me.zakeer.justchat.sessions.SessionUserImage;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AlertDialogManager;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.ConnectionDetector;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.Countries;
import me.zakeer.justchat.utility.FileUtility;
import me.zakeer.justchat.utility.ImageCustomize;
import me.zakeer.justchat.utility.Validate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.core.QBCallback;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserResult;

public class RegisterActivity extends SherlockActivity {
	
	protected static final String TAG = null;
	
	private Context context = RegisterActivity.this;
	
    // alert dialog manager
	private AlertDialogManager alert = new AlertDialogManager();
    
    // Internet detector
    private ConnectionDetector cd;
    
    // UI elements
    private EditText txtName, txtLastName, txtPhone, txtEmail, txtPassword, txtStatus,txtPromo;
    private ImageView userImageView;
    private Button btnPhoneCode;
    // Register button
    private Button btnRegister;
    
    private AsyncLoadVolley asyncLoadVolley;
    private String response = "";
	
	private ArrayList<UserDetailItem> list;
	
	////////////
	
    private GoogleCloudMessaging gcm;    
    private String regid = "";
    
    private String fname, lname, phone, phoneCode, email, password, status,promo;
    
    private String defaultImage = "user.png";
    
    private String path;
    
    /////
    
    //QBUser qbUser;
    
    ////
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.register);
        
        // Changed from getActionBar() to getSupportActionBar()
        
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(Constant.ACTIONBAR_ICON);
                
        String filename = getResources().getString(R.string.register_php);
		asyncLoadVolley = new AsyncLoadVolley(context, filename);		
		asyncLoadVolley.setOnAsyncTaskListener(asyncTaskListener);
		
        cd = new ConnectionDetector(getApplicationContext());
        
        QBAuth.createSession(new QBCallbackImpl() {
		    @Override
		    public void onComplete(Result result) {
		        if (result.isSuccess()) {
		            Log.e("Result", result.toString());
		        } 
		        else {
		            Log.e("Errors",result.getErrors().toString()); 
		        }
		    }
		});
        
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = GcmRegistration.getRegistrationId(context);
            
            Log.e(TAG, " 1 -> Reg id : " + regid);
            
            if(regid!=null)
        	{
	            if (regid.length()==0) {
	            	registerInBackgroundMy(LoginActivity.FROM_START);
	            }
        	}
            else
            {
            	registerInBackgroundMy(LoginActivity.FROM_START);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        
        txtName 		= (EditText) 	findViewById(R.id.txtName);
        txtLastName 	= (EditText) 	findViewById(R.id.txtLastName);
        txtPhone 		= (EditText) 	findViewById(R.id.txtPhone);
        btnPhoneCode	= (Button)	 	findViewById(R.id.btnPhoneCode);
        txtEmail 		= (EditText) 	findViewById(R.id.txtEmail);
        txtPassword 	= (EditText) 	findViewById(R.id.txtPassword);
        txtStatus 		= (EditText) 	findViewById(R.id.txtStatus);
        txtPromo		= (EditText) 	findViewById(R.id.txtPromo);
        userImageView 	= (ImageView) 	findViewById(R.id.userImage);
        btnRegister 	= (Button) 		findViewById(R.id.btnRegister);
        
        list = new ArrayList<UserDetailItem>();
        
        btnPhoneCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, CountryCodesActivity.class);
				startActivityForResult(intent, Constant.PHONE_CODE_INT);
				Log.e(TAG, Countries.getCurrentCountry(context));				
			}
		});	
        
        /*
         * Click event on Register button
         * */
        btnRegister.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View arg0) {
            	
            	boolean check = true;
            	
            	Validate validate = new Validate();
            	
            	// Read EditText dat
        		fname = txtName.getText().toString();
                lname = txtLastName.getText().toString();
                phone = txtPhone.getText().toString();
                phoneCode = btnPhoneCode.getText().toString();
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                status = txtStatus.getText().toString();
        		promo  = txtPromo.getText().toString();
        		
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
        		if(validate.isValidEmail(email))
        			email = txtEmail.getText().toString();
        		else
        		{
        			email = "";
        			check = false;
        			txtEmail.setError("Enter Valid email");
        		}
        		int passwordLength = 8;
        		if(validate.isAtleastValidLength(password, passwordLength))			
        			password = txtPassword.getText().toString();
        		else
        		{
        			password = "";
        			check = false;
        			txtPassword.setError("Password should be atleast "+passwordLength+" characters");
        		}
        		if(validate.isNotEmpty(status))
        			status = txtStatus.getText().toString();
        		else
        		{
        			status = "Hey !!";
        		}
        		/*
        		if(validate.isNotEmpty(promo))
        			promo = txtPromo.getText().toString();
        		else
        		{
        			promo = "";
        			check = false;
        			txtPromo.setError("Enter the Promo Code");
        		}
        		*/
        		if(cd.isConnectedToInternet())
            	{
	        		if(check)
	        		{	
	        			if(regid!=null)
	                	{
		        			if (regid.length()==0) {
		                        registerInBackgroundMy(LoginActivity.FROM_BUTTON_CLICK);
		                    }
		        			else {
		        				startRegistration(LoginActivity.FROM_BUTTON_CLICK);
		        			}   
	                	}
	        			else
	                	{
	                		registerInBackgroundMy(LoginActivity.FROM_BUTTON_CLICK);
	                	}
	                }
            	}
        		else
        		{
        			String noInternet = getResources().getString(R.string.internet_lost);
            		alert.showAlertDialog(RegisterActivity.this, "No Internet!", noInternet, false);
        		}
            }
        });
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
    
    private void startRegistration(final int from) {
    	
    	if(GcmRegistration.getRegistrationId(context)!=null)
    	{   
	    	if(GcmRegistration.getRegistrationId(context)=="")
	    	{
	    		if (checkPlayServices()) {
	        		showToast("Some Error occured. Please try again");
	    		}
	    		else {
	    			showToast("Google Play Services are missing on your phone.");
	    		}   
		    	return;	
	    	}
	    	else
	    	{
	    		Log.e(TAG, "Proceed..");
	    	}
    	}
    	else
    	{
    		if (checkPlayServices()) {
        		showToast("Some Error occured. Please try again");
    		}
    		else {
    			showToast("Google Play Services are missing on your phone.");
    		}   
	    	return;	
    	}
    	
    	if(from==LoginActivity.FROM_BUTTON_CLICK)
    	{    	
	    	Map<String, String> map = new HashMap<String, String>();
	    	map.put(Constant.NAME, fname);
	    	map.put(Constant.LNAME, lname);
			map.put(Constant.EMAIL, email);
			map.put(Constant.PHONE_CODE, phoneCode);
			map.put(Constant.PHONE, phone);
			map.put(Constant.PASSWORD, password);
			map.put(Constant.STATUS, status);
			map.put(Constant.PROMO_CODE, promo);
			map.put(Constant.REGID, GcmRegistration.getRegistrationId(context));
			asyncLoadVolley.setBasicNameValuePair(map);
	    	asyncLoadVolley.beginTask();
	    	
    	}
	}
    
	OnAsyncTaskListener asyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "mess : "+message);
			response = message;
				
				AsyncResponse asyncResponse = new AsyncResponse(message);
				if(asyncResponse.ifSuccess())
				{
					list = asyncResponse.getUserDetail();
					
					//// TODO:
					
					String newemail = Validate.convertEmail(email);
					
					
					QBUser user = new QBUser("", newemail, email);
					QBUsers.signUp(user, new QBCallback() {
						
						@Override
						public void onComplete(Result result, Object arg1) {
							 if (result.isSuccess()) {
						            QBUserResult qbUserResult = (QBUserResult) result;
						            Log.d("Registration was successful","user: " + qbUserResult.getUser().toString());
						        } else {
						            Log.e("Errors",result.getErrors().toString()); 
						        }
						}
						
						  @Override
						    public void onComplete(Result result) {
						        if (result.isSuccess()) {
						            QBUserResult qbUserResult = (QBUserResult) result;
						            Log.d("Registration was successful","user: " + qbUserResult.getUser().toString());
						            
						            QBUser registered = qbUserResult.getUser();
						            int qb_id = registered.getId();						            

						            Sessions.setQbId(context, qb_id+""); 
						            
									String id = list.get(0).getId();
									String name = list.get(0).getName();
									String lname = list.get(0).getLname();
									String phone = list.get(0).getPhone();
									String phoneCode = list.get(0).getPhoneCode();
									String email = list.get(0).getEmail();
									String status = list.get(0).getStatus();
									String online = list.get(0).getOnline();
									
									String userName = name + "_"+lname;
									
									Sessions.save(context, id, name, lname, phone, phoneCode, email, status, online, defaultImage);
									
									if(path==null) {}
									else
									{
										onSelectPhoto(userName, path);
									}
									
									//Start Service									
									Intent service = new Intent(context, QbRegisterService.class);
									startService(service);
									
									// Launch Main Activity
									Intent intent = new Intent(getApplicationContext(), FriendsListActivity.class);
					            	setResult(Constant.FINISH_BACK_ACTIVITY_INT_RESULT_CODE);
							        startActivity(intent);
							        finish();
						            
						            
						            
						        } else {
						            Log.e("Errors",result.getErrors().toString()); 
						        }
						    }
					});
				}
				else
				{
					showToast(""+asyncResponse.getMessage());
				}
		}
		
		@Override
		public void onTaskBegin() {
			
		}
	};
		
	//// GCM ////////////
		
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    GcmRegistration.PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	
	private void registerInBackgroundMy(final int from) {
	    new AsyncTask<Void, Void, Boolean>() {
	    	
	    	@Override
	    	protected void onPreExecute() {
	    		super.onPreExecute();
	    	}
	    	
	    	@Override
	    	protected Boolean doInBackground(Void... params) {
	    		String msg = "";
	    		try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(GcmRegistration.SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;
	                
	                Log.e(TAG, "Reg id : " + regid);
	                
	                if(regid!=null)
	                {
	                	if(regid.length()!=0) // if regid is not empty then save it in preferences.
	                	{
	    	                GcmRegistration.storeRegistrationId(context, regid);
	    	                return true;
	                	}
	                }
	                return false;
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                return false;
	            }	    		
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(Boolean result) {	    		
	    		super.onPostExecute(result);
	    		
    			startRegistration(from);
    			
	    	}
		};
	}
	
	private static final int CAMERA_REQUEST = 1;
	private static final int RESULT_LOAD_IMAGE = 2;
	
	private void onSelectPhoto(String userName, String path) {
		
		String name = userName+"_"+ new Date().getTime() +".jpg";
		
		SessionUserImage.setImage(context, name, path);
		
		Intent intent=new Intent(context, LoadImageService.class);
		intent.putExtra(Constant.NAME, name);
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
			        	userImageView.setImageBitmap(bitmap);
									        	
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
		Uri uriSavedImage = Uri.fromFile(fileUtility.getTempJpgImageFile());
		
		 Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		 i.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage); 
	     startActivityForResult(i, CAMERA_REQUEST);
	        
	}
	
	protected void callgalery()
	{
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}		
    
    protected void showToastLong(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
	}
}
