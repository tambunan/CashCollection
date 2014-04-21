package com.teravin.collection.fragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.teravin.collection.online.R;
import com.teravin.adapter.*;
import com.teravin.collection.online.GPSTracker;
import com.teravin.collection.online.LoginActivity;
import com.teravin.collection.online.Success;
import com.teravin.service.PaymentService;
import com.teravin.util.APIBlink;

/**
 * Created by dumatambunan on 3/3/14.
 */
public class SettlementFragment extends BaseFragment implements APIBlink{
	Button btnSettlement;
	TextView tvDate, tvTotalTransaction, tvTotalSettlement;
	private ListView listdata1;
	Connection conn;
	private int totalRecord;
	public String trxDate;
	private String strTotalSettlement="";
	private String[] settlementListArr;
	ArrayList<HashMap<String, String>> trxOfflineList;
	ArrayList<HashMap<String, String>> settlementList;
	MySQLiteHelper dbHelper;
	private NumberFormat rupiahFormat = new DecimalFormat("'Rp '#,###");
	private LoansDataSource loandatasource;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.settlement, container, false);

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = df.format(c.getTime());

		conn = new Connection(getActivity().getApplicationContext());
		btnSettlement = (Button) mView.findViewById(R.id.btnSettlement);
		tvDate = (TextView) mView.findViewById(R.id.tanggal);
		tvTotalTransaction = (TextView) mView.findViewById(R.id.total_trx);
		tvTotalSettlement = (TextView) mView.findViewById(R.id.total_settlement);
		listdata1 = (ListView) mView.findViewById(R.id.listsettlement);

		if(conn.isConnectingToInternet()){
			/*ONLINE*/
			makeHttpRequestSettlementList getDataSettlement = new makeHttpRequestSettlementList(getActivity());
			getDataSettlement.execute();
		}
		else{
			/*OFFLINE*/

			dbHelper = new MySQLiteHelper(getActivity());
			loandatasource = new LoansDataSource(getActivity().getApplicationContext());
			settlementList=loandatasource.getListTrxOffline();
			System.out.println("settlement list : " + settlementList);
			setStatus();
			for  (HashMap<String, String> map : settlementList)
				for  (Map.Entry<String, String> mapEntry : map.entrySet())
				{
					String key = mapEntry.getKey();
					String value = mapEntry.getValue();
					if  (key.equals("totalAmount")){
						mapEntry.setValue(rupiahFormat.format(Double.parseDouble(value)));
					}
				}
			ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), settlementList,
					R.layout.content_settlement, new String[] { "customerName", "totalAmount"}, new int[] { R.id.customername,
				R.id.amount});
			listdata1.setAdapter(adapter);          
			listdata1.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					HashMap<String, String> tes = settlementList.get(position);
					CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox1);
					checkBox.setChecked(!checkBox.isChecked());
					if (checkBox.isChecked()){
						tes.put("status", "true");
					} else {
						tes.put("status", "false");
					}
				}
			});

			tvDate.setText(formattedDate);  
			tvTotalTransaction.setText(loandatasource.getTotalTrx());
			tvTotalSettlement.setText(rupiahFormat.format(Double.parseDouble(loandatasource.getTotalAmountTrx())));

		}

		btnSettlement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				//				Intent inten = new Intent(getActivity(), BackgroundService.class);
				//				getActivity().stopService(inten);
				//		        Toast.makeText(getActivity().getApplicationContext(), "Upload Complete", Toast.LENGTH_LONG).show();



				if(conn.isConnectingToInternet()){
					uploadData();
				}
				else{
					Toast.makeText(getActivity(), "There is no internet access. Just activated your internet access", 2).show();
				}
			}
		});
		return mView;
	}

	private class makeHttpRequestSettlementList extends AsyncTask<Void, Void, Boolean> implements APIBlink{

		ProgressDialog dialog;
		private Context context;

		private makeHttpRequestSettlementList (Context context){
			this.context = context;
		}

		protected void onPreExecute(){
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Downloading Data From Server\nPlease Wait...");
			dialog.isIndeterminate();
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			settlementList = new ArrayList<HashMap<String, String>>();
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair("callback", ""));

			PaymentService jsonFromServer = new PaymentService(getActivity());
			JSONObject jsonListPayment = jsonFromServer.sendHTTPRequest(settlementListAPI,nameValuePair );
			System.out.println("jsonList Settlement ::: " + jsonListPayment);
			if(jsonListPayment != null){


				/*
	    	 jsonListPayment ::: {"total":5,"action":"settleList","callback":"",
	    	 "trxList":[{"customerName":"cherynaa","trxAmount":2000000,"nextCollectionDate":"",
	    	 "paymentFlag":"F","remark":"","refNo":"190320140000005","invoiceNo":"03140001",
	    	 "trxDate":"19\/03\/2014 14:45:15"},{"customerName":"cherynaa","trxAmount":1500000,
	    	 "nextCollectionDate":"","paymentFlag":"F","remark":"","refNo":"190320140000006",
	    	 "invoiceNo":"03140002","trxDate":"19\/03\/2014 15:56:46"},{"customerName":"cherynaa",
	    	 "trxAmount":1400000,"nextCollectionDate":"","paymentFlag":"F","remark":"",
	    	 "refNo":"190320140000007","invoiceNo":"03140003","trxDate":"19\/03\/2014 16:35:40"},
	    	 {"customerName":"cherynaa","trxAmount":500000,"nextCollectionDate":"","paymentFlag":"F",
	    	 "remark":"","refNo":"190320140000008","invoiceNo":"03140006","trxDate":"19\/03\/2014 16:40:34"},
	    	 {"customerName":"cherynaa","trxAmount":4500000,"nextCollectionDate":"","paymentFlag":"F",
	    	 "remark":"","refNo":"190320140000009","invoiceNo":"03140004","trxDate":"19\/03\/2014 16:55:22"}],
	    	 "controller":"agent","totalSettlement":10000000}
				 * */


				try{
					settlementList.clear();
					JSONArray arrSettlement = jsonListPayment.getJSONArray("trxList");
					totalRecord = jsonListPayment.getInt("total");
					strTotalSettlement = jsonListPayment.getString("totalSettlement");
					System.out.println("3");
					settlementListArr = new String[totalRecord];
					System.out.println("1");

					for (int i = 0; i < arrSettlement.length(); i++) {
						System.out.println("2");
						JSONObject json = arrSettlement.getJSONObject(i);
						String custName = json.getString("customerName");
						String trxAmount = json.getString("trxAmount");
						String refNo= json.getString("refNo");
						String paymentFlag = json.getString("paymentFlag"); 
						trxDate = json.getString("trxDate"); 

						HashMap<String, String> mapsettle = new HashMap<String, String>();
						mapsettle.put("customerName", custName);
						mapsettle.put("totalAmount",  trxAmount);
						mapsettle.put("refNo", refNo);
						mapsettle.put("paymentFlag", paymentFlag);
						mapsettle.put("status", "false");
						settlementList.add(mapsettle);
					}
				}catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Toast.makeText(context.getApplicationContext(), "Problem processing data", Toast.LENGTH_LONG).show();
			}
			return true;
		}

		protected void onPostExecute(Boolean result){
			super.onPostExecute(null);
			if (result){
				dialog.dismiss();
			}

			tvTotalTransaction.setText(String.valueOf(totalRecord));
			tvTotalSettlement.setText(rupiahFormat.format(Double.parseDouble(strTotalSettlement)));
			tvDate.setText(trxDate);

			for (HashMap<String, String> map : settlementList)
				for (Map.Entry<String, String> mapEntry : map.entrySet())
				{
					String key = mapEntry.getKey();
					String value = mapEntry.getValue();
					if (key.equals("totalAmount")){
						mapEntry.setValue(rupiahFormat.format(Double.parseDouble(value)));
					}
				}
			setStatus();
			ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), settlementList,
					R.layout.content_settlement, new String[] { "customerName", "totalAmount"}, new int[] { R.id.customername,
				R.id.amount});
			listdata1.setAdapter(adapter);
			listdata1.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					HashMap<String, String> tes = settlementList.get(position);
					CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox1);
					checkBox.setChecked(!checkBox.isChecked());
					if (checkBox.isChecked()){
						tes.put("status", "true");
						System.out.println("isChecked");
					} else {
						tes.put("status", "false");
						System.out.println("not isChecked");
					}
				}
			});
		}

	}

	public void uploadData(){
		UploadSettlement nih = new UploadSettlement(getActivity());
		nih.execute();
		
	}

	public void setStatus(){
		HashMap<String, String> ayambakar = new HashMap<String, String>();
		for (int a = 0 ; a < settlementList.size() ; a++){
			HashMap cus = settlementList.get(a);
			cus.put("status", "true");
		}
	}
	
	
	private class UploadSettlement extends AsyncTask<Void, Void, Boolean>{
    	Context contextUpl;
    	ProgressDialog dialog;
    	JSONObject jsonResponse;
    	
    	public UploadSettlement (Context context) {
    		this.contextUpl = context;
    	}
    	
    	protected void onPreExecute(){
			super.onPreExecute();
			dialog = new ProgressDialog(contextUpl);
			dialog.setCancelable(false);
			dialog.setMessage("Upload in proccess\nPlease Wait...");
			dialog.isIndeterminate();
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			System.out.println("settlementList ::: " + settlementList);
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			for(Map<String, String> map : settlementList)
			{
				if (map.get("status").equals("true")){
					nameValuePair.add(new BasicNameValuePair("refNo", map.get("refNo")));
				} 
				else{
					System.out.println("uploadData isChecked nameValuePair");
				}
			}

			try {
				System.out.println("nameValuePair ::: " + nameValuePair);
				PaymentService jsonParser = new PaymentService(getActivity());
				jsonResponse = jsonParser.sendHTTPRequest(settlementAPI, nameValuePair );

				
				
				return true;
			}catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity(),"There's something wrong when uploading",2).show();
				return true;
			}
			
		}
    	
		protected void onPostExecute(Boolean result){
			super.onPostExecute(null);
			if (result){
				dialog.dismiss();
			}
			try{
			if(jsonResponse != null){

				
				if(jsonResponse.getString("status").equalsIgnoreCase("Y")){
					Intent intentSettlementSuccess = new Intent (getActivity().getApplicationContext(), Success.class);
					intentSettlementSuccess.putExtra("layout", "settlement");
					intentSettlementSuccess.putExtra("status", "success");
					startActivity(intentSettlementSuccess);
					
				}
				else{
					Intent intentSettlementSuccess = new Intent (getActivity().getApplicationContext(), Success.class);
					intentSettlementSuccess.putExtra("layout", "settlement");
					intentSettlementSuccess.putExtra("status", "fail");
					startActivity(intentSettlementSuccess);
				}
			}else{
				Toast.makeText(getActivity(),"Problem prosesing data",2).show();
			}
			} catch (Exception e){
				e.printStackTrace();
				Log.d("Settlement Fragment", "Error at processing data from jsonResponse!");
			}

		}
	}

}


