package com.example.khula.Data.Models

object ServiceModels {

    data class CreateServiceRequest(
        val title: String,
        val description: String,
        val category: String,
        val price: Double,
        val durationMinutes: Int
    )

    data class ServiceResponse(
        val serviceId: Int?,
        val title: String?,
        val description: String?,
        val category: String?,
        val price: Double?,
        val durationMinutes: Int?,
        val isActive: Boolean?,
        val createdAt: String?
    )

    data class CreateServiceResult(
        val message: String?,
        val serviceId: Int?
    )
}