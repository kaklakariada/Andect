package com.github.kaklakariada.andect;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.github.kaklakariada.fritzbox.FritzBoxSession;
import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.github.kaklakariada.fritzbox.http.HttpTemplate;
import com.github.kaklakariada.fritzbox.model.homeautomation.Device;
import com.github.kaklakariada.fritzbox.model.homeautomation.DeviceList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceListActivityOld extends ListActivity {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceListActivityOld.class);

    private FritzBoxSession fritzBoxSession = null;
    private ArrayAdapter<Device> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list_old);
        listAdapter = new ArrayAdapter<Device>(this, android.R.layout.simple_list_item_1);
        setListAdapter(listAdapter);
        if(fritzBoxSession == null) {
            Intent intent = new Intent(this, ConnectActivity.class);
            LOG.info("Starting login...");
            startActivityForResult(intent, 1);
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String url = data.getStringExtra("url");
        String sid = data.getStringExtra("sid");
        LOG.info("Got url {} and sid {}", url, sid);
        this.fritzBoxSession = new FritzBoxSession(new HttpTemplate(url), sid);
        updateDeviceList();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Device selectedDevice = (Device) getListView().getItemAtPosition(position);
        LOG.info("Device selected: {}", selectedDevice);
    }

    private void updateDeviceList() {
        LOG.info("Update device list");
        listAdapter.clear();
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT,
                AbsListView.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

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
            LOG.info("Display device list {}", deviceList);
            listAdapter.addAll(deviceList.getDevices());
        }

        @Override
        protected void onCancelled() {
            LOG.info("Task cancelled");
        }
    }
}
