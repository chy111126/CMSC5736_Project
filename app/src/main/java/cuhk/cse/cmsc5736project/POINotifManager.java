package cuhk.cse.cmsc5736project;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import cuhk.cse.cmsc5736project.models.POI;

/**
 * Created by TCC on 12/23/2017.
 */

public class POINotifManager {
    public static void notifyPOIArrival(Context context, POI poi) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_maps_place)
                        .setAutoCancel(true)
                        .setContentTitle("POI arrived")
                        .setContentText("You arrived " + poi.getName() + "!");

        // No actions for the notification
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, new Intent(), 0);
        builder.setContentIntent(contentIntent);


        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
