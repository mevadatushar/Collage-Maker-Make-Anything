package com.collagemaker_makeanything.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.R

class GalleryAdapter(private val context: Context, private val imageUris: List<String>) :
RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = imageUris[position]
        when (position) {
            0 -> {
                holder.img1.visibility = View.VISIBLE
                Glide.with(context).load(imageUri).into(holder.img1)
            }
            1 -> {
                holder.img2.visibility = View.VISIBLE
                Glide.with(context).load(imageUri).into(holder.img2)
            }
            2 -> {
                holder.img3.visibility = View.VISIBLE
                Glide.with(context).load(imageUri).into(holder.img3)
            }
            3 -> {
                holder.img4.visibility = View.VISIBLE
                Glide.with(context).load(imageUri).into(holder.img4)
            }
            4 -> {
                holder.img5.visibility = View.VISIBLE
                Glide.with(context).load(imageUri).into(holder.img5)
            }
            5 -> {
                holder.img6.visibility = View.VISIBLE
                Glide.with(context).load(imageUri).into(holder.img6)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img1: ImageView = itemView.findViewById(R.id.img1)
        val img2: ImageView = itemView.findViewById(R.id.img2)
        val img3: ImageView = itemView.findViewById(R.id.img3)
        val img4: ImageView = itemView.findViewById(R.id.img4)
        val img5: ImageView = itemView.findViewById(R.id.img5)
        val img6: ImageView = itemView.findViewById(R.id.img6)
    }
}
