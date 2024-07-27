package ru.kompot69.yotahotspot;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class Utils {
    public static String sizeofFmt(long num) {
        String[] units = {" b", " Kb", " Mb", " Gb", " Tb"};

        int unitIndex = 0;
        while (num >= 1024 && unitIndex < units.length - 1) {
            num /= 1024;
            unitIndex++;
        }

        return num + units[unitIndex];
    }

    public static String convertToTime(long seconds) {
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder time = new StringBuilder();
        if (hours > 0) {
            time.append(hours).append("ч. ");
        }
        if (minutes > 0) {
            time.append(minutes).append("м. ");
        }
        if (hours == 0) {
            time.append(seconds).append("с.");
        }


        return time.toString();
    }


    public static Drawable makeWhiteIcon(int resId, Context context) {
        Drawable menuIcon =  ContextCompat.getDrawable(context, resId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            menuIcon.setColorFilter(new BlendModeColorFilter(Color.parseColor("#FFFFFF"), BlendMode.SRC_ATOP));
        } else {
            menuIcon.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        }
        return menuIcon;
    }

    public String getKeyValue(JSONObject obj, String key) {
        try {
            return obj.getString(key);
        } catch (JSONException ignored) {
            return "—";
        }
    }

    public static JSONObject requestGet(String arg) throws MalformedURLException, IOException, JSONException {
        StringBuilder stringBuilder = new StringBuilder();

        URL url = new URL(arg);
        URLConnection connection = url.openConnection();
        connection.connect();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));


        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        bufferedReader.close();
        return new JSONObject(stringBuilder.toString());
    }
}