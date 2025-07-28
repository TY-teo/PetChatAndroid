package com.petchat.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.petchat.android.databinding.FragmentLocationBinding;

public class LocationFragment extends Fragment {

    private FragmentLocationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews();
        setupFenceToggles();
        setupClickListeners();
    }

    private void setupViews() {
        // 设置当前位置信息
        binding.locationName.setText("家附近公园");
        binding.locationTime.setText("2分钟前更新");
        
        // 设置活动轨迹
        binding.activity1Time.setText("09:00");
        binding.activity1Desc.setText("在家休息");
        
        binding.activity2Time.setText("14:30");
        binding.activity2Desc.setText("散步到公园");
        
        binding.activity3Time.setText("16:20");
        binding.activity3Desc.setText("当前位置");
    }

    private void setupFenceToggles() {
        binding.homeFenceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String message = isChecked ? "家附近围栏已开启" : "家附近围栏已关闭";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        binding.parkFenceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String message = isChecked ? "社区公园围栏已开启" : "社区公园围栏已关闭";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        binding.addFenceButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "添加电子围栏功能开发中...", Toast.LENGTH_SHORT).show();
        });

        binding.mapPlaceholder.setOnClickListener(v -> {
            Toast.makeText(getContext(), "地图功能开发中...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}