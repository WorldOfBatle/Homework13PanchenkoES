package com.example.homework13panchenkoes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public final class NotificationUtils {

    public static final String CHANNEL_ID_NOTES = "notes_channel";

    private NotificationUtils() {
        // утилитный класс
    }

    public static void ensureNotesChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        String name = context.getString(R.string.notification_channel_name);
        String description = context.getString(R.string.notification_channel_description);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_NOTES,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(description);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public static void showNoteNotification(Context context, int notificationId, String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_NOTES)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }
}
