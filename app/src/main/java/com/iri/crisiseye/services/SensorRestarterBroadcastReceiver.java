package com.iri.crisiseye.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.BatteryManager;
import android.os.IBinder;
import android.widget.Toast;

import com.iri.crisiseye.LoginActivity;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    SensorService sensorService;
    private boolean binded = false;
    ServiceConnection sensordatafeedConn;
    double latitude;
    double longitude;
    String deviceID = "";
    LoginActivity loginActivity;
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        String in = intent.getAction();
        System.out.println("in is :: "+ in);
        latitude = intent.getDoubleExtra("latitude",0.0);
        longitude = intent.getDoubleExtra("longitude",0.0);
        deviceID = intent.getStringExtra("deviceID");
        System.out.println(">< latitude in sensor broadcast receiver :: "+latitude);
        System.out.println(">< longitude in sensor broadcast receiver :: "+longitude);
        System.out.println(">< deviceID in sensor broadcast receiver :: "+deviceID);

//        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

//        System.out.println(">< battery level in sensor broadcast receiver :: "+level);
//            Toast.makeText(LoginActivity.this, "battery level showing up! "+level , Toast.LENGTH_SHORT).show();
//        SensorService.battery_level = String.valueOf(level);

        if(intent.getAction().equals("xyz"))
        {

//            SensorService
            Toast.makeText(context,"going to start service", Toast.LENGTH_SHORT).show();

             sensordatafeedConn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    System.out.println(">< in service connected");
                    SensorService.SensorDataFeedBinder binder =(SensorService.SensorDataFeedBinder)iBinder;
                    sensorService =  binder.getService();
                    if(sensorService == null)
                    {
                        System.out.println("sensor service is null");
                    }
                    else if(sensorService != null)
                    {
                        System.out.println("sensor service is not null");
                    }
                    binded = true;
                    sensorService.startTimer(latitude,longitude,deviceID);
//                    sensorService.initializeTimerTask();
                    }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    System.out.println(">< in service disconnected");
                    binded = false;
                }
            };


            Intent service_intent = new Intent(context, SensorService.class);

            System.out.println(">< latitude being sent to service :: "+latitude);
            System.out.println(">< longitude being sent to service :: "+longitude);
            service_intent.putExtra("latitude",latitude);
            service_intent.putExtra("longitude",longitude);
            System.out.println(">< latitude sent to service :: "+latitude);
            System.out.println(">< longitude sent to service :: "+longitude);
            System.out.println(">< binding service");
            context.bindService(service_intent,sensordatafeedConn,Context.BIND_AUTO_CREATE);
            System.out.println(">< bound service");
//            context.startService(new Intent(context, SensorService.class));

            /*if(sensorService != null)
            {
                System.out.println("KKOOkABURRA");
              this.sensorService.initializeTimerTask();
            }
            if(sensorService == null)
            {
             // always comes in this condition

                System.out.println("GRAY NICOLLS");
                this.sensorService.initializeTimerTask();
            }*/
//            this.sensorService.initializeTimerTask();
        }

        else if(intent.getAction().equals("abc")){
            System.out.println("Service Stops! Oooooooooooooppppssssss!!!!");
            Toast.makeText(context,"Service Stopping !! ", Toast.LENGTH_SHORT).show();

            if(binded)
            {
                context.unbindService(sensordatafeedConn);
                binded = false;
            }

            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(setIntent);
            /*finish();
            System.exit(0);
            moveTaskToBack(true);*/
        }

//        System.out.println("Service Stops! Oooooooooooooppppssssss!!!!");
//        context.startService(new Intent(context, SensorService.class));





    }

    public void setActivity(LoginActivity activity) {
        this.loginActivity = activity;
    }
}
