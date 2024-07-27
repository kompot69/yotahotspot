package ru.kompot69.yotahotspot.ui;

import static ru.kompot69.yotahotspot.Utils.requestGet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;

import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;

import ru.kompot69.yotahotspot.R;
import ru.kompot69.yotahotspot.Utils;
import ru.kompot69.yotahotspot.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {
    private Timer timer;
    private Activity activity;
    private Context context;
    private FragmentMainBinding binding;
    
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
        binding = FragmentMainBinding.inflate(inflater,container,false);
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
                    String data = "battery_vol_percent%2Cbattery_charging%2Csignal_num%2Cnetwork_status%2Cmodel_name%2Cbattery_vol_percent%2CSSID1%2CAuthMode%2Crealtime_tx_thrpt%2Crealtime_rx_thrpt%2Cnetwork_provider%2Cnetwork_type%2Cnetwork_status";
                    JSONObject responseObj = requestGet("http://status.yota.ru/goform/goform_get_cmd_process?multi_data=1&isTest=false&cmd="+data);

                    int batteryPercent = Integer.parseInt(responseObj.getString("battery_vol_percent"));
                    Drawable newBatteryImg;
                    if (responseObj.getString("battery_charging").contains("1")) {
                        newBatteryImg = activity.getResources().getDrawable(R.drawable.battery_charge);
                    } else {
                        int id = context.getResources().getIdentifier("battery_"
                                        + String.valueOf(
                                        batteryPercent >= 85 ? 4 :
                                                batteryPercent >= 65 ? 3 :
                                                        batteryPercent >= 45 ? 2 :
                                                                batteryPercent >= 25 ? 1 : 0),
                                "drawable", context.getPackageName());
                        newBatteryImg = activity.getResources().getDrawable(id);
                    }
                    Drawable finalNewBatteryImg = newBatteryImg;



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



                        String model_name = responseObj.getString("model_name");
                    String battery_vol_percent = responseObj.getString("battery_vol_percent")+"%";
                    String SSID1 = responseObj.getString("SSID1");
                    String AuthMode = responseObj.getString("AuthMode");
                    String realtime_tx_thrpt = "↑ " + Utils.sizeofFmt(Long.valueOf(responseObj.getString("realtime_tx_thrpt")));
                    String realtime_rx_thrpt = "↓ " + Utils.sizeofFmt(Long.valueOf(responseObj.getString("realtime_rx_thrpt")));
                    String network_provider = responseObj.getString("network_provider") + " " + responseObj.getString("network_type");
                    String network_status = responseObj.getString("network_status");

                    activity.runOnUiThread(() -> {
                        binding.batteryImg.setImageDrawable(finalNewBatteryImg);
                        binding.imgSim.setImageDrawable(finalNewSignalImg);
                        binding.model.setText((model_name));
                        binding.batteryPercent.setText((battery_vol_percent));
                        binding.wifiName.setText((SSID1));
                        binding.wifiPasstype.setText((AuthMode));
                        binding.dataUpl.setText((realtime_tx_thrpt));
                        binding.dataDwl.setText((realtime_rx_thrpt));
                        binding.operator.setText((network_provider));
                        binding.dataStatus.setText((network_status));
                    });
                } catch (JSONException | IOException | NullPointerException e) {
                    //System.out.println(e.toString());
                    //throw new RuntimeException(e);
                    if (e instanceof ConnectException) {
                        activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Не удалось установить соединение с роутером, пробуем ещё раз...", Snackbar.LENGTH_SHORT).show());
                        //activity.runOnUiThread(() -> Toast.makeText(context, "Не удалось установить соединение с роутером, пробуем ещё раз...", Toast.LENGTH_SHORT).show());
                        //activity.runOnUiThread(() -> Snackbar.make(binding.getRoot(), "Не удалось установить соединение с роутером, пробуем ещё раз...", Snackbar.LENGTH_SHORT).show());
                    }else {

                        activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Произошла какая-то ошибка...", Snackbar.LENGTH_SHORT).show());
                    }
                }
            }
        }, 0, 1500);
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