package com.health.myhealthapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * "Alarm" receives the boradcast made in MedplanActivity to remind the patient of a medication.
 * Notification and Vibration
 *
 * @author Ole Hannemann
 * @author Sam Wolter
 */
public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras =intent.getExtras();

        createSimpleNotification(context, extras.getLong("med_id"));
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 3000 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(1500);
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "com.health.myhealtapplication:alarm",
                    "com.health.myhealtapplication:alarm",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void createSimpleNotification(Context context, long id) {

        Meds meds=Meds.findById(Meds.class, id);
        //create notification channel required for api lvl 28+
        createNotificationChannel(context);

        String notificationcontent = "";
        if(meds == null){
            //gotta catch that nullpointer exceptions
            notificationcontent = "es ist Zeit Medikamente einzunehmen";
        } else {
            notificationcontent = "Es ist Zeit " +meds.getQuantity() +" "+ meds.getName() + " einzunehmen!";
        }

        Intent notificationIntent = new Intent(context, MedplanActivity.class);
        PendingIntent notificationpending = PendingIntent.getActivity(context,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, "com.health.myhealtapplication:alarm")
                .setContentTitle("Medikamenteneinahme")
                .setContentText(notificationcontent)
                .setSmallIcon(R.drawable.pusheen)
                .setContentIntent(notificationpending)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(12341337-Math.toIntExact(meds.getId()), notification);
    }

}

