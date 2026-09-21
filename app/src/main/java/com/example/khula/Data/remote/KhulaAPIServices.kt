package com.example.khula.Data.remote
import com.example.khula.Data.Models.AuthenticateModel.RegisterRequest
import com.example.khula.Data.Models.AuthenticateModel.LoginRequest
import com.example.khula.Data.Models.AuthenticateModel.AuthResponse
import com.example.khula.Data.Models.AuthenticateModel.UserProfile
import com.example.khula.Data.Models.ServiceModels.ServiceResponse
import com.example.khula.Data.Models.ServiceModels.CreateServiceResult
import com.example.khula.Data.Models.ServiceModels.CreateServiceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Path
//creating the API as an interface
interface KhulaAPIServices {

    @POST("api/Authentication/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/Authentication/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>


    @GET("api/Users/me")
    suspend fun getMyProfile(): Response<UserProfile>


    // SERVICES
    @POST("api/Services")
    suspend fun createService(@Body request: CreateServiceRequest): Response<CreateServiceResult>

    @GET("api/Services/my")
    suspend fun getMyServices(): Response<List<ServiceResponse>>

    @DELETE("api/Services/{id}")
    suspend fun deleteService(@Path("id") id: Int): Response<Unit>
}