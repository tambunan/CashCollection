package com.teravin.collection.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.teravin.collection.online.R;

/**
 * Created by tyapeter on 1/14/14.
 */
public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
