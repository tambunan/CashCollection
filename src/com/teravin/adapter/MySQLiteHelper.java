package com.teravin.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String LOAN = "Loan";
	public static final String AGENT = "Agent";
	public static final String COORDINATE = "Coordinate";
	public static final String LOAN_ID = "id";
    public static final String LOAN_CUSTNAME = "customerName";
    public static final String LOAN_INVOICENO = "invoiceno";
    public static final String LOAN_MOBILEPHONENO = "mobilephoneno";
    public static final String LOAN_PHONENO = "phoneno";
    public static final String LOAN_TOTALAMOUNT = "totalamount";
    public static final String LOAN_ADDRESS1 = "address1";
    public static final String LOAN_ADDRESS2 = "address2";
    /////
    public static final String LOAN_RT = "rt";
    public static final String LOAN_RW = "rw";
    public static final String LOAN_KELURAHAN = "kelurahan";
    public static final String LOAN_KECAMATAN = "kecamatan";
    public static final String LOAN_KOTA = "kota";
    public static final String LOAN_PROVINSI = "provinsi";
    /////
    public static final String LOAN_LANGT = "langt";
    public static final String LOAN_LONGT = "longt";
    public static final String LOAN_REMARK = "remark";
    public static final String LOAN_TRXDATE = "trxdate";
    public static final String LOAN_NEXTCOLLECTIONDATE = "nextCollectionDate";
    public static final String LOAN_CARDNOAGENT = "cardnoagent";
    public static final String LOAN_PAYMENTFLAG = "paymentflag";
    public static final String LOAN_FLAGTRX = "flagTrx";
    public static final String LOAN_FULLPAY = "fullpay";
    public static final String LOAN_REFNO = "refno";
    public static final String AGENT_MEMBERTOCOLLECT = "membertocollect";
    public static final String AGENT_AVAILABLE = "available";
    public static final String AGENT_LIMIT = "limitAgent";
    public static final String AGENT_OUTSTANDING = "outstanding";
    public static final String AGENT_CARDNOAGENT = "cardnoagent";
    public static final String DATABASE_NAME = "loandb.db";
    private static final int DATABASE_VERSION = 1;
    public String DB_FULL_PATH = "/data/data/com.teravin.collection.online/databases/";
    private SQLiteDatabase database;

    
    // creation SQLite statement

    public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
    
    
    //address1, address2, invoiceNo, city, totalAmount, province, corporateID,
    //customerName,kelurahan,phoneNo,kecamatan, 
    //rt,rw, nextCollectionDate, remark, ,paymentFlag,refNo, langt, longt
    private static final String DATABASE_CREATE_TRX = "create table Loan (" +
    		"id integer primary key,"+
    		"customerName text," + 
    		"invoiceNo text," + 
    		"mobilePhoneNo text," + 
    		"totalAmount text," +
    		"phoneNo text," + 
    		"nextCollectionDate text," + 
    		"remark text," + 
    		"paymentFlag text," +
    		"flagTrx text," +
    		"fullpay text," +
    		"refNo text," +
    		"langt text," +
    		"longt text," +
    		"trxDate text," +
            "address1 text," +
            "address2 text," +
            "kota text," +
    		"provinsi text," +
    		"rt text," +
    		"rw text," +
            "kelurahan text," +
            "kecamatan text," +
            "cardNoAgent text"+
    		");";
    
    private static final String DATABASE_CREATE_AGENT  = "create table Agent ( "+
    	    "id integer primary key autoincrement, "+
    	    "cardnoagent text not null,"  +
    	    "membertocollect text not null,"  +
    	    "limitAgent text not null,"  +
    	    "available text not null,"  +
    	    "outstanding text not null "  +
    	    " ); ";
    		
    
    private static final String DATABASE_COORDINATE_CREATE = "create table Coordinate (" +
    		"id integer primary key,"+
    		"coordinate text" +
    		");";
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_TRX);
		db.execSQL(DATABASE_COORDINATE_CREATE);
		db.execSQL(DATABASE_CREATE_AGENT);
		
		System.out.println("sukses create table " + LOAN + " dan table Coordinate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS " + LOAN);
		db.execSQL("DROP TABLE IF EXISTS " + AGENT);
		db.execSQL("DROP TABLE IF EXISTS " + COORDINATE);
		onCreate(db);
	}

    public boolean checkDataBase() {
        File dbFile = new File(DB_FULL_PATH + "loandb.db");
        return dbFile.exists();
    }



    public boolean deleteDatabase(Context context){
        File dbFile =context.getDatabasePath("loandb.db");
        System.out.println("dbFile ::: "+dbFile);
        try{
            dbFile.delete();
        }
        catch (SQLiteAbortException e){
            System.out.println("sukses catch 1");
            e.printStackTrace();
        }catch (SQLiteException e){
            System.out.println("sukses catch 2");
            e.printStackTrace();
        }catch (Exception e){
            System.out.println("sukses catch 3");
            e.printStackTrace();
        }

        System.out.println("sukses delete db");
        return true;
    }
	

}
