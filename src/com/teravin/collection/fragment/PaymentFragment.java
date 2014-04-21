package com.teravin.collection.fragment;

import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.text.Editable;

import com.bbpos.cswiper.CSwiperController;
import com.teravin.adapter.LoansDataSource;
import com.teravin.collection.online.R;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;


import android.text.TextWatcher;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Activity;

import com.teravin.adapter.MessagerHandler;
import com.teravin.collection.online.Success;
import com.teravin.model.LoanTrx;
import com.teravin.model.SendCardData;
import com.teravin.util.APIBlink;

import android.app.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.*;

public class PaymentFragment extends SwipeFragment implements APIBlink {

    public static final String TAG = PaymentFragment.class.getName();
    public EditText  installmentAmount;
    private TextView tanggal, desc;
	public TextView invoiceNo;
	public TextView loanName;
    private LinearLayout labelButton;
    private ImageButton btnViewDate;
    private Button btnPay,btnNotPay, btnDetail, btnSubmit, btnSign;
    private MessagerHandler handler;
    private NumberFormat rupiahFormat = new DecimalFormat("'Rp '#,###");
    private SendCardData sendCardData = new SendCardData();
    public static double amountbeforechange, amountchange;
    private int iType =1;  //dongle kecil
    public static int action;
    static final int DATE_DIALOG_ID = 0;
    public static String remark, startDate, instalmentAmount;
    public int mYear, mMonth, mDay;
    
    Calendar c = Calendar.getInstance();
    private int fYear = c.get(Calendar.YEAR);
    private int fMonth = c.get(Calendar.MONTH);
    private int fDay = c.get(Calendar.DAY_OF_MONTH);
   
    static final int DATE_DIALOG_ID_FROM = 0;

    
    private DatePickerDialog.OnDateSetListener datePickerListener 
    = new DatePickerDialog.OnDateSetListener() {

    	public void onDateSet(DatePicker view, int selectedYear,
		    int selectedMonth, int selectedDay) {
			fYear = selectedYear;
			  fMonth = selectedMonth + 1;
			  fDay = selectedDay;
			  startDate = fDay + "/" + fMonth + "/" + fYear;
			  tanggal.setText(startDate);
	    	}
    	};

    public static PaymentFragment newInstance() {
        PaymentFragment f = new PaymentFragment();

        return f;
	}

    protected View _OnCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
    	
    	
		mView = inflater.inflate(R.layout.pay, null, false);
		invoiceNo = (TextView) mView.findViewById(R.id.loanCardNo);
		invoiceNo.setText(PaymentListFragment.invoiceNo);
        labelButton = (LinearLayout) mView.findViewById(R.id.labelButton);
        btnSubmit = (Button) mView.findViewById(R.id.btnSubmit);
        btnViewDate = (ImageButton) mView.findViewById(R.id.btnViewDate);
        tanggal = (EditText) mView.findViewById(R.id.tanggal);
        desc = (EditText) mView.findViewById(R.id.remark);
        loanName = (TextView) mView.findViewById(R.id.loanName);
        loanName.setText(PaymentListFragment.loanName);

        changeButtons(View.GONE);

        installmentAmount = (EditText) mView.findViewById(R.id.installmentAmount);
        installmentAmount.setText((PaymentListFragment.amountLoan));
        amountbeforechange = Double.parseDouble(PaymentListFragment.amountLoan.replace("Rp", "").replace(",", ""));
        installmentAmount.addTextChangedListener(new TextWatcher() {
            String current = "";

            public void onTextChanged(CharSequence c, int start, int before, int count) {
                if(!c.toString().equals(current)){
                    installmentAmount.removeTextChangedListener(this);

                    String s = c.toString();
                    s = s.replace("Rp ", "");
                    s = s.replace(",", "");
                    if(s.length() > 0)
                        s = rupiahFormat.format(Double.parseDouble(s));

                    installmentAmount.setText(s);
                    installmentAmount.setSelection(installmentAmount.length());
                    installmentAmount.addTextChangedListener(this);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                current = s.toString();
            }

            public void afterTextChanged(Editable e) {

            }
        });
//        loadPref();
        AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, AudioManager.FLAG_PLAY_SOUND);

        btnPay = (Button) mView.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	action =1;
                Activity activity = getActivity();
                amountchange = Double.parseDouble(installmentAmount.getText().toString().replace("Rp", "").replace(",", ""));
                instalmentAmount = installmentAmount.getText().toString().replace("Rp", "").replace(",", "");
                if (activity != null) {
                    if(amountchange > amountbeforechange){
                        Toast.makeText(activity, "Amount must be less than or equal to Installment Amount", Toast.LENGTH_LONG).show();
                    }else if(amountchange < amountbeforechange){
                    	changeButtons(View.VISIBLE);
                        labelButton.setVisibility(View.GONE);
                        btnPay.setVisibility(View.GONE);
                        btnDetail.setVisibility(View.GONE);
                        btnNotPay.setVisibility(View.GONE);
                        //instalmentAmount = installmentAmount.getText().toString().replace("Rp", "").replace(",", "");
                        
                    }
                    else {
                        Toast.makeText(activity, "Swipe the card now", Toast.LENGTH_LONG).show();
                    } 
                }

                enableSwipe(); //wait for swipe 15 sec

            }
        });

        btnDetail = (Button) mView.findViewById(R.id.btnDetails);
        btnDetail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              startChildActivity(PaymentDetailFragment.newInstanceDetailFragment());
            }
        });

        btnNotPay = (Button) mView.findViewById(R.id.btnNotpay);
        btnNotPay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	installmentAmount.setText("0");
            	instalmentAmount = "0";
            	changeButtons(View.VISIBLE);
                labelButton.setVisibility(View.GONE);
                btnPay.setVisibility(View.GONE);
                btnDetail.setVisibility(View.GONE);
                btnNotPay.setVisibility(View.GONE);
                
            }
        });
        
        btnSign = (Button) mView.findViewById(R.id.btnSign);
        btnSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            }
        });
            

        btnViewDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//            	custom.showDialog();
            	DatePickerDialog dialog = new DatePickerDialog(getActivity(), datePickerListener, 
                        fYear, fMonth,fDay);
            			dialog.show();
            	Log.d(TAG, "===== CEK ======");
            }
        });
        
        btnSubmit.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Log.d(TAG, "masuk btnSubmit");
            	action =2;
            	Activity activity = getActivity();
            	changeButtons(View.VISIBLE);
                labelButton.setVisibility(View.GONE);
                btnPay.setVisibility(View.GONE);
                btnDetail.setVisibility(View.GONE);
                btnNotPay.setVisibility(View.GONE);
                
                remark=desc.getText().toString();
                
                if (activity != null) {
                    if(remark == null  || startDate == null ||remark == ""  || startDate == ""){
                        Toast.makeText(activity, "You must complete the fields", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(activity, "Swipe the card now", Toast.LENGTH_LONG).show();
                    } 
                }

                enableSwipe(); //wait for swipe 15 sec
            
            }
        });
        
        
        
//        tanggal.setText(tanggalBayar);
        return mView;
	}

    public void showSuccessPayment (LoanTrx data){
        Log.d(TAG, "showSuccessPayment triggered");
        cswiperController.stopCSwiper();
    	Intent successIntent = new Intent (getActivity().getApplicationContext(), Success.class);
    	successIntent.putExtra("refNo", data.getRefNo());
    	successIntent.putExtra("timeStamp",data.getRegData());
    	successIntent.putExtra("cardNo",data.getCardNo());
    	successIntent.putExtra("invoiceNo", data.getInvoiceNo());
    	successIntent.putExtra("instalment",data.getInstallment());
    	successIntent.putExtra("layout","pay");
    	successIntent.putExtra("status","success");
    	startActivity(successIntent);
    	finishChildActivity();
    } 
    
    public void showFailPayment(){
        Log.d(TAG, "showFailPayment triggered");
        cswiperController.stopCSwiper();
    	Intent successIntent = new Intent (getActivity().getApplicationContext(), Success.class);
    	successIntent.putExtra("layout","pay");
    	successIntent.putExtra("status","fail");
    	startActivity(successIntent);
    	finishChildActivity();
    }

    protected void sendPayLoan(LoanTrx dataLoanTrx){
        Log.d(TAG, "sendPayLoan triggered");
        LoansDataSource loansdata = new LoansDataSource(getActivity().getApplicationContext());
        String regData = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = new Date();
        regData = dateFormat.format(dt);
        loansdata.updateLoan(dataLoanTrx);

        if(!conn.isConnectingToInternet()){
            showStatusPay(dataLoanTrx.getInvoiceNo(), dataLoanTrx.getInstallment(), dataLoanTrx.getRegData());
        }
    }

    public void showStatusPay(String invoiceNo, String installment, String reqData){
        Log.d(TAG, "showStatusPay triggered");
        cswiperController.stopCSwiper();
        Intent intent = new Intent(getActivity().getApplicationContext(), Success.class);
        intent.putExtra("cardNoLoan", invoiceNo);
        intent.putExtra("installment", installment);
        intent.putExtra("datePay", reqData);
        intent.putExtra("status", "true");
        intent.putExtra("layout", "pay");
        startActivityForResult(intent, 0);
        finishChildActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        clearData();
    }

    public SendCardData getSendCardData() {
        return sendCardData;
    }

    public void setSendCardData(SendCardData sendCardData) {
        this.sendCardData = sendCardData;
    }

    private void loadPref() {
       // getSharedPreferences(yourfile, MODE_PRIVATE);
        //SharedPreferences sharedPreferences = PreferenceManager.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //PreferenceManager.getShare
        iType = Integer.parseInt(sharedPreferences.getString("key_background", "0"));
        Log.d(TAG, "iType = " + iType);
    }

    private void clearData() {
        installmentAmount.setText("");
        invoiceNo.setText("0");
    }

    public void changeButtons(int view) {

        btnViewDate.setVisibility(view);
        btnSubmit.setVisibility(view);
        tanggal.setVisibility(view);
        desc.setVisibility(view);
    }

    public void enableSwipe() {
        Log.d(TAG, "Enable Swipe");
        try {
            if (cswiperController.getCSwiperState() == CSwiperController.CSwiperControllerState.STATE_IDLE) {
                Log.d(TAG, "Start Swiper");
                cswiperController.startCSwiper();
            }
            else {
                Log.d(TAG, "Stop Swiper");
                cswiperController.stopCSwiper();
            }
        }
        catch (IllegalStateException ex) {
            Log.d(TAG, "IllegalStateException: " + ex.getMessage());
        }
    }

    @Override
    protected void _onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
