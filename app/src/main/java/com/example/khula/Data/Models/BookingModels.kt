package com.example.khula.Data.Models

object BookingModels {

    data class CreateBookingRequest(
        val serviceID: Int,
        val preferredDate: String,
        val preferredTime: String,
        val description: String?
    )

    data class CreateBookingResult(
        val message: String?,
        val bookingId: Int?
    )

    data class BookingResponse(
        val bookingId: Int?,
        val serviceTitle: String?,
        val providerName: String?,
        val providerSuburb: String?,
        val customerName: String?,
        val customerSuburb: String?,
        val preferredDate: String?,
        val preferredTime: String?,
        val status: String?,
        val description: String?,
        val createdAt: String?
    )
    data class ProviderOverviewResponse(
        val newRequests: Int,
        val incoming: Int,
        val completed: Int,
        val rating: Double
    )


    data class UpdateBookingStatusRequest(
        val status: String
    )

    data class UpdateBookingStatusResult(
        val message: String?,
        val status: String?
    )


}