package com.warburton.wfreunion

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.warburton.wfreunion.api.ApiClient
import com.warburton.wfreunion.api.HomeContent
import com.warburton.wfreunion.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var countDownTimer: CountDownTimer? = null
    private var eventDateLong: Long = 0

    // Default target: June 10, 2027 at 12:00 PM
    private val defaultEventDate: Long = Calendar.getInstance().apply {
        set(2027, Calendar.JUNE, 10, 12, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Start countdown with default date immediately
        eventDateLong = defaultEventDate
        startCountdown()

        fetchHomeContent()
    }

    private fun fetchHomeContent() {
        val prefs = requireActivity().getSharedPreferences("wf_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("wf_token", "") ?: ""

        // Show progress only if we are syncing, but keep content visible
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getHomeContent("Bearer $token")
                binding.progressBar.visibility = View.GONE
                
                if (response.isSuccessful && (response.body() != null)) {
                    updateUI(response.body()!!)
                }
            } catch (_: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateUI(content: HomeContent) {
        binding.progressBar.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE

        binding.tvWelcomeTitle.text = content.welcomeTitle
        binding.tvIntroText.text = content.introText
        binding.tvLocation.text = content.location
        binding.tvEventDate.text = content.eventDate

        // Parse date for updated countdown
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val parsedDate = sdf.parse(content.eventDate)
            if (parsedDate != null) {
                eventDateLong = parsedDate.time
                startCountdown()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startCountdown() {
        countDownTimer?.cancel()
        val now = System.currentTimeMillis()
        val remaining = eventDateLong - now

        if (remaining <= 0) {
            binding.tvDays.text = getString(R.string.reunion_started)
            binding.tvHours.text = "🎉"
            binding.tvMinutes.text = ""
            binding.tvSeconds.text = ""
            updateCurrentTime()
            return
        }

        countDownTimer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val days    = millisUntilFinished / (1000L * 60 * 60 * 24)
                val hours   = (millisUntilFinished % (1000L * 60 * 60 * 24)) / (1000L * 60 * 60)
                val minutes = (millisUntilFinished % (1000L * 60 * 60)) / (1000L * 60)
                val seconds = (millisUntilFinished % (1000L * 60)) / 1000L

                binding.tvDays.text    = getString(R.string.timer_days_label, days.toString())
                binding.tvHours.text   = getString(R.string.timer_hours_label, hours.toString().padStart(2, '0'))
                binding.tvMinutes.text = getString(R.string.timer_minutes_label, minutes.toString().padStart(2, '0'))
                binding.tvSeconds.text = getString(R.string.timer_seconds_label, seconds.toString().padStart(2, '0'))
                updateCurrentTime()
            }

            override fun onFinish() {
                binding.tvDays.text = getString(R.string.reunion_started)
                binding.tvHours.text = ""
                binding.tvMinutes.text = ""
                binding.tvSeconds.text = ""
            }
        }.start()
    }

    private fun updateCurrentTime() {
        val now = Date()
        binding.tvCurrentDate.text = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(now)
        binding.tvCurrentTime.text = SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(now)
        binding.tvCurrentDay.text  = SimpleDateFormat("EEEE", Locale.getDefault()).format(now)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}
