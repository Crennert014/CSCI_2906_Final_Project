package com.warburton.wfreunion.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("signup.php")
    suspend fun signup(@Body request: SignupRequest): Response<MessageResponse>

    @DELETE("auth.php")
    suspend fun logout(@Header("Authorization") bearer: String): Response<MessageResponse>

    @GET("photos.php")
    suspend fun getPhotos(@Header("Authorization") bearer: String): Response<PhotoListResponse>

    @Multipart
    @POST("photos.php")
    suspend fun uploadPhoto(
        @Header("Authorization") bearer: String,
        @Part photo: MultipartBody.Part
    ): Response<PhotoUploadResponse>

    @POST("contact.php")
    suspend fun sendContact(@Body request: ContactRequest): Response<MessageResponse>

    @GET("home_content.php")
    suspend fun getHomeContent(@Header("Authorization") bearer: String): Response<HomeContent>

    @GET("activities.php")
    suspend fun getActivities(@Header("Authorization") bearer: String): Response<List<ReunionActivity>>

    @GET("meals.php")
    suspend fun getMealSchedule(@Header("Authorization") bearer: String): Response<List<MealAssignment>>

    @GET("users.php")
    suspend fun getUsers(@Header("Authorization") bearer: String): Response<List<User>>

    @GET("chat.php")
    suspend fun getChatMessages(
        @Header("Authorization") bearer: String,
        @Query("receiver_id") receiverId: String?
    ): Response<List<ChatMessage>>

    @POST("chat.php")
    suspend fun postChatMessage(
        @Header("Authorization") bearer: String,
        @Body message: ChatMessage
    ): Response<MessageResponse>
}
