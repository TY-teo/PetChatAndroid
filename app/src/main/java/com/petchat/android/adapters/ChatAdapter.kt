package com.petchat.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.petchat.android.R
import com.petchat.android.models.Message
import com.petchat.android.models.MessageType
import com.petchat.android.models.PetEmotion
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = inflater.inflate(R.layout.item_message_user, parent, false)
                UserMessageViewHolder(view)
            }
            VIEW_TYPE_PET -> {
                val view = inflater.inflate(R.layout.item_message_pet, parent, false)
                PetMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is PetMessageViewHolder -> holder.bind(message)
        }
    }
    
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            MessageType.USER -> VIEW_TYPE_USER
            MessageType.PET -> VIEW_TYPE_PET
        }
    }
    
    class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        private val timeText: TextView = itemView.findViewById(R.id.time_text)
        
        fun bind(message: Message) {
            messageText.text = message.content
            timeText.text = formatTime(message.timestamp)
        }
    }
    
    class PetMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        private val timeText: TextView = itemView.findViewById(R.id.time_text)
        private val emotionText: TextView = itemView.findViewById(R.id.emotion_text)
        
        fun bind(message: Message) {
            messageText.text = message.content
            timeText.text = formatTime(message.timestamp)
            
            // 显示宠物情绪
            message.emotion?.let { emotion ->
                emotionText.visibility = View.VISIBLE
                emotionText.text = getEmotionEmoji(emotion)
            } ?: run {
                emotionText.visibility = View.GONE
            }
        }
        
        private fun getEmotionEmoji(emotion: PetEmotion): String {
            return when (emotion) {
                PetEmotion.HAPPY -> "😊"
                PetEmotion.EXCITED -> "🤗"
                PetEmotion.SAD -> "😢"
                PetEmotion.HUNGRY -> "🍖"
                PetEmotion.TIRED -> "😴"
                PetEmotion.PLAYFUL -> "🎾"
                PetEmotion.CURIOUS -> "🤔"
                PetEmotion.NORMAL -> "🐕"
            }
        }
    }
    
    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_PET = 1
        
        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
    
    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}