package dom1nic.livestream;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog streamLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button button = (Button) findViewById(R.id.website);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriUrl = Uri.parse("https://dom1nic.eu");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (streamLoader != null) streamLoader.dismiss();
                streamLoader = ProgressDialog.show(MainActivity.this, "Laden", "Laden", true);
                new StreamTester().execute();
            }
        });

        startService(new Intent(this, PlayerService.class));
    }

    public class StreamTester extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            String url = "https://rtmp.3dns.eu/dom1nic/" + (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("quali", true) ? "hd" : "sd") + "/stream/index.m3u8";
            try {
                HttpURLConnection connection = (HttpURLConnection) new java.net.URL(url).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "https://rtmp.3dns.eu/dom1nic/hd/stream/index.m3u8", line;
                while ((line = reader.readLine()) != null)
                    content += line;
                if (content.toLowerCase().contains("403 - access denied"))
                    throw new Exception();
                return Boolean.TRUE;
            } catch (Exception e) {
                return Boolean.FALSE;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            streamLoader.dismiss();
            if (success) {
                Intent Intent = new Intent(MainActivity.this, PlayerFullAcivity.class);
                startActivity(Intent);
            } else {
                Intent Intent = new Intent(MainActivity.this, IdleFullScreen.class);
                startActivity(Intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.noti).setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("noti", true));
        menu.findItem(R.id.quali).setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("quali", true));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_website) {
            Uri uriUrl = Uri.parse("https://dom1nic.eu");
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
            return true;
        }
        if (id == R.id.close) {
            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }
        if (id == R.id.noti) {
            item.setChecked(!item.isChecked());
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("noti", item.isChecked()).apply();
        }
        if (id == R.id.quali) {
            item.setChecked(!item.isChecked());
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("quali", item.isChecked()).apply();
        }
       // if (id == R.id.last) {
       //     String url = "https://rtmp.dom1nic.eu:8080/recent.json";
       //     Log.i("Streaming", url);
//                    .setData(Uri.parse(url));
        //          startActivity(Intent);
        //}
        //if (id == R.id.last_text) {
        //   String url = "https://rtmp.dom1nic.eu:8080/recent.json";
        //   Log.i("Streaming", url);
        //   Intent Intent = new Intent(MainActivity.this, LastActivity.class)
        //           .setData(Uri.parse(url));
        //   startActivity(Intent);
        //}
        return super.onOptionsItemSelected(item);
    }
}
