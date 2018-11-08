package dns.livestream;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlayerReceiver extends BroadcastReceiver {
    public static final String live = "dns.livestream.LIVE";
    public static final int MINUTEN = 2;

    public PlayerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(live)) {
            context.startService(new Intent(context, PlayerService.class));
        } else {
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000, 1000 * 60 * MINUTEN, PendingIntent.getBroadcast(context, 1337, new Intent(live), 0));
        }
    }
}
