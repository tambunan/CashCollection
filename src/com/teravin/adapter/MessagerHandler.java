package com.teravin.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.teravin.collection.fragment.PaymentFragment;
import com.teravin.collection.fragment.PaymentListFragment;
import com.teravin.collection.online.GPSTracker;
import com.teravin.collection.online.LoginActivity;
import com.teravin.collection.online.Success;
import com.teravin.model.LoanTrx;
import com.teravin.model.SendCardData;
import com.teravin.security.Crypto;
import com.teravin.security.SessionManager;
import com.teravin.service.PaymentService;
import com.teravin.util.APIBlink;
import com.teravin.util.Util;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.arith.arithlib;

public class MessagerHandler implements APIBlink {

	public PaymentFragment context;
	//public Context context2;
	Connection conn;
	private JSONObject jsonObj;
	private static final String TAG = "Loan Finance";
	private boolean isRunning = false;
	public String loancard="";
	LoansDataSource loansdata;
	//final Context context2 = this;
	public LoanTrx dataLoanTrx;
	private boolean bPlayDialog = false;
	private boolean bCloseDialog = false;
	ProgressDialog progressDialog;
	private static int autoRefNo = 100001;
	public static String cardNoAgent="";
	public int refNO;
	public static String installment;
	public LoginActivity login;
	public GPSTracker gps;
	
	String codes;
	
	public MessagerHandler(PaymentFragment context) {
		this.context = context;
	}

	public boolean isRunning() {
		return isRunning;
	}


//	@Override
	protected void handleOvertime() {
		Activity activity = context.getActivity();

		if (activity != null) {
			Toast.makeText(context.getActivity(), "Overtime!", Toast.LENGTH_LONG).show();
		}
	}

//	@Override
//	protected void onSwipeCompleted(CardData cardData) {
//		byte[] tempbuf  =new byte[5];
//		byte[] KSNTmp =new byte[20];
//		byte[] KSNSouce =new byte[20];
//		long[] lCount = new long[1];
//
//		byte[] MainKey = new byte[]{
//				(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
//				(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
//				(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
//				(byte)0xff};
//
//		byte[] NowKey = new byte[16];
//		byte[] EncryptDataSouce ;
////		byte[] RealOutData = new byte[cardData.getEncryptData().length] ;
//
////		KSNSouce = cardData.getKsn();
//
//		for (int i=0;i<4;i++)
//			tempbuf[i] = KSNSouce[i+6];
//
//		tempbuf[0] = 0;
//		tempbuf[1] = (byte)(tempbuf[1]& 0x1F);
//		lCount[0] = Util.unsigned4BytesToInt(tempbuf,0);
//
//		new arithlib().jpancount(lCount);
//		new arithlib().jgetnowkey(NowKey,MainKey,lCount,KSNSouce);
//
////		int iLength =  cardData.getEncryptData().length;
////		EncryptDataSouce = new byte[ iLength];
////		EncryptDataSouce = cardData.getEncryptData() ;
////		new arithlib().jDesstring(EncryptDataSouce,(byte)iLength,NowKey,(byte)16,RealOutData,(byte)3);
//
//		Crypto crypto = new Crypto();
//		try{
//			System.out.println("CardData :: "+cardData);
//			byte[] keyByte = crypto.encrypt(NowKey);
//
//			String encryptedKey = Util.toHexString(keyByte);
//
//			SendCardData sendCardData = new SendCardData();
//			sendCardData.setKey(encryptedKey.substring(0,32));
//
//			conn = new Connection(context.getActivity().getApplicationContext());
//			gps = new GPSTracker(context.getActivity().getApplicationContext());
//
//			codes = PaymentListFragment.coordsah;
//			String [] pecah2 = codes.split(",");
//	        String latitude = pecah2[0];
//	        String longitude = pecah2[1];
//	        Log.d(TAG, "Cek latlng " + latitude +"," + longitude);
//
//			if(conn.isConnectingToInternet()){
//				/* ONLINE*/
//				JSONObject jsonObject = sendToServer(sendCardData);
//				if(jsonObject != null ){
//					if(jsonObject.getBoolean("status")){
//						SessionManager sessionManager = new SessionManager(context.getActivity().getApplicationContext());
//						SharedPreferences pref = sessionManager.getPref();
//						String sessionId = pref.getString("JSESSIONID", "");
//
//						refNO = generateRefNo();
//						//installment = context.
//						List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
//						nameValuePair.add(new BasicNameValuePair("invoiceNo", PaymentListFragment.invoiceNo));
//						nameValuePair.add(new BasicNameValuePair("installment",String.valueOf(PaymentFragment.amountchange)));
//						nameValuePair.add(new BasicNameValuePair("langt",latitude));
//						nameValuePair.add(new BasicNameValuePair("longt",longitude));
//						nameValuePair.add(new BasicNameValuePair("deviceRefNo",Integer.toString(refNO)));
//						nameValuePair.add(new BasicNameValuePair("encryptedData", Util.toHexString(sendCardData.getEncryptedData())));
//						nameValuePair.add(new BasicNameValuePair("ksn",Util.toHexString(sendCardData.getKsn()) ));
//						nameValuePair.add(new BasicNameValuePair("key", sendCardData.getKey()));
//						nameValuePair.add(new BasicNameValuePair("JSESSIONID" , sessionId));
//
//						System.out.println("nameValuePair :::: "+nameValuePair);
//
//						LoanTrx dataLoanTrx = new LoanTrx();
//						if (PaymentFragment.action==2){
//							dataLoanTrx.setFullPay("NF");
//							nameValuePair.add(new BasicNameValuePair("nextCollectionDate", PaymentFragment.startDate));
//							nameValuePair.add(new BasicNameValuePair("remark",PaymentFragment.remark));
//						}
//						else{
//							dataLoanTrx.setFullPay("F");
//						}
//						System.out.println("nameValuePair MessageHandler ::  " +nameValuePair );
//						JSONObject sendPayTrx = sendPaymentDataToServer(sendCardData, nameValuePair);
//						/*sendPayTrx :::: {"result":"Y","nextCollectionDate":"",
//						 * "timeStamp":"19\/03\/2014 15:56:46","status":true,"remark":"",
//						 * "deviceRefNo":"2","by":"chery","invoiceNo":"03140002",
//						 * "cardNo":"4511970003776398","installment":"1500000","refNo":"190320140000006",
//						 * "action":"pay","controller":"agent",
//						 * "langt":"-6.2106414","longt":"106.8211793",
//						 * "trxDate":"19\/03\/2014 15:56:46"}*/
//						System.out.println("sendPayTrx :::: "+sendPayTrx);
//						if(sendPayTrx != null){
//							if(sendPayTrx.getBoolean("status")){
//								dataLoanTrx.setCardNo(sendPayTrx.getString("cardNo"));
//								dataLoanTrx.setPaymentFlag("1");
//								dataLoanTrx.setInstallment(sendPayTrx.getString("installment"));
//								dataLoanTrx.setInvoiceNo(sendPayTrx.getString("invoiceNo"));
//								dataLoanTrx.setRefNo(sendPayTrx.getString("refNo"));
//								dataLoanTrx.setFlagTrx("1");
//								dataLoanTrx.setRegData(sendPayTrx.getString("timeStamp"));
//								dataLoanTrx.setNextCollectionDate(sendPayTrx.getString("nextCollectionDate"));
//								dataLoanTrx.setRemark(sendPayTrx.getString("remark"));
//								//update database
//								sendPayLoan(dataLoanTrx);
//								context.showSuccessPayment(dataLoanTrx);
//							}else{
//								Toast.makeText(context.getActivity().getApplicationContext(), "Payment rejected. Please try again", 2).show();
//								context.showFailPayment();
//							}
//						}else{
//							Toast.makeText(context.getActivity().getApplicationContext(), "There is promblem with connectivity",2).show();
//						}
//					}else{
//						Activity activity = context.getActivity();
//
//						if (activity != null) {
//							Toast.makeText(context.getActivity(), "This card is not registered", Toast.LENGTH_LONG).show();
//						}
//					}
//
//				}else{
//					Activity activity = context.getActivity();
//
//					if (activity != null) {
//						Toast.makeText(context.getActivity(), "Problem contacting server", Toast.LENGTH_LONG).show();
//					}
//				}
//			}
//
//			else{
//				/*OFFLINE*/
//				login = new LoginActivity();
//				loansdata = new LoansDataSource(context.getActivity().getApplicationContext());
//				String cardNoAgent = loansdata.getCardNoAgent();
//				String availaibility = loansdata.getAvailableAgent();
//
//
//				if (Double.parseDouble(availaibility) < Double.parseDouble(PaymentFragment.instalmentAmount)){
//					Toast.makeText(context.getActivity().getApplicationContext(), "Your availability less than the instalment", Toast.LENGTH_SHORT).show();
//				}else{
//					refNO = generateRefNo();
//					String _regData = "";
//					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					Date dt = new Date();
//					_regData = dateFormat.format(dt);
//
//					LoanTrx dataLoanTrxOffline = new LoanTrx();
//					dataLoanTrxOffline.setPaymentFlag("1");
//					dataLoanTrxOffline.setInstallment(PaymentFragment.instalmentAmount);
//					dataLoanTrxOffline.setInvoiceNo(PaymentListFragment.invoiceNo);
//					dataLoanTrxOffline.setRefNo(String.valueOf(refNO));
//					dataLoanTrxOffline.setRegData(_regData);
//					dataLoanTrxOffline.setFlagTrx("0");
//					dataLoanTrxOffline.setNextCollectionDate(PaymentFragment.startDate);
//					dataLoanTrxOffline.setRemark(PaymentFragment.remark);
//
//					if (PaymentFragment.action==2){
//						dataLoanTrxOffline.setFullPay("NF");
//						dataLoanTrxOffline.setNextCollectionDate(PaymentFragment.startDate);
//						dataLoanTrxOffline.setRemark(PaymentFragment.remark);
//					}
//					else{
//						dataLoanTrxOffline.setFullPay("F");
//						dataLoanTrxOffline.setNextCollectionDate("");
//						dataLoanTrxOffline.setRemark("");
//					}
//					sendPayLoan(dataLoanTrxOffline);
//				}
//			}
//		} catch (Exception e) {
//			Activity activity = context.getActivity();
//
//			if (activity != null) {
//				Toast.makeText(context.getActivity(), "Problem processing data", Toast.LENGTH_LONG).show();
//			}
//			e.printStackTrace();
//		}
//
//	}

	private void sendPayLoan(LoanTrx dataLoanTrx){
		LoansDataSource loansdata = new LoansDataSource(context.getActivity().getApplicationContext());
		String regData = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt = new Date();
		regData = dateFormat.format(dt);
		loansdata.updateLoan(dataLoanTrx);

		if(!conn.isConnectingToInternet()){
			context.showStatusPay(dataLoanTrx.getInvoiceNo(), dataLoanTrx.getInstallment(), dataLoanTrx.getRegData());
		}
	}

	private JSONObject sendToServer(SendCardData sendCardData){
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(terasoHost + "card3");
		httpClient.getCredentialsProvider()
		.setCredentials(AuthScope.ANY, 
				new UsernamePasswordCredentials("teraso", "teraso"));

		SessionManager sessionManager = new SessionManager(context.getActivity().getApplicationContext());
		SharedPreferences pref = sessionManager.getPref();

		String sessionId = pref.getString("JSESSIONID", "");

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("encryptedData", Util.toHexString(sendCardData.getEncryptedData())));
		nameValuePair.add(new BasicNameValuePair("ksn" , Util.toHexString(sendCardData.getKsn())));
		nameValuePair.add(new BasicNameValuePair("key" , sendCardData.getKey()));
		nameValuePair.add(new BasicNameValuePair("JSESSIONID" , sessionId));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			HttpResponse response = httpClient.execute(httpPost);

			if(response.getStatusLine().getStatusCode() == 200){

				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				StringBuffer sb = new StringBuffer();
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				JSONObject jsonObject = new JSONObject(sb.toString());

				return jsonObject;
			}

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			Log.e("MessagerHandler", e.getMessage());
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
			Log.e("MessagerHandler", e.getMessage());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("MessagerHandler", e.getMessage());
		} 

		return null;
	}


	private JSONObject sendPaymentDataToServer (SendCardData sendCardData, List<NameValuePair> nameValuePair){
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(terasoHost + "collect");
		httpClient.getCredentialsProvider()
		.setCredentials(AuthScope.ANY, 
				new UsernamePasswordCredentials("teraso", "teraso"));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			HttpResponse response = httpClient.execute(httpPost);

			if(response.getStatusLine().getStatusCode() == 200){
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				String line = "";
				StringBuffer sb = new StringBuffer();
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				Log.d(TAG, "sb.toString()22 = "+ sb.toString());

				JSONObject jsonObject2 = new JSONObject(sb.toString());
				Log.d(TAG, "jsonObject22 = "+ jsonObject2);
				return jsonObject2;
			}else{
//				Toast.makeText(getMyContext().getApplicationContext(), "Payment Not Success", 2).show();
				return null;
			}

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			Log.e("MessagerHandler", e.getMessage());
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
			Log.e("MessagerHandler", e.getMessage());
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("MessagerHandler", e.getMessage());
		} 

		return null;
	}
	public int generateRefNo(){
		autoRefNo +=1;
		System.out.println("autoRefNo::: "+ autoRefNo);
		return autoRefNo;
	}

//	@Override
	protected void onSwipeError(int state) {
		Activity activity = context.getActivity();

		if (activity != null) {
			Toast.makeText(context.getActivity(), "Please swipe again", Toast.LENGTH_LONG).show();
		}

//		context.waitForSwipe(15);
	}
	
	private void loadPrefCardAgent(String cardno) {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getActivity().getApplicationContext());
        cardNoAgent = sharedPreferences.getString("cardno", cardno);
        
    }

}
