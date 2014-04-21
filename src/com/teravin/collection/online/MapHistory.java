/* 
 * 
 * AyamBakar production
 * 
 */


package com.teravin.collection.online;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import org.json.JSONArray;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teravin.collection.fragment.ActivityListFragment;
import com.teravin.collection.fragment.PaymentFragment;
import com.teravin.collection.online.R;
import com.teravin.service.DownloadService;
import com.teravin.util.APIBlink;
import com.teravin.adapter.LoansDataSource;
import com.teravin.adapter.MySQLiteHelper;

public class MapHistory extends FragmentActivity {
	public static final String TAG = MapHistory.class.getSimpleName();
	ArrayList<HashMap<String, String>> loanList;
	private LoansDataSource loandatasource;
	String coba = "";
	String coba1 = "";
	String coba2 = "";
	String BaferC = "";
	String BaferA = "";
	String BaferP = "";
	Double [] latitude1;
    Double [] longitude1; 
    GoogleMap mMap;
   Double lati, longLat;	
	GPSTracker gps;
	ListView list1;
	ArrayList<HashMap<String, String>> grup = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        list1 = (ListView) findViewById(R.id.listdatamap);
        
        getDataFromServer ambil = new getDataFromServer(this);
        ambil.execute();
        
        
//        gps = new GPSTracker(getApplicationContext());
//        final double latitude = gps.getLatitude();
//        final double longitude = gps.getLongitude(); 
        
        String datacords = ActivityListFragment.coordsah;
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
//        loandatasource = new LoansDataSource(getApplicationContext());
//        loanList=loandatasource.getLoanActivityDetails(); 
//        setData();
//        addMarkerOnMap();
        
        
        
//        for (HashMap<String, String> map : loanList)
//            for (Entry<String, String> mapEntry : map.entrySet())
//            {
//            	String key = mapEntry.getKey();
//                String value = mapEntry.getValue();
//                if (key.equals("coordinate")){   
//                	coba = mapEntry.getValue();
//                	BaferC = BaferC + "," + coba;		//BaferC, variabel buat nampung file sementara buat Coordinate
//                	System.out.println("LIAT YANG INI " + coba);
//                }
//                if (key.equals("address1")){
//                	coba1 = mapEntry.getValue();
//                	BaferA = BaferA + "~" + coba1;		// BaferA, variabel buat nampung file sementara buat Address
//                	System.out.println("LIAT YANG INI YAH " + coba1);
//                }
////                if (key.equals("paystatus")){
////                	coba2 = mapEntry.getValue();
////                	BaferP = BaferP + "~" + coba2;		// BaferA, variabel buat nampung file sementara buat Paystatus
////                	System.out.println("LIAT YANG INI YAH " + coba2);
////                }
//                
//            }
//        
//        String [] PecahC = BaferC.split(","); 
//        for (int a = 0 ; a < PecahC.length ; a++){
//        	PecahC[a] = PecahC[a].trim();
//        	System.out.println("ini koordinat setelah di pecah = " + PecahC[a]);
//        }
//        
//       String [] PecahA = BaferA.split("~"); 
//        for (int b = 0 ; b < PecahA.length ; b++){
//        	PecahA[b] = PecahA[b].trim();
//        	System.out.println("ini alamat setelah di pecah = " + PecahA[b]);
//        }
//        
////        String [] PecahP = BaferP.split("~"); 
////        for (int j = 0 ; j < PecahA.length ; j++){
////        	PecahA[j] = PecahA[j].trim();
////        	System.out.println("ini paystatus setelah di pecah = " + PecahP[j]);
////        }        
//        
//        int x= 1;
//        for (int i = 1; i < PecahC.length  ; i = i +2) {
//        	lati=Double.parseDouble(PecahC[i]);
//       	   longLat=Double.parseDouble(PecahC[i+1]);
//       	 LatLng latLng = new LatLng(lati, longLat);
//       	  String Address = PecahA[x];
//       	  String status;
////       	  if (PecahP[x].equals("1")){
////       		  status = "Pay";
////       	  } else {
////       		  status = "Not Pay";
////       	  }
//        	  mMap.addMarker(new MarkerOptions()
//        	  .position(latLng)
//        	  .title(Address)
////        	  .snippet(status)
//        	  );
//        	  System.out.println(lati + " " + longLat + " " + PecahA[x]);
//        	  x++;
//        	 }
        
        
        list1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> ambil = grup.get(position);
				String lati = ambil.get("langt");
				String longi = ambil.get("longt");
				String alamat = ambil.get("customerName");
				Toast.makeText(MapHistory.this, "Showing payment of " + alamat, Toast.LENGTH_LONG).show();
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lati), Double.parseDouble(longi)), 17));
			}
		});
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
            }  
            grup.add(gruphm);
            
        }
    }
    
    public void addMarkerOnMap(){
    	for(int a = 0; a<grup.size() ; a++){
    		HashMap<String, String> kelompok = grup.get(a);
    		System.out.println("COBA CEK DAPET GAK NIH DARI HASHMAP? " + kelompok.get("coordinate"));
    		System.out.println("COBA CEK DAPET GAK NIH DARI HASHMAP ADDRESS? " + kelompok.get("alamat"));
    		System.out.println("COBA CEK DAPET GAK NIH DARI HASHMAP PAYSTATUS? " + kelompok.get("bayar"));
     lati = Double.parseDouble(splitTheCoordinateLatitude(kelompok.get("coordinate")));
     longLat = Double.parseDouble(splitTheCoordinateLongitude(kelompok.get("coordinate")));
   	 LatLng latLng = new LatLng(lati, longLat);
   	 String Address = kelompok.get("alamat");
   	  String status;
   	  String paystatus = kelompok.get("bayar");
    	  mMap.addMarker(new MarkerOptions()
    	  .position(latLng)
    	  .title(Address));
    	 }
    System.out.println("SEMUA BERHASIL DIJALANKAN!");
    }
    
 private class getDataFromServer extends AsyncTask<Void, Void, Boolean> implements APIBlink{
    	
    	Context contextasync;
    	
    	ProgressDialog dialog;

    	
    	public getDataFromServer (Context context){
    		this.contextasync = context;
    	}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			DownloadService download = new DownloadService(getApplicationContext());
			JSONObject jsonData;
			try {
				jsonData = download.sendRequestDataLoan(historyPointList);
				
				System.out.println("===== cek JSON DATA " + jsonData);
				
	/*			 cek JSON DATA {"total":2,"action":"historyPointList","callback":"callback",
	 * 			"trxList":[{"customerName":"Hanbin","langt":"-6.2107064","longt":"106.8212811"},
	 * 			{"customerName":"Taehyun","langt":"-6.2107107","longt":"106.8212757"}],"controller":"agent"}

	 */

				grup.clear();
				 JSONArray arrTrx = jsonData.getJSONArray("trxList");
//				 available = jsonData.getString("available");
//                 outstanding = jsonData.getString("oustanding");
//	                int totalRecord = jsonData.getInt("total");
	                System.out.println("====== CEK SAMPE SINI GAK ?");

	                for (int i = 0; i < arrTrx.length(); i++) {
	                	HashMap<String, String> masuk = new HashMap<String, String>();
	                    JSONObject json = arrTrx.getJSONObject(i); 
	                    String customerName = json.getString("customerName");
	                    String latitude = json.getString("langt");
	                    String longitude = json.getString("longt");
//	                    String trxdate = json.getString("trxDate");
//	                    String invoice = json.getString("invoiceNo");
//	                    String remark = json.getString("remark");
//	                    String date = json.getString("nextCollectionDate");
	                    
	                    masuk.put("customerName", customerName);
	                    masuk.put("langt", latitude);
	                    masuk.put("longt", longitude);
//	                    masuk.put("trxDate", trxdate);
//	                    masuk.put("invoiceNo", invoice);
//	                    masuk.put("remark", remark);
//	                    masuk.put("nextCollectionDate", date);
	                    
	                    grup.add(masuk);
	                } 
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
    	
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			dialog = new ProgressDialog(contextasync);
			dialog.setCancelable(false);
			dialog.setMessage("Downloading Data From Server\nPlease Wait...");
			dialog.isIndeterminate();
			dialog.show();
			
		}
		
		protected void onPostExecute(Boolean result){
			super.onPostExecute(null);
			
			
			
			for (int a = 0; a < grup.size() ; a++){
				HashMap<String, String> hm = grup.get(a);
				String lati = hm.get("langt");
				String longi = hm.get("longt");
				String name = hm.get("customerName");
				  mMap.addMarker(new MarkerOptions()
		    	  .position(new LatLng(Double.parseDouble(lati), Double.parseDouble(longi)))
		    	  .title(name)
		    	  .snippet(""));
		    	 }
			ArrayList<HashMap<String, String>> aiueo = new ArrayList<HashMap<String,String>>();
			aiueo = ActivityListFragment.data;
			 ListAdapter adapter = new SimpleAdapter(MapHistory.this, aiueo,
		                R.layout.content_activity_list, new String[] { "","","", "customerName"}, new int[] { R.id.namecustomer,
		                R.id.jumlahamount, R.id.label_amount, R.id.jumlahamount});
			 list1.setAdapter(adapter);
		        
			
//			count=data.size();
//			if (count == 0){
//				btnHistory.setVisibility(View.GONE);
//			}
//			System.out.println("====== CEK available ?" + available + " " + outstanding +"::::: count ::"+ count);
//			tvAvailable.setText(rupiahFormat.format(Double.parseDouble(available)));
//			tvOutstanding.setText(rupiahFormat.format(Double.parseDouble(outstanding)));

//			ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), data,
//	                R.layout.content_activity_list, new String[] { "customerName", "amount"}, new int[] { R.id.namecustomer,
//	                R.id.jumlahamount});
//	        listView.setAdapter(adapter);
	        
			 if (result){
					dialog.dismiss();
				}
			
		}
		
    }
    
}







