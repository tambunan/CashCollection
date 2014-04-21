package com.teravin.adapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.teravin.collection.online.R;
import com.teravin.collection.fragment.PaymentListFragment;
import com.teravin.collection.online.TabsFragmentActivity;
import com.woosim.bt.WoosimPrinter;

public class WoosimPrinterActivity extends Activity implements OnClickListener {

    private final static byte DATA 				= 0x44;
    private final static byte ETX 				= 0x03;
    private final static byte EOT 				= 0x04;
    private final static byte NACK 				= 0x15;
    private final static byte MSR_FAIL 			= 0x4d;
    private final static byte ACK 				= 0x06;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private String address;

    private WoosimPrinter woosim;

    private Button btnFinish2;
    BluetoothDevice mmDevice;
    private CheckBox cheProtocol;
    String noDevices;
    private TextView subtitle;
    double amount ;
    private String cardNo;
    private final static String EUC_KR = "EUC-KR";
    private final int WAIT_TIME = 10000;
    private ProgressBar progressbar;
    static int counter = 0;
    public int refNO;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        woosim = new WoosimPrinter();
        super.onCreate(savedInstanceState);

        refNO = getIntent().getIntExtra("refNo",0);

        findBluetoothDevice();
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.woosim);
        setResult(Activity.RESULT_CANCELED);
        createButton();
        openConnection();


        btnFinish2 = (Button) findViewById(R.id.btnFinish);
        btnFinish2.setOnClickListener(this);
        btnFinish2.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable(){
            public void run() {
                findViewById(R.id.mainSpinner1).setVisibility(View.VISIBLE);
                print();
                subtitle.setText("");
                btnFinish2.setVisibility(View.VISIBLE);
            }
        }, WAIT_TIME);


    }

    public void findBluetoothDevice(){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) {

            Toast t = Toast.makeText(this, "No bluetooth adapter available!",
                    Toast.LENGTH_SHORT);
            t.show();
        }

        if (!mBtAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
            Toast t = Toast.makeText(this, "bluetooth adapter available!",
                    Toast.LENGTH_SHORT);
            t.show();
        }

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("WOOSIM")) {
                    mmDevice = device;
                    break;
                }
            }
        } else {
            noDevices = getResources().getText(R.string.none_paired).toString();

        }
    }
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnFinish:
//				woosim.BTDisConnection();
//				finish();
                if (counter == 1){
                    print();
                    btnFinish2.setText("FINISH");
                    counter = 0;
                }
                else {
                    woosim.BTDisConnection();
                    finish();
                    btnFinish2.setText("FINISH");
                    Intent intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);

                    startActivity(intent);
//					counter = 0;
                }
                break;

            default:
                break;
        }
    }

    public void openConnection(){

        boolean inSecure = false;
        String version = android.os.Build.VERSION.RELEASE;
        String sub1 = version.substring(0, 1);
        String sub2 = version.substring(2, 3);
        String sub3 = version.substring(4, 5);
        int osVersion = Integer.parseInt(sub1+sub2+sub3);


        if(osVersion >= 236){
            inSecure = true;
            Log.e("woosim","osVersion1: "+osVersion);
        }else{
            inSecure = false;
            Log.e("woosim","osVersion2: "+osVersion);
        }

        address = mmDevice.getAddress();


        int reVal = woosim.BTConnection(inSecure, address, cheProtocol.isChecked());

        if (reVal == 1) {
            Toast t = Toast.makeText(this, "Success Connection!",
                    Toast.LENGTH_SHORT);
            t.show();
        } else if (reVal == -2) {
            Toast t = Toast.makeText(this, "NOT CONNECTED",
                    Toast.LENGTH_SHORT);
            t.show();
        } else if (reVal == -5) {
            Toast t = Toast.makeText(this, "DEVICE IS NOT BONDED",
                    Toast.LENGTH_SHORT);
            t.show();
        } else if (reVal == -6) {
            Toast t = Toast.makeText(this, "ALREADY CONNECTED",
                    Toast.LENGTH_SHORT);
            t.show();
        } else if (reVal == -8) {
            Toast t = Toast
                    .makeText(
                            this,
                            "Please enable your Bluetooth and re-run this program!",
                            Toast.LENGTH_LONG);
            t.show();
        } else {
            Toast t = Toast.makeText(this, "ELSE", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    private void print() {
        progressbar.setVisibility(View.VISIBLE);
        Toast t = Toast.makeText(this, "Start Printing.....",
                Toast.LENGTH_SHORT);
        t.show();
        progressbar.setVisibility(View.VISIBLE);
        byte[] init = {0x1b,'@'};
        woosim.controlCommand(init, init.length);

        String regData = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = new Date();
        regData = dateFormat.format(dt);



        woosim.saveSpool(EUC_KR, "================================\r\n", 0, false);
        woosim.saveSpool(EUC_KR, "Terima kasih Anda telah melakukan  "+"\r\n", 0, false);
        woosim.saveSpool(EUC_KR, "transaksi pembayaran pinjaman " +"\r\n", 0, false);
        woosim.saveSpool(EUC_KR, "dengan detail transaksi sbb:"+"\r\n", 0, false);
        woosim.saveSpool(EUC_KR, "Tanggal     : "+regData+"\r\n", 0, false);
        woosim.saveSpool(EUC_KR, "Amount      : "+PaymentListFragment.amountLoan+"\r\n", 0, false);
        woosim.saveSpool(EUC_KR, "Ref No      : "+refNO+"\r\n", 0, false);
        woosim.saveSpool(EUC_KR, "Card No     : "+PaymentListFragment.invoiceNo+"\r\n", 0, false);
        woosim.saveSpool(EUC_KR, "Status      : Berhasil    "+"\r\n\r\n\r\n", 0, false);
        woosim.saveSpool(EUC_KR, "Sign X___________________        "+"\r\n", 0, false);
        if (counter == 1){
            woosim.saveSpool(EUC_KR, "______CUSTOMER COPY________"+"\r\n", 0, false);
        }
        byte[] ff ={0x0c};
        woosim.controlCommand(ff, 1);
        byte[] lf = {0x0a};
        woosim.controlCommand(lf, lf.length);
        woosim.saveSpool(EUC_KR,"Terima kasih \r\n\r\n\n", 0, true);
        woosim.printSpool(true);
        //cardData = null;
        t = Toast.makeText(this, "Printing is done.....",
                Toast.LENGTH_SHORT);
        t.show();
        progressbar.setVisibility(View.GONE);
        btnFinish2.setText("REPRINT");
        btnFinish2.setVisibility(View.VISIBLE);
        counter = 1;
    }

    protected void onDestroy() {
        System.out.println("masuk onDestroy**********");
        super.onDestroy();
        woosim.BTDisConnection();

    }

    public void closeConnection(){
        woosim.BTDisConnection();
        finish();
    }

    private void createButton() {

        subtitle = (TextView) findViewById(R.id.subtitle);
        subtitle.setText("Please wait while printing...");
        progressbar = (ProgressBar) findViewById(R.id.mainSpinner1);
        progressbar.setVisibility(View.VISIBLE);
        cheProtocol = (CheckBox) findViewById(R.id.che_protocol);
        cheProtocol.setVisibility(View.INVISIBLE);
    }

}