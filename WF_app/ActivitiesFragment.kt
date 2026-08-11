package com.warburton.wfreunion

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.warburton.wfreunion.api.ApiClient
import com.warburton.wfreunion.api.MealAssignment
import com.warburton.wfreunion.api.ReunionActivity
import com.warburton.wfreunion.databinding.FragmentActivitiesBinding
import com.warburton.wfreunion.databinding.ItemActivityBinding
import kotlinx.coroutines.launch

class ActivitiesFragment : Fragment() {

    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Show default/static meal table immediately
        populateMeals(emptyList()) 
        
        fetchData()
    }

    private fun fetchData() {
        val prefs = requireActivity().getSharedPreferences("wf_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("wf_token", "") ?: ""

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val activitiesResponse = ApiClient.service.getActivities("Bearer $token")
                val mealsResponse = ApiClient.service.getMealSchedule("Bearer $token")

                binding.progressBar.visibility = View.GONE

                if (activitiesResponse.isSuccessful) {
                    val activities = activitiesResponse.body() ?: emptyList()
                    if (activities.isNotEmpty()) {
                        populateActivities(activities)
                    }
                }
                
                if (mealsResponse.isSuccessful) {
                    val meals = mealsResponse.body() ?: emptyList()
                    if (meals.isNotEmpty()) {
                        populateMeals(meals)
                    }
                }

            } catch (_: Exception) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun populateActivities(activities: List<ReunionActivity>) {
        binding.activitiesContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        
        for (activity in activities) {
            val itemBinding = ItemActivityBinding.inflate(inflater, binding.activitiesContainer, false)
            itemBinding.tvTitle.text = activity.title
            itemBinding.tvCoordinator.text = activity.coordinator
            itemBinding.tvDescription.text = activity.description
            binding.activitiesContainer.addView(itemBinding.root)
        }
    }

    private fun populateMeals(meals: List<MealAssignment>) {
        binding.tableMealSchedule.removeAllViews()

        // Add Header Row
        val headerRow = TableRow(requireContext())
        headerRow.setBackgroundColor(getThemeColor(com.google.android.material.R.attr.colorPrimary))
        
        val headers = arrayOf(
            getString(R.string.meal_label),
            getString(R.string.day_thursday),
            getString(R.string.day_friday),
            getString(R.string.day_saturday),
            getString(R.string.day_sunday)
        )
        for (text in headers) {
            headerRow.addView(createCell(text, isBold = true, isHeader = true))
        }
        binding.tableMealSchedule.addView(headerRow)

        val types = arrayOf(getString(R.string.meal_breakfast), getString(R.string.meal_lunch), getString(R.string.meal_dinner))
        val days = arrayOf(
            getString(R.string.day_thursday),
            getString(R.string.day_friday),
            getString(R.string.day_saturday),
            getString(R.string.day_sunday)
        )

        for (type in types) {
            val row = TableRow(requireContext())
            if (type == getString(R.string.meal_lunch)) {
                row.setBackgroundColor(getThemeColor(com.google.android.material.R.attr.colorSurface))
            } else {
                row.setBackgroundColor(getThemeColor(R.attr.colorSurfaceSoft))
            }

            row.addView(createCell(type, isBold = true, isHeader = false))

            for (day in days) {
                val meal = meals.find { it.mealType.equals(type, ignoreCase = true) && it.day.equals(day, ignoreCase = true) }
                val content = if (meal != null) {
                    "${meal.family}\n${meal.menu}"
                } else {
                    getDefaultMealContent(type, day)
                }
                
                row.addView(createCell(content, isBold = false, isHeader = false))
            }
            binding.tableMealSchedule.addView(row)
            
            val divider = View(requireContext())
            divider.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1)
            divider.setBackgroundColor(getThemeColor(R.attr.colorSurfaceSoft)) 
            binding.tableMealSchedule.addView(divider)
        }
    }

    private fun getDefaultMealContent(type: String, day: String): String {
        return when {
            (type == getString(R.string.meal_breakfast)) && (day == getString(R.string.day_friday)) -> getString(R.string.meal_sorenson)
            (type == getString(R.string.meal_breakfast)) && (day == getString(R.string.day_saturday)) -> getString(R.string.meal_dussler)
            (type == getString(R.string.meal_breakfast)) && (day == getString(R.string.day_sunday)) -> getString(R.string.meal_continental)
            (type == getString(R.string.meal_breakfast)) && (day == getString(R.string.day_thursday)) -> getString(R.string.na)
            (type == getString(R.string.meal_dinner)) && (day == getString(R.string.day_sunday)) -> getString(R.string.na)
            else -> getString(R.string.tbd)
        }
    }

    private fun createCell(text: String, isBold: Boolean, isHeader: Boolean): TextView {
        val tv = TextView(requireContext())
        tv.text = text
        tv.setPadding(30, 30, 30, 30)
        tv.setTextColor(if (isHeader) Color.WHITE else getThemeColor(com.google.android.material.R.attr.colorOnSurface))
        if (isBold) tv.typeface = android.graphics.Typeface.DEFAULT_BOLD
        return tv
    }

    private fun getThemeColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
