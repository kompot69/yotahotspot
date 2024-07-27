package ru.kompot69.yotahotspot;

import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import ru.kompot69.yotahotspot.databinding.ActivityMainBinding;
import ru.kompot69.yotahotspot.ui.AboutFragment;
import ru.kompot69.yotahotspot.ui.InformationFragment;
import ru.kompot69.yotahotspot.ui.MainFragment;
import ru.kompot69.yotahotspot.ui.NetworkInformationFragment;
import ru.kompot69.yotahotspot.ui.WiFiClientsFragment;
import ru.kompot69.yotahotspot.ui.WiFiSettingsFragment;

public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changeFragment(new MainFragment());
        binding.toolbar.setNavigationOnClickListener(view -> {
            binding.drawerLayout.open();
        });
        ActionBarDrawerToggle drawerToggle =
        new ActionBarDrawerToggle(
            this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setHomeAsUpIndicator(Utils.makeWhiteIcon(R.drawable.menu2, this));
        binding.navigationView.getMenu().getItem(0).setChecked(true);
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            changeDrawer(itemId);
            binding.navigationView.getCheckedItem().setChecked(false);
            item.setChecked(true);
            binding.drawerLayout.close();
            return true;
        });
    }

    private void changeDrawer(int itemId) {
        if (itemId == R.id.main) {
            changeFragment(new MainFragment());
        } else if (itemId == R.id.information) {
            changeFragment(new InformationFragment());
        } else if (itemId == R.id.wifi_settings) {
            changeFragment(new WiFiSettingsFragment());
        } else if (itemId == R.id.wifi_clients) {
            changeFragment(new WiFiClientsFragment());
        } else if (itemId == R.id.network_information) {
            changeFragment(new NetworkInformationFragment());
        } else if (itemId == R.id.about) {
            changeFragment(new AboutFragment());
        }
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
