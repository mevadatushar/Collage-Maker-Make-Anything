package com.example.photoeditor.beautycamera

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.Activity.ImagePickerActivity.Companion.TAG
import com.collagemaker_makeanything.R


class ImagePickerAdapter(
    private val context: Context,
    private val images: List<Uri>,
    private val selectedImages: List<Uri>
) : BaseAdapter() {

    companion object {
        private const val VIEW_TYPE_CAMERA = 0
        private const val VIEW_TYPE_IMAGE = 1
    }

    override fun getCount(): Int {
        return images.size + 1 // +1 for the camera icon
    }

    override fun getItem(position: Int): Any {
        return if (position == 0) Uri.EMPTY else images[position - 1]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_CAMERA else VIEW_TYPE_IMAGE
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return if (getItemViewType(position) == VIEW_TYPE_CAMERA) {
            convertView ?: LayoutInflater.from(context).inflate(R.layout.item_camera_icon, parent, false)
        } else {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false)
            val imageView: ImageView = view.findViewById(R.id.image_view_freestyle)
           val checkmarkView: ImageView = view.findViewById(R.id.image_view_checkmark)

            val imageUri = images[position - 1]
            Log.d(TAG, "Image URI: $imageUri")
            Log.d(TAG, "Selected Images: $selectedImages")

            Glide.with(context).load(imageUri).into(imageView)




            // Show or hide the checkmark based on selection
            checkmarkView.visibility = if (selectedImages.contains(imageUri)) View.VISIBLE else View.GONE

            view
        }
    }
}

