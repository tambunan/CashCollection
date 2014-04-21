package com.teravin.adapter;

/**
 * Created by dumatambunan on 2/17/14.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.teravin.security.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

    static InputStream is = null;
    static String jsonStr = "";
    private Context context;
    SessionManager session;
    private JSONObject JObj;
    // constructor
    public JSONParser(Context context){
        this.context = context;
    }

    public String makeHttpRequest(String url, List<NameValuePair> params) {
    	 DefaultHttpClient httpClient = new DefaultHttpClient();
 		HttpPost httpPost = new HttpPost(url);
 		
         try {
             httpPost.setEntity(new UrlEncodedFormEntity(params));
             session = new SessionManager(this.context);
            
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }
         
         if(session.isLoggedIn()){
         	
     		SharedPreferences pref = session.getPref();

     		String j_sessionID = pref.getString("JSESSIONID", "0");
     		String domain = pref.getString("domain", "www1.teravintech.com");
     		String path = pref.getString("path", "/collection");

     		BasicClientCookie2 cookie = new BasicClientCookie2("JSESSIONID",j_sessionID);
     		cookie.setVersion(0);
     		cookie.setDomain(domain);
     		cookie.setPath(path);

     		CookieStore cookieStore = new BasicCookieStore();
     		cookieStore.addCookie(cookie);
     		httpClient.setCookieStore(cookieStore);
     		
         }
         
 		 try {
 			
 	            HttpResponse response = httpClient.execute(httpPost);
 	            if(response.getStatusLine().getStatusCode() == 200){
 	            	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
 		            String line = "";
 		            StringBuffer sb = new StringBuffer();
 		            while ((line = rd.readLine()) != null) {
 		              sb.append(line);
 		            }
 		           jsonStr=sb.toString();
 		           System.out.println("jsonStr :::: "+ jsonStr);
 		            return jsonStr;
 	            }
 	            
 	        } catch (ClientProtocolException e) {
 	            // writing exception to log
 	            e.printStackTrace();
 	            Log.e("MessagerHandler", e.getMessage());
 	        } catch (IOException e) {
 	            // writing exception to log
 	            e.printStackTrace();
 	            Log.e("MessagerHandler", e.getMessage());
 	        } 
 		return null;
    }
    
    public JSONObject convertStringToJSON(String data){
    	System.out.println("data ::::: "+ data);
    	int offset = data.indexOf('(') + 1;
		int end = data.indexOf(')') ;
		String result = data.substring(offset, end);
        try {
			JSONObject jsonObject = new JSONObject(result);
			 return jsonObject;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return JObj;
    }
    
    public JSONObject convertStringToJSON2(String data){
    	try {
			JSONObject jsonObject = new JSONObject(data);
			return jsonObject;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }


}