package com.example.khula

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.khula.Data.Models.ServiceModels.ServiceBrowseResponse
import com.example.khula.Data.remote.RetrofitClient
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch
import androidx.lifecycle.LifecycleOwner

class ProviderServiceAdapter(
    private var items: List<ServiceBrowseResponse>,
    private val onClick: (ServiceBrowseResponse) -> Unit
) : RecyclerView.Adapter<ProviderServiceAdapter.ViewHolder>() {

    // Keep track of which providers the customer has favourited
    private val favoriteProviderIds = mutableSetOf<Int>()

    // Call this from CustomerHome to load the initial favorites
    fun setFavorites(ids: List<Int>) {
        favoriteProviderIds.clear()
        favoriteProviderIds.addAll(ids)
        notifyItemRangeChanged(0, items.size) // Better than notifyDataSetChanged
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProvider: ShapeableImageView = view.findViewById(R.id.imgProvider)
        val tvProviderName: TextView = view.findViewById(R.id.tvProviderName)
        val tvSpecialty: TextView = view.findViewById(R.id.tvSpecialty)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvStartingPrice: TextView = view.findViewById(R.id.tvStartingPrice)
        val btnFavorite: ImageButton = view.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_provider_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvProviderName.text = item.providerName ?: "Unknown Provider"
        holder.tvSpecialty.text = item.category ?: item.title ?: ""
        holder.tvLocation.text = item.providerSuburb ?: item.providerCity ?: ""
        holder.tvStartingPrice.text = "From R${item.price ?: 0.0}"

        // Load Profile Picture
        if (!item.providerAvatarUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.providerAvatarUrl)
                .placeholder(R.drawable.ic_customer)
                .error(R.drawable.ic_customer)
                .into(holder.imgProvider)
        } else {
            holder.imgProvider.setImageResource(R.drawable.ic_customer)
        }

        // Ratings Logic
        val rating = item.averageRating ?: 0.0
        val reviewCount = item.totalReviews ?: 0

        if (reviewCount > 0) {
            holder.tvRating.text = "⭐ ${String.format("%.1f", rating)} ($reviewCount)"
        } else {
            holder.tvRating.text = "⭐ New"
        }

        // Favourites Heart Logic
        val providerId = item.providerId ?: -1
        val isFav = favoriteProviderIds.contains(providerId)
        val context = holder.itemView.context

        if (isFav) {
            holder.btnFavorite.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        } else {
            holder.btnFavorite.setColorFilter(ContextCompat.getColor(context, R.color.grey_500)) // Or however you define grey
        }

        holder.btnFavorite.setOnClickListener {
            if (providerId == -1) return@setOnClickListener

            // We need a lifecycle scope to make the API call.
            // We can grab it from the Activity context.
            val lifecycleOwner = context as? LifecycleOwner
            lifecycleOwner?.lifecycleScope?.launch {
                try {
                    val response = RetrofitClient.api.toggleFavorite(providerId)
                    if (response.isSuccessful) {
                        if (isFav) {
                            favoriteProviderIds.remove(providerId)
                        } else {
                            favoriteProviderIds.add(providerId)
                        }
                        notifyItemChanged(position)
                    }
                } catch (e: Exception) {
                    // Handle network error
                }
            }
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ServiceBrowseResponse>) {
        items = newItems
        notifyDataSetChanged() // This warning is fine for simple lists
    }
}