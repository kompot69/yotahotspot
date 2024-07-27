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
import android.widget.TextView;
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
import ru.kompot69.yotahotspot.databinding.FragmentInformationBinding;

public class InformationFragment extends Fragment {
    private Timer timer;
    private Activity activity;
    private Context context;
    private FragmentInformationBinding binding;
    
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
        binding = FragmentInformationBinding.inflate(inflater,container,false);
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
                    String data = "battery_vol_percent%2Cbattery_charging%2Cmodel_name%2Cimei%2Clan_ipaddr%2Cneed_login%2Cwa_version";
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

                    String model_name = responseObj.getString("model_name");
                    String battery_vol_percent = responseObj.getString("battery_vol_percent")+"%";

                    SpannableStringBuilder imei = new SpannableStringBuilder("IMEI: "+ responseObj.getString("imei"));
                    imei.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder lan_ipaddr = new SpannableStringBuilder("Адрес admin-панели: "+responseObj.getString("lan_ipaddr"));
                    lan_ipaddr.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder need_login = new SpannableStringBuilder("Пароль admin-панели: "+(responseObj.getString("need_login").contains("off")? "Выключен" : "Включен"));
                    need_login.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder wa_version = new SpannableStringBuilder("Версия ПО: "+responseObj.getString("wa_version"));
                    wa_version.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    activity.runOnUiThread(() -> {
                        binding.batteryImg.setImageDrawable(finalNewBatteryImg);
                        binding.model.setText((model_name));
                        binding.batteryPercent.setText((battery_vol_percent));
                        binding.imei.setText((imei));
                        binding.lanIpaddr.setText((lan_ipaddr));
                        binding.needLogin.setText((need_login));
                        binding.waVersion.setText((wa_version));
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
        }, 0, 5000);
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
        
        item.setIcon(Utils.makeWhiteIcon(R.drawable.power, context));
        item.setOnMenuItemClickListener(menuItem -> {

            MaterialAlertDialogBuilder adb = new MaterialAlertDialogBuilder(context)
                    .setTitle("Перезагрузить роутер?")
                    .setPositiveButton("Отмена", (di, i) -> {})
                    .setNegativeButton("Перезагрузить", (di, i) -> {
                        new Thread(() -> {
                            try {
                                JSONObject responseObj = requestGet("http://status.yota.ru/goform/goform_get_cmd_process?multi_data=1&isTest=false&cmd=lan_ipaddr");
                                requestGet("http://"+responseObj.getString("lan_ipaddr")+"/goform/goform_set_cmd_process?isTest=false&goformId=REBOOT_DEVICE");
                                activity.runOnUiThread(() -> Snackbar.make(activity.findViewById(android.R.id.content), "Перезагрузка роутера...", Snackbar.LENGTH_SHORT).show());
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