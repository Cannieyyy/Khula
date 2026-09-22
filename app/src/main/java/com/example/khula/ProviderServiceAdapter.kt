package com.example.khula

// LayoutInflater converts our XML item layout into an actual View.
import android.view.LayoutInflater

// View represents an Android UI element.
import android.view.View

// ViewGroup represents a container that can hold other Views.
import android.view.ViewGroup

// ImageButton is used for the favourite heart button.
import android.widget.ImageButton

// TextView is used for displaying text.
import android.widget.TextView

// RecyclerView allows us to display a list efficiently.
import androidx.recyclerview.widget.RecyclerView

// Import the model returned by the browse-services API.
import com.example.khula.Data.Models.ServiceModels.ServiceBrowseResponse


// This adapter displays services/providers inside the RecyclerView.
//
// services = list of services received from the API.
//
// onItemClick = function that runs when the user taps a service card.
class ProviderServiceAdapter(

    private var services: List<ServiceBrowseResponse>,

    private val onItemClick: (ServiceBrowseResponse) -> Unit

) : RecyclerView.Adapter<ProviderServiceAdapter.ServiceViewHolder>() {


    // This ViewHolder stores references to the Views
    // inside item_provider_card.xml.
    class ServiceViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        // Provider's name.
        val tvProviderName: TextView =
            itemView.findViewById(R.id.tvProviderName)

        // Service category/specialty.
        val tvSpecialty: TextView =
            itemView.findViewById(R.id.tvSpecialty)

        // Provider's location.
        val tvLocation: TextView =
            itemView.findViewById(R.id.tvLocation)

        // Starting/service price.
        val tvStartingPrice: TextView =
            itemView.findViewById(R.id.tvStartingPrice)

        // Rating displayed on the card.
        val tvRating: TextView =
            itemView.findViewById(R.id.tvRating)

        // Favourite button.
        val btnFavorite: ImageButton =
            itemView.findViewById(R.id.btnFavorite)
    }


    // Android calls this when it needs to create a new card.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServiceViewHolder {

        // Load item_provider_card.xml.
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_provider_card,
                parent,
                false
            )

        // Put the XML View inside our ViewHolder.
        return ServiceViewHolder(view)
    }


    // Android calls this when it needs to put service data
    // inside one of our cards.
    override fun onBindViewHolder(
        holder: ServiceViewHolder,
        position: Int
    ) {

        // Get the service at this position.
        val service = services[position]


        // Display the provider's name.
        holder.tvProviderName.text =
            service.providerName ?: "Service Provider"


        // Display the category.
        holder.tvSpecialty.text =
            service.category ?: "Service"


        // Build the provider location.
        //
        // We remove null/blank values so we don't display
        // something like "null, Johannesburg, null".
        val location = listOfNotNull(
            service.providerSuburb,
            service.providerCity
        )
            .filter { it.isNotBlank() }
            .joinToString(", ")


        // Display the location.
        holder.tvLocation.text =
            if (location.isNotEmpty()) {
                location
            } else {
                service.providerProvince ?: "Location not available"
            }


        // Display the service price.
        holder.tvStartingPrice.text =
            "From R${service.price ?: 0.0}"


        // Your API model currently does not contain
        // rating/review information.
        //
        // Therefore we should NOT invent a fake rating.
        holder.tvRating.text = "New"


        // When the whole card is clicked,
        // send the selected service back to CustomerHome.
        holder.itemView.setOnClickListener {

            onItemClick(service)
        }


        // Favourite functionality is not implemented yet.
        holder.btnFavorite.setOnClickListener {

            // We will implement favourites later.
        }
    }


    // RecyclerView needs to know how many items exist.
    override fun getItemCount(): Int {

        return services.size
    }


    // CustomerHome uses this function whenever new
    // services arrive from the API or when search/filtering occurs.
    fun updateData(newServices: List<ServiceBrowseResponse>) {

        // Replace the old list.
        services = newServices

        // Tell RecyclerView to redraw its items.
        notifyDataSetChanged()
    }
}