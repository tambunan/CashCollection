package com.teravin.collection.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import com.teravin.collection.online.R;

/**
 * Created by dumatambunan on 1/17/14.
 */
public class PaymentDetailFragment extends BaseFragment {

    private Button btnPay;

    public static PaymentDetailFragment newInstanceDetailFragment() {
        PaymentDetailFragment pf = new PaymentDetailFragment();
        return pf;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.paydetail, container, false);
        btnPay = (Button) view.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startChildActivity(PaymentFragment.newInstance());
//                Intent testNotification = new Intent (getActivity().getApplicationContext(), CreateNotificationActivity.class);
//                startActivity(testNotification);
            }
        });


    return view;
    }
}
