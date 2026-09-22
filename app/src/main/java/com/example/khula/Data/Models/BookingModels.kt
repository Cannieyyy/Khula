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
        val createdAt: String?
    )
}