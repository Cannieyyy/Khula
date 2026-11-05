package com.example.khula.Data.remote
import com.example.khula.Data.Models.AuthenticateModel
import com.example.khula.Data.Models.AuthenticateModel.RegisterRequest
import com.example.khula.Data.Models.AuthenticateModel.LoginRequest
import com.example.khula.Data.Models.AuthenticateModel.AuthResponse
import com.example.khula.Data.Models.AuthenticateModel.UserProfile
import com.example.khula.Data.Models.AuthenticateModel.ForgotPasswordRequest
import com.example.khula.Data.Models.AuthenticateModel.ResetPasswordRequest
import com.example.khula.Data.Models.AuthenticateModel.GoogleLoginRequest
import com.example.khula.Data.Models.ServiceModels
import com.example.khula.Data.Models.ServiceModels.ServiceResponse
import com.example.khula.Data.Models.ServiceModels.CreateServiceResult
import com.example.khula.Data.Models.ServiceModels.CreateServiceRequest
import com.example.khula.Data.Models.BookingModels
import com.example.khula.Data.Models.MessageModels
import retrofit2.Response
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

//creating the API as an interface
interface KhulaAPIServices {

    @POST("api/Authentication/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/Authentication/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>


    @GET("api/Users/me")
    suspend fun getMyProfile(): Response<UserProfile>

    // Update Profile Text
    @PUT("api/Users/me")
    suspend fun updateProfile(@Body request: ServiceModels.UpdateProfileRequest): Response<Unit>

    // Upload Profile Picture
    @Multipart
    @POST("api/Users/me/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): Response<Unit>

    // SERVICES
    @POST("api/Services")
    suspend fun createService(@Body request: CreateServiceRequest): Response<CreateServiceResult>

    @GET("api/Services/my")
    suspend fun getMyServices(): Response<List<ServiceResponse>>

    @DELETE("api/Services/{id}")
    suspend fun deleteService(@Path("id") id: Int): Response<Unit>

    @POST("api/Favorites/toggle/{providerId}")
    suspend fun toggleFavorite(@Path("providerId") providerId: Int): Response<Unit>

    @GET("api/Favorites/my")
    suspend fun getMyFavorites(): Response<List<Int>>

    @POST("api/Authentication/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("api/Favorites/providers")
    suspend fun getFavoriteProviders(@Body providerIds: List<Int>): Response<List<ServiceModels.FavoriteProviderResponse>>

    @GET("api/Services/browse")
    suspend fun browseServices(
        @Query("category") category: String? = null,
        @Query("province") province: String? = null,
        @Query("city") city: String? = null,
        @Query("suburb") suburb: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("search") search: String? = null
    ): Response<List<ServiceModels.ServiceBrowseResponse>>



    @POST("api/Authentication/google-login")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<AuthResponse>
    @POST("api/Authentication/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>
    @POST("api/Bookings")
    suspend fun createBooking(@Body request: BookingModels.CreateBookingRequest): Response<BookingModels.CreateBookingResult>

    @GET("api/Bookings/customer")
    suspend fun getMyBookingsAsCustomer(): Response<List<BookingModels.BookingResponse>>

    @GET("api/Bookings/provider")
    suspend fun getMyBookingsAsProvider(): Response<List<BookingModels.BookingResponse>>

    @GET("api/Bookings/provider/overview")
    suspend fun getProviderOverview(): Response<BookingModels.ProviderOverviewResponse>

    @PUT("api/Bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") id: Int,
        @Body request: BookingModels.UpdateBookingStatusRequest
    ): Response<BookingModels.UpdateBookingStatusResult>


    @GET("api/Messages/{bookingId}")
    suspend fun getMessages(@Path("bookingId") bookingId: Int): Response<List<MessageModels.MessageResponse>>

    @POST("api/Messages")
    suspend fun sendMessage(@Body request: MessageModels.SendMessageRequest): Response<Unit>

    @POST("api/Users/fcm-token")
    suspend fun saveFcmToken(@Body request: AuthenticateModel.FcmTokenRequest): Response<Unit>

    @PUT("api/Bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") bookingId: Int,
        @Body request: BookingModels.UpdateStatusRequest
    ): Response<Unit>

}