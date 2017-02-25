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
import android.util.Log;
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
                Uri uriUrl = Uri.parse("http://dom1nic.eu");
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
            String url = "http://rtmp.dom1nic.eu:8080/" + (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("quali", true) ? "hls" : "sd") + "/stream/index.m3u8";
            try {
                HttpURLConnection connection = (HttpURLConnection) new java.net.URL(url).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = "", line;
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
            String url = "http://rtmp.dom1nic.eu:8080/" + (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("quali", true) ? "hls" : "sd") + "/stream/index.m3u8";
            if (success) {
                Log.i("MODE", "STREAMING " + (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("quali", true) ? "HD" : "SD"));
                Intent Intent = new Intent(MainActivity.this, PlayerActivity.class)
                        .setData(Uri.parse(url)).putExtra("url", url);
                startActivity(Intent);
            } else {
                url = "http://rtmp.dom1nic.eu:8080/break.mp4";
                Log.i("MODE", "PLAYBACK");
                Intent Intent = new Intent(MainActivity.this, PlayerActivity.class)
                        .setData(Uri.parse(url)).putExtra("url", url);
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
            Uri uriUrl = Uri.parse("http://dom1nic.eu");
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
        if (id == R.id.last) {
            String url = "http://dom1nic.eu/videos/archiv/BF-Bad-Company2-2.mp4";
            Log.i("Streaming", url);
            Intent Intent = new Intent(MainActivity.this, LastActivity.class)
                    .setData(Uri.parse(url));
            startActivity(Intent);
        }
        if (id == R.id.last_text) {
            String url = "http://dom1nic.eu/videos/archiv/BF-Bad-Company2-2.mp4";
            Log.i("Streaming", url);
            Intent Intent = new Intent(MainActivity.this, LastActivity.class)
                    .setData(Uri.parse(url));
            startActivity(Intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
