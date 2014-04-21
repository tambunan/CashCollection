package com.teravin.collection.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.teravin.collection.online.PlaceJSONParser;
import com.teravin.collection.online.R;
import com.teravin.collection.online.GPSTracker;
import com.teravin.collection.online.LoginActivity;
import com.teravin.util.APIBlink;

/**
 * Created by dumatambunan on 1/13/14.
 */
public class DepositPointClass extends Activity implements APIBlink, OnItemClickListener {
	
	ArrayList<HashMap<String, String>> listBank = new ArrayList<HashMap<String,String>>();
	ListView tampil;
	GPSTracker gps;
	ProgressDialog dialog;
	public static String coords;
	int networkFix = 0;
	LocationListener myNetworkLocationListener;
	String TAG = "cashcollection";
	LocationManager myLocationManager = null;
	

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.depositpoint);
        
        getCoordinateNih();
        startJSONParsing();
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //datanya diambil dari loadList aja, pake position...jangan dari view..
    	System.out.println("CEKCEKCdH " + position + " " + id);
        final HashMap<String, String> loan = listBank.get(position);
        
        final String name = loan.get("name");
        System.out.println("COBA INI " + name);
        final String coordinate = loan.get("coordinate");
        System.out.println("COBA INI " + coordinate);
        String [] pecah = coordinate.split(",");
        String destLatitude = pecah[0];
        String destLongitude = pecah[1];
        		
        String [] pecah2 = coords.split(",");
        String sourceLatitude = pecah2[0];
        String sourceLongitude = pecah2[1];
        
        System.out.println("DAPET GAK NIH SOURCE DESTINATIONNYA? " + sourceLatitude + " " + sourceLongitude);
        System.out.println("DAPET GAK NIH DESTINATIONNYA? " + destLatitude + " " + destLongitude);   
        		
        final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ sourceLatitude + "," + sourceLongitude + "&daddr=" + (destLatitude + "," + destLongitude)));
         intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                             startActivity(intent);

    }
    
    public void startJSONParsing(){	
		gps = new GPSTracker(this);
		Double mLatitude = gps.getLatitude();
		Double mLongitude = gps.getLongitude();	
		StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		sb.append("location="+mLatitude+","+mLongitude);
//		sb.append("&radius=20000");		
		sb.append("&rankby=distance");	
//		sb.append("&name=" + "bii");
		sb.append("&types="+"bank");
		sb.append("&sensor=true");
		sb.append("&key=AIzaSyDhekpmejwMkQ4CnID5ZyckI2gUfacG56Y");
        PlacesTask placesTask = new PlacesTask(this);		        			        
        placesTask.execute(sb.toString());
	}
	
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);                
                urlConnection = (HttpURLConnection) url.openConnection();                
                urlConnection.connect();                
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb  = new StringBuffer();
                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                data = sb.toString();
                br.close();
        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
    }         


	private class PlacesTask extends AsyncTask<String, Integer, String>{
		String data = null;
		Context contexta;
		
		
		public PlacesTask (Context context){
			this.contexta = context;
		}
		
		
		@Override
		protected String doInBackground(String... url) {
			try{
				data = downloadUrl(url[0]);
			}catch(Exception e){
				 Log.d("Background Task",e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result){			
			ParserTask parserTask = new ParserTask();
			parserTask.execute(result);
		}
		
		@Override
		protected void onPreExecute(){			
			dialog = new ProgressDialog(contexta);;
			dialog.setCancelable(false);
			dialog.setMessage("Please Wait...");
			dialog.isIndeterminate();
			dialog.show();
		}
		

		
	}
	
	private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
		JSONObject jObject;
		@Override
		protected List<HashMap<String,String>> doInBackground(String... jsonData) {		
			List<HashMap<String, String>> places = null;			
			PlaceJSONParser placeJsonParser = new PlaceJSONParser();        
	        try{
	        	jObject = new JSONObject(jsonData[0]);	        	
	            places = placeJsonParser.parse(jObject);
	        }catch(Exception e){
	                Log.d("Exception",e.toString());
	        }
	        return places;
		}
		
		  public float countDistance(Double sourceLatitude, Double sourceLongitude, Double destLatitude, Double destLongitude){
				Location dari = new Location("Dari");
				Location menuju = new Location("Menuju");
				
				dari.setLatitude(sourceLatitude);
				dari.setLongitude(sourceLongitude);
				
				menuju.setLatitude(destLatitude);
				menuju.setLongitude(destLongitude);
				
				float result = dari.distanceTo(menuju);
				return result;
			}
			
		

		@Override
		protected void onPostExecute(List<HashMap<String,String>> list){			
			for(int i=0;i<list.size();i++){
	            HashMap<String, String> hmPlace = list.get(i);
	            double lat = Double.parseDouble(hmPlace.get("lat"));	            
	            double lng = Double.parseDouble(hmPlace.get("lng"));
	            String name = hmPlace.get("place_name");
	            String vicinity = hmPlace.get("vicinity");
	            System.out.println("CEK NAME :::::: " + name);
//	            String targetName = name.toLowerCase();
//	            if (targetName.contains("bii")){														//plus
	            HashMap<String, String> HM = new HashMap<String, String>();
	            HM.put("name", name);
	            HM.put("alamat", vicinity);
	            HM.put("coordinate", lat +","+lng);
	            Double sourceLatitude = gps.getLatitude();
	            Double sourceLongitude = gps.getLongitude();
	            float distance = countDistance(sourceLatitude, sourceLongitude, lat, lng);
	            HM.put("distance", String.format("%.1f",distance)+ " m");
	            listBank.add(HM);	
			}
	            dialog.dismiss();
	            tampil = (ListView) findViewById(R.id.depositlist);
	            ListAdapter adapter = new SimpleAdapter(DepositPointClass.this, listBank,
	    	            R.layout.content_activity, new String[] { "cardNo", "name",
	                "alamat", "mobilePhoneNo", "installmentAmount", "distance"}, new int[] { R.id.cardno,
	                R.id.amount, R.id.cardname, R.id.mobileno,R.id.address, R.id.city});
	    	    tampil.setAdapter(adapter);
//	    	    Toast.makeText(DepositPointClass.this, "Showing result of nearby bank", Toast.LENGTH_LONG).show();
	    	    tampil.setOnItemClickListener(DepositPointClass.this);
	    	    
//	            condition = 1;
//	            }																					//plus
			
			Toast.makeText(DepositPointClass.this, "Showing " + listBank.size() +" result of nearby bank", Toast.LENGTH_SHORT).show();
		}		
	}
	
public void getCoordinateNih() {
		myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
				String time;
				networkFix++;
				try {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				Log.d(TAG, "count " + networkFix + " " + "coordinate = " + latitude + "," + longitude);
				if (networkFix == 1){
                String coordinate = latitude +"," + longitude;
                Log.d(TAG, "coordinate = " + coordinate);
//                getar.vibrate(150);
                
                Log.d(TAG, "Service Stopped!");
                myLocationManager.removeUpdates(myNetworkLocationListener);
                coords = latitude +"," + longitude;
                networkFix = 0;
				}
				} catch (Exception e){
					e.printStackTrace();
					String logcat = Log.getStackTraceString(e);
					Log.d(TAG, logcat);
					double latitude = 1.0;
					double longitude = 1.0;
					
					
				}
				
			}
		};
		
		myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
        		 0, 0, myNetworkLocationListener);

	}

  }