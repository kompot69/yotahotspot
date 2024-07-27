package ru.kompot69.yotahotspot.ui;

import static ru.kompot69.yotahotspot.Utils.requestGet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import ru.kompot69.yotahotspot.databinding.FragmentWifiSettingsBinding;

public class WiFiSettingsFragment extends Fragment {
    private Timer timer;
    private Activity activity;
    private Context context;
    private FragmentWifiSettingsBinding binding;
    
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
        binding = FragmentWifiSettingsBinding.inflate(inflater,container,false);
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
                    String data = "SSID1%2CAuthMode%2Cmac_address%2CHideSSID%2Cwifi_auto_off%2Cwifi_band%2CCountryCode%2Cwifi_coverage%2CsysIdleTimeToSleep";
                    JSONObject responseObj = requestGet("http://status.yota.ru/goform/goform_get_cmd_process?multi_data=1&isTest=false&cmd="+data);

                    String SSID1 = responseObj.getString("SSID1");
                    String AuthMode = responseObj.getString("AuthMode");

                    SpannableStringBuilder mac_address = new SpannableStringBuilder("MAC: "+ responseObj.getString("mac_address"));
                    mac_address.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder HideSSID = new SpannableStringBuilder("Скрытая сеть: "+(responseObj.getString("HideSSID").contains("0")? "Не скрытая" : "Скрытая"));
                    HideSSID.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder wifi_auto_off = new SpannableStringBuilder("Режим энергосбережения: "+(responseObj.getString("wifi_auto_off").contains("on")? "Включен" : "Выключен"));
                    wifi_auto_off.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder wifi_band = new SpannableStringBuilder("WiFi band: "+responseObj.getString("wifi_band"));
                    wifi_band.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder CountryCode = new SpannableStringBuilder("Код страны: "+responseObj.getString("CountryCode"));
                    CountryCode.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder wifi_coverage = new SpannableStringBuilder("Мощность раздачи: "+(responseObj.getString("wifi_coverage").contains("medium")? "Средняя" : responseObj.getString("wifi_coverage")));
                    wifi_coverage.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder sysIdleTimeToSleep = new SpannableStringBuilder("Переход в спящий режим: "+responseObj.getString("sysIdleTimeToSleep")+" мин");
                    sysIdleTimeToSleep.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    activity.runOnUiThread(() -> {
                        binding.wifiName.setText((SSID1));
                        binding.wifiPasstype.setText((AuthMode));
                        binding.macAddress.setText((mac_address));
                        binding.HideSSID.setText((HideSSID));
                        binding.wifiAutoOff.setText((wifi_auto_off));
                        binding.wifiBand.setText((wifi_band));
                        binding.CountryCode.setText((CountryCode));
                        binding.wifiCoverage.setText((wifi_coverage));
                        binding.sysIdleTimeToSleep.setText((sysIdleTimeToSleep));
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
        MenuItem qr = menu.add("Title");
        qr.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        qr.setIcon(R.drawable.qr);
        //MenuItem settings = menu.add("Title2");
        //settings.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //settings.setIcon(R.drawable.settings);

        qr.setOnMenuItemClickListener(menuItem -> {
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
        /*
        settings.setOnMenuItemClickListener(menuItem -> {
            //sartActivity(new Intent(this, wifiSettings.class));
            activity.runOnUiThread(() -> Toast.makeText(context, "Появится позже...", Toast.LENGTH_SHORT).show());
            return true;
        });
        */
        super.onCreateOptionsMenu(menu, inflater);
    }
}