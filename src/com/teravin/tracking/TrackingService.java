package com.teravin.tracking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.teravin.adapter.LoansDataSource;
import com.teravin.adapter.MySQLiteHelper;
import com.teravin.collection.online.LoginActivity;
import com.teravin.collection.online.MobileActivation;
import com.teravin.security.SessionManager;
import com.teravin.service.PaymentService;
import com.teravin.util.APIBlink;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class TrackingService extends Service implements APIBlink{

    public static Intent ACTION1;
    PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;
    final static private long ONE_SECOND = 1000;
    final static private long FIFTEEN_MINUTES = ONE_SECOND * 60 * 5;
    private MySQLiteHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<HashMap<String, String>> trxOfflineList;
//    int hour = new Date().getHours();
//    int minute = new Date().getMinutes();
//    int second = new Date().getSeconds();
//    int day = new Date().getDate();
//    int month = new Date().getMonth();
//    public static int status;
    int hour;
    int minute;
    int second;
    int day;
    int month;
    public static boolean flag;
    Vibrator getar;
    PowerManager pm;
    PowerManager.WakeLock wl;

    LocationManager myLocationManager;
    LocationListener myLocationListener;
    LocationListener myNetworkLocationListener;
    Location myLocation = null;
    int gpsFix, networkFix;
    Handler myHandler;
    private Intent inten;

    public String logcat = "";
    
//    CommentsDataSource com = new CommentsDataSource(this);

    private static final String TAG = "cashcollection";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

//        Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
//       status = 0;
//       flag = false;
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
    	this.inten = intent;
    	Log.d(TAG, "onStartCommand");
    	gpsFix = 0;
    	networkFix = 0;
    	setup();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }
    

    @Override
    public void onStart(Intent intent, int startId) {
//		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
//    	if (status == 0){
    	
        setup();
//        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), FIFTEEN_MINUTES, pi);
        Log.d(TAG, "onStart");
//        status = 1;
//        flag = true;
//    	} else {
//    		Toast.makeText(this, "Service Already Running BITCH!", Toast.LENGTH_LONG).show();
//    	}

    }

    @Override
    public void onDestroy() {
//		Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
//        unregisterReceiver(br);
        Log.d(TAG, "onDestroy");
        super.onDestroy();
     // Release the wake lock provided by the BroadcastReceiver.
//        SampleAlarmReceiver.completeWakefulIntent(inten);
        
//        wl.release();
        flag = false;
//        status = 0;
    }

    private void setup() {
//        br = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context c, Intent i) {
//                if ("android.intent.action.BOOT_COMPLETED".equals(i.getAction())) {
//                    Intent pushIntent = new Intent(c, BackgroundService.class);
//                    c.startService(pushIntent);
//                }
                getar = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.example.wherehaveibeen");
//                wl.acquire();
//                String latitude = ambilLatitude();
//                String longitude = ambilLongitude();
//                System.out.println("===== cek coordinate " + latitude + " " + longitude + " " + day + "/" + (month+1));
////				Toast.makeText(c, "I GOT YOU" , Toast.LENGTH_SHORT).show();
////				System.out.println("I GOT YOU");
//                String coordinate = latitude + "," + longitude;
//                String time = day + "/" + (month + 1) + "/2014" + " " + hour + ":" + minute + ":" + second;
//				com.createComment(coordinate, time);
                flag = true;
                setupHandlerAndListener();
//                putData(coordinate, time);
//            }
//        };
//        registerReceiver(br, new IntentFilter("com.example.wherehaveibeen"));
//        pi = PendingIntent.getBroadcast(this, 0, new Intent("com.example.wherehaveibeen"), 0);
//        am = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
    }


    public void putData(String coordinate) {
	 	dbHelper = new MySQLiteHelper(getApplicationContext());
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("coordinate", coordinate); 
        db.insert("Coordinate", null, values);
        db.close();
    }
    
    public void setupHandlerAndListener(){
    	 myHandler = new Handler();
    	 myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//    	 myLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	 
    	 
         final Runnable myRunnable = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					myLocationManager.removeUpdates(myLocationListener);
//					double latitude = 0.0;
//					double longitude  = 0.0;
//					
//					Toast.makeText(getApplicationContext(), "Gps start up for 30 seconds!!, Stopping", Toast.LENGTH_SHORT).show();
//					try{
////					if (myLocation != null){
//					latitude = myLocation.getLatitude();
//	                longitude = myLocation.getLongitude();
//						Log.d(TAG, "getting coordinate latitude and longitude via GPSTracker!");
//						
//					} catch (Exception e){
////					} else {
//						Log.d(TAG, "data null! error! at getting coordinate latitude and longitude!!");
//						e.printStackTrace();
//						logcat = Log.getStackTraceString(e);
//				    	MainActivity.tampil.setText(logcat);
////					}
//					}
					
					myNetworkLocationListener = new LocationListener() {
						
						@Override
						public void onStatusChanged(String provider, int status, Bundle extras) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onProviderEnabled(String provider) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onProviderDisabled(String provider) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onLocationChanged(Location location) {
							// TODO Auto-generated method stub
							double latitudeNetwork;
							double longitudeNetwork;
							String time;
			                getDateAndTime();
							networkFix++;
							try {
							latitudeNetwork = location.getLatitude();
							longitudeNetwork = location.getLongitude();
							time = day + "/" + (month + 1) + "/2014" + " " + hour + ":" + minute + ":" + second;
							Log.d(TAG, "count " + networkFix + " " + "coordinate = " + latitudeNetwork + "," + longitudeNetwork + " " + time);
							if (networkFix == 3){
			                String coordinate = latitudeNetwork +"," + longitudeNetwork;
			                Log.d(TAG, "delayed post, 60 seconds. coordinate = " + coordinate + " " + time);
//			                getar.vibrate(150);
			                
			                Log.d(TAG, "Service Stopped!");
			                putData(coordinate);
							uploadCoordinate(latitudeNetwork, longitudeNetwork);
							uploadDataOffline();
			                myLocationManager.removeUpdates(myNetworkLocationListener);
			                stopSelf();
							}
							} catch (Exception e){
								e.printStackTrace();
								String logcat = Log.getStackTraceString(e);
								Log.d(TAG, logcat);
								getDateAndTime();
					            time = day + "/" + (month + 1) + "/2014" + "_" + hour + ":" + minute + ":" + second;   
			                    generateNoteOnSD(TrackingService.this, time + "_network.txt", logcat);
								latitudeNetwork = 1.0;
								longitudeNetwork = 1.0;
								stopSelf();
								SampleAlarmReceiver alarm = new SampleAlarmReceiver();
								alarm.cancelAlarm(TrackingService.this);
								
							}
							
						}
					};
					
					myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
			        		 250, 0, myNetworkLocationListener);
					
				}
         };
				
					

         myHandler.postDelayed(myRunnable, 30000);
         
         
         myLocationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				gpsFix++;
				double latitude;
                double longitude;
                try{
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                String time;
                getDateAndTime();
                time = day + "/" + (month + 1) + "/2014" + " " + hour + ":" + minute + ":" + second;
//                Toast.makeText(getApplicationContext(), latitude + " " + longitude + " " + time, Toast.LENGTH_LONG).show();
                Log.d(TAG, latitude + " " + longitude + " " + time);
                String coordinate = latitude +"," + longitude;
                if (gpsFix == 3) {
//                getar.vibrate(150);
                putData(coordinate);
                uploadCoordinate(latitude, longitude);
				uploadDataOffline();
                myLocationManager.removeUpdates(myLocationListener);
                myHandler.removeCallbacks(myRunnable);
                Log.d(TAG, "Handler Stopped!");
                Log.d(TAG, "Service Stopped!");
                stopSelf();
                }
                } catch (Exception e){
                	e.printStackTrace();
					String logcat = Log.getStackTraceString(e);
					Log.d(TAG, logcat);
					String time;
					getDateAndTime();
		            time = day + "/" + (month + 1) + "/2014" + "_" + hour + ":" + minute + ":" + second;   
                    generateNoteOnSD(TrackingService.this, time + "_gps.txt", logcat);
					latitude = 0.0;
					longitude = 0.0;
					stopSelf();
					SampleAlarmReceiver alarm = new SampleAlarmReceiver();
					alarm.cancelAlarm(TrackingService.this);
                }
                
			}
		};
				
			if (displayGpsStatus(TrackingService.this)){	
			myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
         		500, 0, myLocationListener);
			} else {
				myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
		         		500, 0, myLocationListener);	
			}
    }
    
    public void getDateAndTime(){
    	Date ambil = new Date();
    	hour = ambil.getHours();
        minute = ambil.getMinutes();
        second = ambil.getSeconds();
        day = ambil.getDate();
        month = ambil.getMonth();
    }
    
    public static void generateNoteOnSD(Context context, String sFileName, String sBody){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "LogCat Created in SDCard", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Write Data Success!");
        }
        catch(IOException e)
        {
             e.printStackTrace();
             Log.d(TAG, "Write Data Error!");
        }
       }  
    
	public static Boolean displayGpsStatus(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
		boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(
				contentResolver, LocationManager.GPS_PROVIDER);
		if (gpsStatus) {
			return true;

		} else {
			return false;
		}
	}
    
	public void uploadDataOffline(){
		 trxOfflineList =getListTrxOffline();
		 if(trxOfflineList.size() > 0){
			 System.out.println("trxOfflineList BackgroundService::: ****  " + trxOfflineList);
			 List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			 SessionManager sessionManager = new SessionManager(getApplicationContext());
		        SharedPreferences pref = sessionManager.getPref();
		        String sessionId = pref.getString("JSESSIONID", "");
		    	for(Map<String, String> map : trxOfflineList)
		        {
		    		nameValuePair.add(new BasicNameValuePair("invoiceNo", map.get("invoiceNo")));
		    	    nameValuePair.add(new BasicNameValuePair("installment",map.get("totalAmount")));
		    	    nameValuePair.add(new BasicNameValuePair("langt",map.get("langt")));
		    	    nameValuePair.add(new BasicNameValuePair("longt",map.get("longt")));
		    	    nameValuePair.add(new BasicNameValuePair("refNo",map.get("refNo")));
		    	    nameValuePair.add(new BasicNameValuePair("remark", map.get("remark")));
		    	    nameValuePair.add(new BasicNameValuePair("nextCollectionDate", map.get("nextCollectionDate")));
		    	    nameValuePair.add(new BasicNameValuePair("trxDate", map.get("trxDate")));
		    	    nameValuePair.add(new BasicNameValuePair("settlement", "")); 
		    	    nameValuePair.add(new BasicNameValuePair("JSESSIONID" , sessionId));
		        }
		    	System.out.println("nameValuePair :::: "+nameValuePair);
		    	PaymentService requestPOST = new PaymentService(getApplicationContext());
		    	JSONObject jsonUploadTrx = requestPOST.sendHTTPRequest(uploadTrxOffline, nameValuePair);
		    	System.out.println("balikan jsonUploadTrx ::: " +jsonUploadTrx);
		    	if(jsonUploadTrx == null ){
		    		Toast.makeText(getApplicationContext(), "Problem in prosessing data", 2).show();
		    	}else{
		    		try {
						String result = jsonUploadTrx.getString("result");
						if(result.equalsIgnoreCase("Y")){
							for(Map<String, String> map : trxOfflineList)
					        {
								updateLoanOffline(map.get("invoiceNo"));
					        }
						}
						else{
							Toast.makeText(getApplicationContext(),"There's problem data when send Offline data", 1).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
		    	
		 }
	}
	 public void updateLoanOffline(String invoiceNo ){
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
	        ContentValues con = new ContentValues();
		     con.put("paymentFlag", "3");
		     db.update(MySQLiteHelper.LOAN, con, "invoiceNo ='" +invoiceNo + "'",null);
	 }
	
	 public ArrayList<HashMap<String, String>> getListTrxOffline(){
		 
	    	ArrayList<HashMap<String, String>> trxOffline = new ArrayList<HashMap<String,String>>();
	    	String selectQuery = "SELECT  * FROM " + MySQLiteHelper.LOAN +" where paymentFlag = 1 and flagTrx = 0";
	    	 SQLiteDatabase db = dbHelper.getReadableDatabase();
	         Cursor cursor = db.rawQuery(selectQuery, null);
	         
	         if(cursor.getCount() > 0){
	        	 System.out.println("cursor getListTrxOffline :: "+cursor.getCount());
	            if (cursor.moveToFirst()) {
	                 do {
	                	 
	                 	HashMap<String,String> transaction = new HashMap<String,String>();
	                 	transaction.put("invoiceNo", cursor.getString(2));
	                 	transaction.put("totalAmount", cursor.getString(4));
	                 	transaction.put("customerName", cursor.getString(1));
	                 	transaction.put("refNo", cursor.getString(11));
	                 	transaction.put("trxDate",cursor.getString(14));
	                 	transaction.put("remark", cursor.getString(7));
	                 	transaction.put("nextCollectionDate",cursor.getString(6));
	                 	transaction.put("langt", cursor.getString(12));
	                 	transaction.put("longt",cursor.getString(13));
	                 	trxOffline.add(transaction);
	                 }while (cursor.moveToNext());
	             }
	            
	             cursor.close();
	             db.close();
	         }     	
			return trxOffline;
	    }
	 public void uploadCoordinate(Double latitude, Double longitude){
		 httpRequest upload = new httpRequest(TrackingService.this);
		 upload.execute(latitude + "," + longitude);
	    }
    
	 private class httpRequest extends AsyncTask<String, Void, Boolean>{
	    	Context contextRequest;
//	    	ProgressDialog dialog;
	    	double lat;
	    	double longi;
	    	
	    	public httpRequest (Context context) {
	    		this.contextRequest = context;
	    	}
	    	
	    	protected void onPreExecute(){
				super.onPreExecute();
//				dialog = new ProgressDialog(contextasync);
//				dialog.setCancelable(false);
//				dialog.setMessage("Getting Your Location\nPlease Wait...");
//				dialog.isIndeterminate();
//				dialog.show();
			}

			@Override
			protected Boolean doInBackground(String... data) {
				// TODO Auto-generated method stub
				String [] pecah2 = data[0].split(",");
		        String latitude = pecah2[0];
		        String longitude = pecah2[1];
		 		Log.d(TAG, "cek coordinate asynctask = " + latitude + "," + longitude);
				System.out.println("================Upload Coordinate================");
		 		HashMap kordinat = new HashMap();
		 		Log.d(TAG, "Cek username1 = " + LoginActivity.usernameDB);
//		 		if (LoginActivity.usernameDB.equals("")){
//		 			LoginActivity.usernameDB = MobileActivation.usernameDB;
//		 			Log.d(TAG, "Cek username2 = " + LoginActivity.usernameDB);
//		 			if (LoginActivity.usernameDB.equals("")){
//		 				LoginActivity.usernameDB. = 
//		 			}
//		 		}
		 		String username;
		 		
		 		SharedPreferences sharedPref = contextRequest.getSharedPreferences("usernameAgent", Context.MODE_PRIVATE);
		        
		 		LoansDataSource ambil = new LoansDataSource(contextRequest);
		 		try{
		 		Log.d(TAG, "username = " + ambil.getAgentName());
		 		username = ambil.getAgentName();
		 		
		 		if (ambil.getAgentName().equals("")){
		 			username = MobileActivation.usernameDB;
		 		}
		 		} catch (Exception e){
		 			e.printStackTrace();
		 			Log.d(TAG, "get from DB error! jump to get from prefference");
			        String defaultValue = "NoName";
			        username = sharedPref.getString("username", defaultValue);
		 		}
		 		Log.d(TAG, "Cek username3 = " + LoginActivity.usernameDB);
		 		
		        SharedPreferences.Editor editor = sharedPref.edit();
		        editor.putString("username", username);
		        Log.d(TAG, "Username saved = " + username);
		        editor.commit();    
		 		
	            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	            	nameValuePair.add(new BasicNameValuePair("username", username));
	                nameValuePair.add(new BasicNameValuePair("langt", latitude.toString()));
	                nameValuePair.add(new BasicNameValuePair("longt", longitude.toString()));
	             
	            try {
	            	PaymentService uploadLoan = new PaymentService(TrackingService.this.getApplicationContext());
	                JSONObject jsonuploadData = uploadLoan.sendHTTPRequest(agentCurrentPosition, nameValuePair);
	               
	      /*               
	                * jsonuploadData ::: {"amount":" 15000","id":"67","userAgent":"Duma Tambunan",
	                * "datePick":"15\/01\/2014","payStatus":"1","name":"Duma Tambunan",
	                * "action":"upload","installmentAmount":"15000","controller":"loanAccount"}
					
	      */        if (jsonuploadData == null){
	    	  Log.d(TAG, "This is FAILED");	
           return false;
	      		} else {
			 		Log.d(TAG, "This is Success");					
	                return true;}     
	            }catch (Exception e) {
	                e.printStackTrace();
	                Log.d(TAG, "This is FAILED");	
	                return false;
	            }
	            
				
			}
	    	
			protected void onPostExecute(Boolean result){
				super.onPostExecute(null);
				if (result){
//					dialog.dismiss();
			 		Log.d(TAG, "Success upload data coordinate!");					
				} else {
	            	Toast.makeText(contextRequest, "Problem processing data", Toast.LENGTH_LONG).show();
			 		Log.d(TAG, "FAILED upload data coordinate!");
				}

			}
	    	
	    }


}
