package com.teravin.collection.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.teravin.collection.fragment.ActivityListFragment.MyPerformanceArrayAdapter;
import com.teravin.collection.online.R;
import com.teravin.util.APIBlink;
import com.teravin.util.ConvertString;

/**
* Created by dumatambunan on 3/3/14.
*/
public class DetailActivityFragment extends BaseFragment implements APIBlink {
	
	ArrayList<HashMap<String, String>> dataActivity = new ArrayList<HashMap<String,String>>();
	TextView tvTrxRefNo, tvTrxDate, tvInvoiceNo, tvAmount, tvName, tvCardNo, tvDate, tvremark, labelDate, labelRemark;
	String trxrefno, trxdate, invoiceno, amount, name, date, remark;
	private HashMap<String, String> oke = new HashMap<String, String>();
	private Button btnPrint;
	public static OutputStream mmOutputStream;
	private NumberFormat rupiahFormat = new DecimalFormat("'Rp '#,###");
	 InputStream mmInputStream;
	 Thread workerThread;
	 ConvertString convertStr;
	 byte[] readBuffer;
	 int readBufferPosition;
	 int counter;
	 volatile boolean stopWorker;
	 BluetoothAdapter mBluetoothAdapter;
	 BluetoothSocket mmSocket;
	 BluetoothDevice mmDevice;
	 private static final String tag = "cashcollection";
	 
	public static DetailActivityFragment newInstance() {
        DetailActivityFragment f = new DetailActivityFragment();
        return f;
	}


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activitydetail, container, false);
        
        ActivityListFragment coy = new ActivityListFragment();
        ActivityListFragment.listView.setOnItemClickListener(null);
        
        oke = coy.kirim;
        
        trxrefno = oke.get("customerName");
        trxdate = oke.get("amount");
        invoiceno = oke.get("refNo");
        amount = oke.get("trxDate");
        name = oke.get("invoiceNo");
        date = oke.get("remark");
        remark = oke.get("nextCollectionDate");
        
        System.out.println("========== cek hashmap di tampil " + oke);

        
        tvTrxRefNo = (TextView) mView.findViewById(R.id.tvtrxrefno);
        tvTrxDate = (TextView) mView.findViewById(R.id.tvtrxdate);
        tvInvoiceNo = (TextView) mView.findViewById(R.id.tvinvoiceno);
        tvAmount = (TextView) mView.findViewById(R.id.tvamount);
        tvName = (TextView) mView.findViewById(R.id.tvname);
//        tvCardNo = (TextView) mView.findViewById(R.id.tvcardno);
        tvDate = (TextView) mView.findViewById(R.id.tvdate);
        labelDate = (TextView) mView.findViewById(R.id.tvlabeldate);
        tvremark = (TextView) mView.findViewById(R.id.tvremark);
        labelRemark = (TextView) mView.findViewById(R.id.tvlabelremark);
        btnPrint = (Button) mView.findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				 findBT();
                 openBT();
                 sendData();
                 closeBT();
			}
		});
        
        tvTrxRefNo.setText(trxrefno);
    	tvTrxDate.setText(trxdate);
    	tvInvoiceNo.setText(invoiceno);
    	tvAmount.setText(amount);
    	tvName.setText(name);
    	if (!(date.equals(""))){
    	tvDate.setText(date);
    	} else {
    		tvDate.setVisibility(View.GONE);
    		labelDate.setVisibility(View.GONE);
    	}
    	if (!(remark.equals(""))){
    	tvremark.setText(remark);
    	} else {
    		tvremark.setVisibility(View.GONE);
    		labelRemark.setVisibility(View.GONE);
    	}

    	return mView;
    }

    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(getActivity().getApplicationContext(),"No bluetooth adapter available",2).show();
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
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Device Found", 2).show();
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
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Opened", 2).show();
            
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
                                                //myLabel.setText(data);
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
		sb.append("Activity Detail " + "\n");
		
		/**
		 *  tvTrxRefNo.setText(trxrefno);
    	tvTrxDate.setText(trxdate);
    	tvInvoiceNo.setText(invoiceno);
    	tvAmount.setText(amount);
    	tvName.setText(name);
    	if (!(date.equals(""))){
    	tvDate.setText(date);*/
		
		sb.append("Tgl Transaksi  : " +amount +"\n");
		sb.append("Nama           : " +trxrefno +"\n");
		sb.append("No Tagihan     : " +name +"\n");
		sb.append("Jumlah Tagihan : " +trxdate +"\n");
		sb.append("Ref No         : " +invoiceno +"\n\n\n\n");
		sb.append("Sign X___________________"+"\n\n\n");
		
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
            Toast.makeText(getActivity().getApplicationContext(),"Bluetooth Closed",2).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	Log.d(tag, "This is onStop");
    	ActivityListFragment coy = new ActivityListFragment();
//        ActivityListFragment.listView.setOnItemClickListener(coy.onItemClick);
        
    }

    
}
 