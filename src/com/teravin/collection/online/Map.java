package com.teravin.collection.online;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teravin.collection.online.R;
import com.teravin.adapter.LoansDataSource;
import com.teravin.adapter.MySQLiteHelper;
import com.teravin.collection.fragment.ActivityListFragment;
import com.teravin.collection.fragment.PaymentListFragment;
import com.teravin.service.PaymentService;
import com.teravin.util.APIBlink;

public class Map extends FragmentActivity implements OnInfoWindowClickListener {
	public static final String TAG = Map.class.getSimpleName();
	ArrayList<HashMap<String, String>> loanList;
	private LoansDataSource loandatasource;
	String coba = "";
	String coba1 = "";
	String coba2 = "";
    GoogleMap mMap;
   Double lati, longLat;	
	GPSTracker gps;
	ListView list;
	ArrayList<HashMap<String, String>> grup = new ArrayList<HashMap<String,String>>();
	ArrayList<HashMap<String, String>> dataLoanList = new ArrayList<HashMap<String,String>>();
	public int bafer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        list = (ListView) findViewById(R.id.listdatamap);
        grup = PaymentListFragment.kordinat;
        dataLoanList = PaymentListFragment.loanList;
        
        String datacords = PaymentListFragment.coordsah;
		String [] pecah2 = datacords.split(",");
        String latitude = pecah2[0];
        String longitude = pecah2[1];
        Log.d(TAG, "Cek latlng " + latitude +"," + longitude);
        
        
        try{
        	System.out.println("CEK! " + latitude + "," + longitude);
        } catch (Exception e){
        	System.out.println("gadapet datanya nih. data latitude, longitudenya. Location Settingnya di udah di aktifkan belom di hapenyah?");
        }   
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = mapFragment.getMap();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( Double.parseDouble(latitude), Double.parseDouble(longitude)),15));
        mMap.setMyLocationEnabled(true);
        loandatasource = new LoansDataSource(getApplicationContext());
        loanList=loandatasource.getLoanDetails(); 
//        setData();
//        addMarkerOnMap();
  
        mMap.setOnInfoWindowClickListener(this);
        
        
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> ambil = grup.get(position);
				String lati = splitTheCoordinateLatitude(ambil.get("coordinate"));
				String longi = splitTheCoordinateLongitude(ambil.get("coordinate"));
				String alamat = ambil.get("alamat");
				Toast.makeText(Map.this, "Showing " + alamat, Toast.LENGTH_LONG).show();
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lati), Double.parseDouble(longi)), 17));
			}
		});
        
        mapAsyncTask go = new mapAsyncTask(Map.this);
        go.execute();
	
}   
    
    public String splitTheCoordinateLatitude(String coordinate){
    
    	String pecahdeh [] = coordinate.split(",");
    	String latit = pecahdeh[0];
    	
    	return latit;
    }
    
    public String splitTheCoordinateLongitude(String coordinate){
        
    	String pecahdeh [] = coordinate.split(",");
    	String longi = pecahdeh[1];
    	
    	return longi;
    }
    
    public void setData(){
    	for (HashMap<String, String> map : loanList) {
        	HashMap gruphm = new HashMap();
            for (Entry<String, String> mapEntry : map.entrySet())
            {
            	
            	String key = mapEntry.getKey();
                String value = mapEntry.getValue();
                if (key.equals("coordinate")){   
                	coba = mapEntry.getValue();
                	gruphm.put("coordinate", coba);
                	System.out.println("LIAT YANG INI " + coba);
                }
                if (key.equals("address1")){
                	coba1 = mapEntry.getValue();
                	gruphm.put("alamat", mapEntry.getValue());
                	System.out.println("LIAT YANG INI YAH " + coba1);
                }
                if (key.equals("paystatus")){
                	coba2 = mapEntry.getValue();
                	gruphm.put("bayar", mapEntry.getValue());
                	System.out.println("LIAT YANG INI YAH " + coba2);
                } 
            }  
            grup.add(gruphm);
            
        }
    }


	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		String coordinate = arg0.getPosition().toString();
		int letak1 = coordinate.indexOf("(");
		int letak2 = coordinate.indexOf(")");
		coordinate = coordinate.substring((letak1 + 1), letak2);
		String [] pecah = coordinate.split(",");
		String latitude = pecah[0];
		String longitude = pecah[1];
		 final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ gps.getLatitude() + "," + gps.getLongitude() + "&daddr=" + (latitude + "," + longitude)));
         intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                             startActivity(intent);
	}
	
	 public void addMarkerOnMap(){
	    	for(int a = 0; a<grup.size() ; a++){
//	    		HashMap<String, String> kelompok = grup.get(a);
//	    		System.out.println("COBA CEK DAPET GAK NIH DARI HASHMAP? " + kelompok.get("coordinate"));
//	    		System.out.println("COBA CEK DAPET GAK NIH DARI HASHMAP ADDRESS? " + kelompok.get("alamat"));
//	    		System.out.println("COBA CEK DAPET GAK NIH DARI HASHMAP PAYSTATUS? " + kelompok.get("bayar"));
//	     lati = Double.parseDouble(splitTheCoordinateLatitude(kelompok.get("coordinate")));
//	     longLat = Double.parseDouble(splitTheCoordinateLongitude(kelompok.get("coordinate")));
	    		
	    		HashMap<String, String> ambil = grup.get(a);
	    		String alamatku = ambil.get("alamat");
	    		String coordinate = planB(alamatku);
	    		String lati = splitTheCoordinateLatitude(coordinate);
	    		String longLat = splitTheCoordinateLongitude(coordinate);
	    		ambil.put("coordinate", lati+","+longLat);
//	   	 LatLng latLng = new LatLng(Double.parseDouble(lati), Double.parseDouble(longLat));
//	   	 String Address = ambil.get("alamat");
//	   	  String status = "";
//	   	  String paystatus = kelompok.get("bayar");
//	   	  if (paystatus.equals("1")){
//	   		  status = "Pay";
//	   	  } else {
//	   		  status = "Not Pay";
//	   	  }
//	    	  mMap.addMarker(new MarkerOptions()
//	    	  .position(latLng)
//	    	  .title(Address)
//	    	  .snippet(""));
	    	 }
	    System.out.println("SEMUA BERHASIL DIJALANKAN!");
	    }
	 
	 private class mapAsyncTask extends AsyncTask<Void, Void, Boolean>{
	    	ProgressDialog dialog;
	    	private Context context;
	    	
	    	private mapAsyncTask (Context context){
	    		this.context = context;
	    	}
	    	
			@Override
			protected Boolean doInBackground(Void... arg0) {
			
		        	addMarkerOnMap();
		        	return true;
			}
			
			protected void onPreExecute(){
				super.onPreExecute();
				dialog = new ProgressDialog(context);
				dialog.setCancelable(false);
				dialog.setMessage("Opening Map\nPlease Wait...");
				dialog.isIndeterminate();
				dialog.show();
			}
			
			protected void onPostExecute(Boolean result){
				super.onPostExecute(null);
				
				
				ListAdapter adapter = new SimpleAdapter(Map.this, dataLoanList,
		                R.layout.content_loanpaypick, new String[] { "invoiceNo", "totalAmount",
		                "customerName", "phoneNo", "address1"}, new int[] { R.id.cardno,
		                R.id.amount, R.id.cardname, R.id.mobileno,R.id.address1});
		        list.setAdapter(adapter); 
				
				for(int a = 0; a<grup.size() ; a++){		    		
		    		HashMap<String, String> ambil = grup.get(a);
		    		String alamatku = ambil.get("alamat");
		    		String coordinate = planB(alamatku);
		    		String lati = splitTheCoordinateLatitude(coordinate);
		    		String longLat = splitTheCoordinateLongitude(coordinate);
		    		ambil.put("coordinate", lati+","+longLat);
		   	 LatLng latLng = new LatLng(Double.parseDouble(lati), Double.parseDouble(longLat));
		   	 String Address = ambil.get("alamat");
		   	  String status = "";
				
				mMap.addMarker(new MarkerOptions()
		    	  .position(latLng)
		    	  .title(Address)
		    	  .snippet(""));
				
			}
				
				if (result){
					dialog.dismiss();
				}
	    	
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
	
    

}