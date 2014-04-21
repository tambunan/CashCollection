package com.teravin.collection.online;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.teravin.collection.online.R;
import com.teravin.adapter.LoansDataSource;
import com.teravin.adapter.MySQLiteHelper;
import com.teravin.model.Agent;
import com.teravin.model.Loan;
import com.teravin.service.DownloadService;
import com.teravin.util.APIBlink;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by dumatambunan on 1/13/14.
 */
public class DownloadActivity extends Activity implements APIBlink {
    private Button btnDownload;
    MySQLiteHelper sqlite;
    private String[] historyLoanTrx;
    ArrayList<HashMap<String, String>> loanListData;
    List<Address> addresses;
    String serepkordinat;
    public int bafer = 0;
    ArrayList<HashMap<String, String>> loanList;
    ArrayList<HashMap<String, String>> listAddress;
    public String memberToCollect="";
    
    
    ///////////////////////////// AYAMBAKAR
    AsyncTask<Void, Void, Void> mRegisterTask;
    public static String name;
	public static String email;
	///////////////////////////// AYAMBAKAR


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);
        sqlite = new MySQLiteHelper(getApplicationContext());
        btnDownload = (Button)findViewById(R.id.btnDownload);
        
        //////////////////////////////////////AYAMBAKAR
        
        Intent i = getIntent();

//        name = i.getStringExtra("name");
//        email = i.getStringExtra("email");	

        name = LoginActivity.usernameDB;
		if (name.equals("")){
			name = MobileActivation.usernameDB;
		}

        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
		"com.androidhive.pushnotifications.DISPLAY_MESSAGE"));

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		// Check if regid already presents
		if (regId.equals("")) {
		// Registration is not present, register now with GCM			
		GCMRegistrar.register(this, "192157990405");
		} else {
		// Device is already registered on GCM
		if (GCMRegistrar.isRegisteredOnServer(this)) {
		// Skips registration.				
		//Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
		} else {
		// Try to register again, but not in the UI thread.
		// It's also necessary to cancel the thread onDestroy(),
		// hence the use of AsyncTask instead of a raw thread.
		final Context context = this;
		mRegisterTask = new AsyncTask<Void, Void, Void>() {
		
		@Override
		protected Void doInBackground(Void... params) {
		// Register on our server
		// On server creates a new user
//		ServerUtilities.register(context, name, email, regId);
		return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
		mRegisterTask = null;
		}
		
		};
		mRegisterTask.execute(null, null, null);
		}
		}
		
		////////////////////////////////////////////// AYAMBAKAR
        
        btnDownload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            	LoadingProgress task = new LoadingProgress(DownloadActivity.this);
            	task.execute();   

            }

        });
    }

    public void getLoanDatafromServer(){
        loanListData = new ArrayList<HashMap<String, String>>();
        DownloadService downloadData = new DownloadService(getApplicationContext());
        JSONObject jsonDataLoan;
        LoansDataSource loansdata = new LoansDataSource(getApplicationContext());
        try {
                jsonDataLoan = downloadData.sendRequestDataLoan(paymentListAPI);
                System.out.println("jsonDataLoan :: "+ jsonDataLoan);
                if(jsonDataLoan != null){
                	
                
                
	                /**
	                 * jsonDataLoan :: {"total":1,"cardNo":"5120212229639757",
	                 * "trxList":[{"invoiceDate":"2014-03-18T17:00:00Z","address1":"Jl. Magelang 135B",
	                 * "runTime":"2014-03-25T09:01:49Z","address2":"","invoiceNo":"20030025",
	                 * "memberToCollect":"user1","updatedBy":null,"currency":{"id":1,"class":"Currency"},
	                 * "city":"Yogyakarta","id":58,"cif":{"id":4,"class":"Cif"},"totalAmount":1000000,
	                 * "province":"Yogyakarta","isTransaction":"N","corporateID":"cherys",
	                 * "uploadDate":"2014-03-25T08:43:46Z","dueDate":"2014-03-27T17:00:00Z",
	                 * "customerName":"Parkbom","lastUpdated":"2014-03-25T09:02:10Z",
	                 * "class":"com.teravin.collection.maintenance.Distribution","pic":"Airinn",
	                 * "kelurahan":"","phoneNo":"6218769987","kecamatan":"","fileName":"Distribution-2503141543_1.csv"
	                 * ,"rt":"","uploadBy":"cherynaatha","group":{"id":5,"class":"Grup"},"rw":"",
	                 * "mobileNo":"6285718731360"}],
	                 * "action":"paymentList","callback":"callback","oustanding":7000000,
	                 * "controller":"agent","userLimit":20000000}
	                 * **/
	                
	                JSONArray arrTrx = jsonDataLoan.getJSONArray("trxList");
	                int totalRecord = jsonDataLoan.getInt("total");
	                historyLoanTrx = new String[totalRecord];
	                Agent agent = new Agent();
	                
	                String cardNoAgent = jsonDataLoan.getString("cardNo");
	                System.out.println("cardNoAgent ::: " + cardNoAgent);
	                agent.setCardNoAgent(cardNoAgent);
	                agent.setOutstanding(jsonDataLoan.getString("oustanding"));
	                agent.setLimit(jsonDataLoan.getString("userLimit"));
	                String availableAmount = loansdata.getAvailable(jsonDataLoan.getString("userLimit").toString(), jsonDataLoan.getString("oustanding").toString());
	                agent.setAvailable(availableAmount);
	               
	                for (int i = 0; i < arrTrx.length(); i++) {
	                    JSONObject json = arrTrx.getJSONObject(i); 
	                    
	                    Loan loan = new Loan();
	                    loan.setId(json.getString("id"));
	                    loan.setCustName(json.getString("customerName"));
	                    loan.setInvoiceNo(json.getString("invoiceNo"));
	                    loan.setMobilePhoneNo(json.getString("mobileNo"));
	                    loan.setPhoneNo(json.getString("phoneNo"));
	                    loan.setAddress1(json.getString("address1"));
	                    loan.setAddress2(json.getString("address2"));
	                    loan.setRt(json.getString("rt"));
	                    loan.setRw(json.getString("rw"));
	                    loan.setKelurahan(json.getString("kelurahan"));
	                    loan.setKecamatan(json.getString("kecamatan"));
	                    loan.setKota(json.getString("city"));
	                    loan.setProvinsi(json.getString("province"));
	                    loan.setTotalAmount(json.getString("totalAmount"));
	                    loan.setCardNoAgent(cardNoAgent);
	                    memberToCollect = json.getString("memberToCollect");
	                    String address = json.getString("address1") +" "+ json.getString("address2")+ " RT/RW: " +json.getString("rt") +" / "+json.getString("rw")+ ", Kelurahan: "+json.getString("kelurahan")+
	                    		" , Kecamatan: " + json.getString("kecamatan")+ ", Kota: "+json.getString("city")+" , Provinsi: "+json.getString("province");
	                    
	                    String kordinat = planB(address);
	                    String []koordinat = kordinat.split(",");
	                    System.out.println("koordinat [] ::: "+koordinat);
	                    String langt= koordinat[0];
	                    String longt= koordinat[1];
	                    loan.setLangt(langt);
	                    loan.setLongt(longt);
	                    System.out.println("address ::: "+kordinat);
	                    System.out.println("kordinat ::: "+ loan.getLangt() + " ::::: "+loan.getLongt());
	                    loansdata.createLoan(loan);
	                    
	                }
	                agent.setMembertoCollect(memberToCollect);
	                loansdata.createAgent(agent);
	               
	                Intent intent = new Intent (getApplicationContext(), TabsFragmentActivity.class);
	                startActivity(intent);
                }
                else{
                	  Toast.makeText(getApplicationContext(), "Problem Connection", 2).show();
                }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Problem Connection", 2).show();
            e.printStackTrace();
        }
    }
    
   public String planB (String alamat){
		String data = null;
		double lat = 0;
		double lng = 0;
		String location = alamat;
		List<HashMap<String, String>> places = null;
    	String url = "https://maps.googleapis.com/maps/api/geocode/json?";
        try {
            location = URLEncoder.encode(location, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String address = "address=" + location;
        String sensor = "sensor=false";
        url = url + address + "&" + sensor;
        try{
            data = downloadUrl(url);
        }catch(Exception e){
            Log.d("Background Task",e.toString());
        }      
        System.out.println("DAPET DATA ? " + data);      
        GeocodeJSONParser parser = new GeocodeJSONParser();
        try{
            JSONObject jObject = new JSONObject(data);
            /** Getting the parsed data as a an ArrayList */
            places = parser.parse(jObject);
        }catch(Exception e){
            Log.d("Exception",e.toString());
        }       
        System.out.println("CEK LIST ::::: " + places);
        for(int i=0;i<places.size();i++){
            // Getting a place from the places list
            HashMap<String, String> hmPlace = places.get(i);
            // Getting latitude of the place
            lat = Double.parseDouble(hmPlace.get("lat"));
            // Getting longitude of the place
            lng = Double.parseDouble(hmPlace.get("lng"));
            // Getting name
            String name = hmPlace.get("formatted_address");
            
            System.out.println("HASIL JSONPARSING " + lat + " " + lng);
            
        }
        bafer = 2;
        return lat + ", " + lng;
	}
	
	private String downloadUrl(String strUrl){
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
        		StrictMode.ThreadPolicy policy = new   
        		    StrictMode.ThreadPolicy.Builder().permitAll().build();
        		    StrictMode.setThreadPolicy(policy);
        	
        		    HttpClient httpclient = new DefaultHttpClient();
        	
        		    // Prepare a request object
        		    HttpGet httpget = new HttpGet(strUrl); 
        	
        		    // Execute the request
        		    HttpResponse response;
        		        response = httpclient.execute(httpget);
        		        // Examine the response status
        		        //Log.i("Info",response.getStatusLine().toString());  Comes back with HTTP/1.1 200 OK
        	
        		        // Get hold of the response entity
        		        HttpEntity entity = response.getEntity();
        	
        		        if (entity != null) {
        		            InputStream instream = entity.getContent();
        		        
        	
            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            
            br.close();
        }
        		        iStream.close();
        	            urlConnection.disconnect();
        	            System.out.println("CEK DATA " + data);

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }
        return data;
}
	
	private class LoadingProgress extends AsyncTask<Void, Void, Void>{
		ProgressDialog dialog;
		private Context context;
		
		public LoadingProgress (Context context){
		this.context = context;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub	
            getLoanDatafromServer();
            dialog.dismiss();
            finish();		
			return null;
		}

		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			dialog = new ProgressDialog(context);;
			dialog.setCancelable(false);
			dialog.setMessage("Downloading Data From Server\nPlease Wait...");
			dialog.isIndeterminate();
			dialog.show();
			
			
		}
		
	}
	
 //////////////////////////////// AYAMBAKAR PRODUCTIOn
    
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString("message");
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			
			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			
			// Showing received message
//			lblMessage.append(newMessage + "\n");			
		//	Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
			
			// Releasing wake lock
			WakeLocker.release();
		}
	};
	
	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}
	
	//////////////////////////////// AYAMBAKAR PRODUCTION
    
}

