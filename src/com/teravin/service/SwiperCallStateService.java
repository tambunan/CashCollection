package com.teravin.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: tyapeter
 * Date: 12/20/13
 * Time: 9:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class SwiperCallStateService extends Service {
    public static final String INTENT_ACTION_INCOMING_CALL = "com.teravin.service.INCOMING_CALL";

    private TelephonyManager callManager;
    private IncomingCallListener incomingCallListener;

    // -----------------------------------------------------------------------
    // Debug Function
    // -----------------------------------------------------------------------

    private static final String LOG_TAG = SwiperCallStateService.class.getName();
    private boolean DEBUG_MODE = true;
    private void log(String msg) {
        if (DEBUG_MODE) Log.d(LOG_TAG, msg);
    }

    // -----------------------------------------------------------------------
    // Private Function
    // -----------------------------------------------------------------------

    private void initCallManager() {
        if (callManager == null) {
            log("Add Incoming Call Manager");
            callManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            incomingCallListener = new IncomingCallListener();
            callManager.listen(incomingCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void removeCallManager() {
        if (callManager != null) {
            log("Remove Call Manager");
            callManager.listen(this.incomingCallListener, PhoneStateListener.LISTEN_NONE);
            callManager = null;
        }
    }

    private class IncomingCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int callstate, String incomingNumber) {
            if (callstate == TelephonyManager.CALL_STATE_RINGING) {
                log("Incoming CALL!!!");
                sendBroadcast(new Intent(INTENT_ACTION_INCOMING_CALL));
            }
        }
    }

    // -----------------------------------------------------------------------
    // Interface Function
    // -----------------------------------------------------------------------

    @Override
    public void onCreate() {
        initCallManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        removeCallManager();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
