package net.nallown.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import net.nallown.animetwist.R;

/**
 * Created by Nasir on 30/08/2014.
 */
public class Notifier {

	public static void showNotification(String title, String message, Context context){
		NotificationCompat.Builder mNotification=
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(title)
						.setContentText(message);


		NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(0, mNotification.build());
	}

	public static void cancelNotification(Context context, int notificationId){
		if (Context.NOTIFICATION_SERVICE != null) {
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager =
					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(notificationId);
		}
	}
}
