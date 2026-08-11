package com.warburton.wfreunion.api

import com.google.gson.annotations.SerializedName

// ── Auth ─────────────────────────────────────────────────────────────────────

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(
    val token: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("user_id") val userId: String?
)

data class SignupRequest(
    @SerializedName("full_name") val fullName: String,
    val username: String,
    val password: String
)

data class User(
    val id: String,
    @SerializedName("full_name") val fullName: String,
    val username: String
)

// ── Photos ───────────────────────────────────────────────────────────────────

data class Photo(
    val id: Int,
    val filename: String,
    @SerializedName("original_name") val originalName: String?,
    val url: String,
    @SerializedName("uploaded_at") val uploadedAt: String
)

data class PhotoListResponse(val photos: List<Photo>)

data class PhotoUploadResponse(
    val id: Int,
    val filename: String,
    val url: String
)

// ── Contact ──────────────────────────────────────────────────────────────────

data class ContactRequest(
    val name: String,
    val email: String,
    val subject: String,
    val message: String
)

data class MessageResponse(val message: String)

// ── App Content ──────────────────────────────────────────────────────────────

data class ChatMessage(
    val senderId: String,
    val senderName: String,
    val receiverId: String?, // null for group chat
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class HomeContent(
    @SerializedName("welcome_title") val welcomeTitle: String,
    @SerializedName("intro_text") val introText: String,
    val location: String,
    @SerializedName("event_date") val eventDate: String // Format: YYYY-MM-DD HH:MM:SS
)

data class ReunionActivity(
    val id: Int,
    val title: String,
    val coordinator: String,
    val description: String
)

data class MealAssignment(
    val id: Int,
    val day: String,
    @SerializedName("meal_type") val mealType: String,
    val family: String,
    val menu: String
)

// ── Errors ───────────────────────────────────────────────────────────────────

data class ApiError(val error: String)
