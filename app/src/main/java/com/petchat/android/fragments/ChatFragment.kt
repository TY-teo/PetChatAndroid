package com.petchat.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.petchat.android.R
import com.petchat.android.adapters.ChatAdapter
import com.petchat.android.databinding.FragmentChatBinding
import com.petchat.android.ui.chat.ChatViewModel
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupListeners()
        observeViewModel()
    }
    
    private fun setupViews() {
        // 设置RecyclerView
        chatAdapter = ChatAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            itemAnimator?.changeDuration = 0
        }
    }
    
    private fun setupListeners() {
        // 发送按钮点击事件
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
        
        // 语音按钮点击事件
        binding.voiceButton.setOnClickListener {
            viewModel.toggleVoiceMode()
        }
        
        // 输入框回车发送
        binding.messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
        
        // 语音录制按钮（长按录音）
        binding.voiceRecordButton.setOnClickListener {
            Toast.makeText(context, "语音识别功能开发中...", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    // 更新消息列表
                    chatAdapter.submitList(uiState.messages) {
                        // 滚动到最新消息
                        if (uiState.messages.isNotEmpty()) {
                            binding.recyclerView.smoothScrollToPosition(uiState.messages.size - 1)
                        }
                    }
                    
                    // 更新语音模式UI
                    updateVoiceModeUI(uiState.isVoiceMode)
                }
            }
        }
    }
    
    private fun updateVoiceModeUI(isVoiceMode: Boolean) {
        if (isVoiceMode) {
            binding.voiceInputLayout.visibility = View.VISIBLE
            binding.textInputLayout.visibility = View.GONE
            binding.voiceButton.setImageResource(R.drawable.ic_keyboard)
        } else {
            binding.voiceInputLayout.visibility = View.GONE
            binding.textInputLayout.visibility = View.VISIBLE
            binding.voiceButton.setImageResource(R.drawable.ic_mic)
        }
    }
    
    private fun sendMessage() {
        val text = binding.messageInput.text.toString().trim()
        if (text.isNotEmpty()) {
            viewModel.sendMessage(text)
            binding.messageInput.text.clear()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}