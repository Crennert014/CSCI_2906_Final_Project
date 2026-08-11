package com.warburton.wfreunion

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.warburton.wfreunion.api.ApiClient
import com.warburton.wfreunion.api.ChatMessage
import com.warburton.wfreunion.databinding.FragmentChatRoomBinding
import com.warburton.wfreunion.utils.UserManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    
    private var chatTitle: String? = null
    private var receiverId: String? = null
    private var pollingActive = true
    
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatTitle = arguments?.getString("chatTitle")
        receiverId = arguments?.getString("receiverId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myName = UserManager.getCurrentUser(requireContext()) ?: "family"
        adapter = ChatAdapter(messages, myName)
        
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.rvMessages.adapter = adapter

        startPolling()

        binding.btnSend.setOnClickListener {
            attemptSend(myName)
        }

        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                attemptSend(myName)
                true
            } else {
                false
            }
        }
    }

    private fun attemptSend(myName: String) {
        val text = binding.etMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            sendMessage(text, myName)
        }
    }

    private fun startPolling() {
        lifecycleScope.launch {
            while (pollingActive) {
                loadMessages()
                delay(3.seconds)
            }
        }
    }

    private fun loadMessages() {
        val prefs = requireActivity().getSharedPreferences("wf_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("wf_token", "") ?: ""

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getChatMessages("Bearer $token", receiverId)
                if (response.isSuccessful && (response.body() != null)) {
                    val newList = response.body()!!
                    if (newList.size != messages.size) {
                        messages.clear()
                        messages.addAll(newList)
                        adapter.notifyDataSetChanged()
                        binding.rvMessages.smoothScrollToPosition(messages.size - 1)
                    }
                }
            } catch (_: Exception) {
                // Polling fails silently
            }
        }
    }

    private fun sendMessage(text: String, myName: String) {
        val prefs = requireActivity().getSharedPreferences("wf_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("wf_token", "") ?: ""
        val userId = prefs.getString("wf_user_id", myName) ?: myName
        
        val msg = ChatMessage(
            senderId = userId,
            senderName = myName,
            receiverId = receiverId,
            text = text
        )

        binding.btnSend.isEnabled = false
        lifecycleScope.launch {
            try {
                val response = ApiClient.service.postChatMessage("Bearer $token", msg)
                if (response.isSuccessful) {
                    binding.etMessage.text.clear()
                    loadMessages()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.msg_failed_send_chat), Toast.LENGTH_SHORT).show()
                }
            } catch (_: Exception) {
                Toast.makeText(requireContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnSend.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pollingActive = false
        _binding = null
    }
}
