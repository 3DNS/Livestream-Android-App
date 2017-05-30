package dom1nic.livestream;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayerService extends Service {
    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("CheckStream", "Checking...");
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("noti", true))
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL("https://dom1nic.eu/viewer/stream.php?channel=live").openConnection();
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String status = in.readLine();
                        return Integer.parseInt(status);
                    } catch (Exception e) {
                    }
                    return null;
                }

                @SuppressLint("NewApi")
                @Override
                protected void onPostExecute(Integer integer) {
                    super.onPostExecute(integer);
                    if (integer == null) {
                        stopSelf();
                        return;
                    }
                    if (integer != 0) {
                        NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification.Builder builder = new Notification.Builder(PlayerService.this);
                        builder.setContentTitle("DoM!niC Livestream");
                        builder.setContentText("ist jetzt online!");
                        builder.setContentIntent(PendingIntent.getActivity(PlayerService.this, 7613, new Intent(PlayerService.this, PlayerActivity.class).setData(Uri.parse("https://rtmp.dom1nic.eu:8081/hd/stream/index.m3u8")), 0));
                        builder.setSmallIcon(R.drawable.ic_play_arrow_white_48dp);
                        builder.setAutoCancel(true);
                        builder.setVibrate(new long[]{100, 100, 600, 100});
                        builder.setLights(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), 400, 600);
                        mgr.notify(6725, Build.VERSION.SDK_INT >= 16 ? builder.build() :
                                builder.getNotification());
                    }
                    stopSelf();
                }
            }.execute();
        else Log.i("CheckStream", "Aborted!");
        return START_NOT_STICKY;
    }
}
