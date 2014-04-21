package com.teravin.collection.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.teravin.collection.online.R;
import com.teravin.collection.online.GPSTracker;
import com.teravin.collection.online.LoginActivity;
import com.teravin.util.APIBlink;

/**
* Created by dumatambunan on 3/3/14.
*/
public class DepositPointFragment extends BaseFragment implements APIBlink, OnItemClickListener {
	
	ArrayList<HashMap<String, String>> listBank = new ArrayList<HashMap<String,String>>();
	ListView tampil;
	GPSTracker gps;
	LoginActivity log = new LoginActivity();
	
	public static DepositPointFragment newInstance() {
        DepositPointFragment f = new DepositPointFragment();

        return f;
	}


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.depositpoint, container, false);
        tampil = (ListView) mView.findViewById(R.id.depositlist);
        LoginActivity log = new LoginActivity();
        listBank = log.listBank;
        ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), listBank,
	            R.layout.content_activity, new String[] { "cardNo", "name",
            "alamat", "mobilePhoneNo", "installmentAmount", "distance"}, new int[] { R.id.cardno,
            R.id.amount, R.id.cardname, R.id.mobileno,R.id.address, R.id.city});
	    tampil.setAdapter(adapter);
	    
	    int condition = log.condition;
	    System.out.println("===== CEK CONDITION " + condition);
//	    if (condition == 0){
//	    	Toast.makeText(getActivity(), "There is no nearby BII bank in 20 Km radius", Toast.LENGTH_LONG).show();
//	    } else {
	    	Toast.makeText(getActivity(), "Showing result of nearby bank", Toast.LENGTH_LONG).show();
//	    }
	    tampil.setOnItemClickListener(this);
	    
        return mView;
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //datanya diambil dari loadList aja, pake position...jangan dari view..
    	System.out.println("CEKCEKCdH " + position + " " + id);
        final HashMap<String, String> loan = listBank.get(position);
        GPSTracker gps = new GPSTracker(getActivity().getApplicationContext());
        final String name = loan.get("name");
        System.out.println("COBA INI " + name);
        final String coordinate = loan.get("coordinate");
        System.out.println("COBA INI " + coordinate);
        String [] pecah = coordinate.split(",");
        String destLatitude = pecah[0];
        String destLongitude = pecah[1];
        System.out.println("DAPET GAK NIH SOURCE DESTINATIONNYA? " + gps.getLatitude() + " " + gps.getLongitude());
        System.out.println("DAPET GAK NIH DESTINATIONNYA? " + destLatitude + " " + destLongitude);   
        final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ gps.getLatitude() + "," + gps.getLongitude() + "&daddr=" + (destLatitude + "," + destLongitude)));
         intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                             startActivity(intent);

    }

  }