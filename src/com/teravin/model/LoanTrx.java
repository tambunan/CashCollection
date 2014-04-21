package com.teravin.model;

import java.io.Serializable;

import com.teravin.collection.fragment.PaymentFragment;

public class LoanTrx implements Serializable{

	public String cardNo;
	public String refNo;
	public String regData;
	public String invoiceNo;
	public String installment;
    public String paymentFlag; // untuk memberi flag apakah transaksi sudah dibayar atau belum : 1) Dibayar 2) Belum dibayar 3)
    public String flagTrx; // untuk memberi flag apakah transaksi online atau offline : 1) Online 0) Offline
    public String fullpay; // untuk memberi flag apakah instalment yang dibayarkan full atau tidak :1) Full 0) Tidak Full
    public String nextCollectionDate;
    public String remark;
    
    
    //getter
    public String getPaymentFlag(){
		return this.paymentFlag;
	}
	
	public String getCardNo(){
		return this.cardNo;
	}
	
	public String getFlagTrx(){
		return this.flagTrx;
	}
	
	public String getFullPay(){
		return this.fullpay;
	}
	
	public String getRefNo(){
		return this.refNo;
	}
	
	public String getRegData(){
		return this.regData;
	}
	
	public String getInstallment(){
		return this.installment;
	}
	
	public String getInvoiceNo(){
		return this.invoiceNo;
	}
	
	public String getNextCollectionDate(){
		return this.nextCollectionDate;
	}
	
	public String getRemark(){
		return this.remark;
	}
	
	
	//setter
	public void setPaymentFlag(String _paymentFlag){
		this.paymentFlag = _paymentFlag;
	}
	
	public void setCardNo(String _cardNo){
		this.cardNo = _cardNo;
	}
	
	public void setFlagTrx(String _flagTrx){
		this.flagTrx = _flagTrx;
	}
	
	public void setFullPay(String _fullpay){
		this.fullpay = _fullpay;
	}
	
	public void setRefNo(String _refNo){
		this.refNo = _refNo;
	}
	
	public void setRegData(String _regData){
		this.regData = _regData;
	}
	
	public void setInstallment(String _installment){
		this.installment = _installment;
	}
	
	public void setInvoiceNo(String _invoiceNo){
		this.invoiceNo = _invoiceNo;
	}
	
	public void setNextCollectionDate(String _nextCollectionDate){
		this.nextCollectionDate = _nextCollectionDate;
	}
	
	public void setRemark(String _remark){
		this.remark = _remark;
	}
}
