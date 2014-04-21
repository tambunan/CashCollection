package com.teravin.collection.fragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.model.people.Person.Image;
import com.teravin.collection.online.R;
import com.teravin.adapter.Connection;
import com.teravin.adapter.LoansDataSource;
import com.teravin.adapter.MySQLiteHelper;
import com.teravin.service.DownloadService;
import com.teravin.util.APIBlink;

/**
* Created by dumatambunan on 3/3/14.
*/
public class DepositFragment extends BaseFragment implements APIBlink{
	Button btnDeposit;
	Connection conn;
	TextView tvOutstanding, tvVirtualAccount;
	ListView listDeposit;
	MySQLiteHelper dbHelper;
	private NumberFormat rupiahFormat = new DecimalFormat("'Rp '#,###");
	public LoansDataSource loandatasource;
	public ArrayList<HashMap<String, String>> data1 ;
	public ArrayList<HashMap<String, String>> loanList ;
	ListView list;
	ArrayList settlement = new ArrayList();
    ArrayList deposit = new ArrayList();
    ArrayList date = new ArrayList();
    int save = 0;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.deposit, container, false);
        data1 = new ArrayList<HashMap<String,String>>();
        conn = new Connection(getActivity());
        
        if(conn.isConnectingToInternet()){
	        /*ONLINE*/
	        getDataFromServerDeposit get = new getDataFromServerDeposit(getActivity());
	        get.execute();
        }
        

        btnDeposit = (Button) mView.findViewById(R.id.btndeposit);
        tvOutstanding = (TextView) mView.findViewById(R.id.outstandingcontent);
       
        tvVirtualAccount = (TextView) mView.findViewById(R.id.virtualaccount);
        ImageView image = (ImageView) mView.findViewById(R.id.imageview);
        listDeposit = (ListView) mView.findViewById(R.id.listdeposit);
        

        if (save == 0){
        	divided();
        }
 
        btnDeposit.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
//				startChildActivity(DepositPointFragment.newInstance());
				Intent inten = new Intent(getActivity(), DepositPointClass.class);
				startActivity(inten);
			}
		});   

        return mView;
    }  
    
   public class MyPerformanceArrayAdapter extends ArrayAdapter<String> {
	  private final Activity context;
 	  private final ArrayList settlement;
 	  private final ArrayList deposit;
 	  private final ArrayList date;

 	  class ViewHolder {
 	    public TextView text, text1, date;
 	    public ImageView image;
 	  }

 	  public MyPerformanceArrayAdapter(Activity context, ArrayList settlement, ArrayList deposit, ArrayList date) {
 	    super(context, R.layout.content_deposit, settlement);
 	    
 	    this.context = context;
 	    this.settlement = settlement;
 	    this.deposit = deposit;
 	    this.date = date;
 	  }

 	  @Override
 	  public View getView(int position, View convertView, ViewGroup parent) {
 	    View rowView = convertView;
 	    // reuse views
 	    if (rowView == null) {
 	      LayoutInflater inflater = context.getLayoutInflater();
 	      rowView = inflater.inflate(R.layout.content_deposit, null);
 	      // configure view holder
 	      ViewHolder viewHolder = new ViewHolder();
 	      viewHolder.date = (TextView) rowView.findViewById(R.id.tanggaldeposit);
 	      viewHolder.text = (TextView) rowView.findViewById(R.id.settlement);
 	      viewHolder.text1 = (TextView) rowView.findViewById(R.id.deposit);
 	      viewHolder.image = (ImageView) rowView.findViewById(R.id.imageview);
 	      rowView.setTag(viewHolder);
 	    }

 	    // fill data
 	    ViewHolder holder = (ViewHolder) rowView.getTag();
 	    holder.date.setText((String)date.get(position));
 	    holder.text.setText((String)settlement.get(position));
 	    holder.text1.setText((String)deposit.get(position));
 	    
 	   
 	    HashMap banding = data1.get(position);
 	    String satu = (String)banding.get("settlement");
 	    String dua = (String) banding.get("deposit");
 	    
 	    
 	    double satudoub, duadoub;
	    satudoub = Double.parseDouble(satu.replace("Rp", "").replace(",", ""));
	    duadoub = Double.parseDouble(dua.replace("Rp", "").replace(",", ""));
	    
    	    if (!(satudoub == duadoub)) {
    	      holder.image.setImageResource(R.drawable.redcross);
    	    } else {
    	    	holder.image.setImageResource(R.drawable.checkgreen);
    	    }

    	    return rowView;
    	  }
 	  
 		public int getCount () {
	   	    return data1.size ();
	   	}
    } 
    
private class getDataFromServerDeposit extends AsyncTask<Void, Void, Boolean> implements APIBlink{
    	
    	Context contextasync;
    	
    	ProgressDialog dialog;
    	String limit;
    	String outstanding;

    	
    	public getDataFromServerDeposit (Context context){
    		this.contextasync = context;
    	}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			DownloadService download = new DownloadService(getActivity());
			JSONObject jsonData;
			try {
				data1.clear();
				jsonData = download.sendRequestDataLoan(depositPoint);
				if(jsonData !=null){
					
				
	/*			===== cek JSON DATA {"action":"depositList","callback":"callback",
	 * 				"oustanding":22150000,"trxList":[{"datee":"2014-04-03T17:00:00Z","settlement":15550000,
	 * 				"ok":"N","date":"04\/04\/2014","deposit":0}],"controller":"agent"}


	 */

				
				JSONArray arrTrx = jsonData.getJSONArray("trxList");
                 outstanding = jsonData.getString("oustanding");

	                for (int i = 0; i < arrTrx.length(); i++) {
	                	HashMap<String, String> masuk = new HashMap<String, String>();
	                    JSONObject json = arrTrx.getJSONObject(i); 
	                    String date = json.getString("date");
	                    String deposit = json.getString("deposit");
	                    String settlement = json.getString("settlement");
	                    
	                    masuk.put("date", date);
	                    masuk.put("deposit", rupiahFormat.format(Double.parseDouble(deposit)));
	                    masuk.put("settlement", rupiahFormat.format(Double.parseDouble(settlement)));
	                    data1.add(masuk);
	                } 
				
				}else{
					Toast.makeText(getActivity(), "Problem prosessing data ", 2).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
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
			
			if (result){
				dialog.dismiss();
			}
			 divided();
			    
			tvOutstanding.setText(rupiahFormat.format(Double.parseDouble(outstanding)));
			MyPerformanceArrayAdapter adapter1 = new MyPerformanceArrayAdapter(getActivity(), settlement, deposit, date);
		    listDeposit.setAdapter(adapter1);
			
		}
	 }
	public void divided(){
		for (int a = 0 ; a < data1.size() ; a++){
			HashMap cek = data1.get(a);
			settlement.add(cek.get("settlement"));
			deposit.add(cek.get("deposit"));
			date.add(cek.get("date"));
		}
	}
	@Override
    public void onStop(){
    	super.onStop();
    	
    	listDeposit.setAdapter(null);
    }
    
    
}
