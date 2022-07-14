package co.cablebox.tv.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.cablebox.tv.R;
import co.cablebox.tv.utils.config.wifi.wificonnector.WifiConnector;

public class WifiListRvAdapter extends RecyclerView.Adapter<WifiListRvAdapter.WifiItem> {

    private List<ScanResult> scanResultList = new ArrayList<>();
    private WifiConnector wifiConnector;
    private WifiItemListener wifiItemListener;

    public WifiListRvAdapter(WifiConnector wifiConnector, WifiItemListener wifiItemListener) {
        this.wifiConnector = wifiConnector;
        this.wifiItemListener = wifiItemListener;
    }

    public void setScanResultList(List<ScanResult> scanResultList) {
        this.scanResultList = scanResultList;
        notifyDataSetChanged();
    }

    public List<ScanResult> getListWifi(){
        return scanResultList;
    }

    @Override
    public WifiItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WifiItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.accesspoint_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final WifiItem holder, final int position) {
        holder.fill(scanResultList.get(position), wifiConnector.getCurrentWifiSSID());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiConnector.isConnectedToBSSID(scanResultList.get(position).BSSID)) {
                    Toast.makeText(holder.itemView.getContext(), "Estas conectado!", Toast.LENGTH_SHORT).show();
                } else {
                    wifiItemListener.onWifiItemClicked(scanResultList.get(position));
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                wifiItemListener.onWifiItemLongClick(scanResultList.get(position));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.scanResultList.size();
    }

    @Override
    public void onViewRecycled(WifiItem holder) {
        super.onViewRecycled(holder);
    }

    static class WifiItem extends RecyclerView.ViewHolder {

        private TextView wifiName;
        private TextView wifiIntensity;

        public WifiItem(View itemView) {
            super(itemView);
            wifiName = itemView.findViewById(R.id.apItem_name);
            wifiIntensity = itemView.findViewById(R.id.apItem_intensity);
        }

        @SuppressLint("SetTextI18n")
        public void fill(ScanResult scanResult, String currentSsid) {
            if (scanResult.SSID.equals(currentSsid)) {
                wifiName.setTextColor(Color.GREEN);
            }
            wifiName.setText(scanResult.SSID);
            wifiIntensity.setText(WifiManager.calculateSignalLevel(scanResult.level, 100) + "%");
        }

    }

    public interface WifiItemListener {
        void onWifiItemClicked(ScanResult scanResult);

        void onWifiItemLongClick(ScanResult scanResult);
    }

}
