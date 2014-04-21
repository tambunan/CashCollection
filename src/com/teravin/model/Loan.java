package com.teravin.model;

import java.io.Serializable;

public class Loan implements Serializable{

	/**
	 * callback({"callback":"callback","action":"pick",
	 * "controller":"loanInq","date":"13/12/2013",
	 * "loanList":[{"id":1,"userAgent":"agent",
	 * "card":{"class":"com.teravin.wallet.Card","id":1,
	 * "cardName":"Duma R Tambunan","cardNo":"6019001068662380"},
	 * "name":"Agent","mobilePhoneNo":"6281113","address1":null,
	 * "address2":null,"city":null,"zipCode":null,
	 * "installmentAmount":15000,"datePick":"13/12/2013"}],"total":1})

	 * **/
	public String id;
	public String invoiceNo;
	public String custName;
	public String mobilePhoneNo;
	public String phoneNo;
	public String address1;
	public String address2;
	public String totalAmount;
	public String trxDate;
	public String paymentFlag;
	public String langt;
    public String longt;
    public String nextCollectionDate;
    public String remark;
    public String cardNoAgent;
    public String refNo;
    public String kota;
    public String kelurahan;
    public String kecamatan;
    public String provinsi;
    public String rt;
    public String rw;
    
    public String getId(){
		return id;
	}
	
	public void setId(String _id){
		this.id=_id;
	}
	
	public String getInvoiceNo(){
		return invoiceNo;
	}
	
	public void setInvoiceNo(String _invoiceNo){
		this.invoiceNo=_invoiceNo;
	}
	
	public String getCustName(){
		return custName;
	}
	
	public void setCustName(String _custName){
		this.custName=_custName;
	}
	
	public String getMobilePhoneNo(){
		return mobilePhoneNo;
	}
	
	public void setMobilePhoneNo(String _mobilePhoneNo){
		this.mobilePhoneNo=_mobilePhoneNo;
	}
	
	public String getPhoneNo(){
		return phoneNo;
	}
	
	public void setPhoneNo(String _phoneNo){
		this.phoneNo=_phoneNo;
	}
	
	public String getAddress1(){
		return address1;
	}
	
	public void setAddress1(String _address1){
		this.address1=_address1;
	}
	
	public String getAddress2(){
		return address2;
	}
	
	public void setAddress2(String _address2){
		this.address2=_address2;
	}
	
	public String getKota(){
		return kota;
	}
	
	public void setKota(String _kota){
		this.kota=_kota;
	}
	
	public String getProvinci(){
		return provinsi;
	}
	
	public void setProvinsi(String _provinsi){
		this.provinsi=_provinsi;
	}
	
	public String getRt(){
		return rt;
	}
	
	public void setRt(String _rt){
		this.rt=_rt;
	}
	
	public String getRw(){
		return rw;
	}
	
	public void setRw(String _rw){
		this.rw=_rw;
	}
	
	public String getKelurahan(){
		return kelurahan;
	}
	
	public void setKelurahan(String _kelurahan){
		this.kelurahan=_kelurahan;
	}
	
	public String getKecamatan(){
		return kecamatan;
	}
	
	public void setKecamatan(String _kecamatan){
		this.kecamatan=_kecamatan;
	}
	
	public String getTotalAmount(){
		return totalAmount;
	}
	
	public void setTotalAmount(String _totalAmount){
		this.totalAmount=_totalAmount;
	}
	
	public String getTrxDate(){
		return trxDate;
	}
	
	public void setTrxDate(String _trxDate){
		this.trxDate=_trxDate;
	}
	
	public String getPaymentFlag(){
		return paymentFlag;
	}
	
	public void setPaymentFlag(String _paymentFlag){
		this.paymentFlag=_paymentFlag;
	}
	
	public String getLangt(){
		return langt;
	}
	
	public void setLangt(String _langt){
		this.langt=_langt;
	}
	
	public String getLongt(){
		return longt;
	}
	
	public void setLongt(String _longt){
		this.longt=_longt;
	}
	
	public String getNextCollectionDate(){
		return nextCollectionDate;
	}
	
	public void setNextCollectionDate(String _nextCollectionDate){
		this.nextCollectionDate=_nextCollectionDate;
	}
	
	public String getRemark(){
		return remark;
	}
	
	public void setRemark(String _remark){
		this.remark=_remark;
	}
	
	public String getCardNoAgent(){
		return cardNoAgent;
	}
	
	public void setCardNoAgent(String _cardNoAgent){
		this.cardNoAgent=_cardNoAgent;
	}
	
	public String getRefNo(){
		return refNo;
	}
	
	public void setRefNo(String _refNo){
		this.refNo=_refNo;
	}
}
