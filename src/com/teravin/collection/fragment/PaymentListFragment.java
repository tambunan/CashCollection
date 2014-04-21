package com.teravin.collection.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teravin.collection.online.DownloadActivity;
import com.teravin.collection.online.R;
import com.teravin.adapter.Connection;
import com.teravin.adapter.JSONParser;
import com.teravin.adapter.LoansDataSource;
import com.teravin.adapter.MySQLiteHelper;
import com.teravin.collection.online.*;
import com.teravin.service.PaymentService;
import com.teravin.util.APIBlink;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

public class PaymentListFragment extends BaseFragment implements APIBlink,OnItemClickListener {
	public static final String TAG = PaymentListFragment.class.getSimpleName();
    private LoansDataSource loandatasource;
    private ListView listView;
    Connection conn;
    int count = 0;
    private Button btnRoute;
    public static ArrayList<HashMap<String, String>> loanList;
    public static String invoiceNo, loanName;
	private String[] paymentListArr;
    public static String amountLoan;
    public String datafromServer;
    private NumberFormat rupiahFormat = new DecimalFormat("'Rp '#,###");
    public static ArrayList<HashMap<String, String>> kordinat = new ArrayList<HashMap<String,String>>();
    DownloadActivity log = new DownloadActivity();
    
    public static String coordsah;
	int networkFix = 0;
	LocationListener myNetworkLocationListener;
	LocationManager myLocationManager = null;
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.paymentlist, container, false);
        
        getCoordinateNih();
        
        listView=(ListView) view.findViewById(R.id.list);
        conn = new Connection(getActivity());
        btnRoute = (Button) view.findViewById(R.id.btnRoute);
        
        if(conn.isConnectingToInternet()){
        	 //ONLINE
        	System.out.println("masuk ONLINE PaymentListFragment");
            makeHttpRequestPaymentList getData = new makeHttpRequestPaymentList(getActivity());
            getData.execute();
//        	onResume();
            
        }else{
        	/*OFFLINE*/
        	System.out.println("masuk OFFLINE PaymentListFragment");
        	loandatasource = new LoansDataSource(getActivity().getApplicationContext());
	        loanList=loandatasource.getLoanDetails();
	        count=loanList.size();
    		if (count == 0){
    			btnRoute.setVisibility(View.GONE);
    		}
	        /*OFFLINE paymentList :: [{totalAmount=450000, mobilePhoneNo=6285697168825,
	         *  phoneNo=6218721986, custName=Haru, invoiceNo=21030007, 
	         *  address=Jalan Mayor Suryotomo No. 31  RT/RW:  / , Kelurahan:  , Kecamatan: , 
	         *  Kota: Yogyakarta , Provinsi: Yogyakarta}, {totalAmount=800000, mobilePhoneNo=6285697168826,
	         *   phoneNo=6218721987, custName=Minzy, invoiceNo=21030008,
	         *    address=Jalan Hayam Wuruk No. 20  RT/RW:  / , Kelurahan:  , Kecamatan: , 
	         *    Kota: Yogyakarta , Provinsi: Yogyakarta}, {totalAmount=650000, 
	         *    mobilePhoneNo=6285697168827, phoneNo=6218721988, custName=Yuna, 
	         *    invoiceNo=21030009, address=Jalan Mas Suharto No. 16  RT/RW:  / ,
	         *  Kelurahan:  , Kecamatan: , Kota: Yogyakarta , Provinsi: Yogyakarta}]
	         * 
	         * */
	        
		   	 for (HashMap<String, String> map : loanList)
		        for (Map.Entry<String, String> mapEntry : map.entrySet())
		        {
		            String key = mapEntry.getKey();
		            String value = mapEntry.getValue();
		            if (key.equals("totalAmount")){
		                 mapEntry.setValue(rupiahFormat.format(Double.parseDouble(value)));
		           }
		       }
			
	        ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), loanList,
	                R.layout.content_loanpaypick, new String[] { "invoiceNo", "totalAmount",
	                "customerName", "phoneNo", "address"}, new int[] { R.id.cardno,
	                R.id.amount, R.id.cardname, R.id.mobileno,R.id.address1});
	        listView.setAdapter(adapter); 
	        
        }
        listView.setOnItemClickListener(this);
       
		
        btnRoute.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		if (conn.isConnectingToInternet()){
                Intent inten = new Intent(getActivity().getApplicationContext(), com.teravin.collection.online.Map.class);
                startActivity(inten);
        		} else {
        			Toast.makeText(getActivity(), "Connectivity problem. Please check your connectivity.", Toast.LENGTH_LONG).show();
        		}
        	}
        });
        
        return view;
	}

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //datanya diambil dari loadList aja, pake position...jangan dari view..
        final HashMap<String, String> loan = loanList.get(position);
        PaymentListFragment f = new PaymentListFragment();
        invoiceNo = loan.get("invoiceNo");
        amountLoan =  loan.get("totalAmount");
        loanName = loan.get("customerName");
        System.out.println("loanName ******* : "+ loanName);
        startChildActivity(PaymentFragment.newInstance());
    }
    
    private class makeHttpRequestPaymentList extends AsyncTask<Void, Void, Boolean> implements APIBlink{
    	ProgressDialog dialog;
    	private Context context;
    	
    	private makeHttpRequestPaymentList (Context context){
    		this.context = context;
    	}
    	
		@Override
		protected Boolean doInBackground(Void... arg0) {
		
	        	
	        	List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	    	    nameValuePair.add(new BasicNameValuePair("callback", ""));
	    	    System.out.println("masuk PaymentListFragment");
	        	PaymentService jsonFromServer = new PaymentService(getActivity());
	        	JSONObject jsonListPayment = jsonFromServer.sendHTTPRequest(paymentListAPI,nameValuePair );
	        	loanList = new ArrayList<HashMap<String, String>>();
	        	/**
	        	 * jsonObject => {"total":2,"action":"paymentList","callback":"",
	        	 * "trxList":[{"invoiceDate":"2014-03-13T17:00:00Z","address1":"jalan kpi",
	        	 * "address2":"jalan gajah mada","invoiceNo":"102200",
	        	 * "memberToCollect":"qq","updatedBy":"","city":"jakarta","id":1,
	        	 * "cif":{"id":1,"class":"Cif"},"totalAmount":10000,"province":"jakarta",
	        	 * "isTransaction":null,"corporateID":"anto123",
	        	 * "uploadDate":"2014-03-17T11:02:55Z",
	        	 * "dueDate":"2014-04-27T17:00:00Z","customerName":"Anto",
	        	 * "lastUpdated":"2014-03-18T07:33:58Z","class":"com.teravin.collection.maintenance.Distribution",
	        	 * "pic":"badooo","kelurahan":"bangko","phoneNo":"6219156462",
	        	 * "kecamatan":"bangko","fileName":"Distribution-1703141802.csv","rt":"11",
	        	 * "uploadBy":"aa","group":{"id":2,"class":"Grup"},"rw":"15",
	        	 * "mobileNo":"681285839900"},
	        	 * ***/
	        	try{

		        	JSONArray arrTrx = jsonListPayment.getJSONArray("trxList");
					int totalRecord = jsonListPayment.getInt("total");
					paymentListArr = new String[totalRecord];
					for (int i = 0; i < arrTrx.length(); i++) {
						JSONObject json = arrTrx.getJSONObject(i);
						HashMap<String, String> hm = new HashMap<String, String>();
						String address1 = json.getString("address1") + " ," + json.getString("city") +" ,"+ json.getString("province") ;
						String address2 = json.getString("address2");
						String invoiceNo = json.getString("invoiceNo");
						String totalAmount = json.getString("totalAmount");
						String customerName = json.getString("customerName");
						String phoneNo = json.getString("phoneNo");
	
						HashMap<String, String> map = new HashMap<String, String>();
	
						map.put("address1", address1);
						map.put("address2",  address2);
						map.put("invoiceNo", invoiceNo);
						map.put("totalAmount", totalAmount);
						map.put("customerName",  customerName);
						map.put("phoneNo", phoneNo);
						
						hm.put("alamat", address1);					
//	                    String coordinate = log.planB(address1);
//		                hm.put("coordinate", coordinate);
		                loanList.add(map);
						kordinat.add(hm);
					}
					
				}
				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	        	return true;
		}
		
		protected void onPreExecute(){
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Downloading Data From Server\nPlease Wait...");
			dialog.isIndeterminate();
			dialog.show();
		}
		
		protected void onPostExecute(Boolean result){
			super.onPostExecute(null);
			if (result){
				dialog.dismiss();
			}
			
			count=loanList.size();
    		if (count == 0){
    			btnRoute.setVisibility(View.GONE);
    		}
    		
			for (HashMap<String, String> map : loanList)
		        for (Map.Entry<String, String> mapEntry : map.entrySet())
		        {
		            String key = mapEntry.getKey();
		            String value = mapEntry.getValue();
		            if (key.equals("totalAmount")){
		                 mapEntry.setValue(rupiahFormat.format(Double.parseDouble(value)));
		           }
		       }
			 ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), loanList,
		                R.layout.content_loanpaypick, new String[] { "invoiceNo", "totalAmount",
		                "customerName", "phoneNo", "address1"}, new int[] { R.id.cardno,
		                R.id.amount, R.id.cardname, R.id.mobileno,R.id.address1});
				 listView.setAdapter(adapter);
		}
    	
    }

    @Override
    public void onResume(){
      super.onResume();
      // Resume any paused UI updates, threads, or processes required
      // by the Fragment but suspended when it became inactive.
//      makeHttpRequestPaymentList getData = new makeHttpRequestPaymentList(getActivity());
//      getData.execute();
    }
	
    public void getCoordinateNih() {
    	myLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    	
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
//                    getar.vibrate(150);
                    
                    Log.d(TAG, "Service Stopped!");
                    myLocationManager.removeUpdates(myNetworkLocationListener);
                    coordsah = latitude +"," + longitude;
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
