package nyc.c4q.syd.updateme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

public class AlarmService extends Service {

    private NotificationManager mManager;
    private int NOTIFICATION = 111;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent result = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, NOTIFICATION, result, PendingIntent.FLAG_UPDATE_CURRENT);
        result.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        String task = intent.getStringExtra("task");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("UpdateMe Reminder");
        builder.setContentText("Task reminder: " + task);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pIntent);
        builder.setAutoCancel(true);

        Notification notification = builder.build();
        mManager.notify(NOTIFICATION, notification);
    }

    @Override
    public void onDestroy() {
        mManager.cancel(NOTIFICATION);
    }
}
