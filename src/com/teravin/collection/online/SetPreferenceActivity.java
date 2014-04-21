package com.teravin.collection.online;

import android.app.Activity;
import android.os.Bundle;

import com.teravin.collection.fragment.PrefsFragment;

/**
 * Created by tyapeter on 1/14/14.
 */
public class SetPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }
}
