package com.petchat.android.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.petchat.android.databinding.FragmentPetBinding
import com.petchat.android.ui.pet.HealthStatus
import com.petchat.android.ui.pet.PetViewModel
import com.petchat.android.ui.pet.ReminderStatus
import kotlinx.coroutines.launch

class PetFragment : Fragment() {
    
    private var _binding: FragmentPetBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PetViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupClickListeners() {
        // 设置按钮
        binding.settingsButton.setOnClickListener {
            Toast.makeText(context, "打开设置", Toast.LENGTH_SHORT).show()
        }
        
        // 快捷功能卡片
        binding.healthRecordCard.setOnClickListener {
            handleFunction("健康记录")
        }
        
        binding.petAlbumCard.setOnClickListener {
            handleFunction("宠物相册")
        }
        
        binding.feedingPlanCard.setOnClickListener {
            handleFunction("喂食计划")
        }
        
        binding.activityDataCard.setOnClickListener {
            handleFunction("运动数据")
        }
        
        // 更多功能折叠/展开
        binding.moreFunctionsHeader.setOnClickListener {
            viewModel.toggleMoreFunctions()
        }
        
        // 更多功能列表项
        binding.growthRecordItem.setOnClickListener {
            handleFunction("成长记录")
        }
        
        binding.medicalRecordItem.setOnClickListener {
            handleFunction("病历档案")
        }
        
        binding.vaccineScheduleItem.setOnClickListener {
            handleFunction("疫苗计划")
        }
        
        binding.groomingItem.setOnClickListener {
            handleFunction("美容护理")
        }
        
        binding.deviceSettingsItem.setOnClickListener {
            handleFunction("设备管理")
        }
        
        binding.dataAnalysisItem.setOnClickListener {
            handleFunction("数据分析")
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    // 更新宠物信息
                    uiState.petInfo?.let { petInfo ->
                        binding.petName.text = petInfo.name
                        binding.petBreed.text = "${petInfo.breed} • ${petInfo.age}岁"
                        
                        // 更新健康状态显示
                        val (stars, statusText, statusColor) = when (petInfo.healthStatus) {
                            HealthStatus.EXCELLENT -> Triple("⭐⭐⭐⭐⭐", "健康良好", "#4CAF50")
                            HealthStatus.GOOD -> Triple("⭐⭐⭐⭐", "健康", "#8BC34A")
                            HealthStatus.FAIR -> Triple("⭐⭐⭐", "一般", "#FFC107")
                            HealthStatus.NEEDS_ATTENTION -> Triple("⭐⭐", "需要关注", "#FF5722")
                        }
                        
                        binding.healthStars.text = stars
                        binding.healthStatusText.text = statusText
                        binding.healthStatusText.setTextColor(android.graphics.Color.parseColor(statusColor))
                    }
                    
                    // 更新今日提醒
                    uiState.todayReminders.forEach { reminder ->
                        when (reminder.name) {
                            "喂食" -> {
                                binding.feedingStatus.text = when (reminder.status) {
                                    ReminderStatus.COMPLETED -> "✓ 已完成"
                                    ReminderStatus.PENDING -> reminder.time ?: "待完成"
                                    ReminderStatus.SCHEDULED -> reminder.time ?: "计划中"
                                }
                                binding.feedingStatus.setTextColor(
                                    android.graphics.Color.parseColor(
                                        if (reminder.status == ReminderStatus.COMPLETED) "#4CAF50" else "#FF6B9D"
                                    )
                                )
                            }
                            "遛弯" -> {
                                binding.walkTime.text = reminder.time ?: "待安排"
                                binding.walkTime.setTextColor(
                                    android.graphics.Color.parseColor(
                                        if (reminder.status == ReminderStatus.COMPLETED) "#4CAF50" else "#FF6B9D"
                                    )
                                )
                            }
                            "洗澡" -> {
                                binding.bathTime.text = reminder.time ?: "待安排"
                                binding.bathTime.setTextColor(
                                    android.graphics.Color.parseColor("#999999")
                                )
                            }
                        }
                    }
                    
                    // 更新更多功能折叠状态
                    updateMoreFunctionsVisibility(uiState.isMoreFunctionsExpanded)
                }
            }
        }
    }
    
    private fun updateMoreFunctionsVisibility(isExpanded: Boolean) {
        if (isExpanded) {
            binding.moreFunctionsContent.visibility = View.VISIBLE
            animateExpandIcon(180f)
        } else {
            binding.moreFunctionsContent.visibility = View.GONE
            animateExpandIcon(0f)
        }
    }
    
    private fun animateExpandIcon(rotation: Float) {
        ObjectAnimator.ofFloat(binding.expandIcon, "rotation", rotation).apply {
            duration = 300
            start()
        }
    }
    
    private fun handleFunction(functionName: String) {
        Toast.makeText(context, "正在打开$functionName...", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}