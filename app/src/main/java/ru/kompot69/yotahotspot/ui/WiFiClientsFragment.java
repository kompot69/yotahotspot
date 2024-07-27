package ru.kompot69.yotahotspot.ui;

import static ru.kompot69.yotahotspot.Utils.requestGet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ru.kompot69.yotahotspot.R;
import ru.kompot69.yotahotspot.Utils;
import ru.kompot69.yotahotspot.databinding.FragmentWifiClientsBinding;
import ru.kompot69.yotahotspot.StationAdapter;
import ru.kompot69.yotahotspot.StationData;

public class WiFiClientsFragment extends Fragment {
    private Timer timer;
    private Activity activity;
    private Context context;
    private FragmentWifiClientsBinding binding;
    final ArrayList<StationData> items = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.activity = getActivity();
        this.context = getContext();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWifiClientsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerView.setHasFixedSize(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                try {
                    String data = "realtime_tx_thrpt%2Crealtime_rx_thrpt";
                    JSONObject responseObj = requestGet("http://status.yota.ru/goform/goform_get_cmd_process?multi_data=1&isTest=false&cmd="+data);
                    String realtime_tx_thrpt = "↑ " + Utils.sizeofFmt(Long.parseLong(responseObj.getString("realtime_tx_thrpt")));
                    String realtime_rx_thrpt = "↓ " + Utils.sizeofFmt(Long.parseLong(responseObj.getString("realtime_rx_thrpt")));

                    JSONObject clientsObj = requestGet("http://status.yota.ru/goform/goform_get_cmd_process?isTest=false&cmd=station_list");
                    JSONArray clientsArray = new JSONArray(clientsObj.getString("station_list"));
                    items.clear();
                    for (int i = 0; i < clientsArray.length(); i++) {
                        JSONObject jsonObject = clientsArray.getJSONObject(i);
                        items.add(
                                new StationData(
                                        jsonObject.getString("mac_addr"),
                                        jsonObject.getString("connect_time"),
                                        jsonObject.getString("hostname"),
                                        jsonObject.getString("ip_addr")));
                    }

                    activity.runOnUiThread(() -> {
                        binding.dataUpl.setText((realtime_tx_thrpt));
                        binding.dataDwl.setText((realtime_rx_thrpt));
                        binding.recyclerView.setAdapter(new StationAdapter(items));
                    });

                } catch (IOException | JSONException | NullPointerException e) {
                    if (e instanceof ConnectException) {
                        activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Что-то пошло не так...", Snackbar.LENGTH_SHORT).show());
                    } else {
                        activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Не удалось получить некоторые данные.", Snackbar.LENGTH_SHORT).show());
                    }
                }
            }
        }, 0, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        if (timer != null) {
            timer.cancel();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add("QR Code");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        item.setIcon(Utils.makeWhiteIcon(R.drawable.qr, context));
        item.setOnMenuItemClickListener(menuItem -> {
            View view = getLayoutInflater().inflate(R.layout.qr_code_dialog, null);
            ImageView imageView = view.findViewById(R.id.image_view);
            String imageUrl = "http://status.yota.ru/img/qrcode_ssid_wifikey.png";
            Glide.with(context)
                    .load(imageUrl)
                    .into(imageView);
            MaterialAlertDialogBuilder adb = new MaterialAlertDialogBuilder(context)
                    .setTitle("Поделиться сетью")
                    .setView(view)
                    .setCancelable(true);
            adb.getBackground().setTint(Color.parseColor("#FFFFFF"));
            adb.show();
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}