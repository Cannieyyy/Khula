package com.example.khula

// Used to convert the XML layout into a View.
import android.view.LayoutInflater

// Basic Android View.
import android.view.View

// Container for Views.
import android.view.ViewGroup

// Used for displaying the category name.
import android.widget.TextView

// RecyclerView classes.
import androidx.recyclerview.widget.RecyclerView


// This adapter displays our service categories.
//
// categories = list such as:
// Hair & Beauty
// Plumbing
// Cleaning
// Tutoring
//
// onCategoryClick runs when a category is selected.
class CategoryAdapter(

    private var categories: List<String>,

    private val onCategoryClick: (String) -> Unit

) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    // Represents one item_category.xml.
    class CategoryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        // Category name from item_category.xml.
        val tvCategoryName: TextView =
            itemView.findViewById(R.id.tvCategoryName)
    }


    // Create one category item.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        // Load item_category.xml.
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_category,
                parent,
                false
            )

        // Return the ViewHolder.
        return CategoryViewHolder(view)
    }


    // Put category information into the item.
    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {

        // Get the category at this position.
        val category = categories[position]


        // Display its name.
        holder.tvCategoryName.text = category


        // Detect when the user clicks the category.
        holder.itemView.setOnClickListener {

            // Send the selected category back to CustomerHome.
            onCategoryClick(category)
        }
    }


    // Number of categories.
    override fun getItemCount(): Int {

        return categories.size
    }
}