package com.teravin.util;

import java.util.ArrayList;


public interface APIBlink {
    static final String LOGIN = "http://www1.teravintech.com:9106/collection/j_spring_security_check?spring-security-redirect=/login/ajaxSuccess";
    static final String terasoHost ="http://www1.teravintech.com:9105/";
    static final String remoteNetbankHost = "http://www1.teravintech.com:9101/RemoteNetbank/host/";
    static final String paymentListAPI="http://www1.teravintech.com:9106/collection/agent/paymentList";
    static final String activityListAPI="http://www1.teravintech.com:9106/collection/agent/activityList";
    static final String agentPay="http://www1.teravintech.com:9106/collection/agent/pay";
    static final String settlementListAPI="http://www1.teravintech.com:9106/collection/agent/settleList";
    static final String historyPointList ="http://www1.teravintech.com:9106/collection/agent/historyPointList";
    static final String agentCurrentPosition = "http://www1.teravintech.com:9106/collection/agent/currentPosition";
    static final String depositPoint = "http://www1.teravintech.com:9106/collection/agent/depositList";
    static final String settlementAPI="http://www1.teravintech.com:9106/collection/agent/settlement";
    static final String uploadTrxOffline="http://www1.teravintech.com:9106/collection/agent/uploadTrx";
    static final String checkIMSI="http://www1.teravintech.com:9106/collection/agent/checkImsi";
    static final String getToken = "http://www1.teravintech.com:9106/collection/agent/generateToken";
    static final String FORMAT_JSON = "format=json";
	
}
