package com.teravin.collection.fragment;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.arith.arithlib;
import com.bbpos.cswiper.CSwiperController;
import com.teravin.adapter.Connection;
import com.teravin.adapter.LoansDataSource;
import com.teravin.collection.online.GPSTracker;
import com.teravin.collection.online.LoginActivity;
import com.teravin.collection.online.R;
import com.teravin.model.LoanTrx;
import com.teravin.model.SendCardData;
import com.teravin.security.Crypto;
import com.teravin.security.SessionManager;
import com.teravin.service.SwiperCallStateService;
import com.teravin.util.APIBlink;
import com.teravin.util.Util;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by tyapeter on 4/18/14.
 */
public abstract class SwipeFragment extends BaseFragment implements APIBlink {

    private final static String INTENT_ACTION_CALL_STATE = "com.teravin.collection.fragment.CALL_STATE";

    protected CSwiperController cswiperController;
    protected CSwiperController.CSwiperStateChangedListener stateChangedListener;

    protected IncomingCallServiceReceiver incomingCallServiceReceiver;

    public static final String TAG = SwipeFragment.class.getName();

    public String loancard="";
    Connection conn;
    LoansDataSource loansdata;
    public LoanTrx dataLoanTrx;
    private static int autoRefNo = 100001;
    public static String cardNoAgent="";
    public int refNO;
    public static String installment;
    public LoginActivity login;
    public GPSTracker gps;

    String codes;


//    @Override
//    public void onResume() {
//        super.onResume();
//        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
//        sessionManager.checkLogin();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start Swiper

        startCallStateService();

        stateChangedListener = new StateChangedListener();
        cswiperController = CSwiperController.createInstance(getActivity().getApplicationContext(),
                stateChangedListener);
        cswiperController.setDetectDeviceChange(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        startCallStateService();

        stateChangedListener = new StateChangedListener();
        cswiperController = CSwiperController.createInstance(getActivity().getApplicationContext(),
                stateChangedListener);
        cswiperController.setDetectDeviceChange(true);
        View view = _OnCreateView(inflater, container, savedInstanceState);
        return view;
    }

    //	abstract protected void _OnCreate(Bundle savedInstanceState);
    abstract protected View _OnCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState);

    private void loadPref() {
        // getSharedPreferences(yourfile, MODE_PRIVATE);
        //SharedPreferences sharedPreferences = PreferenceManager.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //PreferenceManager.getShare
//        iType = Integer.parseInt(sharedPreferences.getString("key_background", "0"));
//        Log.d(TAG, "iType = " + iType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cswiperController.deleteCSwiper();
        endCallStateService();
        _onDestroy();
    }

    abstract protected void _onDestroy();

    // -----------------------------------------------------------------------
    // CSwiper API
    // -----------------------------------------------------------------------

    private void cardSwipeDetected() {
        Toast.makeText(getActivity(), R.string.ReadingCardData, Toast.LENGTH_SHORT).show();
    }

    private void decodeCompleted(HashMap<String, String> decodeData) {
        String formatID = decodeData.get("formatID");
        String ksn = decodeData.get("ksn");
        String encTracks = decodeData.get("encTracks");
        String track1Length = decodeData.get("track1Length");
        String track2Length = decodeData.get("track2Length");
        String track3Length = decodeData.get("track3Length");
        String randomNumber = decodeData.get("randomNumber");
        String mac = decodeData.get("mac");
        String maskedPAN = decodeData.get("maskedPAN");
        String expiryDate = decodeData.get("expiryDate");
        String cardHolderName = decodeData.get("cardHolderName");
        String partialTrack = decodeData.get("partialTrack");
        String encWorkingKey = decodeData.get("encWorkingKey");

        String separator = "; ";
        StringBuilder sb = new StringBuilder();
        sb.append("formatID: "+formatID + separator);
        sb.append("ksn: "+ksn + separator);
        sb.append("encTracks: "+encTracks + separator);
        sb.append("track1Length: "+track1Length + separator);
        sb.append("track2Length: "+track2Length + separator);
        sb.append("track3Length: "+track3Length + separator);
        sb.append("randomNumber: "+randomNumber + separator);
        sb.append("mac: "+mac + separator);
        sb.append("maskedPAN: "+maskedPAN + separator);
        sb.append("expiryDate: "+expiryDate + separator);
        sb.append("cardHolderName: "+cardHolderName + separator);
        sb.append("partialTrack: "+partialTrack + separator);
        sb.append("encWorkingKey: "+encWorkingKey + separator);
        Log.d(TAG, sb.toString());
        com.teravin.model.CardData cardData = new com.teravin.model.CardData();
        cardData.setEncryptData(encTracks);
        cardData.setExpiryDate(expiryDate);
        cardData.setfCardnumber(maskedPAN);
        cardData.setName(cardHolderName);
        cardData.setKsn(ksn);
//        onSwipeCompleted(cardData);
    }

    protected void onSwipeCompleted(com.teravin.model.CardData cardData) {

        byte[] tempbuf  =new byte[5];
        byte[] KSNTmp =new byte[20];
        byte[] KSNSouce =new byte[20];
        long[] lCount = new long[1];

        byte[] MainKey = new byte[]{
                (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
                (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
                (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
                (byte)0xff};


        byte[] NowKey = new byte[16];
        byte[] EncryptDataSouce ;
//        byte[] RealOutData = new byte[cardData.getEncryptData().length] ;

//        KSNSouce = cardData.getKsn();

        for (int i=0;i<4;i++)
            tempbuf[i] = KSNSouce[i+6];

        tempbuf[0] = 0;
        tempbuf[1] = (byte)(tempbuf[1]& 0x1F);
        lCount[0] = Util.unsigned4BytesToInt(tempbuf, 0);

        new arithlib().jpancount(lCount);
        new arithlib().jgetnowkey(NowKey,MainKey,lCount,KSNSouce);

//        int iLength =  cardData.getEncryptData().length;
//        EncryptDataSouce = new byte[ iLength];
//        EncryptDataSouce = cardData.getEncryptData() ;
//        new arithlib().jDesstring(EncryptDataSouce,(byte)iLength,NowKey,(byte)16,RealOutData,(byte)3);

        Crypto crypto = new Crypto();

        try {
            EditText amountText = (EditText) getView().findViewById(R.id.amount);

            byte[] keyByte = crypto.encrypt(NowKey);

            String encryptedKey = Util.toHexString(keyByte);

            SendCardData sendCardData = new SendCardData();
            sendCardData.setKey(encryptedKey.substring(0, 32));
//            sendCardData.setEncryptedData(cardData.getEncryptData());
            sendCardData.setfCardnumber(cardData.getfCardnumber());
            sendCardData.setL4pan(cardData.getL4pan());
            sendCardData.setName(cardData.getName());
            sendCardData.setExpiryDate(cardData.getExpiryDate());
            sendCardData.setServiceCode(cardData.getServiceCcode());
//            sendCardData.setKsn(cardData.getKsn());
            sendCardData.setCheckSum(cardData.getCheckSum());
            sendCardData.setAmount(Double.parseDouble(amountText.getText().toString().replaceAll("Rp ", "").replaceAll(",", "")));

            conn = new Connection(getActivity().getApplicationContext());
            gps = new GPSTracker(getActivity().getApplicationContext());

            codes = PaymentListFragment.coordsah;
            String [] pecah2 = codes.split(",");
            String latitude = pecah2[0];
            String longitude = pecah2[1];
            Log.d(TAG, "Cek latlng " + latitude +"," + longitude);

            if(conn.isConnectingToInternet()){
				/* ONLINE*/
                JSONObject jsonObject = sendToServer(sendCardData);
                if(jsonObject != null ){
                    if(jsonObject.getBoolean("status")){
                        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
                        SharedPreferences pref = sessionManager.getPref();
                        String sessionId = pref.getString("JSESSIONID", "");

                        refNO = generateRefNo();
                        //installment = context.
                        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                        nameValuePair.add(new BasicNameValuePair("invoiceNo", PaymentListFragment.invoiceNo));
                        nameValuePair.add(new BasicNameValuePair("installment",String.valueOf(PaymentFragment.amountchange)));
                        nameValuePair.add(new BasicNameValuePair("langt",latitude));
                        nameValuePair.add(new BasicNameValuePair("longt",longitude));
                        nameValuePair.add(new BasicNameValuePair("deviceRefNo",Integer.toString(refNO)));
                        nameValuePair.add(new BasicNameValuePair("encryptedData", Util.toHexString(sendCardData.getEncryptedData())));
                        nameValuePair.add(new BasicNameValuePair("ksn",Util.toHexString(sendCardData.getKsn()) ));
                        nameValuePair.add(new BasicNameValuePair("key", sendCardData.getKey()));
                        nameValuePair.add(new BasicNameValuePair("JSESSIONID" , sessionId));

                        Log.d(TAG, "nameValuePair :::: "+nameValuePair);

                        LoanTrx dataLoanTrx = new LoanTrx();
                        if (PaymentFragment.action==2){
                            dataLoanTrx.setFullPay("NF");
                            nameValuePair.add(new BasicNameValuePair("nextCollectionDate", PaymentFragment.startDate));
                            nameValuePair.add(new BasicNameValuePair("remark",PaymentFragment.remark));
                        }
                        else{
                            dataLoanTrx.setFullPay("F");
                        }
                        Log.d(TAG, "nameValuePair MessageHandler ::  " +nameValuePair );
                        JSONObject sendPayTrx = sendPaymentDataToServer(sendCardData, nameValuePair);
						/*sendPayTrx :::: {"result":"Y","nextCollectionDate":"",
						 * "timeStamp":"19\/03\/2014 15:56:46","status":true,"remark":"",
						 * "deviceRefNo":"2","by":"chery","invoiceNo":"03140002",
						 * "cardNo":"4511970003776398","installment":"1500000","refNo":"190320140000006",
						 * "action":"pay","controller":"agent",
						 * "langt":"-6.2106414","longt":"106.8211793",
						 * "trxDate":"19\/03\/2014 15:56:46"}*/
                        Log.d(TAG, "sendPayTrx :::: "+sendPayTrx);
                        if(sendPayTrx != null){
                            if(sendPayTrx.getBoolean("status")){
                                dataLoanTrx.setCardNo(sendPayTrx.getString("cardNo"));
                                dataLoanTrx.setPaymentFlag("1");
                                dataLoanTrx.setInstallment(sendPayTrx.getString("installment"));
                                dataLoanTrx.setInvoiceNo(sendPayTrx.getString("invoiceNo"));
                                dataLoanTrx.setRefNo(sendPayTrx.getString("refNo"));
                                dataLoanTrx.setFlagTrx("1");
                                dataLoanTrx.setRegData(sendPayTrx.getString("timeStamp"));
                                dataLoanTrx.setNextCollectionDate(sendPayTrx.getString("nextCollectionDate"));
                                dataLoanTrx.setRemark(sendPayTrx.getString("remark"));
                                //update database
                                sendPayLoan(dataLoanTrx);
                                showSuccessPayment(dataLoanTrx);
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(), "Payment rejected. Please try again", 2).show();
                                showFailPayment();
                            }
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "There is promblem with connectivity",2).show();
                        }
                    }else{
                        Activity activity = getActivity();

                        if (activity != null) {
                            Toast.makeText(getActivity(), "This card is not registered", Toast.LENGTH_LONG).show();
                        }
                    }

                }else{
                    Activity activity = getActivity();

                    if (activity != null) {
                        Toast.makeText(getActivity(), "Problem contacting server", Toast.LENGTH_LONG).show();
                    }
                }
            }

            else{
				/*OFFLINE*/
                login = new LoginActivity();
                loansdata = new LoansDataSource(getActivity().getApplicationContext());
                String cardNoAgent = loansdata.getCardNoAgent();
                String availaibility = loansdata.getAvailableAgent();


                if (Double.parseDouble(availaibility) < Double.parseDouble(PaymentFragment.instalmentAmount)){
                    Toast.makeText(getActivity().getApplicationContext(), "Your availability less than the instalment", Toast.LENGTH_SHORT).show();
                }else{
                    refNO = generateRefNo();
                    String _regData = "";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dt = new Date();
                    _regData = dateFormat.format(dt);

                    LoanTrx dataLoanTrxOffline = new LoanTrx();
                    dataLoanTrxOffline.setPaymentFlag("1");
                    dataLoanTrxOffline.setInstallment(PaymentFragment.instalmentAmount);
                    dataLoanTrxOffline.setInvoiceNo(PaymentListFragment.invoiceNo);
                    dataLoanTrxOffline.setRefNo(String.valueOf(refNO));
                    dataLoanTrxOffline.setRegData(_regData);
                    dataLoanTrxOffline.setFlagTrx("0");
                    dataLoanTrxOffline.setNextCollectionDate(PaymentFragment.startDate);
                    dataLoanTrxOffline.setRemark(PaymentFragment.remark);

                    if (PaymentFragment.action==2){
                        dataLoanTrxOffline.setFullPay("NF");
                        dataLoanTrxOffline.setNextCollectionDate(PaymentFragment.startDate);
                        dataLoanTrxOffline.setRemark(PaymentFragment.remark);
                    }
                    else{
                        dataLoanTrxOffline.setFullPay("F");
                        dataLoanTrxOffline.setNextCollectionDate("");
                        dataLoanTrxOffline.setRemark("");
                    }
                    sendPayLoan(dataLoanTrxOffline);
                }
            }

        } catch (Exception e) {
            Activity activity = getActivity();

            if (activity != null) {
                Toast.makeText(getActivity(), "Problem processing data", Toast.LENGTH_LONG).show();
            }
            e.printStackTrace();
        }
    }

    public abstract void showSuccessPayment (LoanTrx data);

    public abstract void showFailPayment();

    protected abstract void sendPayLoan(LoanTrx dataLoanTrx);

    public abstract void showStatusPay(String invoiceNo, String installment, String reqData);

    private JSONObject sendToServer(SendCardData sendCardData){

        Log.d(TAG, "sendToServer");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(terasoHost + "card3");
        httpClient.getCredentialsProvider()
                .setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials("teraso", "teraso"));

        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        SharedPreferences pref = sessionManager.getPref();

        String sessionId = pref.getString("JSESSIONID", "");

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("encryptedData", Util.toHexString(sendCardData.getEncryptedData())));
        nameValuePair.add(new BasicNameValuePair("ksn" , Util.toHexString(sendCardData.getKsn())));
        nameValuePair.add(new BasicNameValuePair("key" , sendCardData.getKey()));
        nameValuePair.add(new BasicNameValuePair("JSESSIONID" , sessionId));
        Log.d(TAG,"sessionId = " + sessionId);

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
                Log.d(TAG, "sb.toString() = "+ sb.toString());

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
                Toast.makeText(getActivity().getApplicationContext(), "Payment Not Success", 2).show();
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
        Log.d(TAG, "autoRefNo::: "+ autoRefNo);
        return autoRefNo;
    }

    private void decodeError(int res) {
        Log.d(TAG, Integer.toString(res));
    }

    private void error(String err) {
        Log.d(TAG, err);
    }

    private class StateChangedListener implements CSwiperController.CSwiperStateChangedListener {

        @Override
        public void onCardSwipeDetected() {
            cardSwipeDetected();
        }

        @Override
        public void onDecodeCompleted(HashMap<String, String> decodeData) {
            decodeCompleted(decodeData);
        }

        @Override
        public void onDecodeError(CSwiperController.DecodeResult decodeResult) {
            if (decodeResult == CSwiperController.DecodeResult.DECODE_SWIPE_FAIL) {
                decodeError(R.string.BadSwipe);
            } else if (decodeResult == CSwiperController.DecodeResult.DECODE_CRC_ERROR) {
                decodeError(R.string.CRCError);
            } else if (decodeResult == CSwiperController.DecodeResult.DECODE_COMM_ERROR) {
                decodeError(R.string.CommunicationError);
            } else {
                decodeError(R.string.UnknownDecodeError);
            }
        }

        @Override
        public void onError(int errorCode, String message) {
            String msg = "";
            if (errorCode == CSwiperController.ERROR)
                msg = "ERROR";
            else if (errorCode == CSwiperController.ERROR_FAIL_TO_START)
                msg = "ERROR_FAIL_TO_START";
            else if (errorCode == CSwiperController.ERROR_FAIL_TO_GET_KSN)
                msg = "ERROR_FAIL_TO_GET_KSN";
            else if (errorCode == CSwiperController.ERROR_FAIL_TO_GET_FIRMWARE_VERSION)
                msg = "ERROR_FAIL_TO_GET_FIRMWARE_VERSION";
            else if (errorCode == CSwiperController.ERROR_FAIL_TO_GET_BATTERY_VOLTAGE)
                msg = "ERROR_FAIL_TO_GET_BATTERY_VOLTAGE";
            error(msg + " " + message);
        }

        @Override
        public void onGetKsnCompleted(String ksn) {
            Toast.makeText(getActivity(), R.string.GetKsnCompleted, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onInterrupted() {
            Toast.makeText(getActivity(), R.string.Interrupted, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNoDeviceDetected() {
            Toast.makeText(getActivity(), R.string.CSwiperIsNotDetected, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTimeout() {
            Toast.makeText(getActivity(), R.string.Timeout, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWaitingForCardSwipe() {
            Toast.makeText(getActivity(), R.string.WaitingCardSwipe, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWaitingForDevice() {
//            Toast.makeText(getActivity(), R.string.WaitingForDevice, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDevicePlugged() {
            Toast toast = Toast.makeText(getActivity(), R.string.CSwiperPlugged, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
            toast.show();
        }

        @Override
        public void onDeviceUnplugged() {
            Toast toast = Toast.makeText(getActivity(), R.string.CSwiperUnplugged, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
            toast.show();
        }

        @Override
        public void onEPBDetected() {
        }

        @Override
        public void onPinEntryDetected(CSwiperController.PINKey arg0) {
        }

        @Override
        public void onWaitingForPinEntry() {
        }

    }

    protected void startCallStateService() {
        getActivity().startService(new Intent(INTENT_ACTION_CALL_STATE));
        if (incomingCallServiceReceiver == null) {
            incomingCallServiceReceiver = new IncomingCallServiceReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SwiperCallStateService.INTENT_ACTION_INCOMING_CALL);
            getActivity().registerReceiver(incomingCallServiceReceiver, intentFilter);
        }
    }

    protected void endCallStateService() {
        getActivity().stopService(new Intent(INTENT_ACTION_CALL_STATE));
        if (incomingCallServiceReceiver != null) {
            getActivity().unregisterReceiver(incomingCallServiceReceiver);
            incomingCallServiceReceiver = null;
        }
    }

    // -----------------------------------------------------------------------
    // Inner classes
    // -----------------------------------------------------------------------

    private class IncomingCallServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(SwiperCallStateService.INTENT_ACTION_INCOMING_CALL)) {
                Log.d(TAG, "" + R.string.IncomingCallDetected);
                try {
                    if (cswiperController.getCSwiperState() != CSwiperController.CSwiperControllerState.STATE_IDLE) {
                        cswiperController.stopCSwiper();
                    }
                }
                catch (IllegalStateException ex) {
                    Log.d(TAG, "IllegalStateException: " + ex.getMessage());
                }
            }
        } // end-of onReceive
    }

}
