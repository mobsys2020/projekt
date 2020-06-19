package com.health.myhealthapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

/**
 * empfängt sowohl Broadcasts von der Main Activity (normaler Alarm), als auch von der
 * Fullscreen Activity (Alarm nach Snooze-Zeit)
 * startet die Fullscreen Activity und den Sound Service, wenn Broadcast empfangen
 * Anmerkung: Seit API Level 29 ist das starten von Activities durch Broadcasts und Services aus dem
 * Hintergrund nur bei bestimmten Ausnahmen möglich.
 *
 * @author Ole Hannemann
 * @author Sam Wolter
 *
 */
public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TAG", "Receive ");
        createSimpleNotification(context);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(3000);
        }
    }

    private void createSimpleNotification (Context context){
//Erstellen NotificationBuilder
        Notification.Builder builder = new Notification.Builder((context));
//Eigenschaften

        builder.setContentTitle("My Title")
                .setContentText("My Content Text")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(createPendingIntent(context))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_DEFAULT);

//Erzeugen
        Notification notification = builder.build();
//Veröffentlichen
        int nID = 1337;
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(nID,notification);
    }

    private PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, MedplanActivity.class);
        PendingIntent pi = PendingIntent.getActivities(context, 0,
                new Intent[]{intent},PendingIntent.
                        FLAG_UPDATE_CURRENT);
        return pi;
    }

}

