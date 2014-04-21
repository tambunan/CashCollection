package com.teravin.collection.online;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabContentFactory;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import com.google.android.gcm.GCMRegistrar;
import com.teravin.collection.online.R;
import com.teravin.collection.fragment.*;
import com.teravin.service.PaymentService;
import com.teravin.util.APIBlink;

/**
 *
 */
public class TabsFragmentActivity extends FragmentActivity implements TabHost.OnTabChangeListener {

	private TabHost mTabHost;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabsFragmentActivity.TabInfo>();
	private TabInfo mLastTab = null;
	private Map<String, BaseFragment> fragmentMap = new HashMap<String, BaseFragment>();
	private Map<String, Integer> indexMap = new HashMap<String, Integer>();
    private int iType = 1;
    
    ///////////////////////////// AYAMBAKAR
    AsyncTask<Void, Void, Void> mRegisterTask;
    public static String name;
	public static String email;
	///////////////////////////// AYAMBAKAR
	private String[] paymentListArr;
	ArrayList<HashMap<String, String>> loanList;
	public static ArrayList<HashMap<String, String>> kordinat = new ArrayList<HashMap<String,String>>();
    //private SharedPreferences prefere;
	/**
	 *
	 */
	private class TabInfo {
		 private String tag;
         private Class<?> clss;
         private Bundle args;
         private BaseFragment fragment;
         TabInfo(String tag, Class<?> clazz, Bundle args) {
        	 this.tag = tag;
        	 this.clss = clazz;
        	 this.args = args;
         }

	}
	/**
	 *
	 *
	 */
	class TabFactory implements TabContentFactory {

		private final Context mContext;

	    /**
	     * @param context
	     */
	    public TabFactory(Context context) {
	        mContext = context;
	    }

	    /** (non-Javadoc)
	     * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
	     */
	    public View createTabContent(String tag) {
	        View v = new View(mContext);
	        v.setMinimumWidth(0);
	        v.setMinimumHeight(0);
	        return v;
	    }

	}
	/** (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
		
		//////////////////////////////////////AYAMBAKAR
		        
		Intent i = getIntent();
		
		name = LoginActivity.usernameDB;
		if (name.equals("")){
			name = MobileActivation.usernameDB;
		}
//		email = i.getStringExtra("email");	
		System.out.println("=========== DATA DARI TABS FRAGMENT ACTIVITY " + name + " " + email);
		
		
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
		"com.androidhive.pushnotifications.DISPLAY_MESSAGE"));
		
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		// Check if regid already presents
		if (regId.equals("")) {
		// Registration is not present, register now with GCM			
		GCMRegistrar.register(this, "192157990405");
		} else {
		// Device is already registered on GCM
		if (GCMRegistrar.isRegisteredOnServer(this)) {
		// Skips registration.				
		Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
		} else {
		// Try to register again, but not in the UI thread.
		// It's also necessary to cancel the thread onDestroy(),
		// hence the use of AsyncTask instead of a raw thread.
		final Context context = this;
		mRegisterTask = new AsyncTask<Void, Void, Void>() {
		
		@Override
		protected Void doInBackground(Void... params) {
		// Register on our server
		// On server creates a new user
		ServerUtilities.register(context, name, regId);
		return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
		mRegisterTask = null;
		}
		
		};
		mRegisterTask.execute(null, null, null);
		}
		}
		
		////////////////////////////////////////////// AYAMBAKAR
	  
	}

	/** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

	/**
	 * Initialise the Tab Host
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Payment").setIndicator("Payment"), ( tabInfo = new TabInfo("Payment", PaymentListFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Activity").setIndicator("Activity"), ( tabInfo = new TabInfo("Activity", ActivityListFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Settlement").setIndicator("Settlement"), ( tabInfo = new TabInfo("Settlement", SettlementFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Deposit").setIndicator("Deposit"), ( tabInfo = new TabInfo("Deposit", DepositFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);


        this.onTabChanged("Payment");

        mTabHost.setOnTabChangedListener(this);

	}


	private static void addTab(TabsFragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = (BaseFragment) activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }

        tabHost.addTab(tabSpec);
       // tabHost.getTabWidget().getChildAt(0).getLayoutParams().height =40;
      //  tabHost.getTabWidget().getChildAt(0).getLayoutParams().height =40;
        //tabHost.getTabWidget().getChildAt(1).getLayoutParams().height =40;

	}

	/** (non-Javadoc)
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		TabInfo newTab = this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                	ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = (BaseFragment) Fragment.instantiate(this, newTab.clss.getName(), newTab.args);
                    ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
		}
    }

	//TODO
	public void startChildActicityForResult(int requestCode, int resultCode, BaseFragment fragment){
		fragment.setRequestCode(requestCode);
		fragment.setResultCode(resultCode);
		startChildActivity(fragment);
	}

	public void startChildActivity(BaseFragment fragment){
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.realtabcontent, fragment, fragment.getClass().getSimpleName());
        setLevel(true);
        fragmentMap.put(mTabHost.getCurrentTabTag()+indexMap.get(mTabHost.getCurrentTabTag()), fragment);
        ft.commit();
        this.getSupportFragmentManager().executePendingTransactions();
	}




	private void setLevel(boolean isAdd){
		Integer idx = getLevel();
        if(isAdd)
        	indexMap.put(mTabHost.getCurrentTabTag(), ++idx);
        else
        	indexMap.put(mTabHost.getCurrentTabTag(), --idx);
	}

	private int getLevel(){
		Integer idx = indexMap.get(mTabHost.getCurrentTabTag());
        if(idx==null || idx<=0){
        	idx = 0;
        }
        return idx;
	}
	//note : child acticity == Fragment
	private BaseFragment getLastChildActivity(){
		return fragmentMap.get(mTabHost.getCurrentTabTag()+indexMap.get(mTabHost.getCurrentTabTag()));
	}

	public void finishChildActicity(Intent intent){
		finishChildActicity(-1, -1, intent);
	}

	public void finishChildActicity(int requestCode, int resultCode, Intent intent){
		BaseFragment fragment = getLastChildActivity();
		 if(fragment!=null){
			 FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			 ft.detach(fragment);
			 fragmentMap.remove(mTabHost.getCurrentTabTag());
			 setLevel(false);

			 if(getLevel()==0){
				 fragment = mLastTab.fragment;
			 }else{
				 fragment = fragmentMap.get(mTabHost.getCurrentTabTag()+indexMap.get(mTabHost.getCurrentTabTag()));
			 }
			 fragment.setIntent(intent);
			 if(intent != null){
				 fragment.onActivityResult(requestCode, resultCode, intent);
			 }
			 ft.attach(fragment);

            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
            Log.d("DEBUG", "fragment");
		 }else{
			 Log.d("DEBUG", "onBackPressed()");
			 super.onBackPressed();
		 }
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    /*
     * Because it's onlt ONE option in the menu.
     * In order to make it simple, We always start SetPreferenceActivity
     * without checking.
     */

        Intent intent = new Intent();
        intent.setClass(TabsFragmentActivity.this, SetPreferenceActivity.class);
        startActivityForResult(intent, 0);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadPref();
    }

    private void loadPref() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setiType(Integer.parseInt(sharedPreferences.getString("key_background", "0")));
    }
	@Override
	public void onBackPressed() {
		finishChildActicity(null);
	}

    public int getiType() {
        return iType;
    }

    public void setiType(int iType) {
        this.iType = iType;
    }
    
	////////////////////////////////AYAMBAKAR PRODUCTIOn
	    
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
	String newMessage = intent.getExtras().getString("message");
	// Waking up mobile if it is sleeping
	WakeLocker.acquire(getApplicationContext());
	
	/**
	* Take appropriate action on this message
	* depending upon your app requirement
	* For now i am just displaying it on the screen
	* */
	
	// Showing received message
	//lblMessage.append(newMessage + "\n");			
	Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
	
	// Releasing wake lock
	WakeLocker.release();
	}
	};
	
	@Override
	protected void onDestroy() {
	if (mRegisterTask != null) {
	mRegisterTask.cancel(true);
	}
	try {
	unregisterReceiver(mHandleMessageReceiver);
	GCMRegistrar.onDestroy(this);
	} catch (Exception e) {
	Log.e("UnRegister Receiver Error", "> " + e.getMessage());
	}
	super.onDestroy();
	}
	
	//////////////////////////////// AYAMBAKAR PRODUCTION
	   
}
