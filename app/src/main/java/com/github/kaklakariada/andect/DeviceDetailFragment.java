package com.github.kaklakariada.andect;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.kaklakariada.fritzbox.model.homeautomation.Device;
import com.github.kaklakariada.fritzbox.model.homeautomation.DeviceList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fragment representing a single Device detail screen.
 * This fragment is either contained in a {@link DeviceListActivity}
 * in two-pane mode (on tablets) or a {@link DeviceDetailActivity}
 * on handsets.
 */
public class DeviceDetailFragment extends Fragment {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceDetailFragment.class);

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Device mItem;
    private FritzBoxService fritzBoxService;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeviceDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fritzBoxService = new FritzBoxService(this.getContext());
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String deviceIdentifier = getArguments().getString(ARG_ITEM_ID, null);
            new GetDeviceTask().execute(deviceIdentifier);
        }
    }

    private void updateDevice(Device device) {
        LOG.info("Update UI for device {}", device.getIdentifier());
        mItem = device;

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mItem != null ? mItem.getName() : "n.a.");
        }
        // ((TextView) getView().findViewById(R.id.device_detail)).setText(mItem.toString());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.device_detail, container, false);

        if (mItem != null) {
            //((TextView) rootView.findViewById(R.id.device_detail)).setText(mItem.toString());
        }

        ((Button) rootView.findViewById(R.id.switch_on)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SwitchPowerState(mItem, true).execute();
            }
        });
        ((Button) rootView.findViewById(R.id.switch_off)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SwitchPowerState(mItem, false).execute();
            }
        });
        ((Button) rootView.findViewById(R.id.toggle_on_off)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TogglePowerState(mItem).execute();
            }
        });
        return rootView;
    }

    public class SwitchPowerState extends AsyncTask<Void, Void, Void> {

        private final Device device;
        private final boolean powerState;

        public SwitchPowerState(Device device, boolean powerState) {
            this.device = device;
            this.powerState = powerState;
        }

        @Override
        protected Void doInBackground(Void... params) {
            fritzBoxService.switchState(device, powerState);
            return null;
        }
    }

    public class TogglePowerState extends AsyncTask<Void, Void, Void> {

        private final Device device;

        public TogglePowerState(Device device) {
            this.device = device;
        }

        @Override
        protected Void doInBackground(Void... params) {
            fritzBoxService.toggle(device);
            return null;
        }
    }

    public class GetDeviceTask extends AsyncTask<String, Void, Device> {

        @Override
        protected Device doInBackground(String... params) {
            String deviceIdentifier = params[0];
            LOG.info("Fetching device {}", deviceIdentifier);
            return fritzBoxService.getDeviceList().getDeviceByIdentifier(deviceIdentifier);
        }

        @Override
        protected void onPostExecute(final Device device) {
            updateDevice(device);
        }

        @Override
        protected void onCancelled() {
            LOG.info("Cancelled");
        }
    }
}
