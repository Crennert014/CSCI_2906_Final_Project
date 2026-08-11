package com.warburton.wfreunion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.warburton.wfreunion.api.ChatMessage
import com.warburton.wfreunion.databinding.ItemChatMessageReceivedBinding
import com.warburton.wfreunion.databinding.ItemChatMessageSentBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private val messages: List<ChatMessage>, private val currentUserName: String) : 
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderName == currentUserName) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_SENT) {
            SentViewHolder(ItemChatMessageSentBinding.inflate(inflater, parent, false))
        } else {
            ReceivedViewHolder(ItemChatMessageReceivedBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))
        
        if (holder is SentViewHolder) {
            holder.binding.tvText.text = msg.text
            holder.binding.tvTime.text = time
        } else if (holder is ReceivedViewHolder) {
            holder.binding.tvSender.text = msg.senderName
            holder.binding.tvText.text = msg.text
            holder.binding.tvTime.text = time
            // Hide sender name if it's a private chat (receiverId is not null)
            holder.binding.tvSender.visibility = if (msg.receiverId == null) ViewGroup.VISIBLE else ViewGroup.GONE
        }
    }

    override fun getItemCount() = messages.size

    class SentViewHolder(val binding: ItemChatMessageSentBinding) : RecyclerView.ViewHolder(binding.root)
    class ReceivedViewHolder(val binding: ItemChatMessageReceivedBinding) : RecyclerView.ViewHolder(binding.root)
}
