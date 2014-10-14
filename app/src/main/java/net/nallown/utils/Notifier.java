package net.nallown.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import net.nallown.animetwist.R;

/**
 * Created by Nasir on 30/08/2014.
 */
public class Notifier {

	public static void showNotification(String title, String message,
	                                    boolean alert, Context context) {
		NotificationCompat.Builder mNotification =
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(title)
						.setContentText(message);

		if (alert) {
			Uri alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mNotification.setSound(alertUri);
		}

		NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(0, mNotification.build());
	}

	public static void cancelNotification(Context context, int notificationId) {
		if (Context.NOTIFICATION_SERVICE != null) {
			NotificationManager mNotificationManager =
					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(notificationId);
		}
	}
}
