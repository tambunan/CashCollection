package com.teravin.model;

/**
 * Created by tyapeter on 1/2/14.
 */
public class CardData {
    private String encryptData;

    private String fCardnumber;

    private String l4pan;

    private String name;

    private String expiryDate;

    private String serviceCcode;

    private String ksn;

    private byte[] checkSum;

    private String maskedPAN;

    public String getEncryptData() {
        return encryptData;
    }

    public void setEncryptData(String encryptData) {
        this.encryptData = encryptData;
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

    public String getServiceCcode() {
        return serviceCcode;
    }

    public void setServiceCcode(String serviceCcode) {
        this.serviceCcode = serviceCcode;
    }

    public String getKsn() {
        return ksn;
    }

    public void setKsn(String ksn) {
        this.ksn = ksn;
    }

    public byte[] getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(byte[] checkSum) {
        this.checkSum = checkSum;
    }

    public String getMaskedPAN() {
        return maskedPAN;
    }

    public void setMaskedPAN(String maskedPAN) {
        this.maskedPAN = maskedPAN;
    }
}
