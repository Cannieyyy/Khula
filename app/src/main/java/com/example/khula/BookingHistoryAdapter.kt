package com.example.khula

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.khula.Data.Models.BookingModels
import java.text.SimpleDateFormat
import java.util.Locale

class BookingHistoryAdapter(
    private var bookings: List<BookingModels.BookingResponse>
) : RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder>() {


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    inner class BookingViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        // Service title.
        val tvServiceName: TextView =
            itemView.findViewById(
                R.id.tvHistoryServiceName
            )

        // Provider name.
        val tvProviderName: TextView =
            itemView.findViewById(
                R.id.tvHistoryProviderName
            )

        // Booking status container.
        val layoutStatus: LinearLayout =
            itemView.findViewById(
                R.id.layoutHistoryStatus
            )

        // Booking status text.
        val tvStatus: TextView =
            itemView.findViewById(
                R.id.tvHistoryStatus
            )

        // Preferred booking date and time.
        val tvDate: TextView =
            itemView.findViewById(
                R.id.tvHistoryDate
            )

        // Price currently has no corresponding
        // field in BookingResponse.
        val tvPrice: TextView =
            itemView.findViewById(
                R.id.tvHistoryPrice
            )
    }


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookingViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_booking_history,
                    parent,
                    false
                )

        return BookingViewHolder(
            view
        )
    }


    // =========================================================
    // BIND BOOKING
    // =========================================================

    override fun onBindViewHolder(
        holder: BookingViewHolder,
        position: Int
    ) {

        // Get current real booking.
        val booking =
            bookings[position]


        // =====================================================
        // SERVICE TITLE
        // =====================================================

        holder.tvServiceName.text =
            booking.serviceTitle
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "Service"


        // =====================================================
        // PROVIDER
        // =====================================================

        val providerName =
            booking.providerName
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "Provider"


        val providerSuburb =
            booking.providerSuburb
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }


        // Show provider and location when available.
        holder.tvProviderName.text =
            if (providerSuburb != null) {

                "$providerName • $providerSuburb"

            } else {

                providerName
            }


        // =====================================================
        // BOOKING DATE
        // =====================================================

        val date =
            booking.preferredDate
                ?.trim()
                .orEmpty()


        // =====================================================
        // BOOKING TIME
        // =====================================================

        val time =
            booking.preferredTime
                ?.trim()
                .orEmpty()


        // Try to make the date easier to read.
        val formattedDate =
            formatDate(
                date
            )


        // Combine date and time.
        holder.tvDate.text =
            when {

                formattedDate.isNotBlank() &&
                        time.isNotBlank() -> {

                    "$formattedDate • $time"
                }

                formattedDate.isNotBlank() -> {

                    formattedDate
                }

                time.isNotBlank() -> {

                    time
                }

                else -> {

                    "Date not set"
                }
            }


        // =====================================================
        // STATUS
        // =====================================================

        val status =
            booking.status
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "Pending"


        // Display exactly what the backend returns.
        holder.tvStatus.text =
            status.uppercase(
                Locale.getDefault()
            )


        // =====================================================
        // STATUS DESIGN
        // =====================================================

        // We have not yet confirmed every possible
        // backend status value.
        //
        // Therefore we are not inventing different
        // Accepted / Rejected / Completed states here.
        //
        // For now use the existing status background.
        holder.layoutStatus.setBackgroundResource(
            R.drawable.bg_status_completed
        )


        // =====================================================
        // PRICE
        // =====================================================

        // BookingResponse does NOT contain a price.
        //
        // Therefore we must not show the hardcoded
        // R350 from the original XML.
        holder.tvPrice.visibility =
            View.GONE
    }


    // =========================================================
    // ITEM COUNT
    // =========================================================

    override fun getItemCount(): Int {

        return bookings.size
    }


    // =========================================================
    // UPDATE DATA
    // =========================================================

    fun updateData(
        newBookings: List<BookingModels.BookingResponse>
    ) {

        // Replace current booking list.
        bookings =
            newBookings

        // Refresh RecyclerView.
        notifyDataSetChanged()
    }


    // =========================================================
    // FORMAT DATE
    // =========================================================

    private fun formatDate(
        date: String
    ): String {

        // Return immediately if no date exists.
        if (date.isBlank()) {

            return ""
        }


        return try {

            // Backend format expected from
            // CreateBookingRequest:
            //
            // 2026-09-22
            val backendFormat =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                )


            // Prevent SimpleDateFormat from accepting
            // invalid dates loosely.
            backendFormat.isLenient =
                false


            // Convert String to Date.
            val parsedDate =
                backendFormat.parse(
                    date
                )


            if (parsedDate != null) {

                // Customer-friendly format:
                //
                // 22 Sep 2026
                val displayFormat =
                    SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    )


                displayFormat.format(
                    parsedDate
                )

            } else {

                // If conversion failed, keep the
                // backend value instead of inventing one.
                date
            }


        } catch (_: Exception) {

            // Backend may eventually return a different
            // date format.
            //
            // In that case show the original value.
            date
        }
    }
}