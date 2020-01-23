package com.scrappers.notepadpp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Date;

import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public  Runnable runnable = null;
    public NotificationManager nm;
    public NotificationCompat.Builder notifBuilder;
    public PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void Notify(String title, String Messege, int icon, String CHANNEL_ID, int notifyId, String packagename) {
        vibrator((Vibrator) getSystemService(VIBRATOR_SERVICE));

        //create a Custom Notification
        RemoteViews notificationMinimizedState = new RemoteViews(getPackageName(), R.layout.shrinked_notification);
        RemoteViews notificationExapndState = new RemoteViews(getPackageName(), R.layout.expanded_notification);


//        Intent i=new Intent("NotifyBtn.stop");
//        PendingIntent Pi= PendingIntent.getBroadcast(getApplicationContext(),22,i,PendingIntent.FLAG_UPDATE_CURRENT);
//        notificationExapndState.setOnClickPendingIntent(R.id.StoprecordingNotif,Pi);
//
//        notificationExapndState.setOnClickPendingIntent(R.id.PauserecordingNotif,Pi);

        //In order to open this activity on press
        Intent intent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setCustomContentView(notificationMinimizedState)
//                .setCustomBigContentView(notificationExapndState)
                .setCustomHeadsUpContentView(notificationMinimizedState)
                .setContentTitle(title)
                .setContentText(Messege)
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setAutoCancel(true);
        nm.notify(notifyId, notifBuilder.build());


    }




    public void vibrator(Vibrator v) {
        v.vibrate(20);
    }


    public void defineNotificationChannelID(String CHANNEL_ID) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }




private final class ServiceHandler extends Handler implements Runnable {
        public ServiceHandler(Looper looper){
            super(looper);
            postDelayed(this,100);
        }

    @Override
    public void handleMessage(Message msg) {



            super.handleMessage(msg);
    }

    @Override
    public void run() {
        Date d=new Date();
        long hours=d.getHours();
        long minutes=d.getMinutes();
        long seconds=d.getSeconds();

        String timeComponent=hours+":"+minutes+":"+seconds;

        if(timeComponent.equals("12:33:0")){
            Notify("Don't Forget To Take Your Today's Notes"
                    ,"BY SCRAPPERS",R.mipmap.noteicon,"ID44",44,"");
        }
    }
}




    //Background Workout
    @Override
    public void onCreate() {


        HandlerThread thread=new HandlerThread("", Thread.MAX_PRIORITY);
        thread.start();
        Looper loop=thread.getLooper();
        ServiceHandler serviceHandler=new ServiceHandler(loop);



//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//
//                Date d=new Date();
//                long hours=d.getHours();
//                long minutes=d.getMinutes();
//                long seconds=d.getSeconds();
//
//                String timeComponent=hours+":"+minutes+":"+seconds;
//
//                if(timeComponent.equals("9:0:0")){
//                    Notify("Don't Forget To Take Your Today's Notes"
//                            ,"BY SCRAPPERS",R.mipmap.noteicon,"ID44",44,"");
//                }
//
//                Toast.makeText(context, timeComponent, Toast.LENGTH_LONG).show();
//                handler.postDelayed(runnable, 1000);
//            }
//        };
//
//        handler.postDelayed(runnable, 0);
//        handler.getLooper();



    }


    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
//        handler.removeCallbacks(runnable);

        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        defineNotificationChannelID("ID44");
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }
}

