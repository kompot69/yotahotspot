package ru.kompot69.yotahotspot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {
    private ArrayList<StationData> items;

    public StationAdapter(ArrayList<StationData> items) {this.items = items;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.macAddr.setText(items.get(position).getMacAddr());
        holder.connectTime.setText(items.get(position).getConnectTime());
        holder.hostname.setText(items.get(position).getHostname());
        holder.ipAddr.setText(items.get(position).getIpAddr());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView macAddr;
        private TextView connectTime;
        public TextView hostname;
        private TextView ipAddr;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            macAddr = itemView.findViewById(R.id.macAddr);
            connectTime = itemView.findViewById(R.id.connectTime);
            hostname = itemView.findViewById(R.id.hostname);
            ipAddr = itemView.findViewById(R.id.ipAddr);
        }
    }
}
