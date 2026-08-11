package com.warburton.wfreunion

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.warburton.wfreunion.api.ApiClient
import com.warburton.wfreunion.api.LoginRequest
import com.warburton.wfreunion.databinding.ActivityLoginBinding
import com.warburton.wfreunion.utils.ThemeManager
import com.warburton.wfreunion.utils.UserManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemeManager.getThemeResId(this))
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateThemeIcon()

        binding.btnThemeToggle.setOnClickListener {
            ThemeManager.toggleTheme(this)
        }

        binding.tvSignupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.btnThemeToggle.setOnLongClickListener {
            if (ThemeManager.isDarkMode(this)) {
                showDarkSchemeDialog()
            }
            true
        }

        prefs = getSharedPreferences("wf_prefs", MODE_PRIVATE)

        // Auto-login if session is still active
        if (prefs.getBoolean("wf_logged_in", false)) {
            navigateToMain()
            return
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // 1. Shared master bypass
            if ((username == getString(R.string.login_username)) && (password == getString(R.string.login_password))) {
                UserManager.setCurrentUser(this, null)
                prefs.edit { putBoolean("wf_logged_in", true) }
                navigateToMain()
                return@setOnClickListener
            }

            if (username.isEmpty() || password.isEmpty()) {
                showError(getString(R.string.error_empty_fields))
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false
            lifecycleScope.launch {
                try {
                    val response = ApiClient.service.login(LoginRequest(username, password))
                    if (response.isSuccessful && (response.body() != null)) {
                        val body = response.body()!!
                        UserManager.setCurrentUser(this@LoginActivity, body.displayName ?: username)
                        
                        prefs.edit {
                            putBoolean("wf_logged_in", true)
                            putString("wf_token", body.token)
                            putString("wf_user_id", body.userId)
                        }
                        
                        navigateToMain()
                    } else {
                        showError(getString(R.string.error_invalid_credentials))
                        binding.btnLogin.isEnabled = true
                    }
                } catch (_: Exception) {
                    showError(getString(R.string.error_network))
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun updateThemeIcon() {
        val isDark = ThemeManager.isDarkMode(this)
        binding.btnThemeToggle.setImageResource(
            if (isDark) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )
    }

    private fun showDarkSchemeDialog() {
        val schemes = arrayOf("Forest Night", "Deep Ocean", "Midnight Charcoal")
        val current = ThemeManager.getDarkScheme(this)

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_theme_title))
            .setSingleChoiceItems(schemes, current) { dialog, which ->
                ThemeManager.setDarkScheme(this, which)
                dialog.dismiss()
                recreate()
            }
            .show()
    }
}
