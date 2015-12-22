package me.zakeer.justchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import me.zakeer.justchat.interfaces.OnAsyncTaskListener;
import me.zakeer.justchat.items.UserDetailItem;
import me.zakeer.justchat.qb.DataHolder;
import me.zakeer.justchat.qb.QbConnect;
import me.zakeer.justchat.sessions.SessionQb;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.AlertDialogManager;
import me.zakeer.justchat.utility.AsyncLoadVolley;
import me.zakeer.justchat.utility.AsyncResponse;
import me.zakeer.justchat.utility.ConnectionDetector;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.ScreenSize;
import me.zakeer.justchat.utility.Validate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;

public class LoginActivity extends Activity {
	
	protected static final String TAG = null;
	
	private Context context = LoginActivity.this;
	
    // alert dialog manager
    private AlertDialogManager alert = new AlertDialogManager();
    
    // Internet detector
    private ConnectionDetector cd;
    
    // UI elements
    private EditText txtPassword;
    private EditText txtEmail;
     
    // Register button
    Button btnLogin;
    
    private ImageView logoImageView1;
    
    private AsyncLoadVolley asyncLoadVolley;
	
	private ArrayList<UserDetailItem> list;
	
	private ScreenSize screenSize;
	
	// GCM
	private GoogleCloudMessaging gcm;
    private String regid = "";
    
    private String email, password;
	
    public static final int FROM_START = 1;
    public static final int FROM_BUTTON_CLICK = 2;
    
        
    boolean isqbloaded=false,isloggedin=false;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        QBSettings.getInstance().fastConfigInit(QbConnect.QB_APP_ID, QbConnect.QB_AUTH_KEY, QbConnect.QB_AUTH_SECRET);
               
        if(Sessions.isLoggedIn(context)) {
        	Intent i = new Intent(getApplicationContext(), FriendsListActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        	setContentView(R.layout.login);

        	
            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                regid = GcmRegistration.getRegistrationId(context);
                
                Log.e(TAG, " 1 -> Reg id : " + regid);
                
                if(regid!=null)
            	{
	                if (regid.length()==0) {
	                    registerInBackgroundMy(FROM_START);
	                }
            	}
                else
                {
                	registerInBackgroundMy(FROM_START);
                }
            } else {
                Log.i(TAG, "No valid Google Play Services APK found.");
            }
            
        	String filename = getResources().getString(R.string.login_php);
			asyncLoadVolley = new AsyncLoadVolley(context, filename);
			asyncLoadVolley.setOnAsyncTaskListener(asyncTaskListener);
		
        cd = new ConnectionDetector(getApplicationContext());
        
        txtEmail = (EditText) findViewById(R.id.loginEmail);
        txtPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        
        logoImageView1 = (ImageView) findViewById(R.id.logo_image1);
        
        screenSize = new ScreenSize(context);
		
		int duration = 1300;
        
        Animation animationTop = new TranslateAnimation(0, 0, (int)(screenSize.getScreenHeightPixel()), 0); 
        animationTop.setDuration(duration);
        animationTop.setFillAfter(true);
        animationTop.setInterpolator(new DecelerateInterpolator());
        logoImageView1.setAnimation(animationTop);
        
        list = new ArrayList<UserDetailItem>();
        
        /*
         * Click event on Register button
         * */
        btnLogin.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View arg0) {
            	
            	if(cd.isConnectedToInternet())
            	{
                // Read EditText dat
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                boolean check = true;
            	
            	Validate validate = new Validate();
                
                if(validate.isNotEmpty(email))
        			email = txtEmail.getText().toString();
        		else
        		{
        			email = "";
        			check = false;
        			txtEmail.setError("Please enter your email");
        		}
        		int passwordLength = 1;
        		if(validate.isAtleastValidLength(password, passwordLength))
        			password = txtPassword.getText().toString();
        		else
        		{
        			password = "";
        			check = false;
        			txtPassword.setError("Please enter a password");
        		}
                
                // Check if user filled the form
                if(check) {
                	
                	// changed temporarily
                	//startRegistration();
                	
                	 // original 
                	
                	if(regid!=null)
                	{
	                	if (regid.length()==0) {
	                		Log.i(TAG, "Regid empty. So register again.");
	                        registerInBackgroundMy(FROM_BUTTON_CLICK);
	                    }
	        			else {
	        				startRegistration(FROM_BUTTON_CLICK);
	        			}
                	}
                	else
                	{
                		registerInBackgroundMy(FROM_BUTTON_CLICK);
                	}
        			
                } else {
                    // user doen't filled that data
                    // ask him to fill the form
                	
                	String message = "Please enter correct email and/or password";
                    //alert.showAlertDialog(LoginActivity.this, "Login Error!", message, false);
                	showToast(message);
                }
            	}
            	else
            	{
            		String noInternet = getResources().getString(R.string.internet_lost);
            		alert.showAlertDialog(LoginActivity.this, "No Internet!", noInternet, false);
            	}
            }
        });
        }
        
        // TODO : 
        //QBSettings.getInstance().fastConfigInit(QbConnect.QB_APP_ID, QbConnect.QB_AUTH_KEY, QbConnect.QB_AUTH_SECRET);
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Constant.FINISH_BACK_ACTIVITY_INT_RESULT_CODE && requestCode==Constant.FINISH_BACK_ACTIVITY_INT_REQUEST_CODE) {
            finish();
        }   
    }
	
    private void createSession(String login, final String password) {

        // Create QuickBlox session with user
        //
        QBAuth.createSessionByEmail(login, password, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    // save current user
                    DataHolder.getInstance().setCurrentQbUser(((QBSessionResult) result).getSession().getUserId(), password);

                    Sessions.setQbId(context, ((QBSessionResult) result).getSession().getUserId()+""); 
                    
                    isqbloaded= true;
                    
                    // show next activity
                    showCallUserActivity();
                }
            }
        });
    }
	
    private void startRegistration(final int from) {
    	// uncomment this code ... its required had commented to test on emulator
    	
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
    	
    	if(from==FROM_BUTTON_CLICK)
    	{
    		String newemail = Validate.convertEmail(email);
    		
    		 createSession(email, newemail);
    		
	    	btnLogin.setClickable(false);
	    	Map<String, String> map = new HashMap<String, String>();
			map.put(Constant.EMAIL, email);
			map.put(Constant.PASSWORD, password);
			map.put(Constant.REGID, GcmRegistration.getRegistrationId(context)); // original 
			asyncLoadVolley.setBasicNameValuePair(map);
	    	asyncLoadVolley.beginTask();  
    	}
	}
    
	

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
	    		Log.i(TAG, "Regid started.");
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
	                GcmRegistration.storeRegistrationId(context, regid);
	                return true;
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                return false;
	            }	
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(Boolean result) {
	    		Log.i(TAG, "Regid end 1.");
	    		super.onPostExecute(result);
	    		
    			startRegistration(from);
	    		
	    	}
		}.execute();		
	}
    
    private String response = "";
	OnAsyncTaskListener asyncTaskListener = new OnAsyncTaskListener() {
		
		@Override
		public void onTaskComplete(boolean isComplete, String message) {
			Log.e(TAG, "mess : "+message);
			response = message;
			
			btnLogin.setClickable(true);
			txtPassword.setText("");
			 
				AsyncResponse asyncResponse = new AsyncResponse(response);
				if(asyncResponse.ifSuccess())
				{	
					
					// TODO 
					//createQbSession(email, password);
					
					/*
					final QBUser user = new QBUser(""+email, ""+password);
					 
					// register user
					QBUsers.signUp(user, new QBCallbackImpl() {
					    @Override
					    public void onComplete(Result result) {
					        // result comes here
					        // check if result success
					        if (result.isSuccess()) {
					            // do stuff you need					        	
					        }
					    }
					});*/
					
					list = asyncResponse.getUserDetail();
					
					String id = list.get(0).getId();
					String name = list.get(0).getName();
					String lname = list.get(0).getLname();
					String phone = list.get(0).getPhone();
					String phoneCode = list.get(0).getPhoneCode();
					String email = list.get(0).getEmail();
					String status = list.get(0).getStatus();
					String online = list.get(0).getOnline();
					String image = list.get(0).getImage();
					Log.e(TAG, "image : "+image);
					storeId(context, id, name, lname, phone, phoneCode, email, status, online, image);
					
					isloggedin = true;
					showCallUserActivity();
					
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
	
	private void storeId(Context context, String id, String name, String lname, String phone, String phoneCode, String email, String status, String online, String image) {
	    Sessions.save(context, id, name, lname, phone, phoneCode, email, status, online, image);
	}
	
	protected void showCallUserActivity() {
		// TODO Auto-generated method stub
		
		// Launch Main Activity
		if(isloggedin && isqbloaded)
		{
			Intent i = new Intent(getApplicationContext(), FriendsListActivity.class);
			startActivity(i);
			finish();
		}
	}

	public void onSkipClick(View view) {
		Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
		startActivityForResult(intent, Constant.FINISH_BACK_ACTIVITY_INT_REQUEST_CODE);
	}	
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}		
    
    protected void showToastLong(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
	}
    
    //// QB
    

    private void createQbSession(String login, final String password) {
    	
        // Create QuickBlox session with user
        //
        QBAuth.createSession(login, password, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
            	Log.e(TAG, "result : "+result.getRawBody());
            	
                if (result.isSuccess()) {
                    // save current user
                    DataHolder.getInstance().setCurrentQbUser(((QBSessionResult) result).getSession().getUserId(), password);
                    
                    int userId = ((QBSessionResult) result).getSession().getUserId();
                    SessionQb.save(context, "" + userId);
                    showToast("succ : qb user id : "+userId);
                    
                    Log.e(TAG, "succ : qb user id : "+userId);
                }
            }
        });
    }

}
