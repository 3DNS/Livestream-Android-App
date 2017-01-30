package dom1nic.livestream;

import android.content.Intent;
import android.net.Uri;
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

public class MainActivity extends AppCompatActivity {
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
                String url = "http://rtmp.dom1nic.eu:8080/" + (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("quali", true) ? "hls" : "sd") + "/stream/index.m3u8";
                Log.i("Streaming", url);
                Intent Intent = new Intent(MainActivity.this, PlayerActivity.class)
                        .setData(Uri.parse(url));
                startActivity(Intent);
            }
        });
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
        return super.onOptionsItemSelected(item);
    }
}
