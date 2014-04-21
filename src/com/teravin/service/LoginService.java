package com.teravin.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.teravin.collection.online.R;
import com.teravin.security.SessionManager;
import com.teravin.util.APIBlink;

public class LoginService implements APIBlink {
	private Context context;

	public LoginService(Context context){
		this.context = context;
	}
	
	public boolean authenticate(String username, String password, String refNo, String token, Boolean activated){
		System.out.println("params ::: "+username + " :: "+refNo + " :: "+token + " :: "+password );
		
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("j_username", username));
        nameValuePair.add(new BasicNameValuePair("j_password" , password));
        nameValuePair.add(new BasicNameValuePair("j_channel" , "Cash Collection"));
        nameValuePair.add(new BasicNameValuePair("j_mobile", "mobile"));
        nameValuePair.add(new BasicNameValuePair("j_asdfg", "edvrfbtg"));
        nameValuePair.add(new BasicNameValuePair("callback", "callback"));
        
        if(activated == false){
    	  nameValuePair.add(new BasicNameValuePair("j_ref", refNo));
          nameValuePair.add(new BasicNameValuePair("j_token", token));
		}
        
        JSONObject response = sendLoginRequest(LOGIN, nameValuePair);
		
        if(response == null){
        	Toast.makeText(context, "Problem contacting server, please try again in a few minutes", Toast.LENGTH_LONG).show();
        	return false;
        }
        
        boolean loginSuccess = false;
        String sessionId = "";
        
        try {
			loginSuccess = response.getBoolean("success");
			sessionId = response.getString("JSESSIONID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if(loginSuccess){
        	SessionManager sessionManager = new SessionManager(context);
        	sessionManager.createLoginSession(username, sessionId);
			return true;
		}
		
		return false;
	}
	
	private JSONObject sendLoginRequest(String uri, List<NameValuePair> nameValuePair){
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(uri);
        
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
	            System.out.println("sb.toString() ::: "+ sb.toString());
	            JSONObject jsonObject = new JSONObject(sb.toString().replace("callback(", "").replace(")", ""));
	            jsonObject.put("JSESSIONID", httpClient.getCookieStore().getCookies().get(0).getValue());
	            System.out.println("+++++++++++masuk Login Siera +++++++++++" + jsonObject);
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
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("MessagerHandler", e.getMessage());
		} 
        
        return null;
	}
	
}
