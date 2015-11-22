package com.github.kaklakariada.andect;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.github.kaklakariada.fritzbox.FritzBoxSession;
import com.github.kaklakariada.fritzbox.HomeAutomation;
import com.github.kaklakariada.fritzbox.http.HttpTemplate;
import com.github.kaklakariada.fritzbox.model.homeautomation.Device;
import com.github.kaklakariada.fritzbox.model.homeautomation.DeviceList;

public class FritzBoxService {
    private final Context context;

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

    private String getSid() {
        return getPreferences().getString("sid", "");
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences("credentials", Context.MODE_PRIVATE);
    }

    public void storePreferences(String url, String username, String password, String sid) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString("url", url);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("sid", sid);
        editor.commit();
    }

    public String login(String url, String username, String password) {
        FritzBoxSession fritzBoxSession = new FritzBoxSession(new HttpTemplate(url));
        fritzBoxSession.login(username, password);
        return fritzBoxSession.getSid();
    }

    public DeviceList getDeviceList() {
        return getHomeAutomation().getDeviceListInfos();
    }

    @NonNull
    private HomeAutomation getHomeAutomation() {
        return new HomeAutomation(getAuthenticatedFritzboxSession());
    }

    @NonNull
    private FritzBoxSession getAuthenticatedFritzboxSession() {
        return new FritzBoxSession(new HttpTemplate(getBaseUrl()), getSid());
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
