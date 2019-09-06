package com.example.observesnetworkconnection;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.novoda.merlin.Connectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;

public class MainActivity extends AppCompatActivity {

    Merlin merlin;
    MerlinsBeard merlinsBeard;
    int connectionCheckingInterval = 5000;
    // check manifest for permissions
    Handler h;
    TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        merlin = new Merlin.Builder().withConnectableCallbacks().build(getApplicationContext());
        merlinsBeard = new MerlinsBeard.Builder().build(getApplicationContext());
        statusText = findViewById(R.id.statusText);

        h = new Handler();

        h.postDelayed(checkConnection, connectionCheckingInterval);

        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                // Toast.makeText(getApplicationContext(), "Attached!", Toast.LENGTH_LONG).show();
                statusText.setText("Attached!");
            }
        });
    }

    private Runnable checkConnection = new Runnable() {
        @Override
        public void run() {
            checkConnectivity();
            h.postDelayed(checkConnection, connectionCheckingInterval);
        }
    };

    private void checkConnectivity() {
        if (merlinsBeard.isConnected()) {
            Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
            statusText.setText("Connected!");
        }
        if (!merlinsBeard.isConnected()) {
            Toast.makeText(getApplicationContext(), "Disconnected!", Toast.LENGTH_SHORT).show();
            statusText.setText("Disconnected!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();
    }

    @Override
    protected void onPause() {
        super.onPause();
        merlin.unbind();
        // To prevent continuously running Runnable
        h.removeCallbacks(checkConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(checkConnection);
    }
}
