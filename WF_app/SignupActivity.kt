package com.warburton.wfreunion

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.warburton.wfreunion.api.ApiClient
import com.warburton.wfreunion.api.SignupRequest
import com.warburton.wfreunion.databinding.ActivitySignupBinding
import com.warburton.wfreunion.utils.ThemeManager
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemeManager.getThemeResId(this))
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateThemeIcon()

        binding.btnThemeToggle.setOnClickListener {
            ThemeManager.toggleTheme(this)
        }

        binding.tvLoginLink.setOnClickListener {
            finish()
        }

        binding.btnSignup.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSignup.isEnabled = false
            lifecycleScope.launch {
                try {
                    val response = ApiClient.service.signup(SignupRequest(fullName, username, password))
                    if (response.isSuccessful) {
                        Toast.makeText(this@SignupActivity, getString(R.string.toast_signup_success), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@SignupActivity, getString(R.string.error_username_exists), Toast.LENGTH_SHORT).show()
                        binding.btnSignup.isEnabled = true
                    }
                } catch (_: Exception) {
                    Toast.makeText(this@SignupActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                    binding.btnSignup.isEnabled = true
                }
            }
        }
    }

    private fun updateThemeIcon() {
        val isDark = ThemeManager.isDarkMode(this)
        binding.btnThemeToggle.setImageResource(
            if (isDark) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )
    }
}
