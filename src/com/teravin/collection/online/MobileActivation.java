package com.teravin.collection.online;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teravin.adapter.Connection;
import com.teravin.adapter.LoansDataSource;
import com.teravin.adapter.MySQLiteHelper;
import com.teravin.security.SessionManager;
import com.teravin.service.LoginService;
import com.teravin.service.PaymentService;
import com.teravin.tracking.SampleAlarmReceiver;
import com.teravin.util.APIBlink;

public class MobileActivation extends Activity implements APIBlink {
	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	Connection cd;
	private Button btnActivate;
	private Button btnToken;
	private LoginService loginService;
	final Context context = this;
	private static final String TAG = "Cash Collection";
	public static SessionManager session;
	private TextView errorText;
	public MySQLiteHelper dbsqlite;
	public LoansDataSource loandata;
	public static String usernameDB = "";
	private String passwordDB = "";
	private String tokenDB = "";
	public String username="";
	public String j_username="";
	private String dataLogin;
	public String idIMSI="";
	private JSONObject jsonLogin;
	GPSTracker gps;
	public String j_token="";
	public String refNo="";
	private EditText usernameField;
	public static ArrayList<HashMap<String, String>> listBank = new ArrayList<HashMap<String,String>>();
	int bafer = 0;
	public static int condition = 0;														
	String ambilName, ambilPass;
	public static int typeApp = 1;
	ProgressDialog dialog;
	private final String A = "AYAMBAKAR";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mobile_activation);
		usernameField = (EditText) findViewById(R.id.usernameactivation);
		
		String tes = displayLocationSettingStatus();
		String message = "This application using location service. Please check your location setting and make sure to enable all the options " +
				"or choose 'High Accuracy' mode. Click Ok to go to Location Setting";
		if (!tes.equals("ok")){
			alertDialog(message, "Location Setting");
		}
		firstCheck();
		//		if (isConnectingToInternet()){
		////			Toast.makeText(this, "Your Phone is Connected to The Internet and Ready to GO!", Toast.LENGTH_LONG).show();
		//		} else {
		//			alertConnectivityError();
		//		}

		cd = new Connection(getApplicationContext());
		loginService = new LoginService(getApplicationContext());
		session = new SessionManager(getApplicationContext());  


		/****************Token****************/

		btnToken = (Button) findViewById(R.id.getToken);
		btnToken.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				j_username = usernameField.getText().toString();
				System.out.println("j_username ::: " + j_username);
				Log.d(TAG,"j_username :: " + j_username);
				mobileActivationProcess(j_username);
			}

		});

		/****************Activate****************/
		btnActivate=(Button) findViewById(R.id.activate);
		btnActivate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				EditText password=(EditText) findViewById(R.id.passwordactivation);
				String j_password = password.getText().toString();

				EditText token=(EditText) findViewById(R.id.tokenactivation);
				j_token = token.getText().toString();

				ambilName = j_username;
				ambilPass = j_password;
				usernameDB = j_username;
				
				
				if(j_username.trim().length() > 0 && j_password.trim().length() > 0 ){

					try {
						Boolean success = false;
						LoginService requestLogin = new LoginService(getApplicationContext()); 
						dbsqlite = new MySQLiteHelper(getApplicationContext());
						loandata = new LoansDataSource(getApplicationContext());

						SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
						boolean defaultValue = false;
						final boolean statusTracking = sharedPref.getBoolean("tracking", defaultValue);

						if (!(statusTracking)){
							SampleAlarmReceiver ambil = new SampleAlarmReceiver();
							ambil.setAlarm(MobileActivation.this);
							System.out.println("======== Tracking ON! =======");
							savePref("tracking", true);
						}

						if(loginService.authenticate(j_username, j_password, refNo,j_token,false)){
							System.out.println("");
							savePref("activation", true);
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
							// alert.showAlertDialog(LoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					//	alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
				}
			}
		});



	}

	private boolean loadPref() {
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

	public void saveOtherpref(String key, String content){
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, content);
		editor.commit();    
		Log.d(A, "save Other pref success!");
	}

	public String getPhoneState(){
		String IMSI = null, ICCID = null;
		TelephonyManager mTelephonyMgr = (TelephonyManager) MobileActivation.this.getSystemService(Context.TELEPHONY_SERVICE);
		try{
			ICCID = mTelephonyMgr.getSimSerialNumber();
			IMSI = mTelephonyMgr.getSubscriberId();
		} catch (Exception e){
			e.printStackTrace();
			
		}
		System.out.println("======== Cek ICCID and IMSI "+ IMSI + " " + ICCID);
		return IMSI;
	}

	public void firstCheck() {
		
		if (loadPref()){
			Intent inten = new Intent(MobileActivation.this, LoginActivity.class);
			inten.putExtra("activate", true);
			startActivity(inten);
			finish();
		} else {
			idIMSI = getPhoneState();
			saveOtherpref("ICCID", idIMSI);
			Log.d(A, "ICCID = " + idIMSI);
		}
	}

	public void mobileActivationProcess(String username){
		if(username !=null){
			
			PaymentService request = new PaymentService(getApplicationContext());
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair("username", username));
			nameValuePair.add(new BasicNameValuePair("imsi" , idIMSI));

			JSONObject response = request.sendHTTPRequest(checkIMSI, nameValuePair);
			if(response != null){
				try {
					if(response.getString("needAk").equalsIgnoreCase("Y")){
						System.out.println("Token");
						List<NameValuePair> nameValuePair2 = new ArrayList<NameValuePair>();
						nameValuePair2.add(new BasicNameValuePair("username", username));
						JSONObject responseToken = request.sendHTTPRequest(getToken, nameValuePair2);
						System.out.println("Token :: " + responseToken);
						if(responseToken != null){
							Log.d( TAG ,"responseToken  ::: " + responseToken);
							if(responseToken.getString("error").equalsIgnoreCase("N")){
								refNo  = responseToken.getString("refNo");
							}
						}else{
							System.out.println("Token null ");
						}
					}else{
						Toast.makeText(getApplicationContext(), "Username is not registered yet", Toast.LENGTH_SHORT);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Problem connecting to server", Toast.LENGTH_SHORT);
			}
		}else{

		}
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








