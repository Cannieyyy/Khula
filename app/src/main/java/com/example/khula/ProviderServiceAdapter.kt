package com.example.khula

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.khula.Data.Models.ServiceModels.ServiceBrowseResponse
import com.google.android.material.imageview.ShapeableImageView

class ProviderServiceAdapter(
    private var items: List<ServiceBrowseResponse>,
    private val onClick: (ServiceBrowseResponse) -> Unit
) : RecyclerView.Adapter<ProviderServiceAdapter.ViewHolder>() {

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

        // No ratings table yet — placeholder until that's built
        holder.tvRating.text = "New"

        holder.btnFavorite.setOnClickListener {
            // TODO: wire to a favourites endpoint once it exists
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ServiceBrowseResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}