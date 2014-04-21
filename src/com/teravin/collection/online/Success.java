package com.teravin.collection.online;

import android.app.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.content.Context;
import android.content.Intent;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.teravin.collection.fragment.PaymentListFragment;
import com.teravin.collection.online.R;
import com.teravin.adapter.WoosimPrinterActivity;
import com.teravin.service.PaymentService;
import com.teravin.util.APIBlink;
import com.teravin.util.ConvertString;


public class Success extends Activity implements APIBlink{

	private String layout, status;
    public int page = 0;
    ConvertString convertStr;
    Button btnPrint,btnShowLocation, sendSms;
    private EditText phoneNumberText;
    private TextView myLabel;
    private LinearLayout footer;
    private ImageView imageStatus;
    private NumberFormat rupiahFormat = new DecimalFormat("'Rp '#,###");
    private String installment, refNo, timeStamp, cardNo, cardName, invoiceNo;
    private double amount;
    public int refNO;
    public TextView label; 
    private static int autoRefNo = 100001;
	 BluetoothAdapter mBluetoothAdapter;
	 BluetoothSocket mmSocket;
	 BluetoothDevice mmDevice;
	// private WoosimPrinter woosim;
	public static OutputStream mmOutputStream;
	 InputStream mmInputStream;
	 Thread workerThread;
	 byte[] readBuffer;
	 int readBufferPosition;
	 int counter;
	 volatile boolean stopWorker;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.success);
        
        layout=getIntent().getStringExtra("layout");
        setContentView(R.layout.successpayment);
    	imageStatus  = (ImageView)findViewById(R.id.image);
    	label = (TextView) findViewById(R.id.label);
    	
        if(layout.equalsIgnoreCase("pay")){
             paymentSuccess();
        }
        else{
        	 settlementSuccess();
        }
    }

	    
   

    private void paymentSuccess(){
        page=2;
       // setContentView(R.layout.successpayment);
        status = getIntent().getStringExtra("status");
        if(status.equalsIgnoreCase("fail")){
        	label.setText("Payment Fail");
        	imageStatus.setImageResource(R.drawable.redcross);
        	
        }else{
        	myLabel = (TextView) findViewById(R.id.notification);
	        label.setText("Payment Success");
	        imageStatus.setImageResource(R.drawable.checkgreen);
	        
	        refNo = getIntent().getStringExtra("refNo");
	        timeStamp = getIntent().getStringExtra("timeStamp");
	        installment = getIntent().getStringExtra("instalment");
	        cardNo = getIntent().getStringExtra("cardNo");
	        cardName = getIntent().getStringExtra("cardName");
	        invoiceNo = getIntent().getStringExtra("invoiceNo");
	
	        btnPrint = (Button) findViewById(R.id.btnPrint);
	        btnPrint.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	               
	//                Intent intent = new Intent(getApplicationContext(), WoosimPrinterActivity.class);
	//                intent.putExtra("refNo", refNo);
	//                startActivity(intent);
	            	 findBT();
	                 openBT();
	                 sendData();
	                 closeBT();
	            }
	        });
	
	        sendSms = (Button) findViewById(R.id.btnSendSms);
	        sendSms.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	                LayoutInflater li =  LayoutInflater.from(Success.this);
	
	                View promptView = li.inflate(R.layout.sms_prompt, null);
	                AlertDialog.Builder alertDialogueBuilder = new AlertDialog.Builder(Success.this);
	                alertDialogueBuilder.setView(promptView);
	
	                phoneNumberText = (EditText) promptView.findViewById(R.id.phoneNumber);
	
	                alertDialogueBuilder
	                        .setCancelable(true)
	                        .setPositiveButton("OK",
	                                new DialogInterface.OnClickListener() {
	                                    public void onClick(DialogInterface dialog,int id) {
	                                        if(phoneNumberText.getText().length() == 0){
	                                            Toast.makeText(Success.this, "Please enter your phone number", Toast.LENGTH_LONG).show();
	                                        }else{
	                                            JSONObject response = sendSMSNotification(phoneNumberText.getText().toString());
	                                            if(response != null){
	                                                Toast.makeText(Success.this, "SMS sent", Toast.LENGTH_LONG).show();
	                                            }else{
	                                                Toast.makeText(Success.this, "Fail sending SMS", Toast.LENGTH_LONG).show();
	                                            }
	                                        }
	                                    }
	                                })
	                        .setNegativeButton("Cancel",
	                                new DialogInterface.OnClickListener() {
	                                    public void onClick(DialogInterface dialog,int id) {
	                                        dialog.cancel();
	                                    }
	                                });
	
	                	AlertDialog alertDialog = alertDialogueBuilder.create();
	                	alertDialog.show();
	
	            	}
	        	});
        }
    }
    
    public void settlementSuccess(){
    	status = getIntent().getStringExtra("status");
    	footer = (LinearLayout) findViewById(R.id.footer);
    	footer.setVisibility(View.GONE);
    	
    	if(status.equalsIgnoreCase("success")){
    		label.setText("Settlement Success");
    		imageStatus.setImageResource(R.drawable.checkgreen);
    		
        	
    	}else{
    		label.setText("Settlement Fail");
    		imageStatus.setImageResource(R.drawable.redcross);
    	}
    	
    	
    }

    private JSONObject sendSMSNotification(String mobileNumber){
        StringBuilder sb = new StringBuilder();
        sb.append("Pembayaran tagihan ");
        sb.append(" "+ rupiahFormat.format(Double.parseDouble(installment)));
        sb.append(" dengan  Ref No: "+ refNo);
        sb.append(" telah diterima agent " + LoginActivity.usernameDB);

        System.out.println("sendSMSNotification ::: "+sb.toString());
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("mobileNo", mobileNumber));
        nameValuePair.add(new BasicNameValuePair("content" , sb.toString()));
        PaymentService requestHttp = new PaymentService(getApplicationContext());
        JSONObject response = requestHttp.sendHTTPRequest(remoteNetbankHost + "sendSms", nameValuePair);
        return response;
        
     }

   
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(),"No bluetooth adapter available",2).show();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // OJL411MY29I911JH is the name of the bluetooth printer device shown after scan
                    if (device.getName().equals("P25_056255_01")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            Toast.makeText(getApplicationContext(), "Bluetooth Device Found", 2).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Tries to open a connection to the bluetooth printer device
     */
    void openBT() {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();
            Toast.makeText(getApplicationContext(), "Bluetooth Opened", 2).show();
            
        } catch (NullPointerException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * After opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendData(){
        String message = setMessage();
        System.out.println("message :::: " + message);
        byte[] byteMessage = convertStr.convertPrintData(message, 0,message.length(), ConvertString.LANGUAGE_CHINESE, ConvertString.FONT_24PX,ConvertString.Align_CENTER,(byte)0x1A);

        try {
            mmOutputStream.write(byteMessage);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String setMessage (){
		String message = "";
       
		StringBuilder sb = new StringBuilder();
		sb.append("Terima kasih Anda telah " + "\n");
		sb.append("melakukan transaksi " + "\n");
		sb.append("pembayaran pinjaman " + "\n");
		sb.append("dengan detail transaksi sbb:" + "\n");
		sb.append("Tanggal        : " +timeStamp +"\n");
		sb.append("Nama           : " +PaymentListFragment.loanName +"\n");
		sb.append("No Kartu Agent : " +cardNo +"\n");
		sb.append("No Tagihan     : " +invoiceNo +"\n");
		sb.append("Jumlah         : " +rupiahFormat.format(Double.parseDouble(installment)) +"\n");
		sb.append("Ref No         : " +refNo +"\n");
		sb.append("Status         : Berhasil"+"\n\n\n\n");
		sb.append("Sign X___________________"+"\n\n");
		
		message = sb.toString();
		
    	return message;
	}
    /*
     * Close the connection to bluetooth printer.
     */
    void closeBT() {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
           // myLabel.setText("Bluetooth Closed");
            Toast.makeText(getApplicationContext(),"Bluetooth Closed",2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed(){
        if(page == 1){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
//        else {																						///// AYAMBAKAR
//            Intent intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
//            startActivity(intent);
//
//        }
        finish();
    }

    public int generateRefNo(){
        autoRefNo +=1;
        return autoRefNo;
    }
}
