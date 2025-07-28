package com.petchat.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.petchat.android.adapters.MomentAdapter;
import com.petchat.android.databinding.FragmentSocialBinding;
import com.petchat.android.models.Moment;

import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends Fragment {

    private FragmentSocialBinding binding;
    private MomentAdapter momentAdapter;
    private List<Moment> momentList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSocialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews();
        loadMoments();
        setupClickListeners();
    }

    private void setupViews() {
        momentList = new ArrayList<>();
        momentAdapter = new MomentAdapter(getContext(), momentList);
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(momentAdapter);
        
        // 设置统计数据
        binding.todayCount.setText("24");
        binding.likeCount.setText("156");
        binding.messageCount.setText("8");
    }

    private void loadMoments() {
        // 添加模拟数据
        momentList.add(new Moment("张小美", "2小时前", 
            "小白今天学会了新技能，会握手了！太聪明了~", 12, 3, true));
        
        momentList.add(new Moment("李大明", "4小时前", 
            "带着毛毛去公园散步，遇到了好多小朋友都想摸它哈哈", 28, 7, true));
        
        momentList.add(new Moment("王小花", "昨天", 
            "第一次使用PetChat，感觉太神奇了！真的能听懂咪咪在说什么", 45, 12, false));
        
        momentAdapter.notifyDataSetChanged();
    }

    private void setupClickListeners() {
        binding.postButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "发布动态功能开发中...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}