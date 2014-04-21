package com.teravin.tracking;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.teravin.adapter.MySQLiteHelper;


/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class SampleAlarmReceiver extends BroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    private int hour, minute;
    public MySQLiteHelper dbsqlite;
    final static private long ONE_SECOND = 1000;
     private long INTERVALTHATIWANT = ONE_SECOND * 60 * 3;
     private static final String tag = "cashcollection";
  
    @Override
    public void onReceive(Context context, Intent intent) {   
        // BEGIN_INCLUDE(alarm_onreceive)
        /* 
         * If your receiver intent includes extras that need to be passed along to the
         * service, use setComponent() to indicate that the service should handle the
         * receiver's intent. For example:
         * 
         * ComponentName comp = new ComponentName(context.getPackageName(), 
         *      MyService.class.getName());
         *
         * // This intent passed in this call will include the wake lock extra as well as 
         * // the receiver intent contents.
         * startWakefulService(context, (intent.setComponent(comp)));
         * 
         * In this example, we simply create a new intent to deliver to the service.
         * This intent holds an extra identifying the wake lock.
         */
    	

        
//        System.out.println("calendar ::: "+calendar.getTime());
//        if(calendar.getTime().toString() == "" ){
//        	
//        }
    	Date ambil = new Date();
		hour = ambil.getHours();
	    minute = ambil.getMinutes();
	    System.out.println("hour :: minute :::: "+hour+" ::: "+minute);
	    if(hour == 17 && minute == 35){
	    	dbsqlite = new MySQLiteHelper(context.getApplicationContext());
			dbsqlite.deleteDatabase(context.getApplicationContext());
			Log.d(tag, "Delete succeed");
	    }
		
		
        Intent service = new Intent(context, TrackingService.class);
        
        // Start the service, keeping the device awake while it is launching.
        context.startService(service);
        Log.d("Tag", "Going To Service!");
        // END_INCLUDE(alarm_onreceive)
    }
	public void getDateAndTime(Context context){
		Date ambil = new Date();
		hour = ambil.getHours();
	    minute = ambil.getMinutes();
	    System.out.println("hour :: minute :::: "+hour+" ::: "+minute);
	    if(hour == 17 && minute == 0){
	    	dbsqlite = new MySQLiteHelper(context);
			dbsqlite.deleteDatabase(context);
	    }
	}
    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SampleAlarmReceiver.class);
        
     // UNTUK ALARM INI REQUEST CODENYA 0 YAH (WHERE HAVE I BEEN = 0, where am i = 1, backgroundtracking = 2, cashcollection = 3). 
        //JANGAN SAMA AMA YANG LAIN
        
        alarmIntent = PendingIntent.getBroadcast(context, 3, intent, 0);
        
        // Set the alarm to fire at approximately 8:30 a.m., according to the device's
        // clock, and to repeat once a day.
//        INTERVALTHATIWANT = MainActivity.interval * ONE_SECOND * 60;
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        		SystemClock.elapsedRealtime(), INTERVALTHATIWANT, alarmIntent);
        
        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);   
        
        Log.d("Start Alarm", "Alarm STARTED!");
    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
    	alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SampleAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        
        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the 
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        
        Log.d("Stop Alarm", "Alarm STOPPED!");
    }
    // END_INCLUDE(cancel_alarm)
    
}
