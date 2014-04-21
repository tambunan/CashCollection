package com.teravin.collection.online;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teravin.collection.online.R;
import com.teravin.adapter.Connection;
import com.teravin.adapter.JSONParser;
import com.teravin.adapter.LoansDataSource;
import com.teravin.adapter.MySQLiteHelper;
import com.teravin.security.SessionManager;
import com.teravin.service.LoginService;
import com.teravin.tracking.SampleAlarmReceiver;
import com.teravin.util.APIBlink;

public class LoginActivity extends Activity implements APIBlink {
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    Connection conn;
	private Button btnLogin;
	private LoginService loginService;
	final Context context = this;
	private static final String TAG = "Loan Finance";
	public static SessionManager session;
	private TextView errorText;
    public MySQLiteHelper dbsqlite;
    public LoansDataSource loandata;
    public static String usernameDB = "";
    private String passwordDB = "";
    public String username="";
    public boolean activated=false;
    private String dataLogin;
    private JSONObject jsonLogin;
    GPSTracker gps;
    public static ArrayList<HashMap<String, String>> listBank = new ArrayList<HashMap<String,String>>();
    int bafer = 0;
    public static int condition = 0;														
    String ambilName, ambilPass;
    public static int typeApp = 1;
    ProgressDialog dialog;
    private final String A = "cashcollection";
    
    
    

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		 }
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		String tes = displayLocationSettingStatus();
		String message = "This application using location service. Please check your location setting and make sure to enable all the options " +
				"or choose 'High Accuracy' mode. Click Ok to go to Location Setting";
		
		if (!tes.equals("ok")){
			alertDialog(message, "Location Setting");
		}
		
		conn = new Connection(getApplicationContext());
		activated = getIntent().getBooleanExtra("activate", false);
				
		/****************Login****************/
		btnLogin=(Button) findViewById(R.id.login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				EditText username=(EditText)findViewById(R.id.username);
				String j_username = username.getText().toString();
				
				EditText password=(EditText)findViewById(R.id.password);
				String j_password = password.getText().toString();
				
				ambilName = j_username;
				ambilPass = j_password;
				usernameDB = j_username;
				dbsqlite = new MySQLiteHelper(getApplicationContext());
                loandata = new LoansDataSource(getApplicationContext());
                
				if(j_username.trim().length() > 0 && j_password.trim().length() > 0 ){
					Boolean success = false;
					
					if (conn.isConnectingToInternet()){
						/*ONLINE*/
						loginService = new LoginService(getApplicationContext());
						session = new SessionManager(getApplicationContext()); 
						
						
						 SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                         boolean defaultValue = false;
                         final boolean statusTracking = sharedPref.getBoolean("tracking", defaultValue);
                         
                         if (!(statusTracking)){
//                         Intent inten = new Intent(LoginActivity.this, BackgroundService.class);
//      			   		 startService(inten);
                        	 SampleAlarmReceiver ambil = new SampleAlarmReceiver();
                        	 ambil.setAlarm(LoginActivity.this);
      			   		 System.out.println("======== Tracking ON! =======");
      			   		 savePref("tracking", true);
                         }
		                         
		          			     
							     if(loginService.authenticate(j_username, j_password,"","", activated)){
							    	 if(dbsqlite.checkDataBase()){
							    		 Intent intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
										 intent.putExtra("name", ambilName);
										 intent.putExtra("email", ambilPass);
										 startActivity(intent);
			                          }
							    	 else{
							    		 Intent intent = new Intent(getApplicationContext(), DownloadActivity.class);
										 startActivity(intent);
		                                 finish();
							    	 }
								  }
								 else{
		                             errorText = (TextView) findViewById(R.id.login_error);
		                             errorText.setText("Incorrect username/password");
		                         }
						
					} else {
						/*OFFLINE*/
						//alertConnectivityError();
						
	                    String agentName =  loandata.getAgentName();
	                    
	                    loadPref(agentName);
	                    System.out.println("agentName :: " +agentName+" :::: "+ j_username.trim() +" ::::: "+usernameDB);
	                    if(j_username.trim().equals(usernameDB) && j_password.trim().equals(passwordDB)) {
	                    	System.out.println("j_username.trim() :: usernameDB :::  "+ j_username.trim() + " :::: "+usernameDB );
                            
                            if (dbsqlite.checkDataBase()){
                                if(loandata.isEmpty()){
                                    Intent intent = new Intent(getApplicationContext(), DownloadActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Intent intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(), DownloadActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }else{
                        	System.out.println("j_username.trim() :: usernameDB :::  "+ j_username.trim() + " :::: "+usernameDB );
                        	 errorText = (TextView) findViewById(R.id.login_error);
                             errorText.setText("Incorrect username/password");
                             success = false;
                        }
                    }
				}
				else{
					 errorText = (TextView) findViewById(R.id.login_error);
                     errorText.setText("Please fill username and password");
                }
				
			}
		});
		
		
		
	}

    private void loadPref(String username) {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        usernameDB = sharedPreferences.getString("user", username);
        passwordDB = sharedPreferences.getString("password", "password");
    }

    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) {
	        Log.d(TAG, "MENU pressed");
	        System.out.println("CEK TOMBOL YANG DITEKAN. keycode = "+ keyCode + " dan keyevent = "+ event);
	        stop();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void stop(){
//		stopService(new Intent(this, BackgroundService.class));
	
		SampleAlarmReceiver alarm = new SampleAlarmReceiver();
		  alarm.cancelAlarm(LoginActivity.this);
		  savePref("tracking", false);
	   	  
        Toast.makeText(this, "Service Telah dihentikan", Toast.LENGTH_LONG).show();
	}
	
	public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
  
          }
          return false;
    }
	
	public void alertConnectivityError(){
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("Your phone is not connected to the internet. Please check your connectivity settings. Application is exitting.")
			.setCancelable(true)
			.setTitle("Connectivity problem")
			 .setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
                finish();
           }
       });;
		AlertDialog alert = alt_bld.create();
		alert.show();
	}
	
	 public String getPhoneState(){
	        String IMSI = null, ICCID = null;
	        TelephonyManager mTelephonyMgr = (TelephonyManager) LoginActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
	        try{
	            ICCID = mTelephonyMgr.getSimSerialNumber();
	            IMSI = mTelephonyMgr.getSubscriberId();
	        } catch (Exception e){
	            e.printStackTrace();
	            System.out.println("======== Cek ICCID and IMSI "+ IMSI + " " + ICCID);
	        }
	        return ICCID;
	    }
	 
	 @Override
		public boolean onTouchEvent(MotionEvent event){ 
		 String DEBUG_TAG = "cashcollection";
		 
		        												/// detect simple touch
		    int action = MotionEventCompat.getActionMasked(event);
		        
		    switch(action) {
		        case (MotionEvent.ACTION_DOWN) :
		            return true;

		        case (MotionEvent.ACTION_UP) :
		            Log.d(DEBUG_TAG,"Action was UP");
		        bafer++;
		        if (bafer == 5){
		        	SampleAlarmReceiver ini = new SampleAlarmReceiver();
		        	ini.cancelAlarm(LoginActivity.this);
		        	Log.d(DEBUG_TAG, "alarm Tracking Stopped!");
		        	Toast.makeText(LoginActivity.this, "Tracking Stopped!", Toast.LENGTH_LONG).show();
		        	bafer = 0;
		        }
		            return true;
    
		        default : 
		            return super.onTouchEvent(event);
		    } 
	 }
	 
	 private boolean loadPref() {
	    	
//       SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//       usernameDB = sharedPreferences.getString("user", "duma");
//       passwordDB = sharedPreferences.getString("password", "password");
//       Log.d(A, "load pref success!");
       
       SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
       boolean defaultValue = false;
       final boolean status = sharedPref.getBoolean("activation", defaultValue);
       return status;
   }
   
   public void savePref(String key, boolean status){
   	 SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, status);
        editor.commit();    
        Log.d(A, "save pref success!");
   }
   
	 
	 public void alertDialog(String message, String title) {
	        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
	        alt_bld.setMessage(message)
	                .setCancelable(true)
	                .setTitle(title)
	                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
//	                    Intent inten = new Intent(context, BackgroundService.class);
//	                    context.stopService(inten);
	                    	Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                    	startActivity(viewIntent);
	                    }
	                });
	        AlertDialog alert = alt_bld.create();
	        alert.show();
	    }
	 
 private String displayLocationSettingStatus() {
	   String result;
		ContentResolver contentResolver = getBaseContext().getContentResolver();
		boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(
				contentResolver, LocationManager.GPS_PROVIDER);
		
		boolean networkStatus = Settings.Secure.isLocationProviderEnabled(
				contentResolver, LocationManager.NETWORK_PROVIDER);
		
		if (gpsStatus) {
			if(networkStatus){
				result = "ok";
			} else {
				result = "network off, gps only";
			}

		} else {
			if (networkStatus){
				result = "gps off, network only";
			} else {
				result = "location totally off";
			}
		}
		Log.d(TAG, "Cek Result "+ result);
		return result;
	}
	 

}
