package com.github.kaklakariada.andect;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.kaklakariada.fritzbox.FritzBoxSession;
import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.github.kaklakariada.fritzbox.http.HttpTemplate;
import com.github.kaklakariada.fritzbox.model.homeautomation.DeviceList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DeviceListActivity extends AppCompatActivity {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceListActivity.class);

    private FritzBoxSession fritzBoxSession = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Intent intent = new Intent(this, ConnectActivity.class);
        if(fritzBoxSession == null) {
            LOG.info("Starting login...");
            startActivityForResult(intent, 1);
            return;
        }
        updateDeviceList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String url = data.getStringExtra("url");
        String sid = data.getStringExtra("sid");
        LOG.info("Got url {} and sid {}", url, sid);
        this.fritzBoxSession = new FritzBoxSession(new HttpTemplate(url), sid);
        updateDeviceList();
    }

    private void updateDeviceList() {
        new UpdateDeviceListTask().execute();
    }


    private class UpdateDeviceListTask extends AsyncTask<Void, Void, DeviceList> {

        @Override
        protected DeviceList doInBackground(Void... params) {
            DeviceList deviceList = new HomeAutomation(fritzBoxSession).getDeviceListInfos();
            LOG.info("Found {} devices, api version: {}", deviceList.getDevices().size(), deviceList.getApiVersion());
            return deviceList;
        }


        @Override
        protected void onPostExecute(final DeviceList deviceList) {
            LOG.debug("Display device list {}", deviceList);
        }

        @Override
        protected void onCancelled() {
            LOG.debug("Task cancelled");
        }
    }
}
