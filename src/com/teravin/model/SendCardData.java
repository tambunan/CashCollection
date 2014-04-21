package com.teravin.model;

import java.io.Serializable;

public class SendCardData implements Serializable{
	private String key;
	private String data;
	private byte[] encryptedData;
	private byte[] encryptedDataLoan;
	private String fCardnumber;
	private String l4pan;
	private String name;
	private String expiryDate;
	private String serviceCode;
	private byte[] ksn;
	private byte[] checkSum;
	private String pin;
	private Double amount;
//	private String description;
//	
	
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
//	public String getDescription() {
//		return description;
//	}
//	public void setDescription(String description) {
//		this.description = description;
//	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public byte[] getEncryptedData() {
		return encryptedData;
	}
	public void setEncryptedData(byte[] encryptedData) {
		this.encryptedData = encryptedData;
	}
	public byte[] getEncryptedDataLoan() {
		return encryptedDataLoan;
	}
	public void setEncryptedDataLoan(byte[] encryptedDataLoan) {
		this.encryptedDataLoan = encryptedDataLoan;
	}
	public String getfCardnumber() {
		return fCardnumber;
	}
	public void setfCardnumber(String fCardnumber) {
		this.fCardnumber = fCardnumber;
	}
	public String getL4pan() {
		return l4pan;
	}
	public void setL4pan(String l4pan) {
		this.l4pan = l4pan;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public byte[] getKsn() {
		return ksn;
	}
	public void setKsn(byte[] ksn) {
		this.ksn = ksn;
	}
	public byte[] getCheckSum() {
		return checkSum;
	}
	public void setCheckSum(byte[] checkSum) {
		this.checkSum = checkSum;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
