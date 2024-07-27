package ru.kompot69.yotahotspot.ui;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Objects;

import ru.kompot69.yotahotspot.R;

public class wifiSettings extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        new Thread(() -> {

            int imageUrl = R.drawable.info; // URL вашей картинки
            ImageView imageView = findViewById(R.id.TEST); // Получение ссылки на ImageView из макета


            try {
                runOnUiThread(() -> {
                    Glide.with(getApplicationContext())
                            .load(imageUrl)
                            .into(imageView);

                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}