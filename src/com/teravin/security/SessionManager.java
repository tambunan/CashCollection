package com.teravin.security;

import java.util.Calendar;

import com.teravin.collection.online.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class SessionManager {
	private Context context;
	private SharedPreferences pref;
	private Editor editor;
	
	private static final String PREF_NAME = "siera-session";
	private static final int EXPIRE_TIME = 5; // in minutes
	
	private SessionManager(){
		
	}
	
	public SessionManager(Context context){
		this.context = context;
		pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		editor = pref.edit();
	}
	
	public void createLoginSession(String username, String sessionId){
		Calendar expireTime = Calendar.getInstance();
		expireTime.add(Calendar.MINUTE, EXPIRE_TIME);
			
		editor.putString("username", username);
		editor.putLong("expireTime", expireTime.getTimeInMillis());
		editor.putBoolean("isLoggedIn", true);
		editor.putString("JSESSIONID", sessionId);
		
		editor.commit();
	}
	
	public void checkLogin(){
		if(!isLoggedIn()){
			logout();
			return;
		}
		
		Calendar now = Calendar.getInstance();
		
		if(now.getTimeInMillis() > getExpireTime()){
			logout();
			return;
		}
		
		resetExpireTime();
	}
	
	public void resetExpireTime(){
		Calendar expireTime = Calendar.getInstance();
		expireTime.add(Calendar.MINUTE, EXPIRE_TIME);
		
		editor.putLong("expireTime", expireTime.getTimeInMillis());
		editor.commit();
	}
	
	public void logout(){
		editor.clear();
		editor.commit();
		
		Intent intent = new Intent(context, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		context.startActivity(intent);
		Toast.makeText(context, "Session Timeout, Please Login again", Toast.LENGTH_SHORT).show();
	}
	
	public long getExpireTime(){
		return pref.getLong("expireTime", 0);
	}
	
	public boolean isLoggedIn(){
        return pref.getBoolean("isLoggedIn", false);
    }

	public SharedPreferences getPref() {
		return pref;
	}
	
}
