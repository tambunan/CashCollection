package com.teravin.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.preference.PreferenceManager;
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


import com.teravin.security.SessionManager;
import com.teravin.util.APIBlink;

public class DownloadService implements APIBlink {

	private Context context;
	static InputStream is = null;
	private String jsonString;
	SessionManager session;
    private LoginService loginService;

    private String usernameDB = "";
    private String passwordDB = "";
    private static final String TAG = "Cash Collection";
    
    public DownloadService(Context context){
		this.context = context;
	}
    
	public JSONObject sendRequestDataLoan(String url) throws JSONException{
		
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	    nameValuePair.add(new BasicNameValuePair("callback", "callback"));
	    DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
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
        else {
            try {
                login();
                SharedPreferences pref = session.getPref();

                String j_sessionID = pref.getString("JSESSIONID", "0");
                String domain = pref.getString("domain", "www1.teravintech.com");
                String path = pref.getString("path", "/wallet");

                BasicClientCookie2 cookie = new BasicClientCookie2("JSESSIONID",j_sessionID);
                cookie.setVersion(0);
                cookie.setDomain(domain);
                cookie.setPath(path);

                CookieStore cookieStore = new BasicCookieStore();
                cookieStore.addCookie(cookie);
                httpClient.setCookieStore(cookieStore);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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
		            int offset = jsonString.indexOf('(') + 1;
		  		  	int end = jsonString.indexOf(')') ;
		  		    String result = jsonString.substring(offset, end);
		            Log.d(TAG, "jsonString @!#$%^&*() : **********"+jsonString);
		            JSONObject jsonObject = new JSONObject(result);
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
		return null;
	}
	
    private void login() throws Exception{
        try {
            loadPref();
            session = new SessionManager(context.getApplicationContext());
//            session.logout();
            loginService = new LoginService(context.getApplicationContext());
            List<NameValuePair> paramsLogin = new ArrayList<NameValuePair>();
            paramsLogin.add(new BasicNameValuePair("j_username",usernameDB));
            paramsLogin.add(new BasicNameValuePair("j_password",passwordDB));
            paramsLogin.add(new BasicNameValuePair("j_channel","Loan"));
            paramsLogin.add(new BasicNameValuePair("j_mobile","mobile"));
            paramsLogin.add(new BasicNameValuePair("j_asdfg","edvrfbtg"));
            paramsLogin.add(new BasicNameValuePair("callback",""));
           // JSONObject jsonLogin = loginService.getLogin(LOGIN, paramsLogin);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPref() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        usernameDB = sharedPreferences.getString("user", "agent");
        passwordDB = sharedPreferences.getString("password", "password");
    }
}
