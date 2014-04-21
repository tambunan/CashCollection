package com.teravin.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.teravin.collection.online.R;
import com.teravin.security.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PaymentService {
	private Context context;
	private JSONObject obj=null;
	private String jsonString;
	//SessionManager session;
    private static final String TAG = "Cash Collection";
    SessionManager session;
    
	public PaymentService(Context context){
		this.context = context;
	}
	
	public JSONObject sendHTTPRequest(String url, List<NameValuePair> nameValuePair){
		System.out.println("masuk isLogin: JSON Array");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		System.out.println("masuk isLogin: JSON Array2");
		session = new SessionManager(this.context);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            System.out.println("masuk isLogin: JSON Array3");
           session = new SessionManager(this.context);
        } catch (UnsupportedEncodingException e) {
           e.printStackTrace();
        }
        if(session.isLoggedIn()){
        	System.out.println("masuk isLogin: JSON Array");
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
		            
		            jsonString=sb.toString();
		            System.out.println("result String: **********"+jsonString);
		            JSONObject jsonObject = new JSONObject(jsonString);
		            Log.d(TAG, "jsonObject => "+jsonObject);
		            return jsonObject;
		           
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
		 catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("MessagerHandler", e.getMessage());
			} 
		return obj;
	}
}
