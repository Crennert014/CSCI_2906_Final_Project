package com.warburton.wfreunion.utils

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class UserAccount(
    val fullName: String,
    val username: String,
    val password: String,
)

object UserManager {
    private const val PREFS_NAME = "user_accounts_prefs"
    private const val KEY_USERS = "users_list"
    private const val KEY_LOGGED_IN_USER = "current_user_name"

    fun signup(context: Context, user: UserAccount): Boolean {
        val users = getAllUsers(context).toMutableList()
        if (users.any { it.username == user.username }) return false
        
        users.add(user)
        saveUsers(context, users)
        return true
    }

    fun login(context: Context, username: String, pass: String): UserAccount? {
        return getAllUsers(context).find { (it.username == username) && (it.password == pass) }
    }

    fun setCurrentUser(context: Context, fullName: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LOGGED_IN_USER, fullName) }
    }

    fun getCurrentUser(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LOGGED_IN_USER, null)
    }

    private fun getAllUsers(context: Context): List<UserAccount> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_USERS, null) ?: return emptyList()
        val type = object : TypeToken<List<UserAccount>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun saveUsers(context: Context, users: List<UserAccount>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(users)
        prefs.edit { putString(KEY_USERS, json) }
    }
}
