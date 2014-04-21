package com.teravin.collection.fragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.teravin.collection.fragment.DepositFragment.MyPerformanceArrayAdapter;
import com.teravin.collection.fragment.DepositFragment.MyPerformanceArrayAdapter.ViewHolder;
import com.teravin.collection.online.R;
import com.teravin.adapter.Connection;
import com.teravin.adapter.LoansDataSource;
import com.teravin.collection.online.Success;
import com.teravin.service.DownloadService;
import com.teravin.util.APIBlink;


/**
 * Created by dumatambunan on 1/6/14.
 */
@SuppressLint("ResourceAsColor")
public class ActivityListFragment extends BaseFragment  implements OnItemClickListener {
	public static final String TAG = PaymentFragment.class.getSimpleName();
	private LoansDataSource loandatasource;
	Connection conn;
	public static ListView listView;
	ArrayList<HashMap<String, String>> loanList;
	Button btnHistory;
	TextView tvAvailable, tvOutstanding;
	int count = 0;
	private NumberFormat rupiahFormat = new DecimalFormat("'Rp '#,###");
	String available="0";
	public static String outstanding="0";
	public static ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
	public static HashMap<String, String> kirim = new HashMap<String, String>();
	ArrayList customerName = new ArrayList();
	ArrayList amount = new ArrayList();
	
	public static String coordsah;
	int networkFix = 0;
	LocationListener myNetworkLocationListener;
	LocationManager myLocationManager = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getCoordinateNih();
		View view = inflater.inflate(R.layout.activity, container, false);
		conn = new Connection(getActivity().getApplicationContext());
		loandatasource = new LoansDataSource(getActivity().getApplicationContext());
		listView=(ListView) view.findViewById(R.id.listactivity);
		btnHistory = (Button) view.findViewById(R.id.btnHistory);
		tvAvailable = (TextView) view.findViewById(R.id.textamountavailable);
		tvOutstanding = (TextView) view.findViewById(R.id.textamountoutstanding);


		if(conn.isConnectingToInternet()){
			/*ONLINE*/

			getDataFromServer ambildata = new getDataFromServer(getActivity());
			ambildata.execute();

		}else{
			/*OFFLINE*/

			data=loandatasource.getLoanActivityDetails();
			System.out.println("data offline activity" + data);
			count=data.size();
			if (count == 0){
				btnHistory.setVisibility(View.GONE);
			}

			for  (HashMap<String, String> map : data)
				for  (Map.Entry<String, String> mapEntry : map.entrySet())
				{
					String key = mapEntry.getKey();
					String value = mapEntry.getValue();
					if  (key.equals("totalAmount")){
						mapEntry.setValue(rupiahFormat.format(Double.parseDouble(value)));
					}
				}

			//            ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), loanList,
			//                    R.layout.content_activity_list, new String[] { "customerName", "totalAmount"}, new int[] { R.id.namecustomer,
			//                    R.id.jumlahamount});
			//            listView.setAdapter(adapter);

			ArrayList<HashMap<String, String>> agentData = new ArrayList<HashMap<String,String>>();
			agentData = loandatasource.getDataAgent();
			String limit = agentData.get(0).get("limitAgent");
			outstanding = agentData.get(0).get("outstanding");
			double warning = 0.8 * Double.parseDouble(limit);

			if (Double.parseDouble(outstanding) > warning){
				tvAvailable.setBackgroundColor(getResources().getColor(R.color.red));
			} else {
				tvAvailable.setBackgroundColor(getResources().getColor(R.color.green));

			}
			Double availableDouble = Double.parseDouble(limit) - Double.parseDouble(outstanding);
			tvAvailable.setText(rupiahFormat.format(availableDouble));
			tvOutstanding.setText(rupiahFormat.format(Double.parseDouble(outstanding)));

		}

		//
		divided();
		MyPerformanceArrayAdapter adapter = new MyPerformanceArrayAdapter(getActivity(), customerName, amount);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);


		btnHistory.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (conn.isConnectingToInternet()){
					Intent inten = new Intent(getActivity().getApplicationContext(), com.teravin.collection.online.MapHistory.class);
					startActivity(inten);
				} else {
					Toast.makeText(getActivity(), "Connectivity problem. Please check your connectivity.", Toast.LENGTH_LONG).show();
				}
			}
		});

		return view;
	}

	public class MyPerformanceArrayAdapter extends ArrayAdapter<String> {
		private final Activity context;
		private final ArrayList customerName;
		private final ArrayList amount;


		class ViewHolder {
			public TextView customerField, amountField;
		}

		public MyPerformanceArrayAdapter(Activity context, ArrayList customerName, ArrayList amount) {
			super(context, R.layout.content_activity_list, customerName);

			this.context = context;
			this.customerName = customerName;
			this.amount = amount;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			// reuse views
			if (rowView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.content_activity_list, null);
				// configure view holder
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.customerField = (TextView) rowView.findViewById(R.id.namecustomer);
				viewHolder.amountField = (TextView) rowView.findViewById(R.id.jumlahamount);
				rowView.setTag(viewHolder);
			}

			// fill data
			ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.customerField.setText((String) customerName.get(position));
			holder.amountField.setText((String) amount.get(position));

			if(data.size() > 0){
				HashMap banding = data.get(position);
				String fullpay="";
				if(conn.isConnectingToInternet()){
					fullpay = (String)banding.get("paymentFlag");
				}else{
					fullpay = (String)banding.get("fullpay");
				}

				if (fullpay.equalsIgnoreCase("F")){
					holder.amountField.setTextColor(getResources().getColor(R.color.darkgreen));
				}
				else{
					holder.amountField.setTextColor(getResources().getColor(R.color.red));
				}
			}
			else{
				Toast.makeText(getActivity(), "Tidak ada data", 2).show();
			}

			return rowView;
		}

		public int getCount () {
			return data.size ();
		}
	} 

	private class getDataFromServer extends AsyncTask<Void, Void, Boolean> implements APIBlink{

		Context contextasync;
		ProgressDialog dialog;
		String limit;


		public getDataFromServer (Context context){
			this.contextasync = context;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			DownloadService download = new DownloadService(getActivity());
			JSONObject jsonData;
			try {
				data.clear();
				jsonData = download.sendRequestDataLoan(activityListAPI);

				if(jsonData != null){

					/*			===== cek JSON DATA {"total":2,"action":"activityList","callback":"callback","oustanding":11000000,
					 * 			"trxList":[{"customerName":"cherynaa","trxAmount":1500000,"nextCollectionDate":"","paymentFlag":"F",
					 * 			"remark":"","refNo":"190320140000006","invoiceNo":"03140002","trxDate":"19\/03\/2014 15:56:46"},
					 * 			{"customerName":"cherynaa","trxAmount":2000000,"nextCollectionDate":"","paymentFlag":"F","remark":"",
					 * 			"refNo":"190320140000005","invoiceNo":"03140001","trxDate":"19\/03\/2014 14:45:15"}],"available":9989000000,
					 * 			"controller":"agent"}
					 */
					JSONArray arrTrx = jsonData.getJSONArray("trxList");
					available = jsonData.getString("available");
					outstanding = jsonData.getString("oustanding");
					limit = jsonData.getString("userLimit");
					int totalRecord = jsonData.getInt("total");

					for (int i = 0; i < arrTrx.length(); i++) {
						HashMap<String, String> masuk = new HashMap<String, String>();
						JSONObject json = arrTrx.getJSONObject(i); 
						String customerName = json.getString("customerName");
						String amount = json.getString("trxAmount");
						String refNo = json.getString("refNo");
						String trxdate = json.getString("trxDate");
						String invoice = json.getString("invoiceNo");
						String remark = json.getString("remark");
						String date = json.getString("nextCollectionDate");
						String paymentFlag = json.getString("paymentFlag");

						masuk.put("customerName", customerName);
						masuk.put("totalAmount", rupiahFormat.format(Double.parseDouble(amount)));
						masuk.put("refNo", refNo);
						masuk.put("trxDate", trxdate);
						masuk.put("invoiceNo", invoice);
						masuk.put("paymentFlag", paymentFlag);
						masuk.put("remark", remark);
						masuk.put("nextCollectionDate", date);

						data.add(masuk);
					} 
				}else{
					Toast.makeText(getActivity(), "Problem contacting server", 2).show();
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
			//data.clear();

		}

		protected void onPostExecute(Boolean result){
			super.onPostExecute(null);

			if (result){
				dialog.dismiss();
			}
			count=data.size();
			if (count == 0){
				btnHistory.setVisibility(View.GONE);
			}

			double warning = 0.8 * Double.parseDouble(limit);
			if (Double.parseDouble(outstanding) > warning){
				tvAvailable.setBackgroundColor(getResources().getColor(R.color.red));
			} else {
				tvAvailable.setBackgroundColor(getResources().getColor(R.color.green));

			}
			tvAvailable.setText(rupiahFormat.format(Double.parseDouble(available)));
			tvOutstanding.setText(rupiahFormat.format(Double.parseDouble(outstanding)));

			divided();
			MyPerformanceArrayAdapter adapter = new MyPerformanceArrayAdapter(getActivity(), customerName, amount);
			listView.setAdapter(adapter);


		}

	}

	public void divided(){
		for (int a = 0 ; a < data.size() ; a++){
			HashMap cek = data.get(a);
			customerName.add(cek.get("customerName"));
			amount.add(cek.get("totalAmount"));
		}

	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		HashMap<String, String> getListActivity;
		if (conn.isConnectingToInternet()){
			getListActivity = data.get(position); 
			kirim.put("customerName", getListActivity.get("customerName"));
			kirim.put("amount", getListActivity.get("totalAmount"));
			kirim.put("refNo", getListActivity.get("refNo"));
			kirim.put("trxDate", getListActivity.get("trxDate"));
			kirim.put("invoiceNo", getListActivity.get("invoiceNo"));
			kirim.put("remark", getListActivity.get("remark"));
			kirim.put("nextCollectionDate", getListActivity.get("nextCollectionDate"));
		} else {
			getListActivity = data.get(position);
			kirim.put("customerName", getListActivity.get("customerName"));
			kirim.put("amount", getListActivity.get("totalAmount"));
			kirim.put("refNo", getListActivity.get("refNo"));
			kirim.put("trxDate", getListActivity.get("trxDate"));
			kirim.put("invoiceNo", getListActivity.get("invoiceNo"));
			kirim.put("remark", getListActivity.get("remark"));
			kirim.put("nextCollectionDate", getListActivity.get("nextCollectionDate"));
		}


		startChildActivity(DetailActivityFragment.newInstance());

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
//	                getar.vibrate(150);
	                
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
	
	@Override
    public void onResume(){
    	super.onStop();
    	Log.d(TAG, "This is onStart");
//    	listView.setOnItemClickListener(onItemClick);
        
    }

}
