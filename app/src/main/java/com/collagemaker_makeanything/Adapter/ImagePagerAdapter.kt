package com.collagemaker_makeanything.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.R

class ImagePagerAdapter(private val imageUris: List<Uri>) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    private var currentBackgroundColor: Int = 0 // Variable to hold current background color

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val backgroundImageView: ImageView = view.findViewById(R.id.backgroundImageView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item_multifit, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // Load the image into the image view
        Glide.with(holder.imageView.context)
            .load(imageUris[position])
            .into(holder.imageView)

        // Set the background color for the item
        holder.backgroundImageView.setBackgroundColor(currentBackgroundColor) // Apply background color
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    // Method to update the background color
    fun setBackgroundColor(color: Int) {
        currentBackgroundColor = color // Update the current background color
        notifyDataSetChanged() // Refresh all items to apply the new background color
    }
}
