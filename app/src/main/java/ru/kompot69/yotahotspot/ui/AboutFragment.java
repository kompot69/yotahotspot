package ru.kompot69.yotahotspot.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import ru.kompot69.yotahotspot.R;
import ru.kompot69.yotahotspot.Utils;
import ru.kompot69.yotahotspot.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {
    
    private Activity activity;
    private Context context;
    private FragmentAboutBinding binding;
    
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
        binding = FragmentAboutBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Тут код взаимодействия с UI фрагмента
        ImageView imageView = view.findViewById(R.id.avaK);
        String imageUrl = "https://4pda.to/s/as6yu42fibXqz1lYqQ2bkc7CQt1KKEFFUdv03Pw5SfsW5B49U.gif";
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
        ImageView imageView2 = view.findViewById(R.id.avaM);
        String imageUrl2 = "https://4pda.to/s/qirthImjawiWym6HZAg76QcAYi1TdTYLt3ecvOtUWfSZCAD.jpg";
        Glide.with(context)
                .load(imageUrl2)
                .into(imageView2);


        binding.buttonIcons.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://phosphoricons.com/"));
            context.startActivity(intent);
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem github = menu.add("Title");
        github.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        github.setIcon(R.drawable.github);

        github.setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kompot69/yotahotspot"));
            context.startActivity(intent);
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}