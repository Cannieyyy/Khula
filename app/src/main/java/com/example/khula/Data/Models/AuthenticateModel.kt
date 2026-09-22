package com.example.khula.Data.Models

class AuthenticateModel {


//created a data class for the register request
    data class RegisterRequest(
        val firstName: String,
        val lastName: String,
        val EmailAddress: String,
        val phoneNumber: String,
        val password: String,
        val ComfirmPassword: String,
        val accountType: String,
        val province: String,
        val city: String,
        val suburb: String
    )

    //created a data class for the Login request
    data class LoginRequest(
        val EmailAddress: String,
        val password: String
    )


    //created a data class for the Response
    data class AuthResponse(
        val message: String?,
        val userId: Int?,
        val accountType: String?,
        val token: String?


    )

    data class UserProfile(
        val userId: Int?,
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val phoneNumber: String?,
        val accountType: String?,
        val province: String?,
        val city: String?,
        val suburb: String?
    )

}