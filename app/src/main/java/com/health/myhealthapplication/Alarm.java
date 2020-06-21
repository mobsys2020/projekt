package com.health.myhealthapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

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

    private void createSimpleNotification(Context context, long id) {

        Meds meds=Meds.findById(Meds.class, id);
//create notification builder
        Notification.Builder builder = new Notification.Builder((context));
//properties

        builder.setContentTitle("Erinnerung an eine Einnahme")
                .setContentText("Es ist Zeit " +meds.getQuantity() +" "+ meds.getName() + " einzunehmen!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(createPendingIntent(context))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_DEFAULT);

//build
        Notification notification = builder.build();
//publish
        int nID = 1337;
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(nID, notification);
    }

    private PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, MedplanActivity.class);
        PendingIntent pi = PendingIntent.getActivities(context, 0,
                new Intent[]{intent}, PendingIntent.
                        FLAG_UPDATE_CURRENT);
        return pi;
    }

}

