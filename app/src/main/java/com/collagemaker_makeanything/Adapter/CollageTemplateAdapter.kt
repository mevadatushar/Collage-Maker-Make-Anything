package com.collagemaker_makeanything.Adapter



import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.collagemaker_makeanything.Api.Group
import com.collagemaker_makeanything.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class CollageTemplateAdapter(
    private val context: Context,
    private val groups: List<Group> ) : RecyclerView.Adapter<CollageTemplateAdapter.ViewHolder>() {

    //    private val baseUrl = "https://s3.ap-south-1.amazonaws.com/photoeditorbeautycamera.app/photoeditor/template/"
    private val baseUrl = "https://s3.ap-south-1.amazonaws.com/photoeditorbeautycamera.app/photoeditor/template/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collage_template_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]

//        holder.imageView.setOnClickListener {
//            showBottomSheetForImageEditUrl(group)
//        }

        holder.bind(group)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    // ViewHolder class
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView) // Replace with your actual ImageView ID

        fun bind(group: Group) {
            // Bind data to your views here
            Glide.with(itemView.context).load(baseUrl + group.imageUrl).into(imageView)
        }
    }

}