package com.teravin.collection.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.teravin.collection.online.TabsFragmentActivity;

public class BaseFragment extends Fragment{
	protected View mView;
	private Intent intent;
	private int requestCode;
	private int resultCode;
	
	public void startChildActivity(BaseFragment fragment){
		((TabsFragmentActivity)getActivity()).startChildActivity(fragment);
	}
	
	public void startChildActicityForResult(int requestCode, int resultCode, BaseFragment fragment){
		((TabsFragmentActivity)getActivity()).startChildActicityForResult(requestCode, resultCode, fragment);
	}
	
	public void finishChildActivity(){
		((TabsFragmentActivity)getActivity()).finishChildActicity(null);
	}
	
	public void finishChildActivity(int requestCode, int resultCode, Intent intent){
		((TabsFragmentActivity)getActivity()).finishChildActicity(requestCode, resultCode, intent);
	}
	
	protected View findViewById(int id){
		return mView.findViewById(id);
	}
	
	public Intent getIntent() {
		return intent;
	}
	
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}
	public int getRequestCode() {
		return requestCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public int getResultCode() {
		return resultCode;
	}
}
