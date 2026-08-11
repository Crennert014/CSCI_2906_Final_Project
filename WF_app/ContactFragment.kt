package com.warburton.wfreunion

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.warburton.wfreunion.api.ApiClient
import com.warburton.wfreunion.api.ContactRequest
import com.warburton.wfreunion.databinding.FragmentContactBinding
import kotlinx.coroutines.launch

class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            val name    = binding.etName.text.toString().trim()
            val email   = binding.etEmail.text.toString().trim()
            val subject = binding.etSubject.text.toString().trim()
            val message = binding.etMessage.text.toString().trim()

            when {
                name.isEmpty() -> { binding.etName.error = getString(R.string.error_name_required); return@setOnClickListener }
                email.isEmpty() -> { binding.etEmail.error = getString(R.string.error_email_required); return@setOnClickListener }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.etEmail.error = getString(R.string.error_email_invalid); return@setOnClickListener
                }
                message.isEmpty() -> { binding.etMessage.error = getString(R.string.error_message_required); return@setOnClickListener }
            }

            binding.btnSubmit.isEnabled = false
            lifecycleScope.launch {
                try {
                    val response = ApiClient.service.sendContact(
                        ContactRequest(name, email, subject, message)
                    )
                    if (response.isSuccessful) {
                        binding.etName.text?.clear()
                        binding.etEmail.text?.clear()
                        binding.etSubject.text?.clear()
                        binding.etMessage.text?.clear()
                        binding.tvSuccess.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), getString(R.string.toast_contact_success), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.toast_contact_fail), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), getString(R.string.error_network), Toast.LENGTH_LONG).show()
                } finally {
                    binding.btnSubmit.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
