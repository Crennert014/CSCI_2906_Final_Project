package com.warburton.wfreunion

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.warburton.wfreunion.databinding.ActivityMainBinding
import com.warburton.wfreunion.utils.ThemeManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemeManager.getThemeResId(this))
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateThemeIcon()

        binding.btnThemeToggle.setOnClickListener {
            ThemeManager.toggleTheme(this)
        }

        binding.btnThemeToggle.setOnLongClickListener {
            if (ThemeManager.isDarkMode(this)) {
                showDarkSchemeDialog()
            }
            true
        }

        prefs = getSharedPreferences("wf_prefs", MODE_PRIVATE)

        binding.btnLogout.setOnClickListener {
            prefs.edit().remove("wf_logged_in").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Delegate navigation to NavigationUI
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        // Keep bottom nav selection in sync when navigating programmatically
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true
        }
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
            .setTitle("Choose Dark Scheme")
            .setSingleChoiceItems(schemes, current) { dialog, which ->
                ThemeManager.setDarkScheme(this, which)
                dialog.dismiss()
                recreate()
            }
            .show()
    }
}
