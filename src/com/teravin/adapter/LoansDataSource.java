package com.teravin.adapter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.teravin.collection.fragment.PaymentFragment;
import com.teravin.model.Agent;
import com.teravin.model.Loan;
import com.teravin.model.LoanTrx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LoansDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private boolean status = false;
	public int tataltrx;
	public String totalAmount;
	
//	private String[] allColumns = { MySQLiteHelper.LOAN_ID,MySQLiteHelper.LOAN_CARDNO, MySQLiteHelper.LOAN_CARDNAME, MySQLiteHelper.LOAN_AGENTNAME, MySQLiteHelper.LOAN_MOBILENO,MySQLiteHelper.LOAN_ADDRESS1 ,MySQLiteHelper.LOAN_ADDRESS2,
//			MySQLiteHelper.LOAN_CITY,MySQLiteHelper.LOAN_ZIPCODE, MySQLiteHelper.LOAN_INSTALLMENTAMOUNT, MySQLiteHelper.LOAN_DATEPICK,MySQLiteHelper.LOAN_DATEPAY,MySQLiteHelper.LOAN_AMOUNTPAY,MySQLiteHelper.LOAN_PAYSTATUS, MySQLiteHelper.LOAN_COORDINATE };

    public LoansDataSource() {

    }
    public LoansDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
    
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean isEmpty()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  COUNT(id) FROM " + MySQLiteHelper.LOAN, null);
        int tetst = cursor.getCount();
        boolean exists = false;
        if (cursor != null){
            cursor.moveToFirst();
            if (cursor.getInt(0) == 0) {
                 exists = true;
            }
            else {
                 exists = false;
            }

        }
        System.out.println("isEmpty ::: " + exists + " ::: " + tetst + " :::: " + cursor.getInt(0));
        return exists;
    }

    public void createLoan(Loan loandata) {
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.LOAN_ID, loandata.getId()); 
        values.put(MySQLiteHelper.LOAN_CUSTNAME, loandata.getCustName()); 
        values.put(MySQLiteHelper.LOAN_INVOICENO, loandata.getInvoiceNo()); 
        values.put(MySQLiteHelper.LOAN_MOBILEPHONENO, loandata.getMobilePhoneNo());
        values.put(MySQLiteHelper.LOAN_PHONENO, loandata.getPhoneNo()); 
        values.put(MySQLiteHelper.LOAN_TOTALAMOUNT, loandata.getTotalAmount()); 
        values.put(MySQLiteHelper.LOAN_ADDRESS1, loandata.getAddress1());
        values.put(MySQLiteHelper.LOAN_ADDRESS2, loandata.getAddress2());
        ///////////
        values.put(MySQLiteHelper.LOAN_RT, loandata.getRt()); 
        values.put(MySQLiteHelper.LOAN_RW, loandata.getRw()); 
        values.put(MySQLiteHelper.LOAN_KELURAHAN, loandata.getKelurahan());
        values.put(MySQLiteHelper.LOAN_KECAMATAN, loandata.getKecamatan()); 
        values.put(MySQLiteHelper.LOAN_KOTA, loandata.getKota()); 
        values.put(MySQLiteHelper.LOAN_PROVINSI, loandata.getProvinci());
        //////////
        values.put(MySQLiteHelper.LOAN_LANGT, loandata.getLangt()); 
        values.put(MySQLiteHelper.LOAN_LONGT, loandata.getLongt()); 
        values.put(MySQLiteHelper.LOAN_REMARK, "");
        values.put(MySQLiteHelper.LOAN_TRXDATE, "");
        values.put(MySQLiteHelper.LOAN_NEXTCOLLECTIONDATE,""); 
        values.put(MySQLiteHelper.LOAN_CARDNOAGENT, loandata.getCardNoAgent());
        values.put(MySQLiteHelper.LOAN_PAYMENTFLAG, "0");
        values.put(MySQLiteHelper.LOAN_FLAGTRX, "1");
        values.put(MySQLiteHelper.LOAN_REFNO, "");

        db.insert(MySQLiteHelper.LOAN, null, values);
        db.close();
    }
    
    public void createAgent(Agent agent){
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(MySQLiteHelper.AGENT_CARDNOAGENT, agent.getCardNoAgent()); 
        values.put(MySQLiteHelper.AGENT_MEMBERTOCOLLECT, agent.getMembertoCollect()); 
        values.put(MySQLiteHelper.AGENT_AVAILABLE, agent.getAvailable()); 
        values.put(MySQLiteHelper.AGENT_OUTSTANDING, agent.getOutstanding());
        values.put(MySQLiteHelper.AGENT_LIMIT, agent.getLimit()); 
		
		db.insert(MySQLiteHelper.AGENT, null, values);
		db.close();
    }
    
    public ArrayList<HashMap<String, String>> getLoanDetails(){
    		ArrayList<HashMap<String, String>> loanList = new ArrayList<HashMap<String, String>>();
    		
            String selectQuery = "SELECT  * FROM " + MySQLiteHelper.LOAN+" where paymentFlag = 0";
            
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
           
            if(cursor.getCount() > 0){
            	if (cursor.moveToFirst()) {
            		do {
            			
            			HashMap<String,String> loan = new HashMap<String,String>();
                        loan.put("invoiceNo", cursor.getString(2));
		            	loan.put("customerName", cursor.getString(1));
		            	loan.put("phoneNo", cursor.getString(5));
		            	loan.put("mobilePhoneNo", cursor.getString(3));
		            	loan.put("address", cursor.getString(13)+" "+cursor.getString(14) +" RT/RW: " +cursor.getString(17) +" / "+cursor.getString(18)+ ", Kelurahan: "+cursor.getString(19)+
		                		" , Kecamatan: " +cursor.getString(20)+ ", Kota: "+cursor.getString(15)+" , Provinsi: "+cursor.getString(16));
		            	loan.put("totalAmount", cursor.getString(4));
		            	
		            	loanList.add(loan);
		            	
            		}while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
           
            }
       return loanList;
     }

    public void updateLoanOffline(String invoiceNo ){
		 SQLiteDatabase db = dbHelper.getWritableDatabase();
	        ContentValues con = new ContentValues();
		     con.put("paymentFlag", "3");
		     db.update(MySQLiteHelper.LOAN, con, "invoiceNo ='" +invoiceNo + "'",null);
	}
	
    public ArrayList<HashMap<String, String>> getLoanActivityDetails(){
        ArrayList<HashMap<String, String>> loanList = new ArrayList<HashMap<String, String>>(); 
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.LOAN +" where paymentFlag = 1";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() > 0){
           
            if (cursor.moveToFirst()) {
                do {
                	
                	HashMap<String,String> loan = new HashMap<String,String>();
                    loan.put("invoiceNo", cursor.getString(2));
	            	loan.put("customerName", cursor.getString(1));
	            	loan.put("phoneNo", cursor.getString(5));
	            	loan.put("mobilePhoneNo", cursor.getString(3));
	            	loan.put("address", cursor.getString(13)+" "+cursor.getString(14) +" RT/RW: " +cursor.getString(17) +" / "+cursor.getString(18)+ ", Kelurahan: "+cursor.getString(19)+
	                		" , Kecamatan: " +cursor.getString(20)+ ", Kota: "+cursor.getString(15)+" , Provinsi: "+cursor.getString(16));
	            	loan.put("totalAmount", cursor.getString(4));
	            	loan.put("refNo", cursor.getString(11));
	            	loan.put("trxDate",cursor.getString(14));
	            	loan.put("remark", cursor.getString(7));
	            	loan.put("nextCollectionDate",cursor.getString(6));
	            	loan.put("langt", cursor.getString(12));
	            	loan.put("longt",cursor.getString(13));
	            	loan.put("fullpay", cursor.getString(10));
                    loanList.add(loan);
                }while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

        }
        return loanList;
    }
   
    public ArrayList<HashMap<String, String>> getListTrxOffline(){
    	ArrayList<HashMap<String, String>> trxOffline = new ArrayList<HashMap<String,String>>();
    	String selectQuery = "SELECT  * FROM " + MySQLiteHelper.LOAN +" where paymentFlag = 1 and flagTrx = 0";
    	 SQLiteDatabase db = dbHelper.getReadableDatabase();
         Cursor cursor = db.rawQuery(selectQuery, null);
         System.out.println("cursor getListTrxOffline :: "+cursor);
         if(cursor.getCount() > 0){
            if (cursor.moveToFirst()) {
                 do {
                	 
                 	HashMap<String,String> transaction = new HashMap<String,String>();
                 	transaction.put("invoiceNo", cursor.getString(2));
                 	transaction.put("totalAmount", cursor.getString(4));
                 	transaction.put("customerName", cursor.getString(1));
                 	transaction.put("refNo", cursor.getString(11));
                 	transaction.put("trxDate",cursor.getString(14));
                 	transaction.put("remark", cursor.getString(7));
                 	transaction.put("nextCollectionDate",cursor.getString(6));
                 	transaction.put("langt", cursor.getString(12));
                 	transaction.put("longt",cursor.getString(13));
                 	trxOffline.add(transaction);
                 }while (cursor.moveToNext());
             }
             cursor.close();
             db.close();
         }     	
		return trxOffline;
    	
    }
    public void updateLoan(LoanTrx dataLoanTrx){
    	System.out.println("dataLoanTrx updateLoan ::: "+dataLoanTrx.getFullPay() + " :: dataLoanTrx "+ dataLoanTrx.getFlagTrx() );
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues con = new ContentValues();
	     con.put("refNo", dataLoanTrx.getRefNo());
	     con.put("totalAmount", dataLoanTrx.getInstallment());
	     con.put("trxDate", dataLoanTrx.getRegData());
	     con.put("paymentFlag", dataLoanTrx.getPaymentFlag());
	     con.put("flagTrx", dataLoanTrx.getFlagTrx());
	     con.put("fullpay",  dataLoanTrx.getFullPay());
	     if(dataLoanTrx.getFullPay().equalsIgnoreCase("NF")){
	    	 con.put("nextCollectionDate", dataLoanTrx.getNextCollectionDate());
		     con.put("remark", dataLoanTrx.getRemark());
	     }
	    
	     db.update(MySQLiteHelper.LOAN, con, "invoiceNo ='" + dataLoanTrx.invoiceNo + "'",null);
	     updateOutstanding(dataLoanTrx.getInstallment());
         db.close();

    }

    public void deleteLoan(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MySQLiteHelper.LOAN, "paystatus=3", null);
        db.close();
        //sqLiteDatabase.delete(MYDATABASE_TABLE, KEY_ID+"="+id, null);
    }
    
    public void deleteAllLoan(){
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	db.delete(MySQLiteHelper.LOAN, null, null);
        db.close();
    }
    

    public String getTotalAmountTrx(){
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	float columntotal = 0;
    	String selectQuery3 = "SELECT SUM(totalAmount) FROM " + MySQLiteHelper.LOAN +" WHERE paymentFlag = 1";
    	Cursor cursor1 = db.rawQuery(selectQuery3, null);
    	if(cursor1.moveToFirst()){
    		columntotal = cursor1.getFloat(0);
    	}
    	cursor1.close();
    	String  sumtotal = Float.toString((float)columntotal);
        cursor1.close();
        db.close();
    	return sumtotal;
    }

    public String getTotalTrx(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int columntotal = 0;
        String selectQuery3 = "SELECT COUNT(id) FROM " + MySQLiteHelper.LOAN +" WHERE paymentFlag = 1";
        Cursor cursor1 = db.rawQuery(selectQuery3, null);
        if(cursor1.moveToFirst()){
            columntotal = cursor1.getInt(0);
        }
        cursor1.close();
        db.close();
        String  counttotal = Integer.toString((int)columntotal);
        return counttotal;
    }
    
     
    
    public ArrayList<HashMap<String, String>> getDataAgent(){
    	ArrayList<HashMap<String, String>> dataAgent = new ArrayList<HashMap<String, String>>(); 
		
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.AGENT;
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
       
        if(cursor.getCount() > 0){
        	if (cursor.moveToFirst()) {
        		do {
        			HashMap<String,String> loan = new HashMap<String,String>();
        			loan.put("cardnoagent", cursor.getString(1));
        			loan.put("membertocollect", cursor.getString(2));
	            	loan.put("limitAgent", cursor.getString(3));
	            	loan.put("available", cursor.getString(4));
	            	loan.put("outstanding", cursor.getString(5));
	            	
	            	dataAgent.add(loan);
        		}while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
       
        }
        return dataAgent;
    	
    }
    
    
    public String getAvailable(String limit, String outstanding){
    	String available ="";
    	BigDecimal limitAmount = new BigDecimal(limit);
    	System.out.println("limit ::: " + limitAmount);
    	
    	BigDecimal outstandingAmount = new BigDecimal(outstanding);
    	System.out.println("limit ::: " + limitAmount.subtract(outstandingAmount));
    	
    	BigDecimal result = limitAmount.subtract(outstandingAmount);
    	available = result.toString();
    	return available;
    }
    //String outstanding ,String totalAmount
    public String updateOutstanding(String installment){
    	String outstandingamount="";
    	
    	ArrayList<HashMap<String, String>> agentData = new ArrayList<HashMap<String,String>>();
    	agentData = getDataAgent();
        String limit = agentData.get(0).get("limitAgent");
        String outstanding = agentData.get(0).get("outstanding");
        String availableAmt = agentData.get(0).get("available");
    	System.out.println("Limit Agent:: "+limit);
    	System.out.println("Outstanding Agent:: "+outstanding);
    	System.out.println("Installment ::"+installment);
    	
    	BigDecimal outstandingAmount = new BigDecimal(outstanding);
    	BigDecimal instalment = new BigDecimal((installment.toString()).trim());
    	BigDecimal available = new BigDecimal(availableAmt);
    	BigDecimal limitAmt = new BigDecimal(limit);
    	
    	BigDecimal resultOutstanding = outstandingAmount.add(instalment);
    	//BigDecimal resultLimit = limitAmt.subtract(instalment);
    	BigDecimal resultAvailable = limitAmt.subtract(resultOutstanding);
    	
    	SQLiteDatabase db = dbHelper.getWritableDatabase();

    	ContentValues con = new ContentValues();
    	//con.put("limitAgent", resultLimit.toString());
    	con.put("available", resultAvailable.toString());
    	con.put("outstanding", resultOutstanding.toString());
    	db.update(MySQLiteHelper.AGENT, con,null,null);
    	db.close();
    //	System.out.println("Limit Agent:: "+resultLimit.toString());
    	System.out.println("Outstanding Agent:: "+resultOutstanding.toString());
    	System.out.println("available ::"+resultAvailable.toString());
    	return outstandingamount;
    }
    
    public String getAgentName(){
    	String agent="";
    	String selectQuery = "SELECT  * FROM " + MySQLiteHelper.AGENT;
    	
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	System.out.println("selectQuery2  : " +selectQuery);
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	System.out.println("selectQuery3  : " +selectQuery);
    	if(cursor.getCount() > 0){
    		System.out.println("selectQuery4  : " +selectQuery);
        	if (cursor.moveToFirst()) {
        		System.out.println("selectQuery5  : " +selectQuery);
        		agent = cursor.getString(2);
        	}
        }
        System.out.println("agent name  : " + agent);
    	return agent;
    }
    
    public String getCardNoAgent(){
    	String cardnoAgent="";
    	String selectQuery = "SELECT  * FROM " + MySQLiteHelper.AGENT;
    	
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	
    	if(cursor.getCount() > 0){
    		
        	if (cursor.moveToFirst()) {
        	
        		cardnoAgent = cursor.getString(1);
        	}
        }
       return cardnoAgent;
    }
    
    public String getAvailableAgent(){
    	String cardnoAgent="";
    	String selectQuery = "SELECT  * FROM " + MySQLiteHelper.AGENT;
    	
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	
    	if(cursor.getCount() > 0){
    		
        	if (cursor.moveToFirst()) {
        	
        		cardnoAgent = cursor.getString(4);
        	}
        }
       return cardnoAgent;
    }
    
  
}
