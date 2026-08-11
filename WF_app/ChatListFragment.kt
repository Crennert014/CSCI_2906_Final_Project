package com.warburton.wfreunion

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.warburton.wfreunion.api.ApiClient
import com.warburton.wfreunion.api.User
import com.warburton.wfreunion.databinding.FragmentChatListBinding
import com.warburton.wfreunion.databinding.ItemChatUserBinding
import com.warburton.wfreunion.utils.UserManager
import kotlinx.coroutines.launch

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val usersList = mutableListOf<User>()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGroupChat.setOnClickListener {
            openChatRoom(null, "Family Group Chat")
        }

        adapter = UserAdapter(usersList) { user ->
            openChatRoom(user.username, user.fullName)
        }
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

        fetchUsers()
    }

    private fun fetchUsers() {
        val prefs = requireActivity().getSharedPreferences("wf_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("wf_token", "") ?: ""
        val myName = UserManager.getCurrentUser(requireContext()) ?: "family"

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getUsers("Bearer $token")
                if (response.isSuccessful && (response.body() != null)) {
                    val users = response.body()!!.filter { it.fullName != myName }
                    usersList.clear()
                    usersList.addAll(users)
                    adapter.notifyDataSetChanged()
                }
            } catch (_: Exception) {
                // Ignore silently or show error
            }
        }
    }

    private fun openChatRoom(receiverId: String?, title: String) {
        val bundle = Bundle().apply {
            putString("chatTitle", title)
            putString("receiverId", receiverId)
        }
        findNavController().navigate(R.id.chatRoomFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class UserAdapter(private val users: List<User>, private val onClick: (User) -> Unit) : 
        RecyclerView.Adapter<UserAdapter.ViewHolder>() {

        inner class ViewHolder(val itemBinding: ItemChatUserBinding) : RecyclerView.ViewHolder(itemBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val b = ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(b)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val user = users[position]
            holder.itemBinding.tvName.text = user.fullName
            holder.itemBinding.tvInitial.text = user.fullName.take(1).uppercase()
            holder.itemBinding.root.setOnClickListener { onClick(user) }
        }

        override fun getItemCount() = users.size
    }
}
