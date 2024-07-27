package ru.kompot69.yotahotspot.ui;

import static ru.kompot69.yotahotspot.Utils.requestGet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;

import ru.kompot69.yotahotspot.R;
import ru.kompot69.yotahotspot.Utils;
import ru.kompot69.yotahotspot.databinding.FragmentNetworkInformationBinding;

public class NetworkInformationFragment extends Fragment {
    private Timer timer;
    private Activity activity;
    private Context context;
    private FragmentNetworkInformationBinding binding;
    
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
        binding = FragmentNetworkInformationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {

                try {
                    String data = "network_provider%2Cnetwork_type%2Cnetwork_status%2Crssi%2Crealtime_rx_bytes%2Crealtime_tx_bytes%2Cmonthly_rx_bytes%2Cmonthly_tx_bytes%2Cwan_ipaddr%2Csim_imsi%2Cuiccid%2Cnet_select%2Cpinnumber%2Cpuknumber%2Cpin_status%2Csignal_num";
                    JSONObject responseObj = requestGet("http://status.yota.ru/goform/goform_get_cmd_process?multi_data=1&isTest=false&cmd="+data);

                    String network_provider = responseObj.getString("network_provider") + " " + responseObj.getString("network_type");
                    String network_status = responseObj.getString("network_status");
                    String rssi = "RSSI: "+responseObj.getString("rssi")+" dBm";

                    SpannableStringBuilder realtime_rx_bytes = new SpannableStringBuilder("Скачано(после вкл.): "+ Utils.sizeofFmt(Long.valueOf(responseObj.getString("realtime_rx_bytes"))));
                    realtime_rx_bytes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder realtime_tx_bytes = new SpannableStringBuilder("Передано(после вкл.): "+Utils.sizeofFmt(Long.valueOf(responseObj.getString("realtime_tx_bytes"))));
                    realtime_tx_bytes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder monthly_rx_bytes = new SpannableStringBuilder("Скачано/месяц: "+Utils.sizeofFmt(Long.valueOf(responseObj.getString("monthly_rx_bytes"))));
                    monthly_rx_bytes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder monthly_tx_bytes = new SpannableStringBuilder("Передано/месяц: "+Utils.sizeofFmt(Long.valueOf(responseObj.getString("monthly_tx_bytes"))));
                    monthly_tx_bytes.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder wan_ipaddr = new SpannableStringBuilder("Внешний IP: "+responseObj.getString("wan_ipaddr"));
                    wan_ipaddr.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder sim_imsi = new SpannableStringBuilder("IMSI: "+responseObj.getString("sim_imsi"));
                    sim_imsi.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder uiccid = new SpannableStringBuilder("ICCID (SIM SN): "+responseObj.getString("uiccid"));
                    uiccid.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder net_select = new SpannableStringBuilder("Тип сети: "+responseObj.getString("net_select"));
                    net_select.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder pinnumber = new SpannableStringBuilder("Попыток PIN: "+responseObj.getString("pinnumber"));
                    pinnumber.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder puknumber = new SpannableStringBuilder("Попыток PUK: "+responseObj.getString("puknumber"));
                    puknumber.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder pin_status = new SpannableStringBuilder("Запрос PIN: "+(responseObj.getString("pin_status").contains("0")? "Выключен" : "Включен"));
                    pin_status.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    int signal_num = Integer.parseInt(responseObj.getString("signal_num"));
                    Drawable newSignalImg;


                    if (responseObj.getString("network_status").contains("Off")) {
                        newSignalImg = activity.getResources().getDrawable(R.drawable.signal_no);
                    } else {
                        if (signal_num >= 1 && signal_num <= 5 ) {
                            int id = context.getResources().getIdentifier("signal_" + String.valueOf(signal_num-1), "drawable", context.getPackageName());
                            newSignalImg = activity.getResources().getDrawable(id);
                        } else {
                            newSignalImg = activity.getResources().getDrawable(R.drawable.signal_no);
                        }
                    }
                    Drawable finalNewSignalImg = newSignalImg;

                    activity.runOnUiThread(() -> {
                        binding.imgSim.setImageDrawable(finalNewSignalImg);
                        binding.operator.setText((network_provider));
                        binding.dataStatus.setText((network_status));
                        binding.rssi.setText((rssi));
                        binding.realtimeRxBytes.setText((realtime_rx_bytes));
                        binding.realtimeTxBytes.setText((realtime_tx_bytes));
                        binding.monthlyRxBytes.setText((monthly_rx_bytes));
                        binding.monthlyTxBytes.setText((monthly_tx_bytes));
                        binding.wanIpaddr.setText((wan_ipaddr));
                        binding.simImsi.setText((sim_imsi));
                        binding.uiccid.setText((uiccid));
                        binding.netSelect.setText((net_select));
                        binding.pinnumber.setText((pinnumber));
                        binding.puknumber.setText((puknumber));
                        binding.pinStatus.setText((pin_status));
                    });
                } catch (JSONException | IOException | NullPointerException e) {
                    if (e instanceof ConnectException) {
                        activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Не удалось установить соединение с роутером, пробуем ещё раз...", Snackbar.LENGTH_SHORT).show());
                    }else {
                        activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Произошла какая-то ошибка...", Snackbar.LENGTH_SHORT).show());
                    }
                }
            }
        }, 0, 4000);
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
        MenuItem item = menu.add("Title");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        item.setIcon(Utils.makeWhiteIcon(R.drawable.signal_off, context));
        item.setOnMenuItemClickListener(menuItem -> {
            MaterialAlertDialogBuilder adb = new MaterialAlertDialogBuilder(context)
                    .setTitle("Вкл./Откл. передачу данных?")
                    .setNegativeButton("Отключить", (di, i) -> {
                        new Thread(() -> {
                            try {
                                JSONObject IPadr = requestGet("http://status.yota.ru/goform/goform_get_cmd_process?multi_data=1&isTest=false&cmd=lan_ipaddr");
                                JSONObject result = requestGet("http://"+IPadr.getString("lan_ipaddr")+"/goform/goform_set_cmd_process?isTest=false&notCallback=true&goformId=DISCONNECT_NETWORK");
                                String resultStr = result.getString("result");
                                if (resultStr.contains("success")){
                                    activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Передача данных отключена.", Snackbar.LENGTH_SHORT).show());
                                }else {
                                    activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), resultStr, Snackbar.LENGTH_SHORT).show());
                                }
                            } catch (JSONException | IOException e) {
                                System.out.println(e.toString());
                                activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Произошла какая-то ошибка...", Snackbar.LENGTH_SHORT).show());
                            }
                        }).start();

                    })
                    .setPositiveButton("Включить", (di, i) -> {
                        new Thread(() -> {
                            try {
                                JSONObject IPadr = requestGet("http://status.yota.ru/goform/goform_get_cmd_process?multi_data=1&isTest=false&cmd=lan_ipaddr");
                                requestGet("http://"+IPadr.getString("lan_ipaddr")+"/goform/goform_set_cmd_process?isTest=false&notCallback=true&goformId=CONNECT_NETWORK");
                                activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Передача данных включена.", Snackbar.LENGTH_SHORT).show());
                            } catch (JSONException | IOException e) {
                                activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Произошла какая-то ошибка...", Snackbar.LENGTH_SHORT).show());
                            }
                        }).start();
                    })
                    .setCancelable(true);
            adb.getBackground().setTint(Color.parseColor("#FFFFFF"));
            adb.show();
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}