package com.github.kaklakariada.andect;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.github.kaklakariada.fritzbox.http.HttpTemplate;
import com.github.kaklakariada.fritzbox.model.homeautomation.Device;
import com.github.kaklakariada.fritzbox.model.homeautomation.DeviceList;

public class FritzBoxService {
    private final Context context;
    private HomeAutomation homeAutomation;

    public FritzBoxService(Context context) {
        this.context = context;
    }

    public String getBaseUrl() {
        return getPreferences().getString("url", "http://fritz.box");
    }

    public String getUsername() {
        return getPreferences().getString("username", "");
    }

    public String getPassword() {
        return getPreferences().getString("password", "");
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences("credentials", Context.MODE_PRIVATE);
    }

    public void storePreferences(String url, String username, String password) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString("url", url);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    public HomeAutomation login(String url, String username, String password) {
        homeAutomation = HomeAutomation.connect(url, username, password);
        return homeAutomation;
    }

    public DeviceList getDeviceList() {
        return getHomeAutomation().getDeviceListInfos();
    }

    @NonNull
    private HomeAutomation getHomeAutomation() {
        return homeAutomation;
    }

    public Device getDevice(String identifer) {
        return getDeviceList().getDeviceByIdentifier(identifer);
    }

    public void switchState(Device device, boolean on) {
        getHomeAutomation().switchPowerState(device.getIdentifier(), on);
    }

    public void toggle(Device device) {
        getHomeAutomation().togglePowerState(device.getIdentifier());
    }
}
